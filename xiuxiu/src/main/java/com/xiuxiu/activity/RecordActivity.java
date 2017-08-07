package com.xiuxiu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiuxiu.R;
import com.xiuxiu.adapter.AccSongDataAdapter;
import com.xiuxiu.adapter.TemplateButtonAdapter;
import com.xiuxiu.dialog.DialogGuide;
import com.xiuxiu.fragment.PagerFragment;
import com.xiuxiu.model.AccSongInfo;
import com.xiuxiu.model.TemplateVideoInfo;
import com.xiuxiu.model.VideoInfo;
import com.xiuxiu.selector.Matisse;
import com.xiuxiu.selector.MimeType;
import com.xiuxiu.selector.engine.impl.GlideEngine;
import com.xiuxiu.service.MusicPlayerService;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.AVRecord;
import com.xiuxiu.util.AccSongInfoParse;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.AccSongLyricsView;
import com.xiuxiu.view.CameraView;
import com.xiuxiu.view.RecordProgressView;
import com.xiuxiu.widget.HorizontalScrollViewPager;
import com.xiuxiu.widget.SearchClearEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// 录制
public class RecordActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, OnPreparedListener,
        OnCompletionListener,
        OnErrorListener, View.OnClickListener {
    private static final String TAG = "RecordActivity";

    /*--界面元素及调用逻辑-------------------------------------------------------------*/
    // 是否开启闪光灯
    private boolean isFlashLightOn = false;
    // 是否开启镜头网格
    private boolean isLensGridOn = false;
    // 是否打开模板面板
    private boolean isTemplateOn = false/*, isTxtTemplateSelected = false*/;
    // 音乐播放是否停止
    private volatile boolean isMusicPlayingStop = false;
    // 默认为前置摄像头
    private int cameraIndex = XConstant.CAMERA_FRONT;
    // 下一步/取消
    private ImageView recordCancelBtn = null;
    // 镜头切换/闪光灯/网格/对焦
    private RelativeLayout lensGridLayout = null, recordNextBtn = null, video_delete = null, videoLocalUpload = null;
    private LinearLayout flashlightBtn = null, lensGridBtn = null, lensFocusBtn = null;
    private ImageView flashlight = null, accSongBtn = null, lensConfigBtn = null, lensFlipBtn = null;
    // 录制进度条
    private RecordProgressView recordProgressView = null;
    // 模板/主题/滤镜
    private int curClassId = XConstant.INVALID_VALUE, curTemplateIndex = 1;
    private LinearLayout templateBtn = null, accSongBtnDub = null;
    private View recordAVBtn = null;
    private CameraView cameraView = null;
    private TemplateButtonAdapter iconBtnAdapter = null;
    private GridView iconBtnGridView = null;
    private LinearLayout iconBtnLayout = null;
    // 伴奏歌词/弹出窗体/歌曲列表
    private TextView tvMusicSongClose = null, video_time = null;
    public PopupWindow accSongListPopupWindow = null, recLensConfigPopupWindow = null;
    private LinearLayout accSongListPopupWindowLayout = null, recLensConfigPopupWindowLayout = null;
    //    private ListView accSongListView = null;
    private List<AccSongInfo> accSongListData = null;
    private AccSongDataAdapter accSongDataAdapter = null;
    private AccSongLyricsView accSongLyricsView = null;
    // 模板
    private List<TemplateVideoInfo> txtTemplateListData = null;
    // 视频模板
    private volatile boolean isSurfaceTextureAvailable = false;
    private volatile boolean isPlayerPreparedFinished = false;
    private int recordMode = XConstant.RECORD_MODE_NOR;
    private int videoFrameRate = 15;// 默认15帧每秒
    private static final int LOCALVIDEO_REQUEST_CODE = 666;
    private String curVideoTemplatePath = null;
    private MediaPlayer mediaPlayer = null;
    private Surface videoTemplateSurface = null;
    private TextureView videoTemplatePreview = null;
    private RelativeLayout videoTemplateLayout = null, previewLayout = null;
    private Dialog countDownTipsDlg = null;// 倒计时提示页面
    private ImageView countDownImageView = null;
    private TextView countDownTextView = null;
    private CountDownAnimationDrawable animationDrawable = null;// 倒计时动画

    /*--音视频播放、录制及存储----------------------------------------------------------*/
    // 保存当前选中的伴奏歌曲名,未选伴奏时需置为""
    private String curAccSongPath = "", curSongLyrics = "";
    private AVRecord avRecord = null;
    private Rect touchRecordRect = null;
    private MusicPlayerReceiver musicPlayerReceiver = null;
    /*---------------------新手引导------------------------------------------------------*/
    private Dialog beginnersGuideDlg = null;//引导页首页
    private RelativeLayout guideLayout = null;//引导标题栏
    private ImageView guideImageView = null;//引导图片
    private ImageView stickerBtn;//贴纸
    private RelativeLayout overlapViewLayout = null;//覆盖,遮挡一部分帧的Layout
    private boolean bGuide = false;//是否是新手引导操作标志量

    public static List<VideoInfo> sysVideoList = null;// 视频信息集合
    private VideoInfo info;

    public enum GuideResourceIndex {INITIALIZES_GUIDE, FIRST_GUIDE, SECOND_GUIDE, THIRD_GUIDE, FOURTH_GUIDE, FIFTH_GUIDE}

    private static final int REQUEST_LOCAL_VIDEO = 111;

    private GuideResourceIndex lastGRIndex = GuideResourceIndex.INITIALIZES_GUIDE;//初始化索引页

    private TabLayout tlDubMusicSongTab;
    private SearchClearEditText etDubMusicSongSearch;
    private HorizontalScrollViewPager hvDubMusicSongViewpager;
    private List<PagerFragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private int recordTime = 0;
    private boolean recordEnable = true;

    private AlertDialog builder;
    private AlertDialog audioBuilder;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            processRecordMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 音视频录制模块
        avRecord = new AVRecord(RecordActivity.this);

        // 视频模板播放器
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        // 注册广播接收>>接收音乐时长及播放进度
        musicPlayerReceiver = new MusicPlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(XConstant.MUSIC_DURATION);
        filter.addAction(XConstant.MUSIC_CURRENT_TIME);
        filter.addAction(XConstant.MUSIC_COMPLETION);
        this.registerReceiver(musicPlayerReceiver, filter);

        if (bGuide)//创建新手引导
        {
            createBeginnersGuideDialog();
        }

        sysVideoList = new ArrayList<VideoInfo>();
        initView();
//        setVideoList();
    }

    @Override
    public void onClick(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            int i = v.getId();
                            if (i == R.id.video_local_upload) {
                                Matisse.from(RecordActivity.this)
                                        .choose(MimeType.ofVideoMp4())
//                                        .theme(R.style.Matisse_Dracula)
                                        .showSingleMediaType(true)
                                        .countable(true)
                                        .maxSelectable(1)
                                        .imageEngine(new GlideEngine())
                                        .forResult(REQUEST_LOCAL_VIDEO);

                            }
                        } else {
                            Toast.makeText(RecordActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    // 发送录制msg
    public void sendRecordMessage(int msg) {
        handler.sendEmptyMessage(msg);
    }

    // 发送录制消息
    public void sendRecordMessage(int msgCode, String msgInfo) {
        Message msg = handler.obtainMessage();
        msg.what = msgCode;
        msg.obj = msgInfo;
        handler.sendMessage(msg);
    }

    private boolean isFirstRecord = true;
    private boolean recordStatus = true;

    // 处理录制msg
    public void processRecordMessage(Message msg) {
        switch (msg.what) {
            case XConstant.RECORD_STATUS_START:
                // 开始录制
//                recordNextBtn.setSelected(true);
                video_delete.setVisibility(View.VISIBLE);
                videoLocalUpload.setVisibility(View.GONE);
                recordNextBtn.setVisibility(View.VISIBLE);
                stickerBtn.setVisibility(View.GONE);
                accSongBtn.setVisibility(View.GONE);

                if (isFirstRecord) {
                    isFirstRecord = false;
                    if (!TextUtils.isEmpty(curAccSongPath)) {
                        Intent playerStart = new Intent(RecordActivity.this, MusicPlayerService.class);
                        playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_RESTART);
                        startService(playerStart);
                        CacheUtils.setString(this, XConstant.CURRENT_SELECT_MUSIC, "");
                    }
                } else {
                    changePlayerStatus(XConstant.PLAYER_STATUS_RESUME);
                }
                avRecord.startRecord();
                recordAVBtn.setSelected(true);
                recordProgressView.setAVRecording(true);
                recordProgressView.updateRefreshTime();
                if (bGuide)//新手引导标语提示 -->继续按住
                {
                    if (lastGRIndex == GuideResourceIndex.INITIALIZES_GUIDE)
                        setGuideImageViewResource(GuideResourceIndex.FIRST_GUIDE);
                    else if (lastGRIndex == GuideResourceIndex.THIRD_GUIDE)
                        setGuideImageViewResource(GuideResourceIndex.FOURTH_GUIDE);
                }
                break;
            case XConstant.RECORD_STATUS_STOP:
                // 停止录制
                recordNextBtn.setSelected(false);
                changePlayerStatus(XConstant.PLAYER_STATUS_PAUSE);
                avRecord.stopRecord();
                recordAVBtn.setSelected(false);
                recordProgressView.setAVRecording(false);
                recordProgressView.addBreakPoints();

                // 文字模板被选中,则进行模板换句
//			if (isTxtTemplateSelected)
//			{
//				accSongLyricsView.updateTxtTemplate(RecordActivity.this);
//			}

                if (bGuide)//新手引导 	stop消息中处理标语切换提示
                {
                    //录制时长超过3s,且上一个提示标语为FIRST_GUIDE("继续按住),则切换为SECOND_GUIDE("松开暂停")
                    if (lastGRIndex == GuideResourceIndex.FIRST_GUIDE && getCurRecordTime() > XConstant.RECORD_NOR_MIN_TIME) {
                        setGuideImageViewResource(GuideResourceIndex.SECOND_GUIDE);
                    } else if (lastGRIndex == GuideResourceIndex.SECOND_GUIDE)//若已经为SECOND_GUIDE,则切换到THIRD_GUIDE("换个场景")
                    {
                        setGuideImageViewResource(GuideResourceIndex.THIRD_GUIDE);
                    }
                }
                break;
            case XConstant.RECORD_STATUS_FINISH:
                // 完成录制
                avRecord.stopRecord();
                avRecord.finishRecord();
                if (recLensConfigPopupWindow != null) {
                    // 关闭镜头设置菜单
                    recLensConfigPopupWindow.dismiss();
                }
                break;
            case XConstant.RECORD_STATUS_ERROR:
                // 录制异常
                new AlertDialog.Builder(this).
                        setCancelable(false).
                        setMessage(msg.obj.toString()).
                        setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 返回至指定tab页
                                finish();
                            }
                        }).show();
                break;
            case XConstant.RECORD_STATUS_PIP:
                // 调整为画中画显示方式
                recordMode = XConstant.RECORD_MODE_PIP;
                int frameWidth = (int) (Util.getScreenWidth(this) * XConstant.PIP_MAPPING_COEF);
                int frameHeight = (int) (frameWidth * 4 / 3);// 帧宽高比为4:3
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameHeight);
                previewLayout.setLayoutParams(layoutParams);
                previewLayout.setX(0);
                previewLayout.setY(Util.getScreenWidth(this) - frameHeight);
                cameraView.setLayoutParams(layoutParams);
                cameraView.setX(0);
                cameraView.setY(0);
                mediaPlayer.pause();
                countDownImageView.setVisibility(View.VISIBLE);
                countDownTextView.setVisibility(View.GONE);
                animationDrawable.start();
                isPlayerPreparedFinished = true;
                break;
            case XConstant.RECORD_COUNTDOWN_FINISHED:
                // 开始画中画录制
//                if (isPlayerPreparedFinished) {
//                    mediaPlayer.start();
//                    sendRecordMessage(XConstant.RECORD_STATUS_START);
//                }
                sendRecordMessage(XConstant.RECORD_STATUS_START);
                // 停止倒计时动画
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                countDownTipsDlg.dismiss();
                break;
            case XConstant.RECORD_TIME:
                updateRecordTime();
                handler.sendEmptyMessageDelayed(XConstant.RECORD_TIME, 10);
                break;
            default:
                break;
        }
    }


    private void updateRecordTime() {
        int curRecordTime = recordProgressView.getCurRecordTime();
        float f = (float) curRecordTime / 1000;
        int second = Math.round(f);
//        Log.d(TAG, "updateRecordTime: " + curRecordTime + "-----" + f+"----"+second);
        if (second < 10) {
            video_time.setText("00:0" + second);
        } else {
            video_time.setText("00:" + second);
        }
    }

    public void initView() {

        int frameWidth = Util.getScreenWidth(this);
        int frameHeight = (int) (frameWidth * 4 / 3);// 帧宽高比为4:3
//        int frameHeight = Util.getScreenHeight(this);
        RelativeLayout.LayoutParams layoutParams = null;

        video_time = (TextView) findViewById(R.id.video_time);

        //本地上传
        videoLocalUpload = (RelativeLayout) findViewById(R.id.video_local_upload);
        videoLocalUpload.setOnClickListener(this);

        //回删功能
        video_delete = (RelativeLayout) findViewById(R.id.video_delete);
        video_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avRecord.isAVRecording()) {
                    Util.showTextToast(RecordActivity.this, "请先停止录制！");
                    return;
                }
                recordProgressView.deleteBreakPoints();
                Intent playerStart = new Intent(RecordActivity.this, MusicPlayerService.class);
                playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_SEEK);
                playerStart.putExtra("MUSIC_PLAYING_SEEK", recordProgressView.getCurRecordTime());
                startService(playerStart);
                updateRecordTime();
                avRecord.deleteDatAtBreakPoint();
                if (recordProgressView.getCurRecordTime() == 0) {
                    video_delete.setVisibility(View.GONE);
                    videoLocalUpload.setVisibility(View.VISIBLE);
                    recordNextBtn.setVisibility(View.INVISIBLE);
                    stickerBtn.setVisibility(View.VISIBLE);
                    accSongBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        // 添加录制进度条
        RelativeLayout recordProgressLayout = (RelativeLayout) this.findViewById(R.id.recordProgressLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, (int) (frameWidth * 0.015));
        recordProgressView = new RecordProgressView(this, RecordActivity.this, frameWidth, (int) (frameWidth * 0.015));
        recordProgressLayout.addView(recordProgressView, layoutParams);
        recordProgressView.setAVRecording(false);
        recordProgressView.setProgressParams(XConstant.RECORD_NOR_MAX_TIME, XConstant.RECORD_ACC_MAX_TIME);

        // 视频模板预览层
        videoTemplateLayout = (RelativeLayout) this.findViewById(R.id.videoTemplateLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameHeight);
        videoTemplateLayout.setLayoutParams(layoutParams);
        videoTemplateLayout.setVisibility(View.INVISIBLE);
        videoTemplatePreview = (TextureView) this.findViewById(R.id.videoTemplatePreview);
        videoTemplatePreview.setScaleX(1.00001f);
        videoTemplatePreview.setSurfaceTextureListener(this);

        // 添加帧预览显示区>>宽/高均为当前手机屏幕分辨率宽度
        previewLayout = (RelativeLayout) this.findViewById(R.id.previewLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameHeight);
        cameraView = new CameraView(this, RecordActivity.this);
        cameraView.setSaveFrameCallback(avRecord);
        previewLayout.addView(cameraView, layoutParams);
        // 触屏录制响应区域
        touchRecordRect = new Rect();

        // 镜头网格
        lensGridLayout = (RelativeLayout) this.findViewById(R.id.lensGridLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameWidth);
        lensGridLayout.setLayoutParams(layoutParams);

        // 遮挡帧下部区域
        overlapViewLayout = (RelativeLayout) this.findViewById(R.id.overlapViewLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameHeight - frameWidth);
        View overlapView = new View(this);
        overlapViewLayout.addView(overlapView, layoutParams);

        // 伴奏歌曲歌词页面
        RelativeLayout accSongLyricsLayout = (RelativeLayout) this.findViewById(R.id.accSongLyricsLayout);
        layoutParams = new RelativeLayout.LayoutParams(frameWidth, (int) (frameWidth * 0.25));
        accSongLyricsView = new AccSongLyricsView(this, frameWidth, (int) (frameWidth * 0.15));
        accSongLyricsLayout.addView(accSongLyricsView, layoutParams);

        // 伴奏列表
//        accSongListData = new ArrayList<AccSongInfo>();
//        accSongDataAdapter = new AccSongDataAdapter(RecordActivity.this, accSongListData);
//        accSongListPopupWindowLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout
//                .acc_song_list_popupwindow_layout, null);
//        accSongListView = (ListView) accSongListPopupWindowLayout.findViewById(R.id.accSongListview);
        /*LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(frameWidth, (int) (frameHeight * 0.9));
        accSongListView.setLayoutParams(lparams);*/
       /* tlDubMusicSongTab = (TabLayout) accSongListPopupWindowLayout.findViewById(R.id.tl_dub_music_song_tab);
        hvDubMusicSongViewpager = (HorizontalScrollViewPager) accSongListPopupWindowLayout.findViewById(R.id.hv_dub_music_song_viewpager);
        etDubMusicSongSearch = (SearchClearEditText) accSongListPopupWindowLayout.findViewById(R.id.et_dub_music_song_search);
        tvMusicSongClose = (TextView) accSongListPopupWindowLayout.findViewById(R.id.tv_music_song_close);
        tvMusicSongClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭弹出窗口
                accSongListPopupWindow.dismiss();
            }
        });*/

        // 伴奏列表弹出窗体
        accSongListPopupWindow = new PopupWindow(this);
        accSongListPopupWindow.setWidth(frameWidth);
        accSongListPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        accSongListPopupWindow.setFocusable(true);
        accSongListPopupWindow.setOutsideTouchable(true);
        accSongListPopupWindow.setAnimationStyle(R.style.accsongPopupWindowAnimation);
        accSongListPopupWindow.setContentView(accSongListPopupWindowLayout);

        // 镜头设置弹出窗体
        recLensConfigPopupWindowLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout
                .record_lensconfig_popupwnd_layout, null);
        recLensConfigPopupWindow = new PopupWindow(this);
        recLensConfigPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        recLensConfigPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        recLensConfigPopupWindow.setFocusable(true);
        recLensConfigPopupWindow.setOutsideTouchable(true);
        recLensConfigPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        recLensConfigPopupWindow.setContentView(recLensConfigPopupWindowLayout);
        recLensConfigPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                lensConfigBtn.setSelected(false);
            }
        });


        // 初始化水平滚动图标按钮
        iconBtnAdapter = new TemplateButtonAdapter(this);
        iconBtnLayout = (LinearLayout) this.findViewById(R.id.iconBtnLayout);
        iconBtnGridView = (GridView) this.findViewById(R.id.iconBtnGridView);
        iconBtnGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                if (index == 0) {
                    // 重置参数
                    resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);
                    iconBtnAdapter.changeItem(1);
                    iconBtnAdapter.notifyDataSetChanged();
                    if (curTemplateIndex != 1) {
                        curTemplateIndex = 1;
                        onClickTemplateIconBtn(1);
                    }

                    // 本地添加
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, LOCALVIDEO_REQUEST_CODE);
                } else {
                    // 设置选中项并更新列表
                    iconBtnAdapter.changeItem(index);
                    iconBtnAdapter.notifyDataSetChanged();

                    if (curTemplateIndex != index) {
                        curTemplateIndex = index;
                        onClickTemplateIconBtn(index);
                    }
                }
            }
        });

        // 下一步按钮
        recordNextBtn = (RelativeLayout) this.findViewById(R.id.recordNextBtn);
        updateRecordNextBtn(false);
        recordNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordEnable) return;
                recordEnable = false;
                if (avRecord.isAVRecording()) {
//                    Util.showTextToast(RecordActivity.this, "请先停止录制！");
                    recordAVBtn.setSelected(false);
                    recordProgressView.setAVRecording(false);
                    sendRecordMessage(XConstant.RECORD_STATUS_FINISH);
                } else {
                    avRecord.finishRecord();
                }
            }
        });

        // 取消按钮-->返回指定tab页
        recordCancelBtn = (ImageView) this.findViewById(R.id.recordCancelBtn);
        recordCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 摄像头切换
        lensFlipBtn = (ImageView) this.findViewById(R.id.lensFlipBtn);
        lensFlipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avRecord.isAVRecording()) {
                    Util.showTextToast(RecordActivity.this, "请先停止录制！");
                    return;
                }

                cameraIndex = (cameraIndex == XConstant.CAMERA_BACK) ? XConstant.CAMERA_FRONT : XConstant.CAMERA_BACK;
                if (!cameraView.switchCamera(cameraIndex)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                    builder.setMessage("检测到您只有一个摄像头,无法完成切换...");
                    builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                    cameraIndex = XConstant.CAMERA_BACK;
                }

                // 前置摄像头时需关闭闪光灯
                if ((cameraIndex == XConstant.CAMERA_FRONT) && isFlashLightOn) {
                    lensConfigBtn.setSelected(false);
                    cameraView.switchFlashLight(false);
                }

                // 前置启用闪光灯按钮,后置禁用闪光灯按钮
                // flashlightBtn.setEnabled(cameraIndex == XConstant.CAMERA_BACK);
            }
        });

        // 闪光灯
        lensConfigBtn = (ImageView) findViewById(R.id.lensConfigBtn);
        lensConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avRecord.isAVRecording()) {
                    Util.showTextToast(RecordActivity.this, "请先停止录制！");
                    return;
                }
//                recLensConfigPopupWindow.showAsDropDown(lensConfigBtn);
                // 打开后置摄像头时>>可以打开闪光灯
                if (cameraIndex == XConstant.CAMERA_BACK) {
                    // 闪光灯开/关并更新按钮状态
                    isFlashLightOn = !isFlashLightOn;
                    lensConfigBtn.setSelected(isFlashLightOn);

                    if (!cameraView.switchFlashLight(isFlashLightOn)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                        builder.setMessage("闪光灯开启失败...");
                        builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                        // 闪光灯开/关并更新按钮状态
                        isFlashLightOn = !isFlashLightOn;
                        lensConfigBtn.setSelected(isFlashLightOn);
                    }
                } else {
                    Util.showTextToast(RecordActivity.this, "请先开启后置摄像头");
                }
            }
        });

        // 闪光灯（弃用）
        flashlightBtn = (LinearLayout) recLensConfigPopupWindowLayout.findViewById(R.id.flashlightBtn);
        flashlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开后置摄像头时>>可以打开闪光灯
                if (cameraIndex == XConstant.CAMERA_BACK) {
                    // 闪光灯开/关并更新按钮状态
                    isFlashLightOn = !isFlashLightOn;
                    flashlightBtn.setSelected(isFlashLightOn);

                    if (!cameraView.switchFlashLight(isFlashLightOn)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                        builder.setMessage("闪光灯开启失败...");
                        builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                        // 闪光灯开/关并更新按钮状态
                        isFlashLightOn = !isFlashLightOn;
                        flashlightBtn.setSelected(isFlashLightOn);
                    }
                }
            }
        });

        // 镜头网格
        lensGridBtn = (LinearLayout) recLensConfigPopupWindowLayout.findViewById(R.id.lensGridBtn);
//        lensGridBtn.setVisibility(View.GONE);
        lensGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLensGridOn = !isLensGridOn;
                // 镜头网格开/关并更新按钮状态
                lensGridBtn.setSelected(isLensGridOn);
//                lensGridLayout.setVisibility(isLensGridOn ? View.VISIBLE : View.GONE);
                // lensGridLayout.setFocusable(false);
            }
        });

        // 镜头对焦
        lensFocusBtn = (LinearLayout) recLensConfigPopupWindowLayout.findViewById(R.id.lensFocusBtn);
        lensFocusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开后置摄像头时>>支持镜头对焦
                if (cameraIndex == XConstant.CAMERA_BACK) {
                    cameraView.autoFocus();
                }
            }
        });

        // 模板
        txtTemplateListData = new ArrayList<TemplateVideoInfo>();
        templateBtn = (LinearLayout) this.findViewById(R.id.templateBtn);
        templateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重置参数
                resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);

                if (curClassId != XConstant.CLASS_ID_TEMPLATE) {
                    // 加载文字模板信息
                    if (txtTemplateListData.size() == 0) {
                        TemplateVideoInfo tmpVideoInfo = null;
                        tmpVideoInfo = new TemplateVideoInfo();
                        tmpVideoInfo.setSelected(false);
                        tmpVideoInfo.setIconId(R.drawable.icon_add_video);
                        tmpVideoInfo.setIconText("本地添加");
                        txtTemplateListData.add(tmpVideoInfo);
                        tmpVideoInfo = new TemplateVideoInfo();
                        tmpVideoInfo.setSelected(false);
                        tmpVideoInfo.setIconId(R.drawable.icon_effects_null);
                        tmpVideoInfo.setIconText("无模板");
                        txtTemplateListData.add(tmpVideoInfo);

                        // 视频模板资源
                        int[] resId = {
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini, R.drawable.video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini, R.drawable
                                .video_chhwz_mini,
                                R.drawable.video_chhwz_mini, R.drawable.video_chhwz_mini
                        };

                        String[] videoIconName = {
                                "吃货会伪装", "插头上锁", "大话西游", "戴帽的真相", "荡秋千", "单身狗的脾气", "大头剧主演", "辟邪神器", "好闺蜜", "长寿好啊", "男女胃的区别", "你先吃", "胖也可爱",
                                "神变废为宝", "说多了都是泪", "输氧管", "脱衣服比赛", "猥琐便利贴", "升职加薪", "像鸡一样", "校服的诱惑", "胸没了", "业界良心", "应该有码", "用绳命尿尿",
                                "这也有wifi", "自己老了"
                        };

                        String[] videoFileName = {
                                "chhwz", "chatouss", "dahuaxiyou", "dongtiandmz", "dqq", "dsgdpq", "dtjzy", "bxsq", "hgm", "kanbingbx",
                                "nnwdqb", "nxc", "pyka",
                                "sbfwb", "shuoduoledoushilei", "syg", "tuoyifubs", "weisuobianlitie", "wwmxd_jx", "xiangjiyiyang",
                                "xiaofudeyouhuo", "xiongmeile",
                                "yejieliangxin", "yinggaiyouma", "yongshengmingniaoniao", "zheyeyouwifi", "zijilaole"
                        };

                        for (int i = 0; i < resId.length; i++) {
                            tmpVideoInfo = new TemplateVideoInfo();
                            String txtTemplateFilePath = XConstant.RES_TMPLYRICS_FILE_PATH + videoFileName[i] + ".lr";
                            String videoFilePath = XConstant.RES_VIDEO_FILE_PATH + videoFileName[i] + ".mp4";
                            tmpVideoInfo.setIconText(videoIconName[i]);
                            tmpVideoInfo.setVideoName(videoFileName[i] + ".mp4");
                            tmpVideoInfo.setVideoFilePath(videoFilePath);
                            tmpVideoInfo.setLyricsFilePath(txtTemplateFilePath);
                            tmpVideoInfo.setSelected(false);
                            tmpVideoInfo.setIconId(resId[i]);
                            txtTemplateListData.add(tmpVideoInfo);
                        }
                    }// if

                    // 不同类别切换时加载数据
                    initHScrollIconButton(XConstant.CLASS_ID_TEMPLATE);
                    iconBtnGridView.setVisibility(View.VISIBLE);
                    templateBtn.setSelected(true);
                    isTemplateOn = true;
                    // isTxtTemplateSelected = false;
                    videoTemplateLayout.setVisibility(View.VISIBLE);
                } else {
                    // 相同类别点选时仅作开关处理
                    isTemplateOn = !isTemplateOn;
                    templateBtn.setSelected(isTemplateOn);
                    if (isTemplateOn) {
                        // 延迟加载文字模板信息
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                while (!isMusicPlayingStop) {
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                        // e.printStackTrace();
                                    }
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                onClickTemplateIconBtn(curTemplateIndex);
                            }
                        }.execute();

                        iconBtnGridView.setVisibility(View.VISIBLE);
                    } else {
                        // 重置摄像头预览
                        resetCameraPreview();
                        iconBtnGridView.setVisibility(View.GONE);
                    }
                }
            }
        });

        // 录制
        recordAVBtn = this.findViewById(R.id.recordAVBtn);

        //触摸录制
//        recordAVBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent e) {
//                switch (e.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        recordAVBtn.setSelected(false);
//                        sendRecordMessage(XConstant.RECORD_STATUS_STOP);
//                        break;
//                    case MotionEvent.ACTION_DOWN:
//                        recordAVBtn.setSelected(true);
//                        sendRecordMessage(XConstant.RECORD_STATUS_START);
//                        break;
//                    default:
//                        break;
//                }
//
//                return true;
//            }
//        });

        //点击录制
        recordAVBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordStatus) {
                    recordAVBtn.setSelected(true);
                    sendRecordMessage(XConstant.RECORD_STATUS_START);
                    handler.sendEmptyMessage(XConstant.RECORD_TIME);
                    recordStatus = false;
                    video_delete.setVisibility(View.VISIBLE);
                    videoLocalUpload.setVisibility(View.GONE);
                } else {
                    recordStatus = true;
                    recordAVBtn.setSelected(false);
                    handler.removeMessages(XConstant.RECORD_TIME);
                    sendRecordMessage(XConstant.RECORD_STATUS_STOP);
                }
            }
        });

        //贴纸
        stickerBtn = (ImageView) this.findViewById(R.id.stickerBtn);

        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                boolean isSticker = CacheUtils.getBooleanFirst(RecordActivity.this, XConstant.GUIDE_STICKER);
                Log.e("RecordActivity", isSticker + "----isSticker---");
                if (isSticker) {
                    DialogGuide dialogGuideSticker = new DialogGuide(RecordActivity.this, R.drawable.tip_sticker);
                    dialogGuideSticker.show();
                    CacheUtils.setBooleanFirst(RecordActivity.this, XConstant.GUIDE_STICKER, false);
                } else {
                    ToolUtils.showToast(RecordActivity.this, "暂未开通");
                }
            }
        });


        // 伴奏
        accSongBtn = (ImageView) this.findViewById(R.id.accSongBtn);

        accSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (avRecord.isAVRecording()) {
                    Util.showTextToast(RecordActivity.this, "请先停止录制！");
                    return;
                }*/
                // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                boolean isMusic = CacheUtils.getBooleanFirst(RecordActivity.this, XConstant.GUIDE_MUSIC);
                Log.e("RecordActivity", isMusic + "----isMusic---");
                if (isMusic) {
                    DialogGuide dialogGuideMusic = new DialogGuide(RecordActivity.this, R.drawable.tip_music);
                    dialogGuideMusic.show();

                    CacheUtils.setBooleanFirst(RecordActivity.this, XConstant.GUIDE_MUSIC, false);
                } else {
                    resetRecord();
                    Intent intent = new Intent(RecordActivity.this, RecordMusicActivity.class);
                    intent.putExtra("opr", 0);
                    intent.putExtra("musictitle", "音乐");
                    intent.putExtra("hinttitle", "搜索音乐");
                    intent.putExtra("selecttitle", "您暂未选择音乐");
                    startActivityForResult(intent, XConstant.MUSIC_ACTIVITY_RESULT);
                    // 重置参数
                    resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);
                    // 重置摄像头预览
                    resetCameraPreview();
//                getDubTypeData();


                    // 动态修改模板显示状态
                    isTemplateOn = false;
                    // isTxtTemplateSelected = false;
                    templateBtn.setSelected(false);
                    iconBtnGridView.setVisibility(View.GONE);

//                accSongListPopupWindow.showAtLocation(accSongBtn, Gravity.BOTTOM, 0, 0);// 重置参数
                    resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);
                    // 重置摄像头预览
                    resetCameraPreview();

                    // 加载歌曲信息
                    // 动态修改模板显示状态
                    isTemplateOn = false;
                    // isTxtTemplateSelected = false;
                    templateBtn.setSelected(false);
                    iconBtnGridView.setVisibility(View.GONE);

                    accSongListPopupWindow.showAtLocation(accSongBtn, Gravity.BOTTOM, 0, 0);
                }

            }
        });

        if (bGuide) {
            setGuideEnableBtn();
        }

        clearFile(XConstant.VIDEO_FILE_PATH);
    }// onActivityCreated()

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case XConstant.CHECK_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    resetRecord();
//                    resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);
//                    avRecord.initRecord();
//                    resetCameraPreview();
//                    if (cameraView != null) {
//                        Log.d(TAG, "onRequestPermissionsResult: ");
//                        cameraView.startPreview();
//                    }
//                }
//        }
//    }

    private String url;
    private List<Uri> mUris;
    private List<String> mPaths;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCAL_VIDEO && resultCode == RESULT_OK) {

            mUris = Matisse.obtainResult(data);
            mPaths = Matisse.obtainPathResult(data);
            for (int i = 0; i < mPaths.size(); i++) {
                url = mPaths.get(i);
                Log.e("onActivityResult", url + "---url--");
            }
            loadVideos();
        }
        switch (resultCode) {
            case LOCALVIDEO_REQUEST_CODE:
//                if (data != null) {
//                    Uri videoUri = data.getData();
//                    String videoFilePath = getLocalVideoPath(this, videoUri);
//                    if (pipModePrepared(videoFilePath)) {
//                        // 播放视频
//                        playerVideoTemplate(videoFilePath);
//                    } else {
//                        if (countDownTipsDlg != null) {
//                            countDownTipsDlg.dismiss();
//                        }
//                        Toast.makeText(this, "无法打开该视频....", Toast.LENGTH_SHORT).show();
//                    }
//                }
                resetRecord();
                resetRecordParams(XConstant.RECORD_NOR_MAX_TIME);
                avRecord.initRecord();
                resetCameraPreview();
                break;

            case XConstant.MUSIC_ACTIVITY_RESULT:
                //TODO
                //返回歌曲名称
                String title = data.getExtras().getString("title");
                if (!TextUtils.isEmpty(title)) {
                    accSongBtn.setSelected(true);
                    String lyricsFilePath = XConstant.RES_ACCLYRICS_FILE_PATH + title + ".lr";
                    String songFilePath = XConstant.RES_ACC_MUSIC_FILE_PATH + title + ".mp3";
                    if (!new File(songFilePath).exists()) {
                        songFilePath = XConstant.RES_MUSIC_FILE_PATH + title + ".mp3";
                    }
                    AccSongInfoParse.getInstance().parseLyricsInfo(lyricsFilePath);
                    List<AccSongInfoParse.LyricsLine> listLyricsLine = AccSongInfoParse.getInstance().getListLyricsLine();
                    setAccSongLyricsLine(listLyricsLine);
                    setCurAccSongPath(songFilePath);
                    setCurSongLyrics(lyricsFilePath);
                    Intent playerStart = new Intent(RecordActivity.this, MusicPlayerService.class);
                    playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
                    playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
                    playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_SINGING);
                    startService(playerStart);
                }
                break;
            default:
                break;
        }
    }

    private void loadVideos() {
        if (url != null && mPaths.size() > 0) {
            Log.d("loadVideo", "mPaths: " + mPaths.size());
            Intent intent = new Intent(RecordActivity.this, VideoLocalActivity.class);
            intent.putExtra("filePath", url);
            startActivity(intent);
        }
    }


    private void setVideoList() {
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};


        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DISPLAY_NAME};

        Cursor cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor == null) {
            Toast.makeText(RecordActivity.this, "没有找到可播放视频文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                info = new VideoInfo();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    info.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                info.setPath(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                info.setTitle(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                info.setDisplayName(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                info.setMimeType(cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));

                sysVideoList.add(info);
            } while (cursor.moveToNext());
        }
    }


    //删除指定目录下文件
    private void clearFile(String folderName) {
        File[] files = new File(folderName).listFiles();
        for (File file : files) {
            file.delete();
        }
    }


    // 初始化水平滚动图标按钮
    private void initHScrollIconButton(int classId) {
        // 记录当前选择的类别id
        curClassId = classId;
        // 重置按钮状态
        templateBtn.setSelected(false);

        txtTemplateListData.get(1).setSelected(true);
        iconBtnAdapter.setIconBtnList(txtTemplateListData);

        int itemCount = txtTemplateListData.size();
        // 设置布局并映射数据
        float mapCoef = Util.getScreenWidth(this) / 480.0f;
        LayoutParams layoutParams = iconBtnLayout.getLayoutParams();
        layoutParams.width = (int) (itemCount * 100 * mapCoef);
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        iconBtnLayout.setLayoutParams(layoutParams);
        iconBtnGridView.setNumColumns(itemCount);
        iconBtnGridView.setAdapter(iconBtnAdapter);
    }

    // 播放模板视频
    public void playerVideoTemplate(final String videoFilePath) {
        isPlayerPreparedFinished = false;
        videoTemplateLayout.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (!isSurfaceTextureAvailable) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(videoFilePath);
                    mediaPlayer.setSurface(videoTemplateSurface);
                    mediaPlayer.prepareAsync();
                    // mediaPlayer.setLooping(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 更新进度条参数
        int totalRecordTime = mp.getDuration() - 500;// -500,使得总录制时长比当前视频时长少500毫秒,以确保摄像头采集数据帧数比视频总帧数少
        totalRecordTime = totalRecordTime < XConstant.RECORD_PIP_MAX_TIME ? totalRecordTime : XConstant.RECORD_PIP_MAX_TIME;
        recordProgressView.setProgressParams(totalRecordTime, XConstant.RECORD_PIP_MAX_TIME);
        mp.seekTo(0);
        mp.start();

        handler.sendEmptyMessageDelayed(XConstant.RECORD_STATUS_PIP, 1000 / videoFrameRate);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        videoTemplateSurface = new Surface(arg0);
        isSurfaceTextureAvailable = true;
        // Log.d("dd_cc_dd", "...Available...");
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        if (curTemplateIndex > 1) {
            accSongLyricsView.compTxtTemplate(RecordActivity.this, getCurRecordTime());
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        // Log.d("dd_cc_dd", "..Destroyed..");
        isSurfaceTextureAvailable = false;
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {
    }

    // 是否符合触屏录制
    public boolean isTouchScreenRecord(int posX, int posY) {
        int[] location = new int[2];
        videoTemplateLayout.getLocationInWindow(location);
        touchRecordRect.left = location[0];
        touchRecordRect.right = touchRecordRect.left + videoTemplateLayout.getWidth();
        touchRecordRect.top = location[1];
        touchRecordRect.bottom = touchRecordRect.top + videoTemplateLayout.getWidth();

        return touchRecordRect.contains(posX, posY);
    }

    // 停止并释放摄像头
    public void stopCamera() {
        if (cameraView != null) {
            cameraView.stopCamera();
        }
    }

    // 切换播放状态
    public void changePlayerStatus(int playerStatus) {
        // 歌曲播放
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtra("MUSIC_PLAYER_STATUS", playerStatus);
        this.startService(intent);

        // 视频模板播放
        if (isTemplateOn && mediaPlayer != null) {
            if (playerStatus == XConstant.PLAYER_STATUS_RESUME) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else if (playerStatus == XConstant.PLAYER_STATUS_PAUSE) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } else if (playerStatus == XConstant.PLAYER_STATUS_STOP) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        }// if
    }

    // 更新应用按钮状态
    public void updateRecordNextBtn(boolean isEnabled) {
        if (isEnabled) {
            recordNextBtn.setEnabled(true);

//            recordNextBtn.setTextColor(this.getResources().getColor(R.color.text_blue_darkblue));
        } else {
            recordNextBtn.setEnabled(false);
//            recordNextBtn.setTextColor(this.getResources().getColor(R.color.dark_blue));
        }
    }

    // 重置摄像头预览
    private void resetCameraPreview() {
        // 隐藏视频预览
        videoTemplateLayout.setVisibility(View.INVISIBLE);

        // 重置摄像头预览
        int width = Util.getScreenWidth(this);
        int height = (int) (width * 4 / 3);// 帧宽高比为4:3
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        previewLayout.setLayoutParams(layoutParams);
        previewLayout.setX(0);
        previewLayout.setY(0);
        cameraView.setLayoutParams(layoutParams);
        cameraView.setX(0);
        cameraView.setY(0);
    }

    // 重置录制相关参数
    private void resetRecordParams(final int duration) {
        // 停止录制
        isMusicPlayingStop = false;
        changePlayerStatus(XConstant.PLAYER_STATUS_STOP);
        if (avRecord.isAVRecording()) {
            sendRecordMessage(XConstant.RECORD_STATUS_STOP);
        }
        clearFile(XConstant.VIDEO_FILE_PATH);
        // 重置参数
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (avRecord.isAVRecording()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                avRecord.clearAVData();
                updateRecordNextBtn(false);
                recordMode = XConstant.RECORD_MODE_NOR;
                recordProgressView.setProgressParams(duration, XConstant.RECORD_ACC_MAX_TIME);
                setCurAccSongPath("");
            }
        }.execute();
    }

    // 获取当前录制时长
    public int getCurRecordTime() {
        return recordProgressView.getCurRecordTime();
    }

    // 获取当前录制模式
    public int getCurRecordMode() {
        return recordMode;
    }

    // 获取当前视频模板文件路径
    public String getCurVideoTemplatePath() {
        return curVideoTemplatePath;
    }

    // 获取当前视频模板帧率
    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 打开摄像头预览
        if (cameraView != null) {
            cameraView.startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止录制
        if (avRecord.isAVRecording()) {
            sendRecordMessage(XConstant.RECORD_STATUS_STOP);
        }

        // 延迟关闭摄像头预览
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (avRecord.isAVRecording()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // 关闭摄像头预览
                if (cameraView != null) {
                    cameraView.stopPreview();
                }
            }
        }.execute();

        // Log.d("dd_cc_dd", "recordfragment onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 清空音视频数据
        avRecord.clearAVData();

        // 关闭录制线程
        if (avRecord != null) {
            avRecord.closeRecord();
        }

        // 释放播放器资源
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (videoTemplateSurface != null) {
            videoTemplateSurface.release();
            videoTemplateSurface = null;
        }

        // 停止音乐播放服务
        Intent playerStart = new Intent(this, MusicPlayerService.class);
        this.stopService(playerStart);

        // 释放广播接收
        if (musicPlayerReceiver != null) {
            this.unregisterReceiver(musicPlayerReceiver);
        }
    }

    // 模板图标按钮被点击
    private void onClickTemplateIconBtn(final int index) {
        // 更新选中状态
        // isTxtTemplateSelected = (index != 1);

        // 停止录制
        if (avRecord.isAVRecording()) {
            sendRecordMessage(XConstant.RECORD_STATUS_STOP);
        }

        // 重置参数
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (avRecord.isAVRecording()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                avRecord.clearAVData();
                updateRecordNextBtn(false);
                recordMode = XConstant.RECORD_MODE_NOR;
                recordProgressView.setProgressParams(XConstant.RECORD_NOR_MAX_TIME, XConstant.RECORD_ACC_MAX_TIME);
            }
        }.execute();

        // 清除文字绘制区域
        accSongLyricsView.resetParams();
        // 停止视频播放并重置摄像头预览
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        resetCameraPreview();

        if (index != 1) {
            // 解析模板
            TemplateVideoInfo txtTemplateInfo = txtTemplateListData.get(index);
            String filePath = txtTemplateInfo.getLyricsFilePath();
            AccSongInfoParse.getInstance().parseLyricsInfo(filePath);
            setAccSongLyricsLine(AccSongInfoParse.getInstance().getListLyricsLine());

            if (txtTemplateInfo.getVideoName().contains(".mp4")) {
                if (cameraView.getCameraCount() > 1) {
                    String videoFilePath = txtTemplateInfo.getVideoFilePath();
                    File videoFile = new File(videoFilePath);
                    if (videoFile.exists()) {
                        if (pipModePrepared(videoFilePath)) {
                            // 播放视频
                            playerVideoTemplate(videoFilePath);
                        } else {
                            if (countDownTipsDlg != null) {
                                countDownTipsDlg.dismiss();
                            }
                            Toast.makeText(this, "无法打开该视频....", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // 文件不存在，从服务器加载至本地
//                        if (NetBroadcastReceiver.netWorkType != XConstant.NET_WORK_NULL) {
//                            GetResourceUrl getResUrl = new GetResourceUrl(resHandler, txtTemplateInfo);
//                            getResUrl.execute();
//                        } else {
//                            Toast.makeText(this, "网络连接中断，无法加载资源", Toast.LENGTH_SHORT).show();
//                        }
                    }
                } else {
                    Toast.makeText(this, "检测到您只有一个摄像头，暂时无法使用该功能", Toast.LENGTH_SHORT).show();
                }
            }
//			else
//			{
//				// 显示文字模板第一句
//				accSongLyricsView.updateTxtTemplate(RecordActivity.this);
//				accSongLyricsView.refreshView();
//			}
        }
    }

    // 准备画中画录制模式
    private boolean pipModePrepared(String videoFilePath) {
        // 倒计时动画
        countDownTipsAnimation();

        // 获取当前视频帧率
        videoFrameRate = AVProcessing.getVideoFrameRate(videoFilePath);
        if (videoFrameRate < 1) {
            videoFrameRate = 15;
            return false;
        }

        // 画中画预览模式
        int frameWidth = (int) (Util.getScreenWidth(this) * XConstant.PIP_MAPPING_COEF);
        int frameHeight = (int) (frameWidth * 4 / 3);// 帧宽高比为4:3
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(frameWidth, frameHeight);
        cameraView.setLayoutParams(layoutParams);
        cameraView.setX(0);
        cameraView.setY(Util.getScreenWidth(this) - frameHeight);

        // 视频路径
        curVideoTemplatePath = videoFilePath;

        return true;
    }

    // 获取/设置当前伴奏歌曲名
    public String getCurAccSongPath() {
        return curAccSongPath;
    }

    public void setCurAccSongPath(String curAccSongPath) {
        this.curAccSongPath = curAccSongPath;
    }

    public String getCurSongLyrics() {
        return curSongLyrics;
    }

    public void setCurSongLyrics(String curSongLyrics) {
        this.curSongLyrics = curSongLyrics;
    }

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
//                Log.d(TAG, "onReceive: " + currentTime);
                accSongLyricsView.compLyrics(currentTime);
            } else if (action.equals(XConstant.MUSIC_DURATION)) {
//                Log.d(TAG, "onReceive: 1");
                recordProgressView.setProgressParams(intent.getIntExtra("music_duration", 0), XConstant.RECORD_ACC_MAX_TIME);
                accSongLyricsView.resetParams();
            } else if (action.equals(XConstant.MUSIC_COMPLETION)) {
                // 播放完成>>重置参数
//                Log.d(TAG, "onReceive: 2");
                accSongLyricsView.resetParams();
                isMusicPlayingStop = true;
            }
        }
    }

    //设置新手引导状态操作
    public void setGuideState(boolean _bGuide) {
        bGuide = _bGuide;
    }

    //获取新手引导状态
    public boolean getGuideState() {
        return bGuide;
    }

    //新手引导
    public void createBeginnersGuideDialog() {
        //获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        beginnersGuideDlg = new Dialog(this, R.style.Dialog_Xiu);
        beginnersGuideDlg.setContentView(R.layout.beginners_guide_dialog);
        WindowManager.LayoutParams lp = beginnersGuideDlg.getWindow().getAttributes();
        lp.dimAmount = 0.75f;
        beginnersGuideDlg.getWindow().setAttributes(lp);
        beginnersGuideDlg.getWindow().setGravity(Gravity.BOTTOM);
        beginnersGuideDlg.getWindow().setLayout(Util.getScreenWidth(this), Util.getScreenHeight(this) - frame.top);
        beginnersGuideDlg.setCanceledOnTouchOutside(false);

        Button beginBtn = (Button) beginnersGuideDlg.findViewById(R.id.guideBeginButton);//开始引导
        ImageButton closeBtn = (ImageButton) beginnersGuideDlg.findViewById(R.id.beginnersCloseButton);//关闭按钮
        TextView guideText = (TextView) beginnersGuideDlg.findViewById(R.id.beginnersGuidePromptTextView);//跳过引导

        beginBtn.setOnClickListener(new View.OnClickListener() //开始引导
        {
            @Override
            public void onClick(View v) {
                beginnersGuideDlg.dismiss();
                guideOperation();
                setInstallationState();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() //关闭引导
        {
            @Override
            public void onClick(View v) {
                beginnersGuideDlg.dismiss();
                setInstallationState();
                bGuide = false;
            }
        });

        guideText.setOnClickListener(new View.OnClickListener() //跳过引导
        {
            @Override
            public void onClick(View v) {
                beginnersGuideDlg.dismiss();
                setInstallationState();
                bGuide = false;
                templateBtn.setEnabled(true);//模版
                accSongBtn.setEnabled(true);//飙歌
            }
        });

        beginnersGuideDlg.show();
    }

    //创建引导提示步骤图片
    private void guideOperation() {
        guideLayout = (RelativeLayout) this.findViewById(R.id.guidePageLayout);
        int nHeight = (int) (Util.getScreenWidth(this) * 0.2);
        guideLayout.setLayoutParams(new RelativeLayout.LayoutParams(Util.getScreenWidth(this), nHeight));
        guideImageView = new ImageView(this);
        guideImageView.setImageResource(R.drawable.guide2);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        guideLayout.addView(guideImageView, lp);
        guideLayout.setAlpha(0.75f);

        int nTop = overlapViewLayout.getTop();
        MarginLayoutParams margin = new MarginLayoutParams(guideLayout.getLayoutParams()); //设置Layout显示位置
        margin.setMargins(0, nTop, margin.rightMargin, nTop + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        guideLayout.setLayoutParams(layoutParams);
    }

    //引导标语图片切换
    public void setGuideImageViewResource(GuideResourceIndex grIndex) {
        if (lastGRIndex == grIndex || guideImageView == null)
            return;

        switch (grIndex) {
            case FIRST_GUIDE://继续按住
                guideImageView.setImageResource(R.drawable.guide3);
                break;
            case SECOND_GUIDE://松开暂停
                guideImageView.setImageResource(R.drawable.guide4);
                break;
            case THIRD_GUIDE://换个场景
                guideImageView.setImageResource(R.drawable.guide5);
                break;
            case FOURTH_GUIDE://继续按住
                guideImageView.setImageResource(R.drawable.guide3);
                break;
            case FIFTH_GUIDE://下一步
                guideImageView.setImageResource(R.drawable.guide6);
                break;
            case INITIALIZES_GUIDE:
            default:
                return;
        }

        lastGRIndex = grIndex;
    }

    //设置新手引导状态时,禁用的几个按钮
    private void setGuideEnableBtn() {
        recordCancelBtn.setEnabled(false);//取消
//        recordCancelBtn.setTextColor(this.getResources().getColor(R.color.dark_blue));

        templateBtn.setEnabled(false);//模版
        accSongBtn.setEnabled(false);//飙歌
    }

    //设置第一次安装状态
    private void setInstallationState() {
        SharedPreferences.Editor spEditor = this.getSharedPreferences(XConstant.INSTALLSTATE_FILENAME, Context.MODE_PRIVATE)
                .edit();
        spEditor.putBoolean("isFirstRun", false);
        spEditor.apply();
    }

    @SuppressLint("NewApi")
    private String getLocalVideoPath(Context context, final Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    // 倒计时提示动画
    private void countDownTipsAnimation() {
        // 提示动画页面
        countDownTipsDlg = new Dialog(this, R.style.Dialog_Xiu);
        countDownTipsDlg.setContentView(R.layout.template_countdown_dialog);
        Window dialogWindow = countDownTipsDlg.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().density * 240);
        layoutParams.height = (int) (getResources().getDisplayMetrics().density * 80);
        layoutParams.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(layoutParams);
        countDownTipsDlg.setCancelable(false);
        countDownTipsDlg.setCanceledOnTouchOutside(false);

        // 动画帧
        animationDrawable = new CountDownAnimationDrawable(handler);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.countdown_5), 1000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.countdown_4), 1000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.countdown_3), 1000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.countdown_2), 1000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.countdown_1), 1000);
        animationDrawable.setOneShot(true);

        countDownTextView = (TextView) countDownTipsDlg.findViewById(R.id.countDownTextView);
        countDownImageView = (ImageView) countDownTipsDlg.findViewById(R.id.countDownImageView);
        countDownImageView.setImageDrawable(animationDrawable);
        countDownImageView.setVisibility(View.GONE);
        countDownTextView.setVisibility(View.VISIBLE);
        countDownTipsDlg.show();
    }

    // 倒计时动画
    class CountDownAnimationDrawable extends AnimationDrawable {
        Handler handler = null;

        public CountDownAnimationDrawable(Handler _handler) {
            handler = _handler;
        }

        @Override
        public boolean selectDrawable(int idx) {
            boolean ret = super.selectDrawable(idx);
            if (idx != 0 && (idx == getNumberOfFrames() - 1)) {
                handler.sendEmptyMessageDelayed(XConstant.RECORD_COUNTDOWN_FINISHED, 500);
            }

            return ret;
        }
    }
//
//    @SuppressLint("HandlerLeak")
//    public Handler resHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case XConstant.DOWNLOAD_STATUS_COMPLETED:
//                    processLoadComplete(msg);
//                    break;
//
//                case XConstant.DOWNLOAD_STATUS_ERROR:
//                    processLoadError(msg);
//                    break;
//
//                case GlobalDef.WM_GET_RESURL_SUSS:
//                    // 开始加载资源
//                    if (NetBroadcastReceiver.netWorkType != XConstant.NET_WORK_NULL) {
//                        LoadResourceTask loadResTask = new LoadResourceTask(this, resHandler, (ResourceInfo) msg.obj);
//                        loadResTask.execute();
//                    } else {
//                        Toast.makeText(this, "网络连接中断，无法加载资源", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//
//                case GlobalDef.WM_GET_RESURL_FAIL:
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };

    // 下载完成
//    private void processLoadComplete(Message msg) {
//        ResourceInfo resInfo = (ResourceInfo) msg.obj;
//        // 模板
//        if (resInfo.getResType() == XConstant.CLASS_ID_TEMPLATE) {
//            // 更新ICON
//            //iconBtnAdapter.notifyDataSetChanged();
//
//            TemplateVideoInfo tmpVideoInfo = (TemplateVideoInfo) resInfo;
//            String videoFilePath = tmpVideoInfo.getVideoFilePath();
//            if (pipModePrepared(videoFilePath)) {
//                // 播放视频
//                playerVideoTemplate(videoFilePath);
//            } else {
//                if (countDownTipsDlg != null) {
//                    countDownTipsDlg.dismiss();
//                }
//                Toast.makeText(this, "无法打开该视频....", Toast.LENGTH_SHORT).show();
//            }
//        }
//        // 伴奏
//        else if (resInfo.getResType() == XConstant.CLASS_ID_ACCSONG) {
//            AccSongInfo accSongInfo = (AccSongInfo) resInfo;
//            // 判断试听/演唱被点击
//            if (accSongDataAdapter.isListenOn()) {
//                int index = accSongInfo.getIndex();
//                if (index > -1 && index < accSongDataAdapter.getCount()) {
//                    // 播放当前选中歌曲
//                    accSongDataAdapter.updateMusicPlayTip(index);
//                    Intent playerStart = new Intent(this.this, MusicPlayerService.class);
//                    playerStart.putExtra("MUSIC_FILE_NAME", accSongInfo.getSongFilePath());
//                    playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
//                    playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_LISTEN);
//                    this.this.startService(playerStart);
//                }
//            } else {
//                // 解析歌词
//                String lyricsFilePath = accSongInfo.getLyricsFilePath();
//                AccSongInfoParse.getInstance().parseLyricsInfo(lyricsFilePath);
//                setAccSongLyricsLine(AccSongInfoParse.getInstance().getListLyricsLine());
//
//                // 保存当前伴奏名并关闭伴奏弹出窗体
//                String songFilePath = accSongInfo.getSongFilePath();
//                setCurAccSongPath(songFilePath);
//                accSongListPopupWindow.dismiss();
//
//                int index = accSongInfo.getIndex();
//                if (index > -1 && index < accSongDataAdapter.getCount()) {
//                    // 播放当前选中歌曲
//                    accSongDataAdapter.updateMusicPlayTip(index);
//                    Intent playerStart = new Intent(this.this, MusicPlayerService.class);
//                    playerStart.putExtra("MUSIC_FILE_NAME", songFilePath);
//                    playerStart.putExtra("MUSIC_PLAYER_STATUS", XConstant.PLAYER_STATUS_START);
//                    playerStart.putExtra("MUSIC_PLAYING_TYPE", XConstant.PLAYING_TYPE_SINGING);
//                    this.this.startService(playerStart);
//
//                    // 开始录制
//                    sendRecordMessage(XConstant.RECORD_STATUS_START);
//                }
//            }
//        }
//
//    }

    private void resetRecord() {
        stickerBtn.setVisibility(View.VISIBLE);
        accSongBtn.setVisibility(View.VISIBLE);
        accSongBtn.setSelected(false);
        recordEnable = true;
        isFirstRecord = true;
        recordStatus = true;
        video_time.setText("00:00");
        setCurAccSongPath("");
        setCurSongLyrics("");
    }


    // 下载出错
    private void processLoadError(Message msg) {
        Toast.makeText(this, "资源下载异常...", Toast.LENGTH_SHORT).show();
    }
}
