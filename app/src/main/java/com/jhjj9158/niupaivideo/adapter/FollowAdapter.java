package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.PersonalActivity;
import com.jhjj9158.niupaivideo.bean.FollowBean;
import com.jhjj9158.niupaivideo.bean.FollowPostBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

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

/**
 * Created by Administrator on 2016/7/7.
 */
public class FollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<FollowBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    private int position;

    private Context context;

    public FollowAdapter(Context context, List<FollowBean.ResultBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addDatas(List<FollowBean.ResultBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void removeDatas() {
        mDatas.removeAll(mDatas);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final FollowBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String name = new String(Base64.decode(data.getNickName().getBytes(),
                        Base64.DEFAULT));
            final int[] isFollow = {data.getIsFollow()};
            if (isFollow[0] == 0) {
                ((Holder) viewHolder).btnFollow.setText(R.string
                        .tv_personal_follow);
                ((Holder) viewHolder).btnFollow.setBackgroundResource(R
                        .drawable.btn_follow);
                ((Holder) viewHolder).btnFollow.setSelected(true);
            }

            Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).followHeadimg);
            ((Holder) viewHolder).followName.setText(name);
            ((Holder) viewHolder).btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setClickable(false);
                    if (isFollow[0] == 1) {
                        setFollow(2, data.getUidx(), new HandlerCallBack() {
                            @Override
                            public void onFinish(String json) {
                                v.setClickable(true);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    String result = jsonObject.getString("msg");
                                    if (result.equals("关注成功")) {
                                        isFollow[0] = 1;
                                        ((Holder) viewHolder).btnFollow.setText(R.string.followed);
                                        ((Holder) viewHolder).btnFollow.setBackgroundResource(R
                                                .drawable.btn_unfollow);
                                        ((Holder) viewHolder).btnFollow.setSelected(false);
                                    } else {
                                        isFollow[0] = 0;
                                        ((Holder) viewHolder).btnFollow.setText(R.string
                                                .tv_personal_follow);
                                        ((Holder) viewHolder).btnFollow.setBackgroundResource(R
                                                .drawable.btn_follow);
                                        ((Holder) viewHolder).btnFollow.setSelected(true);
                                    }
                                    CommonUtil.showTextToast(context, result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        setFollow(1, data.getUidx(), new HandlerCallBack() {
                            @Override
                            public void onFinish(String json) {
                                v.setClickable(true);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    String result = jsonObject.getString("msg");
                                    if (result.equals("关注成功")) {
                                        isFollow[0] = 1;
                                        ((Holder) viewHolder).btnFollow.setText(R.string.followed);
                                        ((Holder) viewHolder).btnFollow.setBackgroundResource(R
                                                .drawable.btn_unfollow);
                                        ((Holder) viewHolder).btnFollow.setSelected(false);
                                    } else {
                                        isFollow[0] = 0;
                                        ((Holder) viewHolder).btnFollow.setText(R.string
                                                .tv_personal_follow);
                                        ((Holder) viewHolder).btnFollow.setBackgroundResource(R
                                                .drawable.btn_follow);
                                        ((Holder) viewHolder).btnFollow.setSelected(true);
                                    }
                                    CommonUtil.showTextToast(context, result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });

            if (mListener == null) return;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });
        }
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    @OnClick({R.id.btn_follow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_follow:
                break;
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.follow_headimg)
        CircleImageView followHeadimg;
        @Bind(R.id.follow_name)
        TextView followName;
        @Bind(R.id.btn_follow)
        TextView btnFollow;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (itemView == mHeaderView) return;
            followHeadimg = (CircleImageView) itemView.findViewById(R.id.follow_headimg);
            followName = (TextView) itemView.findViewById(R.id.follow_name);
            btnFollow = (TextView) itemView.findViewById(R.id.btn_follow);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, FollowBean.ResultBean data);
    }

    private void setFollow(int index, int friendIdx, HandlerCallBack mCallBack) {
        FollowPostBean followPostBean = new FollowPostBean();
        followPostBean.setOpcode("FocusonOrDeletecurd_friends");
        followPostBean.setUseridx(CacheUtils.getInt(context, Contact.USERIDX));
        followPostBean.setFriendidx(friendIdx);
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
                mainThread(response.body().string());
            }
        });
        this.mCallBack = mCallBack;
    }

    Handler handler = new Handler(Looper.getMainLooper());

    HandlerCallBack mCallBack;

    interface HandlerCallBack {
        void onFinish(String json);
    }

    public void mainThread(final String result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //接口回调
                mCallBack.onFinish(result);
            }
        });
    }
}