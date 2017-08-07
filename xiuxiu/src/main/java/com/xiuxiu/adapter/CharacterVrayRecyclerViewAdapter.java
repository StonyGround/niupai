package com.xiuxiu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiuxiu.R;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.ImageRenderView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class CharacterVrayRecyclerViewAdapter extends RecyclerView.Adapter<CharacterVrayRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Bitmap> bitmapList = new ArrayList<>();
    public OnItemClickListener mListener;
    private int selectPosition;

    public CharacterVrayRecyclerViewAdapter(Context context, List<Bitmap> bitmapList) {
        this.mContext = context;
        this.bitmapList = bitmapList;
    }

    public void replaceAll(List<Bitmap> newBitmapList) {
        bitmapList.clear();
        bitmapList.addAll(newBitmapList);
        notifyDataSetChanged();
    }

    public void replace(int position, Bitmap bitmap) {
        bitmapList.set(position, bitmap);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_character_cray = null;
        ImageView iv_characterVray = null;
        RelativeLayout item_select;
        TextView tv_num;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_character_cray = (RelativeLayout) itemView.findViewById(R.id.rl_character_cray);
            iv_characterVray = (ImageView) itemView.findViewById(R.id.iv_characterVray);
            item_select = (RelativeLayout) itemView.findViewById(R.id.item_select);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character_vray, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item_select.setVisibility(View.GONE);
        if (selectPosition == position) {
            holder.item_select.setVisibility(View.VISIBLE);
        }
        holder.tv_num.setText(String.valueOf(position + 1));
        holder.iv_characterVray.setImageBitmap(bitmapList.get(position));
//        Glide.with(mContext).load(new bitma).into(holder.iv_characterVray);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.item_select.setVisibility(View.GONE);
                mListener.onItemClick(v, position);
            }
        });
    }

    public void updateSelect(int position) {
        selectPosition = position;
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