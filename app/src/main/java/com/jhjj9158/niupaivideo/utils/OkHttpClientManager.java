package com.jhjj9158.niupaivideo.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by oneki on 2017/5/18.
 */

public class OkHttpClientManager {

    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private Gson mGson;

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    private static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public static void get(String url, OKHttpCallback callback) {
        getInstance().getAsyn(url, callback);
    }

    /**
     * 返回未加密数据
     * @param url
     * @param callback
     */
    public static void getUnencrypt(String url, OKHttpCallback callback) {
        getInstance().getAsynUnencrypt(url, callback);
    }

    private void getAsyn(String url, final OKHttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallback(response.body().string(), callback);
            }
        });
    }

    private void sendSuccessCallback(final String json, final OKHttpCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Object o = AESUtil.decode(json);
                Log.d("json", String.valueOf(o));
                Type type = callback.getType();
                if (type != null) {
                    o = mGson.fromJson((String) o, type);
                }
                callback.onResponse(o);
            }
        });
    }

    private void getAsynUnencrypt(String url, final OKHttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendUnencryptSuccessCallback(response.body().string(), callback);
            }
        });
    }

    private void sendUnencryptSuccessCallback(final String json, final OKHttpCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(json);
            }
        });
    }

    private void sendFailedCallback(final OKHttpCallback callback, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }
}
