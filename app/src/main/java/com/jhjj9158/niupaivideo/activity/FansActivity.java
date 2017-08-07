package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.FollowAdapter;
import com.jhjj9158.niupaivideo.bean.FollowBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

public class FansActivity extends BaseActivity {

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvWorks.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        int buidx = intent.getIntExtra("buidx", 0);
        int uidx = CacheUtils.getInt(this, Contact.USERIDX);
        if (buidx == 0) {
            buidx = uidx;
        }
        String worksUrl = Contact.HOST + Contact.GET_FANS + "?uidx=" + buidx + "&buidx=" + uidx
                + "&begin=1&num=1000";
        OkHttpUtils.getOkHttpUtils().get(worksUrl, new OkHttpUtils.MCallBack
                () {
            @Override
            public void onResponse(String json) {
                String result = AESUtil.decode(json);
                Gson gson = new Gson();
                List<FollowBean.ResultBean> resultBean = gson.fromJson(result, FollowBean
                        .class).getResult();
                initTitle(FansActivity.this, "粉丝(" + resultBean.size() + ")");
                if (resultBean.size() == 0) {
                    worksNothing.setText("你还没有粉丝");
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }

                FollowAdapter adapter = new FollowAdapter(FansActivity.this, resultBean);
                adapter.setOnItemClickListener(new FollowAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, FollowBean.ResultBean data) {
                        Intent intent = new Intent(FansActivity.this, PersonalActivity.class);
                        intent.putExtra("buidx", data.getUidx());
                        startActivity(intent);
                    }
                });
                rvWorks.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("FansActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("FansActivity");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }
}
