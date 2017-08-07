/**
 * 视频编码
 * 将摄像头采集的数据进行h264编码并存储为mp4文件
 */

#include "AVProcessing.h"

static AVFrame *frame = NULL;
static AVCodec *codec = NULL;
static AVCodecContext *codec_ctx = NULL;
static AVFormatContext *fmt_ctx = NULL;
static AVOutputFormat  *fmt = NULL;
static AVPacket pkt;
static AVStream *video_stream = NULL;
static struct SwsContext *rgb2yuv_sws_ctx = NULL;
static uint8_t *rgb_data[4] = { NULL };
static int rgb_linesize[4] = { 0 };
static int ret = 0, frameIndex = 0, got_packet = 0;
static const char *output_filename = NULL;
void video_encode_release();// 释放资源

// 初始化>>成功:返回该文件包含的帧数,失败:返回错误值
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_initVideoEncode(JNIEnv *env, jclass obj, jstring videoFilePath, jint width, jint height)
{
	// 编码文件名
	output_filename = (*env)->GetStringUTFChars(env, videoFilePath, 0);

	// register all formats and codecs
	av_register_all();

	// 新建输出文件
	avformat_alloc_output_context2(&fmt_ctx, NULL, NULL, output_filename);
	if (fmt_ctx == NULL)
	{
		video_encode_release();
		return RESULT_ERROR;
	}
	fmt = fmt_ctx->oformat;

	// 初始化编解码器并添加流
	codec = avcodec_find_encoder(AV_CODEC_ID_H264);
	if (codec == NULL)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

	video_stream = avformat_new_stream(fmt_ctx, codec);
	if (video_stream == NULL)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

	video_stream->id = fmt_ctx->nb_streams - 1;
	codec_ctx = video_stream->codec;
	codec_ctx->codec_id = AV_CODEC_ID_H264;
	codec_ctx->bit_rate = 400000 * 2;
	codec_ctx->width  = width;
	codec_ctx->height = height;
	codec_ctx->time_base.num = 1;
	codec_ctx->time_base.den = 15;
	codec_ctx->gop_size = 12;
	codec_ctx->max_b_frames = 0;
	codec_ctx->pix_fmt = AV_PIX_FMT_YUV420P;
	// av_opt_set(c->priv_data, "preset", "slow", 0);
	// av_opt_set(codec_ctx->priv_data, "tune", "zerolatency", 0);

	if (fmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
	{
		codec_ctx->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	// 打开编解码器
	ret = avcodec_open2(codec_ctx, codec, NULL);
	if (ret < 0)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

	// 初始化rgb到yuv转换参数
	rgb2yuv_sws_ctx = sws_getContext(codec_ctx->width, codec_ctx->height, AV_PIX_FMT_RGBA,
									 codec_ctx->width, codec_ctx->height, codec_ctx->pix_fmt,
									 SWS_BILINEAR, NULL, NULL, NULL);
	if (rgb2yuv_sws_ctx == NULL)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

//	ret = av_image_alloc(rgb_data, rgb_linesize, codec_ctx->width, codec_ctx->height, AV_PIX_FMT_ARGB, 1);
//	if (ret < 0)
//	{
//		video_encode_release();
//		return RESULT_ERROR;
//	}

	rgb_linesize[0] = width * 4;

	// 新建数据帧
	frame = av_frame_alloc();
	if (frame == NULL)
	{
		video_encode_release();
		return RESULT_ERROR;
	}
	frame->format = codec_ctx->pix_fmt;
	frame->width  = codec_ctx->width;
	frame->height = codec_ctx->height;
	ret = av_image_alloc(frame->data, frame->linesize, codec_ctx->width, codec_ctx->height, codec_ctx->pix_fmt, 32);
	if (ret < 0)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

	// 打开输出文件
	if (!(fmt->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&fmt_ctx->pb, output_filename, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			video_encode_release();
			return RESULT_ERROR;
		}
	}

	// Write the stream header
	ret = avformat_write_header(fmt_ctx, NULL);
	if (ret < 0)
	{
		video_encode_release();
		return RESULT_ERROR;
	}

	(*env)->ReleaseStringUTFChars(env, videoFilePath, output_filename);
	return RESULT_OK;
}

// 释放
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_uninitVideoEncode(JNIEnv *env, jclass obj)
{
	// get the delayed frames
	while (got_packet)
	{
		ret = avcodec_encode_video2(codec_ctx, &pkt, NULL, &got_packet);
		if (ret < 0)
		{
			break;
		}

		if (got_packet)
		{
			pkt.pts = av_rescale_q_rnd(pkt.pts, codec_ctx->time_base, video_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
			pkt.dts = av_rescale_q_rnd(pkt.dts, codec_ctx->time_base, video_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
			pkt.duration = av_rescale_q(pkt.duration, codec_ctx->time_base, video_stream->time_base);
			pkt.stream_index = video_stream->index;
			av_interleaved_write_frame(fmt_ctx, &pkt);
			av_free_packet(&pkt);
		}
	}

	// Write the trailer
	av_write_trailer(fmt_ctx);
	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "视频写文件尾成功.....");

	// 释放资源
	video_encode_release();
}

// 编码>>成功:返回当前已编码帧序号,失败:返回错误值
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_encodeVideoFrames(JNIEnv *env, jclass obj, jbyteArray data)
{
	// 获取数据
	jbyte* pData = (*env)->GetByteArrayElements(env, data, 0);
	rgb_data[0] = (uint8_t*)pData;

	// 格式转换
	sws_scale(
		rgb2yuv_sws_ctx,
		(const uint8_t *const*)rgb_data, rgb_linesize, 0, frame->height,
		frame->data, frame->linesize);

	// 帧编码
	frame->pts = frameIndex++;
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	ret = avcodec_encode_video2(codec_ctx, &pkt, frame, &got_packet);
	if (ret < 0)
	{
		(*env)->ReleaseByteArrayElements(env, data, pData, 0);
		video_encode_release();
		return RESULT_ERROR;
	}

	// 帧输出
	if (got_packet)
	{
		pkt.pts = av_rescale_q_rnd(pkt.pts, codec_ctx->time_base, video_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
		pkt.dts = av_rescale_q_rnd(pkt.dts, codec_ctx->time_base, video_stream->time_base, AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX);
		pkt.duration = av_rescale_q(pkt.duration, codec_ctx->time_base, video_stream->time_base);
		pkt.stream_index = video_stream->index;
		av_interleaved_write_frame(fmt_ctx, &pkt);
		av_free_packet(&pkt);
	}

	(*env)->ReleaseByteArrayElements(env, data, pData, 0);

	return frameIndex;
}

// 释放资源
void video_encode_release()
{
	// 释放资源
	if ((fmt != NULL) && !(fmt->flags & AVFMT_NOFILE))
	{
		avio_close(fmt_ctx->pb);
	}

	if (video_stream != NULL)
	{
		avcodec_close(video_stream->codec);
	}
	avformat_free_context(fmt_ctx);

	if (frame != NULL)
	{
		av_freep(&frame->data[0]);
		av_frame_free(&frame);
	}

	if (rgb2yuv_sws_ctx != NULL)
	{
		sws_freeContext(rgb2yuv_sws_ctx);
	}

	// 重置参数
	frame = NULL;
	codec = NULL;
	codec_ctx = NULL;
	fmt_ctx = NULL;
	fmt = NULL;
	rgb2yuv_sws_ctx = NULL;
	video_stream = NULL;
	ret = 0;
	frameIndex = 0;
	got_packet = 0;
}
