package com.jhjj9158.niupaivideo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jhjj9158.niupaivideo.MyApplication;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.DensityUtil;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.ToolUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;

public class GuideActivity extends BaseActivity {

    public static final String TAG = GuideActivity.class.getSimpleName();

    private ArrayList<ImageView> imageViews;

    private ViewPager viewpager;
    private TextView tv_start_main;
    private LinearLayout ll_point_group;
    private ImageView iv_red_point;

    /**
     * 两点间的间距
     */
    private int maxLeft;
    /**
     * 点在屏幕上的坐标
     */
    private float margLeft;

    private int dipsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
        createShortCut();
        statisticsApp();
        CacheUtils.setBoolean(this, Contact.IS_FROM_GUIDE, true);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tv_start_main = (TextView) findViewById(R.id.tv_start_main);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
        dipsize = DensityUtil.dip2px(this, 10);

        int ids[] = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4};
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < ids.length; i++) {
            //根据资源id创建对应的ImageView
            ImageView imageView = new ImageView(this);
//            imageView.setBackgroundResource(ids[i]);
            imageView.setImageResource(ids[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            imageViews.add(imageView);

            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dipsize, dipsize);//
            if (i != 0) {
                params.leftMargin = dipsize;
            }
            point.setLayoutParams(params);
            ll_point_group.addView(point);

        }


        //设置ViewPager的适配器
        viewpager.setAdapter(new MyPagerAdapter());

        //View从实例化到显示过程中的主要方法
        //两点的间距 = 第1个点距离左边的距离 - 第0个点距离左边的距离
        //onMeasure();-->onLayout()-->onDraw();
        //监听
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener
                ());

        //页面滑动了总页面宽的百分比
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());


        //设置监听按钮点击进入主页面
        tv_start_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CacheUtils.setBoolean(GuideActivity.this, Contact.IS_START_MAIN, true);

                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);

                //关于引导页面
                finish();

            }
        });

    }

    private void statisticsApp() {
        String url = Contact.HOST + Contact.STATISTICS_APP + Contact.getDeviceDetail(this, 1);
        Log.e("DeviceDetail", String.valueOf(url));
        OkHttpClientManager.get(url, new OKHttpCallback() {
            @Override
            public void onResponse(Object response) {
                Log.e("DeviceDetail", String.valueOf(response));
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_guide, null);
    }


    private void createShortCut() {

        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));

        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R
                .mipmap.ic_launcher);

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent intent = new Intent(this, MainActivity.class);

        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutIntent);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        /**
         * 当页面回调了的时候回调这个方法
         * position：滑动的页面的下标位置
         * positionOffset：这个页面滑动的百分比
         * positionOffsetPixels：滑动了当前页面的多少个像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {


            //两点间滑动的距离 = 两点的间距*页面滑动了总页面宽的百分比
//			 margLeft = maxLeft *positionOffset;

            //两点间滑动在屏幕上的坐标 = 起始坐标 + 两点间滑动的距离后对应的坐标
//			 margLeft = maxLeft*position +maxLeft *positionOffset;
            margLeft = maxLeft * (position + positionOffset);
//			 Log.e(TAG, "position=="+position+",positionOffset=="+positionOffset+",
// positionOffsetPixels=="+positionOffsetPixels+",margLeft=="+margLeft);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dipsize, dipsize);
            params.leftMargin = (int) margLeft;//这种方式可以
//			 params.setMargins((int) margLeft, 0, 0, 0);//可以
            iv_red_point.setLayoutParams(params);

            //下面方式效果不行，但是最终也能实现类似效果
//			 if(positionOffsetPixels >0){
//				 iv_red_point.layout((int) margLeft, iv_red_point.getTop(), (int)
// (margLeft+iv_red_point.getWidth()), iv_red_point.getBottom());
//			 }

            Log.e(TAG, "margLeft==" + margLeft + ",t==" + iv_red_point.getTop() + ",r==" + (int)
                    (margLeft + iv_red_point.getWidth()) + ",b==" + iv_red_point.getBottom());
//
        }

        @Override
        public void onPageSelected(int position) {
            if (position == (imageViews.size() - 1)) {//最后一个页面，2
                tv_start_main.setVisibility(View.VISIBLE);
            } else {
                tv_start_main.setVisibility(View.GONE);
            }

        }

        /**
         * 当页面的状态发送变化的时候回调这个方法
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }

    private class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            iv_red_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            // TODO Auto-generated method stub
            //两点的间距 = 第1个点距离左边的距离 - 第0个点距离左边的距离
            maxLeft = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0)
                    .getLeft();
            Log.i(TAG, "maxLeft==" + maxLeft);
        }

    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 相当于getView();
         * container：其实就是ViewPager
         * position:要实例化对应页面的位置
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            ImageView imageView = imageViews.get(position);//更加位置得到对应的数据
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);//把对应的页面添加到容器中(ViewPager)
            //返回能够代表和当前这个控件有关系的对象就行
            //return position;//第一种方案
            return imageView;
        }

        /**
         * view：当前view
         * object:是有instantiateItem()方法返回的对象--position
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
//			return view==imageViews.get(Integer.parseInt((String) object));//第一种方案
            return view == object;
        }


        /**
         * container:ViewPager
         * position:要移除页面的位置
         * object:要移除页面的对象
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("GuideActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("GuideActivity");
    }
}
