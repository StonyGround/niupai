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

public class FavoriteActivity extends BaseActivity {

    private final static int SET_FAVORITE_DATA = 0;

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case SET_FAVORITE_DATA:
                    setFavoriteData(AESUtil.decode(json));
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setFavoriteData(String json) {
        Gson gson = new Gson();
        List<IndexBean.ResultBean> resultBean = gson.fromJson(json, IndexBean
                .class).getResult();
        if (resultBean.size() == 0) {
            worksNothing.setVisibility(View.VISIBLE);
            return;
        }
        initTitle(FavoriteActivity.this, "喜欢(" + resultBean.size() + ")");
        WorksAdapter adapter = new WorksAdapter(FavoriteActivity.this, resultBean);
        adapter.setOnItemClickListener(new WorksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, IndexBean.ResultBean data) {
                Intent intent = new Intent(FavoriteActivity.this, VideoActivity.class);
                intent.putExtra("video", data);
                startActivity(intent);
            }
        });
        rvWorks.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(FavoriteActivity.this, "喜欢");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvWorks.setLayoutManager(gridLayoutManager);
        rvWorks.addItemDecoration(new SpaceItemDecoration(4));
        getFavoriteData();
    }

    private void getFavoriteData() {
        int uidx = CacheUtils.getInt(this, Contact.USERIDX);
        String worksUrl = Contact.HOST + Contact.TAB_FAVORITE + "?uidx=" + uidx + "&loginUidx=" +
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
                message.what = SET_FAVORITE_DATA;
                handler.sendMessage(message);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("FavoriteActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("FavoriteActivity");
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.fragment_works, null);
    }
}
