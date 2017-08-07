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
import com.jhjj9158.niupaivideo.activity.WorksActivity;
import com.jhjj9158.niupaivideo.adapter.LikeAdapter;
import com.jhjj9158.niupaivideo.adapter.MsgCommentAdapter;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.LikeBean;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.dialog.DialogComment;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.InitiView;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by oneki on 2017/4/27.
 */

public class FragmentMsgComment extends BaseDynamicFragment {

    private static final int SET_COMMENT = 0;

    @Bind(R.id.works_nothing)
    TextView worksNothing;
    @Bind(R.id.rv_works)
    RecyclerView rvWorks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case SET_COMMENT:
                    setCommentData(AESUtil.decode(json));
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setCommentData(String json) {
        Gson gson = new Gson();
        List<MsgCommentBean.ResultBean> resultBean = gson.fromJson(json, MsgCommentBean
                .class).getResult();
        if (resultBean.size() == 0) {
            worksNothing.setVisibility(View.VISIBLE);
            return;
        }
        MsgCommentAdapter adapter = new MsgCommentAdapter(getActivity(), resultBean);
        adapter.setOnItemClickListener(new MsgCommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, final MsgCommentBean.ResultBean data) {
                String name = null;
                try {
                    name = URLDecoder.decode(new String(Base64.decode(data.getNickName().getBytes(),
                            Base64.DEFAULT)), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                DialogComment dialogComment = new DialogComment(getActivity(), name);
                dialogComment.setNoticeDialogListerner(new DialogComment.NoticeDialogListener() {

                    @Override
                    public void onClick(String comment) {
                        sendComment(data.getVid(), data.getUidx(), comment, 1, data.getCid());
                    }
                });
                InitiView.initiBottomDialog(dialogComment);
                InitiView.setDialogMatchParent(dialogComment);
                dialogComment.show();
            }
        });
        rvWorks.setAdapter(adapter);
    }

    private void sendComment(int vid, int buidx, String comment, int identify, int replyCid) {

        int uidx = CacheUtils.getInt(getActivity(), Contact.USERIDX);

        comment = CommonUtil.replaceBlank(comment);
        if (TextUtils.isEmpty(comment)) {
            CommonUtil.showTextToast(getActivity(), "评论内容不能为空");
            return;
        }

        if (buidx == uidx) {
            replyCid = 0;
        }
        String url = null;
        try {
            url = Contact.HOST + Contact.ADD_COMMENT + "?vid=" + vid + "&uidx=" + uidx +
                    "&buidx=" + buidx + "&comment=" + URLEncoder.encode(URLEncoder.encode(comment, "UTF-8"), "UTF-8") +
                    "&identify=" + identify + "&replyCid=" + replyCid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
                int result = 0;
                try {
                    JSONObject object = new JSONObject(String.valueOf(response));
                    result = object.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (result == 1) {
                    CommonUtil.showTextToast(getActivity(), "评论成功");
                } else {
                    CommonUtil.showTextToast(getActivity(), "评论失败");
                }
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvWorks.setLayoutManager(linearLayoutManager);

        getCommentData();
        return view;
    }


    private void getCommentData() {
        int uidx = CacheUtils.getInt(getActivity(), Contact.USERIDX);
        String worksUrl = Contact.HOST + Contact.GET_COMMENT + "?uidx=" + uidx + "&cid=0&num=100";
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
                message.what = SET_COMMENT;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentMsgComment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentMsgComment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
