

package com.simit.video.stream;

import java.io.InputStream;
import java.net.InetAddress;

import com.simit.video.server.RtpSocket;
import com.simit.video.test.SendAndReceive;

/**
 * 
 * Each packetizer inherits from this one and therefore uses RTP and UDP
 *
 */
abstract public class AbstractPacketizer {
	
	protected static final int rtphl = RtpSocket.RTP_HEADER_LENGTH;
	
	protected RtpSocket socket = null;
	protected InputStream is = null;
	protected boolean running = false;
	protected byte[] buffer;
	
	public AbstractPacketizer() {
		socket = new RtpSocket();
		buffer = socket.getBuffer();
	}	
	
	public AbstractPacketizer(InputStream fis) {
		super();
		this.is = fis;
	}
	
	public RtpSocket getRtpSocket() {
		return socket;
	}
	
	public void setInputStream(InputStream fis) {
		this.is = fis;
	}
	
	public void setDestination(InetAddress dest, int dport) {
		socket.setDestination(dest, dport);
	}
	
	public void setSender(SendAndReceive send) {
		socket.setSender(send);
	}
	
	public abstract void stop();
	public abstract void start();
	
    // Useful for debug
    protected String printBuffer(int start,int end) {
    	String str = "";
    	for (int i=start;i<end;i++) str+=","+Integer.toHexString(buffer[i]&0xFF);
    	return str;
    }
	
}
