
package com.simit.video.rtspclient;

import java.net.URI;
import java.net.URISyntaxException;

import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;


public class RTSPRequest extends RTSPMessage implements Request
{
	private Method method;

	private String uri;

	public RTSPRequest()
	{
	}

	public RTSPRequest(String messageLine) throws URISyntaxException
	{
		String[] parts = messageLine.split(" ");
		setLine(parts[0], Method.valueOf(parts[1]));
	}

	@Override
	public void setLine(String uri, Method method) throws URISyntaxException
	{
		this.method = method;
		this.uri = new URI(uri).toString();
		;

		super.setLine(method.toString() + ' ' + uri + ' ' + RTSP_VERSION_TOKEN);
	}

	@Override
	public Method getMethod()
	{
		return method;
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public void handleResponse(Client client, Response response)
	{
		if(testForClose(client, this) || testForClose(client, response))
			client.getTransport().disconnect();
	}

	protected void setURI(String uri)
	{
		this.uri = uri;
	}

	protected void setMethod(Method method)
	{
		this.method = method;
	}

	private boolean testForClose(Client client, Message message)
	{
		try
		{
			return message.getHeader("Connection").getRawValue().equalsIgnoreCase("close");
		} catch(MissingHeaderException e)
		{
		} catch(Exception e)
		{
			client.getClientListener().generalError(client, e);
		}
		return false;
	}
}