//
// Created by hzdykj on 2017/6/26.
//

//ffmpeg -i video.mp4 -i watermark.png -filter_complex "[0:v][1:v] overlay=25:25:enable='between(t,0,20)'" -pix_fmt yuv420p -c:a copy output.mp4

/**
 * 功能函数集合
 * 主要包括保存帧数据/删除帧数据/删除指定文件/删除指定文件夹内所有文件
 */

#include <dirent.h>
#include <libavfilter/buffersink.h>
#include <libavfilter/buffersrc.h>
#include "AVProcessing.h"
#include "include/libavfilter/avfilter.h"

#define TAG    "AddWaterMark.c" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型


//Enable SDL?
#define ENABLE_SDL 0
//Output YUV data?
#define ENABLE_YUVFILE 1

const char *filter_descr = "movie=/storage/emulated/0/watermark.png[wm];[in][wm]overlay=5:5[out]";

static AVFormatContext *pFormatCtx;
static AVCodecContext *pCodecCtx;
AVFilterContext *buffersink_ctx;
AVFilterContext *buffersrc_ctx;
AVFilterGraph *filter_graph;
static int video_stream_index = -1;

static int open_input_file(const char *filename) {
    int ret;
    AVCodec *dec;

    if ((ret = avformat_open_input(&pFormatCtx, filename, NULL, NULL)) < 0) {
        LOGD("Cannot open input file\n");
        return ret;
    }

    if ((ret = avformat_find_stream_info(pFormatCtx, NULL)) < 0) {
        LOGD("Cannot find stream information\n");
        return ret;
    }

    /* select the video stream */
    ret = av_find_best_stream(pFormatCtx, AVMEDIA_TYPE_VIDEO, -1, -1, &dec, 0);
    if (ret < 0) {
        LOGD("Cannot find a video stream in the input file\n");
        return ret;
    }
    video_stream_index = ret;
    pCodecCtx = pFormatCtx->streams[video_stream_index]->codec;

    /* init the video decoder */
    if ((ret = avcodec_open2(pCodecCtx, dec, NULL)) < 0) {
        LOGD("Cannot open video decoder\n");
        return ret;
    }

    return 0;
}

static int init_filters(const char *filters_descr) {
    char args[512];
    int ret;
    AVFilter *buffersrc = avfilter_get_by_name("buffer");
    AVFilter *buffersink = avfilter_get_by_name("ffbuffersink");
    AVFilterInOut *outputs = avfilter_inout_alloc();
    AVFilterInOut *inputs = avfilter_inout_alloc();
    enum AVPixelFormat pix_fmts[] = {AV_PIX_FMT_YUV420P, AV_PIX_FMT_NONE};
    AVBufferSinkParams *buffersink_params;

    filter_graph = avfilter_graph_alloc();

    /* buffer video source: the decoded frames from the decoder will be inserted here. */
    snprintf(args, sizeof(args),
             "video_size=%dx%d:pix_fmt=%d:time_base=%d/%d:pixel_aspect=%d/%d",
             pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
             pCodecCtx->time_base.num, pCodecCtx->time_base.den,
             pCodecCtx->sample_aspect_ratio.num, pCodecCtx->sample_aspect_ratio.den);

    ret = avfilter_graph_create_filter(&buffersrc_ctx, buffersrc, "in",
                                       args, NULL, filter_graph);
    if (ret < 0) {
        LOGD("Cannot create buffer source\n");
        return ret;
    }

    /* buffer video sink: to terminate the filter chain. */
    buffersink_params = av_buffersink_params_alloc();
    buffersink_params->pixel_fmts = pix_fmts;
    ret = avfilter_graph_create_filter(&buffersink_ctx, buffersink, "out",
                                       NULL, buffersink_params, filter_graph);
    av_free(buffersink_params);
    if (ret < 0) {
        LOGD("Cannot create buffer sink\n");
        return ret;
    }

    /* Endpoints for the filter graph. */
    outputs->name = av_strdup("in");
    outputs->filter_ctx = buffersrc_ctx;
    outputs->pad_idx = 0;
    outputs->next = NULL;

    inputs->name = av_strdup("out");
    inputs->filter_ctx = buffersink_ctx;
    inputs->pad_idx = 0;
    inputs->next = NULL;

    if ((ret = avfilter_graph_parse_ptr(filter_graph, filters_descr,
                                        &inputs, &outputs, NULL)) < 0)
        return ret;

    if ((ret = avfilter_graph_config(filter_graph, NULL)) < 0)
        return ret;
    return 0;
}

JNIEXPORT void JNICALL
Java_com_xiuxiu_util_AVProcessing_addWaterMark(JNIEnv *env, jclass type,
                                               jstring inputVideoFilePath_,
                                               jstring outputVideoFilePath_,
                                               jstring watermarkImageFilePath_) {
    const char *inputVideoFilePath = (*env)->GetStringUTFChars(env, inputVideoFilePath_, 0);
    const char *outputVideoFilePath = (*env)->GetStringUTFChars(env, outputVideoFilePath_, 0);
    const char *watermarkImageFilePath = (*env)->GetStringUTFChars(env, watermarkImageFilePath_, 0);

    LOGD("%s, %s, %s",inputVideoFilePath, outputVideoFilePath, watermarkImageFilePath);

    // TODO
    int ret;
    AVPacket packet;
    AVFrame *pFrame;
    AVFrame *pFrame_out;

    int got_frame;

    av_register_all();
    avfilter_register_all();

    if ((ret = open_input_file(inputVideoFilePath)) < 0)
        goto end;
    if ((ret = init_filters(filter_descr)) < 0)
        goto end;

#if ENABLE_YUVFILE
    FILE *fp_yuv = fopen(outputVideoFilePath, "wb+");
#endif


    pFrame = av_frame_alloc();
    pFrame_out = av_frame_alloc();

    /* read all packets */
    while (1) {

        ret = av_read_frame(pFormatCtx, &packet);
        if (ret < 0)
            break;

        if (packet.stream_index == video_stream_index) {
            got_frame = 0;
            ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_frame, &packet);
            if (ret < 0) {
                LOGD("Error decoding video\n");
                break;
            }

            if (got_frame) {
                pFrame->pts = av_frame_get_best_effort_timestamp(pFrame);

                /* push the decoded frame into the filtergraph */
                if (av_buffersrc_add_frame(buffersrc_ctx, pFrame) < 0) {
                    LOGD("Error while feeding the filtergraph\n");
                    break;
                }

                /* pull filtered pictures from the filtergraph */
                while (1) {

                    ret = av_buffersink_get_frame(buffersink_ctx, pFrame_out);
                    if (ret < 0)
                        break;

                    LOGD("Process 1 frame!\n");

                    if (pFrame_out->format == AV_PIX_FMT_YUV420P) {
#if ENABLE_YUVFILE
                        //Y, U, V
                        for (int i = 0; i < pFrame_out->height; i++) {
                            fwrite(pFrame_out->data[0] + pFrame_out->linesize[0] * i, 1,
                                   pFrame_out->width, fp_yuv);
                        }
                        for (int i = 0; i < pFrame_out->height / 2; i++) {
                            fwrite(pFrame_out->data[1] + pFrame_out->linesize[1] * i, 1,
                                   pFrame_out->width / 2, fp_yuv);
                        }
                        for (int i = 0; i < pFrame_out->height / 2; i++) {
                            fwrite(pFrame_out->data[2] + pFrame_out->linesize[2] * i, 1,
                                   pFrame_out->width / 2, fp_yuv);
                        }
#endif

                    }
                    av_frame_unref(pFrame_out);
                }
            }
            av_frame_unref(pFrame);
        }
        av_free_packet(&packet);
    }
#if ENABLE_YUVFILE
    fclose(fp_yuv);
#endif

    end:
    avfilter_graph_free(&filter_graph);
    if (pCodecCtx)
        avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);


    if (ret < 0 && ret != AVERROR_EOF) {
        char buf[1024];
        av_strerror(ret, buf, sizeof(buf));
        LOGD("Error occurred: %s\n", buf);
    }


    (*env)->ReleaseStringUTFChars(env, inputVideoFilePath_, inputVideoFilePath);
    (*env)->ReleaseStringUTFChars(env, outputVideoFilePath_, outputVideoFilePath);
    (*env)->ReleaseStringUTFChars(env, watermarkImageFilePath_, watermarkImageFilePath);
}