package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.PersonalActivity;
import com.jhjj9158.niupaivideo.activity.QuickLoignActivity;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.bumptech.glide.Glide;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.UrlEncoderUtils;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class AdapterHomeRecyler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<IndexBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public AdapterHomeRecyler(Context context, List<IndexBean.ResultBean> mDatas) {
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

    public void addDatas(List<IndexBean.ResultBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addRefreshDatas(List<IndexBean.ResultBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearDatas() {
        mDatas.clear();
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
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams)
                    lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final IndexBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String videoPic = new String(Base64.decode(data.getVideoPicUrl().getBytes(),
                    Base64.DEFAULT));
            String headImage = new String(Base64.decode(data.getHeadphoto().getBytes(),
                    Base64.DEFAULT));
            if (!headImage.contains("http")) {
                headImage = "http://" + headImage;
            }
            String createTime = new String(Base64.decode(data.getCreateTime().getBytes(),
                    Base64.DEFAULT));
            String videoSize = new String(Base64.decode(data.getVideoSize().getBytes(),
                    Base64.DEFAULT));
            if (videoSize.length() > 3) {
                videoSize = videoSize.substring(0, 4) + "M";
            }

            String videoDesc = UrlEncoderUtils.decode(new String(Base64.decode(data.getDescriptions()
                    .getBytes(), Base64.DEFAULT)));

            Picasso.with(context).load(videoPic).placeholder(R.drawable.wartfullplacehold).into((
                    (Holder) viewHolder).iv_video);
            if (!headImage.equals("")) {
                Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(
                        ((Holder) viewHolder).iv_head);
            }
            ((Holder) viewHolder).tv_video_ago.setText(createTime);
            ((Holder) viewHolder).tv_video_size.setText(videoSize);
            if (TextUtils.isEmpty(videoDesc)) {
                ((Holder) viewHolder).tv_video_desc.setVisibility(View.GONE);
            }
            ((Holder) viewHolder).tv_video_desc.setText(videoDesc);

            ((Holder) viewHolder).iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CacheUtils.getInt(context, Contact.USERIDX) == 0) {
                        context.startActivity(new Intent(context, QuickLoignActivity.class));
                        return;
                    }

                    Intent intent = new Intent(context, PersonalActivity.class);
                    intent.putExtra("buidx", data.getUidx());
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
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView iv_video;
        ImageView iv_head;
        TextView tv_video_ago;
        TextView tv_video_size;
        TextView tv_video_desc;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            iv_video = (ImageView) itemView.findViewById(R.id.iv_video);
            iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
            tv_video_ago = (TextView) itemView.findViewById(R.id.tv_video_ago);
            tv_video_size = (TextView) itemView.findViewById(R.id.tv_video_size);
            tv_video_desc = (TextView) itemView.findViewById(R.id.tv_video_desc);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, IndexBean.ResultBean data);
    }
}