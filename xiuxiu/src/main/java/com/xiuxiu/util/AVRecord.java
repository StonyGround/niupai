package com.xiuxiu.util;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.activity.VideoEditActivity;
import com.xiuxiu.model.FrameListModel;
import com.xiuxiu.view.CameraView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 音视频录制类
public class AVRecord implements CameraView.SaveFrameCallback {
    // 标志量
    volatile boolean isAVRecording = false;
    volatile boolean audioRecordThreadRunning = false;

    // 源视频帧宽/高
    private int srcFrameWidth = XConstant.FRAME_SRC_WIDTH;
    private int srcFrameHeight = XConstant.FRAME_SRC_HEIGHT;

    // 音频录制及音视频保存
    private Thread audioRecordThread = null;
    private AudioRecordRunnable audioRecordRunnable = null;
    private List<Long> srcAudioFrameList = null;
    private List<Long> srcVideoFrameList = null;
    private List<Long> dstVideoFrameList = null;
    private int yuvFrameSize = 0;
    private int audioBufferSize = 4096;
    private byte[] clip_yuv = null;
    private byte[] audioData = null;
    private Long breakPoint = Long.valueOf(0);


    // 录制模块逻辑控制
    private RecordActivity recordCtrl = null;
    // 音/视频文件路径
    private String audioFilePath = null, audioSeedFilePath = null, videoSeedFilePath = null, tmpOutputFilePath = null;

    public AVRecord(RecordActivity _recordCtrl) {
        recordCtrl = _recordCtrl;
        srcAudioFrameList = new ArrayList<>();
        srcVideoFrameList = new ArrayList<>();
        dstVideoFrameList = new ArrayList<>();

        initRecord();

    }

    //初始化AVRecord
    public void initRecord() {
        // 初始化音频视频文件路径
        audioFilePath = Util.genrateFilePath("xiu_audio", ".mp4", XConstant.VIDEO_FILE_PATH);
        audioSeedFilePath = Util.genrateFilePath("xiu_audio_seed", ".mp4", XConstant.VIDEO_FILE_PATH);
        videoSeedFilePath = Util.genrateFilePath("xiu_video_seed", ".mp4", XConstant.VIDEO_FILE_PATH);
        tmpOutputFilePath = Util.genrateFilePath("xiu_tmp_output", ".mp4", XConstant.VIDEO_FILE_PATH);

        // 默认开启音频录制线程
        audioRecordRunnable = new AudioRecordRunnable();
        if (audioRecordRunnable.IsAudioRecordInitOK()) {
            audioRecordThreadRunning = true;
            audioRecordThread = new Thread(audioRecordRunnable);
            audioRecordThread.start();
        } else {
            recordCtrl.sendRecordMessage(XConstant.RECORD_STATUS_ERROR, "无法打开录音模块。您可能已经禁用了牛拍录音权限，或录音模块被其他程序占用。");
        }
    }

    // 开始录制/编码
    public void startRecord() {
        // 重新分配内存
        if (recordCtrl.getCurRecordMode() == XConstant.RECORD_MODE_NOR) {
            srcFrameWidth = XConstant.FRAME_SRC_WIDTH;
            srcFrameHeight = XConstant.FRAME_SRC_HEIGHT;
            yuvFrameSize = XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2;
        } else if (recordCtrl.getCurRecordMode() == XConstant.RECORD_MODE_PIP) {
            srcFrameWidth = XConstant.FRAME_SRC_WIDTH >> 1;
            srcFrameHeight = XConstant.FRAME_SRC_HEIGHT >> 1;
            yuvFrameSize = srcFrameWidth * srcFrameHeight * 3 / 2;
        }
        clip_yuv = new byte[yuvFrameSize];

        // 录制开始标识
        isAVRecording = true;

        Log.d("dd_cc_dd", "....开始录制......");
    }

    // 停止录制/编码
    public void stopRecord() {
        synchronized (this) {
            isAVRecording = false;
            srcAudioFrameList.add(breakPoint);
            srcVideoFrameList.add(breakPoint);

            Log.d("dd_cc_dd", "....停止录制......");
        }
    }

    // 关闭录制
    public void closeRecord() {
        // 关闭线程
        audioRecordThreadRunning = false;
    }

    // 录制状态
    public boolean isAVRecording() {
        return isAVRecording;
    }

    // 完成录制
    public void finishRecord() {
        if (srcVideoFrameList.size() == 0) {
            Util.showTextToast(recordCtrl, "你还没有录制视频~");
            return;
        }
        new AsyncFinishRecord().execute();
    }

    // 清空音视频数据
    public void clearAVData() {
        for (Long handle : srcAudioFrameList) {
            if (handle != breakPoint) {
                AVProcessing.deleteDataFrame(handle);
            }
        }
        srcAudioFrameList.clear();

//        for (Long handle : srcVideoFrameList) {
//            if (handle != breakPoint) {
//                AVProcessing.deleteDataFrame(handle);
//            }
//        }
        srcVideoFrameList.clear();
    }

    //回删功能
    public void deleteDatAtBreakPoint() {
        //删除最后一个断点
        if (srcVideoFrameList.size() > 0 && srcAudioFrameList.size() > 0) {
            int video = srcVideoFrameList.size() - 1;
            int audio = srcAudioFrameList.size() - 1;
            srcVideoFrameList.remove(video);
            srcAudioFrameList.remove(audio);

            //倒序删除至上一个断点
            for (int i = srcVideoFrameList.size() - 1; srcVideoFrameList.get(i) != breakPoint; i--) {
                srcVideoFrameList.remove(srcVideoFrameList.get(i));
                if (i == 0) {
                    break;
                }
            }

            for (int i = srcAudioFrameList.size() - 1; srcAudioFrameList.get(i) != breakPoint; i--) {
                srcAudioFrameList.remove(srcAudioFrameList.get(i));
                if (i == 0) {
                    break;
                }
            }
        }
    }

    // 保存待编码的视频帧
    @Override
    public void onSaveFrames(byte[] data, int length) {
        synchronized (this) {
            if (isAVRecording) {
                // 旋转并裁剪数据>>640*480旋转并裁剪为480*480或320*240数据旋转为240*320
                AVProcessing.rotateClipYUV(data, clip_yuv, srcFrameWidth, srcFrameHeight, false);
//                Log.d("dd_cc_dd", "data..." + srcVideoFrameList.size() + Arrays.toString(data) + "..size..." + srcVideoFrameList.size());
//                Log.d("dd_cc_dd", "clip_yuv..." + srcVideoFrameList.size() + Arrays.toString(clip_yuv) + "..size..." + srcVideoFrameList
//                        .size());
                Long handle = AVProcessing.saveDataFrame(clip_yuv, yuvFrameSize);

//                Log.d("dd_cc_dd", "handle..." + handle);
                if (handle != null) {
                    srcVideoFrameList.add(handle);
//                    Log.d("dd_cc_dd", "handle..." + Util.byteArrayToLeInt(data) + "..size..." + srcVideoFrameList.size());
//                    Log.d("dd_cc_dd", "handle..." + handle + "..size..." + srcVideoFrameList.size());
//                    Log.d("dd_cc_dd", "handle..." + Arrays.toString(Util.toBytes(handle)) + "..size..." + srcVideoFrameList.size());
                }
            }
        }
    }

    // 音频录制线程
    class AudioRecordRunnable implements Runnable {
        private int sampleRate = 44100;// 采样率
        private int channelConfig = AudioFormat.CHANNEL_IN_MONO;  // 单声道
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // pcm数据格式为16位每个样本
        private AudioRecord audioRecord = null;
        private final static int BASE_BUFFER_SIZE = 4096;

        public AudioRecordRunnable() {
            audioBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

            int k = 0;
            do {
                k++;
            }
            while (audioBufferSize > BASE_BUFFER_SIZE * k);

            audioBufferSize = BASE_BUFFER_SIZE * k;
            audioData = new byte[audioBufferSize];
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, audioBufferSize);
        }

        // 检查录音模块初始化状态
        public boolean IsAudioRecordInitOK() {
            if (audioRecord != null) {
                return audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
            } else {
                return false;
            }
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            if (audioRecord != null) {
                // 等待设备初始化完成
                while (audioRecordThreadRunning && (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED)) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }

                // 开始录制
                audioRecord.startRecording();
                int ret = 0;
                while (audioRecordThreadRunning) {
                    // 读取数据并保存
                    ret = audioRecord.read(audioData, 0, audioBufferSize);
                    if (ret > 0 && isAVRecording) {
                        Long handle = AVProcessing.saveDataFrame(audioData, audioBufferSize);
                        if (handle != null) {
                            srcAudioFrameList.add(handle);
//                            Log.d("dd_cc_dd", "srcAudioFrameList...handle..." + handle + "..size..." + srcAudioFrameList.size());
                        }
                    }
                }

                // 停止录制,释放设备
                audioRecord.stop();
                audioRecord.release();
                // Log.d("dd_cc_dd", "....音频设备释放完毕......");
            }// if
        }// void run()
    }// class AudioRecordRunnable

    // 完成录制
    private class AsyncFinishRecord extends AsyncTask<Void, Integer, Void> {
        private TextView textInfo = null;
        private ProgressBar progressBar = null;
        private Dialog processingInfoDialog = null;

        // 预处理
        @Override
        protected void onPreExecute() {
            // 创建数据处理提示窗体
            processingInfoDialog = new Dialog(recordCtrl, R.style.Dialog_Xiu);
            processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
            Window dialogWindow = processingInfoDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int) (recordCtrl.getResources().getDisplayMetrics().density * 240);
            layoutParams.height = (int) (recordCtrl.getResources().getDisplayMetrics().density * 80);
            layoutParams.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(layoutParams);
            processingInfoDialog.setCancelable(false);
            processingInfoDialog.setCanceledOnTouchOutside(false);

            textInfo = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTextView);
            progressBar = (ProgressBar) processingInfoDialog.findViewById(R.id.processingProgressbar);
            processingInfoDialog.show();
        }

        // 数据处理
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(1);

            // 停止音频录制线程/摄像头/音乐播放
            audioRecordThreadRunning = false;
            recordCtrl.stopCamera();
            recordCtrl.changePlayerStatus(XConstant.PLAYER_STATUS_STOP);

            long handle = 0;
            int percent = 0, totalFrameCount = 0;
            byte[] data = new byte[audioBufferSize];
            // 音频编码并存储
            AVProcessing.initAudioEncode(audioFilePath, 44100, 1);
            totalFrameCount = srcAudioFrameList.size();
//            Log.d("doInBackground", "srcAudioFrameList...handle..." + handle + "..size..." + srcAudioFrameList.size());
            for (int i = 0; i < totalFrameCount; i++) {
                handle = srcAudioFrameList.get(i);
                if (handle != 0) {
                    AVProcessing.copyFrameData(handle, data, audioBufferSize);
                    AVProcessing.encodeAudioFrame(data, audioBufferSize);
                    percent = (int) (85.0f * i / totalFrameCount) + 1;
                    publishProgress(percent);
                }
            }
            AVProcessing.uninitAudioEncode();

            int nRet = -2;
            // 音视频混合
            if (!recordCtrl.getCurAccSongPath().equals("")) {
                // 已选伴奏>>先将伴奏与人声混音并删除人声音频,再音视频混合,最后更新人声音频资源路径
                String mixerFilePath = Util.genrateFilePath("mixer_acc_music", ".mp4", XConstant.VIDEO_FILE_PATH);
                String accSongFilePath = recordCtrl.getCurAccSongPath();
//                Log.d("AVRecord", accSongFilePath + "------");
                nRet = AVProcessing.mixAudioWithSong(audioFilePath, accSongFilePath, mixerFilePath);
                AVProcessing.deleteFile(audioFilePath);
                publishProgress(90);
//                nRet = AVProcessing.muxAVFile(mixerFilePath, videoSeedFilePath, tmpOutputFilePath, XConstant.RECORD_ACC_MAX_TIME * 0.001);
                audioFilePath = mixerFilePath;
                Log.d("dd_cc_dd", "....音视频混合完成 ......" + nRet);
            }
//            else {
//                // 未选伴奏
//                if (recordCtrl.getCurRecordMode() == XConstant.RECORD_MODE_NOR) {
//                    // 直接混合
//                    Log.d("AVRecord","NO MUSIC------");
////                    nRet = AVProcessing.muxAVFile(audioFilePath, videoSeedFilePath, tmpOutputFilePath, XConstant.RECORD_NOR_MAX_TIME *
////                            0.001);
//                    Log.d("dd_cc_dd", "....音视频混合完成 ......" + nRet);
//                } else if (recordCtrl.getCurRecordMode() == XConstant.RECORD_MODE_PIP) {
//                    // 分离音视频
//                    String extractVideoFilePath = Util.genrateFilePath("template/temp") + "t_extract_video.mp4";
//                    String extractAudioFilePath = Util.genrateFilePath("template/temp") + "t_extract_audio.mp4";
//                    AVProcessing.extractAVFile(recordCtrl.getCurVideoTemplatePath(), extractVideoFilePath, extractAudioFilePath,
//                            (recordCtrl.getCurRecordTime() + 100) * 0.001);
//                    publishProgress(88);
//                    // 叠加人声后重新混合
//                    String mixerAudioFilePath = Util.genrateFilePath("template/temp") + "t_mixer_audio.mp4";
//                    AVProcessing.mixAudioWithSong2(audioFilePath, extractAudioFilePath, mixerAudioFilePath, 0.3);
//                    publishProgress(95);
//                    nRet = AVProcessing.muxAVFile(mixerAudioFilePath, extractVideoFilePath, tmpOutputFilePath, XConstant
//                            .RECORD_PIP_MAX_TIME * 0.001);
//                    audioFilePath = mixerAudioFilePath;
//                }
//            }

            publishProgress(100);
            return null;
        }

        // 更新UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            textInfo.setText(values[0] + "%");
            progressBar.setProgress(values[0]);
        }

        // 处理结束
        @Override
        protected void onPostExecute(Void result) {
            processingInfoDialog.dismiss();

			/*
             *  追加断点主要是方便后面实现[回删]功能
			 *  摄像头采集的数据帧无法满足15帧/秒,而视频模板解码的视频帧为15帧/秒
			 *  这样对于帧数不稳定的混合数据需要重新做数据映射,以满足编码所需的15帧/秒
			*/

            // 计算目标帧数据长度
            int dstFrameListSize = 0;
            dstFrameListSize = (int) (recordCtrl.getCurRecordTime() / 66.6f);// 66.6f == 1秒15帧

            // 分离视频帧
            int srcFrameListSize = 0;
            List<Long> segVideoFrames = new ArrayList<Long>();
            List<List<Long>> segVideoFramesArray = new ArrayList<List<Long>>();
            for (Long value : srcVideoFrameList) {
                if (value != breakPoint) {
                    // 保存视频帧
                    segVideoFrames.add(value);
                    srcFrameListSize++;
                } else {
                    // 保存帧片段
                    if (segVideoFrames.size() != 0) {
                        segVideoFramesArray.add(segVideoFrames);
                    }
                    segVideoFrames = new ArrayList<Long>();
                }
            }

            // 视频段重新映射
            long handle = 0;
            int index = 0, segSrcFrameListSize = 0, segDstFrameListSize = 0;
            float mappedRatio = (float) dstFrameListSize / srcFrameListSize;
            List<List<Long>> segMappedVideoFramesArray = new ArrayList<List<Long>>();
            for (List<Long> list : segVideoFramesArray) {
                List<Long> segMappedVideoFrames = new ArrayList<Long>();
                segSrcFrameListSize = list.size();
                segDstFrameListSize = (int) (mappedRatio * segSrcFrameListSize);
                for (int i = 0; i < segDstFrameListSize; i++) {
                    index = (int) (((float) i / segDstFrameListSize) * segSrcFrameListSize);
                    index = index < segSrcFrameListSize ? index : (segSrcFrameListSize - 1);
                    handle = list.get(index);
                    segMappedVideoFrames.add(handle);
                }

                segMappedVideoFrames.add(breakPoint);
                segMappedVideoFramesArray.add(segMappedVideoFrames);
            }// for

            // 填充数据
            dstVideoFrameList.clear();
            for (List<Long> list : segMappedVideoFramesArray) {
                dstVideoFrameList.addAll(list);
            }

            //序列化
            FrameListModel frameListModel = new FrameListModel();
            frameListModel.setFrameList(dstVideoFrameList);

            // 跳转到视频编辑页面
            Intent intent = new Intent(recordCtrl, VideoEditActivity.class);
            intent.putExtra("recordMode", recordCtrl.getCurRecordMode());
            intent.putExtra("videoFrameRate", recordCtrl.getVideoFrameRate());
            intent.putExtra("songLyricsPath", recordCtrl.getCurSongLyrics());
            intent.putExtra("audioFilePath", audioFilePath);
            intent.putExtra("audioSeedFilePath", audioSeedFilePath);
            intent.putExtra("videoSeedFilePath", videoSeedFilePath);
            intent.putExtra("tmpOutputFilePath", tmpOutputFilePath);
            intent.putExtra("videoFrameList", frameListModel);
            recordCtrl.startActivityForResult(intent, 666);
        }
    }// class AsyncFinishRecord

}
