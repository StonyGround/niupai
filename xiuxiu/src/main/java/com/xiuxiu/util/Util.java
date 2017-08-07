package com.xiuxiu.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore.Video;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class Util {


    //权限
    public static boolean checkPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                for (String permission : permissions) {
                    int rest = (Integer) method.invoke(context, permission);
                    if (rest != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            for (String permission : permissions) {
                PackageManager pm = context.getPackageManager();
                if (pm.checkPermission(permission, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //将内容转换成可发送给服务器的字符串数据
    public static String convertContentToSend(EditText inputEdit) {
        Spanned s = inputEdit.getEditableText();
        ImageSpan[] imageSpans = s.getSpans(0, s.length(), ImageSpan.class);
        if (imageSpans == null) {
            return inputEdit.getText().toString().trim();
        }
        Editable et = inputEdit.getText();
        int length = Expressions.expressionNames.length;

        int count = 0;
        for (ImageSpan imageSpan : imageSpans) {
            Log.v("tang", "index:" + count++);
            int start = s.getSpanStart(imageSpan);
            int end = s.getSpanEnd(imageSpan);
            for (int index = 0; index < length; index++) {
                String faceName = Expressions.expressionNames[index];
                if (Expressions.getfaces().containsKey(faceName)) {
                    if (Expressions.getfaces().get(faceName) == Integer.parseInt(imageSpan.getSource())) {
                        et.replace(start, end, "[" + faceName + "]");
                        Log.v("tang", "s:" + start + ", e:" + end + ", facename:" + faceName);
                        break;
                    }
                }
            }
        }

        return et.toString();
    }

    private static int uPlayMode;
    //	private static final Context context = MyApplication.getAppContext();
    //初始化播放模式

    public static int getNPlayMode(Context context) {
        return uPlayMode = (getVideoPlayMode(context) == -1) ? XConstant.AUTOPLAY_FORBID : getVideoPlayMode(context);
    }


    //文件拷贝
    public static boolean fileCopy(String oldFilePath, String newFilePath) throws IOException {
        //如果原文件不存在
        if (!fileExists(oldFilePath)) {
            return false;
        }
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[1024];
        //输出流
        FileOutputStream outputStream = new FileOutputStream(new File(newFilePath));
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // 生成文件完整路径>>路径+文件名
    public static String genrateFilePath(String uniqueId, String fileExtension, String dirPath) {
        String fileName = uniqueId + fileExtension;
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        return dirPath + fileName;
    }

    // 生成文件路径>>不包括文件名
    public static String genrateFilePath(String subFolder) {
        String dirPath = XConstant.DCIM_FILE_PATH + subFolder + "/";

        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        return dirPath;
    }

    // 创建文件目录
    public static void generateFileDir(String strFileDir) {
        File file = new File(strFileDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    // 获取当前程序版本名称
    public static String getAppVersionName(Context context) {
        VersionManager.setContext(context);
        return VersionManager.getAppVersionName();
    }

    // 拷贝assets子目录至本地
    public static void copyAssetsSubFolderToLocal(String dirPath, String assetFolderName, AssetManager assetManager) {
        byte[] buffer = new byte[1024];
        // 创建文件夹
        File file = new File(dirPath);
        file.mkdirs();

        // 拷贝资源
        InputStream istream = null;
        OutputStream ostream = null;
        String[] fileNames = null;
        try {
            fileNames = assetManager.list(assetFolderName);
            for (String fileName : fileNames) {
                file = new File(dirPath + fileName);
                ostream = new FileOutputStream(file);
                istream = assetManager.open(assetFolderName + "/" + fileName);

                int length = istream.read(buffer);
                while (length > 0) {
                    ostream.write(buffer, 0, length);
                    length = istream.read(buffer);
                }

                istream.close();
                ostream.flush();
                ostream.close();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    // 拷贝assets资源文件至本地
    public static void copyAssetsFileToLocal(String dirPath, String fileName, String assetFolderName, AssetManager assetManager) {
        byte[] buffer = new byte[1024];
        // 创建文件夹
        File file = new File(dirPath);
        file.mkdirs();

        // 拷贝资源
        InputStream istream = null;
        OutputStream ostream = null;
        try {
            file = new File(dirPath + fileName);
            ostream = new FileOutputStream(file);
            istream = assetManager.open(assetFolderName + "/" + fileName);

            int length = istream.read(buffer);
            while (length > 0) {
                ostream.write(buffer, 0, length);
                length = istream.read(buffer);
            }

            istream.close();
            ostream.flush();
            ostream.close();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    // 获取本地资源文件
    public static File[] getLocalFiles(String mainPath, String subFolderName) {
        String dirPath = mainPath + subFolderName;
        File file = new File(dirPath);

        return file.listFiles();
    }

    // 获取本地资源(音乐)文件名
    public static String getLocalFilePath(String mainPath, String subFolderName, int index) {
        String dirPath = mainPath + subFolderName;
        File file = new File(dirPath);
        File[] files = file.listFiles();

        return (index < files.length) ? files[index].getPath() : "";
    }

    // 获取本地资源(音乐)文件数量
    public static int getLocalFileCount(String mainPath, String subFolderName) {
        String dirPath = mainPath + subFolderName;
        File file = new File(dirPath);
        File[] files = file.listFiles();

        return files.length;
    }

    // 注册视频文件
    public static void registerVideo(String videoFilePath, Context context) {
        ContentValues values = new ContentValues(7);
        values.put(Video.Media.TITLE, "123456789");
        values.put(Video.Media.DISPLAY_NAME, "123456789");
        values.put(Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(Video.Media.MIME_TYPE, "video/3gpp");
        values.put(Video.Media.DATA, videoFilePath);

        Uri videoTable = Uri.parse("content://media/external/video/media");
        values.put(Video.Media.SIZE, new File(videoFilePath).length());

        try {
            context.getContentResolver().insert(videoTable, values);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    // 获取当前屏幕宽度
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    // 获取当前屏幕高度
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    // dp转换为px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // px转换为dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将px值转换为sp值
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    // 将sp值转换为px值
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /* 计算字符串包含的字符总长度,并判断其合法性
      (1个汉字 = 2个英文字符,该函数提供修改昵称/用户注册时使用,加了判断字符集操作)*/
    public static int calcCharSequenceLength(CharSequence chSequence) {
        if (TextUtils.isEmpty(chSequence))
            return -1;

        int nStrLen = 0; //返回的字符串长度大小 
        for (int i = 0; i < chSequence.length(); i++) {
            char c = chSequence.charAt(i);
            int tmp = (int) c;
            if (tmp > 0 && tmp < 127) {
                //根据ASCII码 确定0~9 / a~z / A~Z / 英文下划线(_) / 英文减号(-)为可用字符,其余的直接返回-1;
                if ((tmp >= 48 && tmp <= 57) || (tmp >= 65 && tmp <= 90) ||
                        (tmp >= 97 && tmp <= 122) || tmp == 95 || tmp == 45) {
                    nStrLen++;
                } else {
                    return -1;//含有非法字符,则直接返回-1
                }
            } else {
                //检测非ASCII码表中的字符集  (当前只有汉字为合法字符,其余表情,全角标点均为非法字符集)
                Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
                if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //中日韩统一表意文字
                        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)//中日韩统一表意文字扩充A
                    nStrLen += 2;//1个汉字 = 2个英文字符
                else
                    return -1;//含有非法字符,则直接返回-1
            }
        }

        return nStrLen;
    }

    //获取视频播放模式
    public static int getVideoPlayMode(Context context) {
        int nPlayMode = -1;
        SharedPreferences sp = context.getSharedPreferences(XConstant.CONFIG_FILENAME, Context.MODE_PRIVATE);
        nPlayMode = sp.getInt(XConstant.PLAY_MODE, -1);
        return nPlayMode;
    }

    //写入视频播放模式
    public static void setVideoPlayMode(Context context, int nPlayMode) {
        SharedPreferences sp = context.getSharedPreferences(XConstant.CONFIG_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(XConstant.PLAY_MODE, nPlayMode);
        editor.commit();
        //更新播放模式
        Util.uPlayMode = nPlayMode;
    }

    //是否为非自动播放（即手动播放）
    //获取手机品牌
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    //获取手机型号
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    //获取设备的唯一标识码imei
    public static String getImei(Context context) {
        //Return null if device ID is not available.
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    //获取系统版本
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    //获取sdk版本
    public static int getSdkVersion() {
        return Integer.valueOf(android.os.Build.VERSION.SDK_INT);
    }

    //获取本机号码
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    //解析字符串，获取视频网络文件名称
    public static String GetVideoName(String strVideoUrl) {
        String VideoUrl = strVideoUrl;
        int nlen = VideoUrl.length() - VideoUrl.lastIndexOf("/") - 1;
        char ch[] = new char[nlen];
        VideoUrl.getChars(VideoUrl.lastIndexOf("/") + 1, VideoUrl.length(), ch, 0);
        return String.valueOf(ch);
    }

    //获取视频的临时文件名（XXX.vcfg）
    public static String getVideoTempName(String strVideoUrl) {
        int nlen = strVideoUrl.length() - strVideoUrl.lastIndexOf("/") - 1;
        char ch[] = new char[nlen];
        strVideoUrl.getChars(strVideoUrl.lastIndexOf("/") + 1, strVideoUrl.length(), ch, 0);
        String strVideoName = String.valueOf(ch);
        nlen = strVideoName.lastIndexOf(".");
        ch = new char[nlen];
        strVideoName.getChars(0, nlen, ch, 0);

        return String.valueOf(ch) + ".vcfg";
    }

    //获取视频的后缀名
    public static String getVideoFileSuffix(String strVideoUrl) {
        int nLen = strVideoUrl.length() - strVideoUrl.lastIndexOf(".");
        char[] ch = new char[nLen];
        strVideoUrl.getChars(strVideoUrl.lastIndexOf("."), strVideoUrl.length(), ch, 0);
        return String.valueOf(ch);
    }

    //将视频的临时文件还原为原始后缀
    public static String getVideoTargetFile(String str, String strFileSuffix) {
        int nStart = str.lastIndexOf("/");
        int nEnd = str.lastIndexOf(".");
        char ch[] = new char[nEnd - nStart];
        str.getChars(nStart, nEnd, ch, 0);
        String targetFile = genrateFilePath(String.valueOf(ch), strFileSuffix, XConstant.VIDEO_CACHE_FILE_PATH);
        return targetFile;
    }

    //加载本地图片
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 根据两点经纬度计算距离
    public static String getStrDistance(double dLatA, double dLonA, double dLatB, double dLonB) {
        double radLat1 = (dLatA * Math.PI / 180.0);
        double radLat2 = (dLatB * Math.PI / 180.0);
        double dLatPoor = radLat1 - radLat2;
        double dLngPoor = (dLonA - dLonB) * Math.PI / 180.0;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dLatPoor / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow
                (Math.sin(dLngPoor / 2), 2)));
        distance = distance * XConstant.EARTH_RADIUS;

        DecimalFormat df = null;
        if (distance < 0.01) {
            return "0.01km内";
        } else if (distance < 10.0) {
            df = new DecimalFormat("0.00");
            return df.format(distance) + "km";
        } else if (distance < 99.9) {
            df = new DecimalFormat("0.0");
            return df.format(distance) + "km";
        } else if (distance < 999.9) {
            return String.valueOf((int) distance) + "km";
        } else {
            return "1000km外";
        }
    }


    //取消Task
    public static void cancelTask(AsyncTask<?, ?, ?> asyncTask) {
        if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            asyncTask.cancel(true);
        }
    }


    // 中英文字符长度计算,输入法自带表情 = 4个英文字符
    public static int calculateCharactersNum(String strContent) {
        if (TextUtils.isEmpty(strContent))
            return -1;

        String strTemp = strContent.trim();// 去除字符串首尾空格
        int nStrLen = 0;
        for (int i = 0; i < strTemp.length(); i++) {
            char c = strTemp.charAt(i);
            int tmp = (int) c;
            Log.v("tang", "value: " + tmp);
            if (tmp > 0 && tmp < 127) {
                nStrLen++;
            } else if (tmp == 65532)//65532表情图案的数值
            {
                nStrLen += 7;// 1个表情 = 7个英文字符(自定义)
            } else {
                nStrLen += 2;// 1个汉字 = 2个英文字符
            }
        }

        return nStrLen;
    }

    //读取打赏模式
    public static boolean readSponsorMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(XConstant.CONFIG_FILENAME, Context.MODE_PRIVATE);
        boolean bSponsorOn = true;
        if (sp.contains(XConstant.SPONSOR_MODE)) {
            bSponsorOn = sp.getBoolean(XConstant.SPONSOR_MODE, true);
        }
        return bSponsorOn;
    }

    // 敏感词检测
    public static boolean detectionKeyword(String strContent) {
        if (TextUtils.isEmpty(strContent)) {
            return true;
        }

        for (int i = 0; i < XConstant.flitkey1.length; i++) {
            if (strContent.indexOf(XConstant.flitkey1[i]) != -1) {
                return false;
            }
        }

        if (strContent.matches(XConstant.paString) || strContent.matches(XConstant.paString2)) {
            return false;
        }

        return true;
    }

    // 敏感词替换
    public static String replaceKeyWord(String strContent) {
        String sReplace = strContent;
        if (TextUtils.isEmpty(sReplace)) {
            return sReplace;
        }

        for (int i = 0; i < XConstant.flitkey1.length; i++) {
            sReplace = sReplace.replaceAll(XConstant.flitkey1[i], "**");
        }


        return sReplace;
    }

    // 使用正则表达式检测敏感词
    public static boolean regularExpressionsKeyWord(String strContent) {
        if (strContent.matches(XConstant.paString) || strContent.matches(XConstant.paString2)) {
            return false;
        }

        return true;
    }

    // 获取目录文件大小
    public static long getDirSize(File dir) throws Exception {
        if (dir == null) {
            return 0;
        }

        if (!dir.isDirectory() || !dir.exists()) {
            return 0;
        }

        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                //	dirSize += file.length();
                // 递归调用继续统计
                dirSize += getDirSize(file);
            }
        }

        return dirSize;
    }

    // 转换文件大小 B/KB/MB/GB
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1) {
            fileSizeString = "0KB";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }

        return fileSizeString;
    }

    // 从Sdcard加载图片
    public static Bitmap loadImageFromSD(String strFilePath) {
        return BitmapFactory.decodeFile(strFilePath);
    }

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showTextToast(Context context, String msg) {
        mHandler.removeCallbacks(r);
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static Bitmap drawTextToCenter(Context context, int position, Bitmap bitmap, String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        switch (position) {
            case 0:
                paint.setColor(Color.BLACK);
                break;
            case 7:
                paint.setColor(Color.BLACK);
                break;
            default:
                paint.setColor(Color.WHITE);
                break;
        }
        paint.setTextSize(dip2px(context, 14));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                (bitmap.getWidth() - bounds.width()) / 2,
                (bitmap.getHeight() + bounds.height()) / 2);
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    public static Bitmap createWatermark(Context context, int position, Bitmap bitmap, String markText) {

        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        // 画背景图
        canvas.drawBitmap(bitmap, 0, 0, null);
        //-------------开始绘制文字-------------------------------

        if (!TextUtils.isEmpty(markText)) {
            int screenWidth = getScreenWidth(context);
            float textSize = dip2px(context, 14) * bitmapWidth / screenWidth;
            // 创建画笔
            TextPaint mPaint = new TextPaint();
            // 文字矩阵区域
            Rect textBounds = new Rect();

            // 水印的字体大小
            mPaint.setTextSize(dip2px(context, 14));
            // 文字阴影
            mPaint.setShadowLayer(0.5f, 0f, 1f, Color.YELLOW);
            // 抗锯齿
            mPaint.setAntiAlias(true);
            // 水印的区域
            mPaint.getTextBounds(markText, 0, markText.length(), textBounds);
            // 水印的颜色
            float textX = 0;
            float textY = 0;
            switch (position) {
                case 0:
                    mPaint.setColor(Color.BLACK);
                    break;
                case 7:
                    mPaint.setColor(Color.BLACK);
                    break;
                default:
                    mPaint.setColor(Color.WHITE);
                    textX = dip2px(context, 10) * bitmapWidth / screenWidth;
//                    textX = bitmapWidth / 2;
                    textY = dip2px(context, 10) * bitmapWidth / screenWidth;
                    break;
            }

            StaticLayout layout = new StaticLayout(markText, 0, markText.length(), mPaint, (int) (bitmapWidth - textSize),
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
            // 文字开始的坐标

            // 画文字
            canvas.translate(textX, textY);
            layout.draw(canvas);
        }

        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bmp;
    }

    public static byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }
    public static int byteArrayToLeInt(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE * 1);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }


    /**
     * YUV420sp
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] getYUV420sp(int inputWidth, int inputHeight,
                                     Bitmap scaled) {

        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    /**
     * RGB转YUV420sp
     *
     * @param yuv420sp
     *            inputWidth * inputHeight * 3 / 2
     * @param argb
     *            inputWidth * inputHeight
     * @param width
     * @param height
     */
    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width,
                                       int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;

        // ---颜色数据---
        int a, R, G, B;
        //
        int argbIndex = 0;
        //

        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // a is not used obviously
                a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                //
                argbIndex++;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                //
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                // NV21 has a plane of Y and interleaved planes of VU each
                // sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                // sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    //
                    yuv420sp[uvIndex++] = (byte) V;
                    //
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }

}
