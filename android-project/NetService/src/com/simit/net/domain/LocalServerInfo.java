package com.simit.net.domain;


/*
 * 用户基本信息
 */

public class LocalServerInfo {
	private RegisterType	registerType;
	private NetType			netType;
	private int				serverId;
	private String			serverName;	//用户备注名
	private String			ipAddr;
	
	public LocalServerInfo(){

	}
	
	public LocalServerInfo(RegisterType registerType, NetType netType, int serverId, String serverName, String ipAddr){
		this.registerType 	= registerType;
		this.netType 		= netType;
		this.serverId 		= serverId;
		this.serverName 	= serverName;
		this.ipAddr 		= ipAddr;
	}
	
	public void setUserInfoRegisterType(RegisterType registerType){
		this.registerType = registerType;
		
	}
	
	public void setUserInfoNetType(NetType netType){
		this.netType = netType;
	}
	
	public void SetUserInfoUserId(int userId){
		this.serverId = userId;
	}
	
	public void SetUserInfoUserName(String userName){
		this.serverName = userName;
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
		return serverId;
	}
	
	public String GetUserInfoUserName(){
		return serverName;
	}
	
	public String GetUserInfoIpAddr(){
		return ipAddr;
	}
}
