package com.jhjj9158.niupaivideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.VideoActivity;
import com.jhjj9158.niupaivideo.adapter.WorksAdapter;
import com.jhjj9158.niupaivideo.bean.IndexBean;
import com.jhjj9158.niupaivideo.callback.OKHttpCallback;
import com.jhjj9158.niupaivideo.utils.AESUtil;
import com.jhjj9158.niupaivideo.utils.CacheUtils;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.OkHttpClientManager;
import com.jhjj9158.niupaivideo.utils.OkHttpUtils;
import com.jhjj9158.niupaivideo.widget.SpaceItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by pc on 17-4-24.
 */

public class FragmentFavorite extends BaseDynamicFragment {

    @Bind(R.id.rv_works)
    RecyclerView rvWorks;
    @Bind(R.id.works_nothing)
    TextView worksNothing;

    private boolean isLoadMore = false;
    private int index = 1;
    private int buidx;
    private WorksAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        Log.e("FragmentFavorite", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_works, container, false);
        ButterKnife.bind(this, view);
        buidx = CacheUtils.getInt(getActivity(), "buidx");

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvWorks.setLayoutManager(gridLayoutManager);
        rvWorks.addItemDecoration(new SpaceItemDecoration(2));

        rvWorks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    int lastPosition = gridLayoutManager.findLastVisibleItemPosition();
                    int count = index - 1;
                    Log.e("count", count + "---" + lastPosition);
                    if (lastPosition + 1 == count) {
                        isLoadMore = true;
                        getFaovriteData(index);
                    }
                }
            }
        });
        getFaovriteData(index);
        return view;
    }

    private void getFaovriteData(int begin) {
        index = begin + 9;
        String faovriteUrl = Contact.HOST + Contact.TAB_FAVORITE + "?uidx=" + buidx +
                "&loginUidx=" + CacheUtils.getInt(getActivity(),Contact.USERIDX) + "&begin=" + begin + "&num=9";
        OkHttpClientManager.get(faovriteUrl, new OKHttpCallback<IndexBean>() {
            @Override
            public void onResponse(IndexBean response) {
                List<IndexBean.ResultBean> resultBean = response.getResult();
                if (resultBean.size() == 0) {
                    worksNothing.setVisibility(View.VISIBLE);
                    return;
                }

                if (isLoadMore && adapter != null) {
                    isLoadMore = false;
                    adapter.addDatas(resultBean);
                    return;
                }

                adapter = new WorksAdapter(getActivity(), resultBean);
                adapter.setOnItemClickListener(new WorksAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, IndexBean.ResultBean data) {
                        Intent intent = new Intent(getActivity(), VideoActivity.class);
                        intent.putExtra("video", data);
                        startActivity(intent);
                    }
                });
                rvWorks.setAdapter(adapter);
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentFavorite");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentFavorite");
    }
}
