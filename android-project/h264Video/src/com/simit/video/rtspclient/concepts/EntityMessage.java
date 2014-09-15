
package com.simit.video.rtspclient.concepts;

import com.simit.video.rtspclient.MissingHeaderException;

public interface EntityMessage
{
	Content getContent();
	
	void setContent(Content content);
	
	Message getMessage();
	
	byte[] getBytes() throws MissingHeaderException;
	
	boolean isEntity();
}
