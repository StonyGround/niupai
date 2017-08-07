package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.MsgCommentBean;
import com.jhjj9158.niupaivideo.bean.Noticebean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<Noticebean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public NoticeAdapter(Context context, List<Noticebean.ResultBean> mDatas) {
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

    public void addDatas(List<Noticebean.ResultBean> datas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final Noticebean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            String comment = data.getInformcontent();
            int isRead = data.getIsread();
            int videoId = data.getVid();
            int type = data.getType();
            String url = data.getUrl();
            String date = data.getTime();

            ((Holder) viewHolder).notice_title.setText(comment);
            ((Holder) viewHolder).notice_date.setText(date);
            if (type == 2) {
                ((Holder) viewHolder).notice_line.setVisibility(View.GONE);
                ((Holder) viewHolder).notice_go.setVisibility(View.GONE);
            }
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
//        return 5;
    }

    class Holder extends RecyclerView.ViewHolder {
        CircleImageView notice_headimg;
        TextView notice_date;
        TextView notice_title;
        TextView notice_go;
        View notice_line;
        RelativeLayout rl_notice_cotent;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            notice_headimg = (CircleImageView) itemView.findViewById(R.id.notice_headimg);
            notice_date = (TextView) itemView.findViewById(R.id.notice_date);
            notice_title = (TextView) itemView.findViewById(R.id.notice_title);
            notice_go = (TextView) itemView.findViewById(R.id.notice_go);
            notice_line = itemView.findViewById(R.id.notice_line);
            rl_notice_cotent = (RelativeLayout) itemView.findViewById(R.id.rl_notice_cotent);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Noticebean.ResultBean data);
    }
}