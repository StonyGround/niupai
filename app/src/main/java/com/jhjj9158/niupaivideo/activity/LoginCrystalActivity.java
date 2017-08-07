package com.jhjj9158.niupaivideo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.LoginResultBean;
import com.jhjj9158.niupaivideo.bean.UserBean;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.MD5Util;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginCrystalActivity extends BaseActivity {

    @Bind(R.id.name)
    AutoCompleteTextView name;
    @Bind(R.id.pwd)
    EditText pwd;
    @Bind(R.id.btn_login)
    Button btnLogin;

    private int platform;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String jsonLogin = msg.obj.toString();
                    CacheUtils.setString(LoginCrystalActivity.this, "userInfoJson", jsonLogin);
                    Gson gson = new Gson();
                    LoginResultBean loginResult = gson.fromJson(jsonLogin, LoginResultBean.class);
                    CommonUtil.showTextToast(LoginCrystalActivity.this, loginResult.getMsg());
                    if (loginResult.getCode() == 100) {
                        int useridx = loginResult.getData().get(0).getUseridx();
                        CacheUtils.setInt(LoginCrystalActivity.this, Contact.USERIDX, useridx);
                        CacheUtils.setInt(LoginCrystalActivity.this, Contact.OLDUIDX, loginResult
                                .getData().get(0).getOldidx());
                        CacheUtils.setString(LoginCrystalActivity.this, Contact.OLDID, loginResult
                                .getData().get(0).getOldid());
                        String pwd = loginResult.getData().get(0).getPassword();
                        CacheUtils.setString(LoginCrystalActivity.this, Contact.PASSWORD, MD5Util.md5(pwd));
                        //更新用户信息
                        CommonUtil.updateInfo(LoginCrystalActivity.this);

                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        platform = getIntent().getIntExtra("platform", 0);

        if (platform == 3) {
            initTitle(this, "欢乐直播登录");
        } else {
            initTitle(this, "水晶直播登录");
        }

        initAutoComplete("history", name);

        btnLogin.setClickable(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userIsEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {
                userIsEmpty();
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userIsEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {
                userIsEmpty();
            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_login, null);
    }

    public void userIsEmpty() {
        if (!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(pwd.getText()) && pwd.getText().toString().length() >= 6) {
            btnLogin.setClickable(true);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.button_login_click));
        } else {
            btnLogin.setClickable(false);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.button_login));
        }
    }


    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        UserBean userBean = new UserBean();
        userBean.setOpcode("UserLogin");
        userBean.setUseridx(name.getText().toString());
        userBean.setPassword(pwd.getText().toString());
        userBean.setPlatformtype(platform);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userBean);

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
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    private void initAutoComplete(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("name_login", 0);
        String longhistory = sp.getString("history", "nothing");
        if (longhistory.equals("nothing"))
            return;
        String[] hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);
        //只保留最近的50条的记录
        if (hisArrays.length > 50) {
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(adapter);
        auto.setDropDownHeight(350);
        auto.setThreshold(1);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("name_login", 0);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).apply();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("LoginCrystalActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("LoginCrystalActivity");
    }
}
