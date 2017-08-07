package com.xiuxiu.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiuxiu.R;
import com.xiuxiu.adapter.CharacterRecyclerViewAdapter;
import com.xiuxiu.adapter.CharacterVrayRecyclerViewAdapter;
import com.xiuxiu.model.CharacterPhotoItemInfo;
import com.xiuxiu.model.FrameListModel;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.BitmapUtil;
import com.xiuxiu.util.ToolUtils;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.ImageRenderView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterActivity extends AppCompatActivity {


    private ImageView tvCharacterClose;
    private ImageView tvCharacterAffirm;
    private RecyclerView rvCharacterVray, rvCharacter;

    private RelativeLayout.LayoutParams layoutParams = null;
    private RelativeLayout videoEditBody = null;
    private RelativeLayout videoPreviewLayout = null;

    private int frameWidth = XConstant.FRAME_DST_WIDTH;
    private int frameHeight = XConstant.FRAME_DST_HEIGHT;
    // 录制模式
    private ImageRenderView imageRenderView = null;
    private byte[] y_data = null, u_data = null, v_data = null;
    private ByteBuffer yBuf = null, uBuf = null, vBuf = null, mBuf = null, lBuf = null;

    private int[] rgbData = null;
    private byte[] coverData = null;


    private int halfFrameWidth = XConstant.FRAME_SRC_HEIGHT >> 1;
    private int halfFrameHeight = XConstant.FRAME_SRC_WIDTH >> 1;

    private CharacterVrayRecyclerViewAdapter characterVrayRecyclerViewAdapter = null;
    private CharacterRecyclerViewAdapter characterRecyclerViewAdapter = null;

    private List<CharacterPhotoItemInfo> resultBeanList = new ArrayList<>();

    private List<Bitmap> bitmapList;

    private String videoFilePath = null, coverFilePath = null;

    private List<Long> videoFrameList = null;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        imageView = (ImageView) findViewById(R.id.image_test);

        FrameListModel frameListModel = getIntent().getParcelableExtra("videoFrameList");
        videoFrameList = frameListModel.getFrameList();
//        initView();
//        initData();
        int rgbFrameSize = XConstant.FRAME_SRC_WIDTH * XConstant.FRAME_SRC_HEIGHT;
        int[] rgbData = new int[rgbFrameSize];
        int halfFrameWidth = XConstant.FRAME_SRC_HEIGHT >> 1;
        int halfFrameHeight = XConstant.FRAME_SRC_WIDTH >> 1;

        int bitMapWidth = XConstant.FRAME_DST_WIDTH;
        int bitMapHeight = XConstant.FRAME_DST_HEIGHT;


        long handle = videoFrameList.get(0);
        if (handle != 0) {
            byte[] yuv = new byte[XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2];
            AVProcessing.copyFrameData(handle, yuv, XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2);
            Log.d("CharacterActivity", "yuv1------" + Arrays.toString(yuv));

            Bitmap cameraBmp = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);
//            AVProcessing.yuv2rgb(handle, rgbData, bitMapWidth, bitMapHeight);
            Log.d("CharacterActivity", "rgb1--" + Arrays.toString(rgbData));
            rgbData = BitmapUtil.yuv2rgb(yuv, bitMapWidth, bitMapHeight);
            Log.d("CharacterActivity", "rgb2--" + Arrays.toString(rgbData));
            cameraBmp.setPixels(rgbData, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
//            Bitmap cameraBmp = BitmapUtil.yuv2bitmap(yuv, XConstant.FRAME_DST_WIDTH, XConstant.FRAME_DST_HEIGHT);
            imageView.setImageBitmap(cameraBmp);
            bitmap2yuv(cameraBmp);
        }
    }

    private void bitmap2yuv(Bitmap cameraBmp) {
        int[] rgbData = new int[XConstant.FRAME_SRC_WIDTH * XConstant.FRAME_SRC_HEIGHT];
        int[] rgb = new int[XConstant.FRAME_SRC_WIDTH * XConstant.FRAME_SRC_HEIGHT];
        int bitMapWidth = XConstant.FRAME_DST_WIDTH;
        int bitMapHeight = XConstant.FRAME_DST_HEIGHT;

        byte[] yuv = new byte[XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2];
//        byte[] yuv = ToolUtils.getNV21S(bitMapWidth, bitMapHeight, cameraBmp);
//        Log.d("CharacterActivity", "yuv2------" + Arrays.toString(yuv));
        cameraBmp.getPixels(rgbData, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
        yuv = BitmapUtil.colorconvertRGB_IYUV_I420(rgbData, bitMapWidth, bitMapHeight);
//        BitmapUtil.encodeYUV420SP(yuv, rgbData, bitMapWidth, bitMapHeight);
//        yuv = Util.getYUV420sp(bitMapWidth, bitMapHeight, cameraBmp);
//        Integer h = AVProcessing.saveDataFrame(yuv, XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2);
        int yuvFrameSize = XConstant.FRAME_DST_WIDTH * XConstant.FRAME_DST_HEIGHT * 3 / 2;
//        Log.d("CharacterActivity", yuv.length + "yuv2------" + yuvFrameSize);
        Long handle = AVProcessing.saveDataFrame(yuv, yuvFrameSize);
//        Integer handle = ByteBuffer.wrap(yuv).getInt();
//        Log.d("CharacterActivity", handle + "------2");

//        Bitmap bitmap = BitmapUtil.yuv2bitmap(yuv, XConstant.FRAME_DST_WIDTH, XConstant.FRAME_DST_HEIGHT);

        AVProcessing.yuv2rgb(handle, rgb, bitMapWidth, bitMapHeight);
        Bitmap bitmap = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);
//        rgbData = BitmapUtil.yuv2rgb(yuv, bitMapWidth, bitMapHeight);
        bitmap.setPixels(rgb, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
        imageView.setImageBitmap(bitmap);
    }


    private void initData() {

    }

    private void initView() {

        tvCharacterClose = (ImageView) findViewById(R.id.tv_character_close);
        tvCharacterAffirm = (ImageView) findViewById(R.id.tv_character_affirm);
        rvCharacterVray = (RecyclerView) findViewById(R.id.rv_character_vray);
        rvCharacter = (RecyclerView) findViewById(R.id.rv_character);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacterVray.setLayoutManager(mLinearLayoutManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacter.setLayoutManager(linearLayoutManager);

    }
}
