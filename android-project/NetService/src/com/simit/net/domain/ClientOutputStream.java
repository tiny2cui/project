package com.simit.net.domain;

import java.io.OutputStream;
/*
 * 将client type与socket对应关系存储
 */
public class ClientOutputStream {
	private OutputStream	outputStream;
	private byte			clientType;
	
	public ClientOutputStream(OutputStream outputStream, byte clientType){
		this.outputStream = outputStream;
		this.clientType = clientType;
	}
	
	public byte getClientType(){
		return clientType;
	}
	
	public OutputStream GetOutpuStream(){
		return outputStream;
	}
}
