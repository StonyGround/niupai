LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := avcodec
LOCAL_SRC_FILES := prebuilt/libavcodec-56.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avfilter
LOCAL_SRC_FILES := prebuilt/libavfilter-5.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avformat
LOCAL_SRC_FILES := prebuilt/libavformat-56.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avutil
LOCAL_SRC_FILES := prebuilt/libavutil-54.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swresample
LOCAL_SRC_FILES := prebuilt/libswresample-1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swscale
LOCAL_SRC_FILES := prebuilt/libswscale-3.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := postproc
LOCAL_SRC_FILES := prebuilt/libpostproc-53.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := AVProcessing
LOCAL_SRC_FILES := EncodeAudio.c       \
                   EncodeVideo.c       \
                   MuxAVFile.c         \
                   MixAudioWithSong.c  \
                   MixAudioWithSong2.c \
                   YUVConvert.c        \
                   ShaderManager.c     \
                   ExtractAVFile.c     \
                   DecodeVideo2Frame.c \
                   Util.c              \
                   AddWaterMark.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid -lGLESv2
LOCAL_SHARED_LIBRARIES := avcodec avformat avfilter avutil swresample swscale postproc
include $(BUILD_SHARED_LIBRARY)