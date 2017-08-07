package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.SearchActivity;
import com.jhjj9158.niupaivideo.adapter.TabFragmentAdapter;
import com.jhjj9158.niupaivideo.bean.TabTitleBean;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.widget.HorizontalScrollViewPager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 首页
 * Created by pc on 17-4-1.
 */

public class FragmentHome extends Fragment {

    @Bind(R.id.viewpager)
    HorizontalScrollViewPager viewpager;
    @Bind(R.id.tablLayout)
    TabLayout tabLayout;
    @Bind(R.id.home_search)
    ImageView homeSearch;

    private List<BaseDynamicFragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<TabTitleBean.ResultBean> resultBeanList = new ArrayList<>();

    private FragmentTransaction transaction;
    private TabFragmentAdapter tabFragmentAdapter;
    private View rootView;

    private String type;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:

                    try {
                        Gson gson = new Gson();
                        resultBeanList = gson.fromJson(AESUtil.decode(json), TabTitleBean.class)
                                .getResult();
                        for (int i = 0; i < resultBeanList.size(); i++) {

//                        transaction = getFragmentManager().beginTransaction();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("type", new String(Base64.decode(resultBeanList.get(i)
//                                .getVrid().getBytes(), Base64.DEFAULT)));
//                        fragmentDynamic.setArguments(bundle);
                            FragmentDynamic fragmentDynamic = new FragmentDynamic();
//                        transaction.commit();

                            tabFragmentAdapter.addData(fragmentDynamic, new String(Base64.decode(resultBeanList.get(i).getVrname()
                                    .getBytes(), Base64.DEFAULT)));

//                        titles.add(new String(Base64.decode(resultBeanList.get(i).getVrname()
//                                .getBytes(), Base64.DEFAULT)));
//                        fragmentList.add(fragmentDynamic);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles.add("热门");
        titles.add("最新");
        titles.add("关注");
        fragmentList.add(new FragmentHot());
        fragmentList.add(new FragmentNew());
        fragmentList.add(new FragmentFollow());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ButterKnife.bind(this, rootView);

            tabFragmentAdapter = new TabFragmentAdapter(getFragmentManager(), fragmentList, titles);
            viewpager.setAdapter(tabFragmentAdapter);
            viewpager.setEnabled(false);
            tabLayout.setupWithViewPager(viewpager);

            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                    .TAB_TITLE);
            requestBuilder.method("GET", null);
            Request request = requestBuilder.build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message = new Message();
                    message.obj = response.body().string();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });

            viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position >= 3 && position < resultBeanList.size() + 3) {
                        type = new String(Base64.decode(resultBeanList.get(position - 3).getVrid().getBytes(), Base64.DEFAULT));
                        fragmentList.get(position).reStr(type);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentHome");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentHome");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("FragmentHome", "onDestroyView");
    }

    @OnClick(R.id.home_search)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }
}
