package com.jhjj9158.niupaivideo.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.MyApplication;
import com.jhjj9158.niupaivideo.activity.QuickLoignActivity;
import com.jhjj9158.niupaivideo.bean.LoginResultBean;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import java.io.IOException;

import okhttp3.Call;

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.getType()) {
            case 1://1为第三方登录,2为分享
                String code = ((SendAuth.Resp) resp).code;
                if (!TextUtils.isEmpty(code)) {
                    CacheUtils.setString(this, "code_weixin", code);
                }
                finish();
                break;
            default:
                finish();
                break;
        }
    }
}
