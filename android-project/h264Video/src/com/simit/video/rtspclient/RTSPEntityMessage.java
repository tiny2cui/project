
package com.simit.video.rtspclient;

import com.simit.video.rtspclient.concepts.Content;
import com.simit.video.rtspclient.concepts.EntityMessage;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.headers.ContentEncodingHeader;
import com.simit.video.rtspclient.headers.ContentLengthHeader;
import com.simit.video.rtspclient.headers.ContentTypeHeader;


public class RTSPEntityMessage implements EntityMessage
{
	private Content content;

	private final Message message;

	public RTSPEntityMessage(Message message)
	{
		this.message = message;
	}
	
	public RTSPEntityMessage(Message message, Content body)
	{
		this(message);
		setContent(body);
	}
	
	@Override
	public Message getMessage()
	{
		return message;
	}

	public byte[] getBytes() throws MissingHeaderException
	{
		message.getHeader(ContentTypeHeader.NAME);
		message.getHeader(ContentLengthHeader.NAME);
		return content.getBytes();
	}

	@Override
	public Content getContent()
	{
		return content;
	}

	@Override
	public void setContent(Content content)
	{
		if(content == null) throw new NullPointerException();
		this.content = content;
		message.addHeader(new ContentTypeHeader(content.getType()));
		if(content.getEncoding() != null)
			message.addHeader(new ContentEncodingHeader(content.getEncoding()));
		message.addHeader(new ContentLengthHeader(content.getBytes().length));
	}
	
	@Override
	public boolean isEntity()
	{
		return content != null;
	}
}
