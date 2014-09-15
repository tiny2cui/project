

package com.simit.video.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import com.simit.video.stream.RtpPacket;
import com.simit.video.test.FrameType;
import com.simit.video.test.SendAndReceive;


public class RtpSocket {

	private String TAG="RtpSocket";
	private DatagramSocket socket;
	private DatagramPacket datagram;
	
	private byte[] buffer = new byte[MTU];
	private int seq = 0;
	private boolean upts = false;
	private int ssrc;
	private int remotePort = -1;
	/** Remote address */
	InetAddress remoteAddr;
	private SendAndReceive send;
	
	public static final int RTP_HEADER_LENGTH = 12;
	public static final int MTU = 1500;
	
	private int sendDataSize=0;
	/** Creates a new RTP socket (only receiver) */
	public RtpSocket(DatagramSocket datagram_socket) {
		
		socket = datagram_socket;
		remoteAddr = null;
		remotePort = 0;
		datagram = new DatagramPacket(new byte[1],1);
	}
	
	/** Creates a new RTP socket (sender and receiver) */
	public RtpSocket(DatagramSocket datagram_socket,
			InetAddress remote_address, int remote_port) {
		socket = datagram_socket;
		remoteAddr = remote_address;
		remotePort = remote_port;
		datagram = new DatagramPacket(new byte[1400],1400);
	}
	
	/** Returns the RTP SipdroidSocket */
	public DatagramSocket getDatagramSocket() {
		return socket;
	}
	
	public RtpSocket(byte[] buffer, InetAddress dest, int dport) {
		datagram.setPort(dport);
		datagram.setAddress(dest);
	}
	
	public RtpSocket() {
		
		/*							     Version(2)  Padding(0)					 					*/
		/*									 ^		  ^			Extension(0)						*/
		/*									 |		  |				^								*/
		/*									 | --------				|								*/
		/*									 | |---------------------								*/
		/*									 | ||  -----------------------> Source Identifier(0)	*/
		/*									 | ||  |												*/
		buffer[0] = (byte) Integer.parseInt("10000000",2);
		
		/* Payload Type */
		buffer[1] = (byte) 96;
		
		/* Byte 2,3        ->  Sequence Number                   */
		/* Byte 4,5,6,7    ->  Timestamp                         */
		
		/* Byte 8,9,10,11  ->  Sync Source Identifier            */
		setLong((ssrc=(new Random()).nextInt()),8,12);
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			
		}
		datagram = new DatagramPacket(buffer, 1);

	}

	public void close() {
		socket.close();
	}
	
	public void setSSRC(int ssrc) {
		this.ssrc= ssrc; 
		setLong(ssrc,8,12);
	}
	
	public int getSSRC() {
		return ssrc;
	}
	
	public void setDestination(InetAddress dest, int dport) {
		remotePort = dport;
		datagram.setPort(dport);
		datagram.setAddress(dest);
	}
	
	public void setSender(SendAndReceive send){
		this.send=send;
	}
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public int getPort() {
		return remotePort;
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}
	/** Receives a RTP packet from this socket */
	public void receive(RtpPacket rtpp) throws IOException {
		
	
		datagram.setData(rtpp.getPacket());
		datagram.setLength(rtpp.getPacket().length);
		socket.receive(datagram);
		if (!socket.isConnected())
			socket.connect(datagram.getAddress(),datagram.getPort());
		
		rtpp.setLength(datagram.getLength());
//		Log.i(TAG, "receiveSize-->"+rtpp.packet_len);//+"receiveData-->"+new String(rtpp.packet));
//		Log.i(TAG, "receiveData-->"+new String(rtpp.packet));
	}
	
	
	/* Send RTP packet over the network */
	public void send(int length) throws IOException {
		
		updateSequence();
		//datagram.setLength(length);
		byte[] data=new byte[length];
		System.arraycopy(buffer, 0, data, 0, length);
		
		//socket.send(datagram);
		send.sendData(data, FrameType.MESSAGE_VIDEO_DATA);
		//Log.i(TAG, "send port"+datagram.getPort()+"send size-->"+datagram.getLength());
		//Log.i(TAG, "send -->"+sendDataSize++);
		if (upts) {
			upts = false;
			buffer[1] -= 0x80;
		}
		
	}
	
	/** Sends a RTP packet from this socket */
	public void send(RtpPacket rtpp) throws IOException {
//		datagram.setData(rtpp.getPacket());
//		datagram.setLength(rtpp.getLength());
//		datagram.setAddress(remoteAddr);
//		datagram.setPort(remotePort);
////		Log.i(TAG, "send Size--->"+datagram.getLength());
//		socket.send(datagram);
		send.sendData(rtpp.getPacket(), FrameType.MESSAGE_VIDEO_DATA);
		
		
	}
	
	private void updateSequence() {
		setLong(++seq, 2, 4);
	}
	
	public void updateTimestamp(long timestamp) {
		setLong(timestamp, 4, 8);
	}
	
	public void markNextPacket() {
		upts = true;
		buffer[1] += 0x80; // Mark next packet
	}
	
	private void setLong(long n, int begin, int end) {
		for (end--; end >= begin; end--) {
			buffer[end] = (byte) (n % 256);
			n >>= 8;
		}
	}	
	
}
