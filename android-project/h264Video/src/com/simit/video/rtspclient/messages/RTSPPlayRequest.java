
package com.simit.video.rtspclient.messages;

import java.net.URISyntaxException;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.RTSPRequest;
import com.simit.video.rtspclient.headers.SessionHeader;


/**
 * 
 * @author paulo
 *
 */
public class RTSPPlayRequest extends RTSPRequest
{

	public RTSPPlayRequest()
	{
	}
	
	public RTSPPlayRequest(String messageLine) throws URISyntaxException
	{
		super(messageLine);
	}

	@Override
	public byte[] getBytes() throws MissingHeaderException
	{
		getHeader(SessionHeader.NAME);
		return super.getBytes();
	}
}
