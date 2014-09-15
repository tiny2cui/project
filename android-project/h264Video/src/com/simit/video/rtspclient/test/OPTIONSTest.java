
package com.simit.video.rtspclient.test;

import java.net.URI;

import com.simit.video.rtspclient.RTSPClient;
import com.simit.video.rtspclient.concepts.Client;
import com.simit.video.rtspclient.concepts.ClientListener;
import com.simit.video.rtspclient.concepts.Request;
import com.simit.video.rtspclient.concepts.Response;
import com.simit.video.rtspclient.transport.PlainTCP;


public class OPTIONSTest implements ClientListener
{
	public static void main(String[] args) throws Throwable
	{
		new OPTIONSTest();
	}

	private OPTIONSTest() throws Exception
	{
		RTSPClient client = new RTSPClient();

		client.setTransport(new PlainTCP());
		client.setClientListener(this);
		client.options("*", new URI("rtsp://rmv8.bbc.net.uk/1xtra/"));
	}

	@Override
	public void requestFailed(Client client, Request request, Throwable cause)
	{
		System.out.println("Request failed \n" + request);
	}

	@Override
	public void response(Client client, Request request, Response response)
	{
		System.out.println("Got response: \n" + response);
		System.out.println("for the request: \n" + request);
	}

	@Override
	public void generalError(Client client, Throwable error)
	{
		error.printStackTrace();
	}

	@Override
	public void mediaDescriptor(Client client, String descriptor)
	{
		// TODO Auto-generated method stub
		
	}
}
