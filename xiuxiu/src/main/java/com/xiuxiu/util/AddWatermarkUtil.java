package com.xiuxiu.util;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


/**
 * Created by sanqian on 28/06/2017.
 */

public class AddWatermarkUtil {

    private static OnCompoundListener mListener;
    private static AddWatermarkUtil addWatermarkUtil;

    public AddWatermarkUtil setListener(OnCompoundListener mListener){
        this.mListener = mListener;
        return this;
    }

    //获取下载工具的实例
    public synchronized static AddWatermarkUtil getsInstance(){
        if (addWatermarkUtil == null){
            try {
                addWatermarkUtil = new AddWatermarkUtil();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  addWatermarkUtil;
    }

    public static void
    addWatermark(Context context, String inputVideoFilePath, String outputVideoFilePath, String watermarkImageFilePath) {
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace();
        }

        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
//            String watermarkCmd = "-i $INPUTVIDEO -i $WATERMARK -filter_complex \"[0:v][1:v] overlay=25:25:enable='between(t,0,20)'\" -pix_fmt yuv420p -c:a copy $OUTPUTVIDEO";
            String[] cmd = {"-i", inputVideoFilePath,
                    "-i", watermarkImageFilePath,
//                    "-filter_complex", "[0:v][1:v] overlay=25:25:enable='between(t,0,20)'",
                    "-filter_complex", "[0:v][1:v] overlay=10:main_h-overlay_h-10",
                    "-pix_fmt", "yuv420p",
                    "-c:a",
                    "copy", outputVideoFilePath};

            System.out.println("-------cmd------" + cmd);

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    System.out.println("onStart");
                }

                @Override
                public void onProgress(String progress) {
                    System.out.println("onProgress :" + progress);
                    if ( mListener != null) mListener.onStart();
                }

                @Override
                public void onFailure(String message) {
                    System.out.println(message);
                    if ( mListener != null) mListener.onFailed();
                }

                @Override
                public void onSuccess(String message) {
                    System.out.println(message);
                    if ( mListener != null) mListener.onSuccess();
                }

                @Override
                public void onFinish() {
                    System.out.println("AddWatermark success");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }
    }

    public interface OnCompoundListener{
        public void onSuccess();
        public void onFailed();
        public void onStart();
    }
}
