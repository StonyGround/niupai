package com.xiuxiu.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiuxiu.R;
import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.adapter.MusicRecyclerViewAdapter;
import com.xiuxiu.callback.OKHttpCallback;
import com.xiuxiu.model.DubMusicDataInfo;
import com.xiuxiu.service.MusicPlayerService;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.DownloadUtils;
import com.xiuxiu.util.OkHttpClientManager;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.widget.EndlessRecyclerOnScrollListener;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class DubMusicRecommendFragment extends PagerFragment {

    private static final int NUM = 20;

    private ImageView tvDubMusicRecommendNodata;
    private RecyclerView rvDubMusicRecommend;
    //    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    private MusicRecyclerViewAdapter mRecyclerViewAdapter;
    private int mType;
    private int tags;
    private boolean isLoadMore = false;
    private RecordActivity recordActivity;

    private int preMusic = -1;

    private static final String TAG = DubMusicRecommendFragment.class.getSimpleName();

    private static List<DubMusicDataInfo.ResultBean> resultBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dub_music_recommend, container, false);
        rvDubMusicRecommend = (RecyclerView) view.findViewById(R.id.rv_dub_music_recommend);
        tvDubMusicRecommendNodata = (ImageView) view.findViewById(R.id.tv_dub_music_recommend_nodata);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_dub_music_swiperefreshlayout);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvDubMusicRecommend.setLayoutManager(linearLayoutManager);
        rvDubMusicRecommend.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                isLoadMore = true;
                int page = currentPage * NUM + 1;
                getDubMusicData(mType, tags, page);
                Log.e(TAG, "page = " + page + "-------");
            }
        });

//        mSwipeRefreshLayout.setColorSchemeColors(Color.RED);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mRecyclerViewAdapter.clearData();
//                mRecyclerViewAdapter.notifyDataSetChanged();
//                getDubMusicData(mType, tags, 1);
//            }
//        });

        getActivity().startService(new Intent(getActivity(), MusicPlayerService.class));

        return view;
    }

    private void getDubMusicData(int mType, int tags, int begin) {
        String url = XConstant.HOST + XConstant.GET_VOICE_INFO_LIST + "?mType=" + mType + "&tags=" + tags + "&begin=" + begin + "&num=" +
                NUM;
        Log.e(TAG, "NUM = " + NUM + "-------");
        OkHttpClientManager.get(url, new OKHttpCallback<DubMusicDataInfo>() {
            @Override
            public void onResponse(DubMusicDataInfo response) {
                resultBean = response.getResult();
                if (resultBean.size() == 0 & !isLoadMore) {
                    tvDubMusicRecommendNodata.setVisibility(View.VISIBLE);
                    int count = resultBean.size();
                    Log.e(TAG, "count = " + count + "-------");
                    return;

                }

                if (resultBean.size() != 20) {
                    ToolUtils.showToast(getActivity(), "已加载全部");
                }

                if (isLoadMore & mRecyclerViewAdapter != null) {
                    mRecyclerViewAdapter.addAllData(resultBean);
                    return;
                }

                try {
                    if (resultBean.size() != 0) {
//                        if (mSwipeRefreshLayout != null) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
                        mRecyclerViewAdapter = new MusicRecyclerViewAdapter(getActivity(), resultBean);
                        rvDubMusicRecommend.setAdapter(mRecyclerViewAdapter);
                        mRecyclerViewAdapter.setOnItemClickListener(new MusicRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position, DubMusicDataInfo.ResultBean data, final List<DubMusicDataInfo.ResultBean> mData) {
                                final String title = new String(Base64.decode(data.getTitle().getBytes(), Base64.DEFAULT));
                                final String songFilePath = XConstant.RES_MUSIC_FILE_PATH + title + ".mp3";
                                File songFile = new File(songFilePath);
                                if (songFile.exists()) {
                                    playMusic(title,songFilePath,position,mData);

                                } else {

                                    DownloadUtils.getsInstance().setListener(new DownloadUtils.OnDownloadListener() {
                                        @Override
                                        public void onDowload(String mp3Url) { //下载成功
                                            playMusic(title,songFilePath,position,mData);
                                        }

                                        @Override
                                        public void onFailed(String error) { //下载失败

                                        }
                                    }).download(data);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    private void playMusic(String title,String songFilePath, int position,List<DubMusicDataInfo.ResultBean> mData) {
        Intent playerStart = new Intent(getActivity(), MusicPlayerService.class);
        playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
        playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
        CacheUtils.setString(getActivity(), XConstant.CURRENT_SELECT_MUSIC, title);
//                            playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_LISTEN);
        getActivity().startService(playerStart);

        for (DubMusicDataInfo.ResultBean bean : mData) {
            bean.setSelected(false);
        }
        mData.get(position).setSelected(true);
        mRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void reStr(String parid, String subid) {
        Log.d("subid", parid + "--" + subid);
        mType = Integer.parseInt(new String(Base64.decode(parid.getBytes(), Base64.DEFAULT)));
        tags = Integer.parseInt(new String(Base64.decode(subid.getBytes(), Base64.DEFAULT)));
        Log.d("mType", mType + "--" + tags);
        getDubMusicData(mType, tags, 1);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
