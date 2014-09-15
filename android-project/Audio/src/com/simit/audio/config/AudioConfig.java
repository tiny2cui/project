package com.simit.audio.config;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class AudioConfig {

	/**
	 * 音频输入（采集）
	 */
	public static final int channelConfig_in = AudioFormat.CHANNEL_IN_MONO;
	
	/**
	 * 音频输出（播放）
	 */
	public static final int channelConfig_out = AudioFormat.CHANNEL_OUT_MONO;
	
	/**
	 * 采集音频样率
	 */
	public static final int sampleRateInHz = 8000;
	
	/**
	 * 采集音频的编码格式
	 */
	public static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

	/**
	 * 采集音频源
	 */
	public static final int audioSource = MediaRecorder.AudioSource.MIC;

	/**
	 * 音频播放流类型
	 */
	public static final int streamType = AudioManager.STREAM_MUSIC;
	
	/**
	 * 音频播放模式
	 */
	public static final int mode = AudioTrack.MODE_STREAM;
	
	/**
	 * 收发音频数据包大小：定为音频每一帧的大小
	 */
	public static final int packetSize = 160;
	
	public static final int packageFrameSize=10;
	
	
}
