package com.xiuxiu.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.xiuxiu.activity.VideoEditActivity;
import com.xiuxiu.util.AVProcessing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

// 图像渲染页面>>主要包括添加特效/综合绘制/读取缓存数据
public class ImageRenderView extends GLSurfaceView implements GLSurfaceView.Renderer {
    // 纹理id
    private int[] textureYId = new int[1];
    private int[] textureUId = new int[1];
    private int[] textureVId = new int[1];
    private int[] textureMId = new int[1];
    private int[] textureLId = new int[1];
    private int[] textureTId = new int[1];
    // shader属性变量handle
    private int aPositionCopy = 0, aTexCoordCopy = 0, uSTextureCopy = 0;
    private int aPositionMain = 0, aTexCoordMain = 0, uTransMatMain = 0, uYTextureMain = 0, uUTextureMain = 0, uVTextureMain = 0,
            uMTextureMain = 0, uTTextureMain = 0;
    private int programHandleMain = 0, programHandleBlur = 0, programHandleCopy = 0, programHandleLogo = 0;
    // 与绘制相关的参数
    private int viewWidth = 0, viewHeight = 0;
    private int frameWidth = 0, frameHeight = 0;
    private int halfFrameWidth = 0, halfFrameHeight = 0;
    private int curFilterIndex = 0, preFilterIndex = -999;
    private int frameIndex = 0, frameThreshold = 0;
    private FloatBuffer quadVertices = null;
    private ByteBuffer yPixels = null, uPixels = null, vPixels = null, mPixels = null, lPixels = null, tPixels = null, rgbBuf = null;
    private IntBuffer rgbPixels = null;
    private VideoEditActivity videoEditActivity = null;
    private Rect textEditRect = null;
    private boolean isTextEditClicked = false, isCopyFrameBuffer = false;
    volatile boolean isRenderFinished = false;

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

    // 单位矩阵
    private final float[] identityMat = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    // 模型变换矩阵
    private final float[] transformMat = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
    };

    // 顶点及纹理坐标
    private final float[] coords = {
        /*x,     y,    z,    u,    v*/
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    };

    public ImageRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageRenderView(Context context, int _frameWidth, int _frameHeight) {
        super(context);

        frameWidth = _frameWidth;
        frameHeight = _frameHeight;
        halfFrameWidth = frameWidth >> 1;
        halfFrameHeight = frameHeight >> 1;
        init(context);
    }

    // 初始化
    private void init(Context context) {
        // 顶点
        quadVertices = ByteBuffer.allocateDirect(coords.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        quadVertices.put(coords).position(0);

        // 设置opengl es基本渲染环境
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        textEditRect = new Rect();
        videoEditActivity = (VideoEditActivity) context;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
//        if (yPixels != null) {
//            // y纹理
//            yPixels.position(0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYId[0]);
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
//                    yPixels);
//
//            // u纹理
//            uPixels.position(0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUId[0]);
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, halfFrameWidth, halfFrameHeight, GLES20.GL_LUMINANCE,
//                    GLES20.GL_UNSIGNED_BYTE, uPixels);
//
//            // v纹理
//            vPixels.position(0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureVId[0]);
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, halfFrameWidth, halfFrameHeight, GLES20.GL_LUMINANCE,
//                    GLES20.GL_UNSIGNED_BYTE, vPixels);
//
//            // mask纹理
//            mPixels.position(0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMId[0]);
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
//                    mPixels);
//
//            tPixels.position(0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureTId[0]);
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
//                    tPixels);
//        }

        // 将图像渲染至fbo

        if (rgbPixels != null) {
            rgbPixels.position(0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYId[0]);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth * 3, frameHeight, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    rgbPixels);


            // mask纹理
            mPixels.position(0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMId[0]);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                    mPixels);

            tPixels.position(0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureTId[0]);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                    tPixels);
        }

        AVProcessing.bindFBO();
        AVProcessing.useShaderProgram(programHandleMain);
        if (curFilterIndex != preFilterIndex) {
            preFilterIndex = curFilterIndex;
            changeFilterShader(0);
        }

        // 上传数据
        quadVertices.position(0);
        GLES20.glVertexAttribPointer(aPositionMain, 3, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
        quadVertices.position(3);
        GLES20.glVertexAttribPointer(aTexCoordMain, 2, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
        if (isCopyFrameBuffer) {
            GLES20.glUniformMatrix4fv(uTransMatMain, 1, false, identityMat, 0);
        } else {
            GLES20.glUniformMatrix4fv(uTransMatMain, 1, false, transformMat, 0);
        }

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 添加logo及模糊特效至fbo
        if (frameIndex > frameThreshold) {
            AVProcessing.useFBOTexture();

            // 模糊
            AVProcessing.useShaderProgram(programHandleBlur);
            int aPositionBlur = AVProcessing.getShaderHandle("aPosition", programHandleBlur);
            int aTexCoordBlur = AVProcessing.getShaderHandle("aTexCoord", programHandleBlur);
            int uSTextureBlur = AVProcessing.getShaderHandle("uSTexture", programHandleBlur);

            GLES20.glUniform1i(uSTextureBlur, 0);
            GLES20.glEnableVertexAttribArray(aPositionBlur);
            GLES20.glEnableVertexAttribArray(aTexCoordBlur);
            quadVertices.position(0);
            GLES20.glVertexAttribPointer(aPositionBlur, 3, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
            quadVertices.position(3);
            GLES20.glVertexAttribPointer(aTexCoordBlur, 2, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);

            for (int i = 0; i < (frameIndex - frameThreshold) * 2; i++) {
                AVProcessing.bindFBO();
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }

            // 添加logo
            lPixels.position(0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureLId[0]);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                    lPixels);
            AVProcessing.useShaderProgram(programHandleLogo);

            int aPositionLogo = AVProcessing.getShaderHandle("aPosition", programHandleLogo);
            int aTexCoordLogo = AVProcessing.getShaderHandle("aTexCoord", programHandleLogo);
            int uLTextureLogo = AVProcessing.getShaderHandle("uLTexture", programHandleLogo);
            int uSTextureLogo = AVProcessing.getShaderHandle("uSTexture", programHandleLogo);
            int uTransMatLogo = AVProcessing.getShaderHandle("uTransMat", programHandleLogo);

            GLES20.glUniform1i(uLTextureLogo, 5);
            GLES20.glUniform1i(uSTextureLogo, 0);
            if (isCopyFrameBuffer) {
                GLES20.glUniformMatrix4fv(uTransMatLogo, 1, false, identityMat, 0);
            } else {
                GLES20.glUniformMatrix4fv(uTransMatLogo, 1, false, transformMat, 0);
            }
            GLES20.glEnableVertexAttribArray(aPositionLogo);
            GLES20.glEnableVertexAttribArray(aTexCoordLogo);
            quadVertices.position(0);
            GLES20.glVertexAttribPointer(aPositionLogo, 3, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
            quadVertices.position(3);
            GLES20.glVertexAttribPointer(aTexCoordLogo, 2, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);

            AVProcessing.bindFBO();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }// if

        if (isCopyFrameBuffer) {
            // 拷贝帧缓存数据
            GLES20.glReadPixels(0, 0, frameWidth, frameHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, rgbBuf);
        } else {
            // 显示fbo纹理
            AVProcessing.useShaderProgram(programHandleCopy);
            quadVertices.position(0);
            GLES20.glVertexAttribPointer(aPositionCopy, 3, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
            quadVertices.position(3);
            GLES20.glVertexAttribPointer(aTexCoordCopy, 2, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, quadVertices);
            GLES20.glViewport(0, 0, viewWidth, viewHeight);
            AVProcessing.useFBOTexture();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }

        isRenderFinished = true;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // 设置opengl es渲染参数
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);

        // 默认滤镜着色器程序
        changeFilterShader(0);

        // 创建blur/logo/copy着色器程序
        programHandleBlur = AVProcessing.createBlurShaderProgram();
        AVProcessing.useShaderProgram(programHandleBlur);

        programHandleLogo = AVProcessing.createLogoShaderProgram();
        AVProcessing.useShaderProgram(programHandleLogo);

        programHandleCopy = AVProcessing.createCopyShaderProgram();
        aPositionCopy = AVProcessing.getShaderHandle("aPosition", programHandleCopy);
        aTexCoordCopy = AVProcessing.getShaderHandle("aTexCoord", programHandleCopy);
        uSTextureCopy = AVProcessing.getShaderHandle("uSTexture", programHandleCopy);

        AVProcessing.useShaderProgram(programHandleCopy);
        GLES20.glUniform1i(uSTextureCopy, 0);
        GLES20.glEnableVertexAttribArray(aPositionCopy);
        GLES20.glEnableVertexAttribArray(aTexCoordCopy);

        // 初始化fbo
        AVProcessing.initFBO(frameWidth, frameHeight);

        // 创建yuv纹理及mask/logo纹理
        createTexture(frameWidth, frameHeight, GLES20.GL_LUMINANCE, textureYId);
        createTexture(halfFrameWidth, halfFrameHeight, GLES20.GL_LUMINANCE, textureUId);
        createTexture(halfFrameWidth, halfFrameHeight, GLES20.GL_LUMINANCE, textureVId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureMId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureLId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureTId);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        viewWidth = width;
        viewHeight = height;
        GLES20.glViewport(0, 0, viewWidth, viewHeight);
    }

    // 创建纹理
    private void createTexture(int width, int height, int format, int[] textureId) {
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, format, GLES20.GL_UNSIGNED_BYTE, null);
    }

    // 重新创建纹理
    public void reCreateTexture(int width, int height) {
        frameWidth = width;
        frameHeight = height;
        halfFrameWidth = frameWidth >> 1;
        halfFrameHeight = frameHeight >> 1;

        AVProcessing.deleteFBO();
        GLES20.glDeleteTextures(1, textureYId, 0);
        GLES20.glDeleteTextures(1, textureUId, 0);
        GLES20.glDeleteTextures(1, textureVId, 0);
        GLES20.glDeleteTextures(1, textureMId, 0);
        GLES20.glDeleteTextures(1, textureLId, 0);
        GLES20.glDeleteTextures(1, textureTId, 0);

        AVProcessing.initFBO(frameWidth, frameHeight);
        createTexture(frameWidth, frameHeight, GLES20.GL_LUMINANCE, textureYId);
        createTexture(frameWidth >> 1, frameHeight >> 1, GLES20.GL_LUMINANCE, textureUId);
        createTexture(frameWidth >> 1, frameHeight >> 1, GLES20.GL_LUMINANCE, textureVId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureMId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureLId);
        createTexture(frameWidth, frameHeight, GLES20.GL_RGBA, textureTId);
    }

    // 拷贝帧缓存纹理数据
    public void copyFrameBufferTextureData(ByteBuffer rgbBuf, ByteBuffer ydata, ByteBuffer udata, ByteBuffer vdata, ByteBuffer mdata,
                                           ByteBuffer ldata, ByteBuffer tdata, int frameIndex, int frameThreshold) {
        isRenderFinished = false;
        this.rgbBuf = rgbBuf;
        updateTextureData(ydata, udata, vdata, mdata, ldata, tdata, frameIndex, frameThreshold);
    }

    // 更新纹理数据
    public void updateTextureData(ByteBuffer ydata, ByteBuffer udata, ByteBuffer vdata, ByteBuffer mdata, ByteBuffer ldata, ByteBuffer
            tdata, int frameIndex, int frameThreshold) {
        yPixels = ydata;
        uPixels = udata;
        vPixels = vdata;
        mPixels = mdata;
        lPixels = ldata;
        tPixels = tdata;

        this.frameIndex = frameIndex;
        this.frameThreshold = frameThreshold;

        requestRender();
    }

    public void updateTextureData(IntBuffer rgbData, ByteBuffer mdata, ByteBuffer ldata, ByteBuffer
            tdata, int frameIndex, int frameThreshold) {
        rgbPixels = rgbData;
        mPixels = mdata;
        lPixels = ldata;
        tPixels = tdata;

        this.frameIndex = frameIndex;
        this.frameThreshold = frameThreshold;

        requestRender();
    }

    public void setCopyFrameBuffer(boolean isCopyFrameBuffer) {
        this.isCopyFrameBuffer = isCopyFrameBuffer;
    }

    public boolean isRenderFinished() {
        return isRenderFinished;
    }

    public void setRenderFinished(boolean renderFinished) {
        isRenderFinished = renderFinished;
    }

    // 释放资源
    public void releaseShaderProgram() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                AVProcessing.deleteShaderProgram(programHandleBlur);
                AVProcessing.deleteShaderProgram(programHandleLogo);
                AVProcessing.deleteShaderProgram(programHandleCopy);
                AVProcessing.deleteShaderProgram(programHandleMain);
                AVProcessing.deleteFBO();

                GLES20.glDeleteTextures(1, textureYId, 0);
                GLES20.glDeleteTextures(1, textureUId, 0);
                GLES20.glDeleteTextures(1, textureVId, 0);
                GLES20.glDeleteTextures(1, textureMId, 0);
                GLES20.glDeleteTextures(1, textureLId, 0);
                GLES20.glDeleteTextures(1, textureTId, 0);
            }
        });
    }

    // 切换滤镜着色器程序
    public void changeFilterShader(int filterId) {
        // 释放原着色器程序
        AVProcessing.deleteShaderProgram(programHandleMain);

        // 创建着色器程序
        programHandleMain = AVProcessing.createMainShaderProgram(filterId);

        // 获取shader属性变量handle
        aPositionMain = AVProcessing.getShaderHandle("aPosition", programHandleMain);
        aTexCoordMain = AVProcessing.getShaderHandle("aTexCoord", programHandleMain);
        uTransMatMain = AVProcessing.getShaderHandle("uTransMat", programHandleMain);
        uYTextureMain = AVProcessing.getShaderHandle("inputImageTexture", programHandleMain);

//        uYTextureMain = AVProcessing.getShaderHandle("uYTexture", programHandleMain);
//        uUTextureMain = AVProcessing.getShaderHandle("uUTexture", programHandleMain);
//        uVTextureMain = AVProcessing.getShaderHandle("uVTexture", programHandleMain);
//        uMTextureMain = AVProcessing.getShaderHandle("uMTexture", programHandleMain);

        // 使用着色器程序
        AVProcessing.useShaderProgram(programHandleMain);
        GLES20.glUniform1i(uYTextureMain, 1);
        GLES20.glUniform1i(uUTextureMain, 2);
        GLES20.glUniform1i(uVTextureMain, 3);
        GLES20.glUniform1i(uMTextureMain, 4);
        GLES20.glEnableVertexAttribArray(aPositionMain);
        GLES20.glEnableVertexAttribArray(aTexCoordMain);
    }

    // 切换滤镜
    public void changeFilter(int filterIndex) {
        curFilterIndex = filterIndex;
        requestRender();
    }

    // 更新文字编辑区域
    public void updateTextEditRect(Rect rect) {
        isTextEditClicked = (rect != null);
        if (isTextEditClicked) {
            textEditRect.left = (int) (viewWidth * ((float) rect.left / frameWidth));
            textEditRect.right = (int) (viewWidth * ((float) rect.right / frameWidth));
            textEditRect.top = (int) (viewHeight * ((float) rect.top / frameHeight));
            textEditRect.bottom = (int) (viewHeight * ((float) rect.bottom / frameHeight));
        }
    }

    // 触屏事件
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 文字编辑区域被点击
//                if (isTextEditClicked && textEditRect.contains((int) e.getX(), (int) e.getY())) {
//                    videoEditActivity.onThemeTextClicked();
//                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(e);
    }
}
