package com.xiuxiu.util;

public class AVProcessing {
    // yuv数据转rgb
    public native static void yuv2rgb(long handle, int[] rgb, int width, int height);

    // yuv数据拷贝
    public native static void copyYUVData(long srcHandle, byte[] dst_y, byte[] dst_u, byte[] dst_v, int length);

    // 普通帧数据拷贝
    public native static void copyFrameData(long srcHandle, byte[] dstData, int length);

    public native static byte[] copyByteArray(int srcHandle, int length);

    // yuv数据裁剪并旋转
    public native static void setRotateClipFunc(int cameraIndex);

    public native static void rotateClipYUV(byte[] src_yuv, byte[] dst_yuv, int width, int height, boolean isClipData);

    // 音频编码及文件合并
    public native static int initAudioEncode(String filePath, int sampleRate, int channels);

    public native static void uninitAudioEncode();

    public native static void encodeAudioFrame(byte[] data, int length);

    public native static void mergeAudioFiles(String audioFileFolder, String outputFilePath);

    // 音视频混合
    public native static int muxAVFile(String audioFilePath, String videoFilePath, String outputFilePath, double maxDuration);

    // 音视频分离
    public native static int extractAVFile(String inputFilePath, String vOutputFilePath, String aOutputFilePath, double maxDuration);

    // 人声与伴奏混音(人声与伴奏采样率相等,均为44100)
    public native static int mixAudioWithSong(String audioFilePath, String songFilePath, String outputFilePath);

    // 人声与伴奏混音(人声采样率为44100,伴奏为任意采样率)
    public native static int mixAudioWithSong2(String audioFilePath, String songFilePath, String outputFilePath, double strength);

    // 计算当前视频帧率
    public native static int getVideoFrameRate(String videoFilePath);

    // 视频编码
    public native static int initVideoEncode(String videoFilePath, int width, int height);

    public native static void uninitVideoEncode();

    public native static int encodeVideoFrames(byte[] data);

    // 视频解码
    public native static int initVideoDecode(String videoFilePath);

    public native static int uninitVideoDecode();

    public native static double decodeVideo2Frame(byte[] dst_y, byte[] dst_u, byte[] dst_v, int length);

    // 保存/删除数据帧
    public native static long saveDataFrame(byte[] data, int length);

    public native static void deleteDataFrame(long handle);

    // 读取帧数据
    public native static void openFrameFile(String frameFilePath);

    public native static void closeFrameFile();

    public native static int readFrameData(byte[] data, int length);

    // shader程序管理
    public native static int createBlurShaderProgram();

    public native static int createLogoShaderProgram();

    public native static int createCopyShaderProgram();

    public native static int createMainShaderProgram(int shaderId);

    public native static void useShaderProgram(int programHandle);

    public native static int getShaderHandle(String name, int programHandle);

    public native static void deleteShaderProgram(int programHandle);

    // fbo管理
    public native static void initFBO(int width, int height);

    public native static void bindFBO();

    public native static void useFBOTexture();

    public native static void deleteFBO();

    // 删除指定文件
    public native static void deleteFile(String filePath);

    // 删除指定文件夹内所有文件
    public native static void deleteFiles(String fileFolder);

    // 拷贝指定目录下文件至另一目录
    public native static int copyFile(String srcFilePath, String dstFilePath);

    static {
        System.loadLibrary("avutil-54");
        System.loadLibrary("avcodec-56");
        System.loadLibrary("swresample-1");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        System.loadLibrary("avfilter-5");
        System.loadLibrary("postproc-53");
        System.loadLibrary("AVProcessing");
    }
}
