
package com.simit.video.rtspclient.concepts;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.headers.ContentEncodingHeader;
import com.simit.video.rtspclient.headers.ContentTypeHeader;


public class Content
{
	private String type;

	private String encoding;

	private byte[] content;

	public void setDescription(Message message) throws MissingHeaderException
	{
		type = message.getHeader(ContentTypeHeader.NAME).getRawValue();
		try
		{
			encoding = message.getHeader(ContentEncodingHeader.NAME).getRawValue();
		} catch(MissingHeaderException e)
		{
		}
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public byte[] getBytes()
	{
		return content;
	}

	public void setBytes(byte[] content)
	{
		this.content = content;
	}
}
