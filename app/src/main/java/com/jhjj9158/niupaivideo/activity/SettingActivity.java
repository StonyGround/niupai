package com.jhjj9158.niupaivideo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.DataCleanUtil;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.setting_feedback)
    RelativeLayout settingFeedback;
    @Bind(R.id.setting_about)
    RelativeLayout settingAbout;
    @Bind(R.id.setting_clear)
    RelativeLayout settingClear;
    @Bind(R.id.setting_quit)
    TextView settingQuit;
    @Bind(R.id.setting_clear_size)
    TextView settingClearSize;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "设置");
//        try {
//            settingClearSize.setText(getString(R.string.cache_size, DataCleanUtil
//                    .getTotalCacheSize(SettingActivity.this)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        builder = new AlertDialog.Builder(this);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_setting, null);
    }

    @OnClick({R.id.setting_feedback, R.id.setting_clear, R.id.setting_quit, R.id.setting_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.setting_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.setting_clear:
                builder.setMessage("确定清理缓存吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanUtil.cleanApplicationData(SettingActivity.this);
                                CommonUtil.showTextToast(SettingActivity.this, "清除成功");
//                                settingClearSize.setText(getString(R.string.cache_size, "0M"));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.setting_quit:
                builder.setMessage("确定退出当前帐号吗？")
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = Contact.HOST + Contact.QUIT + "?uidx=" + com.xiuxiu.util.CacheUtils.getInt(SettingActivity
                                        .this, Contact.USERIDX);
                                OkHttpClientManager.get(url, new OKHttpCallback() {
                                    @Override
                                    public void onResponse(Object response) {
                                    }

                                    @Override
                                    public void onError(IOException e) {

                                    }
                                });
                                CacheUtils.setInt(SettingActivity.this, Contact.USERIDX, 0);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }
}
