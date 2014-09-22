package com.tiny.chat.domain;

public class SendData{
	private byte[] data;
	private int len;
	private String destinationIP;
	private int destinationPort;
	public SendData() {
		
	}
	public SendData(byte[] data ,int len) {
		this.data=data;
		this.len=len;
	}
	public SendData(byte[] data ,int len,String ip,int port) {
		this.data=data;
		this.len=len;
		this.destinationIP=ip;
		this.destinationPort=port;
		
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
	public int getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}
	
	
}
