package com.jhjj9158.niupaivideo.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.NetworkType;
import com.jhjj9158.niupaivideo.broadcast.NetStateChangeReceiver;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.observer.NetStateChangeObserver;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.NetworkUtils;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {

    Toolbar toolbar;
    LinearLayout llChildContent;
    ImageView toolbar_back;
    TextView toolbar_title;
    LinearLayout ll_toolbar;
    RelativeLayout toolbar_right;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //Activity栈
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);

        //网络检测
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.registerObserver(this);
        }

        //状态栏控制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(50, 00, 00, 00));
        }

        llChildContent = (LinearLayout) findViewById(R.id.ll_child_content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_back = (ImageView) findViewById(R.id.toolbar_back);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        ll_toolbar = (LinearLayout) findViewById(R.id.ll_toolbar);
        toolbar_right = (RelativeLayout) findViewById(R.id.toolbar_right);

        View child = getChildView();
        ButterKnife.bind(this, child);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, -1);
        llChildContent.addView(child, params);
    }

    protected abstract View getChildView();

    protected void initTitle(final Activity activity, String title) {
        toolbar_title.setText(title);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    //toolbar右侧添加view
    protected void addToolBarRightView(View view, View.OnClickListener mOnClickListener) {
        toolbar_right.addView(view);
        if (mOnClickListener == null) return;
        toolbar_right.setOnClickListener(mOnClickListener);
    }

    //隐藏toolbar
    protected void hintTitle() {
        ll_toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (alertDialog != null && !NetworkUtils.isConnected(this)) {
            alertDialog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 是否需要注册网络变化的Observer,如果不需要监听网络变化,则返回false;否则返回true.默认返回false
     */
    protected boolean needRegisterNetworkChangeObserver() {
        return true;
    }

    @Override
    public void onNetDisconnected() {
        Log.e("BaseActivity", "onNetDisconnected");
        alertDialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("网络连接已断开")
                .setPositiveButton(getString(R.string.net_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new
                                Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNeutralButton(getString(R.string.net_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                }).show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        Log.e("BaseActivity", "onNetConnected");
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }
}
