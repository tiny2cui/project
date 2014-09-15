package com.simit.net.domain;


/*
 * 用户基本信息
 */

public class UserInfo {
	private RegisterType	registerType;
	private NetType			netType;
	private int				userId;
	private String			userName;	//用户备注名
	private String			ipAddr;
	
	public UserInfo(){

	}
	
	public UserInfo(RegisterType registerType, NetType netType, int userId, String userName, String ipAddr){
		this.registerType 	= registerType;
		this.netType 		= netType;
		this.userId 		= userId;
		this.userName 		= userName;
		this.ipAddr 		= ipAddr;
	}
	
	public void setUserInfoRegisterType(RegisterType registerType){
		this.registerType = registerType;
		
	}
	
	public void setUserInfoNetType(NetType netType){
		this.netType = netType;
	}
	
	public void SetUserInfoUserId(int userId){
		this.userId = userId;
	}
	
	public void SetUserInfoUserName(String userName){
		this.userName = userName;
	}
	
	public void SetUserInfoIpAddr(String ipAddr){
		this.ipAddr = ipAddr;
	}
	
	public RegisterType GetUserInfoRegisterType(){
		return registerType;
	}
	
	public NetType GetUserInfoNetType(){
		return netType;
	}
	
	public int GetUserInfoUserId(){
		return userId;
	}
	
	public String GetUserInfoUserName(){
		return userName;
	}
	
	public String GetUserInfoIpAddr(){
		return ipAddr;
	}
}
