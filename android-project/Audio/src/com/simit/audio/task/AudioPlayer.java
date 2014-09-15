package com.simit.audio.task;


import com.simit.audio.config.AudioConfig;
import com.simit.audio.util.Tags;
import android.media.AudioTrack;
import android.util.Log;

public class AudioPlayer {

	private static AudioPlayer player;
	/**
	 * 音频播放
	 */
	private AudioTrack audioTrack;
	private int bufferSizeInBytes;

	private boolean isPlaying = false;

	private AudioPlayer() {
		Log.d(Tags.AudioDemo.toString(), "AudioPlayer = " + "开启播放器");
		initAudioTrack();
	}

	public static AudioPlayer getInstance() {
		if (player == null) {
			player = new AudioPlayer();
		}
		return player;
	}

	public void addData(short[] rawData, int size) {
		if (isPlaying) {
			if(isPlaying && audioTrack != null) {
				audioTrack.write(rawData, 0, size);
			}
		}
	}

	/**
	 * 初始化AudioTrack
	 * 
	 * @return
	 */
	private boolean initAudioTrack() {
		bufferSizeInBytes = AudioTrack.getMinBufferSize(AudioConfig.sampleRateInHz,
				AudioConfig.channelConfig_out, AudioConfig.audioFormat);
		Log.e(Tags.AudioDemo.toString(), "AudioPlayer getMinBufferSize = "
				+ bufferSizeInBytes);
		if (bufferSizeInBytes == AudioTrack.ERROR
				|| bufferSizeInBytes == AudioTrack.ERROR_BAD_VALUE
				|| bufferSizeInBytes == AudioTrack.ERROR_INVALID_OPERATION) {
			Log.e(Tags.AudioDemo.toString(), "AudioPlayer error = "
					+ bufferSizeInBytes);
			return false;
		}
		// 初始化AudioTrack
		audioTrack = new AudioTrack(AudioConfig.streamType, AudioConfig.sampleRateInHz, AudioConfig.channelConfig_out,
				AudioConfig.audioFormat, bufferSizeInBytes, AudioConfig.mode);
		// 设置播放音量
		audioTrack.setStereoVolume(1.0f, 1.0f);
		return true;
	}

	public void startPlaying() {
		if (isPlaying) {
			return;
		}
		if (null == audioTrack) {
			Log.e(Tags.AudioDemo.toString(), "AudioPlayer error = "
					+ "initialized player error!");
			return;
		}

		// 开始播放
		Log.d(Tags.AudioDemo.toString(), "AudioPlayer = " + "开始播放");
		audioTrack.play();
		this.isPlaying = true;
	}

	public void stopPlaying() {
		// 停止播放
		this.isPlaying = false;
		Log.d(Tags.AudioDemo.toString(), "AudioPlayer = " + "停止播放");
		if(audioTrack != null) {
			if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
		}
	}

	public void release() {
		if(audioTrack != null) {
			/*audioTrack.release();*/
		}
	}
	
	public boolean isPlaying() {
		return this.isPlaying;
	}
}
