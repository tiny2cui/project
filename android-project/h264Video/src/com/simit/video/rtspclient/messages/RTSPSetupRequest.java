
package com.simit.video.rtspclient.messages;

import java.net.URISyntaxException;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.RTSPRequest;
import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.headers.SessionHeader;


public class RTSPSetupRequest extends RTSPRequest
{
	public RTSPSetupRequest()
	{
	}

	public RTSPSetupRequest(String line) throws URISyntaxException
	{
		super(line);
	}

	@Override
	public byte[] getBytes() throws MissingHeaderException
	{
		getHeader("Transport");
		return super.getBytes();
	}

	@Override
	public void handleResponse(Client client, Response response)
	{
		super.handleResponse(client, response);
		try
		{
			if(response.getStatusCode() == 200)
				client.setSession((SessionHeader) response
						.getHeader(SessionHeader.NAME));
		} catch(MissingHeaderException e)
		{
			client.getClientListener().generalError(client, e);
		}
	}
}
