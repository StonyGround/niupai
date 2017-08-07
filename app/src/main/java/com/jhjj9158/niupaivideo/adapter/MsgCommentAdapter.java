package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.PersonalActivity;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.dialog.DialogComment;
import com.bumptech.glide.Glide;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.InitiView;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MsgCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<MsgCommentBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public MsgCommentAdapter(Context context, List<MsgCommentBean.ResultBean> mDatas) {
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

    public void addDatas(List<MsgCommentBean.ResultBean> datas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_comment,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final MsgCommentBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String name = null;
            String comment = null;
            try {
                name = URLDecoder.decode(new String(Base64.decode(data.getNickName().getBytes(),
                        Base64.DEFAULT)), "utf-8");
                comment = URLDecoder.decode(new String(Base64.decode(data.getComment().getBytes(),
                        Base64.DEFAULT)), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String reply = new String(Base64.decode(data.getReplycomment().getBytes(),
                    Base64.DEFAULT));
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            String date = new String(Base64.decode(data.getCDate().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String videoPic = new String(Base64.decode(data.getVideoPicUrl().getBytes(),
                    Base64.DEFAULT));
            if (!videoPic.contains("http")) {
                videoPic = "http://" + headImage;
            }

            if (TextUtils.isEmpty(reply)) {
                ((Holder) viewHolder).msgCommentReply.setVisibility(View.GONE);
            }
            Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(((MsgCommentAdapter.Holder) viewHolder)
                    .msgCommentHeadimg);
            Glide.with(context).load(videoPic).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).msgCommentVideo);
            ((Holder) viewHolder).msgCommentName.setText(name);
            ((Holder) viewHolder).msgCommentDetail.setText("回复你：" + comment);
            ((Holder) viewHolder).msgCommentReply.setText("我的评论：" + reply);
            ((Holder) viewHolder).msgCommentDate.setText(date);

            ((Holder) viewHolder).msgCommentHeadimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getUidx());
                    context.startActivity(intent);
                }
            });
            ((Holder) viewHolder).msgCommentName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getUidx());
                    context.startActivity(intent);
                }
            });
            ((Holder) viewHolder).msgCommentVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("vid", data.getVid());
                    context.startActivity(intent);
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

    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {
        CircleImageView msgCommentHeadimg;
        TextView msgCommentName;
        TextView msgCommentReply;
        ImageView msgCommentVideo;
        TextView msgCommentDetail;
        TextView msgCommentDate;
        LinearLayout msg_ll_comment;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            msgCommentHeadimg = (CircleImageView) itemView.findViewById(R.id.msg_comment_headimg);
            msgCommentName = (TextView) itemView.findViewById(R.id.msg_comment_name);
            msgCommentReply = (TextView) itemView.findViewById(R.id.msg_comment_reply);
            msgCommentVideo = (ImageView) itemView.findViewById(R.id.msg_comment_video);
            msgCommentDetail = (TextView) itemView.findViewById(R.id.msg_comment_detail);
            msgCommentDate = (TextView) itemView.findViewById(R.id.msg_comment_date);
            msg_ll_comment = (LinearLayout) itemView.findViewById(R.id.msg_ll_comment);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, MsgCommentBean.ResultBean data);
    }

}