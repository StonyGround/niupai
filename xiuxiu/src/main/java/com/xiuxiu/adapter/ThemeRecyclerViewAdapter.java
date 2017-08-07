package com.xiuxiu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiuxiu.R;
import com.xiuxiu.model.ThemeInfo;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;

import java.io.File;
import java.util.List;


public class ThemeRecyclerViewAdapter extends RecyclerView.Adapter<ThemeRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<ThemeInfo> themeInfos;
    public OnItemClickListener mListener;


    public ThemeRecyclerViewAdapter(Context context, List<ThemeInfo> themeInfos) {
        this.mContext = context;
        this.themeInfos = themeInfos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_theme_cray, iv_theme_download;
        LinearLayout ll_theme;

        public TextView tv_search_song_type;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_theme_cray = (ImageView) itemView.findViewById(R.id.iv_theme_cray);
            iv_theme_download = (ImageView) itemView.findViewById(R.id.iv_theme_download);
            ll_theme = (LinearLayout) itemView.findViewById(R.id.ll_theme);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        themeInfos.get(position).setDownload(false);

        String name = themeInfos.get(position).getThemeName();
        String filePath = XConstant.RES_THEME_ZIP_FILE_PATH + name + ".zip";
        final File themeFile = new File(filePath);
        if (themeFile.exists()) {
//            themeInfos.get(position).setDownload(true);
            holder.iv_theme_download.setVisibility(View.GONE);
        }

//        if(themeInfos.get(position).isDownload()){
//            holder.iv_theme_download.setVisibility(View.GONE);
//        }
                Glide.with(mContext)
                .load(themeInfos.get(position).getThemeUrl())
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_theme_cray);

        int viewWidth = Util.getScreenWidth(mContext);
        viewWidth = viewWidth / 6;
        int viewHeight = (int) (viewWidth * 4 / 4);// 帧宽高比为4:4
        holder.ll_theme.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, viewHeight));
//        int height = holder.ll_theme.getResources().getDisplayMetrics().heightPixels;
//        int width = height/14;

        Log.e("ThemeAdapter", viewWidth + "-----" + viewHeight);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeFile.exists()) {
                    holder.iv_theme_download.setVisibility(View.GONE);
                }
                mListener.onItemClick(holder.iv_theme_download, position, themeInfos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return themeInfos.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, ThemeInfo data);
    }

}