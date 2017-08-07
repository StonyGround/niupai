/**
 * 音频编码
 * 将麦克风采集的音频数据进行aac编码并存储为mp4文件
 */

#include "AVProcessing.h"

static AVFrame *frame = NULL;
static AVCodec *codec = NULL;
static AVCodecContext *codec_ctx = NULL;
static AVFormatContext *fmt_ctx = NULL;
static AVOutputFormat  *fmt = NULL;
static AVPacket pkt;
static AVStream *audio_stream = NULL;
static SwrContext *swr_ctx = NULL;
static uint8_t **src_samples_data = NULL, **dst_samples_data = NULL;
static int ret = 0, got_packet = 0;
static int src_nb_samples = 0, samples_count = 0, src_samples_size = 0;
static int dst_nb_samples = 0, max_dst_nb_samples = 0, dst_samples_size = 0;
static const char *output_filename = NULL;
void audio_encode_release();// 释放资源

// 初始化
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_initAudioEncode(JNIEnv *env, jclass obj, jstring filePath, jint sampleRate, jint channels)
{
	// 编码文件名
	output_filename = (*env)->GetStringUTFChars(env, filePath, 0);

	// register all formats and codecs
	av_register_all();

	// 新建输出文件
	avformat_alloc_output_context2(&fmt_ctx, NULL, NULL, output_filename);
	if (fmt_ctx == NULL)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}
	fmt = fmt_ctx->oformat;

	// 初始化编解码器并添加流
	codec = avcodec_find_encoder(AV_CODEC_ID_AAC);
	if (codec == NULL)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	audio_stream = avformat_new_stream(fmt_ctx, codec);
	if (audio_stream == NULL)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	audio_stream->id = fmt_ctx->nb_streams - 1;
	codec_ctx = audio_stream->codec;
	codec_ctx->bit_rate = 64000;
	codec_ctx->sample_rate = 44100;
	codec_ctx->channel_layout = AV_CH_LAYOUT_STEREO;
	codec_ctx->channels = 2;
	codec_ctx->sample_fmt = AV_SAMPLE_FMT_FLTP;
	codec_ctx->strict_std_compliance = FF_COMPLIANCE_EXPERIMENTAL;
// 	codec_ctx->time_base.num = 1;
// 	codec_ctx->time_base.den = sampleRate;
//  codec_ctx->profile = FF_PROFILE_AAC_MAIN;
//  codec_ctx->codec_type = AVMEDIA_TYPE_AUDIO;

	if (fmt->flags & AVFMT_GLOBALHEADER)
	{
		codec_ctx->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	ret = avcodec_open2(codec_ctx, codec, NULL);
	if (ret < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	src_nb_samples = codec_ctx->frame_size;
	ret = av_samples_alloc_array_and_samples(&src_samples_data, NULL, channels, src_nb_samples, AV_SAMPLE_FMT_S16, 0);
	if (ret < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}
	src_samples_size = av_samples_get_buffer_size(NULL, channels, src_nb_samples, AV_SAMPLE_FMT_S16, 0);

	max_dst_nb_samples = src_nb_samples;
	ret = av_samples_alloc_array_and_samples(&dst_samples_data, NULL, codec_ctx->channels, max_dst_nb_samples, codec_ctx->sample_fmt, 0);
	if (ret < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	dst_samples_size = av_samples_get_buffer_size(NULL, codec_ctx->channels, codec_ctx->frame_size, codec_ctx->sample_fmt, 0);
	if (dst_samples_size < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	// src_samples_size: 2048, src_nb_samples: 1024, dst_samples_size: 8192
	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "src_samples_size: %d, src_nb_samples: %d, dst_samples_size: %d", src_samples_size, src_nb_samples, dst_samples_size);

	// 初始化S16到FLTP转换参数
	swr_ctx = swr_alloc();
	if (swr_ctx == NULL)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	// set options
	av_opt_set_int       (swr_ctx, "in_channel_count",  channels,               0);
	av_opt_set_int       (swr_ctx, "in_sample_rate",    sampleRate,             0);
	av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt",     AV_SAMPLE_FMT_S16,      0);
	av_opt_set_int       (swr_ctx, "out_channel_count", codec_ctx->channels,    0);
	av_opt_set_int       (swr_ctx, "out_sample_rate",   codec_ctx->sample_rate, 0);
	av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt",    codec_ctx->sample_fmt,  0);

	// initialize the resampling context
	if (swr_init(swr_ctx) < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	// 新建数据帧
	frame = av_frame_alloc();
	if (frame == NULL)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	// 打开输出文件
	if (!(fmt->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&fmt_ctx->pb, output_filename, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			audio_encode_release();
			return RESULT_ERROR;
		}
	}

	// Write the stream header
	ret = avformat_write_header(fmt_ctx, NULL);
	if (ret < 0)
	{
		audio_encode_release();
		return RESULT_ERROR;
	}

	(*env)->ReleaseStringUTFChars(env, filePath, output_filename);
	return RESULT_OK;
}

// 释放
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_uninitAudioEncode(JNIEnv *env, jclass obj)
{
	// get the delayed frames
	while (got_packet)
	{
		ret = avcodec_encode_audio2(codec_ctx, &pkt, NULL, &got_packet);
		if (ret < 0)
		{
			break;
		}

		if (got_packet)
		{
			pkt.pts = av_rescale_q_rnd(pkt.pts, codec_ctx->time_base, audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.dts = av_rescale_q_rnd(pkt.dts, codec_ctx->time_base, audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.duration = av_rescale_q(pkt.duration, codec_ctx->time_base, audio_stream->time_base);
			pkt.stream_index = audio_stream->index;
			av_interleaved_write_frame(fmt_ctx, &pkt);
			av_free_packet(&pkt);
		}
	}

	// Write the trailer
	av_write_trailer(fmt_ctx);
	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "音频写文件尾成功.....");

	// 释放资源
	audio_encode_release();
}

// 编码
JNIEXPORT void JNICALL Java_com_xiuxiu_util_AVProcessing_encodeAudioFrame(JNIEnv *env, jclass obj, jbyteArray data, jint length)
{
	// 获取数据>>由于数据长度为4096,aac帧大小为1024,单帧占用空间为2048,因此采集一次数据需编码两次
	jbyte* pData = (*env)->GetByteArrayElements(env, data, 0);
	int k = 0;
	int count = length / 2048;
	for (k = 0; k < count; k++)
	{
		memcpy(src_samples_data[0], pData + k*src_samples_size, src_samples_size);

		// 格式转换
		if (swr_ctx != NULL)
		{
			dst_nb_samples = av_rescale_rnd(swr_get_delay(swr_ctx, codec_ctx->sample_rate) + src_nb_samples, codec_ctx->sample_rate, codec_ctx->sample_rate, AV_ROUND_UP);
			if (dst_nb_samples > max_dst_nb_samples)
			{
				av_free(dst_samples_data[0]);
				ret = av_samples_alloc(dst_samples_data, NULL, codec_ctx->channels, dst_nb_samples, codec_ctx->sample_fmt, 0);
				if (ret < 0)
				{
					audio_encode_release();
					return;
				}
				max_dst_nb_samples = dst_nb_samples;
				dst_samples_size = av_samples_get_buffer_size(NULL, codec_ctx->channels, dst_nb_samples, codec_ctx->sample_fmt, 0);
			}

			ret = swr_convert(swr_ctx, dst_samples_data, dst_nb_samples, (const uint8_t **)src_samples_data, src_nb_samples);
			if (ret < 0)
			{
				audio_encode_release();
				return;
			}
		}

		// 填充数据帧
		frame->nb_samples = dst_nb_samples;
		frame->format = codec_ctx->sample_fmt;
		frame->channel_layout = codec_ctx->channel_layout;
		frame->pts = av_rescale_q(samples_count, (AVRational){1, codec_ctx->sample_rate}, codec_ctx->time_base);
		ret = avcodec_fill_audio_frame(frame, codec_ctx->channels, codec_ctx->sample_fmt, dst_samples_data[0], dst_samples_size, 0);
		if (ret < 0)
		{
			audio_encode_release();
			return;
		}
		samples_count += dst_nb_samples;

		av_init_packet(&pkt);
		pkt.data = NULL;
		pkt.size = 0;

		// encode the samples
		ret = avcodec_encode_audio2(codec_ctx, &pkt, frame, &got_packet);
		if (ret < 0)
		{
			audio_encode_release();
			return;
		}

		if (got_packet)
		{
			pkt.pts = av_rescale_q_rnd(pkt.pts, codec_ctx->time_base, audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.dts = av_rescale_q_rnd(pkt.dts, codec_ctx->time_base, audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.duration = av_rescale_q(pkt.duration, codec_ctx->time_base, audio_stream->time_base);
			pkt.stream_index = audio_stream->index;
			av_interleaved_write_frame(fmt_ctx, &pkt);
			av_free_packet(&pkt);
		}
	}

	(*env)->ReleaseByteArrayElements(env, data, pData, 0);
}

// 释放资源
void audio_encode_release()
{
	// 释放资源
	if ((fmt != NULL) && !(fmt->flags & AVFMT_NOFILE))
	{
		avio_close(fmt_ctx->pb);
	}

	if (audio_stream != NULL)
	{
		avcodec_close(audio_stream->codec);
	}
	avformat_free_context(fmt_ctx);

	if (src_samples_data != NULL)
	{
		av_freep(&src_samples_data[0]);
	}

	if (dst_samples_data != NULL)
	{
		av_freep(&dst_samples_data[0]);
	}

	if (frame != NULL)
	{
		av_frame_free(&frame);
	}

	if (swr_ctx != NULL)
	{
		swr_free(&swr_ctx);
	}

	// 参数重置
	frame = NULL;
	codec = NULL;
	codec_ctx = NULL;
	fmt_ctx = NULL;
	fmt = NULL;
	swr_ctx = NULL;
	src_samples_data = NULL;
	dst_samples_data = NULL;
	audio_stream = NULL;
	ret = 0;
	samples_count = 0;
	got_packet = 0;
	src_samples_size = 0;
	dst_samples_size = 0;
	src_nb_samples = 0;
	dst_nb_samples = 0;
	max_dst_nb_samples = 0;
}
