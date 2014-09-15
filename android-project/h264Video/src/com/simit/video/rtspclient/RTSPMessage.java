
package com.simit.video.rtspclient;

import java.util.ArrayList;
import java.util.List;

import com.simit.video.rtspclient.concepts.EntityMessage;
import com.simit.video.rtspclient.concepts.Header;
import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.headers.CSeqHeader;


public abstract class RTSPMessage implements Message
{
	private String line;

	private List<Header> headers;

	private CSeqHeader cseq;
	
	private EntityMessage entity;

	public RTSPMessage()
	{
		headers = new ArrayList<Header>();
	}

	@Override
	public byte[] getBytes() throws MissingHeaderException
	{
		getHeader(CSeqHeader.NAME);
		//addHeader(new Header("User-Agent", "RTSPClientLib/Java"));
		byte[] message = toString().getBytes();
		if(getEntityMessage() != null)
		{
			byte[] body = entity.getBytes();
			byte[] full = new byte[message.length + body.length];
			System.arraycopy(message, 0, full, 0, message.length);
			System.arraycopy(body, 0, full, message.length, body.length);
			message = full;
		}
		return message;
	}

	@Override
	public Header getHeader(final String name) throws MissingHeaderException
	{
		int index = headers.indexOf(new Object() {
			@Override
			public boolean equals(Object obj)
			{
				return name.equalsIgnoreCase(((Header) obj).getName());
			}
		});
		if(index == -1)
			throw new MissingHeaderException(name);
		return headers.get(index);
	}

	@Override
	public Header[] getHeaders()
	{
		return headers.toArray(new Header[headers.size()]);
	}

	@Override
	public CSeqHeader getCSeq()
	{
		return cseq;
	}

	@Override
	public String getLine()
	{
		return line;
	}

	public void setLine(String line)
	{
		this.line = line;
	}
	
	@Override
	public void addHeader(Header header)
	{
		if(header == null) return;
		if(header instanceof CSeqHeader)
			cseq = (CSeqHeader) header;
		int index = headers.indexOf(header);
		if(index > -1)
			headers.remove(index);
		else
			index = headers.size();
		headers.add(index, header);
	}
	
	@Override
	public EntityMessage getEntityMessage()
	{
		return entity;
	}
	
	@Override
	public Message setEntityMessage(EntityMessage entity)
	{
		this.entity = entity;
		return this;
	}
	
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(getLine()).append("\r\n");
		for(Header header : headers)
			buffer.append(header).append("\r\n");
		buffer.append("\r\n");
		return buffer.toString();
	}
}
