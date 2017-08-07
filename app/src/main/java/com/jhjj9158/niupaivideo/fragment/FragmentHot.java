package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.adapter.AdapterHomeBanner;
import com.jhjj9158.niupaivideo.adapter.AdapterHomeRecyler;
import com.jhjj9158.niupaivideo.bean.BannerBean;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.NetworkUtils;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.ToolUtils;
import com.jhjj9158.niupaivideo.widget.AdaptiveHeightlViewPager;
import com.jhjj9158.niupaivideo.widget.GridSpacingItemDecoration;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.jhjj9158.niupaivideo.widget.StaggeredScrollEnable;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.view.View.inflate;

/**
 * 热门
 * Created by pc on 17-4-17.
 */

public class FragmentHot extends BaseDynamicFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    AdaptiveHeightlViewPager viewpager_banner;
    LinearLayout ll_point_group;

    private int currentItem = Integer.MAX_VALUE / 2;

    private List<BannerBean.ResultBean> bannerList = new ArrayList<>();
    private int minVid = 0;
    private int preSelectPositon = 0;
    private StaggeredScrollEnable layoutManager;
    private AdapterHomeRecyler adapterHomeRecyler;
    private boolean isRefresh = false;
    private View topView;
    private int index;
    private boolean isLoadMore = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Contact.GET_BANNER_DATA:
                    String jsonBanner = msg.obj.toString();
                    setBannerData(AESUtil.decode(jsonBanner));
                    break;
                case Contact.BANNER_START_ROLLING:
                    currentItem++;
                    viewpager_banner.setCurrentItem(currentItem);
                    if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                        handler.removeMessages(Contact.BANNER_START_ROLLING);
                    }
                    handler.sendEmptyMessageDelayed(Contact.BANNER_START_ROLLING, Contact.BANNER_STAY_TIME);
                    break;
                case Contact.BANNER_CHANGE_ROLLING:
                    currentItem = msg.arg1;
                    handler.sendEmptyMessageDelayed(Contact.BANNER_START_ROLLING, Contact.BANNER_STAY_TIME);
                    break;
                case Contact.NET_ERROR:
                    CommonUtil.showTextToast(getActivity(), "网络请求超时");
                    swipeRefresh.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onRefresh() {
        minVid = 0;
        isRefresh = true;
        getHotData(0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        Log.e("FragmentHot", "onCreateView");
        View view = inflater.inflate(R.layout.tab_hot, container, false);
        ButterKnife.bind(this, view);

        layoutManager = new StaggeredScrollEnable(2, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setAutoMeasureEnabled(true);
        recyclerview.setLayoutManager(layoutManager);

//        recyclerview.addItemDecoration(new GridSpacingItemDecoration(2, 5, true));
//        recyclerview.addItemDecoration(new SpaceItemDecoration(5));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        topView = LayoutInflater.from(getActivity()).inflate(R.layout.home_top, recyclerview,
                false);

        //上拉加载
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    int[] lastPositions = new int[layoutManager.getSpanCount()];
                    layoutManager.findLastVisibleItemPositions(lastPositions);

                    Integer[] obj = new Integer[lastPositions.length];
                    for (int i = 0; i < lastPositions.length; i++) {
                        obj[i] = lastPositions[i];
                    }
                    int lastPosition = Collections.max(Arrays.asList(obj));

                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        layoutManager.setScrollEnabled(false);
                        isLoadMore = true;
                        getHotData(index);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        swipeRefresh.setColorSchemeResources(R.color.button_login_click);
        swipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources()
                        .getDisplayMetrics()));
        swipeRefresh.setOnRefreshListener(this);
        getHotData(0);
        return view;
    }

    private void getHotData(int begin) {
        index = begin + Contact.HOME_PAGE_NUM;
        String url = Contact.HOST + Contact.INDEX + "?type=1&uidx=1&begin=" + begin + "&num=" + Contact.HOME_PAGE_NUM + "&vid=" + minVid;
        OkHttpClientManager.get(url, new OKHttpCallback<IndexBean>() {
            @Override
            public void onResponse(IndexBean response) {
                final List<IndexBean.ResultBean> resultBeanList = response.getResult();

                if (resultBeanList.size() == 0) {
                    swipeRefresh.setRefreshing(false);
                    CommonUtil.showTextToast(getActivity(), "已加载全部数据~");
                    return;
                }

                minVid = CommonUtil.getMinVid(resultBeanList);

                if (isRefresh && adapterHomeRecyler != null) {
                    isRefresh = false;
                    adapterHomeRecyler.addRefreshDatas(resultBeanList);
                    swipeRefresh.setRefreshing(false);
                    layoutManager.setScrollEnabled(true);
                    return;
                }

                if (isLoadMore && adapterHomeRecyler != null) {
                    isLoadMore = false;
                    adapterHomeRecyler.addDatas(resultBeanList);
                    swipeRefresh.setRefreshing(false);
                    layoutManager.setScrollEnabled(true);
                    return;
                }

                adapterHomeRecyler = new AdapterHomeRecyler(getActivity(),
                        resultBeanList);
                adapterHomeRecyler.setOnItemClickListener(new AdapterHomeRecyler.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, IndexBean.ResultBean data) {
                        Intent intent = new Intent(getActivity(), VideoActivity.class);
                        intent.putExtra("video", data);
                        startActivity(intent);
                    }
                });
                //获取banner数据
                getBannerData();
                swipeRefresh.setRefreshing(false);
                layoutManager.setScrollEnabled(true);
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    private void getBannerData() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Contact.HOST + Contact
                .GET_BANNER + "?stype=1");
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = Contact.NET_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = Contact.GET_BANNER_DATA;
                handler.sendMessage(message);
            }
        });
    }

    private void setBannerData(String json) {
        Gson gson = new Gson();
        bannerList = gson.fromJson(json, BannerBean.class).getResult();


        viewpager_banner = (AdaptiveHeightlViewPager) topView.findViewById(R.id.viewpager_banner);
        ll_point_group = (LinearLayout) topView.findViewById(R.id.ll_point_group);
        viewpager_banner.setAdapter(new AdapterHomeBanner(getActivity(), bannerList));
        viewpager_banner.setCurrentItem(currentItem);

        viewpager_banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        if (bannerList.size() > 1) {
            ll_point_group.removeAllViews();
            for (int i = 0; i < bannerList.size(); i++) {
                ImageView point = new ImageView(getActivity());
                point.setBackgroundResource(R.drawable.point_selector);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        20, 20);
                point.setLayoutParams(params);
                if (i == 0) {
                    point.setEnabled(true);
                } else {
                    point.setEnabled(false);
                    params.leftMargin = 20;
                }

                // 把点添加到LinearLayout中
                ll_point_group.addView(point);
            }

            if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                handler.removeMessages(Contact.BANNER_START_ROLLING);
            }
            handler.sendEmptyMessageDelayed(Contact.BANNER_START_ROLLING, Contact.BANNER_STAY_TIME);


            viewpager_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int
                        positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                        handler.removeMessages(Contact.BANNER_START_ROLLING);
                    }
                    Message message = new Message();
                    message.arg1 = position;
                    message.what = Contact.BANNER_CHANGE_ROLLING;
                    handler.sendMessage(message);

                    int diff = (Integer.MAX_VALUE / 2) % (bannerList.size());
                    ll_point_group.getChildAt(preSelectPositon).setEnabled(false);
                    ll_point_group.getChildAt((position - diff) % bannerList.size()).setEnabled(true);
                    preSelectPositon = (position - diff) % bannerList.size();

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    switch (state) {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            swipeRefresh.setEnabled(false);
                            if (handler.hasMessages(Contact.BANNER_START_ROLLING)) {
                                handler.removeMessages(Contact.BANNER_START_ROLLING);
                            }
                            break;
                        case ViewPager.SCROLL_STATE_IDLE:
                            swipeRefresh.setEnabled(true);
                            handler.sendEmptyMessageDelayed(Contact.BANNER_START_ROLLING, Contact.BANNER_STAY_TIME);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        adapterHomeRecyler.setHeaderView(topView);
        recyclerview.setAdapter(adapterHomeRecyler);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentHot");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentHot");
    }

    @Override
    public void onDestroyView() {
        Log.e("FragmentHot", "onDestroyView");
        swipeRefresh.removeAllViews();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();

    }
}
