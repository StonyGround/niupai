/**
 * yuv数据转换
 * 主要包括yuv原始数据转rgb及数据旋转/裁剪
 */

#include <dirent.h>
#include "AVProcessing.h"

#define TAG    "YUVConvert.c" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

// yuv数据裁剪并旋转
typedef void (*fpRotateClipYUV)(jbyte *pSrc, jbyte *pDst, jint width, jint height, jboolean isClipData);

// 前置摄像头
void rotateClipYUV_CameraFront(jbyte *pSrc, jbyte *pDst, jint width, jint height, jboolean isClipData) {
    int i = 0, j = 0, k = 0;
    int size = width * height;
    int diff = width - height;
    int uvHeight = height >> 1;
    int dstQtrSize = uvHeight * uvHeight;

    if (isClipData) {
        diff = width - height;
        dstQtrSize = uvHeight * uvHeight;
    } else {
        diff = 0;
        dstQtrSize = size >> 2;
    }

    // 旋转y
    for (i = width - 1; i >= diff; i--) {
        for (j = height - 1; j >= 0; j--) {
            pDst[k] = pSrc[width * j + i];
            k++;
        }
    }

    // 旋转uv
    for (i = width - 1; i >= diff; i -= 2) {
        for (j = uvHeight - 1; j >= 0; j--) {
            pDst[k] = pSrc[size + width * j + i];// cb/u
            pDst[k + dstQtrSize] = pSrc[size + width * j + i + 1];// cr/v
            k++;
        }
    }

    // 针对前置摄像头第一行花屏问题,直接用第二行数据替换第一行
    if (isClipData) {
        size = height * height;
        memcpy(pDst, pDst + height, height);
        memcpy(pDst + size, pDst + size + uvHeight, uvHeight);
        memcpy(pDst + size + dstQtrSize, pDst + size + dstQtrSize + uvHeight, uvHeight);
    } else {
        memcpy(pDst, pDst + height, height);
        memcpy(pDst + size, pDst + size + uvHeight, uvHeight);
        memcpy(pDst + size + dstQtrSize, pDst + size + dstQtrSize + uvHeight, uvHeight);
    }
}

// 后置摄像头
void rotateClipYUV_CameraBack(jbyte *pSrc, jbyte *pDst, jint width, jint height, jboolean isClipData) {
    int i = 0, j = 0, k = 0;
    int size = width * height;
    int uvHeight = height >> 1;
    int dstWidth = width;
    int dstQtrSize = uvHeight * uvHeight;

    if (isClipData) {
        dstWidth = height;
        dstQtrSize = uvHeight * uvHeight;
    } else {
        dstWidth = width;
        dstQtrSize = size >> 2;
    }

    // 旋转y
    for (i = 0; i < dstWidth; i++) {
        for (j = height - 1; j >= 0; j--) {
            pDst[k] = pSrc[width * j + i];
            k++;
        }
    }

    // 旋转uv
    for (i = 0; i < dstWidth; i += 2) {
        for (j = uvHeight - 1; j >= 0; j--) {
            pDst[k] = pSrc[size + width * j + i + 1];// cb/u
            pDst[k + dstQtrSize] = pSrc[size + width * j + i];// cr/v
            k++;
        }
    }
}

// 初始化函数指针
fpRotateClipYUV rotateClipYUV = rotateClipYUV_CameraBack;

JNIEXPORT void JNICALL
Java_com_xiuxiu_util_AVProcessing_rotateClipYUV(JNIEnv *env, jclass obj, jbyteArray src_yuv, jbyteArray dst_yuv, jint width, jint height,
                                                jboolean isClipData) {
    jbyte *pSrc = (*env)->GetByteArrayElements(env, src_yuv, 0);
    jbyte *pDst = (*env)->GetByteArrayElements(env, dst_yuv, 0);

    (*rotateClipYUV)(pSrc, pDst, width, height, isClipData);

    (*env)->ReleaseByteArrayElements(env, src_yuv, pSrc, 0);
    (*env)->ReleaseByteArrayElements(env, dst_yuv, pDst, 0);
}

// 选择函数
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_setRotateClipFunc(JNIEnv *env, jclass obj, jint cameraIndex) {
    rotateClipYUV = (cameraIndex == 0) ? rotateClipYUV_CameraBack : rotateClipYUV_CameraFront;
}

// yuv数据转rgb
JNIEXPORT void JNICALL
Java_com_xiuxiu_util_AVProcessing_yuv2rgb(JNIEnv *env, jclass obj, jlong handle, jintArray rgb, jint width, jint height) {
    jbyte *pYUV = (jbyte *) handle;
    jint *pRGB = (*env)->GetIntArrayElements(env, rgb, 0);

    // 转码
    int i = 0, j = 0, yp = 0;
    const int hfWidth = width >> 1;
    const int size = width * height;
    const int qtrSize = size >> 2;
    for (i = 0, yp = 0; i < height; i++) {
        int uvp = size + (i >> 1) * hfWidth, u = 0, v = 0;
        for (j = 0; j < width; j++, yp++) {
            int y = (0xff & pYUV[yp]) - 16;
            if ((j & 1) == 0) {
                u = (0xff & pYUV[uvp + (j >> 1)]) - 128;
                v = (0xff & pYUV[uvp + qtrSize + (j >> 1)]) - 128;
            }

            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            pRGB[i * width + j] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        }// for
    }// for
    (*env)->ReleaseIntArrayElements(env, rgb, pRGB, 0);
}

// yuv数据拷贝
JNIEXPORT void JNICALL
Java_com_xiuxiu_util_AVProcessing_copyYUVData(JNIEnv *env, jclass obj, jlong handle, jbyteArray dst_y, jbyteArray dst_u, jbyteArray dst_v,
                                              jint length) {
    jbyte *pSrcYUV = (jbyte *) handle;
    jbyte *pDstY = (*env)->GetByteArrayElements(env, dst_y, 0);
    jbyte *pDstU = (*env)->GetByteArrayElements(env, dst_u, 0);
    jbyte *pDstV = (*env)->GetByteArrayElements(env, dst_v, 0);

    int qtrLen = length >> 2;
    memcpy(pDstY, pSrcYUV, length);
    memcpy(pDstU, pSrcYUV + length, qtrLen);
    memcpy(pDstV, pSrcYUV + length + qtrLen, qtrLen);

    (*env)->ReleaseByteArrayElements(env, dst_y, pDstY, 0);
    (*env)->ReleaseByteArrayElements(env, dst_u, pDstU, 0);
    (*env)->ReleaseByteArrayElements(env, dst_v, pDstV, 0);
}
