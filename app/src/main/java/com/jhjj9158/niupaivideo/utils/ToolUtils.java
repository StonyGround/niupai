package com.jhjj9158.niupaivideo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hzdykj on 2017/6/3.
 */

public class ToolUtils {


    //判断空字符串
    public static boolean isEmpty(String str){
        if(str==null||"".equals(str)){
            return true;
        }
        return false;
    }

    //获得VersionCode
    public static String getVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            versionCode = packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    //获取版本
    public static String getVersionName(Context context){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //判断字节长度
    public static String getWholeText(String text, int byteCount){
        try {
            if (text != null && text.getBytes("utf-8").length > byteCount) {
                char[] tempChars = text.toCharArray();
                int sumByte = 0;
                int charIndex = 0;
                for (int i = 0, len = tempChars.length; i < len; i++) {
                    char itemChar = tempChars[i];
                    // 根据Unicode值，判断它占用的字节数
                    if (itemChar >= 0x0000 && itemChar <= 0x007F) {
                        sumByte += 1;
                    } else if (itemChar >= 0x0080 && itemChar <= 0x07FF) {
                        sumByte += 2;
                    } else {
                        sumByte += 3;
                    }
                    if (sumByte > byteCount) {
                        charIndex = i;
                        break;
                    }
                }
                return String.valueOf(tempChars, 0, charIndex);
            }
        } catch (UnsupportedEncodingException e) {
        }
        return text;
    }

    //禁止输入表情
    public static boolean isEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }
}
