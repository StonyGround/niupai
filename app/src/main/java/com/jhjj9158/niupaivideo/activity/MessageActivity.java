package com.jhjj9158.niupaivideo.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.fragment.BaseDynamicFragment;
import com.jhjj9158.niupaivideo.fragment.FragmentLike;
import com.jhjj9158.niupaivideo.fragment.FragmentMoments;
import com.jhjj9158.niupaivideo.fragment.FragmentMsgComment;
import com.jhjj9158.niupaivideo.fragment.FragmentNotice;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageActivity extends BaseActivity {

    @Bind(R.id.msg_tab)
    TabLayout msgTab;
    @Bind(R.id.msg_viewpager)
    HorizontalScrollViewPager msgViewpager;
    private List<BaseDynamicFragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hintTitle();
        initTitle(this,"消息");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(50, 00, 00, 00));
        }

        titles.add("通知");
        titles.add("评论");
        titles.add("点赞");
        titles.add("动态");
        fragmentList.add(new FragmentNotice());
        fragmentList.add(new FragmentMsgComment());
        fragmentList.add(new FragmentLike());
        fragmentList.add(new FragmentMoments());

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter
                (getSupportFragmentManager(), fragmentList, titles);
        msgViewpager.setAdapter(tabFragmentAdapter);
        msgTab.setTabMode(TabLayout.MODE_FIXED);
        msgTab.setupWithViewPager(msgViewpager);
    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_message, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("MessageActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("MessageActivity");
    }
}
