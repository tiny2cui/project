package com.tiny.chat.domain;

public class SendData{
	private byte[] data;
	private int len;
	private String destinationIP;
	public SendData() {
		
	}
	public SendData(byte[] data ,int len) {
		this.data=data;
		this.len=len;
	}
	public SendData(byte[] data ,int len,String ip) {
		this.data=data;
		this.len=len;
		this.destinationIP=ip;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public String getDestinationIP() {
		return destinationIP;
	}
	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}
	
}
