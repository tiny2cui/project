package com.simit.video.server;

import java.io.IOException;
import java.net.InetAddress;

import com.simit.video.test.SendAndReceive;

public interface Stream {

	public void start() throws IllegalStateException;
	public void prepare() throws IllegalStateException,IOException;
	public void stop();
	public void release();
	
	public void setDestination(InetAddress dest, int dport);
	public void setSender(SendAndReceive send);
	public int getLocalPort();
	public int getDestinationPort();
	public int getSSRC();
	public String generateSessionDescriptor() throws IllegalStateException, IOException;
	
	public boolean isStreaming();
	
}
