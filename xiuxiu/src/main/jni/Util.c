/**
 * 功能函数集合
 * 主要包括保存帧数据/删除帧数据/删除指定文件/删除指定文件夹内所有文件
 */

#include <dirent.h>
#include "AVProcessing.h"

#define TAG    "Util.c" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型

static FILE *fp_frame_rb = NULL;

// 获取当前视频帧率
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_getVideoFrameRate(JNIEnv *env, jclass obj, jstring videoFilePath) {
    int frameRate = RESULT_ERROR;
    AVFormatContext *fmt_ctx = NULL;
    const char *video_filename = (*env)->GetStringUTFChars(env, videoFilePath, 0);

    // register all formats and codecs
    av_register_all();

    // 打开视频文件
    if (avformat_open_input(&fmt_ctx, video_filename, NULL, NULL) < 0) {
        goto end;
    }

    if (avformat_find_stream_info(fmt_ctx, NULL) < 0) {
        goto end;
    }

    int stream_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    if (stream_index < 0) {
        goto end;
    }

    AVStream *stream = fmt_ctx->streams[stream_index];
    double num = stream->avg_frame_rate.num;
    double den = stream->avg_frame_rate.den;
    if ((num > 0.00001) && (den > 0.00001)) {
        frameRate = (int) (num / den);
    }

    end:
    avformat_close_input(&fmt_ctx);
    (*env)->ReleaseStringUTFChars(env, videoFilePath, video_filename);

    return frameRate;
}

// 保存帧数据
JNIEXPORT jlong JNICALL Java_com_xiuxiu_util_AVProcessing_saveDataFrame(JNIEnv *env, jclass obj, jbyteArray data, jint length) {
    jbyte *pData = (*env)->GetByteArrayElements(env, data, 0);

    int size = length * sizeof(jbyte);
    jbyte *handle = (jbyte *) malloc(size);
    if (handle != NULL) {
        memcpy(handle, pData, size);
    }

    (*env)->ReleaseByteArrayElements(env, data, pData, 0);
    LOGD("handle: %d  value: %s", handle, (char*)handle);
    return (jlong) handle;
}

// 删除帧数据
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_deleteDataFrame(JNIEnv *env, jclass obj, jlong handle) {
    jlong *pHandle = (jlong *) handle;
    if (pHandle != NULL) {
        free(pHandle);
        pHandle = NULL;
    }
}

// 拷贝帧数据
JNIEXPORT void JNICALL
Java_com_xiuxiu_util_AVProcessing_copyFrameData(JNIEnv *env, jclass obj, jlong handle, jbyteArray dstData, jint length) {
//    LOGD("copyFrameData - handle: %d ", handle);
    jbyte *pSrc = (jbyte *) handle;
    jbyte *pDst = (*env)->GetByteArrayElements(env, dstData, 0);


    memcpy(pDst, pSrc, length);

    (*env)->ReleaseByteArrayElements(env, dstData, pDst, 0);
}

JNIEXPORT jbyteArray JNICALL
Java_com_xiuxiu_util_AVProcessing_copyByteArray(JNIEnv *env, jclass type, jint srcHandle,
                                                 jint length) {
    jbyteArray dstData = (*env)->NewByteArray(env, length);

//    int size = length * sizeof(jbyte);
//    jbyte *handle = (jbyte *) malloc(size);
//    memcpy(handle, srcHandle, length);


//    (*env)->SetByteArrayRegion(env, dstData, 0, length, srcHandle);

    return dstData;
}

// 打开数据帧文件
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_openFrameFile(JNIEnv *env, jclass obj, jstring frameFilePath) {
    const char *frame_filename = (*env)->GetStringUTFChars(env, frameFilePath, 0);
    fp_frame_rb = fopen(frame_filename, "rb");
    (*env)->ReleaseStringUTFChars(env, frameFilePath, frame_filename);
}

// 关闭数据帧文件
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_closeFrameFile(JNIEnv *env, jclass obj) {
    if (fp_frame_rb != NULL) {
        fclose(fp_frame_rb);
        fp_frame_rb = NULL;
    }
}

// 读取帧数据
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_readFrameData(JNIEnv *env, jclass obj, jbyteArray data, jint length) {
    jbyte *pData = (*env)->GetByteArrayElements(env, data, 0);
    int ret = fread(pData, sizeof(jbyte), length, fp_frame_rb);
    (*env)->ReleaseByteArrayElements(env, data, pData, 0);

    return ret;
}

// 拷贝指定目录下文件至另一目录
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_copyFile(JNIEnv *env, jclass obj, jstring srcFilePath, jstring dstFilePath) {
    const char *src_filename = (*env)->GetStringUTFChars(env, srcFilePath, 0);
    const char *dst_filename = (*env)->GetStringUTFChars(env, dstFilePath, 0);

    FILE *fp_in = fopen(src_filename, "rb");
    FILE *fp_out = fopen(dst_filename, "wb");

    (*env)->ReleaseStringUTFChars(env, srcFilePath, src_filename);
    (*env)->ReleaseStringUTFChars(env, dstFilePath, dst_filename);

    if (fp_in != NULL && fp_out != NULL) {
        fseek(fp_in, 0, SEEK_END);
        int len = ftell(fp_in);
        fseek(fp_in, 0, SEEK_SET);

        char *buf = (char *) malloc(len);
        fread(buf, sizeof(char), len, fp_in);
        fwrite(buf, sizeof(char), len, fp_out);

        fclose(fp_in);
        fclose(fp_out);
        free(buf);
        fp_in = NULL;
        fp_out = NULL;
        buf = NULL;

        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}

// 删除指定文件
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_deleteFile(JNIEnv *env, jclass obj, jstring filePath) {
    const char *filename = (*env)->GetStringUTFChars(env, filePath, 0);
    remove(filename);
    (*env)->ReleaseStringUTFChars(env, filePath, filename);
}

// 删除指定文件夹内所有文件
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_deleteFiles(JNIEnv *env, jclass obj, jstring fileFolder) {
    const char *file_folder = (*env)->GetStringUTFChars(env, fileFolder, 0);

    DIR *pDir = NULL;
    struct dirent *dmsg;
    char szFileName[128];
    char szFolderName[128];

    strcpy(szFolderName, file_folder);
    strcat(szFolderName, "/%s");
    if ((pDir = opendir(file_folder)) != NULL) {
        // 遍历目录并删除文件
        while ((dmsg = readdir(pDir)) != NULL) {
            if (strcmp(dmsg->d_name, ".") != 0 && strcmp(dmsg->d_name, "..") != 0) {
                sprintf(szFileName, szFolderName, dmsg->d_name);
                remove(szFileName);
            }
        }
    }

    if (pDir != NULL) {
        closedir(pDir);
    }

    (*env)->ReleaseStringUTFChars(env, fileFolder, file_folder);
}
