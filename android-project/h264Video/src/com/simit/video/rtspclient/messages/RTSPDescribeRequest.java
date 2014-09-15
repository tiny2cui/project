
package com.simit.video.rtspclient.messages;

import java.net.URISyntaxException;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.RTSPMessageFactory;
import com.simit.video.rtspclient.RTSPRequest;
import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.Response;


public class RTSPDescribeRequest extends RTSPRequest
{

	public RTSPDescribeRequest()
	{
		super();
	}

	public RTSPDescribeRequest(String messageLine) throws URISyntaxException
	{
		super(messageLine);
	}

	@Override
	public byte[] getBytes() throws MissingHeaderException
	{
		getHeader("Accept");
		return super.getBytes();
	}

	@Override
	public void handleResponse(Client client, Response response)
	{
		super.handleResponse(client, response);
		try
		{
			client.getClientListener().mediaDescriptor(client,
//					RTSPMessageFactory.sdpmsg);
					new String(response.getEntityMessage().getContent().getBytes()));
		} catch(Exception e)
		{
			client.getClientListener().generalError(client, e);
		}
	}
}
