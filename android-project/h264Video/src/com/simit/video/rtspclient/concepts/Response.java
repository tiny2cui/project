
package com.simit.video.rtspclient.concepts;

public interface Response extends Message
{	
	void setLine(int statusCode, String statusPhrase);
	
	int getStatusCode();
	
	String getStatusText();
}
