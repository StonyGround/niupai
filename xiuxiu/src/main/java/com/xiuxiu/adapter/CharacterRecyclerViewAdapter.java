package com.xiuxiu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.xiuxiu.R;
import com.xiuxiu.model.CharacterPhotoItemInfo;

import java.util.ArrayList;
import java.util.List;


public class CharacterRecyclerViewAdapter extends RecyclerView.Adapter<CharacterRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private int[] images;
    public OnItemClickListener mListener;

    public CharacterRecyclerViewAdapter(Context context, int[] images) {
        this.mContext = context;
        this.images = images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_character_icon = null;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_character_icon = (ImageView) itemView.findViewById(R.id.iv_character_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.iv_character_icon.setImageResource(images[position]);

//        Glide.with(mContext).load(mCharacterPhotoItemInfo.get(position).getId()).centerCrop().override(40, 46).into(holder.iv_character_icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}