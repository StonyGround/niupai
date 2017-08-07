package com.xiuxiu.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.xiuxiu.R;
import com.xiuxiu.adapter.CharacterRecyclerViewAdapter;
import com.xiuxiu.adapter.CharacterVrayRecyclerViewAdapter;
import com.xiuxiu.dialog.DialogAddText;
import com.xiuxiu.dialog.DialogCircleProgress;
import com.xiuxiu.dialog.DialogCopy;
import com.xiuxiu.dialog.InitView;
import com.xiuxiu.model.FrameListModel;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.BitmapUtil;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.BubbleTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


// 视频文字编辑页面
public class TextEditActivity extends Activity implements View.OnClickListener {

    private static final String TAG="TextEditActivity";

    //添加文字or图片 1文字 2图片
    private int type;
    private List<Long> videoFrameList;
    private ImageView image_preview, edit_cancel, edit_sure;
    private RecyclerView rv_small, rv_image;
    private RelativeLayout contentLayout, videoPreviewLayout;
    private BubbleTextView mCurrentEditTextView;

    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<Bitmap> previewList = new ArrayList<>();

    private CharacterVrayRecyclerViewAdapter characterVrayRecyclerViewAdapter;
    private CharacterRecyclerViewAdapter characterRecyclerViewAdapter;

    private int perTextX = 0;
    private int curFramePos = 0;
    private int[] textBgArr = {R.drawable.bubble1, R.drawable.bubble2, R.drawable.bubble3, R.drawable.bubble4, R
            .drawable.bubble5, R.drawable.bubble6, R.drawable.bubble7, R.drawable.bubble8, R.drawable.bubble9, R.drawable.bubble10, R
            .drawable.bubble11};

    private int[] imageBgArr = {R.drawable.theme01, R.drawable.theme02, R.drawable.theme03, R.drawable.theme04, R
            .drawable.theme05, R.drawable.theme06, R.drawable.theme07, R.drawable.theme08, R.drawable.theme09, R.drawable.theme10, R
            .drawable.theme11, R.drawable.theme12, R.drawable.theme13};

    private HashMap<Integer, List<BubbleTextView>> textEditModelMap = new HashMap<Integer, List<BubbleTextView>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_text_edit_layout);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 1);
        FrameListModel frameListModel = intent.getParcelableExtra("videoFrameList");
        videoFrameList = frameListModel.getFrameList();

        image_preview = (ImageView) findViewById(R.id.image_preview);
        edit_cancel = (ImageView) findViewById(R.id.edit_cancel);
        edit_sure = (ImageView) findViewById(R.id.edit_sure);
        rv_small = (RecyclerView) findViewById(R.id.rv_small);
        rv_image = (RecyclerView) findViewById(R.id.rv_image);
        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        videoPreviewLayout = (RelativeLayout) findViewById(R.id.videoPreviewLayout);
        edit_cancel.setOnClickListener(this);
        edit_sure.setOnClickListener(this);
        LinearLayoutManager sLinearLayoutManager = new LinearLayoutManager(this);
        sLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_small.setLayoutManager(sLinearLayoutManager);
        LinearLayoutManager iLinearLayoutManager = new LinearLayoutManager(this);
        iLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_image.setLayoutManager(iLinearLayoutManager);

        new AsyncGetBitmap().execute();
        image_preview.setImageBitmap(getBitMapIcon(0, true));

        if (type == 1) {
            characterRecyclerViewAdapter = new CharacterRecyclerViewAdapter(this, textBgArr);
        } else {
            characterRecyclerViewAdapter = new CharacterRecyclerViewAdapter(this, imageBgArr);
        }
        rv_image.setAdapter(characterRecyclerViewAdapter);
        characterRecyclerViewAdapter.setOnItemClickListener(new CharacterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                int resId;
                if (type == 1) {
                    resId = textBgArr[position];
                } else {
                    resId = imageBgArr[position];
                }
                // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                /*boolean isDubbing = CacheUtils.getBooleanFirst(TextEditActivity.this, XConstant.GUIDE_DUBBING);
                if (isDubbing) {
                    DialogGuide dialogGuideDubbing = new DialogGuide(TextEditActivity.this, R.drawable.tip_dubbing);
                    dialogGuideDubbing.show();

                    CacheUtils.setBooleanFirst(TextEditActivity.this, XConstant.GUIDE_DUBBING, false);
                }else {
                    createBubble(resId, true, false, curFramePos, null,null);
                }*/
                createBubble(resId, true, false, curFramePos, null,null);
//                startActivity(new Intent(TextEditActivity.this, CSDNActivity.class));
            }
        });
    }

    //初始化预览
    private class AsyncGetBitmap extends AsyncTask<Void, Integer, Void> {

        private TextView textInfo = null;
        private ProgressBar progressBar = null;
        private Dialog processingInfoDialog = null;

        @Override
        protected void onPreExecute() {
            processingInfoDialog = new Dialog(TextEditActivity.this, R.style.Dialog_Xiu);
            processingInfoDialog.setContentView(R.layout.finish_record_processing_progress);
            Window dialogWindow = processingInfoDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int) (TextEditActivity.this.getResources().getDisplayMetrics().density * 240);
            layoutParams.height = (int) (TextEditActivity.this.getResources().getDisplayMetrics().density * 80);
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
            int count = videoFrameList.size();
            for (int i = 0; i < count; i++) {
                Bitmap bitmap = getBitMapIcon(i, false);
//                byte[] yuv = new byte[XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2];
//                AVProcessing.copyFrameData(videoFrameList.get(i), yuv, XConstant.FRAME_DST_WIDTH *
//                        XConstant.FRAME_DST_HEIGHT * 3 / 2);
//                Bitmap bitmap = BitmapUtil.yuv2bitmap(yuv, XConstant.FRAME_DST_WIDTH, XConstant.FRAME_DST_HEIGHT);
                if (bitmap != null) {
                    bitmapList.add(bitmap);
                    previewList.add(bitmap);
                    publishProgress(i * 100 / count);
                }
            }
            Log.d("bitmapList", bitmapList.size() + "---");
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
            characterVrayRecyclerViewAdapter = new CharacterVrayRecyclerViewAdapter(TextEditActivity.this, previewList);
            rv_small.setAdapter(characterVrayRecyclerViewAdapter);
            characterVrayRecyclerViewAdapter.setOnItemClickListener(new CharacterVrayRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    contentLayout.removeAllViews();
                    for (Map.Entry entry : textEditModelMap.entrySet()) {
                        int key = (int) entry.getKey();

                        //合成上帧预览图
                        if (key == curFramePos) {
                            List<BubbleTextView> bubbleTextViewList = (List<BubbleTextView>) entry.getValue();
                            Bitmap backBitmap = bitmapList.get(key);
                            for (BubbleTextView bubbleTextView : bubbleTextViewList) {
                                Bitmap combine = BitmapUtil.combineBitmap(TextEditActivity.this, backBitmap, bubbleTextView);
                                backBitmap = combine;
                            }
                            characterVrayRecyclerViewAdapter.replace(curFramePos, backBitmap);
                        }

                        //创建当前帧文本框
                        if (key == position) {
                            Log.d("onPostExecute", "onItemClick: " + key);
                            List<BubbleTextView> bubbleTextViewList = (List<BubbleTextView>) entry.getValue();
                            for (BubbleTextView bubbleTextView : bubbleTextViewList) {
                                int resId = bubbleTextView.getBubbleId();
                                createBubble(resId, false, false, 0, bubbleTextView,null);
                            }
                        }
                    }

                    curFramePos = position;
                    image_preview.setImageBitmap(getBitMapIcon(position, true));

                    characterVrayRecyclerViewAdapter.updateSelect(position);
                    characterVrayRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
            processingInfoDialog.dismiss();
        }
    }

    //map是否存在当前页
    private boolean isExist(int position) {
        for (Map.Entry entry : textEditModelMap.entrySet()) {
            int key = (int) entry.getKey();
            if (key == position) return true;
        }
        return false;
    }

    //添加or新增
    private void addBubble(int frame, BubbleTextView bubbleTextView) {
        if (isExist(frame)) {
            for (Map.Entry entry : textEditModelMap.entrySet()) {
                int key = (int) entry.getKey();
                if (key == frame) {
                    List<BubbleTextView> bubbleTextViewList = (List<BubbleTextView>) entry.getValue();
                    bubbleTextViewList.add(bubbleTextView);
                    textEditModelMap.put(frame, bubbleTextViewList);
                }
            }
        } else {
            List<BubbleTextView> bubbleTextViewList = new ArrayList<>();
            bubbleTextViewList.add(bubbleTextView);
            textEditModelMap.put(frame, bubbleTextViewList);
        }
    }

    //创建文本框
    private void createBubble(int resId, boolean isCreate, boolean isCopy, int frame, @Nullable BubbleTextView model,@Nullable Bitmap originBitmap) {
        BubbleTextView bubbleTextView;
        if (!isCreate && model != null) {
            bubbleTextView = model;
        } else {
            int fontColor = Color.WHITE;
            if (resId == R.drawable.bubble1 || resId == R.drawable.bubble8) {
                fontColor = Color.BLACK;
            }
            bubbleTextView = new BubbleTextView(TextEditActivity.this, fontColor, resId);
            if (isCopy) {
                bubbleTextView.setImageResource(model,originBitmap);
            } else {
                bubbleTextView.setImageResource(resId);
            }
            addBubble(frame, bubbleTextView);
            bubbleTextView.setOperationListener(new BubbleTextView.OperationListener() {
                @Override
                public void onDeleteClick(BubbleTextView bubbleTextView) {
//                Log.d("BubbleTextView", "onDeleteClick before size" + textEditModelMap.size());
                    for (Iterator<Map.Entry<Integer, List<BubbleTextView>>> it = textEditModelMap.entrySet().iterator(); it.hasNext();
                            ) {
                        Map.Entry<Integer, List<BubbleTextView>> item = it.next();
                        int key = item.getKey();
                        if (key == curFramePos) {
                            List<BubbleTextView> bubbleTextViewList = item.getValue();
                            bubbleTextViewList.remove(bubbleTextView);
                        }
                    }
                }

                @Override
                public void onEdit(BubbleTextView bubbleTextView) {
                    Log.d(TAG, "onEdit: ");
                    mCurrentEditTextView.setInEdit(false);
                    mCurrentEditTextView = bubbleTextView;
                    mCurrentEditTextView.setInEdit(true);

                }

                @Override
                public void onClick(final BubbleTextView bubbleTextView) {
                    final DialogAddText dialogAddText = new DialogAddText(TextEditActivity.this, bubbleTextView.getmStr());
                    InitView.initiBottomDialog(dialogAddText);
                    InitView.setDialogMatchParent(dialogAddText);
                    dialogAddText.show();
                    dialogAddText.setNoticeDialogListerner(new DialogAddText.NoticeDialogListener() {
                        @Override
                        public void onClick(String text) {
                            bubbleTextView.setText(text);
                        }
                    });

                }

                @Override
                public void onTop(final BubbleTextView bubbleTextView) {
                    final DialogCopy dialogCopy = new DialogCopy(TextEditActivity.this);
                    InitView.initiBottomDialog(dialogCopy);
                    InitView.setDialogMatchParent(dialogCopy);
                    dialogCopy.show();
                    dialogCopy.setCopyFiveListener(new DialogCopy.CopyFiveListener() {
                        @Override
                        public void onClick() {
                            combine(1, bubbleTextView);
                            dialogCopy.dismiss();
                        }
                    });
                    dialogCopy.setCopyTenListener(new DialogCopy.CopyTenListener() {
                        @Override
                        public void onClick() {
                            combine(10, bubbleTextView);
                            dialogCopy.dismiss();
                        }
                    });
                    dialogCopy.setCopyAllListener(new DialogCopy.CopyAllListener() {
                        @Override
                        public void onClick() {
                            combine(bitmapList.size(), bubbleTextView);
                            dialogCopy.dismiss();
                        }
                    });

                }
            });
        }

        if (!isCopy) {
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rl.addRule(RelativeLayout.CENTER_IN_PARENT);
            contentLayout.addView(bubbleTextView, rl);
            setCurrentEdit(bubbleTextView);
        }
    }

    //选中当前文本框
    private void setCurrentEdit(BubbleTextView bubbleTextView) {
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView.setInEdit(false);
        }
        mCurrentEditTextView = bubbleTextView;
        mCurrentEditTextView.setInEdit(true);
    }

    //获取bitmap预览图
    private Bitmap getBitMapIcon(int position, boolean isOrigin) {
        long handle = videoFrameList.get(position);
        if (handle != 0) {
            int bitMapWidth = XConstant.FRAME_DST_WIDTH;
            int bitMapHeight = XConstant.FRAME_DST_HEIGHT;
            int rgbFrameSize = bitMapWidth * bitMapHeight;
            int[] rgbData = new int[rgbFrameSize];
            Bitmap cameraBmp = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_4444);
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

    //返回上个页面
    private void goBack() {
        Intent intent = new Intent();
        FrameListModel frameListModel = new FrameListModel();
        frameListModel.setFrameList(videoFrameList);
        intent.putExtra("videoFrameList", frameListModel);
        setResult(XConstant.TEXT_ACTIVITY_RESULT, intent);
        finish();
    }

    //copy、合并图层
    private void combine(int count, BubbleTextView bubbleTextView) {
        new AsyncComnineBitmap(bubbleTextView, count).execute();
    }

    private class AsyncComnineBitmap extends AsyncTask<Void, Void, Void> {

        private BubbleTextView bubbleTextView;
        private int count;
        private Bitmap originBitmap;

        AsyncComnineBitmap(BubbleTextView bubbleTextView, int count) {
            this.bubbleTextView = bubbleTextView;
            this.count = count;
        }

        private DialogCircleProgress dialogCircleProgress = null;

        @Override
        protected void onPreExecute() {
            dialogCircleProgress = new DialogCircleProgress(TextEditActivity.this);
            InitView.setDialogMatchParent(dialogCircleProgress);
            dialogCircleProgress.show();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            originBitmap = BitmapFactory.decodeResource(getResources(), bubbleTextView.getBubbleId(), options);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int videoSize = previewList.size();
            if (curFramePos + count + 1 < videoSize) {
                videoSize = curFramePos + count + 1;
            }

            if (count == previewList.size()) {
                for (int i = 0; i < videoSize; i++) {
                    Bitmap combine = BitmapUtil.combineBitmap(TextEditActivity.this, previewList.get(i), bubbleTextView);
                    previewList.set(i, combine);
                    if (i != curFramePos) {
                        createBubble(bubbleTextView.getBubbleId(), true, true, i, bubbleTextView,originBitmap);
                    }
                }
            } else {
                for (int i = curFramePos; i < videoSize; i++) {
                    Bitmap combine = BitmapUtil.combineBitmap(TextEditActivity.this, previewList.get(i), bubbleTextView);
                    previewList.set(i, combine);
                    if (i != curFramePos) {
                        createBubble(bubbleTextView.getBubbleId(), true, true, i, bubbleTextView,originBitmap);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            characterVrayRecyclerViewAdapter.notifyDataSetChanged();
            dialogCircleProgress.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_cancel) {
            goBack();
        } else if (v.getId() == R.id.edit_sure) {
            new AsyncSubmit().execute();
        }
    }

    //提交合成
    private class AsyncSubmit extends AsyncTask<Void, Integer, Void> {

        private DialogCircleProgress dialogCircleProgress = null;

        @Override
        protected void onPreExecute() {
            dialogCircleProgress = new DialogCircleProgress(TextEditActivity.this);
            InitView.setDialogMatchParent(dialogCircleProgress);
            dialogCircleProgress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(1);
            for (int i = 1; i <= 85; i++) {
                publishProgress(i);
            }
            for (Map.Entry entry : textEditModelMap.entrySet()) {
                synchronized (this) {
                    int key = (int) entry.getKey();
                    List<BubbleTextView> bubbleTextViewList = (List<BubbleTextView>) entry.getValue();
                    for (BubbleTextView bubbleTextView : bubbleTextViewList) {
                        Bitmap background = getBitMapIcon(key, true);
                        Bitmap combine = BitmapUtil.combineBitmap(TextEditActivity.this, background, bubbleTextView);
                        Long handle = BitmapUtil.bitmap2yuv(combine);
                        AVProcessing.deleteDataFrame(videoFrameList.get(key));
                        videoFrameList.set(key, handle);
                    }
                }
            }

            publishProgress(100);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialogCircleProgress.dismiss();
            goBack();
        }
    }
}
