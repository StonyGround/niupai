package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.FollowBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SearchUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<FollowBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public SearchUserAdapter(Context context, List<FollowBean.ResultBean> mDatas) {
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

    public void addRefreshDatas(List<FollowBean.ResultBean> datas) {
        mDatas.clear();
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
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

            if (!headImage.equals("")) {
                Picasso.with(context).load(headImage).placeholder(R.drawable.me_user_admin).into(
                        ((Holder) viewHolder).search_headimg);
            }

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
        if(mDatas.size()>5){
            return 5;
        }else{
            return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        CircleImageView search_headimg;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            search_headimg = (CircleImageView) itemView.findViewById(R.id.search_headimg);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, FollowBean.ResultBean data);
    }
}