package com.xiuxiu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.util.Util;

import java.util.ArrayList;
import java.util.List;


public class ThemeVrayRecyclerViewAdapter extends RecyclerView.Adapter<ThemeVrayRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Bitmap> bitmapList = new ArrayList<>();
    public OnItemClickListener mListener;

    public ThemeVrayRecyclerViewAdapter(Context context, List<Bitmap> bitmapList) {
        this.mContext = context;
        this.bitmapList = bitmapList;

    }

    public void clearData() {
        this.bitmapList.clear();
    }

    public void replaceAll(List<Bitmap> newBitmapList) {
        this.bitmapList.addAll(newBitmapList);
        notifyDataSetChanged();
    }

    public void replace(int position, Bitmap bitmap) {
        bitmapList.set(position, bitmap);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_theme_cray = null, rl_theme_cray_seek = null;
        ImageView iv_themeVray = null;

        public TextView tv_search_song_type;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_theme_cray_seek = (RelativeLayout) itemView.findViewById(R.id.rl_theme_cray_seek);
            rl_theme_cray = (RelativeLayout) itemView.findViewById(R.id.rl_theme_cray);
            iv_themeVray = (ImageView) itemView.findViewById(R.id.iv_themeVray);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme_vray, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int viewWidth = Util.getScreenWidth(mContext);
        viewWidth = viewWidth / 10;
        int viewHeight = (int) (viewWidth * 4 / 3);// 帧宽高比为4:3
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, viewHeight));
        Log.e("ThemeAdapter", viewWidth + "-----" + viewHeight);

        holder.iv_themeVray.setImageBitmap(bitmapList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}