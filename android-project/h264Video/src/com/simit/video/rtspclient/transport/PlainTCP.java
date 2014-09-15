
package com.simit.video.rtspclient.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.Transport;
import com.simit.video.rtspclient.concepts.TransportListener;


class TransportThread extends Thread
{
	private final PlainTCP transport;

	private volatile SafeTransportListener listener;

	public TransportThread(PlainTCP transport, TransportListener listener)
	{
		this.transport = transport;
		this.listener = new SafeTransportListener(listener);
	}

	public SafeTransportListener getListener()
	{
		return listener;
	}

	public void setListener(TransportListener listener)
	{
		listener = new SafeTransportListener(listener);
	}

	@Override
	public void run()
	{
		listener.connected(transport);
		byte[] buffer = new byte[2048];
		int read = -1;
		while(transport.isConnected())
		{
			try
			{
				read = transport.receive1(buffer);
				if(read == -1)
				{
					transport.setConnected(false);
					listener.remoteDisconnection(transport);
				} else
					listener.dataReceived(transport, buffer, read);
			} catch(IOException e)
			{
				listener.error(transport, e);
			}
		}
	}
}

public class PlainTCP implements Transport
{
	private Socket socket;

	private TransportThread thread;

	private TransportListener transportListener;

	private volatile boolean connected;

	public PlainTCP()
	{
	}

	@Override
	public void connect(URI to) throws IOException
	{
		if(connected)
			throw new IllegalStateException("Socket is still open. Close it first");
		int port = to.getPort();
		if(port == -1) port = 8086;
		InetAddress serverAddr = InetAddress.getByName(to.getHost().toString());
		socket = new Socket(serverAddr, port);
		setConnected(true);
		thread = new TransportThread(this, transportListener);
		thread.start();
	}

	@Override
	public void disconnect()
	{
		setConnected(false);
		try
		{
			socket.close();
		} catch(IOException e)
		{
		}
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
		socket.getOutputStream().write(message.getBytes());
		thread.getListener().dataSent(this);
	}

	@Override
	public void setTransportListener(TransportListener listener)
	{
		transportListener = listener;
		if(thread != null)
			thread.setListener(listener);
	}

	@Override
	public void setUserData(Object data)
	{
	}

	int receive1(byte[] data) throws IOException
	{
		return socket.getInputStream().read(data);
	}
	
	public void receive(byte[] data) 
	{
		//return socket.getInputStream().read(data);
	}

	void setConnected(boolean connected)
	{
		this.connected = connected;
	}
}