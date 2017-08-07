package com.xiuxiu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import com.xiuxiu.R;

/**
 * Created by hzdykj on 2017/6/26.
 */

public class MediaDecoder {
    private static final String TAG = "MediaDecoder";
    private MediaMetadataRetriever retriever = null;
    private String fileLength;

    private Context mContext;

    public MediaDecoder(String file) {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file);
            fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.i(TAG, "fileLength : "+fileLength);
    }
    /**
     * 获取视频某一帧
     * @param timeMs 毫秒
     */
    public void decodeFrame(long timeMs, ImageView imageView){
//        if(retriever == null) return false;
        for (int i = 0; i < fileLength.length(); i++) {

        }
        Bitmap bitmap = retriever.getFrameAtTime(timeMs * 66, MediaMetadataRetriever.OPTION_CLOSEST);
        addWaterMark(bitmap);
        imageView.setImageBitmap(bitmap);
//        if(bitmap == null) return false;
//        return true;
    }
    /**
     * 取得视频文件播放长度
     * @return
     */
    public String getVedioFileLength(){
        return fileLength;
    }

    private Bitmap addWaterMark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.watermark);
        canvas.drawBitmap(waterMark, 0, 0, null);

        return result;
    }

}
