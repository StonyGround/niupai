package com.xiuxiu.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.xiuxiu.R;
import com.xiuxiu.util.AddWatermarkUtil;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;

import java.io.File;
import java.io.IOException;

public class VideoLocalActivity extends AppCompatActivity {

    private String filePath = null, coverFilePath = null, videoFilePath = null;
    private VideoView vv_video;
    private TextView tv_cancel, tv_start, tv_confirm;
    private RelativeLayout rl_local = null, rlProgress = null;

    private static ImageView iv_image_bg;

    private MediaMetadataRetriever retriever = null;
    private String fileLength;
    private Bitmap bitmap;
    private int frameCount = 1000 / 15;

    private static File watermark;

    private String imageScale;

    /**
     * TAG
     */
    private final static String TAG = VideoLocalActivity.class.getSimpleName();

    /**
     * Notification 的ID
     */
    int notifyId = 200;
    /** NotificationCompat 构造器*/
    /**
     * 是否点击发布
     */
    public boolean isPublish = false;
    public boolean isUpdateShow = false;
    /**
     * 通知栏按钮广播
     */
    public ButtonBroadcastReceiver bReceiver;
    /**
     * 通知栏按钮点击事件对应的ACTION
     */
    public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";

    /**
     * Notification管理
     */
    public NotificationManager mNotificationManager;

    private int isSuccess = 1;
    private int isReturn = 3;
    private int isVideo = 7;

    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_local);
        initService();
        initButtonReceiver();
        initView();
        try {
            retriever = new MediaMetadataRetriever();

            Intent intent = getIntent();
            filePath = intent.getStringExtra("filePath");
            Log.e("filePath", filePath);

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
                    isReturn = 4;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ToolUtils.showToast(VideoLocalActivity.this, "您选择的视频格式不对，请重新选择!");
        }
        initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
            isReturn = 4;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化要用到的系统服务
     */
    private void initService() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void initView() {
        vv_video = (VideoView) findViewById(R.id.vv_video);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        rl_local = (RelativeLayout) findViewById(R.id.rl_local);
        iv_image_bg = (ImageView) findViewById(R.id.iv_image_bg);
        rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
    }

    private void initData() {
        try {
            vv_video.setMediaController(new MediaController(this));
            MediaController mc = new MediaController(this);
            mc.setVisibility(View.INVISIBLE);

            vv_video.setMediaController(mc);
            vv_video.setVideoURI(Uri.parse(filePath));
            vv_video.start();
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(1 * frameCount, MediaMetadataRetriever.OPTION_CLOSEST);
            Log.e("filePath", filePath + "----");
            fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (bitmap != null) {
                ToolUtils.saveMyBitmap(bitmap, "xiu_localvideo_pic");
                imageScale = String.valueOf((float) bitmap.getWidth() / (float) bitmap.getHeight());
            }
            coverFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_localvideo_pic.png";
            videoFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_output_localvideo.mp4";
//            Glide.with(this).load(new File(coverFilePath)).into(iv_image_bg);
        } catch (Exception e) {
            e.printStackTrace();
            ToolUtils.showToast(VideoLocalActivity.this, "您选择的视频格式不对，请重新选择!");
            isVideo = 8;
        }

        ToolUtils.iconSave(VideoLocalActivity.this, R.drawable.watermark, "watermark");
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVideo == 7) {
//                    iv_image_bg.setVisibility(View.GONE);
//                    vv_video.setVisibility(View.VISIBLE);
                    vv_video.start();
                } else {
                    ToolUtils.showToast(VideoLocalActivity.this, "您选择的视频格式不对，无法播放！");
                }
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //合成视频
               /* String picFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_localvideo_pic" + ".png";
                String outputVideoFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_output_localvideo" + ".mp4";
                File picFile = new File(picFilePath);
                File outputVideoFile = new File(outputVideoFilePath);
                if (picFile.exists() || picFile.isFile()) {
                    ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
                    getWaterMarkVideo();
                }else {
                    getWaterMarkVideo();
                }*/
                if (isVideo == 7) {
                    if (isSuccess == 1) {
                        getWaterMarkVideo();
                        getVideoData();
//                        ToolUtils.showToast(VideoLocalActivity.this, "正在通知栏合成中...");
//                        showButtonNotify();
                        rlProgress.setVisibility(View.VISIBLE);
                    } else {
                        ToolUtils.showToast(VideoLocalActivity.this, "已合成完毕");
                    }
                } else {
                    ToolUtils.showToast(VideoLocalActivity.this, "您选择的视频格式不对，无法合成！");
                }

//                startActivity(new Intent(VideoLocalActivity.this, RecordActivity.class));
//                VideoLocalActivity.this.finish();
            }
        });
    }

    private void getVideoData() {
        AddWatermarkUtil.getsInstance().setListener(new AddWatermarkUtil.OnCompoundListener() {
            @Override
            public void onSuccess() {
                if (isReturn == 3) {
                    Intent intent = new Intent(VideoLocalActivity.this, VideoSubmitActivity.class);
                    intent.putExtra("coverFilePath", coverFilePath);
                    intent.putExtra("videoFilePath", videoFilePath);
                    intent.putExtra("identify", 1);
                    intent.putExtra("imageScale", imageScale);
                    Log.e("imageScale", imageScale);
                    startActivity(intent);
                    VideoLocalActivity.this.finish();
                    rlProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed() {
                ToolUtils.showToast(VideoLocalActivity.this, "合成失败,请重新选择视频");
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onStart() {
                Log.e("onStart", "rlProgress------");
            }
        });
    }

    private void getWaterMarkVideo() {
        watermark = new File(XConstant.LOCAL_VIDEO_COPE_FILE_PATH, "watermark.png");
        String outputVideo = Util.genrateFilePath("xiu_output_localvideo", ".mp4", XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH);
        AddWatermarkUtil.addWatermark(VideoLocalActivity.this, filePath, outputVideo, watermark.getPath());
    }

    /**
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 带按钮的通知栏
     */
    public void showButtonNotify() {
        mBuilder = new NotificationCompat.Builder(this);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
        mRemoteViews.setImageViewResource(R.id.custom_icon, R.drawable.ic_title_icon);

        mRemoteViews.setTextViewText(R.id.tv_custom_title, "正在合成中...");
        mRemoteViews.setTextViewText(R.id.tv_custom_name, "合成完毕后点击前往发布按钮");
        mRemoteViews.setTextColor(R.id.tv_custom_title, Color.parseColor("#333333"));
        mRemoteViews.setTextColor(R.id.tv_custom_name, Color.BLACK);
        mRemoteViews.setTextViewText(R.id.btn_custom_publish, "前往发布");
        mRemoteViews.setTextColor(R.id.btn_custom_publish, Color.BLACK);

        AddWatermarkUtil.getsInstance().setListener(new AddWatermarkUtil.OnCompoundListener() {
            @Override
            public void onSuccess() {
                //btn_custom_play
                isSuccess = 2;
                isUpdateShow = !isUpdateShow;

                if (isUpdateShow) {
                    mRemoteViews.setTextViewText(R.id.tv_custom_title, "合成成功");
                    mRemoteViews.setTextViewText(R.id.tv_custom_name, "已合成完毕点击前往发布按钮");
                    mRemoteViews.setTextViewText(R.id.btn_custom_publish, "前往发布");
                    mRemoteViews.setTextColor(R.id.btn_custom_publish, Color.BLACK);
                }

                if (isReturn == 3) {
                    ToolUtils.showToast(VideoLocalActivity.this, "合成成功点击通知栏消息前往发布界面");
                }
                rlProgress.setVisibility(View.GONE);
//                VideoLocalActivity.this.finish();
            }

            @Override
            public void onFailed() {
                ToolUtils.showToast(VideoLocalActivity.this, "合成失败,请重新选择视频");
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onStart() {
                Log.e("onStart", "rlProgress------");
            }
        });

        if (isPublish) {
            Log.e("VideoLocalActivity", isPublish + "---11--" + isPublish);
        } else {
            Log.e("VideoLocalActivity", isPublish + "---22--" + isPublish);
        }

        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        /* 前往发布  按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, BUTTON_PALY_ID, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (isSuccess == 2) {
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_publish, intent_paly);
        } else {
            ToolUtils.showToast(VideoLocalActivity.this, "暂未合成完毕");
        }


        /*String picFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_localvideo_pic" + ".png";
        String outputVideoFilePath = XConstant.LOCAL_VIDEO_OUTPUT_FILE_PATH + "xiu_output_localvideo" + ".mp4";
        File picFile = new File(picFilePath);
        File outputVideoFile = new File(outputVideoFilePath);
        if (picFile.exists() || outputVideoFile.exists()) {
            Log.e("picFile+outputVideoFile", picFile + "----" + outputVideoFile);
        } else {
            isPublish = !isPublish;
            ToolUtils.showToast(VideoLocalActivity.this, "请重新合成");
        }*/

        /*PendingIntent pendingIntent;
        isPublish = !isPublish;
        if (isPublish) {
            pendingIntent = getDefalutIntent(Notification.FLAG_FOREGROUND_SERVICE);
        } else {
            pendingIntent = getDefalutIntent(Notification.FLAG_AUTO_CANCEL);
        }*/

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在合成中")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_title_icon);
        Notification notify = mBuilder.build();
        isPublish = !isPublish;
        if (isPublish) {
            notify.flags = Notification.FLAG_ONGOING_EVENT;
        } else {
            notify.flags = Notification.FLAG_AUTO_CANCEL;
        }
        mNotificationManager.notify(notifyId, notify);
    }

    /**
     * 带按钮的通知栏点击广播接收
     */
    public void initButtonReceiver() {
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
    }

    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    /**
     * 前往发布 按钮点击 ID
     */
    public final static int BUTTON_PALY_ID = 2;

    /**
     * 广播监听按钮点击时间
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PALY_ID:
                        String play_status = "";
                        isPublish = !isPublish;
                        if (isPublish) {
                            play_status = "正在前往发布界面";
                            Intent intent1 = new Intent(VideoLocalActivity.this, VideoSubmitActivity.class);
                            intent1.putExtra("coverFilePath", coverFilePath);
                            intent1.putExtra("videoFilePath", videoFilePath);
                            Log.e("videoFilePath", videoFilePath);
                            startActivity(intent1);
                            VideoLocalActivity.this.finish();
                        }
                        showButtonNotify();
                        Log.d(TAG, play_status + "----play_status---");
                        if (TextUtils.isEmpty(play_status)) {
                            ToolUtils.showToast(VideoLocalActivity.this, "暂未合成完毕");
                        } else {
                            ToolUtils.showToast(VideoLocalActivity.this, play_status);
                        }

                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
        }
        super.onDestroy();
    }
}
