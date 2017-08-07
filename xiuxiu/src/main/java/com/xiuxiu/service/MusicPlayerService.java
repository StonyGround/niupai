package com.xiuxiu.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import com.xiuxiu.util.XConstant;


// 音乐播放服务
public class MusicPlayerService extends Service implements OnCompletionListener {
    private MediaPlayer mediaPlayer;// 媒体播放器对象
    private int curPlayerStatus = XConstant.PLAYER_STATUS_STOP;

    private int timeCycle = 20;// 定时器周期
    private int diffPos = 0;
    private int currentPos = 0;
    private int prevPos = XConstant.INVALID_VALUE;

    private int duration = 0;// 音乐时长
    private int curPlayingTime = 0;// 播放时间
    private int prePlayingTime = 0;
    private long preThreadTime = 0;

    private Handler handler = null;
    private HandlerThread handlerThread = null;

    private AudioManager audioMgr = null; // Audio管理器，用了控制音量

    // 广播音乐播放进度 
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (handler == null || mediaPlayer == null) {
                return;
            }

            Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);

            // 计算两次线程调用时间差
            long curThreadTime = System.currentTimeMillis();
            long diffThreadTime = curThreadTime - preThreadTime;
            preThreadTime = curThreadTime;

            // 计算播放时间
            currentPos = mediaPlayer.getCurrentPosition();
            if (prevPos != currentPos) {
                curPlayingTime = currentPos;
                diffPos = currentPos - prevPos - 5;
                prevPos = currentPos;
            } else {
                if (diffThreadTime > 2 * timeCycle) {
                    diffThreadTime = 2 * timeCycle;
                }

                curPlayingTime += diffThreadTime;
                if (curPlayingTime > currentPos + diffPos) {
                    curPlayingTime = currentPos + diffPos;
                    // Log.d("dd_cc_dd", "..ret............................");
                }
            }

            // 计算两次播放时间变化>>过小的变化不发送
            int diffPlayingTime = curPlayingTime - prePlayingTime;
            if (diffPlayingTime > 5) {
                Intent intent = new Intent();
                intent.setAction(XConstant.MUSIC_CURRENT_TIME);
                intent.putExtra("music_currentTime", curPlayingTime);
                sendBroadcast(intent);
            }
            prePlayingTime = curPlayingTime;

            handler.postDelayed(this, timeCycle);
        }// run()
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnCompletionListener(this);
        // 设置多媒体流类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioMgr = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        handlerThread = new HandlerThread("music_thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        /*// 获取最大音乐音量
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 初始化音量大概为最大音量的1/2
        int curVolume = maxVolume / 6;
        // 每次调整的音量大概为最大音量的1/6
        int stepVolume = maxVolume / 12;

        for (int i = 0; i < 5; i++) {
            mediaPlayer.start();
            curVolume += stepVolume;
            if (curVolume >= maxVolume) {
                curVolume = maxVolume;
            }
            audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
                    AudioManager.FLAG_PLAY_SOUND);
            SystemClock.sleep(2000);
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        curPlayerStatus = intent.getIntExtra("MUSIC_PLAYER_STATUS", 0);
        switch (curPlayerStatus) {
            case XConstant.PLAYER_STATUS_START:
                String fileName = intent.getStringExtra("MUSIC_FILE_NAME");
                int playingType = intent.getIntExtra("MUSIC_PLAYING_TYPE", 0);
                playerStart(fileName, playingType);
                break;
            case XConstant.PLAYER_STATUS_PAUSE:
                playerPause();
                break;
            case XConstant.PLAYER_STATUS_RESUME:
                playerResume();
                break;
            case XConstant.PLAYER_STATUS_STOP:
                playerStop();
                break;
            case XConstant.PLAYER_STATUS_RESTART:
                playerRestart();
                break;
            case XConstant.PLAYER_STATUS_SEEK:
                int seek = intent.getIntExtra("MUSIC_PLAYING_SEEK", 0);
                playerSeek(seek);
                break;
            case XConstant.PLAYER_STATUS_PAUSE_RESUME:
                if(mediaPlayer.isPlaying()){
                    playerRestart();
//                    playerPause();
                }else {
                    playerResume();
                }
                break;
            case XConstant.PLAYER_STATUS_ADD:
                audioAdd();
                break;
            case XConstant.PLAYER_STATUS_SUBTRACT:
                audioSubtract();
                break;
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        release();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // 音乐播放完成
        handler.removeCallbacks(runnable);
    }

    // 播放音乐
    private void playerStart(String fileName, int playingType) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileName);
//            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
//            mediaPlayer.start();
//            adjustVolume();

            prevPos = XConstant.INVALID_VALUE;
            prePlayingTime = 0;
            preThreadTime = System.currentTimeMillis();
            handler.removeCallbacks(runnable);
            if (playingType == XConstant.PLAYING_TYPE_SINGING) {
                // 发送歌曲时长
                Intent intent = new Intent();
                intent.setAction(XConstant.MUSIC_DURATION);
                duration = mediaPlayer.getDuration();
                intent.putExtra("music_duration", duration);
                sendBroadcast(intent);

                handler.postDelayed(runnable, timeCycle);

            }

            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调整音量
     */
    private void adjustVolume() {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_PLAY_SOUND);
    }

    //调小音量
    private void audioSubtract(){
        audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);//调小音量
//    	mediaPlayer.setVolume(0, 0); //直接设置静音
    }

    //调大音量
    private void audioAdd(){
        audioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);//调大音量
//    	 mediaPlayer.setVolume(1, 1);//设置左右声道都有声音
    }

    // 暂停音乐
    private void playerPause() {
        Log.d("MusicPlayerService", "playerPause");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // 播放恢复
    private void playerResume() {
        Log.d("MusicPlayerService", "playerResume");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
//

    }

    // 停止音乐
    private void playerStop() {
        Log.d("MusicPlayerService", "playerStop");
        if (mediaPlayer != null) {
            handler.removeCallbacks(runnable);
            Intent intent = new Intent();
            intent.setAction(XConstant.MUSIC_COMPLETION);
            sendBroadcast(intent);

            mediaPlayer.stop();
        }
    }

    //重新播放
    private void playerRestart() {
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    private void playerSeek(int seek) {
        mediaPlayer.seekTo(seek);
        Intent intent = new Intent();
        intent.setAction(XConstant.MUSIC_CURRENT_TIME);
        intent.putExtra("music_currentTime", seek);
        sendBroadcast(intent);
    }

    // 释放资源
    private void release() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
