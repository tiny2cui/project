
package com.simit.video.rtspclient.concepts;

import java.net.URISyntaxException;

import com.simit.video.rtspclient.InvalidMessageException;

/**
 * A RTSP message factory to build objects from incoming messages or to
 * initialize outgoing messages correctly.
 * 
 * 
 * 
 * 
 */
public interface MessageFactory
{

	/**
	 * 
	 * @param message
	 */
	void incomingMessage(MessageBuffer message) throws InvalidMessageException;

	Request outgoingRequest(String uri, Request.Method method, int cseq,
			Header... extras) throws URISyntaxException;

	Request outgoingRequest(Content body, String uri, Request.Method method,
			int cseq, Header... extras) throws URISyntaxException;

	Response outgoingResponse(int code, String message, int cseq, Header... extras);

	Response outgoingResponse(Content body, int code, String text, int cseq,
			Header... extras);
}
