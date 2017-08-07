package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Process;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.util.AVProcessing;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;


// 摄像头数据采集及图层绘制
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback, Runnable {
    // 源视频帧宽/高
    private int srcFrameWidth = XConstant.FRAME_SRC_WIDTH;
    private int srcFrameHeight = XConstant.FRAME_SRC_HEIGHT;

    // 数据采集
    private int curCameraIndex = XConstant.CAMERA_FRONT;
    private byte[] src_yuv = null;
    private Camera camera = null;
    private volatile boolean isDataPrepared = false;
    // 视频帧共享存储回调接口
    private SurfaceHolder surfaceHolder;
    private SaveFrameCallback saveFrameCallback = null;
    // 帧数据保存线程
    volatile boolean saveFramesThreadRunning = false;
    private Thread saveFramesThread = null;
    private Context context = null;
    private RecordActivity recordCtrl = null;

    public CameraView(Context _context) {
        super(_context);
        context = _context;
    }

    public CameraView(Context _context, RecordActivity _recordCtrl) {
        super(_context);

        context = _context;
        recordCtrl = _recordCtrl;
        if (Camera.getNumberOfCameras() > 1) {
            curCameraIndex = XConstant.CAMERA_FRONT;
        } else {
            curCameraIndex = XConstant.CAMERA_BACK;
        }

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder sh) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder sh, int format, int width, int height) {
        if (Util.getScreenWidth(context) == width) {
            srcFrameWidth = XConstant.FRAME_SRC_WIDTH;
            srcFrameHeight = XConstant.FRAME_SRC_HEIGHT;
        } else {
            srcFrameWidth = XConstant.FRAME_SRC_WIDTH >> 1;
            srcFrameHeight = XConstant.FRAME_SRC_HEIGHT >> 1;
        }

        src_yuv = new byte[srcFrameWidth * srcFrameHeight * 3 / 2];

        stopCamera();
        startCamera(curCameraIndex);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder sh) {
        stopCamera();
    }

    // 根据索引初始化摄像头
    public void initCamera(int cameraIndex) {
        // 初始化并打开摄像头
        if (camera == null) {
            try {
                camera = Camera.open(cameraIndex);
            } catch (Exception e) {
                camera = null;
                saveFramesThreadRunning = false;
                recordCtrl.sendRecordMessage(XConstant.RECORD_STATUS_ERROR, "无法打开摄像头。您可能已经禁用了91拍摄像头权限，或摄像头被其他程序占用。");
                return;
            }

            Camera.Parameters params = camera.getParameters();
            if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                // 自动对焦
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            try {
                params.setPreviewFormat(ImageFormat.NV21);
                params.setPreviewSize(srcFrameWidth, srcFrameHeight);
                camera.setParameters(params);

                params = camera.getParameters();
                params.setPreviewFpsRange(15 * 1000, 30 * 1000);
                camera.setParameters(params);
            } catch (Exception e) {
            }

            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            camera.startPreview();
            camera.setPreviewCallback(this);
            camera.setDisplayOrientation(90);
            AVProcessing.setRotateClipFunc(cameraIndex);

            // 开启数据帧保存线程
            if (!saveFramesThreadRunning) {
                saveFramesThreadRunning = true;
                saveFramesThread = new Thread(this);
                saveFramesThread.start();
            }
        }
    }

    // 摄像头切换
    public boolean switchCamera(int cameraIndex) {
        if (Camera.getNumberOfCameras() > 1) {
            curCameraIndex = cameraIndex;
            stopCamera();
            startCamera(curCameraIndex);

            return true;
        } else {
            return false;
        }
    }

    // 获取摄像头个数
    public int getCameraCount() {
        return Camera.getNumberOfCameras();
    }

    // 打开摄像头
    private void startCamera(int cameraIndex) {
        // new AsyncStartCamera().execute(cameraIndex);

        initCamera(cameraIndex);
    }

    // 锁屏/解锁时>>调用startPreview()/stopPreview()为主
    public void startPreview() {
        if (camera != null) {
            camera.startPreview();
            camera.setPreviewCallback(this);
        }

        // 开启数据帧保存线程
        if (!saveFramesThreadRunning) {
            saveFramesThreadRunning = true;
            saveFramesThread = new Thread(this);
            saveFramesThread.start();
        }
    }

    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
        }

        saveFramesThreadRunning = false;
    }

    // 停止并释放摄像头
    public void stopCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        isDataPrepared = false;
        saveFramesThreadRunning = false;
    }

    // 对焦(不支持自动对焦的机型,可选择手动对焦)
    public void autoFocus() {
        if (camera != null) {
            camera.autoFocus(null);
        }
    }

    // 设置背景颜色
    private void setBackColor(String strColor) {
        this.setBackgroundColor(Color.parseColor(strColor));
    }

    // 打开/关闭闪光灯
    public boolean switchFlashLight(boolean isFlashLightOn) {
        if (camera != null) {
            try {
                Camera.Parameters params = camera.getParameters();
                if (isFlashLightOn) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }

                camera.setParameters(params);

                return true;
            } catch (Exception ex) {
                // ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    // 获取摄像头视频数据
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (this) {
            isDataPrepared = true;
            System.arraycopy(data, 0, src_yuv, 0, data.length);
        }
    }

    // 数据帧保存线程
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);

        while (saveFramesThreadRunning) {
            try {
                // 大于每秒15帧
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 保存数据帧
            synchronized (this) {
                if (saveFrameCallback != null && isDataPrepared) {
                    saveFrameCallback.onSaveFrames(src_yuv, 0);
                }
            }
        }
    }

    // 保存视频帧
    public interface SaveFrameCallback {
        public void onSaveFrames(byte[] data, int length);
    }

    public void setSaveFrameCallback(SaveFrameCallback saveFrameCallback) {
        this.saveFrameCallback = saveFrameCallback;
    }

    // 打开摄像头
    public class AsyncStartCamera extends AsyncTask<Integer, Integer, Void> {
        // 预处理
        @Override
        protected void onPreExecute() {
            setBackColor("#000000");
        }

        // 数据处理
        @Override
        protected Void doInBackground(Integer... params) {
            initCamera(params[0]);
            return null;
        }

        // 更新UI
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        // 处理结束
        @Override
        protected void onPostExecute(Void result) {
            setBackColor("#00000000");
        }
    }// class AsyncStartCamera
}
