
package com.simit.video.rtspclient.concepts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.headers.SessionHeader;


/**
 * Models a simple RTSPClient.<br>
 * it MUST keep track of CSeq to match requests and responses.
 * 
 * @author paulo
 * 
 */
public interface Client
{

	void setTransport(Transport transport);

	Transport getTransport();

	void setClientListener(ClientListener listener);

	ClientListener getClientListener();

	void setSession(SessionHeader session);

	MessageFactory getMessageFactory();

	URI getURI();

	void describe(URI uri) throws IOException;

	/**
	 * Sets up a session
	 * 
	 * @param uri
	 *          base URI for the request.
	 * @param localPort
	 *          Port for RTP stream. RTCP port is derived by adding 1 to this
	 *          port.
	 */
	void setup(URI uri, int localPort) throws IOException;

	/**
	 * Sets up a session with a specific resource. If a session has been
	 * previously established, a call to this method will set up a different
	 * resource with the same session identifier as the previous one.
	 * 
	 * @see #setup(URI, int)
	 * @param resource
	 *          resource derived from SDP (via control: attribute). the final URI
	 *          will be <code>uri + '/' + resource</code>.
	 */
	void setup(URI uri, int localPort, String resource) throws IOException;

	void teardown();

	void play() throws IOException;

	void record() throws IOException;

	void options(String uri, URI endpoint) throws URISyntaxException, IOException;

	/**
	 * Sends a message and, if message is a {@link Request}, store it as an
	 * outstanding request.
	 * 
	 * @param message
	 * @throws IOException
	 * @throws MissingHeaderException
	 *           Malformed message, lacking mandatory header.
	 */
	void send(Message message) throws IOException, MissingHeaderException;

	/**
	 * 
	 * @return value of CSeq for next packet.
	 */
	int nextCSeq();
}
