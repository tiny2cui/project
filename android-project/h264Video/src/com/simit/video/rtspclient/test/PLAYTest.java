
package com.simit.video.rtspclient.test;

import java.io.IOException;

import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.concepts.Request.Method;


/**
 * Testing the PLAY message. RTP Stream can be checked with Wireshark or a play
 * with RTP only capability.
 * 
 * @author paulo
 * 
 */
public class PLAYTest extends SETUPandTEARDOWNTest
{

	public static void main(String[] args) throws Throwable
	{
		new PLAYTest();
	}

	protected PLAYTest() throws Exception
	{
		super();
	}

	@Override
	public void response(Client client, Request request, Response response)
	{
		try
		{
			super.response(client, request, response);

			if(request.getMethod() == Method.PLAY && response.getStatusCode() == 200)
			{
				Thread.sleep(10000);
				client.teardown();
			}
		} catch(Throwable t)
		{
			generalError(client, t);
		}
	}

	@Override
	protected void sessionSet(Client client) throws IOException
	{
		client.play();
	}
}
