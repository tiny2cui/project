
package com.simit.video.rtspclient.messages;

import java.net.URISyntaxException;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.RTSPRequest;
import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.headers.SessionHeader;


public class RTSPTeardownRequest extends RTSPRequest
{

	public RTSPTeardownRequest()
	{
		super();
	}

	public RTSPTeardownRequest(String messageLine) throws URISyntaxException
	{
		super(messageLine);
	}

	@Override
	public byte[] getBytes() throws MissingHeaderException
	{
		getHeader(SessionHeader.NAME);
		return super.getBytes();
	}
	
	@Override
	public void handleResponse(Client client, Response response)
	{
		super.handleResponse(client, response);
		if(response.getStatusCode() == 200) client.setSession(null);
		client.getTransport().disconnect();
	}
}
