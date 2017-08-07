package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;

public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView.requestFocusFromTouch();
        webView.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        int fromType = intent.getIntExtra("fromType", 0);
        int fuidx = intent.getIntExtra("fuidx", 0);
        int oldidx = CacheUtils.getInt(this, "oldidx");
        String oldid = CacheUtils.getString(this, "oldid");
        String pwd = CacheUtils.getString(this, "password");

        String intentUrl=intent.getStringExtra("url");

        if (fromType == 3) {
            initTitle(this, "欢乐直播登录");
        } else if(fromType==11) {
            initTitle(this, "水晶直播登录");
        }else if(fromType== Contact.WEBVIEW_AGREEMENT){
            initTitle(this, "牛拍注册协议");
        }else{
            initTitle(this, "牛拍");
        }

        String url = "http://liveh5.happy88.com/event170406/niupaijump.aspx?uidx=" + oldidx + "&plattype=" + fromType +
                "&type=30&usename=" + oldid + "&pwd=" + pwd + "&fuidx=" + fuidx;

        if(TextUtils.isEmpty(intentUrl)){
            webView.loadUrl(url);
        }else{
            webView.loadUrl(intentUrl);
        }

    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_web_view, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WebViewActivity");
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WebViewActivity");
        MobclickAgent.onPause(this);
    }
}
