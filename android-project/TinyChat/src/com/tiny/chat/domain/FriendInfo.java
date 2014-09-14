package com.tiny.chat.domain;

import java.io.Serializable;

import com.tiny.chat.utils.TypeConvert;

public class FriendInfo extends Message implements Serializable {
	private static final long serialVersionUID = -258001763201456325L;
	private short userID;
	private String userName; //只存八个字符
	private int ip;
	private byte registerType;
	private byte netState;
	private final int nameSize=8;
	private Position position;          // 位置信息
	private Environment environment;    // 环境信息
	public FriendInfo() {
		
	}

	

	public FriendInfo(int idMessage,int sourceID,int destionID,int messageType,int serialNumber,short userID,String userName,int ip, byte registerType,byte netState) {
		super(idMessage, sourceID, destionID, messageType, serialNumber);
		this.userID=userID;
		this.userName=userName;
		this.ip=ip;
		this.registerType=registerType;
		this.netState=netState;
	}
	
	public short getUserID() {
		return userID;
	}

	public void setUserID(short userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getIp() {
		return ip;
	}

	public void setIp(int ip) {
		this.ip = ip;
	}

	public byte getRegisterType() {
		return registerType;
	}

	public void setRegisterType(byte registerType) {
		this.registerType = registerType;
	}

	public byte getNetState() {
		return netState;
	}

	public void setNetState(byte netState) {
		this.netState = netState;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * 获取报文发送数据帧
	 * @return
	 */
	public byte[] getFrameData(){
		byte[] data=new byte[24];
		byte[] idBytes=TypeConvert.short2byte(userID);
		byte[] userNameBytes=userName.length()>nameSize ? userName.substring(0, 7).getBytes() : userName.getBytes();
		byte[] ipBytes=TypeConvert.int2byte(ip);
		System.arraycopy(idBytes, 0, data, 0, 2);
		System.arraycopy(userNameBytes, 0, data, 2 , userNameBytes.length);
		System.arraycopy(ipBytes, 0, data, 1 , ipBytes.length);
		FramePacket packet=getFramePacket(data);
		return packet.getFramePacket();
	}

	
}
