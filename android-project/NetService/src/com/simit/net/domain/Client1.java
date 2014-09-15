package com.simit.net.domain;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


/**
 * 
 * @author wen.cui
 *
 */
public class Client1 {
	private OutputStream outputStream;
	private byte clientType;
	private int clientId;
	private int serverId;
	private Socket socket;
	private String ip;

	public Client1(OutputStream outputStream, byte clientType, int clientId, int serverId) {
		this.outputStream = outputStream;
		this.clientType = clientType;
		this.clientId=clientId;
		this.serverId=serverId;
	}
	public Client1(byte clientType, int clientId, int serverId,Socket socket) {
		this.clientType = clientType;
		this.clientId=clientId;
		this.serverId=serverId;
		this.socket=socket;
	}
	public Client1(byte clientType, int clientId, int serverId,String ip) {
		this.clientType = clientType;
		this.clientId=clientId;
		this.serverId=serverId;
		this.ip=ip;
	}
	
	public byte getClientType() {
		return clientType;
	}

	public OutputStream getClientOutpuStream() {
		if(socket!=null){
			try {
				OutputStream outputStream=socket.getOutputStream();
				if(outputStream!=null){
					return outputStream;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public OutputStream getOutputStream() {
		return outputStream;
	}


	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
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


	public Socket getSocket() {
		return socket;
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	
}
