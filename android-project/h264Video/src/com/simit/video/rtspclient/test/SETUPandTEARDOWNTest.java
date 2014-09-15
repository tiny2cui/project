
package com.simit.video.rtspclient.test;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.simit.video.rtspclient.RTSPClient;
import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.ClientListener;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.transport.PlainTCP;


public class SETUPandTEARDOWNTest implements ClientListener
{
	private final static String TARGET_URI = "rtsp://192.168.0.102/sample_50kbit.3gp";

	public static void main(String[] args) throws Throwable
	{
		new SETUPandTEARDOWNTest();
	}

	private final List<String> resourceList;
	
	private String controlURI;

	private int port;

	protected SETUPandTEARDOWNTest() throws Exception
	{
		RTSPClient client = new RTSPClient();

		client.setTransport(new PlainTCP());
		client.setClientListener(this);
		client.describe(new URI(TARGET_URI));
		resourceList = Collections.synchronizedList(new LinkedList<String>());
		port = 2000;
	}

	@Override
	public void requestFailed(Client client, Request request, Throwable cause)
	{
		System.out.println("Request failed \n" + request);
		cause.printStackTrace();
	}

	@Override
	public void response(Client client, Request request, Response response)
	{
		try
		{
			System.out.println("Got response: \n" + response);
			System.out.println("for the request: \n" + request);
			if(response.getStatusCode() == 200)
			{
				switch(request.getMethod())
				{
				case DESCRIBE:
					System.out.println(resourceList);
					if(resourceList.get(0).equals("*"))
					{
						controlURI = request.getURI();
						resourceList.remove(0);
					}
					if(resourceList.size() > 0)
						client.setup(new URI(controlURI), nextPort(), resourceList
								.remove(0));
					else
						client.setup(new URI(controlURI), nextPort());
					break;

				case SETUP:
					//sets up next session or ends everything.
					if(resourceList.size() > 0)
						client.setup(new URI(controlURI), nextPort(), resourceList
								.remove(0));
					else
						sessionSet(client);
					break;
				}
			} else
				client.teardown();
		} catch(Throwable t)
		{
			generalError(client, t);
		}
	}

	@Override
	public void generalError(Client client, Throwable error)
	{
		error.printStackTrace();
	}

	@Override
	public void mediaDescriptor(Client client, String descriptor)
	{
		// searches for control: session and media arguments.
		final String target = "control:";
		System.out.println("Session Descriptor\n" + descriptor);
		int position = -1;
		while((position = descriptor.indexOf(target)) > -1)
		{
			descriptor = descriptor.substring(position + target.length());
			resourceList.add(descriptor.substring(0, descriptor.indexOf('\r')));
		}
	}
	
	protected void sessionSet(Client client) throws IOException
	{
		client.teardown();	
	}

	private int nextPort()
	{
		return (port += 2) - 2;
	}
}