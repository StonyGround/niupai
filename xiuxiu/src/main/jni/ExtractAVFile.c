/**
 * 音视频分离
 * 从mp4文件中分别抽取音频及视频媒体流
 */

#include "AVProcessing.h"

// 添加流
static AVStream* add_av_stream(AVFormatContext *fmt_ctx, AVStream *input_stream)
{
	AVStream *output_stream = NULL;

	output_stream = avformat_new_stream(fmt_ctx, input_stream->codec->codec);
	if (output_stream == NULL)
	{
		return NULL;
	}

	int ret = avcodec_copy_context(output_stream->codec, input_stream->codec);
	if (ret < 0)
	{
		return NULL;
	}

	output_stream->codec->codec_tag = 0;
	if (fmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
	{
		output_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	return output_stream;
}

// 打开流
static AVStream* open_av_stream(AVFormatContext* fmt_ctx, enum AVMediaType type)
{
	if (avformat_find_stream_info(fmt_ctx, NULL) < 0)
	{
		return NULL;
	}

	int stream_index = av_find_best_stream(fmt_ctx, type, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		return NULL;
	}
	else
	{
		return fmt_ctx->streams[stream_index];
	}
}

// 分离视频
static int extract_video_file(const char *input_filename, const char *output_filename, double max_duration)
{
	int ret = RESULT_ERROR;
	AVFormatContext *video_fmt_ctx = NULL;
	AVFormatContext *output_fmt_ctx = NULL;
	AVOutputFormat  *output_fmt = NULL;
	AVStream *output_video_stream = NULL, *input_video_stream = NULL;

	// register all formats and codecs
	av_register_all();

	// 打开文件
	if (avformat_open_input(&video_fmt_ctx, input_filename, NULL, NULL) < 0)
	{
		goto end;
	}

	// 打开视频流
	input_video_stream = open_av_stream(video_fmt_ctx, AVMEDIA_TYPE_VIDEO);
	if (input_video_stream == NULL)
	{
		goto end;
	}

	// 新建输出文件
	avformat_alloc_output_context2(&output_fmt_ctx, NULL, NULL, output_filename);
	if (output_fmt_ctx == NULL)
	{
		goto end;
	}
	output_fmt = output_fmt_ctx->oformat;

	// 添加视频流
	output_video_stream = add_av_stream(output_fmt_ctx, input_video_stream);
	if (output_video_stream == NULL)
	{
		goto end;
	}

	// 打开输出文件
	if (!(output_fmt->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&output_fmt_ctx->pb, output_filename, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			goto end;
		}
	}

	// Write the stream header
	ret = avformat_write_header(output_fmt_ctx, NULL);
	if (ret < 0)
	{
		goto end;
	}

	// 抽取视频数据包
	double video_time = 0.0;
	AVPacket pkt;
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	while (av_read_frame(video_fmt_ctx, &pkt) >= 0)
	{
		if (pkt.stream_index == 0)
		{
			video_time = output_video_stream->pts.val * av_q2d(output_video_stream->time_base);
			if (video_time >= max_duration)
			{
				av_free_packet(&pkt);
				break;
			}

			pkt.pts = av_rescale_q_rnd(pkt.pts, input_video_stream->time_base, output_video_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.dts = av_rescale_q_rnd(pkt.dts, input_video_stream->time_base, output_video_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.duration = (int)av_rescale_q(pkt.duration, input_video_stream->time_base, output_video_stream->time_base);
			pkt.stream_index = output_video_stream->index;
			av_interleaved_write_frame(output_fmt_ctx, &pkt);
		}
		av_free_packet(&pkt);
	}// while

	// Write the trailer
	av_write_trailer(output_fmt_ctx);
	ret = RESULT_OK;

end:
	if ((output_fmt != NULL) && !(output_fmt->flags & AVFMT_NOFILE))
	{
		avio_close(output_fmt_ctx->pb);
	}

	if (output_video_stream != NULL)
	{
		avcodec_close(output_video_stream->codec);
	}

	avformat_close_input(&video_fmt_ctx);
	avformat_free_context(output_fmt_ctx);

	return ret;
}

// 分离音频
static int extract_audio_file(const char *input_filename, const char *output_filename, double max_duration)
{
	int ret = RESULT_ERROR;
	AVFormatContext *audio_fmt_ctx = NULL;
	AVFormatContext *output_fmt_ctx = NULL;
	AVOutputFormat  *output_fmt = NULL;
	AVStream *input_audio_stream = NULL, *output_audio_stream = NULL;

	// register all formats and codecs
	av_register_all();

	// 打开音频文件
	if (avformat_open_input(&audio_fmt_ctx, input_filename, NULL, NULL) < 0)
	{
		goto end;
	}

	// 打开音频流
	input_audio_stream = open_av_stream(audio_fmt_ctx, AVMEDIA_TYPE_AUDIO);
	if (input_audio_stream == NULL)
	{
		goto end;
	}

	// 新建输出文件
	avformat_alloc_output_context2(&output_fmt_ctx, NULL, NULL, output_filename);
	if (output_fmt_ctx == NULL)
	{
		goto end;
	}
	output_fmt = output_fmt_ctx->oformat;

	// 添加音视频流
	output_audio_stream = add_av_stream(output_fmt_ctx, input_audio_stream);
	if (output_audio_stream == NULL)
	{
		goto end;
	}

	// 打开输出文件
	if (!(output_fmt->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&output_fmt_ctx->pb, output_filename, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			goto end;
		}
	}

	// Write the stream header
	ret = avformat_write_header(output_fmt_ctx, NULL);
	if (ret < 0)
	{
		goto end;
	}

	// 抽取音频数据包
	double audio_time = 0.0;
	AVPacket pkt;
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	while (av_read_frame(audio_fmt_ctx, &pkt) >= 0)
	{
		if (pkt.stream_index == 1)
		{
			audio_time = output_audio_stream->pts.val * av_q2d(output_audio_stream->time_base);
			if (audio_time > max_duration)
			{
				av_free_packet(&pkt);
				break;
			}

			pkt.pts = av_rescale_q_rnd(pkt.pts, input_audio_stream->time_base, output_audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.dts = av_rescale_q_rnd(pkt.dts, input_audio_stream->time_base, output_audio_stream->time_base, AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX);
			pkt.duration = (int)av_rescale_q(pkt.duration, input_audio_stream->time_base, output_audio_stream->time_base);
			pkt.stream_index = output_audio_stream->index;
			av_interleaved_write_frame(output_fmt_ctx, &pkt);
		}
		av_free_packet(&pkt);
	}// while

	// Write the trailer
	av_write_trailer(output_fmt_ctx);
	ret = RESULT_OK;

end:
	if ((output_fmt != NULL) && !(output_fmt->flags & AVFMT_NOFILE))
	{
		avio_close(output_fmt_ctx->pb);
	}

	if (output_audio_stream != NULL)
	{
		avcodec_close(output_audio_stream->codec);
	}

	avformat_close_input(&audio_fmt_ctx);
	avformat_free_context(output_fmt_ctx);

	return ret;
}

// 音视频分离
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_extractAVFile(JNIEnv *env, jclass obj, jstring inputFilePath, jstring vOutputFilePath, jstring aOutputFilePath, jdouble maxDuration)
{
	const char *input_filename = (*env)->GetStringUTFChars(env, inputFilePath, 0);
	const char *v_output_filename = (*env)->GetStringUTFChars(env, vOutputFilePath, 0);
	const char *a_output_filename = (*env)->GetStringUTFChars(env, aOutputFilePath, 0);

	int vRet = extract_video_file(input_filename, v_output_filename, maxDuration);
	int aRet = extract_audio_file(input_filename, a_output_filename, maxDuration);

	(*env)->ReleaseStringUTFChars(env, inputFilePath, input_filename);
	(*env)->ReleaseStringUTFChars(env, vOutputFilePath, v_output_filename);
	(*env)->ReleaseStringUTFChars(env, aOutputFilePath, a_output_filename);

	return vRet | aRet;
}
