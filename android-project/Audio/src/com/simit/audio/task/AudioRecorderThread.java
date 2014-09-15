package com.simit.audio.task;


import com.simit.audio.config.AudioConfig;
import com.simit.audio.util.MyLog;
import com.simit.audio.util.SendAndReceive;
import com.simit.audio.util.Tags;
import com.speex.Speex;
import android.media.AudioRecord;

public class AudioRecorderThread implements Runnable {

	private String TAG="AudioRecorderThread";
	private static AudioRecorderThread recorder;

	/**
	 * 音频录制
	 */
	private AudioRecord audioRecord;
	private int bufferSizeInBytes;

	/**
	 * 存放数据缓冲区
	 */
	private short[] audioData;
	private int sizeInBytes;
	private int bufferRead;

	private boolean isRecording = false;
	
	/**
	 * 编码器
	 */
	private AudioEncoderThread encoder;

	public static AudioRecorderThread getInstance() {
		if (recorder == null) {
			recorder = new AudioRecorderThread();
		}
		return recorder;
	}

	private AudioRecorderThread() {
		MyLog.d(Tags.AudioDemo.toString(), "AudioRecorder = " + "开启录制器");
		initAudioRecord();
	}

	/**
	 * 初始化参数
	 * 
	 * @return
	 */
	private boolean initAudioRecord() {
		bufferSizeInBytes = AudioRecord.getMinBufferSize(
				AudioConfig.sampleRateInHz, AudioConfig.channelConfig_in,
				AudioConfig.audioFormat);
		
		{ // 获取每一帧的大小，录制音频每次录制一帧的大小
			Speex.open(Speex.DEFAULT_COMPRESSION, 0);
			sizeInBytes = Speex.getFrameSize();
			Speex.close();
		}

		MyLog.e(Tags.AudioDemo.toString(), "AudioRecord getMinBufferSize = "
				+ bufferSizeInBytes);
		if (bufferSizeInBytes == AudioRecord.ERROR
				|| bufferSizeInBytes == AudioRecord.ERROR_BAD_VALUE
				|| bufferSizeInBytes == AudioRecord.ERROR_INVALID_OPERATION) {
			MyLog.e(Tags.AudioDemo.toString(), "AudioRecord error = "
					+ bufferSizeInBytes);
			return false;
		}

		// 初始化缓冲区
		audioData = new short[bufferSizeInBytes];
		// 初始化AudioRecord
		if (null == audioRecord) {
			audioRecord = new AudioRecord(AudioConfig.audioSource,
					AudioConfig.sampleRateInHz, AudioConfig.channelConfig_in,
					AudioConfig.audioFormat, bufferSizeInBytes);
		}
		return true;
	}

	/**
	 * 开始录制
	 */
	public void startRecording(SendAndReceive send) {
		if (this.isRecording) {
			return;
		}
		if (null == audioRecord) {
			MyLog.e(Tags.AudioDemo.toString(), "AudioRecorder error = "
					+ "initialized recorder error!");
			return;
		}

		// 启动编码器
		encoder = AudioEncoderThread.getInstance();
		encoder.setSend(send);
		encoder.startEncoding();

		// 开始音频录制
		MyLog.d(Tags.AudioDemo.toString(), "AudioRecord = " + "开始录制");
		audioRecord.startRecording();
		
		this.isRecording = true;
		new Thread(this).start();
	}

	/**
	 * 停止录制
	 */
	public void stopRecording() {
		// 停止录制
		this.isRecording = false;
		MyLog.d(Tags.AudioDemo.toString(), "AudioRecord = " + "停止录制");
		if(audioRecord != null) {
			audioRecord.stop();
		}
		
		// 停止编码
		encoder.stopEncoding();
		encoder.release();
	}
	/**
	 * 释放资源
	 */
	public void release() {
		if (audioRecord != null) {
			audioRecord.release();
			audioRecord = null;
		}
	}
	
	public boolean isRecording() {
		return this.isRecording;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRecording) {
			MyLog.d(Tags.Threader.toString(), "AudioRecord = " + "录制ing");
			bufferRead = audioRecord.read(audioData, 0, sizeInBytes);
			
			MyLog.d(Tags.Threader.toString(), "AudioRecord bufferRead录制大小 = "	+ bufferRead);
			
			if (bufferRead > 0) {
				// 将数据加入编码器
				encoder.addData(audioData, bufferRead);
			} else if (bufferRead == AudioRecord.ERROR
					|| bufferRead == AudioRecord.ERROR_BAD_VALUE
					|| bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				//TODO录制失败
				MyLog.e(TAG, "录制失败");
			}
			
			/*isRecording = false;*/
		}

		/*// 停止录制
		MyLog.d(Tags.AudioDemo.toString(), "AudioRecord = " + "停止录制");
		audioRecord.stop();
		// 停止编码
		encoder.stopEncoding();*/
	}
}
