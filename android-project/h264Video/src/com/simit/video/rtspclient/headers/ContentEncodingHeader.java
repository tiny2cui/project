
package com.simit.video.rtspclient.headers;


public class ContentEncodingHeader extends BaseStringHeader
{
	public static final String NAME = "Content-Encoding";

	public ContentEncodingHeader()
	{
		super(NAME);
	}

	public ContentEncodingHeader(String header)
	{
		super(NAME, header);
	}

}
