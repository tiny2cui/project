
package com.simit.video.rtspclient;

import com.simit.video.rtspclient.concepts.Response;

public class RTSPResponse extends RTSPMessage implements Response
{
	private int status;

	private String text;

	public RTSPResponse()
	{
	}

	public RTSPResponse(String line)
	{
		setLine(line);
		line = line.substring(line.indexOf(' ') + 1);
		status = Integer.parseInt(line.substring(0, line.indexOf(' ')));
		text = line.substring(line.indexOf(' ') + 1);
	}

	@Override
	public int getStatusCode()
	{
		return status;
	}
	
	@Override
	public String getStatusText()
	{
		return text;
	}

	@Override
	public void setLine(int statusCode, String statusText)
	{
		status = statusCode;
		text = statusText;
		super.setLine(RTSP_VERSION_TOKEN + ' ' + status + ' ' + text);
	}
}
