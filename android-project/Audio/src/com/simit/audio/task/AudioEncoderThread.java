package com.simit.audio.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import com.simit.audio.config.AudioConfig;
import com.simit.audio.util.MyLog;
import com.simit.audio.util.Tags;
import com.simit.audio.util.FrameType;
import com.simit.audio.util.SendAndReceive;
import com.speex.Speex;

public class AudioEncoderThread implements Runnable {

	private String TAG = "AudioEncoderThread";
	private static AudioEncoderThread encoder;
	private int frameSize; // 编码帧大小
	private List<AudioData> dataList = null; // 编码原始数据
	private AudioData rawData;
	private byte[] encodedData;  //编码后的数据
	private int encodeSize;
	private boolean isEncoding = false;
	private SendAndReceive send;
	private byte[] tempData;
	private Semaphore sync;

	
	public static AudioEncoderThread getInstance() {
		if (encoder == null) {
			encoder = new AudioEncoderThread();
		}
		return encoder;
	}

	private AudioEncoderThread() {
		MyLog.i(Tags.AudioDemo.toString(), "AudioEncoder = " + "开启编码器");
		Speex.open(8, 0);
		frameSize = Speex.getFrameSize();
		dataList = Collections.synchronizedList(new LinkedList<AudioData>());
	}

	public void addData(short[] data, int size) {
		AudioData rawData = new AudioData();
		short[] tempData = new short[size];
		System.arraycopy(data, 0, tempData, 0, size);
		rawData.setShortData(tempData);
		rawData.setSize(size);
		rawData.setTimestamp(System.currentTimeMillis());
		dataList.add(rawData);
		sync.release();
		MyLog.i(Tags.AudioDemo.toString(), "AudioEncoder dataList size = " + dataList.size() + " tempData size = " + tempData.length);
	}

	public void startEncoding() {
		if (isEncoding) {
			return;
		}
		if (encoder == null) {
			MyLog.i(Tags.AudioDemo.toString(), "AudioEncoder error = " + "initial encoder error!");
		}
		
		// 开始编码
		MyLog.i(Tags.AudioDemo.toString(), "AudioEncoder = " + "开始编码");
		isEncoding = true;
		sync=new Semaphore(0);
		new Thread(this).start();
	}

	public void stopEncoding() {
		// 停止编码
		this.isEncoding = false;
		MyLog.i(Tags.AudioDemo.toString(), "AudioEncoder = " + "停止编码");
	}

	/**
	 * 释放资源
	 */
	public void release() {
		Speex.close();
		encoder = null;
	}

	public boolean isEncoding() {
		return this.isEncoding;
	}

	public void run() {
		while (isEncoding) {
			try {
				sync.acquire(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dataList.size() > AudioConfig.packageFrameSize) {
				MyLog.i(Tags.Threader.toString(), "AudioEncoder = " + "编码ing");

				for (int i = 0; i < AudioConfig.packageFrameSize; i++) {
					rawData = dataList.remove(0);
					if (encodedData == null
							|| encodedData.length != rawData.getSize()) {
						encodedData = new byte[rawData.getSize()];
					}
					encodeSize = Speex.encode(rawData.getShortData(), 0,
							encodedData, rawData.getSize());
					if (tempData == null
							|| tempData.length != encodeSize
									* AudioConfig.packageFrameSize) {
						tempData = new byte[encodeSize
								* AudioConfig.packageFrameSize];
					}
					System.arraycopy(encodedData, 0, tempData, encodeSize * i,
							encodeSize);
				}
				if (encodeSize > 0) {
					send.sendData(tempData, FrameType.MESSAGE_AUDIO_DATA);
				}
			}
		}
		
	}

	public void setSend(SendAndReceive send) {
		this.send = send;
	}

}
