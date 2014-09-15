
package com.simit.video.rtspclient.headers;


public class ContentLengthHeader extends BaseIntegerHeader
{
	public static final String NAME = "Content-Length";
	
	public ContentLengthHeader()
	{
		super(NAME);
	}
	
	public ContentLengthHeader(int value)
	{
		super(NAME, value);
	}

	public ContentLengthHeader(String header)
	{
		super(NAME, header);
	}

}
