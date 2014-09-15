
package com.simit.video.rtspclient.messages;

import java.net.URISyntaxException;
import com.simit.video.rtspclient.RTSPRequest;


public class RTSPOptionsRequest extends RTSPRequest
{
	public RTSPOptionsRequest()
	{
	}
	
	public RTSPOptionsRequest(String line) throws URISyntaxException
	{
		super(line);
	}
	
	@Override
	public void setLine(String uri, Method method) throws URISyntaxException
	{
		setMethod(method);
		//setURI("*".equals(uri) ? uri : new URI(uri).toString());
		setURI("*");
		super.setLine(method.toString() + ' ' + "*" + ' ' + RTSP_VERSION_TOKEN);
	}
}
