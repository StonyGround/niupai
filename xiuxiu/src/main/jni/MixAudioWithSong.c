/**
 * 伴奏与人声混音,仅支持44100采样率的伴奏
 * 先将mp3伴奏解码并转为aac数据,再与人声混音到mp4文件
 */

#include "AVProcessing.h"

#define  MAX_SAMPLES   1000000
#define  MIN_SAMPLES  -1000000

// 混音公式
inline int aac_mix_samples(int a, int b)
{
	int result = a < 0 && b < 0 ? ((a + b) - (a * b) / MIN_SAMPLES) : (a > 0 && b > 0 ? ((a + b) - (a * b)/MAX_SAMPLES) : (a + b));
	return result > MAX_SAMPLES ? MAX_SAMPLES : (result < MIN_SAMPLES ? MIN_SAMPLES : result);
}

// 伴奏与人声混音
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_mixAudioWithSong(JNIEnv *env, jclass obj, jstring audioFilePath, jstring songFilePath, jstring outputFilePath)
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
	SwrContext *swr_ctx = NULL;
	uint8_t **dst_samples_data = NULL;

	// 基本参数
	int ret = RESULT_ERROR, audio_ret = 0, accsong_ret = 0;
	int src_nb_samples = 0, dst_nb_samples = 0, dst_samples_size = 0;
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

	// 找到mp3解码器并打开
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

	// 初始化样本数据
	src_nb_samples = codec_ctx_dec_accsong->frame_size;
	dst_nb_samples = codec_ctx_enc->frame_size;
	ret = av_samples_alloc_array_and_samples(&dst_samples_data, NULL, codec_ctx_enc->channels, dst_nb_samples, codec_ctx_enc->sample_fmt, 0);
	if (ret < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	dst_samples_size = av_samples_get_buffer_size(NULL, codec_ctx_enc->channels, codec_ctx_enc->frame_size, codec_ctx_enc->sample_fmt, 0);
	if (dst_samples_size < 0)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "src样本数...%d, dst样本数...%d, dst样本大小...%d", src_nb_samples, dst_nb_samples, dst_samples_size);

	// 初始化S16P到FLTP转换参数
	swr_ctx = swr_alloc();
	if (swr_ctx == NULL)
	{
		ret = RESULT_ERROR;
		goto end;
	}

	// set options
	av_opt_set_int       (swr_ctx, "in_channel_count",  codec_ctx_dec_accsong->channels,    0);
	av_opt_set_int       (swr_ctx, "in_sample_rate",    codec_ctx_dec_accsong->sample_rate, 0);
	av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt",     codec_ctx_dec_accsong->sample_fmt,  0);
	av_opt_set_int       (swr_ctx, "out_channel_count", codec_ctx_enc->channels,            0);
	av_opt_set_int       (swr_ctx, "out_sample_rate",   codec_ctx_enc->sample_rate,         0);
	av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt",    codec_ctx_enc->sample_fmt,          0);

	// initialize the resampling context
	if (swr_init(swr_ctx) < 0)
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

	while (1)
	{
		// 读数据
		audio_ret = av_read_frame(fmt_ctx_audio, &pkt_audio);
		accsong_ret = av_read_frame(fmt_ctx_accsong, &pkt_accsong);
		if (audio_ret < 0 || accsong_ret < 0)
		{
			break;
		}

		// 解码
		audio_ret = avcodec_decode_audio4(codec_ctx_dec_audio, frame_audio, &got_frame_audio, &pkt_audio);
		accsong_ret = avcodec_decode_audio4(codec_ctx_dec_accsong, frame_accsong, &got_frame_accsong, &pkt_accsong);
		if (audio_ret < 0 || accsong_ret < 0)
		{
			break;
		}

		if (got_frame_audio && got_frame_accsong)
		{
			// 格式转换
			if (swr_ctx != NULL)
			{
				ret = swr_convert(swr_ctx, dst_samples_data, dst_nb_samples, (const uint8_t **)frame_accsong->data, src_nb_samples);
				if (ret < 0)
				{
					goto end;
				}
			}

			// 人声与伴奏混音
			float *p = (float *)frame_audio->data[0];
			float *q = (float *)dst_samples_data[0];
			int i = 0, k = 0;
			int len = dst_nb_samples * codec_ctx_enc->channels;
			for (i = 0; i < len; i++, k++)
			{
				if (i == dst_nb_samples)
				{
					k = 0;
					p = (float *)frame_audio->data[1];
				}

				q[i] = aac_mix_samples(p[k]*1000000, q[i]*500000) * 0.000001f;
			}

			// 填充数据帧
			frame_audio->pts = av_rescale_q(samples_count, codec_ctx_enc->time_base, codec_ctx_enc->time_base);
			ret = avcodec_fill_audio_frame(frame_audio, codec_ctx_enc->channels, codec_ctx_enc->sample_fmt, dst_samples_data[0], dst_samples_size, 0);
			if (ret < 0)
			{
				break;
			}
			samples_count += dst_nb_samples;

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

	if (dst_samples_data != NULL)
	{
		av_freep(&dst_samples_data[0]);
	}

	if (swr_ctx != NULL)
	{
		swr_free(&swr_ctx);
	}

	(*env)->ReleaseStringUTFChars(env, audioFilePath,   audio_filename);
	(*env)->ReleaseStringUTFChars(env, songFilePath,  accsong_filename);
	(*env)->ReleaseStringUTFChars(env, outputFilePath, output_filename);

	// __android_log_print( ANDROID_LOG_INFO, "dd_cc_dd", "资源释放成功.....");

	return ret;
}
