package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class WorksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    public WorksAdapter(Context context, List<IndexBean.ResultBean> mDatas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_works,
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
            String videoPicUrl = new String(Base64.decode(data.getVideoPicUrl().getBytes(),
                    Base64.DEFAULT));
            Glide.with(context).load(videoPicUrl).placeholder(R.drawable.me_user_admin).into(((Holder) viewHolder).imageView);

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

        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, IndexBean.ResultBean data);
    }
}