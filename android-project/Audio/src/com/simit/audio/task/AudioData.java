package com.simit.audio.task;

public class AudioData {
	/**
	 * 数据大小
	 */
	private int size;
	/**
	 * 类型区分
	 */
	private short[] shortData;
	private byte[] byteData;
	/**
	 * 时间戳
	 */
	private long timestamp;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public short[] getShortData() {
		return shortData;
	}

	public void setShortData(short[] shortData) {
		this.shortData = shortData;
	}

	public byte[] getByteData() {
		return byteData;
	}

	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
