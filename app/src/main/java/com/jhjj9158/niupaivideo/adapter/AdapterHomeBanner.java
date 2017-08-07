package com.jhjj9158.niupaivideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.WebViewActivity;
import com.jhjj9158.niupaivideo.bean.BannerBean;
import com.jhjj9158.niupaivideo.widget.ResizableImageView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
public class AdapterHomeBanner extends PagerAdapter {

    private Context context;

    private List<BannerBean.ResultBean> bannerList;

    public AdapterHomeBanner(Context context, List<BannerBean.ResultBean> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    public void clearDatas() {
        bannerList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
//        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final int diff = (Integer.MAX_VALUE / 2) % (bannerList.size());
        String imageUrl = new String(Base64.decode(bannerList.get((position - diff) % bannerList.size()).getAdvImg().getBytes(),
                Base64.DEFAULT));
        final String link = new String(Base64.decode(bannerList.get((position - diff) % bannerList.size()).getLinkUrl().getBytes(),
                Base64.DEFAULT));
        final String title = new String(Base64.decode(bannerList.get((position - diff) % bannerList.size()).getTags().getBytes(),
                Base64.DEFAULT));

//        String imageUrl = new String(Base64.decode(bannerList.get(position).getAdvImg().getBytes(),
//                Base64.DEFAULT));
//        final String link = new String(Base64.decode(bannerList.get(position).getLinkUrl().getBytes(),
//                Base64.DEFAULT));
//        final String title = new String(Base64.decode(bannerList.get(position).getTags().getBytes(),
//                Base64.DEFAULT));
        Glide.with(context).load(imageUrl).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("url", link);
                intent.setClass(context, WebViewActivity.class);
                context.startActivity(intent);
            }
        });

        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
