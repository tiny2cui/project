package com.simit.net.domain;

import com.simit.net.domain.NetType;

/**
 * 本地属性类
 * @author wen.cui
 */
public class LocalProperty {

//	private NetService		netService;
	private int				deveiceId;
	private byte			supportTypes;
	private String			name;
	private NetType			netType;
	private String			ipAddr;
	private String          password;
	private int          userId;
	private int loginServerState=-1;
	private Location location;

	public LocalProperty(){
    	name	= "Control Center";
    	//ipAddr=IPv4Util.getLocalIpAddress();
    	this.deveiceId=7;
    	this.password="12345678";
    	this.userId=7;
    	this.location=new Location(0, 0, 0);
		
	}
	
	public byte[] getPassword() {
		return password.getBytes();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalProperty(int id, String name){
		this.deveiceId=id;
		this.name = name;
	}
	
	public void setDeveiceId(int id){
		this.deveiceId = id;
	}
	
	public int getDeveiceId(){
		return deveiceId;
	}
	
	public void setSupportTypes(byte supportTypes){
		this.supportTypes = supportTypes;
	}
	
	public byte getSupportTypes(){
		return supportTypes;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setNetType(NetType netType){
		this.netType = netType;
	}
	
	public NetType getNetType(){
		return netType;
	}
	
	public void setIpAddress(String ipAddr){
		this.ipAddr = ipAddr;
	}
	
	public String getIpAddress(){
		return ipAddr;
	}
	public void setLoginServerState(int loginServerState) {
		this.loginServerState = loginServerState;
	}
	
}
