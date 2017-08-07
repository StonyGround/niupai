package com.xiuxiu.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xiuxiu.R;
import com.xiuxiu.adapter.CharacterRecyclerViewAdapter;
import com.xiuxiu.adapter.CharacterVrayRecyclerViewAdapter;
import com.xiuxiu.model.CharacterPhotoItem;
import com.xiuxiu.model.CharacterPhotoItemInfo;
import com.xiuxiu.model.IconButtonInfo;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.ImageRenderView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class CharacterDialogFragmentWindow extends DialogFragment {

    private ImageView tvCharacterClose;
    private ImageView tvCharacterAffirm;
    private RecyclerView rvCharacterVray, rvCharacter;

    private RelativeLayout.LayoutParams layoutParams = null;
    private RelativeLayout videoEditBody = null;
    private RelativeLayout videoPreviewLayout = null;

    private SubmitDialogListener submitDialogListener;


    private int frameWidth = XConstant.FRAME_DST_WIDTH;
    private int frameHeight = XConstant.FRAME_DST_HEIGHT;
    // 录制模式
    private ImageRenderView imageRenderView = null;
    private List<Integer> videoFrameList = null;
    private byte[] y_data = null, u_data = null, v_data = null;
    private ByteBuffer yBuf = null, uBuf = null, vBuf = null, mBuf = null, lBuf = null;

    private int frameIndex = 0, frameThreshold = 0, yuvFrameSize = 0, mainFrameSize = 0;

    private int[] rgbData = null;
    private byte[] coverData = null;
    private Bitmap cameraBmp;


    private int halfFrameWidth = XConstant.FRAME_SRC_HEIGHT >> 1;
    private int halfFrameHeight = XConstant.FRAME_SRC_WIDTH >> 1;

    private CharacterVrayRecyclerViewAdapter characterVrayRecyclerViewAdapter = null;
    private CharacterRecyclerViewAdapter characterRecyclerViewAdapter = null;

    private List<CharacterPhotoItemInfo> resultBeanList = new ArrayList<>();

    private List<Bitmap> bitmapList;

    private String videoFilePath = null, coverFilePath = null;

    public void setSubmitDialogListener(SubmitDialogListener mListener) {
        submitDialogListener = mListener;
    }

    public interface SubmitDialogListener {
        void onClick(String title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initVideoEdit();
        getDialog().setCanceledOnTouchOutside(true);
        //无标题
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.character_dialog_fragment_window, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //透明状态栏
//        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        tvCharacterClose = (ImageView) view.findViewById(R.id.tv_character_close);
        tvCharacterAffirm = (ImageView) view.findViewById(R.id.tv_character_affirm);
        rvCharacterVray = (RecyclerView) view.findViewById(R.id.rv_character_vray);
        rvCharacter = (RecyclerView) view.findViewById(R.id.rv_character);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacterVray.setLayoutManager(mLinearLayoutManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvCharacter.setLayoutManager(linearLayoutManager);

        // 设置宽度为屏宽、靠近屏幕底部。
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        Bundle bundle = getActivity().getIntent().getExtras();
        videoFrameList = bundle.getIntegerArrayList("videoFrameList");

        tvCharacterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭弹出窗口
                dismiss();
            }
        });

        tvCharacterAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CacheUtils.setString(getActivity(), XConstant.CURRENT_SELECT_MUSIC, "");
            }
        });
        int viewWidth = Util.getScreenWidth(getActivity());
        int viewHeight = (int) (viewWidth * 4 / 3);// 帧宽高比为4:3

        // 初始化视频预览;
        videoEditBody = (RelativeLayout) view.findViewById(R.id.videoEditBody);
        layoutParams = (RelativeLayout.LayoutParams) videoEditBody.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        videoEditBody.setLayoutParams(layoutParams);

        videoPreviewLayout = (RelativeLayout) view.findViewById(R.id.videoPreviewLayout);
        layoutParams = (RelativeLayout.LayoutParams) videoPreviewLayout.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;

        imageRenderView = new ImageRenderView(getActivity(), frameWidth, frameHeight);
        videoPreviewLayout.addView(imageRenderView, layoutParams);
//        initData();

        frameWidth = Util.getScreenWidth(getActivity())/8;
        frameHeight = frameWidth/3*4;

        bitmapList = new ArrayList<>();
        getBitMapIcon();
        getCharacterData();
        return view;
    }

    private void getBitMapIcon(){

        for (int i = 0; i < videoFrameList.size(); i++) {
            int handles = videoFrameList.get(i);
            if (handles != 0) {
                AVProcessing.yuv2rgb(handles, rgbData, frameWidth, frameHeight);
                for (int j = 0; j < frameHeight; j++) {
                    for (int k = 0; k < frameWidth; k++) {
                        rgbData[j * frameWidth + k] = 255;
                        rgbData[j * frameWidth + k] = (rgbData[j * frameWidth + k] << 8) + coverData[(j * frameWidth + k) * 3 + 2]; //+r
                        rgbData[j * frameWidth + k] = (rgbData[j * frameWidth + k] << 8) + coverData[(j * frameWidth + k) * 3 + 1]; //+g
                        rgbData[j * frameWidth + k] = (rgbData[j * frameWidth + k] << 8) + coverData[(j * frameWidth + k) * 3 + 0]; //+b
                    }
                }
                Bitmap cameraBmp = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
                cameraBmp.setPixels(rgbData, 0, frameWidth, 0, 0, frameWidth, frameHeight);
                bitmapList.add(cameraBmp);
                Log.e("bitmapList", cameraBmp + "-----" + i);
            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout( dm.widthPixels,  getDialog().getWindow().getAttributes().height );
    }

    private void initData(){

    }

    public void getCharacterData(){

        characterVrayRecyclerViewAdapter = new CharacterVrayRecyclerViewAdapter(getActivity(), bitmapList);
        rvCharacterVray.setAdapter(characterVrayRecyclerViewAdapter);
        characterVrayRecyclerViewAdapter.setOnItemClickListener(new CharacterVrayRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int handle = videoFrameList.get(position);
                if (handle != 0) {
                    // yuv数据填充
                    AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
                    yBuf.put(y_data).position(0);
                    uBuf.put(u_data).position(0);
                    vBuf.put(v_data).position(0);

                    imageRenderView.setRenderFinished(false);
//                    imageRenderView.updateTextureData(yBuf, uBuf, vBuf, mBuf, lBuf, frameIndex, frameThreshold);
                }
            }
        });

        //获取第一帧的图片
        int index = 0;
        int handle = videoFrameList.get(index);
        if (handle != 0) {
            // yuv数据填充
            AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
            yBuf.put(y_data).position(0);
            uBuf.put(u_data).position(0);
            vBuf.put(v_data).position(0);

            imageRenderView.setRenderFinished(false);
//            imageRenderView.updateTextureData(yBuf, uBuf, vBuf, mBuf, lBuf, frameIndex, frameThreshold);
        }

//        Integer[] resId = {R.drawable.bubble2,
//                R.drawable.bubble2, R.drawable.bubble2, R.drawable.bubble2,
//                R.drawable.bubble2, R.drawable.bubble2, R.drawable.bubble2};



        //文字icon
//        characterRecyclerViewAdapter = new CharacterRecyclerViewAdapter(getActivity(), CharacterPhotoItem.addList);
        rvCharacter.setAdapter(characterRecyclerViewAdapter);
//        characterRecyclerViewAdapter.setOnItemClickListener(new CharacterRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position, CharacterPhotoItemInfo data) {
//                Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    private void initVideoEdit() {
        // 分配内存
        mainFrameSize = frameWidth * frameHeight;
        yuvFrameSize = mainFrameSize;

        coverData = new byte[mainFrameSize * 4];

        int qtrFrameSize = yuvFrameSize >> 2;
        y_data = new byte[yuvFrameSize];
        u_data = new byte[qtrFrameSize];
        v_data = new byte[qtrFrameSize];

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

        rgbData = new int[yuvFrameSize];
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
    public void onResume() {
        super.onResume();
    }
}
