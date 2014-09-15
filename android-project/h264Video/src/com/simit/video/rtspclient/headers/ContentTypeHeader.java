
package com.simit.video.rtspclient.headers;


public class ContentTypeHeader extends BaseStringHeader
{
	public static final String NAME = "Content-Type";

	public ContentTypeHeader()
	{
		super(NAME);
	}

	public ContentTypeHeader(String header)
	{
		super(NAME, header);
	}
}
