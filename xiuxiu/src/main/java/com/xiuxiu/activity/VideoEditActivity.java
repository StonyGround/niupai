package com.xiuxiu.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xiuxiu.R;
import com.xiuxiu.adapter.CharacterRecyclerViewAdapter;
import com.xiuxiu.adapter.CharacterVrayRecyclerViewAdapter;
import com.xiuxiu.adapter.IconButtonAdapter;
import com.xiuxiu.layer.ImageLayer;
import com.xiuxiu.layer.LogoLayer;
import com.xiuxiu.model.FrameListModel;
import com.xiuxiu.model.IconButtonInfo;
import com.xiuxiu.model.TextEditModel;
import com.xiuxiu.service.MusicPlayerService;
import com.xiuxiu.util.AccSongInfoParse;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.view.AccSongLyricsView;
import com.xiuxiu.view.ImageRenderView;
import com.xiuxiu.view.StickerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

// 视频编辑页
public class VideoEditActivity extends AppCompatActivity implements OnPreparedListener, OnClickListener,
        OnCompletionListener, OnErrorListener {

    private static final String TAG = "VideoEditActivity";
    // 视频预览
    private ImageView videoApplyBtn = null;
    private ImageView editingReturnBtn = null;
    private ImageView videoPlayingTips = null;
    private MediaPlayer mediaPlayer = null;
    private Surface videoSurface = null;
    private RelativeLayout videoPreviewLayout = null;

    // 音/视频文件及输出文件路径
    private String audioSeedFilePath = null, videoSeedFilePath = null, audioFilePath = null, tmpOutputFilePath = null, outputFilePath =
            null, songLyricsPath = null;
    // 主题/滤镜/配乐// 录制模式
    private int recordMode = XConstant.RECORD_MODE_NOR;
    private int curClassId = -999, curThemeIndex = 0, curFilterIndex = 0, curIncMusicIndex = 1;
    private boolean isThemeOn = false, isFilterOn = false, isIncMusicOn = false, isOriginalVoiceOff = false;
    private LinearLayout themeBtn = null, filterBtn = null, incidentalMusicBtn = null;
    private List<IconButtonInfo> iconBtnListData = null;
    private IconButtonAdapter iconBtnAdapter = null;
    private GridView iconBtnGridView = null;
    private LinearLayout iconBtnLayout = null;
    private String themeText = "";
    private String[] incMusicFileName = {
            "incmusic_bye_bye_sunday.mp3", "incmusic_swing_dance_two.mp3", "incmusic_the_soul_of_bruce.mp3", "incmusic_a_little_kiss.mp3",
            "incmusic_aimlessly_through_the_streets.mp3", "incmusic_bibibabibobi.mp3", "incmusic_beiershuang.mp3", "incmusic_shehuiyao.mp3",
            "incmusic_xiaopingguo.mp3", "incmusic_you_me.mp3", "incmusic_i_do.mp3", "incmusic_jazz_club.mp3",
            "incmusic_barcelona.mp3", "incmusic_take_me_home.mp3", "incmusic_travelling_circus.mp3", "incmusic_jn_style.mp3",
            "incmusic_hulijiao.mp3", "incmusic_laopailianqing.mp3", "incmusic_gufeng.mp3", "incmusic_all_things_holiday.mp3"};
    // 视频渲染
    private int frameWidth = XConstant.FRAME_DST_WIDTH;
    private int frameHeight = XConstant.FRAME_DST_HEIGHT;
    //    private int frameHeight = Util.getScreenHeight(this);
    private int listIndex = 0, frameIndex = 0, frameThreshold = 0, yuvFrameSize = 0, mainFrameSize = 0, qtrFrameSize = 0;
    private static final double TIME_PER_FRAME = 66.6666667;// 1000.0 / 15 (1秒15帧)
    private static final int ADD_LOGO_FRAMECOUNT = 20;
    private int videoTemplateFrameIndex = 0;
    private double videoTemplateTimePerFrame = TIME_PER_FRAME;
    private double frameTotalTime = 0.0;// 视频总时长
    private byte[] y_data = null, u_data = null, v_data = null;
    private int[] rgbData = null;
    private ByteBuffer yBuf = null, uBuf = null, vBuf = null, mBuf = null, lBuf = null, tBuf = null;
    private IntBuffer rgbBuffer = null;
    private Bitmap baseBmp = null;
    private ImageRenderView imageRenderView = null;
    private List<Long> videoFrameList = null;
    private LogoLayer logoLayer = null;
    private ImageLayer imageLayer = null;

    private boolean isPlaying = false, isStartPlay = true;

    private String accSongPath = "";

    private Bitmap watermark;

    private LinearLayout ll_text_edit;
    private RecyclerView rvCharacterVray;
    private RecyclerView rvCharacter;
    private List<Bitmap> bitmapList;
    private CharacterVrayRecyclerViewAdapter characterVrayRecyclerViewAdapter;
    private CharacterRecyclerViewAdapter characterRecyclerViewAdapter;
    private RelativeLayout contentLayout;
    private RelativeLayout rl_add_text, rl_video_edit;
    private ImageView add_text_cancle, add_text_sure;
    private int curFramePos;
    private Bitmap curBitmap;
    private List<TextEditModel> addTextList;
    private StickerView stickerView;
    private int perTextX = 0;
    private int[] textBgArr = {R.drawable.bubble1, R.drawable.bubble2, R.drawable.bubble3, R.drawable.bubble4, R
            .drawable.bubble5, R.drawable.bubble6, R.drawable.bubble7, R.drawable.bubble8, R.drawable.bubble9, R.drawable.bubble10, R
            .drawable.bubble11};

    private LinearLayout ll_image_edit;
    private RecyclerView rv_theme_vray;
    private RecyclerView rv_theme;
    private List<Bitmap> imageMuxList;
    private RelativeLayout rl_add_image;
    private ImageView add_image_cancle, add_image_sure;

    //视频文件宽度和屏幕宽度比，适用于等比例缩放需要添加的图片
    private float size;

    private int waterMarkId;

    private AccSongLyricsView accSongLyricsView = null;
    private volatile boolean isMusicPlayingStop = false;
    private MusicPlayerReceiver musicPlayerReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_edit);

        size = (float) Util.getScreenWidth(VideoEditActivity.this) / (float) XConstant.FRAME_DST_WIDTH;

        //隐藏状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // 获取传递的数据
        recordMode = getIntent().getIntExtra("recordMode", XConstant.RECORD_MODE_NOR);
        audioFilePath = getIntent().getStringExtra("audioFilePath");
        songLyricsPath = getIntent().getStringExtra("songLyricsPath");
        audioSeedFilePath = getIntent().getStringExtra("audioSeedFilePath");
        videoSeedFilePath = getIntent().getStringExtra("videoSeedFilePath");
        tmpOutputFilePath = getIntent().getStringExtra("tmpOutputFilePath");
        FrameListModel frameListModel = getIntent().getParcelableExtra("videoFrameList");
        videoFrameList = frameListModel.getFrameList();
        deleteBreakPoint();
        int videoFrameRate = getIntent().getIntExtra("videoFrameRate", 15);
        videoTemplateTimePerFrame = 1000.0 / videoFrameRate;
        outputFilePath = Util.genrateFilePath("xiu_output", ".mp4", XConstant.VIDEO_FILE_PATH);
        frameTotalTime = videoFrameList.size() * TIME_PER_FRAME;

        //选择水印
        int fromtype = CacheUtils.getInt(VideoEditActivity.this, XConstant.FROM_TYPE_MY);
        if (fromtype == 3) {
            waterMarkId = R.drawable.watermark_happy;
        } else if (fromtype == 11) {
            waterMarkId = R.drawable.watermark_crystal;
        } else {
            waterMarkId = R.drawable.watermark;
        }
        watermark = BitmapFactory.decodeResource(getResources(), waterMarkId);

        // 预览返回
        editingReturnBtn = (ImageView) findViewById(R.id.editingReturnBtn);
        editingReturnBtn.setOnClickListener(this);

        // 使用视频
        videoApplyBtn = (ImageView) findViewById(R.id.videoApplyBtn);
        videoApplyBtn.setOnClickListener(this);

        int viewWidth = Util.getScreenWidth(this);
        int viewHeight = (int) (viewWidth * 4 / 3);// 帧宽高比为4:3
        // 初始化视频预览
        RelativeLayout.LayoutParams layoutParams = null;
        RelativeLayout videoEditBody = (RelativeLayout) findViewById(R.id.videoEditBody);
        layoutParams = (RelativeLayout.LayoutParams) videoEditBody.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        videoEditBody.setLayoutParams(layoutParams);

        videoPreviewLayout = (RelativeLayout) findViewById(R.id.videoPreviewLayout);
        layoutParams = (RelativeLayout.LayoutParams) videoPreviewLayout.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        videoPreviewLayout.setLayoutParams(layoutParams);
        videoPreviewLayout.setOnClickListener(this);

        // 视频渲染
        if (recordMode == XConstant.RECORD_MODE_NOR) {
            imageRenderView = new ImageRenderView(this, frameWidth, frameHeight);
            videoPreviewLayout.addView(imageRenderView, layoutParams);
//            videoPreviewLayout.addView(videoPreview, layoutParams);
        } else {
            imageRenderView = new ImageRenderView(this, XConstant.FRAME_SRC_HEIGHT >> 1, XConstant.FRAME_SRC_WIDTH >> 1);
            viewWidth = (int) (viewWidth * XConstant.PIP_MAPPING_COEF);
            viewHeight = viewWidth * 4 / 3;
            layoutParams = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
            imageRenderView.setX(0);
            imageRenderView.setY(Util.getScreenWidth(this) - viewHeight);
            videoPreviewLayout.addView(imageRenderView, layoutParams);
        }

        // 伴奏歌曲歌词页面
        int screenWidth = Util.getScreenWidth(this);
        RelativeLayout accSongLyricsLayout = (RelativeLayout) this.findViewById(R.id.accSongLyricsLayout);
        layoutParams = new RelativeLayout.LayoutParams(screenWidth, (int) (screenWidth * 0.25));
        accSongLyricsView = new AccSongLyricsView(this, screenWidth, (int) (screenWidth * 0.15));
        accSongLyricsLayout.addView(accSongLyricsView, layoutParams);


        // 播放控制
        videoPlayingTips = (ImageView) findViewById(R.id.videoPlayingTips);
//        videoPlayingTips.setVisibility(View.GONE);
        videoPlayingTips.setOnClickListener(this);
        videoPreviewLayout.bringChildToFront(videoPlayingTips);

        // 注册广播接收>>接收音乐时长及播放进度
        musicPlayerReceiver = new MusicPlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(XConstant.MUSIC_DURATION);
        filter.addAction(XConstant.MUSIC_CURRENT_TIME);
        filter.addAction(XConstant.MUSIC_COMPLETION);
        this.registerReceiver(musicPlayerReceiver, filter);

        //文字编辑
        bitmapList = new ArrayList<>();

        ll_text_edit = (LinearLayout) findViewById(R.id.ll_text_edit);
        rvCharacterVray = (RecyclerView) findViewById(R.id.rv_character_vray);
        rvCharacter = (RecyclerView) findViewById(R.id.rv_character);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacterVray.setLayoutManager(mLinearLayoutManager);
        LinearLayoutManager mLinearLayoutManager1 = new LinearLayoutManager(this);
        mLinearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacter.setLayoutManager(mLinearLayoutManager1);

        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        videoPreviewLayout.bringChildToFront(contentLayout);
        rl_add_text = (RelativeLayout) findViewById(R.id.rl_add_text);
        rl_video_edit = (RelativeLayout) findViewById(R.id.rl_video_edit);
        add_text_cancle = (ImageView) findViewById(R.id.add_text_cancle);
        add_text_sure = (ImageView) findViewById(R.id.add_text_sure);
        add_text_sure.setOnClickListener(this);
        add_text_cancle.setOnClickListener(this);

        //添加主题
        ll_image_edit = (LinearLayout) findViewById(R.id.ll_image_edit);
        rv_theme_vray = (RecyclerView) findViewById(R.id.rv_theme_vray);
        rv_theme = (RecyclerView) findViewById(R.id.rv_theme);
        LinearLayoutManager mLinearLayoutManager2 = new LinearLayoutManager(this);
        mLinearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager mLinearLayoutManager3 = new LinearLayoutManager(this);
        mLinearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_theme_vray.setLayoutManager(mLinearLayoutManager2);
        rv_theme.setLayoutManager(mLinearLayoutManager3);
        imageMuxList = new ArrayList<>();
        rl_add_image = (RelativeLayout) findViewById(R.id.rl_add_image);
        add_image_cancle = (ImageView) findViewById(R.id.add_image_cancle);
        add_image_sure = (ImageView) findViewById(R.id.add_image_sure);
        add_image_cancle.setOnClickListener(this);
        add_image_sure.setOnClickListener(this);

        // 播放器
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.setOnErrorListener(this);
        // 定义图层
        logoLayer = new LogoLayer(this, 0, false);
        imageLayer = new ImageLayer(this, 1, false);

        String nickName = CacheUtils.getString(this, XConstant.NICKNAME);
        if (nickName.equals("")) {
            logoLayer.setUserName("牛拍");
        } else {
            logoLayer.setUserName(nickName);
        }
        Log.e("VideoEditActivity", nickName + "----nickName----");

        // 主题/滤镜/字幕/配乐
        themeBtn = (LinearLayout) findViewById(R.id.themeBtn);
        filterBtn = (LinearLayout) findViewById(R.id.filterBtn);
        incidentalMusicBtn = (LinearLayout) findViewById(R.id.incidentalMusicBtn);
        themeBtn.setOnClickListener(this);
        filterBtn.setOnClickListener(this);
        incidentalMusicBtn.setOnClickListener(this);
        if (recordMode == XConstant.RECORD_MODE_PIP) {
            themeBtn.setEnabled(false);
            filterBtn.setEnabled(false);
            incidentalMusicBtn.setEnabled(false);
        }

        // 初始化水平滚动图标按钮
        iconBtnListData = new ArrayList<IconButtonInfo>();
        iconBtnAdapter = new IconButtonAdapter(this);
        iconBtnLayout = (LinearLayout) findViewById(R.id.iconBtnLayout);
        iconBtnGridView = (GridView) findViewById(R.id.iconBtnGridView);
        iconBtnGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                //设置选中项并更新列表
                iconBtnAdapter.changeItem(index);
                iconBtnAdapter.notifyDataSetChanged();

                // 获取选中项
                IconButtonInfo buttonInfo = (IconButtonInfo) iconBtnAdapter.getItem(index);
                switch (buttonInfo.getResType()) {
                    case XConstant.CLASS_ID_THEME:
                        // 主题
                        if (curThemeIndex != index) {
                            curThemeIndex = index;
                            onClickThemeIconBtn(index);
                        }
                        break;
                    case XConstant.CLASS_ID_FILTER:
                        // 滤镜
                        if (curFilterIndex != index) {
                            curFilterIndex = index;
                            onClickFilterIconBtn(index);
                        }
                        break;
                    case XConstant.CLASS_ID_INCMUSIC:
                        // 配乐
                        onClickIncMusicIconBtn(index);
                        break;
                    default:
                        break;
                }

                // 播放控制
                if (buttonInfo.getResType() != XConstant.CLASS_ID_INCMUSIC) {
                    playerStart();
                }
            }
        });


        //视频渲染初始化
        initVideoEdit();

    }// onCreate()


    // 设置伴奏歌曲歌词数据信息
    public void setAccSongLyricsLine(List<AccSongInfoParse.LyricsLine> listLyricsLine) {
        accSongLyricsView.setListLyricsLine(listLyricsLine);
    }

    // 接收音乐播放服务发送的信息
    class MusicPlayerReceiver extends BroadcastReceiver {
        private int currentTime = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(XConstant.MUSIC_CURRENT_TIME)) {
                // 播放进度
                currentTime = intent.getIntExtra("music_currentTime", 0);
                accSongLyricsView.compLyrics(currentTime);
            } else if (action.equals(XConstant.MUSIC_DURATION)) {
                accSongLyricsView.resetParams();
            } else if (action.equals(XConstant.MUSIC_COMPLETION)) {
                // 播放完成>>重置参数
                accSongLyricsView.resetParams();
                isMusicPlayingStop = true;
            }
        }
    }

    // 初始化视频编辑参数
    private void initVideoEdit() {

        // 分配内存
        mainFrameSize = frameWidth * frameHeight;
        if (recordMode == XConstant.RECORD_MODE_NOR) {
            yuvFrameSize = mainFrameSize;
        } else if (recordMode == XConstant.RECORD_MODE_PIP) {
            yuvFrameSize = XConstant.FRAME_SRC_WIDTH * XConstant.FRAME_SRC_HEIGHT / 4;
        }

        qtrFrameSize = yuvFrameSize >> 2;
        y_data = new byte[yuvFrameSize];
        u_data = new byte[qtrFrameSize];
        v_data = new byte[qtrFrameSize];

        rgbData = new int[yuvFrameSize];
        rgbBuffer = IntBuffer.allocate(yuvFrameSize);

        yBuf = ByteBuffer.allocateDirect(yuvFrameSize);
        yBuf.order(ByteOrder.nativeOrder()).position(0);
        uBuf = ByteBuffer.allocateDirect(qtrFrameSize);
        uBuf.order(ByteOrder.nativeOrder()).position(0);
        vBuf = ByteBuffer.allocateDirect(qtrFrameSize);
        vBuf.order(ByteOrder.nativeOrder()).position(0);
        mBuf = ByteBuffer.allocateDirect(mainFrameSize * 4);
        mBuf.order(ByteOrder.nativeOrder()).position(0);
        lBuf = ByteBuffer.allocateDirect(mainFrameSize * 4);
        lBuf.order(ByteOrder.nativeOrder()).position(0);
        tBuf = ByteBuffer.allocateDirect(mainFrameSize * 4);
        tBuf.order(ByteOrder.nativeOrder()).position(0);
        baseBmp = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_4444);// 底图

        // 计算视频帧数
        int breakPoints = 0;
        for (Long handle : videoFrameList) {
            if (handle == 0) {
                breakPoints++;
            }
        }

        int frameCount = videoFrameList.size() - breakPoints;
        frameThreshold = frameCount - ADD_LOGO_FRAMECOUNT - 1;

//        Log.d("dd_cc_dd", "..initVideoEdit..frameCount.." + frameCount);

        //渲染第一帧图片
        renderFrame(0);
    }

    //文字编辑、贴图预览
    private void renderFrame(int position) {

        long handle = videoFrameList.get(position);
        if (handle != 0) {
            // yuv数据填充
            AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
            yBuf.put(y_data).position(0);
            uBuf.put(u_data).position(0);
            vBuf.put(v_data).position(0);

            imageLayer.loadImage(waterMarkId, 0, frameHeight - watermark.getHeight());
            imageLayer.drawLayer(baseBmp);
            baseBmp.copyPixelsToBuffer(mBuf);
            mBuf.position(0);

            // 更新数据
            imageRenderView.setRenderFinished(false);
            imageRenderView.updateTextureData(yBuf, uBuf, vBuf, mBuf, lBuf, tBuf, 0, 0);
        }
    }


    // 初始化预览视频
    private void initPreview(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            FileInputStream is = new FileInputStream(outputFilePath);
            mediaPlayer.setDataSource(is.getFD());
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 播放视频文件
    private void playerStart() {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
//        videoPlayingTips.setVisibility(View.GONE);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.seekTo(0);
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        videoPlayingTips.setVisibility(View.VISIBLE);
        listIndex = 0;
        frameIndex = 0;
        videoTemplateFrameIndex = 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.editingReturnBtn) {
            // 返回
            gotoMainActivity(XConstant.RECORD_VIEW);
        } else if (id == R.id.videoApplyBtn) {
            //停止渲染
            handler.sendEmptyMessage(XConstant.VIDEO_RENDER_PAUSE);

            // 添加遮挡
            int[] pixels = new int[mainFrameSize];
            Bitmap overlapBmp = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_4444);
            for (int i = 0; i < mainFrameSize; i++) {
                pixels[i] = 0xff5d5b62;
            }
            overlapBmp.setPixels(pixels, 0, frameWidth, 0, 0, frameWidth, frameHeight);

            LogoLayer tlogoLayer = new LogoLayer(this, 0, false);
//            String nickName = "";
            String nickName = CacheUtils.getString(this, XConstant.NICKNAME);
            if (nickName.equals("")) {
                tlogoLayer.setUserName("牛拍");
            } else {
                tlogoLayer.setUserName(nickName);
            }
            tlogoLayer.updateLogoAlpha(ADD_LOGO_FRAMECOUNT, 0, ADD_LOGO_FRAMECOUNT);
            tlogoLayer.drawLayer(overlapBmp);
            videoPlayingTips.setImageBitmap(overlapBmp);
            videoPlayingTips.setVisibility(View.VISIBLE);
            videoPlayingTips.setScaleType(ImageView.ScaleType.FIT_CENTER);
            videoPreviewLayout.bringChildToFront(videoPlayingTips);

            RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(videoPlayingTips.getLayoutParams());
            rp.width = videoPreviewLayout.getWidth();
            rp.height = videoPreviewLayout.getHeight();
            videoPlayingTips.setLayoutParams(rp);

            // 合成输出视频
            new AsyncAVCreate().execute();
        } else if (id == R.id.videoPlayingTips) {
            //播放
            if (isStartPlay) {
                videoPlayingTips.setVisibility(View.GONE);
                handler.sendEmptyMessage(XConstant.VIDEO_RENDER_START);
                isStartPlay = false;
                isPlaying = true;

                AccSongInfoParse.getInstance().parseLyricsInfo(songLyricsPath);
                List<AccSongInfoParse.LyricsLine> listLyricsLine = AccSongInfoParse.getInstance().getListLyricsLine();
                setAccSongLyricsLine(listLyricsLine);

                Intent playerStart = new Intent(this, MusicPlayerService.class);
                if (TextUtils.isEmpty(accSongPath)) {
                    playerStart.putExtra("MUSIC_FILE_NAME", audioFilePath);
                } else {
                    playerStart.putExtra("MUSIC_FILE_NAME", accSongPath);
                }
                playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
                playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_SINGING);
                startService(playerStart);
                CacheUtils.setString(this, XConstant.CURRENT_SELECT_MUSIC, "");
            }

            if (!isPlaying) {
                isPlaying = true;
                videoPlayingTips.setVisibility(View.GONE);
                handler.sendEmptyMessage(XConstant.VIDEO_RENDER_START);
                Intent playerStart = new Intent(this, MusicPlayerService.class);
                playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_RESUME);
                startService(playerStart);
            }
        } else if (id == R.id.videoPreviewLayout) {
            //暂停
            if (isPlaying) {
                isPlaying = false;
                videoPlayingTips.setVisibility(View.VISIBLE);
                handler.removeMessages(XConstant.VIDEO_RENDER_START);
                Intent playerStart = new Intent(this, MusicPlayerService.class);
                playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_PAUSE);
                startService(playerStart);
            }
        } else if (id == R.id.themeBtn) {
            // 主题
//
//            Intent intent = new Intent(VideoEditActivity.this, CharacterActivity.class);
            Intent intent = new Intent(VideoEditActivity.this, ThemeEditActivity.class);
            FrameListModel frameListModel = new FrameListModel();
            frameListModel.setFrameList(videoFrameList);
            intent.putExtra("videoFrameList", frameListModel);
            startActivityForResult(intent, XConstant.TEXT_ACTIVITY_RESULT);
        } else if (id == R.id.filterBtn) {
            // 文字
            Intent intent = new Intent(VideoEditActivity.this, TextEditActivity.class);
            intent.putExtra("type", 1);
            FrameListModel frameListModel = new FrameListModel();
            frameListModel.setFrameList(videoFrameList);
            intent.putExtra("videoFrameList", frameListModel);
            startActivityForResult(intent, XConstant.TEXT_ACTIVITY_RESULT);

        } else if (id == R.id.incidentalMusicBtn) {
            // 配乐
            // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                /*boolean isDubbing = CacheUtils.getBooleanFirst(VideoEditActivity.this, XConstant.GUIDE_DUBBING);
                if (isDubbing) {
                    DialogGuide dialogGuideDubbing = new DialogGuide(VideoEditActivity.this, R.drawable.tip_dubbing);
                    dialogGuideDubbing.show();

                    CacheUtils.setBooleanFirst(VideoEditActivity.this, XConstant.GUIDE_DUBBING, false);
                }else {
                    Intent intent = new Intent(VideoEditActivity.this, RecordMusicActivity.class);
                    intent.putExtra("opr", 1);
                    intent.putExtra("musictitle", "配乐");
                    intent.putExtra("hinttitle", "搜索配乐");
                    intent.putExtra("selecttitle", "您暂未选择配乐");
                    startActivityForResult(intent, XConstant.MUSIC_ACTIVITY_RESULT);
                }*/
            Intent intent = new Intent(VideoEditActivity.this, RecordMusicActivity.class);
            intent.putExtra("opr", 1);
            intent.putExtra("musictitle", "配乐");
            intent.putExtra("hinttitle", "搜索配乐");
            intent.putExtra("selecttitle", "您暂未选择配乐");
            startActivityForResult(intent, XConstant.MUSIC_ACTIVITY_RESULT);
        } else if (id == R.id.add_text_cancle) {
            rl_add_text.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            ll_text_edit.setVisibility(View.GONE);
            videoPlayingTips.setVisibility(View.VISIBLE);
            rl_video_edit.setVisibility(View.VISIBLE);
        } else if (id == R.id.add_text_sure) {
            //TODO
        } else if (id == R.id.add_image_cancle) {
            rl_add_image.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            ll_image_edit.setVisibility(View.GONE);
            videoPlayingTips.setVisibility(View.VISIBLE);
            rl_video_edit.setVisibility(View.VISIBLE);
        }
        // void onClick()
    }

    //删除断点
    private void deleteBreakPoint() {
        Iterator<Long> sListIterator = videoFrameList.iterator();
        while (sListIterator.hasNext()) {
            long e = sListIterator.next();
            if (e == 0) {
                sListIterator.remove();
            }
        }
    }

    // 初始化水平滚动图标按钮
    private void initHScrollIconButton(int classId, int[] resId, String[] text) {
        // 记录当前选择的类别id
        curClassId = classId;
        // 保存历史索引
        int flagIndex = 0;
        switch (curClassId) {
            case XConstant.CLASS_ID_THEME:
                flagIndex = curThemeIndex;
                break;
            case XConstant.CLASS_ID_FILTER:
                flagIndex = curFilterIndex;
                break;
            case XConstant.CLASS_ID_INCMUSIC:
                flagIndex = curIncMusicIndex;
                break;
            default:
                break;
        }

        // 重置按钮状态
        themeBtn.setSelected(false);
        filterBtn.setSelected(false);
        incidentalMusicBtn.setSelected(false);

        // 填充数据
        iconBtnListData.clear();
        IconButtonInfo btnInfo;
        int itemCount = text.length;
        for (int i = 0; i < itemCount; i++) {
            btnInfo = new IconButtonInfo(classId);
//			if (classId != XConstant.CLASS_ID_INCMUSIC)
//			{
//				btnInfo.setResId(resId[i]);
//			}
            btnInfo.setResId(resId[i]);
            btnInfo.setText(text[i]);
            btnInfo.setSelected(i == flagIndex);
            if (classId == XConstant.CLASS_ID_INCMUSIC && i >= 2) {
                btnInfo.setSongFilePath(XConstant.RES_MUSIC_FILE_PATH + "inc_music/" + incMusicFileName[i - 2]);
            }
            iconBtnListData.add(btnInfo);
        }

        // 设置配乐[关闭原音]选中状态
        if ((curClassId == XConstant.CLASS_ID_INCMUSIC) && isOriginalVoiceOff) {
            iconBtnListData.get(0).setSelected(true);
        }

        iconBtnAdapter.setIconBtnList(iconBtnListData);

        // 设置布局并映射数据
        float mapCoef = Util.getScreenWidth(this) / 480.0f;
        LayoutParams layoutParams = iconBtnLayout.getLayoutParams();
        layoutParams.width = (int) (itemCount * 100 * mapCoef);
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        iconBtnLayout.setLayoutParams(layoutParams);
        iconBtnGridView.setNumColumns(itemCount);
        iconBtnGridView.setAdapter(iconBtnAdapter);
    }

    // 返回至主页面
    private void gotoMainActivity(int viewIndex) {
        // 先释放资源
        release();

        // 再页面跳转
        Intent intent = new Intent();
        intent.putExtra("VIEW_INDEX", viewIndex);
        setResult(666, intent);
        finish();
    }

    private void clearVideoData() {
        for (Long handle : videoFrameList) {
            if (handle != 0) {
                AVProcessing.deleteDataFrame(handle);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gotoMainActivity(XConstant.RECORD_VIEW);
        }

        return super.onKeyDown(keyCode, event);
    }


    private void getStartIcon() {
        int viewWidth = Util.getScreenWidth(this);
        viewWidth = viewWidth / 5;
        int viewHeight = viewWidth;
        Glide.with(VideoEditActivity.this)
                .load(R.drawable.edit_play)
                .override(viewWidth, viewHeight)
                .into(videoPlayingTips);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(videoPlayingTips.getLayoutParams());
        rp.width = viewWidth;
        rp.height = viewHeight;
        int left = videoPreviewLayout.getWidth();
        int top = videoPreviewLayout.getHeight();
        rp.setMargins((left / 2) - viewWidth / 2, (top / 2) - viewHeight / 2, 0, 0);
        videoPlayingTips.setLayoutParams(rp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = null;
        switch (resultCode) {
            case 1001:
                bundle = data.getExtras();
                if (bundle != null) {
                    // 更新主题文字
                    themeText = bundle.getString("themeText");
                    imageRenderView.updateTextEditRect(imageLayer.updateMainTextContent(themeText));
                }
                break;
            case 1002:

                bundle = data.getExtras();
                if (bundle != null) {
                    String viewName = bundle.getString("VIEW_NAME");
                    if (viewName.equals("EDIT_VIEW")) {
                        // 重新播放
                        listIndex = 0;
                        frameIndex = 0;
                        videoTemplateFrameIndex = 0;
                        imageRenderView.setCopyFrameBuffer(false);
                        getStartIcon();
                        initVideoEdit();
                    } else if (viewName.equals("HOME_VIEW")) {
                        // 返回至主页
                        gotoMainActivity(XConstant.HOME_VIEW);
                    } else if (viewName.equals("PERSON_VIEW")) {
                        // 返回至个人页
                        gotoMainActivity(XConstant.PERSON_VIEW);
                    }
                }
                break;

            case XConstant.MUSIC_ACTIVITY_RESULT:
                //返回歌曲名称
                String title = data.getExtras().getString("title");
                if (!TextUtils.isEmpty(title)) {
                    accSongPath = XConstant.RES_ACC_MUSIC_FILE_PATH + title + ".mp3";
                    songLyricsPath = XConstant.RES_ACCLYRICS_FILE_PATH + title + ".lr";
                    if (!new File(accSongPath).exists()) {
                        accSongPath = XConstant.RES_MUSIC_FILE_PATH + title + ".mp3";
                    }
                }
                break;

            case XConstant.TEXT_ACTIVITY_RESULT:
                FrameListModel frameListModel = data.getExtras().getParcelable("videoFrameList");
                List<Long> list = frameListModel.getFrameList();
                videoFrameList = list;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageRenderView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageRenderView.onResume();
    }

    @Override
    protected void onStop() {
        // 暂停
        if (mediaPlayer != null) {
            mediaPlayer.pause();
//            videoPlayingTips.setVisibility(View.VISIBLE);
        }

        // Log.d("dd_cc_dd", "视频编辑页面...onStop()...");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止音乐播放服务
        Intent playerStart = new Intent(this, MusicPlayerService.class);
        this.stopService(playerStart);

        // 释放广播接收
        if (musicPlayerReceiver != null) {
            this.unregisterReceiver(musicPlayerReceiver);
        }
    }

    // 释放资源
    private void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (videoSurface != null) {
            videoSurface.release();
            videoSurface = null;
        }

        imageRenderView.releaseShaderProgram();
        clearVideoData();
    }

    // 主题文字被点击
    public void onThemeTextClicked() {
//        Intent intent = new Intent(VideoEditActivity.this, TextEditActivity.class);
//        intent.putExtra("themeText", themeText);
//        startActivityForResult(intent, 1001);
    }

    // 主题图标按钮被点击
    private void onClickThemeIconBtn(int index) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String strDate = dateFormat.format(new Date());

        String strLocation = "未知";
//    	if (MainActivity.getLocationInfo() != null)
//		{
//    		strLocation = MainActivity.getLocationInfo().getString("City", "未知");
//		}
//    	String[] content = { strDate, strLocation };

        imageLayer.resetParams();
        switch (index) {
            case 0:
                // 无主题
                themeText = "";
                imageRenderView.updateTextEditRect(null);
                break;
            case 1:
                // 我的午餐
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 主文字
                themeText = "洗去浮华的质朴美味";
                imageLayer.setMainTxtFontColor(Color.WHITE);
                Rect rect0 = imageLayer.setMainTextContent(themeText, 24, 220, 30, 400);
                imageRenderView.updateTextEditRect(rect0);
                // 副文字
                String[] content1 = {strLocation};
                float[] posX1 = {30.0f};
                float[] posY1 = {450.0f};
                imageLayer.setSubTextContent(content1, 20, posX1, posY1);
                break;
            case 2:
                // 下午茶
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 主文字
                themeText = "层层香甜的味蕾享受";
                imageLayer.setMainTxtFontColor(Color.WHITE);
                Rect rect1 = imageLayer.setMainTextContent(themeText, 24, 220, 30, 400);
                imageRenderView.updateTextEditRect(rect1);
                // 副文字
                String[] content2 = {strLocation};
                float[] posX2 = {30.0f};
                float[] posY2 = {450.0f};
                imageLayer.setSubTextContent(content2, 20, posX2, posY2);
                break;
            case 3:
                // 我的晚餐
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 主文字
                themeText = "温婉清新的精致菜色";
                imageLayer.setMainTxtFontColor(Color.WHITE);
                Rect rect2 = imageLayer.setMainTextContent(themeText, 24, 220, 30, 400);
                imageRenderView.updateTextEditRect(rect2);
                // 副文字
                String[] content3 = {strLocation};
                float[] posX3 = {30.0f};
                float[] posY3 = {450.0f};
                imageLayer.setSubTextContent(content3, 20, posX3, posY3);
                break;
            case 4:
                // 闺蜜
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 副文字
                float[] posX4 = {268.0f, 419.0f};
                float[] posY4 = {447.0f, 447.0f};
//        	imageLayer.setSubTextContent(content, 20, posX4, posY4);
                break;
            case 5:
                // 人在囧途
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 副文字
                float[] posX5 = {260.0f, 411.0f};
                float[] posY5 = {447.0f, 447.0f};
//        	imageLayer.setSubTextContent(content, 20, posX5, posY5);
                break;
            case 6:
                // 好基友
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 副文字
                float[] posX6 = {268.0f, 419.0f};
                float[] posY6 = {447.0f, 447.0f};
//        	imageLayer.setSubTextContent(content, 20, posX6, posY6);
                break;
            case 7:
                // 我的宝贝
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 副文字
                String[] content7 = {strDate};
                float[] posX7 = {120.0f};
                float[] posY7 = {432.0f};
                imageLayer.setSubTextContent(content7, 20, posX7, posY7);
                break;
            case 8:
                // 萌宠
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 副文字
                String[] content8 = {"我是吃货,我爱卖萌~"};
                float[] posX8 = {255.0f};
                float[] posY8 = {420.0f};
                imageLayer.setSubTextContent(content8, 20, posX8, posY8);
                break;
            case 9:
                // 购物
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 主文字
                themeText = "又败家了肿么办T_T";
                imageLayer.setMainTxtFontColor(Color.WHITE);
                Rect rect9 = imageLayer.setMainTextContent(themeText, 24, 220, 130, 400);
                imageRenderView.updateTextEditRect(rect9);
                // 副文字
                String[] content9 = {strLocation};
                float[] posX9 = {220.0f};
                float[] posY9 = {450.0f};
                imageLayer.setSubTextContent(content9, 20, posX9, posY9);
                break;
            case 10:
                // 啤酒炸鸡
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 270);
                // 主文字
                themeText = "吃炸鸡喝啤酒\n看教授！";
                imageLayer.setMainTxtFontColor(Color.BLACK);
                Rect rect10 = imageLayer.setMainTextContent(themeText, 18, 120, 90, 290);
                imageRenderView.updateTextEditRect(rect10);
                break;
            case 11:
                // 在路上
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                // 主文字
                themeText = "每一次远行都是一个新的开始";
                imageLayer.setMainTxtFontColor(Color.WHITE);
                Rect rect11 = imageLayer.setMainTextContent(themeText, 24, 330, 10, 420);
                imageRenderView.updateTextEditRect(rect11);
                // 副文字
                float[] posX11 = {10.0f, 150.0f};
                float[] posY11 = {470.0f, 470.0f};
//        	imageLayer.setSubTextContent(content, 20, posX11, posY11);
                break;
            case 12:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 330, 330);
                break;
            case 13:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 330, 330);
                break;
            case 14:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 280, 280);
                break;
            case 15:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 0);
                break;
            case 16:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 330);
                break;
            case 17:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 360);
                break;
            case 18:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 380);
                break;
            case 19:
                imageLayer.loadImage(R.drawable.theme_lunch_mini, 0, 380);
                break;
            default:
                break;
        }
    }

    // 滤镜图标按钮被点击
    private void onClickFilterIconBtn(int index) {
        imageRenderView.changeFilter(index);
    }

    // 配乐图标按钮被点击
    private void onClickIncMusicIconBtn(int index) {
        if (index != curIncMusicIndex) {
            if (index == 0) {
                // 原音关闭/开启
                isOriginalVoiceOff = !isOriginalVoiceOff;
            } else {
                // 更新索引
                curIncMusicIndex = index;
            }

            // 视频播放重置
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            listIndex = 0;
            frameIndex = 0;
            videoTemplateFrameIndex = 0;
//            videoPlayingTips.setVisibility(View.GONE);

            if (curIncMusicIndex != 1) {
                String incMusicFilePath = iconBtnListData.get(curIncMusicIndex).getSongFilePath();
                File file = new File(incMusicFilePath);
                // 配乐不存在
                if (!file.exists()) {
//					GetResourceUrl getResUrl = new GetResourceUrl(inncHandler, iconBtnListData.get(curIncMusicIndex));
//					getResUrl.execute();
                } else {
                    new AsyncEditVideoWithMusic().execute(curIncMusicIndex);
                }
            }
            // 视频添加配乐线程
            //new AsyncEditVideoWithMusic().execute(curIncMusicIndex);
        } else {
            playerStart();
        }
    }

    // 视频编辑>>添加配乐
    class AsyncEditVideoWithMusic extends AsyncTask<Integer, Integer, Void> {
        private int index = 0;
        private TextView textInfo = null;
        private ProgressBar progressBar = null;
        private Dialog processingInfoDialog = null;

        // 预处理
        @Override
        protected void onPreExecute() {
            // 创建数据处理提示窗体
            processingInfoDialog = new Dialog(VideoEditActivity.this, R.style.Dialog_Xiu);
            processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
            Window dialogWindow = processingInfoDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int) (getResources().getDisplayMetrics().density * 240);
            layoutParams.height = (int) (getResources().getDisplayMetrics().density * 80);
            layoutParams.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(layoutParams);
            processingInfoDialog.setCanceledOnTouchOutside(false);
            textInfo = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTextView);
            progressBar = (ProgressBar) processingInfoDialog.findViewById(R.id.processingProgressbar);
            textInfo.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            processingInfoDialog.show();
        }

        // 数据处理
        @Override
        protected Void doInBackground(Integer... params) {
            index = params[0];
            // 停止音频录制/编码线程
            publishProgress(0);

            // 原声开启/关闭
            String tAudioFilePath = isOriginalVoiceOff ? audioSeedFilePath : audioFilePath;

            if (index == 1) {
                // 无配乐
                AVProcessing.muxAVFile(tAudioFilePath, videoSeedFilePath, tmpOutputFilePath, frameTotalTime * 0.001);
            } else {
                // 先将配乐与人声混音,再音视频混合
                String incMusicFilePath = iconBtnListData.get(index).getSongFilePath();
                String mixerFilePath = Util.genrateFilePath("mixer_inc_music", ".mp4", XConstant.VIDEO_FILE_PATH);
                AVProcessing.mixAudioWithSong(tAudioFilePath, incMusicFilePath, mixerFilePath);
                AVProcessing.muxAVFile(mixerFilePath, videoSeedFilePath, tmpOutputFilePath, frameTotalTime * 0.001);
            }

            publishProgress(100);
            return null;
        }

        // 更新UI
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        // 处理结束
        @Override
        protected void onPostExecute(Void result) {
            processingInfoDialog.dismiss();

            // 重新播放
            if (videoSurface != null) {
//                initPreview(videoSurface);
//                videoPlayingTips.setVisibility(View.GONE);
            }
        }
    }// class AsyncEditVideoWithMusic

    // 合成输出视频
    class AsyncAVCreate extends AsyncTask<Void, Integer, Void> {
        private TextView textInfo = null;
        private ProgressBar progressBar = null;
        private Dialog processingInfoDialog = null;
        private int halfFrameWidth = XConstant.FRAME_SRC_HEIGHT >> 1;
        private int halfFrameHeight = XConstant.FRAME_SRC_WIDTH >> 1;
        private int cameraBmpWidth = (int) (XConstant.FRAME_SRC_HEIGHT * XConstant.PIP_MAPPING_COEF);
        private int cameraBmpHeight = (int) (XConstant.FRAME_SRC_WIDTH * XConstant.PIP_MAPPING_COEF);
        private int encodeFrameIndex = 0;
        private int[] rgbData = null;
        private int[] rgbBitmap = null;
        private byte[] coverData = null;
        private Bitmap cameraBmp = null;
        private ByteBuffer rgbBuf = null;
        private byte[] vYData = null, vUData = null, vVData = null;
        private ByteBuffer vYBuf = null, vUBuf = null, vVBuf = null;
        private String videoFilePath = null, coverFilePath = null;

        // 预处理
        @Override
        protected void onPreExecute() {
            // 创建数据处理提示窗体
            processingInfoDialog = new Dialog(VideoEditActivity.this, R.style.Dialog_Xiu);
            processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
            Window dialogWindow = processingInfoDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int) (getResources().getDisplayMetrics().density * 240);
            layoutParams.height = (int) (getResources().getDisplayMetrics().density * 80);
            layoutParams.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(layoutParams);
            processingInfoDialog.setCancelable(false);
            processingInfoDialog.setCanceledOnTouchOutside(false);

            textInfo = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTextView);
            progressBar = (ProgressBar) processingInfoDialog.findViewById(R.id.processingProgressbar);
            processingInfoDialog.show();
        }

        // 数据处理
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(1);

            int percent = 0;
            int index = 0;
            int frameCount = frameThreshold + ADD_LOGO_FRAMECOUNT + 1;

            // 帧数据
            rgbBuf = ByteBuffer.allocateDirect(mainFrameSize * 4).order(ByteOrder.nativeOrder());
            rgbBuf.position(0);
            coverData = new byte[mainFrameSize * 4];

            // 视频编码初始化
            videoFilePath = Util.genrateFilePath("xiu_video", ".mp4", XConstant.VIDEO_FILE_PATH);


            AVProcessing.initVideoEncode(videoFilePath, frameWidth, frameHeight);

            imageRenderView.setCopyFrameBuffer(true);
            for (int i = 0; i < videoFrameList.size(); i++) {
                long handle = videoFrameList.get(i);
                if (handle != 0) {
                    if (recordMode == XConstant.RECORD_MODE_NOR) {
                        // yuv数据填充
                        AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
                        yBuf.put(y_data).position(0);
                        uBuf.put(u_data).position(0);
                        vBuf.put(v_data).position(0);

                        // mask图层
                        imageLayer.loadImage(waterMarkId, 0, frameHeight - watermark.getHeight());
                        imageLayer.drawLayer(baseBmp);
                        baseBmp.copyPixelsToBuffer(mBuf);
                        mBuf.position(0);

//                        if (addTextList != null && addTextList.size() > 0) {
//                            for (TextEditModel model : addTextList) {
//                                if (model.getPosition() == i) {
//                                    Bitmap bitmap = model.getBitmap();
////                        imageLayer.loadImage(R.drawable.watermark, 0, frameHeight - watermark.getHeight());
//                                    imageLayer.loadBitmap(bitmap, model.getX(), model.getY(), bitmap.getWidth(), bitmap.getHeight());
//                                    imageLayer.drawLayer(baseBmp);
//                                    baseBmp.copyPixelsToBuffer(mBuf);
//                                    mBuf.position(0);
//                                    Util.showTextToast(VideoEditActivity.this, model.getX() + "-----" + model.getY());
//                                }
//                            }
//                        }

                        // logo图层
                        logoLayer.updateLogoAlpha(index, frameThreshold, ADD_LOGO_FRAMECOUNT);
                        logoLayer.drawLayer(baseBmp);
                        baseBmp.copyPixelsToBuffer(lBuf);
                        lBuf.position(0);

                        // 拷贝帧缓存纹理数据
                        imageRenderView.copyFrameBufferTextureData(rgbBuf, yBuf, uBuf, vBuf, mBuf, lBuf, tBuf, index, frameThreshold);
                    } else {
                        // 已编码帧索引
                        encodeFrameIndex++;

                        // yuv数据填充
                        while (AVProcessing.decodeVideo2Frame(vYData, vUData, vVData, mainFrameSize) < encodeFrameIndex * TIME_PER_FRAME) {

                            // Log.d("dd_cc_dd", "丢帧了.....");
                        }
                        vYBuf.put(vYData).position(0);
                        vUBuf.put(vUData).position(0);
                        vVBuf.put(vVData).position(0);

                        // 摄像头采集的数据转换为mask图层
                        AVProcessing.yuv2rgb(handle, rgbData, halfFrameWidth, halfFrameHeight);
                        cameraBmp.setPixels(rgbData, 0, halfFrameWidth, 0, 0, halfFrameWidth, halfFrameHeight);
                        imageLayer.loadBitmap(cameraBmp, 0, frameHeight - cameraBmpHeight, cameraBmpWidth, cameraBmpHeight);
                        imageLayer.drawLayer(baseBmp);
                        baseBmp.copyPixelsToBuffer(mBuf);
                        mBuf.position(0);

                        // logo图层
                        logoLayer.updateLogoAlpha(index, frameThreshold, ADD_LOGO_FRAMECOUNT);
                        logoLayer.drawLayer(baseBmp);
                        baseBmp.copyPixelsToBuffer(lBuf);
                        lBuf.position(0);

                        // 拷贝帧缓存纹理数据
                        imageRenderView.copyFrameBufferTextureData(rgbBuf, vYBuf, vUBuf, vVBuf, mBuf, lBuf, tBuf, index, frameThreshold);
                    }

                    while (!imageRenderView.isRenderFinished()) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            // e.printStackTrace();
                        }
                    }

                    // 视频编码
                    rgbBuf.position(0);
                    AVProcessing.encodeVideoFrames(rgbBuf.array());

                    // Log.d("dd_cc_dd", "..当前编码帧索引.." + index);
                    // 保存封面数据(此处直接创建封面图片,后面页面间跳转时程序会崩溃,原因???)
                    if (index == 1) {
                        rgbBuf.position(0);
                        System.arraycopy(rgbBuf.array(), 0, coverData, 0, mainFrameSize * 4);
                    }

                    // 更新ui
                    percent = (int) (85.0f * index / frameCount) + 1;
                    publishProgress(percent);
                    index++;
                }// if
            }
            AVProcessing.uninitVideoEncode();

            if (recordMode == XConstant.RECORD_MODE_NOR) {
                // 音视频混合
//                if (isOriginalVoiceOff) {
//                    // 原声关闭
//                    if (curIncMusicIndex == 1) {
//                        // 无配乐
//                        AVProcessing.muxAVFile(audioSeedFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
//                    } else {
//                        // 直接将配乐与视频混合
//                        String incMusicFilePath = XConstant.RES_MUSIC_FILE_PATH + "inc_music/" + incMusicFileName[curIncMusicIndex - 2];
//                        AVProcessing.muxAVFile(incMusicFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
//                    }
//                } else {
//                    // 原声开启
//                    if (curIncMusicIndex == 1) {
//                        // 无配乐
//                        AVProcessing.muxAVFile(audioFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
//                    } else {
//                        // 先将配乐与人声混音,再音视频混合
//                        String incMusicFilePath = XConstant.RES_MUSIC_FILE_PATH + "inc_music/" + incMusicFileName[curIncMusicIndex - 2];
//                        String mixerFilePath = Util.genrateFilePath("mixer_inc_music", ".mp4", XConstant.VIDEO_FILE_PATH);
//                        AVProcessing.mixAudioWithSong(audioFilePath, incMusicFilePath, mixerFilePath);
//                        publishProgress(90);
//                        AVProcessing.muxAVFile(mixerFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
//                    }
//                }
                if (!TextUtils.isEmpty(accSongPath)) {
                    // 已选伴奏>>先将伴奏与人声混音并删除人声音频,再音视频混合,最后更新人声音频资源路径
                    String mixerFilePath = Util.genrateFilePath("mixer_acc_music_edit", ".mp4", XConstant.VIDEO_FILE_PATH);
                    Log.d("AVRecord", accSongPath + "------" + audioFilePath);
                    int ret = AVProcessing.mixAudioWithSong2(audioFilePath, accSongPath, mixerFilePath, 0.2);
                    Log.d("AVRecord", ret + "------");
                    AVProcessing.deleteFile(audioFilePath);
                    publishProgress(90);
                    AVProcessing.muxAVFile(mixerFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
                } else {
                    AVProcessing.muxAVFile(audioFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
                }
            } else if (recordMode == XConstant.RECORD_MODE_PIP) {
                AVProcessing.muxAVFile(audioFilePath, videoFilePath, outputFilePath, frameTotalTime * 0.001);
                AVProcessing.uninitVideoDecode();
            }

            publishProgress(100);
            return null;
        }

        // 更新UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            textInfo.setText(values[0] + "%");
            progressBar.setProgress(values[0]);
        }

        // 处理结束
        @Override
        protected void onPostExecute(Void result) {
            processingInfoDialog.dismiss();

            // 创建视频封面
            coverFilePath = Util.genrateFilePath("xiu_cover", ".png", XConstant.VIDEO_FILE_PATH);
            ByteBuffer coverDataBuf = ByteBuffer.wrap(coverData);
            coverDataBuf.position(0);
            Bitmap coverBmp = Bitmap.createBitmap(frameWidth, frameHeight, Config.ARGB_8888);
            coverBmp.copyPixelsFromBuffer(coverDataBuf);

            try {
                FileOutputStream fos = new FileOutputStream(coverFilePath);
                coverBmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
                coverBmp.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 注册视频文件
            Util.registerVideo(outputFilePath, VideoEditActivity.this);

//             跳转到视频发布页面
            Intent intent = new Intent(VideoEditActivity.this, VideoSubmitActivity.class);
            intent.putExtra("coverFilePath", coverFilePath);
            intent.putExtra("videoFilePath", outputFilePath);
            intent.putExtra("recordMode", recordMode);
            intent.putExtra("identify", 0);
            startActivityForResult(intent, 1002);
        }

    }// class AsyncAVCreate

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case XConstant.VIDEO_RENDER_START:
                    handler.sendEmptyMessageDelayed(XConstant.VIDEO_RENDER_START, 66);
                    if (imageRenderView.isRenderFinished()) {
                        if (listIndex < videoFrameList.size()) {
                            videoRender(listIndex);
                            listIndex++;
                        } else {
                            videoPlayingTips.setVisibility(View.VISIBLE);
                            listIndex = 0;
                            frameIndex = 0;
                            isStartPlay = true;
                            Intent playerStart = new Intent(VideoEditActivity.this, MusicPlayerService.class);
                            playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_STOP);
                            startService(playerStart);
                            handler.removeMessages(XConstant.VIDEO_RENDER_START);
                        }
                    }
                    break;
                case XConstant.VIDEO_RENDER_PAUSE:
                    Intent playerStart = new Intent(VideoEditActivity.this, MusicPlayerService.class);
                    playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_STOP);
                    startService(playerStart);
                    handler.removeMessages(XConstant.VIDEO_RENDER_START);
                    break;
                default:
                    break;
            }
        }
    };

    //渲染指定帧数
    private void videoRender(int index) {


        long handle = videoFrameList.get(index);
        if (handle != 0) {
            // yuv数据填充
//            AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
//            yBuf.put(y_data).position(0);
//            uBuf.put(u_data).position(0);
//            vBuf.put(v_data).position(0);
            // TODO: 2017/8/7
            AVProcessing.yuv2rgb(handle, rgbData, XConstant.FRAME_DST_WIDTH, XConstant.FRAME_DST_HEIGHT);
            rgbBuffer.put(rgbData).position(0);

            //mask图层
            imageLayer.loadImage(waterMarkId, 0, frameHeight - watermark.getHeight());
            imageLayer.drawLayer(baseBmp);
            baseBmp.copyPixelsToBuffer(mBuf);
            mBuf.position(0);

            frameIndex++;
            // logo图层
            logoLayer.updateLogoAlpha(frameIndex, frameThreshold, ADD_LOGO_FRAMECOUNT);
            logoLayer.drawLayer(baseBmp);
            baseBmp.copyPixelsToBuffer(lBuf);
            lBuf.position(0);

            // 更新数据
            imageRenderView.setRenderFinished(false);
            imageRenderView.updateTextureData(rgbBuffer, mBuf, lBuf, tBuf, frameIndex, frameThreshold);
//            Log.d("dd_cc_dd", "..onSurfaceTextureAvailable.." + frameIndex);
        }
    }

    /*@Override
    public void run() {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoFilePath);

        for (int i = 40000 * 1000; i < 50 * 1000 * 1000; i += 500 * 1000) {
            Bitmap bitmap = metadataRetriever.
                    getFrameAtTime(videoPreview.getCurrentPosition()*1000, MediaMetadataRetriever.OPTION_CLOSEST);
            Log.i(TAG, "bitmap---i: " + i/1000);

            String path = Environment.getExternalStorageDirectory() + "/bitmap/" + i + ".png";
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                Log.i(TAG, "i: " + i/1000);
            } catch (Exception e) {
                Log.i(TAG, "Error: " + i/1000);
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            bitmap.recycle();
        }
    }*/

    // 下载完成
    private void processLoadComplete(Message msg) {
        // 更新ICON
        //iconBtnAdapter.notifyDataSetChanged();
        // 视频添加配乐线程
        new AsyncEditVideoWithMusic().execute(curIncMusicIndex);
    }

    // 下载出错
    private void processLoadError(Message msg) {
        Toast.makeText(VideoEditActivity.this, "资源下载异常...", Toast.LENGTH_SHORT).show();
    }

}
