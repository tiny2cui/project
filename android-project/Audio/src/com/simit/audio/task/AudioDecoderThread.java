package com.simit.audio.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.simit.audio.config.AudioConfig;
import com.simit.audio.util.MyLog;
import com.simit.audio.util.Tags;
import com.speex.Speex;

public class AudioDecoderThread implements Runnable {

	private static AudioDecoderThread decoder;

	/**
	 * 帧大小
	 */
	private int frameSize;
	/**
	 * 解码前数据
	 */
	private List<AudioData> dataList = null;
	private AudioData rawData;
	/**
	 * 解码后的数据
	 */
	private short[] decodedData = new short[AudioConfig.packetSize];
	private int decodeSize;

	private boolean isDecoding = false;
	
	private byte[] tempData=new byte[AudioConfig.packetSize];
	
	/**
	 * 播放器
	 */
	private AudioPlayer player;

	private Semaphore sync;
	public static AudioDecoderThread getInstance() {
		if (decoder == null) {
			decoder = new AudioDecoderThread();
		}
		return decoder;
	}

	private AudioDecoderThread() {
		MyLog.d(Tags.AudioDemo.toString(), "AudioDecoder = " + "开启解码器");
		Speex.open(Speex.DEFAULT_COMPRESSION, 0);
		frameSize = Speex.getFrameSize();
		dataList = Collections.synchronizedList(new LinkedList<AudioData>());
	}

	public void addData(byte[] data, int size) {
		if(sync!=null){
			AudioData adata = new AudioData();
			byte[] tempData = new byte[size];
			System.arraycopy(data, 0, tempData, 0, size);
			adata.setByteData(tempData);
			adata.setSize(size);
			dataList.add(adata);
			sync.release();
			MyLog.d(Tags.AudioDemo.toString(), "AudioDecoder dataList size = " + dataList.size() + " tempData size = " + tempData.length);
		}
	}

	public void startDecoding() {
		if (isDecoding) {
			return;
		}
		if (decoder == null) {
			MyLog.d(Tags.AudioDemo.toString(), "AudioDecoder error = " + "initial decoder error!");
		}

		// 启动播放器
		player = AudioPlayer.getInstance();
		player.startPlaying();

		// 开始解码
		MyLog.d(Tags.AudioDemo.toString(), "AudioDecoder = " + "开始解码");
		this.isDecoding = true;
		sync=new Semaphore(0);
		new Thread(this).start();
	}

	public void stopDecoding() {
		// 停止解码
		this.isDecoding = false;
		MyLog.d(Tags.AudioDemo.toString(), "AudioDecoder = " + "停止解码");
		
		// 停止播放
		if(player!=null){
			player.stopPlaying();
			player.release();
		}
		
	}
	
	public void release() {
		Speex.close();
		decoder = null;
	}
	 
	public boolean isDecoding() {
		return this.isDecoding;
	}

	public void run() {

		while (isDecoding) {			
			try {
				sync.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			if (dataList.size() > 0) {
				MyLog.d(Tags.Threader.toString(), "AudioDecoder = " + "解码ing");
				rawData = dataList.remove(0);
				int size=rawData.getSize()/AudioConfig.packageFrameSize;
				for(int i=0;i<AudioConfig.packageFrameSize;i++){
					System.arraycopy(rawData.getByteData(), i * size, tempData, 0, size);
					decodeSize = Speex.decode(tempData, decodedData,tempData.length);
					if (decodeSize > 0) {
						// 把数据加入播放器
						player.addData(decodedData, decodedData.length);
					}					
				}
			}
			
		}
		/*// 停止播放
		player.stopPlaying();*/
	}

}