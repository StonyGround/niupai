package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.broadcast.NetStateChangeReceiver;
import com.jhjj9158.niupaivideo.fragment.FragmentHome;
import com.jhjj9158.niupaivideo.fragment.FragmentMy;
import com.jhjj9158.niupaivideo.observer.NetStateChangeObserver;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.selector.Matisse;
import com.xiuxiu.selector.MimeType;
import com.xiuxiu.selector.engine.impl.GlideEngine;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements NetStateChangeObserver {

    private static final String TAG = "MainActivity";

    @Bind(R.id.iv_screen)
    ImageView ivScreen;
    @Bind(R.id.flash)
    ImageView flash;


    private FragmentTabHost tabHost;
    private List<View> tabList;
    private Class[] fragmentArray = new Class[]{FragmentHome.class, FragmentMy.class};
    private static final String[] tabTitles = new String[]{"主页", "我的"};
    private int[] imgRes = new int[]{R.drawable.btn_tab_home, R.drawable.btn_tab_my};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Contact.FLASH:
                    if (!CacheUtils.getBoolean(MainActivity.this, Contact.IS_START_MAIN)) {
                        startActivity(new Intent(MainActivity.this, GuideActivity.class));
                    }
                    flash.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
//        CacheUtils.setInt(this, Contact.USERIDX, 1628007796);

        if (CacheUtils.getBoolean(this, Contact.IS_FROM_GUIDE)) {
            flash.setVisibility(View.GONE);
            CacheUtils.setBoolean(this, Contact.IS_FROM_GUIDE, false);
        } else {
            handler.sendEmptyMessageDelayed(Contact.FLASH, 3000);
        }

        if (!CommonUtil.checkPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission
                        .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest
                    .permission.ACCESS_FINE_LOCATION, Manifest.permission
                    .ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Contact.CHECK_PERMISSION);
        }
        CommonUtil.updateInfo(this);
        initiTabHost();
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_main, null);
    }

    private View getTabItemView(int tabIndex) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(imgRes[tabIndex]);
        TextView tab_name = (TextView) view.findViewById(R.id.tab_name);
        tab_name.setText(tabTitles[tabIndex]);
        return view;
    }


    private void initiTabHost() {
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < fragmentArray.length; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabTitles[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            tabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        tabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CacheUtils.getInt(MainActivity.this, Contact.USERIDX) == 0) {
                    startActivity(new Intent(MainActivity.this, QuickLoignActivity
                            .class));
                } else {
                    tabHost.setCurrentTab(1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabHost != null) {
            if (CacheUtils.getInt(MainActivity.this, Contact.USERIDX) == 0) {
                tabHost.setCurrentTab(0);
            }
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION_CAMERA_RECORD:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startActivity(new Intent(this, RecordActivity.class));
//                } else {
//                    new AlertDialog.Builder(this).setMessage("请允许牛拍获取您的相机、录音权限，以确保您能拍摄视频！")
//                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).show();
//                }
                break;
        }
    }

    @OnClick(R.id.iv_screen)
    public void onViewClicked() {
//        CommonUtil.showTextToast(this, "敬请期待");
        if (CacheUtils.getInt(this, Contact.USERIDX) == 0) {
            startActivity(new Intent(this, QuickLoignActivity.class));
            return;
        }

//        if (CommonUtil.checkPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
//                .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})) {
//            startActivity(new Intent(this, RecordActivity.class));
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.RECORD_AUDIO}, Contact.CHECK_PERMISSION_CAMERA_RECORD);
//        }

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            startActivity(new Intent(MainActivity.this, RecordActivity.class));
                        } else {
                            new AlertDialog.Builder(MainActivity.this).setMessage("请允许牛拍获取您的相机、录音权限，以确保您能拍摄视频！")
                                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
