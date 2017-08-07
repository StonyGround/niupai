package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.PersonalActivity;
import com.jhjj9158.niupaivideo.bean.LikeBean;
import com.jhjj9158.niupaivideo.bean.Noticebean;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class LikeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<LikeBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public LikeAdapter(Context context, List<LikeBean.ResultBean> mDatas) {
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

    public void addDatas(List<LikeBean.ResultBean> datas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final LikeBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String name = null;
            try {
                name = URLDecoder.decode(new String(Base64.decode(data.getNickName().getBytes(),
                        Base64.DEFAULT)),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String content;
            String video = new String(Base64.decode(data.getVideoPicUrl().getBytes(),
                    Base64.DEFAULT));
            if (TextUtils.isEmpty(data.getComment())) {
                content = new String(Base64.decode(data.getTContent().getBytes(),
                        Base64.DEFAULT));
            } else {
                content = new String(Base64.decode(data.getComment().getBytes(),
                        Base64.DEFAULT));
            }
            if (!video.contains("http")) {
                video = "http://" + video;
            }
            String date = new String(Base64.decode(data.getCDate().getBytes(),
                    Base64.DEFAULT));

            ((Holder) viewHolder).like_name.setText(name);
            ((Holder) viewHolder).like_content.setText(content);
            ((Holder) viewHolder).like_date.setText(date);
            Glide.with(context).load(video).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).like_video);

            ((Holder) viewHolder).like_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView like_name;
        TextView like_content;
        TextView like_date;
        ImageView like_video;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            like_name = (TextView) itemView.findViewById(R.id.like_name);
            like_content = (TextView) itemView.findViewById(R.id.like_content);
            like_date = (TextView) itemView.findViewById(R.id.like_date);
            like_video = (ImageView) itemView.findViewById(R.id.like_video);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LikeBean.ResultBean data);
    }
}