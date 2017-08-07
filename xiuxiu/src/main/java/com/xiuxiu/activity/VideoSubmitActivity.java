package com.xiuxiu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiuxiu.R;
import com.xiuxiu.adapter.ShareItemListAdapter;
import com.xiuxiu.dialog.ExpressionPopupWindow;
import com.xiuxiu.task.UploadVideoTask;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.GlobalDef;
import com.xiuxiu.util.PCommonUtil;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.wxapi.WXEntryActivity;
import com.xiuxiu.wxapi.WXShare;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 视频提交页面
public class VideoSubmitActivity extends Activity {
    private static final String TAG = "VideoSubmitActivity";

    private String coverFilePath = null, videoFilePath = null;
    private TextView returnBtn = null;//返回
    private Button submitBtn = null;//发送视频
    private ImageButton expressionBtn = null;//打开表情框
    private EditText videoEditView = null;//视频描述
    private TextView locateTextView = null;//显示最终定位到的地理位置
    private ExpressionPopupWindow popupWindow = null;//表情弹出框
    private RelativeLayout addTagLayout = null;    //添加标签的Layout
    private TextView tagsView = null;//显示标签名称
    private ImageButton deleteTagsBtn = null;//删除标签按钮
    private ImageButton locationBtn = null;//开始定位按钮
    private GridView shareGridView = null;//分享到列表显示
    private ShareItemListAdapter shareData = null;
    private Dialog backPromptDialog = null;
    private TextView continueToEditBtn = null;//继续编辑
    // private Button saveDraftsBtn = null;//保存草稿
    private TextView giveUpVideoBtn = null;//放弃视频
    private TextView submitVideoCancelBtn = null;//取消
    private InputMethodManager imm = null;//软键盘
    private TextView subEditErrorView = null;// 错误提示
    private TextView tv_save, tv_share;
    private ImageView addLocationBtn;

    // 视频上传进度提示
    private TextView textInfo = null, textTitle = null;
    private ProgressBar progressBar = null;
    private Dialog processingInfoDialog = null;
    private int shareIndex = -1;// 分享标志
    // 录制模式
    private int recordMode = XConstant.RECORD_MODE_NOR;

    private static final int GET_CODE = 1;
    private static final int UPDATE_TIME = 3000;//定时检测间隔时间
    private static final int MAXWORDS = 280;// 最大字符数

    private UploadVideoTask uploadVideoTask;

    private boolean isLocation = false;

    private boolean isSave = false;
    private File videoFile;
    private int identify;
    private String imageScale = "0.75";

    private String title;
    private String content;
    private String photo;
    private String contentCircle;
    private String link;



    // 分享到的字符串数据
    private int imgStrName[] = {
            R.string.share_qq, R.string.share_wechat, R.string.share_friends,
            R.string.fave, R.string.sina, R.string.qqZone};

    // 分享到的图片数据
    private int imgIconId[] = {
            R.drawable.icon_qq_share, R.drawable.icon_wechat_share, R.drawable.icon_friends_share,
            R.drawable.wechat_favorite_unselect, R.drawable.icon_weibo_share, R.drawable.icon_qzone};

    // 选中分享到时的图片数据
    private int imgIconUpId[] = {
            R.drawable.icon_qq_share_c, R.drawable.icon_wechat_share_c, R.drawable.icon_friends_share_c,
            R.drawable.wechat_favorite, R.drawable.icon_weibo_share_c, R.drawable.icon_qzone_c};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_submit);
        UMShareAPI.get(this);
        Config.DEBUG = true;
//        UmengTool.checkWx(this);

        // 获取传递的数据
        recordMode = getIntent().getIntExtra("recordMode", XConstant.RECORD_MODE_NOR);
        coverFilePath = getIntent().getStringExtra("coverFilePath");
        videoFilePath = getIntent().getStringExtra("videoFilePath");
        identify = getIntent().getIntExtra("identify", 0);
        imageScale = getIntent().getStringExtra("imageScale");

        videoFile = new File(videoFilePath);

        // 更新视频封面
        ImageView videoThumbnails = (ImageView) findViewById(R.id.videoThumbnails);
        LayoutParams layoutParams = videoThumbnails.getLayoutParams();
        layoutParams.width = (int) (Util.getScreenWidth(this) * 0.25);
        layoutParams.height = (int) (Util.getScreenWidth(this) * 0.25);
        videoThumbnails.setLayoutParams(layoutParams);
        Bitmap coverBmp = BitmapFactory.decodeFile(coverFilePath);
        videoThumbnails.setImageBitmap(coverBmp);

        //软键盘
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //编辑框
        videoEditView = (EditText) findViewById(R.id.videoDescText);
        //添加标签
        addTagLayout = (RelativeLayout) findViewById(R.id.addTagLayout);
        //显示标签
        tagsView = (TextView) findViewById(R.id.addTagsView);
        //删除标签
        deleteTagsBtn = (ImageButton) findViewById(R.id.deleteTagsBtn);
        // 错误提示
        subEditErrorView = (TextView) findViewById(R.id.subEditErrorTextView);

        //分享到
        shareGridView = (GridView) findViewById(R.id.recordFinishShareGridView);
        shareData = new ShareItemListAdapter(this, imgStrName, imgIconId, imgIconUpId);
        shareGridView.setAdapter(shareData);
        shareGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                shareIndex = shareData.changeItem(arg2) ? arg2 : -1;
                shareData.notifyDataSetChanged();
            }
        });

        // 返回提示对话框
//        createBackPromptDialog();

        //定位显示
        locateTextView = (TextView) findViewById(R.id.locateText);

        //创建表情弹出菜单
        popupWindow = new ExpressionPopupWindow(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                expressionBtn.setBackgroundResource(R.drawable.icon_smlie);
            }
        });

        // 返回
        returnBtn = (TextView) findViewById(R.id.addsubmitCancelTextBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoVideoEditActivity("EDIT_VIEW");
                finish();
                ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
            }
        });

        addLocationBtn = (ImageView) findViewById(R.id.addLocationBtn);
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLocation) {
                    isLocation = false;
                    addLocationBtn.setSelected(false);
                    locateTextView.setText("");
                } else {
                    locateTextView.setText("正在定位...");
                    isLocation = true;
//                    if (!Util.checkPermission(VideoSubmitActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest
//                            .permission.ACCESS_FINE_LOCATION})) {
//                        ActivityCompat.requestPermissions(VideoSubmitActivity.this, new String[]{Manifest.permission
// .ACCESS_COARSE_LOCATION,
//                                Manifest.permission.ACCESS_FINE_LOCATION}, XConstant.CHECK_PERMISSION);
//                    }else {
                    getLocation();
//                    }
                }
            }
        });

        // 发布
        submitBtn = (Button) findViewById(R.id.addsubmitFinishBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoEditText = Util.convertContentToSend(videoEditView);
                // 敏感词检测
                if (!Util.detectionKeyword(videoEditText)) {
                    subEditErrorView.setPadding(videoEditView.getLeft(), 0, 0, 0);
                    subEditErrorView.setText(getResources().getString(R.string.commentErrorIllegalText));
                    subEditErrorView.setVisibility(View.VISIBLE);
                    return;
                }

                // 字数检测
                if (Util.calculateCharactersNum(videoEditText) > MAXWORDS) {
                    subEditErrorView.setPadding(videoEditView.getLeft(), 0, 0, 0);
                    subEditErrorView.setText(getResources().getString(R.string.subErrorText));
                    subEditErrorView.setVisibility(View.VISIBLE);
                    return;
                }

                subEditErrorView.setVisibility(View.INVISIBLE);
                // 创建视频发布进度提示框
                processingInfoDialog = new Dialog(VideoSubmitActivity.this, R.style.Dialog_Xiu);
                processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
                Window dialogWindow = processingInfoDialog.getWindow();
                WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
                layoutParams.width = (int) (getResources().getDisplayMetrics().density * 240);
                layoutParams.height = (int) (getResources().getDisplayMetrics().density * 80);
                layoutParams.gravity = Gravity.CENTER;
                dialogWindow.setAttributes(layoutParams);
                processingInfoDialog.setCancelable(false);
                processingInfoDialog.setCanceledOnTouchOutside(false);

                textTitle = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTitle);
                textInfo = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTextView);
                progressBar = (ProgressBar) processingInfoDialog.findViewById(R.id.processingProgressbar);
                processingInfoDialog.show();

                textTitle.setText(getResources().getString(R.string.processingProgressTitleText));
                textInfo.setText(0 + "%");
                progressBar.setProgress(0);

                // 限制待发布的视频体积(大于100k)

                Log.d(TAG, "onClick: " + CacheUtils.getInt(VideoSubmitActivity.this, "useridx"));

                if (videoFile.length() > 1024 * 100) {
                    String videoEditTextContent = videoEditText;
                    uploadVideoTask = new UploadVideoTask(
                            VideoSubmitActivity.this, handler,
                            String.valueOf(CacheUtils.getInt(VideoSubmitActivity.this, "useridx")), tagsView.getText().toString(),
                            videoEditTextContent,
                            locateTextView.getText().toString(), videoFilePath, coverFilePath, identify, imageScale);
                    uploadVideoTask.execute();
                    title = videoEditTextContent;


                } else {
                    Toast.makeText(VideoSubmitActivity.this, "视频数据异常，无法发布", Toast.LENGTH_SHORT).show();
                    processingInfoDialog.dismiss();
                }


            }
        });

        //表情
        expressionBtn = (ImageButton) findViewById(R.id.addExpressionBtn);
        expressionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expressionBtn.setBackgroundResource(R.drawable.icon_smlie_c);
                popupWindow.showAtLocation(expressionBtn, Gravity.BOTTOM, 0, 20);
                imm.hideSoftInputFromWindow(videoEditView.getWindowToken(), 0);//隐藏软键盘
            }
        });

        //添加标签
        addTagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: addTagLayout");
                Intent intent = new Intent(VideoSubmitActivity.this, AddTagsActivity.class);
                intent.putExtra("sTags", tagsView.getText());
                startActivityForResult(intent, GET_CODE);
                overridePendingTransition(R.anim.activity_up_animation, 0);
            }
        });

        //删除标签
        deleteTagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsView.setText("");
                tagsView.setHint(getResources().getString(R.string.addTags));
                deleteTagsBtn.setVisibility(View.GONE);
            }
        });


        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = null;
                if (!isSave) {
                    String saveFilePath = Util.genrateFilePath("Camera");
                    SimpleDateFormat timesdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String FileTime = timesdf.format(new Date());//获取系统时间
                    fileName = FileTime.replace(":", "");
                    try {
                        Log.d("copy", videoFilePath + "--" + saveFilePath + fileName + ".mp4");
                        if (Util.fileCopy(videoFilePath, saveFilePath + fileName + ".mp4")) {
                            isSave = true;
                            Util.showTextToast(VideoSubmitActivity.this, "保存成功，可在媒体库查看");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Util.showTextToast(VideoSubmitActivity.this, "已存在");
                }
            }
        });

        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (uploadVideoTask == null || uploadVideoTask.getVideoId() == -1) {
//                    Util.showTextToast(VideoSubmitActivity.this, "请先上传视频");
//                    return;
//                }
//                shareDialog();
            }
        });

        InputFilter[] emojiFiltersContent = {emojiFilter};
        videoEditView.setFilters(new InputFilter[]{emojiFilter});
    }


    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                ToolUtils.showToast(VideoSubmitActivity.this, "不支持输入Emoji表情符号，请见谅！");
                return "";
            }
            return null;
        }
    };

    private void upLoadImage() {
        String baseUrl = XConstant.HOST + "works/uploadVideoInfoNew";
        String urlParams = null;
        String videoEditText = Util.convertContentToSend(videoEditView);
        try {
            urlParams = "?uidx=" + CacheUtils.getString(this, "useridx")
                    + "&descriptions=" + URLEncoder.encode(videoEditText, "UTF-8")
                    + "&tags=" + URLEncoder.encode(tagsView.getText().toString(), "UTF-8")
                    + "&area=" + URLEncoder.encode(locateTextView.getText().toString(), "UTF-8")
                    + "&vrtype=1&videoSize=0.75&imgScale=0.75&identify=" + identify;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = XConstant.HOST + PCommonUtil.generateAPIStringWithSecret(VideoSubmitActivity.this, baseUrl, urlParams);
    }

    private void getShareData(){
        String shareTitle = null;
        String name = CacheUtils.getString(VideoSubmitActivity.this, XConstant.NICKNAME);

        int vid = uploadVideoTask.getVideoId();

        List<String> defaultTitles = new ArrayList<String>() {
        };
        defaultTitles.add("这个视频有毒啊，这毒不能让我一个人中，你也来看看");
        defaultTitles.add("这个视频有意思，小帅哥你也来看看");
        defaultTitles.add("发现一个有意思的短片，好看到哭！快来");
        Collections.shuffle(defaultTitles);

        shareTitle = defaultTitles.get(0);
        contentCircle = shareTitle;
        photo = coverFilePath;

        link = "http://www.quliao.com/mobile/works.aspx?worksId=" + vid + "&from=singlemessage&isappinstalled=1";
        if (TextUtils.isEmpty(name)) {
            content = "分享自牛拍";
        } else {
            content = "分享自" + name + "的牛拍";
        }
    }

    private boolean isShare = false;

    private void shareDialog(SHARE_MEDIA platform) {
        String shareTitle = null;
        String name = CacheUtils.getString(VideoSubmitActivity.this, XConstant.NICKNAME);

        int vid = uploadVideoTask.getVideoId();

        List<String> defaultTitles = new ArrayList<String>() {
        };
        defaultTitles.add("这个视频有毒啊，这毒不能让我一个人中，你也来看看");
        defaultTitles.add("这个视频有意思，小帅哥你也来看看");
        defaultTitles.add("发现一个有意思的短片，好看到哭！快来");
        Collections.shuffle(defaultTitles);

        shareTitle = defaultTitles.get(0);
        UMImage image = new UMImage(VideoSubmitActivity.this, new File(coverFilePath));

        UMWeb web = new UMWeb("http://www.quliao.com/mobile/works.aspx?worksId=" + vid +
                "&from=singlemessage&isappinstalled=1");
        web.setTitle(shareTitle);
        if (TextUtils.isEmpty(name)) {
            web.setDescription("分享自牛拍");
        } else {
            web.setDescription("分享自" + name + "的牛拍");
        }
        web.setThumb(image);

        UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
//                Util.showTextToast(VideoSubmitActivity.this, "onStart");
//                gotoVideoEditActivity("HOME_VIEW");
                Log.e(TAG, "onStart" + "----onStart----");
                isShare = true;
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
//                Util.showTextToast(VideoSubmitActivity.this, "onResult");
                gotoVideoEditActivity("HOME_VIEW");
                Log.e(TAG, "onResult" + "----onResult----");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                Util.showTextToast(VideoSubmitActivity.this, "onError" + throwable);
                gotoVideoEditActivity("HOME_VIEW");
                Log.e(TAG, "onError" + "----onError----");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
//                Util.showTextToast(VideoSubmitActivity.this, "onCancel");
                gotoVideoEditActivity("HOME_VIEW");
                Log.e(TAG, "onCancel" + "----onCancel----");
            }
        };

        new ShareAction(VideoSubmitActivity.this)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }

    //添加表情图片到编辑文本框
    public void addExpressionToEdit(int nIndex, int[] resIds) {
        long id = resIds[nIndex];
        int index = videoEditView.getSelectionStart(); // 获取光标所在位置
        Editable et = videoEditView.getEditableText();// 获取EditText的文字

        if (index < 0 || index >= et.length()) {
            videoEditView.append(Html.fromHtml("<img src='" + id + "'/>", imageGetter, null));
        } else {
            et.insert(index, Html.fromHtml("<img src='" + id + "'/>", imageGetter, null));
        }
    }

    ImageGetter imageGetter = new ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            int id = Integer.parseInt(source);
            // 根据id从资源文件中获取图片对象
            Drawable dable = getResources().getDrawable(id);
            dable.setBounds(0, 0, dable.getIntrinsicWidth(), dable.getIntrinsicHeight());
            return dable;
        }
    };

    //删除编辑文本框表情
    public void deleteExpression() {
        int index = videoEditView.getSelectionStart(); // 获取光标所在位置
        Editable et = videoEditView.getEditableText();// 获取EditText的文字
        if (index > 0 && index <= et.length()) {
            et.delete(index - 1, index);
        }
    }

    private void getLocation() {
        AMapLocationClient mLocationClient = new AMapLocationClient(VideoSubmitActivity.this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        addLocationBtn.setSelected(true);
                        locateTextView.setText(aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict());
                    }
                } else {
                    locateTextView.setText("获取定位失败");
                }
            }
        });
    }

    //获取从标签页传回的标签信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult: " + requestCode);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String dateString = data.getExtras().getString("LabelMsg");
                    tagsView.setText(dateString);
                    deleteTagsBtn.setVisibility(View.VISIBLE);
                    break;
                case RESULT_CANCELED:
                    Log.e(TAG, "取消返回" + "----取消返回----");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case XConstant.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            gotoVideoEditActivity("EDIT_VIEW");
            ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
        }

        return super.onKeyDown(keyCode, event);
    }

    // 返回至视频编辑界面

    private void gotoVideoEditActivity(String viewName) {
        Intent intent = new Intent();
        intent.putExtra("VIEW_NAME", viewName);
        setResult(1002, intent);
        finish();
    }

    // 创建返回提示对话框
    private void createBackPromptDialog() {
        backPromptDialog = new Dialog(VideoSubmitActivity.this, R.style.videoShareDialog);
        backPromptDialog.setContentView(R.layout.submit_video_back_prompt_dialog);
        backPromptDialog.getWindow().setGravity(Gravity.BOTTOM);
        backPromptDialog.getWindow().setWindowAnimations(R.style.videoShareDialogAnimation);
        backPromptDialog.getWindow().setLayout(Util.getScreenWidth(this), WindowManager.LayoutParams.WRAP_CONTENT);

        continueToEditBtn = (TextView) backPromptDialog.findViewById(R.id.ContinueToEditBtn);
        // saveDraftsBtn = (Button)backPromptDialog.findViewById(R.id.saveDraftsBtn);
        giveUpVideoBtn = (TextView) backPromptDialog.findViewById(R.id.GiveUpVideoBtn);
        submitVideoCancelBtn = (TextView) backPromptDialog.findViewById(R.id.SubmitVideoCancelBtn);

        // 继续编辑
        if (recordMode == XConstant.RECORD_MODE_PIP) {
            // 通过画中画模式录制的视频,不提供继续编辑功能
            continueToEditBtn.setEnabled(false);
        }

        continueToEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                backPromptDialog.dismiss();
//                gotoVideoEditActivity("EDIT_VIEW");
                finish();
            }
        });

        // 保存草稿(暂时先不加该功能)
//		saveDraftsBtn.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// 拷贝
//				String draftBox = Util.genrateFilePath("video/draftBox");
//				long curTime = System.currentTimeMillis();
//				AVProcessing.copyFile(coverFilePath, draftBox + "xiu_cover_" + String.valueOf(curTime) + ".png");
//				AVProcessing.copyFile(videoFilePath, draftBox + "xiu_video_" + String.valueOf(curTime) + ".mp4");
//				
//				backPromptDialog.dismiss();
//			}
//		});

        // 放弃视频
        giveUpVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPromptDialog.dismiss();

                // 页面跳转>>先返回至编辑页,以释放资源
                gotoVideoEditActivity("HOME_VIEW");
            }
        });

        // 取消
        submitVideoCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPromptDialog.dismiss();
            }
        });
    }// createBackPromptDialog()

    // 消息处理
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GlobalDef.MSG_UPLOAD_VIDEO_SUSS:
                    // 页面跳转>>先返回至编辑页,以释放资源
                    processingInfoDialog.dismiss();
                    Util.showTextToast(VideoSubmitActivity.this, "上传成功");
                    ToolUtils.deleteDir(new File(XConstant.LOCAL_VIDEO_FILE_PATH));
                    SHARE_MEDIA platform = null;
                    getShareData();
                    if (shareIndex == 0) {
                        platform = SHARE_MEDIA.QQ;
                    } else if (shareIndex == 1) {
                        platform = SHARE_MEDIA.WEIXIN;
                        //TODO
//                        Log.e(TAG, "WXShare" + title + "----" + content + "----" + photo + "----" + contentCircle + "----" + link + "----");
//                        WXEntryActivity wxShare = new WXEntryActivity(VideoSubmitActivity.this, 0, title, content, photo, contentCircle, link);
//                        wxShare.shareWX();
//                        return;
                    } else if (shareIndex == 2) {
                        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
//                        WXShare wxShare = new WXShare(VideoSubmitActivity.this, 1, title, content, photo, contentCircle, link);
//                        wxShare.share2WX();
//                        return;
                    } else if (shareIndex == 3) {
                        platform = SHARE_MEDIA.WEIXIN_FAVORITE;
//                        WXShare wxShare = new WXShare(VideoSubmitActivity.this, 2, title, content, photo, contentCircle, link);
//                        wxShare.share2WX();
//                        return;
                    } else if (shareIndex == 4) {
                        platform = SHARE_MEDIA.SINA;
                    } else if (shareIndex == 5) {
                        platform = SHARE_MEDIA.QZONE;
                    }

                    if (shareIndex != -1) {
                        if (!UMShareAPI.get(VideoSubmitActivity.this).isInstall(VideoSubmitActivity.this, platform)) {
                            Util.showTextToast(VideoSubmitActivity.this, "未安装客户端");
                            gotoVideoEditActivity("HOME_VIEW");
                            return;
                        }
                        shareDialog(platform);
                    } else {
                        gotoVideoEditActivity("HOME_VIEW");
                    }
//                    finish();
//				    MyUserInfoManager.getInstance().setVideoNum(MyUserInfoManager.getInstance().getVideoNum() + 1);
//                    MainActivity.setNewVideoInfoMark();
//                    gotoVideoEditActivity("PERSON_VIEW");
//                    MainActivity.shareNewVideoInfo(shareIndex, msg.arg1, (String)msg.obj, coverFilePath, videoEditView.getText()
// .toString());
                    break;
                case GlobalDef.MSG_UPLOAD_VIDEO_FAIL:
                    // 视频上传失败
                    textTitle.setText("视频上传失败...");
                    processingInfoDialog.setCancelable(true);
                    processingInfoDialog.setCanceledOnTouchOutside(true);
                    break;
                case GlobalDef.MSG_UPLOAD_VIDEO_TIPS:
                    // 更新上传进度
                    int value = (Integer) msg.obj;
                    textInfo.setText(value + "%");
                    progressBar.setProgress(value);
                    break;
                case GlobalDef.MSG_MAP_LOCATION_CODE:
                    // 地理位置信息
                    String strLocationInfod = msg.getData().getString("Province") + msg.getData().getString("City") + msg.getData()
                            .getString("District");
                    if (strLocationInfod != null && strLocationInfod.length() > 0) {
                        locateTextView.setText(strLocationInfod);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isShare) {
            gotoVideoEditActivity("HOME_VIEW");
        }
        Log.e(TAG, isShare + "----isShare----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
