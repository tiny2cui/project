#include <jni.h>

#include <string.h>
#include <unistd.h>

#include <speex/speex.h>
#include "speex/speex_preprocess.h"
#include "speex/speex_echo.h"

static int codec_open = 0;

static int dec_frame_size;
static int enc_frame_size;

static SpeexBits ebits, dbits;
void *enc_state;
void *dec_state;

static JavaVM *gJavaVM;

extern "C"
JNIEXPORT jint JNICALL Java_com_speex_Speex_open(JNIEnv *env,
		jobject obj, jint compression, jint speexModeId) {
	int tmp;

	if (codec_open++ != 0)
	return (jint) 0;

	// default 5
	if (compression < 1 || compression > 10) {
		compression = 5;
	}

	{ // 编码参数设置init de ebits et encoder
		speex_bits_init(&ebits);
		// 在xx窄宽模式下的编码状态
		enc_state = speex_encoder_init(speex_lib_get_mode(speexModeId));
		tmp = compression;
		// 设定编码的质量
		speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &tmp);
		tmp = 3;
		speex_encoder_ctl(enc_state, SPEEX_SET_COMPLEXITY, &tmp);
		tmp = 1;
		speex_encoder_ctl(enc_state, SPEEX_SET_HIGHPASS, &tmp);
		// 获取编码的帧大小
		speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &enc_frame_size);
	}

	{ // 解码参数设置 init de dbits et decoder
		speex_bits_init(&dbits);
		// 在xx窄宽模式下的编码状态
		dec_state = speex_decoder_init(speex_lib_get_mode(speexModeId));
		tmp = 1;
		speex_decoder_ctl(dec_state, SPEEX_SET_ENH, &tmp);
		tmp = 1;
		speex_decoder_ctl(dec_state, SPEEX_SET_HIGHPASS, &tmp);
		// 设定解码的帧大小
		speex_decoder_ctl(dec_state, SPEEX_GET_FRAME_SIZE, &dec_frame_size);
	}

	{ // get delay time during coding and decoding
		int skip_group_delay = 0;
		speex_encoder_ctl(enc_state, SPEEX_GET_LOOKAHEAD, &skip_group_delay);
		speex_decoder_ctl(dec_state, SPEEX_GET_LOOKAHEAD, &tmp);
		skip_group_delay += tmp;
	}

	// TODO
	// 预处理初始化
	SpeexPreprocessState *m_st;
	SpeexEchoState *echo_state;
	m_st = speex_preprocess_state_init(enc_frame_size, 8000);
	echo_state = speex_echo_state_init(enc_frame_size, 8000);

	int denoise = 1;
	int noiseSuppress = -25;
	{ // 降噪
		speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_DENOISE, &denoise);
		speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_NOISE_SUPPRESS, &noiseSuppress);
	}

	int agc = 1;
    float level = 24000;
	{ // 增益
    	speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_AGC, &agc);
    	speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_AGC_LEVEL, &level);
	}

    int vad = 1;
    int vadProbStart = 80;
    int vadProbContinue = 65;
	{// 静音检测
    	speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_VAD, &vad);
    	speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_PROB_START , &vadProbStart);
    	speex_preprocess_ctl(m_st, SPEEX_PREPROCESS_SET_PROB_CONTINUE, &vadProbContinue);
	}

	return (jint) 0;
}

extern "C"
JNIEXPORT jint Java_com_speex_Speex_encode(JNIEnv *env, jobject obj,
		jshortArray lin, jint offset, jbyteArray encoded, jint size) {

	jshort buffer[enc_frame_size];
	jbyte output_buffer[size];
	int nsamples = (size - 1) / enc_frame_size + 1;
	int i, tot_bytes = 0;

	if (!codec_open)
	return 0;

	speex_bits_reset(&ebits);

	// 把数据写入buffer
	for (i = 0; i < nsamples; i++) {
		// TODO
		if ((offset + i * enc_frame_size + enc_frame_size) > size) {
			env->GetShortArrayRegion(lin, offset + i * enc_frame_size,
					size - (offset + i * enc_frame_size), buffer);
		} else {
			env->GetShortArrayRegion(lin, offset + i * enc_frame_size,
					enc_frame_size, buffer);
		}
		env->GetShortArrayRegion(lin, offset + i * enc_frame_size,
							enc_frame_size, buffer);
		// 把buffer数据写入ebits
		speex_encode_int(enc_state, buffer, &ebits);
	}

	/*env->GetShortArrayRegion(lin, offset, enc_frame_size, buffer);
	speex_encode_int(enc_state, buffer, &ebits);*/

	// 把ebits数据写入output_buffer
	tot_bytes = speex_bits_write(&ebits, (char *) output_buffer, size);
	// 统计编码的大小
	env->SetByteArrayRegion(encoded, 0, tot_bytes, output_buffer);

	return (jint) tot_bytes;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_speex_Speex_decode(JNIEnv *env,
		jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

	jbyte buffer[dec_frame_size];
	jshort output_buffer[size];
	jsize encoded_length = size;
	int nsamples = (size - 1) / dec_frame_size + 1;
    int i, offset = 0, tot_shorts = 0;

	if (!codec_open)
	return 0;

	speex_bits_reset(&dbits);

	// 把数据写入buffer
    for(i = 0; i < nsamples; i++){
        if(offset + i * dec_frame_size + dec_frame_size > size) {
        	env->GetByteArrayRegion(encoded, offset + i * dec_frame_size,
        			size - (offset + i * dec_frame_size), buffer);
        	speex_bits_read_from(&dbits, (char *) buffer, size - (offset + i * dec_frame_size));
        } else {
        	env->GetByteArrayRegion(encoded, offset + i * dec_frame_size,
        			dec_frame_size, buffer);
        	speex_bits_read_from(&dbits, (char *) buffer, dec_frame_size);
        }
    }
//	env->GetByteArrayRegion(encoded, 0, encoded_length, buffer);
//	// buffer数据写入dbits
//	speex_bits_read_fdrom(&dbits, (char *) buffer, encoded_length);

    // dbits数据写入output_buffer
	speex_decode_int(dec_state, &dbits, output_buffer);
	// TODO
	env->SetShortArrayRegion(lin, 0, encoded_length, output_buffer);
	/*env->SetShortArrayRegion(lin, 0, dec_frame_size, output_buffer);*/

	/*return (jint) dec_frame_size;*/
	/*return (jint) encoded_length;*/
	return (jint) encoded_length;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_speex_Speex_getFrameSize(JNIEnv *env,
		jobject obj) {

	if (!codec_open)
	return 0;
	return (jint) enc_frame_size;

}

extern "C"
JNIEXPORT void JNICALL Java_com_speex_Speex_close
(JNIEnv *env, jobject obj) {

	if (--codec_open != 0)
	return;

	speex_bits_destroy(&ebits);
	speex_bits_destroy(&dbits);
	speex_decoder_destroy(dec_state);
	speex_encoder_destroy(enc_state);
}
