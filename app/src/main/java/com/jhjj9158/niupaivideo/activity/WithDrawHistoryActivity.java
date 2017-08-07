package com.jhjj9158.niupaivideo.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.WithdrawHistoryAdapter;
import com.jhjj9158.niupaivideo.bean.WithdrawHistoryBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;

public class WithDrawHistoryActivity extends BaseActivity {

    @Bind(R.id.rv_works)
    RecyclerView rvWorks;
    @Bind(R.id.works_nothing)
    TextView worksNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(this, "提现记录");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rvWorks.setLayoutManager(linearLayoutManager);

        String url = Contact.HOST + Contact.WITHDRAW_HISTORY + "?uidx=" + CacheUtils.getInt(this, Contact.USERIDX) + "&begin=0&num=100";
        OkHttpClientManager.get(url, new OKHttpCallback<WithdrawHistoryBean>() {
            @Override
            public void onResponse(WithdrawHistoryBean response) {
                List<WithdrawHistoryBean.ResultBean> resultBean = response.getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }
                rvWorks.setAdapter(new WithdrawHistoryAdapter(WithDrawHistoryActivity.this, resultBean));
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }
}
