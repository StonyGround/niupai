package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.bean.FollowPostBean;
import com.jhjj9158.niupaivideo.bean.PersonalBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.dialog.DialogImage;
import com.jhjj9158.niupaivideo.dialog.DialogReport;
import com.jhjj9158.niupaivideo.fragment.BaseDynamicFragment;
import com.jhjj9158.niupaivideo.fragment.FragmentFavorite;
import com.jhjj9158.niupaivideo.fragment.FragmentWorks;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.BlurTransformation;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.InitiView;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalActivity extends BaseActivity {

    private static final int GET_OTHERS_INFO = 0;
    private static final int FOLLOW = 1;

    @Bind(R.id.personal_back)
    ImageView personalBack;
    @Bind(R.id.personal_name)
    TextView personalName;
    @Bind(R.id.personal_gender)
    ImageView personalGender;
    @Bind(R.id.personal_more)
    ImageView personalMore;
    @Bind(R.id.personal_follow)
    TextView personalFollow;
    @Bind(R.id.personal_fans)
    TextView personalFans;
    @Bind(R.id.personal_id)
    TextView personalId;
    @Bind(R.id.btn_personal_follow)
    TextView btnPersonalFollow;
    @Bind(R.id.personal_signature)
    TextView personalSignature;
    @Bind(R.id.ll_personal_info)
    LinearLayout llPersonalInfo;
    @Bind(R.id.personal_tab)
    TabLayout personalTab;
    @Bind(R.id.personal_headimg)
    CircleImageView personalHeadimg;
    @Bind(R.id.personal_bg)
    ImageView personalBg;
    @Bind(R.id.personal_viewpager)
    HorizontalScrollViewPager personalViewpager;

    private int buidx;
    private int uidx;
    private int isFollow;
    private int fansNum;
    private int vid;

    private List<BaseDynamicFragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case GET_OTHERS_INFO:
                    setPersonalInfo(AESUtil.decode(json));
                    break;
                case FOLLOW:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String result = jsonObject.getString("msg");
                        if (result.equals("关注成功")) {
                            btnPersonalFollow.setText(R.string.unfollow);
                            personalFans.setText(" " + "粉丝" + fansNum);
                        } else {
                            btnPersonalFollow.setText(R.string.tv_personal_follow);
                            personalFans.setText(" " + "粉丝" + fansNum);
                        }
                        CommonUtil.showTextToast(PersonalActivity
                                .this, result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    btnPersonalFollow.setClickable(true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String headImage;

    private void setPersonalInfo(String json) {
        Gson gson = new Gson();
        PersonalBean.ResultBean resultBean = gson.fromJson(json, PersonalBean.class).getResult();
        String name = null;
        try {
            name = URLDecoder.decode(new String(Base64.decode(resultBean.getNickName().getBytes(),
                    Base64.DEFAULT)),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headImage = new String(Base64.decode(resultBean.getHeadphoto().getBytes(),
                Base64.DEFAULT));
        if (!headImage.contains("http")) {
            headImage = "http://" + headImage;
        }
        int followNum = resultBean.getFollowNum();
        fansNum = resultBean.getFansNum();
        String signature = null;
        try {
            signature = URLDecoder.decode(new String(Base64.decode(resultBean.getSignature().getBytes(),
                    Base64.DEFAULT)),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int showuidx = resultBean.getShowuidx();
        isFollow = resultBean.getIsFollow();
        int worksNum = resultBean.getVNum();
        int favoriteNum = resultBean.getZanNum();

        personalName.setText(name);
        if (resultBean.getGender() == 2) {
            personalGender.setImageResource(R.drawable.women);
        } else if (resultBean.getGender() == 0) {
            personalGender.setVisibility(View.GONE);
        }
        Picasso.with(this).load(headImage).placeholder(R.drawable.me_user_admin).into(personalHeadimg);
        personalFollow.setText("关注" + followNum + " |");
        personalFans.setText(" " + "粉丝" + fansNum);
        if (TextUtils.isEmpty(signature)) {
            personalSignature.setText("这个人很懒，什么都没有写...");
        } else {
            personalSignature.setText(signature);
        }
        personalId.setText("ID:" + showuidx);
        if (isFollow == 1) {
            btnPersonalFollow.setText("取消关注");
        }
        Glide.with(this).load(headImage).placeholder(R.drawable.me_user_admin).transform(new BlurTransformation(this,20))
                .into(personalBg);
        titles.add("作品" + worksNum);
        titles.add("喜欢" + favoriteNum);
        initTabPager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();

        Intent getIntent = getIntent();
        buidx = getIntent.getIntExtra("buidx", 0);
        vid = getIntent.getIntExtra("vid", 0);
        uidx = CacheUtils.getInt(this, Contact.USERIDX);
        CacheUtils.setInt(this, "buidx", buidx);


        fragmentList.add(new FragmentWorks());
        fragmentList.add(new FragmentFavorite());

        getPersonalInfo();

    }

    @Override
    protected View getChildView() {
        return View.inflate(this,R.layout.activity_personal,null);
    }

    private void initTabPager() {

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter
                (getSupportFragmentManager(), fragmentList, titles);
        personalViewpager.setAdapter(tabFragmentAdapter);
        personalTab.setTabMode(TabLayout.MODE_FIXED);
        personalTab.setupWithViewPager(personalViewpager);
    }

    private FragmentTransaction transaction;

    private void getPersonalInfo() {
        String url = Contact.HOST + Contact.PERSONAL_INFO + "?uidx=" + uidx + "&buidx=" + buidx;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
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
                message.what = GET_OTHERS_INFO;
                handler.sendMessage(message);
            }
        });
    }

    @OnClick({R.id.personal_more, R.id.personal_back, R.id.btn_personal_follow, R.id
            .personal_follow, R.id.personal_fans,R.id.personal_headimg})
    public void onViewClicked(final View view) {
        switch (view.getId()) {
            case R.id.personal_more:
                DialogReport dialogReport = new DialogReport(this);
                dialogReport.setReportDialogListener(new DialogReport.ReportDialogListener() {
                    @Override
                    public void onClick() {
                        String url = Contact.HOST + Contact.USER_REPORT + "?uidx=" + uidx + "&buidx=" + buidx + "&vid=" + vid;
                        OkHttpClientManager.get(url, new OKHttpCallback() {
                            @Override
                            public void onResponse(Object response) {
                                try {
                                    JSONObject object = new JSONObject((String) response);
                                    int result = object.getInt("result");
                                    if (result == 1) {
                                        CommonUtil.showTextToast(PersonalActivity.this, "举报成功");
                                        finish();
                                    } else {
                                        CommonUtil.showTextToast(PersonalActivity.this, "举报失败");
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
                });
                InitiView.initiBottomDialog(dialogReport);
//                InitiView.setDialogMatchParent(dialogComment);
                dialogReport.show();
                break;
            case R.id.btn_personal_follow:
                btnPersonalFollow.setClickable(false);
                if (isFollow == 1) {
                    if(buidx!=uidx) {
                        fansNum = fansNum - 1;
                    }
                    isFollow = 2;
                    setFollow(2);
                } else {
                    isFollow = 1;
                    if(buidx!=uidx) {
                        fansNum = fansNum + 1;
                    }
                    setFollow(1);
                }
                break;
            case R.id.personal_back:
                finish();
                break;
            case R.id.personal_follow:
                Intent intent = new Intent(PersonalActivity.this, FollowActivity.class);
                intent.putExtra("buidx", buidx);
                startActivity(intent);
                break;
            case R.id.personal_fans:
                Intent intentFans = new Intent(PersonalActivity.this, FansActivity.class);
                intentFans.putExtra("buidx", buidx);
                startActivity(intentFans);
                break;
            case R.id.personal_headimg:
                DialogImage dialogImage=new DialogImage(this,headImage);
                InitiView.setDialogMatchParent(dialogImage);
                dialogImage.show();
                break;
        }
    }

    private void setFollow(int index) {
        FollowPostBean followPostBean = new FollowPostBean();
        followPostBean.setOpcode("FocusonOrDeletecurd_friends");
        followPostBean.setUseridx(uidx);
        followPostBean.setFriendidx(buidx);
        followPostBean.setIndex(index);

        Gson gson = new Gson();
        String json = gson.toJson(followPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(json, Contact.KEY))
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
                message.what = FOLLOW;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("PersonalActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("PersonalActivity");
    }
}
