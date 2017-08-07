package com.jhjj9158.niupaivideo;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.jhjj9158.niupaivideo.broadcast.NetStateChangeReceiver;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.CrashHandler;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.PicassoImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.IOException;

/**
 * Created by pc on 17-4-10.
 */

public class MyApplication extends Application {

    private int count;

    {
        PlatformConfig.setWeixin("wx17181f643ff9a6c8", "6b16d435a31070b3ddb0cf0000e04445");
        PlatformConfig.setQQZone("1105995205", "UjRJFeXvj6EyH1Bq");
        PlatformConfig.setSinaWeibo("1694884006", "cd1be2e8b6f78d3d17b422473e47244c", "http://www.quliao.com/");
    }

    private static final String WEIXIN_APP_ID = "wx17181f643ff9a6c8";
    private static final String UMENG_APP_KEY = "58eb71334ad1566c47001e16";

    public static IWXAPI api;

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
        api.registerApp(WEIXIN_APP_ID);
    }

    public static String CHANNEL_VALUE;
    public static int CHANNEL_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        Config.DEBUG = true;
        UMShareAPI.get(this);
        Config.isJumptoAppStore = true;
        //注册微信
        regToWx();

        //网络检测
        NetStateChangeReceiver.registerReceiver(this);

        //异常捕获
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        //图片选择器
        initImagePicker();

        //获取渠道信息
        getChannelInfo();

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(getApplicationContext(), UMENG_APP_KEY, CHANNEL_VALUE));

        //APP前后台检测
        ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                count++;
                if (0 == count - 1) {
                    Log.d("MyApplication", "foreground");
                    statisticsApp(2);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (0 == count) {
                    Log.d("MyApplication", "background");
                    statisticsApp(3);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        // 注册监听
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    private void statisticsApp(int stype) {
        String url = Contact.HOST + Contact.STATISTICS_APP + Contact.getDeviceDetail(this, stype);
        Log.e("DeviceDetail", String.valueOf(url));
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
                Log.e("DeviceDetail", String.valueOf(response));
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    private void getChannelInfo() {
        try {
            ApplicationInfo info = this.getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            CHANNEL_ID = info.metaData.getInt("Channel_ID");
            CHANNEL_VALUE = info.metaData.getString("Channel_Value");
            Log.e("getChannelInfo", CHANNEL_ID + "---------" + CHANNEL_VALUE);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CHANNEL_ID = 1;
            CHANNEL_VALUE = "tg";
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public void onTerminate() {
        // 取消BroadcastReceiver注册
        NetStateChangeReceiver.unregisterReceiver(this);
        super.onTerminate();
    }

}
