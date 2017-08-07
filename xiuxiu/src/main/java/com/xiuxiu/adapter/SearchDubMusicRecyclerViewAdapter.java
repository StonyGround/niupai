package com.xiuxiu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.model.DubSearchMusicDataInfo;

import java.util.ArrayList;
import java.util.List;


public class SearchDubMusicRecyclerViewAdapter extends RecyclerView.Adapter<SearchDubMusicRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<DubSearchMusicDataInfo.ResultBean> mDatas = new ArrayList<>();
    public OnItemClickListener mListener;
    int position;

    public void addAllData(List<DubSearchMusicDataInfo.ResultBean> mDatas) {
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mDatas.clear();
    }

    public SearchDubMusicRecyclerViewAdapter(Context context, List<DubSearchMusicDataInfo.ResultBean> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_song_close_music;
        public ImageView iv_song_start_music;
        public TextView tv_song_name;
        public LinearLayout ll_song_start_music;

        public TextView tv_search_song_type;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_song_close_music = (ImageView) itemView.findViewById(R.id.iv_song_close_music);
            iv_song_start_music = (ImageView) itemView.findViewById(R.id.iv_song_start_music);
            tv_song_name = (TextView) itemView.findViewById(R.id.tv_song_name);
            ll_song_start_music = (LinearLayout) itemView.findViewById(R.id.ll_song_start_music);
            tv_search_song_type = (TextView) itemView.findViewById(R.id.tv_search_song_type);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        this.position = position;

        holder.iv_song_close_music.setSelected(mDatas.get(position).isSelected());
        holder.iv_song_start_music.setSelected(mDatas.get(position).isSelected());
        if (holder.iv_song_start_music.isSelected()) {
            holder.iv_song_start_music.setVisibility(View.VISIBLE);
            holder.iv_song_close_music.setImageResource(R.drawable.start_music);
            holder.iv_song_close_music.setSelected(true);
        }else{
            holder.iv_song_start_music.setVisibility(View.GONE);
            holder.iv_song_close_music.setImageResource(R.drawable.close_music);
            holder.iv_song_close_music.setSelected(false);
        }

        String audioUrl = new String(Base64.decode(mDatas.get(position).getAudioUrl().getBytes(), Base64.DEFAULT));
        String Bzaudiourl = new String(Base64.decode(mDatas.get(position).getBzaudiourl().getBytes(), Base64.DEFAULT));
        String Lraudio = new String(Base64.decode(mDatas.get(position).getLraudio().getBytes(), Base64.DEFAULT));
        int mType = mDatas.get(position).getMtype();

        holder.tv_search_song_type.setVisibility(View.VISIBLE);

        holder.tv_song_name.setText(new String(Base64.decode(mDatas.get(position).getTitle().getBytes(), Base64.DEFAULT)));
        if (!TextUtils.isEmpty(audioUrl)) {

//            Glide.with(mContext).load(R.drawable.music_play).asGif().centerCrop().override(40, 46).into(holder.iv_song_start_music);
        }

        if (mType == 1) {
            holder.tv_search_song_type.setText("推荐");
        }else if (mType == 2) {
            holder.tv_search_song_type.setText("嗨起来");
        }else if (mType == 3) {
            holder.tv_search_song_type.setText("快乐");
        }else if (mType == 4) {
            holder.tv_search_song_type.setText("伤感");
        }else if (mType == 5) {
            holder.tv_search_song_type.setText("安静");
        }else {
            holder.tv_search_song_type.setText("其它");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (DubSearchMusicDataInfo.ResultBean resultBean : mDatas) {
                    resultBean.setSelected(false);
                }
                mDatas.get(position).setSelected(true);
                notifyDataSetChanged();
                mListener.onItemClick(v, position, mDatas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, DubSearchMusicDataInfo.ResultBean data);
    }

}