package com.xiuxiu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import com.xiuxiu.view.ThemeView;

import java.io.ByteArrayOutputStream;

/**
 * Created by hzdykj on 2017/7/11.
 */

public class ThemeBitmapUtil {
    public static Bitmap yuv2bitmap(byte[] yuv, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(yuv, ImageFormat.NV21, width, height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 50, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    public static long bitmap2yuv(Bitmap bitmap) {
        int bitMapWidth = XConstant.FRAME_DST_WIDTH;
        int bitMapHeight = XConstant.FRAME_DST_HEIGHT;

        int[] argb = new int[bitMapWidth * bitMapHeight];
        bitmap.getPixels(argb, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
        byte[] yuv = colorconvertRGB_IYUV_I420(argb, bitMapWidth, bitMapHeight);

        int yuvFrameSize = bitMapWidth * bitMapHeight * 3 / 2;
        return AVProcessing.saveDataFrame(yuv, yuvFrameSize);
    }

    public static Bitmap combineBitmap(Context context, Bitmap background, ThemeView themeView) {

        float size = (float) background.getWidth() / (float) Util.getScreenWidth(context);
        if (background == null) {
            return null;
        }

        Bitmap foreground=themeView.getBitmap();
        Matrix matrix=themeView.getMatrix();

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();



        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        Log.d("matrix", arrayOfFloat[Matrix.MSKEW_X] + "combine---" + arrayOfFloat[Matrix.MSKEW_Y]);
        arrayOfFloat[Matrix.MTRANS_X] = arrayOfFloat[Matrix.MTRANS_X] * size;
        arrayOfFloat[Matrix.MTRANS_Y] = arrayOfFloat[Matrix.MTRANS_Y] * size;
        arrayOfFloat[Matrix.MSCALE_X] = arrayOfFloat[Matrix.MSCALE_X] * size;
        arrayOfFloat[Matrix.MSCALE_Y] = arrayOfFloat[Matrix.MSCALE_Y] * size;
        arrayOfFloat[Matrix.MSKEW_X] = arrayOfFloat[Matrix.MSKEW_X] * size;
        arrayOfFloat[Matrix.MSKEW_Y] = arrayOfFloat[Matrix.MSKEW_Y] * size;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setValues(arrayOfFloat);

        Matrix scale = new Matrix();
        scale.postScale(size, size);
//        scale.preRotate(30);
        foreground = Bitmap.createBitmap(foreground, 0, 0, foreground.getWidth(), foreground.getHeight(), matrix, true);

        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, scaleMatrix, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }


    public static int[] yuv2rgb(byte[] pYUV, int width, int height) {
        int[] pRGB = new int[width * height];
        int i, j, yp;
        int hfWidth = width >> 1;
        int size = width * height;
        int qtrSize = size >> 2;
        for (i = 0, yp = 0; i < height; i++) {
            int uvp = size + (i >> 1) * hfWidth, u = 0, v = 0;
            for (j = 0; j < width; j++, yp++) {
                int y = (0xff & pYUV[yp]) - 16;
                if ((j & 1) == 0) {
                    u = (0xff & pYUV[uvp + (j >> 1)]) - 128;
                    v = (0xff & pYUV[uvp + qtrSize + (j >> 1)]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                pRGB[i * width + j] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return pRGB;
    }


    public static byte[] colorconvertRGB_IYUV_I420(int[] aRGB, int width, int height) {
        final int frameSize = width * height;
        final int chromasize = frameSize / 4;

        int yIndex = 0;
        int uIndex = frameSize;
        int vIndex = frameSize + chromasize;
        byte[] yuv = new byte[width * height * 3 / 2];

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                //a = (aRGB[index] & 0xff000000) >> 24; //not using it right now
                R = (aRGB[index] & 0xff0000) >> 16;
                G = (aRGB[index] & 0xff00) >> 8;
                B = (aRGB[index] & 0xff) >> 0;

                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));

                if (j % 2 == 0 && index % 2 == 0) {
                    yuv[uIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    yuv[vIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                }

                index++;
            }
        }
        return yuv;
    }
}
