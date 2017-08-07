package com.jhjj9158.niupaivideo.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pc on 17-4-13.
 */

public class OkHttpUtils {

    //声明
    private static volatile OkHttpUtils okHttpUtils;
    Handler handler;
    private final Gson gson;
    //声明接口
    MCallBack mCallBack;

    // 私有构造方法
    private OkHttpUtils() {
        handler = new Handler(Looper.getMainLooper());
        gson = new Gson();
    }

    //设置方法
    public static OkHttpUtils getOkHttpUtils() {
        if (null == okHttpUtils) {
            synchronized (OkHttpUtils.class) {
                if (null == okHttpUtils) {
                    okHttpUtils = new OkHttpUtils();
                }
            }
        }
        return okHttpUtils;
    }


    //设置拼接字符串的方法
    public void get(String url, MCallBack callBack) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
//                getJson(json, cls);
                mainThread(json);
            }
        });

        this.mCallBack = callBack;
    }

    //解析
    public <T> void getJson(String json, Class<T> cls) {

//        T t = gson.fromJson(json, cls);
//        mainThread(t);
    }

//    //设置请求Post请求
//    public <T> void post(String url, Map<String, String> map, final Class<T> cls) {
//        //得到Client对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //得到Body
//        RequestBody formBody = null;
//        //遍历
//        Iterator<String> iterator = map.keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            //获取Value
//            String value = map.get(key);
//            formBody = new FormBody.Builder().add(key, value).build();
//        }
//        //得到Result
//        Request request = new Request.Builder().url(url).post(builder.build()).build();
//
//        //获取call
//        final Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call1, IOException e) {
//                if (mCallBack != null) {
//                    mCallBack.onFailure(call, e);
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                //设置解析
//                String json = response.body().string();
//                getJson(json, cls);
//            }
//        });
//
//    }

    //将消息发送到主线程
    public void  mainThread(final String result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //接口回调
                mCallBack.onResponse(result);
            }
        });
    }

    //设置接口
    public interface MCallBack {
        void onResponse(String json);

        void onFailure(Call call, IOException e);
    }

    //设置接口
    public void setmCallBack(MCallBack callBack) {
        this.mCallBack = callBack;
    }

//    //请求网络
//    public void initNetData(String url, Map map, Class cls, Methods methods) {
//        switch (methods) {
//            case GET:
//                get(url, map, cls);
//                break;
//            case POST:
//                post(url, map, cls);
//                break;
//        }
//    }


    //生成枚举
    public enum Methods {
        GET, POST

    }

}
