package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.adapter.LikeAdapter;
import com.jhjj9158.niupaivideo.adapter.MsgCommentAdapter;
import com.jhjj9158.niupaivideo.bean.LikeBean;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by oneki on 2017/4/27.
 */

public class FragmentLike extends BaseDynamicFragment {

    private static final int SET_LIKE = 0;

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case SET_LIKE:
                    setLikeData(AESUtil.decode(json));
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setLikeData(String json) {
        Gson gson = new Gson();
        List<LikeBean.ResultBean> resultBean = gson.fromJson(json, LikeBean
                .class).getResult();
        if (resultBean.size() == 0) {
            worksNothing.setVisibility(View.VISIBLE);
            return;
        }
        LikeAdapter adapter = new LikeAdapter(getActivity(), resultBean);
        adapter.setOnItemClickListener(new LikeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, LikeBean.ResultBean data) {
                Intent intent=new Intent(getActivity(),VideoActivity.class);
                intent.putExtra("vid",data.getVid());
                startActivity(intent);
            }
        });
        rvWorks.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvWorks.setLayoutManager(linearLayoutManager);
        getLikeData();
        return view;
    }


    private void getLikeData() {
        int uidx = CacheUtils.getInt(getActivity(), Contact.USERIDX);
        String worksUrl = Contact.HOST + Contact.GET_LIKE + "?uidx=" + uidx + "&begin=1&num=100";
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
                message.what = SET_LIKE;
                handler.sendMessage(message);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentLike");
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentLike");
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
