
package com.simit.video.rtspclient.headers;

public class SessionHeader extends BaseStringHeader
{
	public static final String NAME = "Session";

	public SessionHeader()
	{
		super(NAME);
	}
	
	public SessionHeader(String header)
	{
		super(NAME, header);
	}
	
}
