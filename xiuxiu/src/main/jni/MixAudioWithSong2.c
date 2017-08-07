/**
 * 伴奏与人声混音,支持不同采样率及声道的伴奏
 * 先将伴奏与人声解码并转为s16数据完成混音,在转为fltp数据编码到mp4文件
 */

#include "AVProcessing.h"

#define  MY_INT16_MAX   32767
#define  MY_INT16_MIN  -32768

// 混音算法
inline short TPMixSamples(short a, short b)
{
	int result = a < 0 && b < 0 ? ((int)a + (int)b) - (((int)a * (int)b) / MY_INT16_MIN) : ( a > 0 && b > 0 ? ((int)a + (int)b) - (((int)a * (int)b)/MY_INT16_MAX) : a + b);
	return result > MY_INT16_MAX ? MY_INT16_MAX : (result < MY_INT16_MIN ? MY_INT16_MIN : result);
}

struct BufData
{
	uint8_t *data;
	int buf_len;
};

// 伴奏与人声混音
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_mixAudioWithSong2(JNIEnv *env, jclass obj, jstring audioFilePath, jstring songFilePath, jstring outputFilePath, jdouble strength)
{
	const char *audio_filename   = (*env)->GetStringUTFChars(env, audioFilePath,  0);
	const char *accsong_filename = (*env)->GetStringUTFChars(env, songFilePath,   0);
	const char *output_filename  = (*env)->GetStringUTFChars(env, outputFilePath, 0);

	// 伴奏
	AVPacket pkt_accsong;
	AVFrame *frame_accsong = NULL;
	AVFormatContext *fmt_ctx_accsong  = NULL;

	// 人声
	AVPacket pkt_audio;
	AVFrame *frame_audio = NULL;
	AVFormatContext *fmt_ctx_audio  = NULL;

	// 编解码
	AVCodec *codec_enc = NULL;
	AVCodecContext *codec_ctx_enc = NULL;
	AVCodec *codec_dec_audio = NULL;
	AVCodecContext *codec_ctx_dec_audio = NULL;
	AVCodec *codec_dec_accsong = NULL;
	AVCodecContext *codec_ctx_dec_accsong = NULL;

	// 输出
	AVPacket pkt_output;
	AVFormatContext *fmt_ctx_output = NULL;
	AVOutputFormat  *fmt_output = NULL;
	AVStream *audio_stream = NULL;

	// 格式转换
	SwrContext *acc_swr_ctx = NULL;
	SwrContext *ado_swr_ctx = NULL;
	SwrContext *ret_swr_ctx = NULL;
	uint8_t **acc_dst_samples_data = NULL;
	uint8_t **ado_dst_samples_data = NULL;
	uint8_t **ret_dst_samples_data = NULL;
	const int bufTotalSize = 40960 * sizeof(float);
	struct BufData buf_data;
	buf_data.data = (uint8_t *)malloc(bufTotalSize);
	buf_data.buf_len = 0;

	// 基本参数
	int ret = RESULT_ERROR, audio_ret = 0, accsong_ret = 0, samples_size = 0;
	int acc_src_nb_samples = 0, acc_dst_nb_samples = 0, acc_max_dst_nb_samples = 0;
	int ado_src_nb_samples = 0, ado_dst_nb_samples = 0, ado_max_dst_nb_samples = 0;
	int ret_src_nb_samples = 0, ret_dst_nb_samples = 0, ret_max_dst_nb_samples = 0;
	int samples_count = 0, stream_index = 0, got_packet = 0, got_frame_audio = 0, got_frame_accsong = 0;

	// register all formats and codecs
	av_register_all();

	// 打开人声音频文件
	if (avformat_open_input(&fmt_ctx_audio, audio_filename, NULL, NULL) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 找到aac解码器并打开
	if (avformat_find_stream_info(fmt_ctx_audio, NULL) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	stream_index = av_find_best_stream(fmt_ctx_audio, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	codec_ctx_dec_audio = fmt_ctx_audio->streams[stream_index]->codec;
	av_opt_set_int(codec_ctx_dec_audio, "refcounted_frames", 1, 0);
	codec_dec_audio = avcodec_find_decoder(codec_ctx_dec_audio->codec_id);
	if (codec_dec_audio == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	ret = avcodec_open2(codec_ctx_dec_audio, codec_dec_audio, NULL);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 打开伴奏音频文件
	if (avformat_open_input(&fmt_ctx_accsong, accsong_filename, NULL, NULL) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 找到伴奏解码器并打开
	if (avformat_find_stream_info(fmt_ctx_accsong, NULL) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	stream_index = av_find_best_stream(fmt_ctx_accsong, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	codec_ctx_dec_accsong = fmt_ctx_accsong->streams[stream_index]->codec;
	av_opt_set_int(codec_ctx_dec_accsong, "refcounted_frames", 1, 0);
	codec_dec_accsong = avcodec_find_decoder(codec_ctx_dec_accsong->codec_id);
	if (codec_dec_accsong == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	ret = avcodec_open2(codec_ctx_dec_accsong, codec_dec_accsong, NULL);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 新建输出文件
	avformat_alloc_output_context2(&fmt_ctx_output, NULL, NULL, output_filename);
	if (fmt_ctx_output == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}
	fmt_output = fmt_ctx_output->oformat;

	// 初始化aac编码器并添加音频流
	codec_enc = avcodec_find_encoder(AV_CODEC_ID_AAC);
	if (codec_enc == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	audio_stream = avformat_new_stream(fmt_ctx_output, codec_enc);
	if (audio_stream == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	audio_stream->id = fmt_ctx_output->nb_streams - 1;
	codec_ctx_enc = audio_stream->codec;
	codec_ctx_enc->bit_rate = 64000;
	codec_ctx_enc->sample_rate = 44100;
	codec_ctx_enc->channel_layout = AV_CH_LAYOUT_STEREO;
	codec_ctx_enc->channels = 2;
	codec_ctx_enc->sample_fmt = AV_SAMPLE_FMT_FLTP;
	codec_ctx_enc->strict_std_compliance = FF_COMPLIANCE_EXPERIMENTAL;
// 	codec_ctx_enc->time_base.num = 1;
// 	codec_ctx_enc->time_base.den = sampleRate;
//  codec_ctx_enc->profile = FF_PROFILE_AAC_MAIN;
//  codec_ctx_enc->codec_type = AVMEDIA_TYPE_AUDIO;

	if (fmt_output->flags & AVFMT_GLOBALHEADER)
	{
		codec_ctx_enc->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	ret = avcodec_open2(codec_ctx_enc, codec_enc, NULL);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 伴奏基本参数
	acc_src_nb_samples = codec_ctx_dec_accsong->frame_size;
	acc_dst_nb_samples = av_rescale_rnd(acc_src_nb_samples, codec_ctx_enc->sample_rate, codec_ctx_dec_accsong->sample_rate, AV_ROUND_UP);
	acc_max_dst_nb_samples = acc_dst_nb_samples;
	ret = av_samples_alloc_array_and_samples(&acc_dst_samples_data, NULL, codec_ctx_enc->channels, acc_dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 人声
	ado_src_nb_samples = codec_ctx_dec_audio->frame_size;
	ado_dst_nb_samples = av_rescale_rnd(ado_src_nb_samples, codec_ctx_enc->sample_rate, codec_ctx_dec_audio->sample_rate, AV_ROUND_UP);
	ado_max_dst_nb_samples = ado_dst_nb_samples;
	ret = av_samples_alloc_array_and_samples(&ado_dst_samples_data, NULL, codec_ctx_enc->channels, ado_dst_nb_samples, AV_SAMPLE_FMT_S16, 0);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 混音结果
	ret_src_nb_samples = codec_ctx_enc->frame_size;
	ret_dst_nb_samples = ret_src_nb_samples;
	ret_max_dst_nb_samples = ret_src_nb_samples;
	ret = av_samples_alloc_array_and_samples(&ret_dst_samples_data, NULL, codec_ctx_enc->channels, ret_dst_nb_samples, codec_ctx_enc->sample_fmt, 0);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	samples_size = av_samples_get_buffer_size(NULL, codec_ctx_enc->channels, codec_ctx_enc->frame_size, codec_ctx_enc->sample_fmt, 0);
	if (samples_size < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "src样本数...%d, dst样本数...%d, dst样本大小...%d", ret_src_nb_samples, ret_dst_nb_samples, samples_size);

	// 初始化伴奏转换参数
	acc_swr_ctx = swr_alloc();
	if (acc_swr_ctx == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// set options
	av_opt_set_int       (acc_swr_ctx, "in_channel_count",  codec_ctx_dec_accsong->channels,    0);
	av_opt_set_int       (acc_swr_ctx, "in_sample_rate",    codec_ctx_dec_accsong->sample_rate, 0);
	av_opt_set_sample_fmt(acc_swr_ctx, "in_sample_fmt",     codec_ctx_dec_accsong->sample_fmt,  0);
	av_opt_set_int       (acc_swr_ctx, "out_channel_count", codec_ctx_enc->channels,            0);
	av_opt_set_int       (acc_swr_ctx, "out_sample_rate",   codec_ctx_enc->sample_rate,         0);
	av_opt_set_sample_fmt(acc_swr_ctx, "out_sample_fmt",    AV_SAMPLE_FMT_S16,                  0);

	// initialize the resampling context
	if (swr_init(acc_swr_ctx) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 初始化人声转换参数
	ado_swr_ctx = swr_alloc();
	if (ado_swr_ctx == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// set options
	av_opt_set_int       (ado_swr_ctx, "in_channel_count",  codec_ctx_dec_audio->channels,    0);
	av_opt_set_int       (ado_swr_ctx, "in_sample_rate",    codec_ctx_dec_audio->sample_rate, 0);
	av_opt_set_sample_fmt(ado_swr_ctx, "in_sample_fmt",     codec_ctx_dec_audio->sample_fmt,  0);
	av_opt_set_int       (ado_swr_ctx, "out_channel_count", codec_ctx_enc->channels,          0);
	av_opt_set_int       (ado_swr_ctx, "out_sample_rate",   codec_ctx_enc->sample_rate,       0);
	av_opt_set_sample_fmt(ado_swr_ctx, "out_sample_fmt",    AV_SAMPLE_FMT_S16,                0);

	// initialize the resampling context
	if (swr_init(ado_swr_ctx) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 初始化混音结果转换参数
	ret_swr_ctx = swr_alloc();
	if (ret_swr_ctx == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// set options
	av_opt_set_int       (ret_swr_ctx, "in_channel_count",  codec_ctx_enc->channels,    0);
	av_opt_set_int       (ret_swr_ctx, "in_sample_rate",    codec_ctx_enc->sample_rate, 0);
	av_opt_set_sample_fmt(ret_swr_ctx, "in_sample_fmt",     AV_SAMPLE_FMT_S16,          0);
	av_opt_set_int       (ret_swr_ctx, "out_channel_count", codec_ctx_enc->channels,    0);
	av_opt_set_int       (ret_swr_ctx, "out_sample_rate",   codec_ctx_enc->sample_rate, 0);
	av_opt_set_sample_fmt(ret_swr_ctx, "out_sample_fmt",    codec_ctx_enc->sample_fmt,  0);

	// initialize the resampling context
	if (swr_init(ret_swr_ctx) < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 新建数据帧
	frame_audio = av_frame_alloc();
	if (frame_audio == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	frame_accsong = av_frame_alloc();
	if (frame_accsong == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// 打开输出文件
	if (!(fmt_output->flags & AVFMT_NOFILE))
	{
		ret = avio_open(&fmt_ctx_output->pb, output_filename, AVIO_FLAG_WRITE);
		if (ret < 0)
		{
			ret = RESULT_ERROR;
			goto end;
		}
	}

	// Write the stream header
	ret = avformat_write_header(fmt_ctx_output, NULL);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "写文件头成功.....");

	// 初始化解码包
	av_init_packet(&pkt_audio);
	pkt_audio.data = NULL;
	pkt_audio.size = 0;

	av_init_packet(&pkt_accsong);
	pkt_accsong.data = NULL;
	pkt_accsong.size = 0;

	// 初始化阈值
	int threshold_size = 2*samples_size;

	while (1)
	{
		// 伴奏音频
		if (buf_data.buf_len < threshold_size)
		{
			while (buf_data.buf_len < bufTotalSize - threshold_size)
			{
				accsong_ret = av_read_frame(fmt_ctx_accsong, &pkt_accsong);
				if (accsong_ret < 0)
				{
					break;
				}

				accsong_ret = avcodec_decode_audio4(codec_ctx_dec_accsong, frame_accsong, &got_frame_accsong, &pkt_accsong);
				if (accsong_ret < 0)
				{
					break;
				}

				if (got_frame_accsong)
				{
					// 格式转换
					if (acc_swr_ctx != NULL)
					{
						acc_dst_nb_samples = av_rescale_rnd(swr_get_delay(acc_swr_ctx, codec_ctx_dec_accsong->sample_rate) + acc_src_nb_samples, codec_ctx_enc->sample_rate, codec_ctx_dec_accsong->sample_rate, AV_ROUND_UP);
						if (acc_dst_nb_samples > acc_max_dst_nb_samples)
						{
							av_free(acc_dst_samples_data[0]);
							ret = av_samples_alloc(acc_dst_samples_data, NULL, codec_ctx_enc->channels, acc_dst_nb_samples, AV_SAMPLE_FMT_S16, 1);
							if (ret < 0)
							{
								break;
							}
							acc_max_dst_nb_samples = acc_dst_nb_samples;
						}

						ret = swr_convert(acc_swr_ctx, acc_dst_samples_data, acc_dst_nb_samples, (const uint8_t **)frame_accsong->data, acc_src_nb_samples);
						if (ret < 0)
						{
							goto end;
						}

						int dst_bufsize = av_samples_get_buffer_size(NULL, codec_ctx_enc->channels, ret, AV_SAMPLE_FMT_S16, 1);
						if (dst_bufsize < 0)
						{
							goto end;
						}

						// 填充数据
						memcpy(buf_data.data + buf_data.buf_len, (uint8_t *)acc_dst_samples_data[0], dst_bufsize);
						buf_data.buf_len += dst_bufsize;

						// __android_log_print(ANDROID_LOG_INFO, "dd_cc_dd", "dst_bufsize...%d....swr ret...%d", dst_bufsize, ret);
					}// if
				}// if
			}// while
		}// if

		// 人声音频
		audio_ret = av_read_frame(fmt_ctx_audio, &pkt_audio);
		if (audio_ret < 0)
		{
			break;
		}

		audio_ret = avcodec_decode_audio4(codec_ctx_dec_audio, frame_audio, &got_frame_audio, &pkt_audio);
		if (audio_ret < 0)
		{
			break;
		}

		if (got_frame_audio)
		{
			// 格式转换
			if (ado_swr_ctx != NULL)
			{
				ado_dst_nb_samples = av_rescale_rnd(swr_get_delay(ado_swr_ctx, codec_ctx_dec_audio->sample_rate) + ado_src_nb_samples, codec_ctx_enc->sample_rate, codec_ctx_dec_audio->sample_rate, AV_ROUND_UP);
				if (ado_dst_nb_samples > ado_max_dst_nb_samples)
				{
					av_free(ado_dst_samples_data[0]);
					ret = av_samples_alloc(ado_dst_samples_data, NULL, codec_ctx_enc->channels, ado_dst_nb_samples, AV_SAMPLE_FMT_S16, 1);
					if (ret < 0)
					{
						break;
					}
					ado_max_dst_nb_samples = ado_dst_nb_samples;
				}

				ret = swr_convert(ado_swr_ctx, ado_dst_samples_data, ado_dst_nb_samples, (const uint8_t **)frame_audio->data, ado_src_nb_samples);
				if (ret < 0)
				{
					goto end;
				}
			}

			// 人声与伴奏混音
			int16_t *p = (int16_t *)ado_dst_samples_data[0];
			int16_t *q = (int16_t *)buf_data.data;
			int i = 0;
			int len = ret_src_nb_samples * codec_ctx_enc->channels;
			for (i = 0; i < len; i++)
			{
				p[i] = TPMixSamples(p[i], q[i] * strength);
			}

			// 结果转换
			if (ret_swr_ctx != NULL)
			{
				ret_dst_nb_samples = av_rescale_rnd(swr_get_delay(ret_swr_ctx, codec_ctx_enc->sample_rate) + ret_src_nb_samples, codec_ctx_enc->sample_rate, codec_ctx_enc->sample_rate, AV_ROUND_UP);
				if (ret_dst_nb_samples > ret_max_dst_nb_samples)
				{
					av_free(ret_dst_samples_data[0]);
					ret = av_samples_alloc(ret_dst_samples_data, NULL, codec_ctx_enc->channels, ret_dst_nb_samples, codec_ctx_enc->sample_fmt, 1);
					if (ret < 0)
					{
						break;
					}
					ret_max_dst_nb_samples = ret_dst_nb_samples;
				}

				ret = swr_convert(ret_swr_ctx, ret_dst_samples_data, ret_dst_nb_samples, (const uint8_t **)ado_dst_samples_data, ret_src_nb_samples);
				if (ret < 0)
				{
					goto end;
				}
			}// if

			// 填充数据帧
			frame_audio->pts = av_rescale_q(samples_count, codec_ctx_enc->time_base, codec_ctx_enc->time_base);
			ret = avcodec_fill_audio_frame(frame_audio, codec_ctx_enc->channels, codec_ctx_enc->sample_fmt, ret_dst_samples_data[0], samples_size, 0);
			if (ret < 0)
			{
				break;
			}
			samples_count += ret_src_nb_samples;

			// 编码
			av_init_packet(&pkt_output);
			pkt_output.data = NULL;
			pkt_output.size = 0;
			ret = avcodec_encode_audio2(codec_ctx_enc, &pkt_output, frame_audio, &got_packet);
			if (ret < 0)
			{
				ret = RESULT_ERROR;
				goto end;
			}

			// 输出
			if (got_packet)
			{
				av_interleaved_write_frame(fmt_ctx_output, &pkt_output);
				av_free_packet(&pkt_output);
			}

			// 调整buf数据
			buf_data.buf_len -= len*2;
			if (buf_data.buf_len <= 0)
			{
				break;
			}
			memcpy(buf_data.data, buf_data.data + len*2, buf_data.buf_len);

			// __android_log_print(ANDROID_LOG_INFO, "dd_cc_dd", "buf_data.buf_len...%d..", buf_data.buf_len);
		}// if

		av_frame_unref(frame_audio);
		av_frame_unref(frame_accsong);

		av_free_packet(&pkt_audio);
		av_free_packet(&pkt_accsong);
	}// while

	// get the delayed frames
	while (got_packet)
	{
		ret = avcodec_encode_audio2(codec_ctx_enc, &pkt_output, NULL, &got_packet);
		if (ret < 0)
		{
			break;
		}

		if (got_packet)
		{
			av_interleaved_write_frame(fmt_ctx_output, &pkt_output);
			av_free_packet(&pkt_output);
		}
	}

	// Write the trailer
	av_write_trailer(fmt_ctx_output);
	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "写文件尾成功.....");
	ret = RESULT_OK;

end:
    if ((fmt_output != NULL) && !(fmt_output->flags & AVFMT_NOFILE))
	{
		avio_close(fmt_ctx_output->pb);
	}

	if (audio_stream != NULL)
	{
		avcodec_close(audio_stream->codec);
	}
	avformat_free_context(fmt_ctx_output);
	avformat_close_input(&fmt_ctx_audio);
	avformat_close_input(&fmt_ctx_accsong);

	av_frame_free(&frame_audio);
	av_frame_free(&frame_accsong);

	if (acc_dst_samples_data != NULL)
	{
		av_freep(&acc_dst_samples_data[0]);
	}

	if (acc_swr_ctx != NULL)
	{
		swr_free(&acc_swr_ctx);
	}

	if (ado_dst_samples_data != NULL)
	{
		av_freep(&ado_dst_samples_data[0]);
	}

	if (ado_swr_ctx != NULL)
	{
		swr_free(&ado_swr_ctx);
	}

	if (ret_dst_samples_data != NULL)
	{
		av_freep(&ret_dst_samples_data[0]);
	}

	if (ret_swr_ctx != NULL)
	{
		swr_free(&ret_swr_ctx);
	}

	if (buf_data.data != NULL)
	{
		free(buf_data.data);
	}

	(*env)->ReleaseStringUTFChars(env, audioFilePath,   audio_filename);
	(*env)->ReleaseStringUTFChars(env, songFilePath,  accsong_filename);
	(*env)->ReleaseStringUTFChars(env, outputFilePath, output_filename);

	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "资源释放成功.....");

	return ret;
}
