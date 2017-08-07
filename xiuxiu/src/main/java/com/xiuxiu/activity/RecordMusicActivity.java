package com.xiuxiu.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiuxiu.R;
import com.xiuxiu.adapter.SearchMusicRecyclerViewAdapter;
import com.xiuxiu.adapter.TabFragmentAdapter;
import com.xiuxiu.callback.OKHttpCallback;
import com.xiuxiu.fragment.DubMusicRecommendFragment;
import com.xiuxiu.fragment.PagerFragment;
import com.xiuxiu.model.DubMusicTypeInfo;
import com.xiuxiu.model.DubSearchMusicDataInfo;
import com.xiuxiu.service.MusicPlayerService;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.DownloadSearchUtils;
import com.xiuxiu.util.OkHttpClientManager;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.widget.HorizontalScrollViewPager;
import com.xiuxiu.widget.SearchClearEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordMusicActivity extends AppCompatActivity {


    private TabLayout tlDubMusicSongTab;
    private SearchClearEditText etDubMusicSongSearch;
    private HorizontalScrollViewPager hvDubMusicSongViewpager;
    private List<PagerFragment> fragmentList = new ArrayList<PagerFragment>();
    private List<String> titles = new ArrayList<>();
    private TextView tvMusicSongClose;

    private RecyclerView rvDubMusicSearch;
    private ImageView ivDubMusicSearchNodata;
    private LinearLayout llDubMusicSearch, llDubMusicSongGone;
    private View vDubMusic;
    private TextView tvMusicTitle;

    private List<DubMusicTypeInfo.ResultBean> resultBean;
    private List<DubSearchMusicDataInfo.ResultBean> searchData;
    private boolean isLoadMore = false;
    private SearchMusicRecyclerViewAdapter searchMusicRecyclerViewAdapter;
    private TextView tv_music_song_affirm;
    private TextView test;

    private int lable = 0;
    private boolean isSearchData = false;

    private int opr;
    private String title;
    private String hinttitle;
    private String selecttitle;

    private static final String TAG = RecordMusicActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dialog_fragment_window);

        opr = getIntent().getIntExtra("opr", 0);
        title = getIntent().getStringExtra("musictitle");
        hinttitle = getIntent().getStringExtra("hinttitle");
        selecttitle = getIntent().getStringExtra("selecttitle");
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        tlDubMusicSongTab = (TabLayout) findViewById(R.id.tl_dub_music_song_tab);
        hvDubMusicSongViewpager = (HorizontalScrollViewPager) findViewById(R.id.hv_dub_music_song_viewpager);
        etDubMusicSongSearch = (SearchClearEditText) findViewById(R.id.et_dub_music_song_search);
        etDubMusicSongSearch.setHint(hinttitle);

        tvMusicSongClose = (TextView) findViewById(R.id.tv_music_song_close);

        tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        tvMusicTitle.setText(title);

        rvDubMusicSearch = (RecyclerView) findViewById(R.id.rv_dub_music_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecordMusicActivity.this);
        rvDubMusicSearch.setLayoutManager(linearLayoutManager);
        ivDubMusicSearchNodata = (ImageView) findViewById(R.id.iv_dub_music_search_nodata);
        llDubMusicSearch = (LinearLayout) findViewById(R.id.ll_dub_music_search);
        vDubMusic = findViewById(R.id.v_dub_music);
        llDubMusicSongGone = (LinearLayout) findViewById(R.id.ll_dub_music_song_gone);
        tv_music_song_affirm = (TextView) findViewById(R.id.tv_music_song_affirm);
        tlDubMusicSongTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        // 设置宽度为屏宽、靠近屏幕底部。
        tvMusicSongClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭弹出窗口
                Intent intent = new Intent(RecordMusicActivity.this, MusicPlayerService.class);
                intent.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_STOP);
                RecordMusicActivity.this.startService(intent);
                CacheUtils.setString(RecordMusicActivity.this, XConstant.CURRENT_SELECT_MUSIC, "");
                finish();

            }
        });
        getDubTypeData();
        initData();

        if (isSearchData) {
            isSearchData = false;

        } else {
            isSearchData = true;

        }

        //返回歌曲名称
        tv_music_song_affirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String song = CacheUtils.getString(RecordMusicActivity.this, XConstant.CURRENT_SELECT_MUSIC);
                if (TextUtils.isEmpty(song)) {
                    ToolUtils.showToast(RecordMusicActivity.this, selecttitle);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("title", song);
                RecordMusicActivity.this.setResult(XConstant.MUSIC_ACTIVITY_RESULT, intent);

                Intent intentMusic = new Intent(RecordMusicActivity.this, MusicPlayerService.class);
                intentMusic.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_STOP);
                RecordMusicActivity.this.startService(intentMusic);
                CacheUtils.setString(RecordMusicActivity.this, XConstant.CURRENT_SELECT_MUSIC, "");

                RecordMusicActivity.this.finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intentMusic = new Intent(RecordMusicActivity.this, MusicPlayerService.class);
            intentMusic.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_STOP);
            RecordMusicActivity.this.startService(intentMusic);
            CacheUtils.setString(RecordMusicActivity.this, XConstant.CURRENT_SELECT_MUSIC, "");
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initData() {
        etDubMusicSongSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                String s = charSequence.toString().trim();
//                if(s != null && !s.equals("")){
//                    etDubMusicSongSearch.setGravity(Gravity.LEFT);
//                }else{
//                    etDubMusicSongSearch.setGravity(Gravity.CENTER);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search = etDubMusicSongSearch.getText().toString().trim();
                if (search.length() > 0) {
                    getSearchData();
//                    rvDubMusicSearch.setVisibility(View.VISIBLE);
//                    llDubMusicSearch.setVisibility(View.VISIBLE);
                } else {
                    llDubMusicSongGone.setVisibility(View.VISIBLE);
                    rvDubMusicSearch.setVisibility(View.GONE);
                    llDubMusicSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    //search
    private void getSearchData() {
        final String title = etDubMusicSongSearch.getText().toString().trim();
        String url = XConstant.HOST + XConstant.SEARCH_VOICE_INFO_LIST + "?title=" + title + "&opr=" + opr;
        OkHttpClientManager.get(url, new OKHttpCallback<DubSearchMusicDataInfo>() {
            @Override
            public void onResponse(DubSearchMusicDataInfo response) {
                searchData = response.getResult();
                if (searchData.size() == 0) {
                    llDubMusicSearch.setVisibility(View.VISIBLE);
                    ivDubMusicSearchNodata.setVisibility(View.VISIBLE);
                    rvDubMusicSearch.setVisibility(View.GONE);
                    llDubMusicSongGone.setVisibility(View.GONE);
                    return;
                }
                if (searchData.size() != 0) {
                    llDubMusicSongGone.setVisibility(View.GONE);
                    rvDubMusicSearch.setVisibility(View.VISIBLE);
                    llDubMusicSearch.setVisibility(View.VISIBLE);
                }
//                for (int i = 0; i < searchData.size(); i++) {
//                    Log.e("searchData", searchData.get(i).getAudioUrl());
//                }

                searchMusicRecyclerViewAdapter = new SearchMusicRecyclerViewAdapter(RecordMusicActivity.this, searchData);

                rvDubMusicSearch.setAdapter(searchMusicRecyclerViewAdapter);
                searchMusicRecyclerViewAdapter.setOnItemClickListener(new SearchMusicRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position, DubSearchMusicDataInfo.ResultBean data) {
                        final String title = new String(Base64.decode(data.getTitle().getBytes(), Base64.DEFAULT));
                        final String songFilePath = XConstant.RES_MUSIC_FILE_PATH + title + ".mp3";
                        File songFile = new File(songFilePath);
                        if (songFile.exists()) {
                            playMusic(title, songFilePath, position);
                        } else {
//                            Toast.makeText(RecordMusicActivity.this, "正在下载:" + title, Toast.LENGTH_SHORT).show();
                            DownloadSearchUtils.getsInstance().setListener(new DownloadSearchUtils.OnDownloadListener() {
                                @Override
                                public void onDowload(String mp3Url) { //下载成功
                                    playMusic(title, songFilePath, position);
                                }

                                @Override
                                public void onFailed(String error) { //下载失败

                                }
                            }).download(data);
                        }
                    }
                });
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    private void playMusic(String title, String songFilePath, int position) {
        Intent playerStart = new Intent(RecordMusicActivity.this, MusicPlayerService.class);
        playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
        playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
        RecordMusicActivity.this.startService(playerStart);
        CacheUtils.setString(RecordMusicActivity.this, XConstant.CURRENT_SELECT_MUSIC, title);

        for (DubSearchMusicDataInfo.ResultBean bean : searchData) {
            bean.setSelected(false);
        }
        searchData.get(position).setSelected(true);
        searchMusicRecyclerViewAdapter.notifyDataSetChanged();
    }

    //请求音乐类型
    private void getDubTypeData() {
        String url = XConstant.HOST + XConstant.GET_MATERIAL_TYPE + "?opr=" + opr;
        OkHttpClientManager.get(url, new OKHttpCallback<DubMusicTypeInfo>() {
            @Override
            public void onResponse(DubMusicTypeInfo response) {
                resultBean = response.getResult();
                for (int i = 0; i < resultBean.size(); i++) {
//                    Log.e("DubMusicTypeInfo-Parid", new String(Base64.decode(resultBean.get(i).getParid().getBytes(), Base64.DEFAULT)));
//                    Log.e("DubMusicTypeInfo-Subid", new String(Base64.decode(resultBean.get(i).getSubid().getBytes(), Base64.DEFAULT)));
                    String subtit = new String(Base64.decode(resultBean.get(i).getSubtit().getBytes(),
                            Base64.DEFAULT));
                    titles.add(subtit);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    DubMusicRecommendFragment d = new DubMusicRecommendFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("type", new String(Base64.decode(resultBean.get(i).getParid().getBytes(), Base64.DEFAULT)));
//                    d.setArguments(bundle);
                    fragmentList.add(d);
                    transaction.commit();

                }
//                for (int i = 0; i < resultBean.size(); i++) {
//                    DubMusicRecommendFragment dub = new DubMusicRecommendFragment();
//                    fragmentList.add(dub);
//                }
                TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter
                        (getSupportFragmentManager(), fragmentList, titles);
                hvDubMusicSongViewpager.setAdapter(tabFragmentAdapter);
//                hvDubMusicSongViewpager.setCurrentItem(111, true);
                tlDubMusicSongTab.setTabMode(TabLayout.MODE_SCROLLABLE);
                tlDubMusicSongTab.setupWithViewPager(hvDubMusicSongViewpager);
//                for (int i = 0; i < tabFragmentAdapter.getCount(); i++) {
//                    tlDubMusicSongTab.getTabAt(i).setText("tab_" +(i+1));
//                }
                fragmentList.get(0).reStr(resultBean.get(0).getParid(), resultBean.get(0).getSubid());
                hvDubMusicSongViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        String par = resultBean.get(position).getParid();
                        String sub = resultBean.get(position).getSubid();
                        fragmentList.get(position).reStr(par, sub);
                        Log.e(TAG, "par = " + par + "------" + "sub = " + sub);

                        Intent playerStart = new Intent(RecordMusicActivity.this, MusicPlayerService.class);
                        playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_PAUSE);
                        startService(playerStart);
//                        if (position > lable) {
//                            lable = position;
//                            if(lable+1<resultBean.size()){
//                                fondre(false, true);
//                            }
//                        }
//                        if (position < lable) {
//                            lable = position;
//                            if(lable>0) {
//                                fondre(true, false);
//                            }
//                        }
//                        Log.d("onPageSelected",position+"---");
//                        fondre();
//                        fragmentList.get(position+1).reStr(resultBean.get(position+1).getParid());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
