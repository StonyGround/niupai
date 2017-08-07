package com.xiuxiu.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiuxiu.R;
import com.xiuxiu.adapter.ThemeRecyclerViewAdapter;
import com.xiuxiu.adapter.ThemeVrayRecyclerViewAdapter;
import com.xiuxiu.model.ThemeInfo;
import com.xiuxiu.model.ThemePhotoItem;
import com.xiuxiu.model.ThemePhotoItemInfo;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.CacheUtils;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;
import com.xiuxiu.view.ImageRenderView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThemeDialogFragmentWindow extends DialogFragment {

    private ImageView ivThemeClose;
    private ImageView tvThemeAffirm;
    private RecyclerView rvThemeVray, rvTheme;

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

    private int listIndex = 0, frameIndex = 0, frameThreshold = 0, yuvFrameSize = 0, mainFrameSize = 0;


    private ThemeVrayRecyclerViewAdapter mThemeVrayRecyclerViewAdapter;
    private ThemeRecyclerViewAdapter mThemeRecyclerViewAdapter;

    private List<ThemePhotoItemInfo> mThemeInfo;

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
        View view = inflater.inflate(R.layout.theme_dialog_fragment_window, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ivThemeClose = (ImageView) view.findViewById(R.id.iv_theme_close);
        tvThemeAffirm = (ImageView) view.findViewById(R.id.iv_theme_affirm);
        rvThemeVray = (RecyclerView) view.findViewById(R.id.rv_theme_vray);
        rvTheme = (RecyclerView) view.findViewById(R.id.rv_theme);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvThemeVray.setLayoutManager(layoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTheme.setLayoutManager(linearLayoutManager);

        // 设置宽度为屏宽、靠近屏幕底部。
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

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

        ivThemeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭弹出窗口
                dismiss();
            }
        });

        tvThemeAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                submitDialogListener.onClick(CacheUtils.getString(getActivity(), XConstant.CURRENT_SELECT_MUSIC));
                dismiss();
            }
        });

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CacheUtils.setString(getActivity(), XConstant.CURRENT_SELECT_MUSIC, "");
            }
        });

        Bundle bundle = getActivity().getIntent().getExtras();
        videoFrameList = bundle.getIntegerArrayList("videoFrameList");
//        y_data = bundle.getByteArray("y_data");
//        u_data = bundle.getByteArray("u_data");
//        v_data = bundle.getByteArray("v_data");
//        yuvFrameSize = bundle.getInt("yuvFrameSize");
        getThemeGUI();
        getThemeData();
        return view;
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
    }

    //主题
    private void getThemeGUI() {
        //获取所有帧图片
//        mThemeVrayRecyclerViewAdapter = new ThemeVrayRecyclerViewAdapter(getActivity(), videoFrameList);
//        rvThemeVray.setAdapter(mThemeVrayRecyclerViewAdapter);
//        mThemeVrayRecyclerViewAdapter.setOnItemClickListener(new ThemeVrayRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position, List<Integer> data) {
//                int handle = videoFrameList.get(position);
//                if (handle != 0) {
//                    // yuv数据填充
//                    AVProcessing.copyYUVData(handle, y_data, u_data, v_data, yuvFrameSize);
//                    yBuf.put(y_data).position(0);
//                    uBuf.put(u_data).position(0);
//                    vBuf.put(v_data).position(0);
//
//                    imageRenderView.setRenderFinished(false);
////                    imageRenderView.updateTextureData(yBuf, uBuf, vBuf, mBuf, lBuf, frameIndex, frameThreshold);
//                }
//            }
//        });

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

//        int index = 0;

//        //主题icon
//        mThemeRecyclerViewAdapter = new ThemeRecyclerViewAdapter(getActivity(), ThemePhotoItem.addList);
//        rvTheme.setAdapter(mThemeRecyclerViewAdapter);
//        mThemeRecyclerViewAdapter.setOnItemClickListener(new ThemeRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position, ThemePhotoItemInfo data) {
//
//            }
//        });

    }

    //请求主题icon
    private void getThemeData() {
        String themeUrl = XConstant.HOST + "restheme.xml";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(themeUrl);
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ThemeService", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String themes = response.toString();
                Log.e("ThemeService", themes);
            }
        });
    }

    private void initVideoEdit() {
        // 分配内存
        mainFrameSize = frameWidth * frameHeight;
        yuvFrameSize = mainFrameSize;

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

        // 计算视频帧数
//        int breakPoints = 0;
//        for (Integer handle : videoFrameList) {
//            if (handle == 0) {
//                breakPoints++;
//            }
//        }
//
//        int frameCount = videoFrameList.size() - breakPoints;
//
//        Log.d("dd_cc_dd", "..initVideoEdit..frameCount.." + frameCount);
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
