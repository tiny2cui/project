package com.simit.net.task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.simit.net.utils.Constants;

public class InternalUDPTransThread extends Thread{
	
	private final int					RECV_BUFFER_SIZE = 1024;
	
	private DatagramPacket				interalDatagramPacket;
	private DatagramSocket				internalDatagramSocket;
	private byte[]						recvBuf;
	
	public InternalUDPTransThread(){
		try {
			recvBuf = new byte[RECV_BUFFER_SIZE];
			internalDatagramSocket = new DatagramSocket(Constants.INTERNAL_UDP_PORT);
			interalDatagramPacket = new DatagramPacket(recvBuf, RECV_BUFFER_SIZE);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	public void run(){
		
		try {
			internalDatagramSocket.receive(interalDatagramPacket);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
}
