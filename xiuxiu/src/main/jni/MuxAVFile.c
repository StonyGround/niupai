/**
 * 音视频混合
 * 将音频及视频两路媒体流混合到mp4文件
 */

#include "AVProcessing.h"

#define TAG    "MuxAVFile.c" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型

// 添加流
static AVStream *add_av_stream(AVFormatContext *fmt_ctx, AVStream *input_stream) {
    AVStream *output_stream = NULL;

    output_stream = avformat_new_stream(fmt_ctx, input_stream->codec->codec);
    if (output_stream == NULL) {
        return NULL;
    }

    int ret = avcodec_copy_context(output_stream->codec, input_stream->codec);
    if (ret < 0) {
        return NULL;
    }

    output_stream->codec->codec_tag = 0;
    if (fmt_ctx->oformat->flags & AVFMT_GLOBALHEADER) {
        output_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
    }

    return output_stream;
}

// 打开流
static AVStream *open_av_stream(AVFormatContext *fmt_ctx, enum AVMediaType type) {
    if (avformat_find_stream_info(fmt_ctx, NULL) < 0) {
        return NULL;
    }

    int stream_index = av_find_best_stream(fmt_ctx, type, -1, -1, NULL, 0);
    if (stream_index < 0) {
        return NULL;
    } else {
        return fmt_ctx->streams[stream_index];
    }
}

// 音视频混合
JNIEXPORT int JNICALL
Java_com_xiuxiu_util_AVProcessing_muxAVFile(JNIEnv *env, jclass obj, jstring audioFilePath, jstring videoFilePath, jstring outputFilePath,
                                            jdouble maxDuration) {
    const char *audio_filename = (*env)->GetStringUTFChars(env, audioFilePath, 0);
    const char *video_filename = (*env)->GetStringUTFChars(env, videoFilePath, 0);
    const char *output_filename = (*env)->GetStringUTFChars(env, outputFilePath, 0);

    int ret = RESULT_ERROR;
    int audio_ret = 0, video_ret = 0;
    double audio_time = 0.0, video_time = 0.0;

    AVFormatContext *audio_fmt_ctx = NULL;
    AVFormatContext *video_fmt_ctx = NULL;
    AVFormatContext *output_fmt_ctx = NULL;
    AVOutputFormat *output_fmt = NULL;
    AVStream *input_audio_stream = NULL, *input_video_stream = NULL;
    AVStream *output_audio_stream = NULL, *output_video_stream = NULL;
    AVPacket audio_pkt, video_pkt;

    // register all formats and codecs
    av_register_all();
    // 打开音/视频文件
    if (avformat_open_input(&audio_fmt_ctx, audio_filename, NULL, NULL) < 0) {
        ret = RESULT_ERROR;
        goto end;
    }
    if (avformat_open_input(&video_fmt_ctx, video_filename, NULL, NULL) < 0) {
        ret = RESULT_ERROR;
        goto end;
    }
    // 打开音/视频流
    input_audio_stream = open_av_stream(audio_fmt_ctx, AVMEDIA_TYPE_AUDIO);
    input_video_stream = open_av_stream(video_fmt_ctx, AVMEDIA_TYPE_VIDEO);
    if ((input_audio_stream == NULL) || (input_video_stream == NULL)) {
        ret = RESULT_ERROR;
        goto end;
    }

    // 新建输出文件
    avformat_alloc_output_context2(&output_fmt_ctx, NULL, NULL, output_filename);
    if (output_fmt_ctx == NULL) {
        ret = RESULT_ERROR;
        goto end;
    }
    output_fmt = output_fmt_ctx->oformat;

    // 添加音/视频流
    output_video_stream = add_av_stream(output_fmt_ctx, input_video_stream);
    output_audio_stream = add_av_stream(output_fmt_ctx, input_audio_stream);
    if (output_audio_stream == NULL || output_video_stream == NULL) {
        ret = RESULT_ERROR;
        goto end;
    }

    // 打开输出文件
    if (!(output_fmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&output_fmt_ctx->pb, output_filename, AVIO_FLAG_WRITE);
        if (ret < 0) {
            ret = RESULT_ERROR;
            goto end;
        }
    }

    // Write the stream header
    ret = avformat_write_header(output_fmt_ctx, NULL);
    if (ret < 0) {
        ret = RESULT_ERROR;
        goto end;
    }

    av_init_packet(&audio_pkt);
    audio_pkt.data = NULL;
    audio_pkt.size = 0;
    av_init_packet(&video_pkt);
    video_pkt.data = NULL;
    video_pkt.size = 0;

    // 音/视频交叉输出
    while (1) {
        // Compute current audio and video time.
        audio_time = output_audio_stream->pts.val * av_q2d(output_audio_stream->time_base);
        video_time = output_video_stream->pts.val * av_q2d(output_video_stream->time_base);
        if (audio_time >= maxDuration || video_time >= maxDuration) {
            break;
        }

        // Write interleaved audio and video frames
        if (audio_time <= video_time) {
            // Read audio frames from the file
            audio_ret = av_read_frame(audio_fmt_ctx, &audio_pkt);
            if (audio_ret < 0) {
                av_free_packet(&audio_pkt);
                break;
            } else {
                audio_pkt.pts = av_rescale_q_rnd(audio_pkt.pts, input_audio_stream->time_base, output_audio_stream->time_base,
                                                 AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
                audio_pkt.dts = av_rescale_q_rnd(audio_pkt.dts, input_audio_stream->time_base, output_audio_stream->time_base,
                                                 AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
                audio_pkt.duration = av_rescale_q(audio_pkt.duration, input_audio_stream->time_base, output_audio_stream->time_base);
                audio_pkt.stream_index = output_audio_stream->index;
                av_interleaved_write_frame(output_fmt_ctx, &audio_pkt);
                av_free_packet(&audio_pkt);
            }
        } else if (video_time <= audio_time) {
            // Read video frames from the file
            video_ret = av_read_frame(video_fmt_ctx, &video_pkt);
            if (video_ret < 0) {
                av_free_packet(&video_pkt);
                break;
            } else {
                video_pkt.pts = av_rescale_q_rnd(video_pkt.pts, input_video_stream->time_base, output_video_stream->time_base,
                                                 AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
                video_pkt.dts = av_rescale_q_rnd(video_pkt.dts, input_video_stream->time_base, output_video_stream->time_base,
                                                 AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
                video_pkt.duration = av_rescale_q(video_pkt.duration, input_video_stream->time_base, output_video_stream->time_base);
                video_pkt.stream_index = output_video_stream->index;
                av_interleaved_write_frame(output_fmt_ctx, &video_pkt);
                av_free_packet(&video_pkt);
            }
        }
    }// while

    // Write the trailer
    av_write_trailer(output_fmt_ctx);
    ret = RESULT_OK;

    end:
    if ((output_fmt != NULL) && !(output_fmt->flags & AVFMT_NOFILE)) {
        avio_close(output_fmt_ctx->pb);
    }

    if (output_audio_stream != NULL) {
        avcodec_close(output_audio_stream->codec);
    }

    if (output_video_stream != NULL) {
        avcodec_close(output_video_stream->codec);
    }

    avformat_close_input(&video_fmt_ctx);
    avformat_close_input(&audio_fmt_ctx);
    avformat_free_context(output_fmt_ctx);

    (*env)->ReleaseStringUTFChars(env, audioFilePath, audio_filename);
    (*env)->ReleaseStringUTFChars(env, videoFilePath, video_filename);
    (*env)->ReleaseStringUTFChars(env, outputFilePath, output_filename);

    return ret;
}
