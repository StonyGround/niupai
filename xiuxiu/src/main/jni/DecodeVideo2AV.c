/**
 * 视频解码
 * 将视频中两路音视频数据分离为独立的音频数据及视频数据
 */

#include "AVProcessing.h"

#define CHANNEL_IN_MONO     1
#define CHANNEL_IN_STEREO   2
#define VIDEO_STREAM_INDEX  0
#define AUDIO_STREAM_INDEX  1

// 视频解码
static void decodeVideo(const char *video_filename, const char *output_filename)
{
	FILE *fp = NULL;
	AVPacket pkt;
	AVFrame *frame = NULL;
	AVCodec *codec = NULL;
	AVCodecContext *codec_ctx = NULL;
	AVFormatContext *fmt_ctx = NULL;
	uint8_t *video_dst_data[4] = { NULL };
	int video_dst_linesize[4] = { 0 };
	int video_dst_bufsize = 0;
	int ret = 0, got_frame = 0;

	// register all formats and codecs
	av_register_all();

	// 打开视频文件
	if (avformat_open_input(&fmt_ctx, video_filename, NULL, NULL) < 0)
	{
		goto end;
	}

	if (avformat_find_stream_info(fmt_ctx, NULL) < 0)
	{
		goto end;
	}

	int stream_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		goto end;
	}

	// 查找编解码器并打开
	codec_ctx = fmt_ctx->streams[stream_index]->codec;
	av_opt_set_int(codec_ctx, "refcounted_frames", 1, 0);
	codec = avcodec_find_decoder(codec_ctx->codec_id);
	if (codec == NULL)
	{
		goto end;
	}

	ret = avcodec_open2(codec_ctx, codec, NULL);
	if (ret < 0)
	{
		goto end;
	}

	// 打开输出文件
	fp = fopen(output_filename, "wb");
	if (fp == NULL)
	{
		goto end;
	}

	// allocate image where the decoded image will be put
	video_dst_bufsize = av_image_alloc(video_dst_data, video_dst_linesize, codec_ctx->width, codec_ctx->height, codec_ctx->pix_fmt, 1);
	if (video_dst_bufsize < 0)
	{
		goto end;
	}

	// 新建数据帧
	frame = av_frame_alloc();
	if (frame == NULL)
	{
		goto end;
	}

	// 解码
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	while (av_read_frame(fmt_ctx, &pkt) >= 0)
	{
		if (pkt.stream_index == VIDEO_STREAM_INDEX)
		{
			do
			{
				ret = avcodec_decode_video2(codec_ctx, frame, &got_frame, &pkt);
				if (ret < 0)
				{
					break;
				}

				if (got_frame)
				{
					av_image_copy(
						video_dst_data, video_dst_linesize,
						(const uint8_t **)frame->data, frame->linesize,
						codec_ctx->pix_fmt, codec_ctx->width, codec_ctx->height);
					fwrite(video_dst_data[0], sizeof(uint8_t), video_dst_bufsize, fp);
					av_frame_unref(frame);
				}

				pkt.data += ret;
				pkt.size -= ret;
			} while (pkt.size > 0);
		}
		av_free_packet(&pkt);
	}

	/* flush cached frames */
	pkt.data = NULL;
	pkt.size = 0;
	if (pkt.stream_index == VIDEO_STREAM_INDEX)
	{
		do
		{
			ret = avcodec_decode_video2(codec_ctx, frame, &got_frame, &pkt);
			if (ret < 0)
			{
				break;
			}

			if (got_frame)
			{
				av_image_copy(
					video_dst_data, video_dst_linesize,
					(const uint8_t **)frame->data, frame->linesize,
					codec_ctx->pix_fmt, codec_ctx->width, codec_ctx->height);
				fwrite(video_dst_data[0], sizeof(uint8_t), video_dst_bufsize, fp);
				av_frame_unref(frame);
			}
		} while (got_frame);
	}

end:
	avcodec_close(codec_ctx);
	avformat_close_input(&fmt_ctx);
	av_frame_free(&frame);

	if (video_dst_data != NULL)
	{
		av_freep(&video_dst_data[0]);
	}

	if (fp != NULL)
	{
		fclose(fp);
	}
}

// 音频解码
static void decodeAudio(const char *audio_filename, const char *output_filename)
{
	FILE *fp = NULL;
	AVPacket pkt;
	AVFrame *frame = NULL;
	AVCodec *codec = NULL;
	AVCodecContext *codec_ctx = NULL;
	AVFormatContext *fmt_ctx = NULL;
	SwrContext *swr_ctx = NULL;
	uint8_t **dst_samples_data = NULL;
	int ret = 0, got_frame = 0;
	int src_nb_samples = 0;
	int dst_nb_samples = 0, max_dst_nb_samples = 0, dst_samples_size = 0;

	// register all formats and codecs
	av_register_all();

	// 打开音频文件
	if (avformat_open_input(&fmt_ctx, audio_filename, NULL, NULL) < 0)
	{
		goto end;
	}

	if (avformat_find_stream_info(fmt_ctx, NULL) < 0)
	{
		goto end;
	}

	int stream_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		goto end;
	}

	// 查找编解码器并打开
	codec_ctx = fmt_ctx->streams[stream_index]->codec;
	av_opt_set_int(codec_ctx, "refcounted_frames", 1, 0);
	codec = avcodec_find_decoder(codec_ctx->codec_id);
	if (codec == NULL)
	{
		goto end;
	}

	ret = avcodec_open2(codec_ctx, codec, NULL);
	if (ret < 0)
	{
		goto end;
	}

	// 初始化样本数据
	src_nb_samples = codec_ctx->frame_size;
	max_dst_nb_samples = src_nb_samples;
	ret = av_samples_alloc_array_and_samples(&dst_samples_data, NULL, CHANNEL_IN_MONO, max_dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
	if (ret < 0)
	{
		goto end;
	}

	dst_samples_size = av_samples_get_buffer_size(NULL, CHANNEL_IN_MONO, codec_ctx->frame_size, AV_SAMPLE_FMT_S16, 0);
	if (dst_samples_size < 0)
	{
		goto end;
	}

	// 初始化S16P到S16转换参数
	swr_ctx = swr_alloc();
	if (swr_ctx == NULL)
	{
		goto end;
	}

	// set options
	av_opt_set_int       (swr_ctx, "in_channel_count",  codec_ctx->channels,    0);
	av_opt_set_int       (swr_ctx, "in_sample_rate",    codec_ctx->sample_rate, 0);
	av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt",     codec_ctx->sample_fmt,  0);
	av_opt_set_int       (swr_ctx, "out_channel_count", CHANNEL_IN_MONO,        0);
	av_opt_set_int       (swr_ctx, "out_sample_rate",   codec_ctx->sample_rate, 0);
	av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt",    AV_SAMPLE_FMT_S16,      0);

	// initialize the resampling context
	if (swr_init(swr_ctx) < 0)
	{
		goto end;
	}

	// 打开输出文件
	fp = fopen(output_filename, "wb");
	if (fp == NULL)
	{
		goto end;
	}

	// 新建数据帧
	frame = av_frame_alloc();
	if (frame == NULL)
	{
		goto end;
	}

	// 解码
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	while (av_read_frame(fmt_ctx, &pkt) >= 0)
	{
		if (pkt.stream_index == AUDIO_STREAM_INDEX)
		{
			do
			{
				ret = avcodec_decode_audio4(codec_ctx, frame, &got_frame, &pkt);
				if (ret < 0)
				{
					break;
				}
				else
				{
					ret = (ret != pkt.size) ? FFMIN(ret, pkt.size) : ret;
				}

				if (got_frame)
				{
					// 格式转换
					if (swr_ctx != NULL)
					{
						dst_nb_samples = (int)av_rescale_rnd(swr_get_delay(swr_ctx, codec_ctx->sample_rate) + src_nb_samples, codec_ctx->sample_rate, codec_ctx->sample_rate, AV_ROUND_UP);
						if (dst_nb_samples > max_dst_nb_samples)
						{
							av_free(dst_samples_data[0]);
							ret = av_samples_alloc(dst_samples_data, NULL, CHANNEL_IN_MONO, dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
							if (ret < 0)
							{
								goto end;
							}
							max_dst_nb_samples = dst_nb_samples;
							dst_samples_size = av_samples_get_buffer_size(NULL, CHANNEL_IN_MONO, dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
						}

						ret = swr_convert(swr_ctx, dst_samples_data, dst_nb_samples, (const uint8_t **)frame->data, src_nb_samples);
						if (ret < 0)
						{
							goto end;
						}
					}

					fwrite(dst_samples_data[0], sizeof(uint8_t), dst_samples_size, fp);
					av_frame_unref(frame);
				}

				pkt.data += ret;
				pkt.size -= ret;
			} while (pkt.size > 0);
		}// if

		av_free_packet(&pkt);
	}// while

	/* flush cached frames */
	pkt.data = NULL;
	pkt.size = 0;
	if (pkt.stream_index == AUDIO_STREAM_INDEX)
	{
		do
		{
			ret = avcodec_decode_audio4(codec_ctx, frame, &got_frame, &pkt);
			if (ret < 0)
			{
				break;
			}
			else
			{
				ret = (ret != pkt.size) ? FFMIN(ret, pkt.size) : ret;
			}

			if (got_frame)
			{
				// 格式转换
				if (swr_ctx != NULL)
				{
					dst_nb_samples = (int)av_rescale_rnd(swr_get_delay(swr_ctx, codec_ctx->sample_rate) + src_nb_samples, codec_ctx->sample_rate, codec_ctx->sample_rate, AV_ROUND_UP);
					if (dst_nb_samples > max_dst_nb_samples)
					{
						av_free(dst_samples_data[0]);
						ret = av_samples_alloc(dst_samples_data, NULL, CHANNEL_IN_MONO, dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
						if (ret < 0)
						{
							goto end;
						}
						max_dst_nb_samples = dst_nb_samples;
						dst_samples_size = av_samples_get_buffer_size(NULL, CHANNEL_IN_MONO, dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
					}

					ret = swr_convert(swr_ctx, dst_samples_data, dst_nb_samples, (const uint8_t **)frame->extended_data[0], src_nb_samples);
					if (ret < 0)
					{
						goto end;
					}
				}

				fwrite(dst_samples_data[0], sizeof(uint8_t), dst_samples_size, fp);
				av_frame_unref(frame);
			}
		} while (got_frame);
	}// if

end:
	avcodec_close(codec_ctx);
	avformat_close_input(&fmt_ctx);
	av_frame_free(&frame);

	if (dst_samples_data != NULL)
	{
		av_freep(&dst_samples_data[0]);
	}

	if (swr_ctx != NULL)
	{
		swr_free(&swr_ctx);
	}

	if (fp != NULL)
	{
		fclose(fp);
	}
}

// 视频解码为音频/视频流
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_decodeVideo2AV(JNIEnv *env, jclass obj, jstring videoFilePath, jstring vOutputFilePath, jstring aOutputFilePath)
{
	const char *video_filename = (*env)->GetStringUTFChars(env, videoFilePath, 0);
	const char *v_output_filename = (*env)->GetStringUTFChars(env, vOutputFilePath, 0);
	const char *a_output_filename = (*env)->GetStringUTFChars(env, aOutputFilePath, 0);

	decodeVideo(video_filename, v_output_filename);
	decodeAudio(video_filename, a_output_filename);

	(*env)->ReleaseStringUTFChars(env, videoFilePath, video_filename);
	(*env)->ReleaseStringUTFChars(env, vOutputFilePath, v_output_filename);
	(*env)->ReleaseStringUTFChars(env, aOutputFilePath, a_output_filename);

	return RESULT_OK;
}

