package com.simit.net.domain;

/**
 * 友邻ID与IP对应关系
 * @author admin
 *
 */
public class Friend {
	
	private int			id;
	private String		ipAddr;
	
	public Friend(int id, String ipAddr){
		this.id = id;
		this.ipAddr = ipAddr;
	}
	
	public int getId(){
		return id;
	}
	
	public String getIp(){
		return ipAddr;
	}
	
}
