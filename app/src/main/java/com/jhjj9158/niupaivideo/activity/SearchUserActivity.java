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
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

public class SearchUserActivity extends BaseActivity {

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "用户");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvWorks.setLayoutManager(linearLayoutManager);

        List<FollowBean.ResultBean> resultBean = getIntent().getParcelableArrayListExtra("followBeanList");
        if (resultBean.size() == 0) {
            worksNothing.setVisibility(View.VISIBLE);
            return;
        }

        FollowAdapter adapter = new FollowAdapter(SearchUserActivity.this, resultBean);
        adapter.setOnItemClickListener(new FollowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FollowBean.ResultBean data) {
                Intent intent = new Intent(SearchUserActivity.this, PersonalActivity.class);
                intent.putExtra("buidx", data.getUidx());
                startActivity(intent);
            }
        });
        rvWorks.setAdapter(adapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("SearchUserActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("SearchUserActivity");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }
}
