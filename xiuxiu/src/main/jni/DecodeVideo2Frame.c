/**
 * 视频解码
 * 将视频中两路音视频数据分离为独立的音频数据及视频数据
 */

#include "AVProcessing.h"

#define CHANNEL_IN_MONO     1
#define CHANNEL_IN_STEREO   2

static AVPacket pkt;
static AVFrame *frame = NULL;
static AVCodec *codec = NULL;
static AVCodecContext *codec_ctx = NULL;
static AVFormatContext *fmt_ctx = NULL;
static struct SwsContext *frame_size_sws_ctx = NULL;
static uint8_t *video_dst_data[4] = { NULL };
static int video_dst_linesize[4] = { 0 };
static int video_dst_bufsize = 0;
static int ret = 0, got_frame = 0, frame_index = 0;
static double time_per_frame = 0.0;
void video_decode_release();// 释放资源

// 初始化参数
JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_initVideoDecode(JNIEnv *env, jclass obj, jstring videoFilePath)
{
	const char *video_filename = (*env)->GetStringUTFChars(env, videoFilePath, 0);

	// register all formats and codecs
	av_register_all();

	// 打开视频文件
	if (avformat_open_input(&fmt_ctx, video_filename, NULL, NULL) < 0)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	if (avformat_find_stream_info(fmt_ctx, NULL) < 0)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	int stream_index = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
	if (stream_index < 0)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	// 计算每帧用时
	double num = fmt_ctx->streams[stream_index]->avg_frame_rate.num;
	double den = fmt_ctx->streams[stream_index]->avg_frame_rate.den;
	time_per_frame = 1000.0 * den / num;// 单位毫秒

	// 查找编解码器并打开
	codec_ctx = fmt_ctx->streams[stream_index]->codec;
	av_opt_set_int(codec_ctx, "refcounted_frames", 1, 0);
	codec = avcodec_find_decoder(codec_ctx->codec_id);
	if (codec == NULL)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	ret = avcodec_open2(codec_ctx, codec, NULL);
	if (ret < 0)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	// allocate image where the decoded image will be put
	video_dst_bufsize = av_image_alloc(video_dst_data, video_dst_linesize, 480, 480, codec_ctx->pix_fmt, 1);
	if (video_dst_bufsize < 0)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	// 新建数据帧
	frame = av_frame_alloc();
	if (frame == NULL)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	// 帧尺寸转换
	frame_size_sws_ctx = sws_getContext(codec_ctx->width, codec_ctx->height, codec_ctx->pix_fmt, 480, 480, codec_ctx->pix_fmt, SWS_BILINEAR, NULL, NULL, NULL);
	if (frame_size_sws_ctx == NULL)
	{
		video_decode_release();
		return RESULT_ERROR;
	}

	(*env)->ReleaseStringUTFChars(env, videoFilePath, video_filename);
	return RESULT_OK;
}

// 释放资源
void video_decode_release()
{
	if (codec_ctx != NULL)
	{
		avcodec_close(codec_ctx);
	}

	if (fmt_ctx != NULL)
	{
		avformat_close_input(&fmt_ctx);
	}

	if (frame != NULL)
	{
		av_frame_free(&frame);
	}

	if (video_dst_data != NULL)
	{
		av_freep(&video_dst_data[0]);
	}

	if (frame_size_sws_ctx != NULL)
	{
		sws_freeContext(frame_size_sws_ctx);
	}

	frame = NULL;
	codec = NULL;
	codec_ctx = NULL;
	fmt_ctx = NULL;
	frame_size_sws_ctx = NULL;
	video_dst_data[0] = NULL;
	video_dst_data[1] = NULL;
	video_dst_data[2] = NULL;
	video_dst_data[3] = NULL;
	video_dst_linesize[0] = 0;
	video_dst_linesize[1] = 0;
	video_dst_linesize[2] = 0;
	video_dst_linesize[3] = 0;
	video_dst_bufsize = 0;
	ret = 0;
	got_frame = 0;
	frame_index = 0;
	time_per_frame = 0.0;
}

JNIEXPORT jint JNICALL Java_com_xiuxiu_util_AVProcessing_uninitVideoDecode(JNIEnv *env, jclass obj)
{
	video_decode_release();
	return RESULT_OK;
}

// 解码单帧图像
static int decodeVideo2Frame(jbyte* pDstY, jbyte* pDstU, jbyte* pDstV, jint length)
{
	// 解码
	int qtrLen = length >> 2;
	av_init_packet(&pkt);
	pkt.data = NULL;
	pkt.size = 0;
	while (av_read_frame(fmt_ctx, &pkt) >= 0)
	{
		if (pkt.stream_index == 0)
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
					if (codec_ctx->width == 480 && codec_ctx->height == 480)
					{
						// 直接拷贝
						av_image_copy(
							video_dst_data, video_dst_linesize,
							(const uint8_t **)frame->data, frame->linesize,
							codec_ctx->pix_fmt, codec_ctx->width, codec_ctx->height);
					}
					else
					{
						// 尺寸转换
						sws_scale(
							frame_size_sws_ctx,
							(const uint8_t *const*)frame->data, frame->linesize, 0, frame->height,
							video_dst_data, video_dst_linesize);
					}

					memcpy(pDstY, video_dst_data[0], length);
					memcpy(pDstU, video_dst_data[0] + length, qtrLen);
					memcpy(pDstV, video_dst_data[0] + length + qtrLen, qtrLen);
					av_frame_unref(frame);
				}

				pkt.data += ret;
				pkt.size -= ret;
			} while (pkt.size > 0);

			av_free_packet(&pkt);
			break;
		}

		av_free_packet(&pkt);
	}// while

	return got_frame;
}

// 视频解码为视频帧
JNIEXPORT jdouble JNICALL Java_com_xiuxiu_util_AVProcessing_decodeVideo2Frame(JNIEnv *env, jclass obj, jbyteArray dst_y, jbyteArray dst_u, jbyteArray dst_v, jint length)
{
	jbyte* pDstY = (*env)->GetByteArrayElements(env, dst_y, 0);
	jbyte* pDstU = (*env)->GetByteArrayElements(env, dst_u, 0);
	jbyte* pDstV = (*env)->GetByteArrayElements(env, dst_v, 0);

	int got_ret = 0;
	do
	{
		got_ret = decodeVideo2Frame(pDstY, pDstU, pDstV, length);
	}
	while (got_ret != 1);

	(*env)->ReleaseByteArrayElements(env, dst_y, pDstY, 0);
	(*env)->ReleaseByteArrayElements(env, dst_u, pDstU, 0);
	(*env)->ReleaseByteArrayElements(env, dst_v, pDstV, 0);

	frame_index++;
	return frame_index * time_per_frame;
}

