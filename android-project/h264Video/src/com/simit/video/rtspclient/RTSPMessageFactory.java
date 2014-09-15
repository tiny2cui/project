package com.simit.video.rtspclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.simit.video.rtspclient.concepts.Content;
import com.simit.video.rtspclient.concepts.Header;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.MessageBuffer;
import com.simit.video.rtspclient.concepts.MessageFactory;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.concepts.Request.Method;
import com.simit.video.rtspclient.headers.CSeqHeader;
import com.simit.video.rtspclient.headers.ContentEncodingHeader;
import com.simit.video.rtspclient.headers.ContentLengthHeader;
import com.simit.video.rtspclient.headers.ContentTypeHeader;
import com.simit.video.rtspclient.headers.SessionHeader;
import com.simit.video.rtspclient.messages.RTSPDescribeRequest;
import com.simit.video.rtspclient.messages.RTSPOptionsRequest;
import com.simit.video.rtspclient.messages.RTSPPlayRequest;
import com.simit.video.rtspclient.messages.RTSPSetupRequest;
import com.simit.video.rtspclient.messages.RTSPTeardownRequest;


public class RTSPMessageFactory implements MessageFactory
{
	private static Map<String, Constructor<? extends Header>> headerMap;
	private static Map<Request.Method, Class<? extends Request>> requestMap;

	static
	{
		headerMap = new HashMap<String, Constructor<? extends Header>>();
		requestMap = new HashMap<Request.Method, Class<? extends Request>>();

		try
		{
			putHeader(CSeqHeader.class);
			putHeader(ContentEncodingHeader.class);
			putHeader(ContentLengthHeader.class);
			putHeader(ContentTypeHeader.class);
			putHeader(SessionHeader.class);

			requestMap.put(Method.OPTIONS, RTSPOptionsRequest.class);
			requestMap.put(Method.SETUP, RTSPSetupRequest.class);
			requestMap.put(Method.TEARDOWN, RTSPTeardownRequest.class);
			requestMap.put(Method.DESCRIBE, RTSPDescribeRequest.class);
			requestMap.put(Method.PLAY, RTSPPlayRequest.class);
		} catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void putHeader(Class<? extends Header> cls) throws Exception
	{
		headerMap.put(cls.getDeclaredField("NAME").get(null).toString()
				.toLowerCase(), cls.getConstructor(String.class));
	}

	public static String sdpmsg;
	public static boolean flagMalFormedPacket=false;
	ArrayList<String> aSdp = new ArrayList<String>();
	@Override
	public void incomingMessage(MessageBuffer buffer)
			throws InvalidMessageException
	{
//		ByteArrayInputStream in = new ByteArrayInputStream(buffer.getData(), buffer
//				.getOffset(), buffer.getLength());
		ByteArrayInputStream in = new ByteArrayInputStream(buffer.getData());
		int initial = in.available();
		Message message = null;

		try
		{
			// message line.
			String line = readLine(in);
			if(line.startsWith(Message.RTSP_TOKEN))
			{
				message = new RTSPResponse(line);
			} 
			else
			{
				Method method = Method.valueOf(line.substring(0, line.indexOf(' ')));
				Class<? extends Request> cls = requestMap.get(method);
				if(cls != null)
					message = cls.getConstructor(String.class).newInstance(line);
				else
					message = new RTSPRequest(line);
			}

			while(true)
			{
				line = readLine(in);
				if(in == null)
					throw new IncompleteMessageException();
				if(line==null || line.length() == 0)
				{
					break;
				}
				else{
				Constructor<? extends Header> c = headerMap.get(line.substring(0,
						line.indexOf(':')).toLowerCase());
				if(c != null)
					message.addHeader(c.newInstance(line));
				else
					message.addHeader(new Header(line));
				}
			}
			buffer.setMessage(message);

			try
			{
				int length = ((ContentLengthHeader) message
						.getHeader(ContentLengthHeader.NAME)).getValue();
				int left_len = in.available();
				if(in.available()==0 && length>0)
				{
					flagMalFormedPacket=true;
				}
				else if(in.available() < length)
					throw new IncompleteMessageException();
				else{
					Content content = new Content();
					content.setDescription(message);
					byte[] data = new byte[length];
					in.read(data);
					content.setBytes(data);
					message.setEntityMessage(new RTSPEntityMessage(message, content));
					flagMalFormedPacket=false;
				}
			} catch(MissingHeaderException e)
			{
			}

		} catch(Exception e)
		{
			throw new InvalidMessageException(e);
		} finally
		{
			if(!flagMalFormedPacket){
			buffer.setused(initial - in.available());
			try
			{
				in.close();
			} catch(IOException e)
			{
			}
			}
		}
	}

	@Override
	public Request outgoingRequest(String uri, Method method, int cseq,
			Header... extras) throws URISyntaxException
	{
		Class<? extends Request> cls = requestMap.get(method);
		Request message;
		try
		{
			message = cls != null ? cls.newInstance() : new RTSPRequest();
		} catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		message.setLine(uri, method);
		fillMessage(message, cseq, extras);

		return message;
	}

	@Override
	public Request outgoingRequest(Content body, String uri, Method method,
			int cseq, Header... extras) throws URISyntaxException
	{
		Message message = outgoingRequest(uri, method, cseq, extras);
		return (Request) message.setEntityMessage(new RTSPEntityMessage(message,
				body));
	}

	@Override
	public Response outgoingResponse(int code, String text, int cseq,
			Header... extras)
	{
		RTSPResponse message = new RTSPResponse();
		message.setLine(code, text);
		fillMessage(message, cseq, extras);

		return message;
	}

	@Override
	public Response outgoingResponse(Content body, int code, String text,
			int cseq, Header... extras)
	{
		Message message = outgoingResponse(code, text, cseq, extras);
		return (Response) message.setEntityMessage(new RTSPEntityMessage(message, body));
	}

	private void fillMessage(Message message, int cseq, Header[] extras)
	{
		message.addHeader(new CSeqHeader(cseq));

		for(Header h : extras)
			message.addHeader(h);
	}

	private String readLine(InputStream in) throws IOException
	{
		int ch = 0;
		StringBuilder b = new StringBuilder();
		for(ch = in.read(); ch != -1 && ch != 0x0d && ch != 0x0a; ch = in.read())
			b.append((char) ch);
		if(ch == -1)
			return null;
		in.read();
		return b.toString();
	}
}
