package com.tiny.chat.domain;

import com.simit.net.utils.IPv4Util;

public class OnlineDevice {

	private byte type; //0 未知用户 1 超级管理员用户 2 普通管理员 3 普通客户端用户 4 服务器用户
	private short userId;
	private String password;
	private short deviceId;
	private String ip;
	
	private boolean online;
	private byte latitudeDegree;            //纬度_度
	private byte latitudeMinute;            //纬度_分
	private float latitudeSecond;           //纬度_秒

	private byte longitudeDegree;           //经度_度
	private byte longitudeMinute;           //经度_分
	private float longitudeSecond;          //经度_秒
	
	
	public OnlineDevice() {
		
	}

	public OnlineDevice(byte[] frame) {
		if(frame.length==30){
			this.type=frame[0];
			this.userId=(short)(((frame[1] & 0xFF)<<8) | frame[2]);
			this.password=new String(frame, 3, 8);
			this.deviceId=(short)(((frame[11] & 0xFF)<<8) | frame[12]);
			byte[] ipBytes=new byte[4];
			System.arraycopy(frame, 13, ipBytes, 0, 4);
			this.ip=IPv4Util.bytesToIp(ipBytes);
			this.latitudeDegree=frame[17];
			this.latitudeMinute=frame[18];
			this.latitudeSecond= 0.0f;
			this.longitudeDegree=frame[23];
			this.longitudeMinute=frame[24];
			this.longitudeSecond= 0.0f;
			if(frame[29]==0){
				this.online=false;
			}else{
				this.online=true;
			}
		}
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public short getUserId() {
		return userId;
	}

	public void setUserId(short userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public short getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(short deviceId) {
		this.deviceId = deviceId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public byte getLatitudeDegree() {
		return latitudeDegree;
	}

	public void setLatitudeDegree(byte latitudeDegree) {
		this.latitudeDegree = latitudeDegree;
	}

	public byte getLatitudeMinute() {
		return latitudeMinute;
	}

	public void setLatitudeMinute(byte latitudeMinute) {
		this.latitudeMinute = latitudeMinute;
	}

	public float getLatitudeSecond() {
		return latitudeSecond;
	}

	public void setLatitudeSecond(float latitudeSecond) {
		this.latitudeSecond = latitudeSecond;
	}

	public byte getLongitudeDegree() {
		return longitudeDegree;
	}

	public void setLongitudeDegree(byte longitudeDegree) {
		this.longitudeDegree = longitudeDegree;
	}

	public byte getLongitudeMinute() {
		return longitudeMinute;
	}

	public void setLongitudeMinute(byte longitudeMinute) {
		this.longitudeMinute = longitudeMinute;
	}

	public float getLongitudeSecond() {
		return longitudeSecond;
	}

	public void setLongitudeSecond(float longitudeSecond) {
		this.longitudeSecond = longitudeSecond;
	}
	
	
}
