package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.MyApplication;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.LoginResultBean;
import com.jhjj9158.niupaivideo.bean.QQLoginBean;
import com.jhjj9158.niupaivideo.bean.UserInfoBean;
import com.jhjj9158.niupaivideo.bean.UserPostBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.MD5Util;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiuxiu.util.XConstant;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuickLoignActivity extends BaseActivity {

    private static final String TAG = "QuickLoignActivity";

    private static final int LOGIN_WECHAT = 3;
    private static final int LOGIN_QQ = 1;
    private static final int LOGIN_SINA = 5;
    private static final int GET_NICKNAME = 2;

    @Bind(R.id.ll_login_wechat)
    LinearLayout llLoginWechat;
    @Bind(R.id.ll_login_qq)
    LinearLayout llLoginQq;
    @Bind(R.id.ll_login_sina)
    LinearLayout llLoginSina;
    @Bind(R.id.tv_login_crystal)
    TextView tvLoginCrystal;
    @Bind(R.id.tv_login_happy)
    TextView tvLoginHappy;
    @Bind(R.id.tv_agressment_1)
    TextView tvAgressment1;
    @Bind(R.id.tv_agressment)
    TextView tvAgressment;

    private UMShareAPI mShareAPI = null;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String jsonLogin = msg.obj.toString();
            switch (msg.what) {
                case LOGIN_QQ:
                    getLoginResult(jsonLogin, LOGIN_QQ);
                    break;
                case LOGIN_SINA:
                    getLoginResult(jsonLogin, LOGIN_SINA);
                    break;
                case LOGIN_WECHAT:
                    getLoginResult(jsonLogin, LOGIN_WECHAT);
                    break;
                case GET_NICKNAME:
                    setUserInfo(jsonLogin);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setUserInfo(String json) {
        Gson gson = new Gson();
        UserInfoBean userInfoBean = gson.fromJson(json, UserInfoBean.class);
        UserInfoBean.DataBean userInfo = userInfoBean.getData().get(0);
        if (userInfoBean.getCode() == 100) {
            //保存
            String name = null;
            try {
                name = new String(URLDecoder.decode(userInfo.getNickName(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            CacheUtils.setString(QuickLoignActivity.this, Contact.NICKNAME, name);
            com.xiuxiu.util.CacheUtils.setString(QuickLoignActivity.this, XConstant.NICKNAME , name);
            finish();
        }
    }

    private void getLoginResult(String json, int type) {
        Gson gson = new Gson();
        LoginResultBean loginResult = gson.fromJson(json, LoginResultBean.class);
        CommonUtil.showTextToast(QuickLoignActivity.this, loginResult.getMsg());
        if (loginResult.getCode() == 100) {
            int useridx = loginResult.getData().get(0).getUseridx();
            CacheUtils.setInt(QuickLoignActivity.this, Contact.USERIDX, useridx);
            CacheUtils.setInt(QuickLoignActivity.this, Contact.OLDUIDX, loginResult
                    .getData().get(0).getOldidx());
            CacheUtils.setString(QuickLoignActivity.this, Contact.OLDID, loginResult
                    .getData().get(0).getOldid());
            String pwd = loginResult.getData().get(0).getPassword();
            CacheUtils.setString(QuickLoignActivity.this, Contact.PASSWORD, MD5Util.md5(pwd));
            //更新用户信息
            CommonUtil.updateInfo(QuickLoignActivity.this);
            statisticsNewUser(useridx, type);

            getUserInfo(useridx);

        }
    }

    private void statisticsNewUser(int uidx, int type) {
        String url = Contact.HOST + Contact.STATISTICS_NEW_USER + "?uidx=" + uidx + "&openId=&type=" + type + "&channel=" + MyApplication
                .CHANNEL_ID;
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "登录");

        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
        UMShareAPI.get(this).setShareConfig(config);
        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_quick_loign, null);
    }

    @OnClick({R.id.ll_login_wechat, R.id.ll_login_qq, R.id.ll_login_sina, R.id.tv_login_crystal, R.id.tv_login_happy, R.id.tv_agressment})
    public void onViewClicked(View view) {
        SHARE_MEDIA platform = null;
        switch (view.getId()) {
            case R.id.ll_login_wechat:
                if (!MyApplication.api.isWXAppInstalled()) {
                    CommonUtil.showTextToast(QuickLoignActivity.this, "未安装微信客户端");
                    return;
                }
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "4146c1c15c8887a3d9916ef8fbcedcd7";
                MyApplication.api.sendReq(req);
//                platform = SHARE_MEDIA.WEIXIN;
//                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                break;
            case R.id.ll_login_qq:
                Tencent mTencent = Tencent.createInstance("1105995205", getApplicationContext());
//                platform = SHARE_MEDIA.QQ;
//                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                IUiListener listener = new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        getUserInfoByQQ((JSONObject) o);
                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                };
                if (!mTencent.isSessionValid()) {
                    mTencent.login(QuickLoignActivity.this, "all", listener);
                }
                break;
            case R.id.ll_login_sina:
                if (!UMShareAPI.get(QuickLoignActivity.this).isInstall(QuickLoignActivity.this, SHARE_MEDIA.SINA)) {
                    CommonUtil.showTextToast(QuickLoignActivity.this, "未安装新浪客户端");
                    return;
                }
                platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(QuickLoignActivity.this, platform, umAuthListener);
                break;
            case R.id.tv_login_crystal:
                Intent intentCrystal = new Intent(QuickLoignActivity.this, LoginCrystalActivity.class);
                intentCrystal.putExtra("platform", 11);
                startActivity(intentCrystal);
                break;
            case R.id.tv_login_happy:
                Intent intentHappy = new Intent(QuickLoignActivity.this, LoginCrystalActivity.class);
                intentHappy.putExtra("platform", 3);
                startActivity(intentHappy);
                break;
            case R.id.tv_agressment:
                Intent webIntent = new Intent(this, WebViewActivity.class);
                webIntent.putExtra("fromType", Contact.WEBVIEW_AGREEMENT);
                webIntent.putExtra("url", "file:///android_asset/agreement.html");
                startActivity(webIntent);
                break;
        }

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String media = platform.toString();
            if (media.equals("SINA")) {
                getUserInfoBySina(data);
            } else if (media.equals("WEIXIN")) {
//                getUserInfoByWeixin(data);
            } else if (media.equals("QQ")) {
//                getUserInfoByQQ(o);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    private void getUserInfoByQQ(JSONObject json) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        QQLoginBean bean = new Gson().fromJson(String.valueOf(json), QQLoginBean.class);
        String openid = bean.getOpenid();
        String token = bean.getAccess_token();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            if (entry.getKey().equals("openid")) {
//                openid = entry.getValue();
//            }
//            if (entry.getKey().equals("access_token")) {
//                token = entry.getValue();
//            }
//        }
        Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_QQ + "?platid=0&openid=" + openid + "&token=" + token);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = LOGIN_QQ;
                handler.sendMessage(message);
            }
        });
    }

    private void getUserInfoBySina(Map<String, String> map) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String openid = null;
        String token = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("uid")) {
                openid = entry.getValue();
            }
            if (entry.getKey().equals("access_token")) {
                token = entry.getValue();
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_SINA + "?platid=0&openid=" + openid + "&token=" + token);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();

                message.obj = response.body().string();
                message.what = LOGIN_SINA;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //微信登录
        String code = CacheUtils.getString(QuickLoignActivity.this, "code_weixin");
        if (!code.isEmpty()) {
            CacheUtils.delString(QuickLoignActivity.this, "code_weixin");
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Log.d(TAG, "onResume: " + Contact.LOGIN_WEIXIN + "?platid=0&code=" + code);
            Request.Builder requestBuilder = new Request.Builder().url(Contact.LOGIN_WEIXIN + "?platid=0&code=" + code);
            requestBuilder.method("GET", null);
            Request request = requestBuilder.build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message = new Message();

                    message.obj = response.body().string();
                    message.what = LOGIN_WECHAT;
                    handler.sendMessage(message);
                }
            });
        }
        if (CacheUtils.getInt(QuickLoignActivity.this, Contact.USERIDX) != 0) {
            finish();
        }
        MobclickAgent.onPageStart("QuickLoginActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("QuickLoginActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    private void getUserInfo(int uid) {

        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("GetUserInfor");
        userPostBean.setUseridx(uid);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = GET_NICKNAME;
                handler.sendMessage(message);
            }
        });
    }
}
