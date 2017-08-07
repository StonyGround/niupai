package com.jhjj9158.niupaivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ToolUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.tv_agressment)
    TextView tvAgressment;
    @Bind(R.id.tv_version_name)
    TextView tvVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this , "关于");
        tvVersionName.setText(ToolUtils.getVersionName(this));
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_about, null);
    }



    @OnClick(R.id.tv_agressment)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_agressment:
                Intent webIntent = new Intent(this, WebViewActivity.class);
                webIntent.putExtra("fromType", Contact.WEBVIEW_AGREEMENT);
                webIntent.putExtra("url","file:///android_asset/agreement.html");
                startActivity(webIntent);
                break;
        }
    }
}
