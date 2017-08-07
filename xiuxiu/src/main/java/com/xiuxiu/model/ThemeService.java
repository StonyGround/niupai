package com.xiuxiu.model;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hzdykj on 2017/6/21.
 */

public class ThemeService {

    public static void pullPersonXml(String path) throws Exception {


//        final List<ThemeInfo> themeInfoList = null;
//        ThemeInfo themeInfo;
//
//        // 创建工厂类对象
//        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//        // 创建解析器对象
//        XmlPullParser parser = factory.newPullParser();

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(path);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ThemeService", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String themes = response.toString();
                Log.e("ThemeService", themes);
//                for (int i = 0; i < themeInfoList.size(); i++) {
//
//                }
            }
        });

    }
}
