package com.simit.net.domain;

/**
 * 
 * @author wen.cui
 *
 */

public class Client {
	private byte clientType;
	private int clientId;
	private int serverId;
	private String ip;
	private int port;

	public Client(byte clientType, int clientId, int serverId,String ip,int port) {
		this.clientType = clientType;
		this.clientId=clientId;
		this.serverId=serverId;
		this.ip=ip;
		this.port=port;
	}
	
	public byte getClientType() {
		return clientType;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public void setClientType(byte clientType) {
		this.clientType = clientType;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
