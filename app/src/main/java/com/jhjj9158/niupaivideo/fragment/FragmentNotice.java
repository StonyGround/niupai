package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.activity.WebViewActivity;
import com.jhjj9158.niupaivideo.adapter.MsgCommentAdapter;
import com.jhjj9158.niupaivideo.adapter.NoticeAdapter;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.bean.Noticebean;
import com.jhjj9158.niupaivideo.bean.TabTitleBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
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

public class FragmentNotice extends BaseDynamicFragment {

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    String result = AESUtil.decode(json);
                    Gson gson = new Gson();
                    List<Noticebean.ResultBean> resultBean = gson.fromJson(result, Noticebean
                            .class).getResult();
                    if (resultBean.size() == 0) {
                        worksNothing.setVisibility(View.VISIBLE);
                        return;
                    }
                    NoticeAdapter adapter = new NoticeAdapter(getActivity(), resultBean);
                    adapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, Noticebean.ResultBean data) {
                            if (!CacheUtils.getBoolean(getActivity(), Contact.NOTICE_ONCLICK)) {
                                CacheUtils.setBoolean(getActivity(), Contact.NOTICE_ONCLICK, true);
                                Intent intent = new Intent();
                                if (TextUtils.isEmpty(data.getUrl())) {
                                    intent.putExtra("vid", data.getVid());
                                    intent.setClass(getActivity(), VideoActivity.class);
                                } else {
                                    intent.putExtra("url", data.getUrl());
                                    intent.setClass(getActivity(), WebViewActivity.class);
                                }
                                startActivity(intent);
                            }
                        }
                    });
                    rvWorks.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvWorks.setLayoutManager(linearLayoutManager);

        int uidx = CacheUtils.getInt(getActivity(), Contact.USERIDX);
        String worksUrl = Contact.HOST + Contact.GET_NOTICE + "?uidx=" + uidx + "&begin=0&num=100";

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
                message.what = 1;
                handler.sendMessage(message);
            }
        });

        //更新为已读
        updateIsRead();
        return view;
    }

    private void updateIsRead() {
        String url = Contact.HOST + Contact.REWARD_IS_READ;
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentNotice");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentNotice");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
