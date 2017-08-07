package com.jhjj9158.niupaivideo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.OnClick;

public class AccountEditActivity extends BaseActivity {

    @Bind(R.id.account_alipay)
    EditText accountAlipay;
    @Bind(R.id.account_name)
    EditText accountName;
    @Bind(R.id.account_confirm)
    TextView accountConfirm;

    private boolean isClick = false;
    private double money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "绑定支付宝账号");

//        if(!TextUtils.isEmpty(CacheUtils.getString(this,"account_alipay"))){
//            accountAlipay.setText(CacheUtils.getString(this,"account_alipay"));
//            accountName.setText(CacheUtils.getString(this,"account_name"));
//        }
        money = getIntent().getDoubleExtra("money", 0);

        accountAlipay.addTextChangedListener(new TextWatcher() {
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

        accountName.addTextChangedListener(new TextWatcher() {
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

    public void userIsEmpty() {
        if (!TextUtils.isEmpty(accountAlipay.getText()) && !TextUtils.isEmpty(accountName.getText())) {
            isClick = true;
            accountConfirm.setBackgroundResource(R.drawable.btn_circle_save);
        } else {
            isClick = false;
            accountConfirm.setBackgroundResource(R.drawable.btn_confirm_unclick);
        }
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_account_edit, null);
    }

    @OnClick(R.id.account_confirm)
    public void onViewClicked() {
        if (isClick) {
            new AlertDialog.Builder(this)
                    .setTitle("请再次核实您的支付宝信息")
                    .setMessage("支付宝账号：" + accountAlipay.getText().toString() + "\n真实姓名：" + accountName.getText().toString())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String url = null;
                            try {
                                url = Contact.HOST + Contact.BIND_ALIPAY + "?uidx=" + CacheUtils.getInt(AccountEditActivity.this,
                                        Contact.USERIDX) + "&alipay=" + accountAlipay.getText().toString() + "&alipayName=" + URLEncoder.encode
                                        (accountName.getText().toString(), "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            OkHttpClientManager.get(url, new OKHttpCallback() {
                                @Override
                                public void onResponse(Object response) {
                                    try {
                                        JSONObject object = new JSONObject((String) response);
                                        String errorcode = object.getString("errorcode");
                                        if (errorcode.equals("00000:ok")) {
                                            CommonUtil.showTextToast(AccountEditActivity.this, "绑定成功");
                                            Intent intent = new Intent(AccountEditActivity.this, WithDrawActivity.class);
                                            intent.putExtra("alipay", accountAlipay.getText().toString());
                                            intent.putExtra("alipay_name", accountName.getText().toString());
                                            intent.putExtra("money", money);
                                            startActivity(intent);

                                            AccountEditActivity.this.finish();
                                        } else {
                                            CommonUtil.showTextToast(AccountEditActivity.this, "绑定失败,请核对支付宝信息!");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(IOException e) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("返回修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
}
