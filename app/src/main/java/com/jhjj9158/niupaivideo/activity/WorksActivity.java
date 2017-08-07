package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorksActivity extends BaseActivity {

    private final static int SET_WORKS_DATA = 0;

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case SET_WORKS_DATA:
                    setWorksData(AESUtil.decode(json));
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setWorksData(String json) {
        Gson gson = new Gson();
        final List<IndexBean.ResultBean> resultBean = gson.fromJson(json, IndexBean
                .class).getResult();

        initTitle(WorksActivity.this, "作品(" + resultBean.size() + ")");
        if (resultBean.size() == 0) {
            worksNothing.setVisibility(View.VISIBLE);
            return;
        }

        WorksAdapter adapter = new WorksAdapter(WorksActivity.this, resultBean);
        adapter.setOnItemClickListener(new WorksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, IndexBean.ResultBean data) {
                Intent intent = new Intent(WorksActivity.this, VideoActivity.class);
                intent.putExtra("video", data);
                startActivity(intent);
            }
        });
        rvWorks.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(WorksActivity.this, "作品");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvWorks.setLayoutManager(gridLayoutManager);
        rvWorks.addItemDecoration(new SpaceItemDecoration(4));

        getWorksData();
    }

    private void getWorksData() {
        int uidx = CacheUtils.getInt(this, Contact.USERIDX);
        String worksUrl = Contact.HOST + Contact.TAB_WORKS + "?uidx=" + uidx + "&loginUidx=" +
                uidx + "&begin=1&num=100";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(worksUrl);
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
                message.what = SET_WORKS_DATA;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WorksActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WorksActivity");
        MobclickAgent.onPause(this);
    }
}
