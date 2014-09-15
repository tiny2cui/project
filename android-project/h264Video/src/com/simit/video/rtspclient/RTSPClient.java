
package com.simit.video.rtspclient;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.ClientListener;
import com.simit.video.rtspclient.concepts.Header;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.MessageBuffer;
import com.simit.video.rtspclient.concepts.MessageFactory;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.concepts.Transport;
import com.simit.video.rtspclient.concepts.TransportListener;
import com.simit.video.rtspclient.concepts.Request.Method;
import com.simit.video.rtspclient.headers.RangeHeader;
import com.simit.video.rtspclient.headers.SessionHeader;
import com.simit.video.rtspclient.headers.TransportHeader;
import com.simit.video.rtspclient.headers.TransportHeader.LowerTransport;
import com.simit.video.rtspclient.messages.RTSPOptionsRequest;


public class RTSPClient implements Client, TransportListener
{
	private Transport transport;

	private MessageFactory messageFactory;

	private MessageBuffer messageBuffer;

	private volatile int cseq;

	private SessionHeader session;

	/**
	 * URI kept from last setup.
	 */
	private URI uri;

	private Map<Integer, Request> outstanding;

	private ClientListener clientListener;

	public RTSPClient()
	{
		messageFactory = new RTSPMessageFactory();
		cseq = 0;
		outstanding = new HashMap<Integer, Request>();
		messageBuffer = new MessageBuffer();
	}

	@Override
	public Transport getTransport()
	{
		return transport;
	}

	@Override
	public void setSession(SessionHeader session)
	{
		this.session = session;
	}

	@Override
	public MessageFactory getMessageFactory()
	{
		return messageFactory;
	}

	@Override
	public URI getURI()
	{
		return uri;
	}

	@Override
	public void options(String uri, URI endpoint) throws URISyntaxException,
			IOException
	{
		try
		{
			RTSPOptionsRequest message = (RTSPOptionsRequest) messageFactory
					.outgoingRequest(uri, Method.OPTIONS, nextCSeq());
			if(!getTransport().isConnected())
				message.addHeader(new Header("Connection", "close"));
			send(message, endpoint);
		} catch(MissingHeaderException e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void play() throws IOException
	{
		RangeHeader rH = new RangeHeader("npt=0.000-");
		try
		{
			String s1 = session.getRawValue();
			if(s1.indexOf(";")!=-1) 
				{
				s1=s1.substring(0,s1.indexOf(";"));
				session.setRawValue(s1);
				send(messageFactory.outgoingRequest(uri.toString(), Method.PLAY,
						nextCSeq(), session,rH));
				}
			else send(messageFactory.outgoingRequest(uri.toString(), Method.PLAY,
					nextCSeq(), session,rH));
		} catch(Exception e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void record() throws IOException
	{
		throw new UnsupportedOperationException(
				"Recording is not supported in current version.");
	}

	@Override
	public void setClientListener(ClientListener listener)
	{
		clientListener = listener;
	}

	@Override
	public ClientListener getClientListener()
	{
		return clientListener;
	}

	@Override
	public void setTransport(Transport transport)
	{
		this.transport = transport;
		transport.setTransportListener(this);
	}

	@Override
	public void describe(URI uri) throws IOException
	{
		this.uri = uri;
		try
		{
			send(messageFactory.outgoingRequest(uri.toString(), Method.DESCRIBE,
					nextCSeq(), new Header("Accept", "application/sdp")));
		} catch(Exception e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void setup(URI uri, int localPort) throws IOException
	{
		this.uri = uri;
		
		try
		{
			//String portParam = "client_port=" + localPort + "-" + (1 + localPort);

//			send(getSetup(uri.toString(), localPort, new TransportHeader(
//					LowerTransport.DEFAULT, "unicast", portParam), session));
			send(getSetup(uri.toString(), localPort, new TransportHeader(
					LowerTransport.DEFAULT, "unicast"), session));
		} catch(Exception e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void setup(URI uri, int localPort, String resource) throws IOException
	{
		this.uri = uri;
		try
		{
			//String portParam = "client_port=" + localPort + "-" + (1 + localPort);
			String finalURI = uri.toString();
			
			if(resource != null && !resource.equals("*"))
			{
				if(finalURI.endsWith("/"))finalURI +=resource;
				else finalURI += '/' + resource;
			}
				
//			send(getSetup(finalURI, localPort, new TransportHeader(
//					LowerTransport.DEFAULT, "unicast", portParam), session));
			send(getSetup(finalURI, localPort, new TransportHeader(
					LowerTransport.DEFAULT, "unicast"), session));
		} catch(Exception e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void teardown()
	{
		if(session == null)
			return;
		try
		{
			send(messageFactory.outgoingRequest(uri.toString(), Method.TEARDOWN,
					nextCSeq(), session, new Header("Connection", "close")));
		} catch(Exception e)
		{
			if(clientListener != null)
				clientListener.generalError(this, e);
		}
	}

	@Override
	public void connected(Transport t) throws Throwable
	{
	}

	@Override
	public void dataReceived(Transport t, byte[] data, int size) throws Throwable
	{
		messageBuffer.addData(data, size);
		int len = messageBuffer.getLength();
		int len_s = size;
//		
		while(messageBuffer.getLength() > 0)
		{
			messageFactory.incomingMessage(messageBuffer);
			if(RTSPMessageFactory.flagMalFormedPacket)
			{
				break;
			}
			else
			{
				try
				{
					messageBuffer.discardData();
					Message message = messageBuffer.getMessage();
					if(message instanceof Request)
						send(messageFactory.outgoingResponse(405, "Method Not Allowed",
								message.getCSeq().getValue()));
					else
					{
						Request request = null;
						synchronized(outstanding)
						{
							request = outstanding.remove(message.getCSeq().getValue());
						}
						Response response = (Response) message;
						request.handleResponse(this, response);
						clientListener.response(this, request, response);
					}
				} catch(IncompleteMessageException ie)
				{
					break;
				} catch(InvalidMessageException e)
				{
					messageBuffer.discardData();
					if(clientListener != null)
						clientListener.generalError(this, e.getCause());
				}
			}
		}
			
	}

	@Override
	public void dataSent(Transport t) throws Throwable
	{
	}

	@Override
	public void error(Transport t, Throwable error)
	{
		clientListener.generalError(this, error);
	}

	@Override
	public void error(Transport t, Message message, Throwable error)
	{
		clientListener.requestFailed(this, (Request) message, error);
	}

	@Override
	public void remoteDisconnection(Transport t) throws Throwable
	{
		synchronized(outstanding)
		{
			for(Map.Entry<Integer, Request> request : outstanding.entrySet())
				clientListener.requestFailed(this, request.getValue(),
						new SocketException("Socket has been closed"));
		}
	}

	@Override
	public int nextCSeq()
	{
		return cseq++;
	}

	@Override
	public void send(Message message) throws IOException, MissingHeaderException
	{
		send(message, uri);
	}

	private void send(Message message, URI endpoint) throws IOException,
			MissingHeaderException
	{
		if(!transport.isConnected())
			transport.connect(null);

		if(message instanceof Request)
		{
			Request request = (Request) message;
			synchronized(outstanding)
			{
				outstanding.put(message.getCSeq().getValue(), request);
			}
			try
			{
				transport.sendMessage(message);
			} catch(IOException e)
			{
				clientListener.requestFailed(this, request, e);
			}
		} else
			transport.sendMessage(message);
	}

	private Request getSetup(String uri, int localPort, Header... headers)
			throws URISyntaxException
	{
		return getMessageFactory().outgoingRequest(uri, Method.SETUP, nextCSeq(),
				headers);
	}
}