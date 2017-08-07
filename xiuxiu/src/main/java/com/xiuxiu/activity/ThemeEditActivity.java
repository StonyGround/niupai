package com.xiuxiu.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiuxiu.R;
import com.xiuxiu.adapter.ThemeRecyclerViewAdapter;
import com.xiuxiu.adapter.ThemeVrayRecyclerViewAdapter;
import com.xiuxiu.dialog.DialogCircleProgress;
import com.xiuxiu.dialog.DialogGuide;
import com.xiuxiu.dialog.InitView;
import com.xiuxiu.model.FrameListModel;
import com.xiuxiu.model.ThemeInfo;
import com.xiuxiu.model.ThemeTypeInfo;
import com.xiuxiu.request.HttpUtils;
import com.xiuxiu.request.SaxService;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.BitmapUtil;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.DownloadThemeUtils;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.DoubleSeekBar;
import com.xiuxiu.view.SingleSeekBar;
import com.xiuxiu.view.ThemeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ThemeEditActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Long> videoFrameList;

    private ImageView image_preview, edit_cancel, edit_sure, iv_play;
    private RecyclerView rv_small, rv_image;
    private RelativeLayout contentLayout, videoPreviewLayout, rlThemeVraySeekbar, rlThemeVraySeekbarSing, rl_play;

    private List<Bitmap> bitmapList = new ArrayList<>();

    private ThemeVrayRecyclerViewAdapter themeVrayRecyclerViewAdapter;

//    private IdentityHashMap<String, List<TextEditModel>> textEditModelMap = new IdentityHashMap<String, List<TextEditModel>>();

    private ThemeRecyclerViewAdapter mThemeRecyclerViewAdapter;
    private List<ThemeInfo> themeInfoList;
    private String filePath, outputfilePath, fileIconPath, fileImagePath;
    private File themeFile;
    private static final String TAG = ThemeEditActivity.class.getSimpleName();
    private InputStream inputStream;

    private int curFramePos = 0;
    private ThemeView mCurrentThemeView;

    private List<ThemeView> themeViewList = new ArrayList<>();

    private SingleSeekBar<Integer> mSingleSeekBar;

    private int ten = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_edit);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        FrameListModel frameListModel = intent.getParcelableExtra("videoFrameList");
        videoFrameList = frameListModel.getFrameList();

        image_preview = (ImageView) findViewById(R.id.image_preview);
        edit_cancel = (ImageView) findViewById(R.id.edit_cancel);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        edit_sure = (ImageView) findViewById(R.id.edit_sure);
        rv_small = (RecyclerView) findViewById(R.id.rv_small);
        rv_image = (RecyclerView) findViewById(R.id.rv_image);
        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        videoPreviewLayout = (RelativeLayout) findViewById(R.id.videoPreviewLayout);
        rlThemeVraySeekbar = (RelativeLayout) findViewById(R.id.rl_theme_cray_seekbar);
        rlThemeVraySeekbarSing = (RelativeLayout) findViewById(R.id.rl_theme_cray_seekbar_sing);
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);


        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        rl_play.setOnClickListener(this);

        LinearLayoutManager sLinearLayoutManager = new LinearLayoutManager(this);
        sLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_small.setLayoutManager(sLinearLayoutManager);
        LinearLayoutManager iLinearLayoutManager = new LinearLayoutManager(this);
        iLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_image.setLayoutManager(iLinearLayoutManager);

        new ThemeEditActivity.AsyncThemeData().execute();

        new ThemeEditActivity.AsyncGetBitmap().execute();
        image_preview.setImageBitmap(getBitMapIcon(0, true));

        handler.sendEmptyMessage(GIF_START);
    }

    private class AsyncThemeData extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final String url = XConstant.HOST + "restheme.xml";
            inputStream = HttpUtils.getXML(url);
            try {
                themeInfoList = new ArrayList<>();
                List<HashMap<String, String>> list = SaxService.readXML(inputStream, "theme");
                for (HashMap<String, String> map : list) {
                    ThemeInfo themeInfo = new ThemeInfo();
                    themeInfo.setThemeid(map.get("themeid"));
                    themeInfo.setThemeName(map.get("themeName"));
                    themeInfo.setThemeUrl(map.get("themeUrl"));
                    themeInfo.setThemeZip(map.get("themeZip"));
                    themeInfoList.add(themeInfo);
                    for (int i = 0; i < themeInfoList.size(); i++) {
                        themeInfoList.get(i).getThemeid();
                        themeInfoList.get(i).getThemeName();
                        themeInfoList.get(i).getThemeUrl();
                        themeInfoList.get(i).getThemeZip();
//                        Log.e("themeInfos", themeInfoList.get(i).getThemeid() + themeInfoList.get(i).getThemeUrl() + themeInfoList.get(i)
//                                .getThemeZip() + "----themeInfoList-----");
                    }
//                    Log.e("ThemeInfo", themeInfo.getThemeid() + themeInfo.getThemeUrl() + themeInfo.getThemeZip() + "----theme-----");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mThemeRecyclerViewAdapter = new ThemeRecyclerViewAdapter(ThemeEditActivity.this, themeInfoList);
            rv_image.setAdapter(mThemeRecyclerViewAdapter);
            mThemeRecyclerViewAdapter.setOnItemClickListener(new ThemeRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, ThemeInfo data) {
//                Toast.makeText(ThemeEditActivity.this, position +"" , Toast.LENGTH_SHORT).show();
                    String name = data.getThemeName();
                    filePath = XConstant.RES_THEME_ZIP_FILE_PATH + name + ".zip";
                    outputfilePath = XConstant.RES_THEME_ICON_FILE_PATH;
                    themeFile = new File(filePath);
                    if (themeFile.exists()) {
                        fileIconPath = XConstant.RES_THEME_ICON_FILE_PATH + "/" + name + "/" + "config.json";
                        Gson gson = new Gson();
                        Reader reader = null;
                        try {
                            reader = new FileReader(fileIconPath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //json data save model
                        ThemeTypeInfo themeType = gson.fromJson(reader, ThemeTypeInfo.class);
                        fileImagePath = getImagePath(name, themeType.getFrames().get(0).getPic());
                        createTheme(fileImagePath, themeType, curFramePos, true, null);
//                        doubleSeekBar(1, 100, themeType);

                    } else {
                        //download theme
                        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                        boolean isTheme = CacheUtils.getBooleanFirst(ThemeEditActivity.this, XConstant.GUIDE_THEME);
                        if (isTheme) {
                            DialogGuide dialogGuideTheme = new DialogGuide(ThemeEditActivity.this, R.drawable.tip_theme);
                            dialogGuideTheme.show();
                            downloadTheme(view, data, name);

                            CacheUtils.setBooleanFirst(ThemeEditActivity.this, XConstant.GUIDE_THEME, false);
                        }else {
                            downloadTheme(view, data, name);
                        }
                    }
                }
            });
        }
    }

    private String getImagePath(String name, int index) {
        return XConstant.RES_THEME_ICON_FILE_PATH + name + "/" + name + index + ".png";
    }

    private void downloadTheme(final View view, final ThemeInfo theme, final String name) {
        DownloadThemeUtils.getsInstance().setListener(new DownloadThemeUtils.OnDownloadListener() {
            @Override
            public void onDowload(String themeUrl) {
//                Toast.makeText(ThemeEditActivity.this, "download success", Toast.LENGTH_SHORT).show();
                try {
                    //Unzip theme
                    ToolUtils.UnZipFolder(filePath, outputfilePath);
                    //parse theme local json data
                    fileIconPath = XConstant.RES_THEME_ICON_FILE_PATH + "/" + name + "/" + "config.json";
                    Gson gson = new Gson();
                    Reader reader = new FileReader(fileIconPath);
                    //json data save model
                    ThemeTypeInfo themeType = gson.fromJson(reader, ThemeTypeInfo.class);
                    //
                    if (themeFile.exists()) {
                        view.setVisibility(View.GONE);
                    }

                    fileImagePath = getImagePath(name, themeType.getFrames().get(0).getPic());
                    createTheme(fileImagePath, themeType, curFramePos, true, null);
//                    doubleSeekBar(1, 100, themeType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String error) {
            }
        }).download(theme);
    }

    private void singSeekBar(int start, int end) {
        //左游标
        mSingleSeekBar = new SingleSeekBar<Integer>(start, end, bitmapList.get(0).getHeight(), this);
        mSingleSeekBar.setOnRangeSeekBarChangeListener(new SingleSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(SingleSeekBar<?> bar, Integer minValue, Integer maxValue) {
                curFramePos = convertFrame(minValue);
//                Log.e(TAG, "curFramePos = " + curFramePos);
                image_preview.setImageBitmap(getBitMapIcon(curFramePos, true));

                for (ThemeView themeView : themeViewList) {
                    themeView.setInEdit(false);
                    if (curFramePos <= themeView.getMaxFrame() && curFramePos >= themeView.getMinFrame()) {
                        int index = (curFramePos - themeView.getMinFrame()) % themeView.getThemeTypeInfo().getFrames()
                                .size();
                        String filePath = getImagePath(themeView.getThemeTypeInfo().getName(), themeView.getThemeTypeInfo()
                                .getFrames().get(index).getPic());
                        themeView.changeThemeImage(filePath);
                    } else {
                        themeView.changeThemeImage("");
                    }
                }
            }
        });
        rlThemeVraySeekbar.addView(mSingleSeekBar);
    }


    private void copyThemeToArray(ThemeView themeView, ThemeTypeInfo themeTypeInfo) {
        themeViewList.add(themeView);
    }

    //创建主题框
    private void createTheme(@Nullable String path, @Nullable final ThemeTypeInfo themeTypeInfo, int frame, boolean isCreate,
                             @Nullable ThemeView model) {
        ThemeView themeView = null;
        if (!isCreate && model != null) {
            if (!TextUtils.isEmpty(path)) {
                themeView.changeThemeImage(path);
            }
        } else {
            //左右游标
            DoubleSeekBar<Integer> mDoubleSeekBarr = new DoubleSeekBar<Integer>(1, 100, bitmapList.get(0).getHeight(), this);
            mDoubleSeekBarr.setOnRangeSeekBarChangeListener(new DoubleSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(DoubleSeekBar<?> bar, Integer minValue, Integer maxValue) {
//                    Log.e(TAG, "minValue:" + minValue + "---TAG---" + "maxValue:" + maxValue);

                    //更新themeview大小值
                    for (ThemeView themeView : themeViewList) {
                        if (bar == themeView.getDoubleSeekBar()) {
                            themeView.setMinFrame(convertFrame(minValue));
                            themeView.setMaxFrame(convertFrame(maxValue));
                        }
                    }
                }
            });

            double left = (double) curFramePos / (double) (videoFrameList.size() - 1);
            mDoubleSeekBarr.setNormalizedMinValue(left);
            double right = left + ((double) themeTypeInfo.getFrames().size() / (double) (videoFrameList.size() - 1));
            if (right >= 0 && right <= 1) {
                right = left + ((double) themeTypeInfo.getFrames().size() / (double) (videoFrameList.size() - 1));
            } else {
                right = 1;
            }
            mDoubleSeekBarr.setNormalizedMaxValue(right);
            rlThemeVraySeekbar.addView(mDoubleSeekBarr);
//            Log.e(TAG, "left = " + left + "-----" + "right = " + right);


            int countFrame = curFramePos + themeTypeInfo.getFrames().size();
            if (countFrame > videoFrameList.size() - 1) {
                countFrame = videoFrameList.size() - 1;
            }
            themeView = new ThemeView(ThemeEditActivity.this, themeTypeInfo, frame, countFrame, mDoubleSeekBarr);
            themeView.setThemeImage(path);
            themeView.setOperationListener(new ThemeView.OperationListener() {
                @Override
                public void onDeleteClick(ThemeView themeView) {

                    for (Iterator<ThemeView> it = themeViewList.iterator(); it.hasNext(); ) {
                        ThemeView item = it.next();
                        if (item == themeView) {
                            item.getDoubleSeekBar().setClear(true);
                            it.remove();
                        }
                    }

//                    setCurrentTheme(themeViewList.get(0));
                }

                @Override
                public void onEdit(ThemeView themeView) {
                    mCurrentThemeView.setInEdit(false);
                    mCurrentThemeView = themeView;
                    mCurrentThemeView.setInEdit(true);

                    //切换seekbar
                    for (ThemeView theme : themeViewList) {
                        theme.getDoubleSeekBar().setDrawIcon(false);
                    }
                    themeView.getDoubleSeekBar().setDrawIcon(true);
                }

                @Override
                public void onClick(ThemeView themeView) {
                }

                @Override
                public void onTop(final ThemeView themeView) {
                }
            });
        }

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT);
        contentLayout.addView(themeView, rl);
        setCurrentTheme(themeView);

        for (ThemeView theme : themeViewList) {
            theme.getDoubleSeekBar().setDrawIcon(false);
        }
        themeView.getDoubleSeekBar().setDrawIcon(true);

        //添加主题到List
        copyThemeToArray(themeView, themeTypeInfo);
    }

    //seekbar坐标转换为帧
    private int convertFrame(int seekBarValue) {
        int countFrame = videoFrameList.size() - 1;
        return Math.round(countFrame * seekBarValue / 100);
    }

    //选中当前主题框
    private void setCurrentTheme(ThemeView themeView) {
        if (mCurrentThemeView != null) {
            mCurrentThemeView.setInEdit(false);
        }
        mCurrentThemeView = themeView;
        mCurrentThemeView.setInEdit(true);
    }

    private class AsyncGetBitmap extends AsyncTask<Void, Integer, Void> {

        private TextView textInfo = null;
        private ProgressBar progressBar = null;
        private Dialog processingInfoDialog = null;

        @Override
        protected void onPreExecute() {
            processingInfoDialog = new Dialog(ThemeEditActivity.this, R.style.Dialog_Xiu);
            processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
            Window dialogWindow = processingInfoDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int) (ThemeEditActivity.this.getResources().getDisplayMetrics().density * 240);
            layoutParams.height = (int) (ThemeEditActivity.this.getResources().getDisplayMetrics().density * 80);
            layoutParams.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(layoutParams);
            processingInfoDialog.setCancelable(false);
            processingInfoDialog.setCanceledOnTouchOutside(false);

            textInfo = (TextView) processingInfoDialog.findViewById(R.id.processingProgressTextView);
            progressBar = (ProgressBar) processingInfoDialog.findViewById(R.id.processingProgressbar);
            processingInfoDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(1);
            int count = videoFrameList.size() - 1;
            count = count / ten;
            int size = ten;
            for (int i = 0; i < size; i++) {
                int c = i * count;
//                Log.d("countTheme", c + "---");
                Bitmap bitmap = getBitMapIcon(c, false);
                if (bitmap != null) {
                    bitmapList.add(bitmap);
                    publishProgress(i * 100 / size);
                }
            }
//            Log.d("bitmapList", bitmapList.size() + "---");
            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            textInfo.setText(values[0] + "%");
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            themeVrayRecyclerViewAdapter = new ThemeVrayRecyclerViewAdapter(ThemeEditActivity.this, bitmapList);
            rv_small.setAdapter(themeVrayRecyclerViewAdapter);
            themeVrayRecyclerViewAdapter.setOnItemClickListener(new ThemeVrayRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    curFramePos = (videoFrameList.size() - 1) * position / ten;
                    image_preview.setImageBitmap(getBitMapIcon(position, true));
                    double count = (double) (videoFrameList.size() - 1) / (double) ten;
                    double singbar = (double) (position * count) / (double) (videoFrameList.size() - 1);
//                    Log.d(TAG, "count = " + count + "----" + "singbar =" + singbar + "videoFrameList.size" + videoFrameList.size());
                    mSingleSeekBar.setNormalizedMinValue(singbar);
                }
            });
            processingInfoDialog.dismiss();
            singSeekBar(1, 100);
        }
    }

    private Bitmap
    getBitMapIcon(int position, boolean isOrigin) {
        long handle = videoFrameList.get(position);
        if (handle != 0) {
            int bitMapWidth = XConstant.FRAME_DST_WIDTH;
            int bitMapHeight = XConstant.FRAME_DST_HEIGHT;
            int rgbFrameSize = bitMapWidth * bitMapHeight;
            int[] rgbData = new int[rgbFrameSize];
            Bitmap cameraBmp = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);
            AVProcessing.yuv2rgb(handle, rgbData, bitMapWidth, bitMapHeight);
            cameraBmp.setPixels(rgbData, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
            if (!isOrigin) {
                Matrix matrix = new Matrix();
                float scale = 0.25f;
                matrix.postScale(scale, scale);
                cameraBmp = Bitmap.createBitmap(cameraBmp, 0, 0, bitMapWidth, bitMapHeight, matrix,
                        true);
            }
            return cameraBmp;
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        Intent intent = new Intent();
        FrameListModel frameListModel = new FrameListModel();
        frameListModel.setFrameList(videoFrameList);
        intent.putExtra("videoFrameList", frameListModel);
        setResult(XConstant.TEXT_ACTIVITY_RESULT, intent);
        finish();
    }

    private static final int VIDEO_PLAY = 0;//播放
    private static final int VIDEO_PAUSE = 1;//暂停
    private static final int VIDEO_STOP = 2;//停止
    private static final int GIF_START = 3;//GIF
    private static final int PROGRESS = 4;
    private int gifPosition = 0;
    private boolean isPlaying = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_PLAY:

                    if (curFramePos < videoFrameList.size()) {
                        isPlaying = true;
                        image_preview.setImageBitmap(getBitMapIcon(curFramePos, true));
                        double d = (double) curFramePos / (double) (videoFrameList.size() - 1);
//                        Log.e(TAG, "SINGBAR = " + d + "curFramePos = " + curFramePos);
                        mSingleSeekBar.setNormalizedMinValue(d);
                        handler.sendEmptyMessageDelayed(VIDEO_PLAY, 66);
                    } else {
                        handler.sendEmptyMessage(VIDEO_STOP);
                    }

                    curFramePos++;
                    break;
                case VIDEO_PAUSE:
                    isPlaying = false;
                    handler.removeMessages(VIDEO_PLAY);
                    iv_play.setVisibility(View.VISIBLE);
                    break;
                case VIDEO_STOP:
                    isPlaying = false;
                    curFramePos = 0;
                    iv_play.setVisibility(View.VISIBLE);
                    mSingleSeekBar.setNormalizedMinValue(0);
                    break;
                case GIF_START:

                    for (ThemeView themeView : themeViewList) {
                        if (isPlaying) {
                            themeView.setInEdit(false);
                        }

                        if (curFramePos <= themeView.getMaxFrame() && curFramePos >= themeView.getMinFrame()) {
                            int index = gifPosition % themeView.getThemeTypeInfo().getFrames()
                                    .size();
                            String filePath = getImagePath(themeView.getThemeTypeInfo().getName(), themeView.getThemeTypeInfo()
                                    .getFrames().get(index).getPic());
                            themeView.changeThemeImage(filePath);
                        } else {
                            themeView.changeThemeImage("");
                        }
                    }
                    gifPosition++;

                    handler.sendEmptyMessageDelayed(GIF_START, 100);
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_cancel) {
            goBack();
        } else if (v.getId() == R.id.edit_sure) {
            handler.sendEmptyMessage(VIDEO_PAUSE);
            new AsyncSumbit().execute();
        } else if (v.getId() == R.id.iv_play) {
            iv_play.setVisibility(View.GONE);
            handler.sendEmptyMessageDelayed(VIDEO_PLAY, 66);
        } else if (v.getId() == R.id.rl_play) {
            if (iv_play.getVisibility() == View.VISIBLE) {
                iv_play.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(VIDEO_PLAY, 66);
            } else {
                handler.sendEmptyMessage(VIDEO_PAUSE);
            }
        }
    }

    private class AsyncSumbit extends AsyncTask<Void, Integer, Void> {
        private DialogCircleProgress dialogCircleProgress = null;

        @Override
        protected void onPreExecute() {
            dialogCircleProgress = new DialogCircleProgress(ThemeEditActivity.this);
            InitView.setDialogMatchParent(dialogCircleProgress);
            dialogCircleProgress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < themeViewList.size(); i++) {
                ThemeView themeView = themeViewList.get(i);
                int minFrame = themeView.getMinFrame();
                int maxFrame = themeView.getMaxFrame();
                ThemeTypeInfo themeTypeInfo = themeView.getThemeTypeInfo();

                for (int j = minFrame; j <= maxFrame; j++) {
                    synchronized (ThemeEditActivity.this) {
                        int index = j % themeTypeInfo.getFrames().size();
                        String filePath = getImagePath(themeTypeInfo.getName(), themeTypeInfo
                                .getFrames().get(index).getPic());

                        Bitmap background = getBitMapIcon(j, true);
                        Bitmap foreground = BitmapFactory.decodeFile(filePath);

                        Bitmap combine = BitmapUtil.combineTheme(ThemeEditActivity.this, background, foreground, themeView.getMatrix());
                        Long handle = BitmapUtil.bitmap2yuv(combine);
                        AVProcessing.deleteDataFrame(videoFrameList.get(j));
                        videoFrameList.set(j, handle);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialogCircleProgress.dismiss();
            goBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}