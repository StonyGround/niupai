package com.xiuxiu.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xiuxiu.R;
import com.xiuxiu.model.AccSongInfo;
import com.xiuxiu.model.DubMusicDataInfo;

import java.util.ArrayList;
import java.util.List;


public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<DubMusicDataInfo.ResultBean> mDatas = new ArrayList<>();
    public OnItemClickListener mListener;
    //    int position;
    private boolean isListenClicked = false;

    public void addAllData(List<DubMusicDataInfo.ResultBean> mDatas) {
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mDatas.clear();
    }

    public MusicRecyclerViewAdapter(Context context, List<DubMusicDataInfo.ResultBean> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_song_close_music;
        ImageView iv_song_start_music;
        TextView tv_song_name;
        LinearLayout ll_song_start_music;

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

    private DubMusicDataInfo.ResultBean resultBean;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        this.position = position;
        holder.iv_song_close_music.setSelected(mDatas.get(position).isSelected());
        holder.iv_song_start_music.setSelected(mDatas.get(position).isSelected());
        if (holder.iv_song_start_music.isSelected()) {
            holder.iv_song_start_music.setVisibility(View.VISIBLE);
            holder.iv_song_close_music.setSelected(true);
            Log.e("iv_song_close_music", "iv_song_close_music = " + "---true----");
        } else {
            holder.iv_song_start_music.setVisibility(View.GONE);
            holder.iv_song_close_music.setSelected(false);
            Log.e("iv_song_close_music", "iv_song_close_music = " + "---false----");
        }

        String audioUrl = new String(Base64.decode(mDatas.get(position).getAudioUrl().getBytes(), Base64.DEFAULT));
        String Bzaudiourl = new String(Base64.decode(mDatas.get(position).getBzaudiourl().getBytes(), Base64.DEFAULT));
        String Lraudio = new String(Base64.decode(mDatas.get(position).getLraudio().getBytes(), Base64.DEFAULT));
//        Log.e("AudioUrl", audioUrl);
//        Log.e("Bzaudiourl", Bzaudiourl);
//        Log.e("Lraudio", Lraudio);
        holder.tv_song_name.setText(new String(Base64.decode(mDatas.get(position).getTitle().getBytes(), Base64.DEFAULT)));
        holder.tv_search_song_type.setVisibility(View.GONE);
//        if (!TextUtils.isEmpty(audioUrl)) {
//            holder.iv_song_close_music.setImageResource(R.drawable.close_music);
//        }
//        if (!TextUtils.isEmpty(audioUrl)) {
//            holder.iv_song_close_music.setImageResource(R.drawable.close_music);
//            Glide.with(mContext).load(R.drawable.music_play).asGif().centerCrop().override(40, 46).into(holder.iv_song_start_music);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateMusicPlayTip(position);

//                for (DubMusicDataInfo.ResultBean resultBean : mDatas) {
//                    resultBean.setSelected(false);
//                }
//                mDatas.get(position).setSelected(true);
//                notifyDataSetChanged();


                mListener.onItemClick(v, position, mDatas.get(position), mDatas);
            }
        });
    }

    // 更新音乐播放提示
    public boolean updateMusicPlayTip(int index) {

        return false;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, DubMusicDataInfo.ResultBean data, List<DubMusicDataInfo.ResultBean> mData);
    }

}