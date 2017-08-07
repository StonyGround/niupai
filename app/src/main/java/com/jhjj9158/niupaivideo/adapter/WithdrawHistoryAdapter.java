package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.bean.WithdrawHistoryBean;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class WithdrawHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int NOTIFY = 2;

    long restTime = 0;

    private List<WithdrawHistoryBean.ResultBean> mDatas = new ArrayList<>();

    private View mHeaderView;

    private TextView textView;

    private OnItemClickListener mListener;
    int position;

    private Context context;

    public WithdrawHistoryAdapter(Context context, List<WithdrawHistoryBean.ResultBean> mDatas) {
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

    public void addDatas(List<WithdrawHistoryBean.ResultBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addRefreshDatas(List<WithdrawHistoryBean.ResultBean> datas) {
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_history,
                parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        this.position = position;
        final int pos = getRealPosition(viewHolder);
        final WithdrawHistoryBean.ResultBean data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            ((Holder) viewHolder).history_time.setText("提现时间：" + new String(Base64.decode(data.getApplytime().getBytes(), Base64.DEFAULT)));
            ((Holder) viewHolder).history_alipay.setText("提现账户：" + new String(Base64.decode(data.getAlipay().getBytes(), Base64.DEFAULT))
                    + "（支付宝）");
            int flag=data.getFlg();
            if(flag==0){
                ((Holder) viewHolder).history_status.setText(Html.fromHtml("提现状态：<font color=\"#FFBA4F\">处理中</font>"));
            }else if(flag==1){
                ((Holder) viewHolder).history_status.setText(Html.fromHtml("提现状态：<font color=\"#32BD49\">已完成</font>"));
            }else if(flag==2){
                ((Holder) viewHolder).history_status.setText(Html.fromHtml("提现状态：<font color=\"#FF1235\">提现失败</font>"));
            }

            ((Holder) viewHolder).history_money.setText(new String(Base64.decode(data.getWallet().getBytes(), Base64.DEFAULT))+"元");

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

        TextView history_alipay;
        TextView history_time;
        TextView history_status;
        TextView history_money;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            history_alipay = (TextView) itemView.findViewById(R.id.history_alipay);
            history_time = (TextView) itemView.findViewById(R.id.history_time);
            history_status = (TextView) itemView.findViewById(R.id.history_status);
            history_money = (TextView) itemView.findViewById(R.id.history_money);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, WithdrawHistoryBean.ResultBean data);
    }
}