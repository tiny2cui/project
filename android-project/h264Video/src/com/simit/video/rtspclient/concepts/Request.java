
package com.simit.video.rtspclient.concepts;

import java.net.URISyntaxException;

public interface Request extends Message
{
	enum Method
	{
		OPTIONS, DESCRIBE, SETUP, PLAY, RECORD, TEARDOWN
	};

	void setLine(String uri, Method method) throws URISyntaxException;
	
	Method getMethod();
	
	String getURI();
	
	void handleResponse(Client client, Response response);	
}
