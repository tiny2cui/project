package com.simit.video.rtspclient.transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.Transport;
import com.simit.video.rtspclient.concepts.TransportListener;
import com.simit.video.test.FrameType;
import com.simit.video.test.SendAndReceive;


public class PlainUDP implements Transport
{
	
	

	private TransportListener transportListener;

	
	private volatile boolean connected;
	
	private volatile SafeTransportListener sListener;

	private SendAndReceive send;
	public PlainUDP(SendAndReceive send)
	{
		
		this.send=send;
	}

	@Override
	public void connect(URI to) throws IOException
	{
		setConnected(true);
		sListener=new SafeTransportListener(transportListener);
		sListener.connected(this);
	}

	@Override
	public void disconnect()
	{
		setConnected(false);
		
	}

	@Override
	public boolean isConnected()
	{
		return connected;
	}

	@Override
	public synchronized void sendMessage(Message message) throws IOException,
			MissingHeaderException
	{
		send.sendData(message.getBytes(), FrameType.MESSAGE_VIDEO_CONTROL_C2S);
	}

	@Override
	public void setTransportListener(TransportListener listener)
	{
		transportListener = listener;
		if(sListener != null)
			sListener=new SafeTransportListener(listener);
	}

	@Override
	public void setUserData(Object data)
	{
	}

	public void receive(byte[] data)
	{
		//byte[] buffer = new byte[2048];
		int read = -1;
		if(data!=null && data.length>0){
			read=data.length;
		}
		if(isConnected())
		{
			//read = transport.receive(buffer);
			if(read == -1)
			{
				setConnected(false);
				sListener.remoteDisconnection(this);
			} else{
				sListener.dataReceived(this, data, read);
			}	
		}
	}

	void setConnected(boolean connected)
	{
		this.connected = connected;
	}
}
