
package com.simit.video.rtspclient.concepts;

/**
 * Listener for {@link Client} events, such as a {@link Response} that arrives.
 * 
 * @author paulo
 * 
 */
public interface ClientListener
{
	void generalError(Client client, Throwable error);

	/**
	 * this method is called when a client obtains a session descriptor, such as
	 * SDP from a DESCRIBE.
	 * 
	 * @param client
	 * @param descriptor
	 */
	void mediaDescriptor(Client client, String descriptor);

	void requestFailed(Client client, Request request, Throwable cause);

	void response(Client client, Request request, Response response);
}
