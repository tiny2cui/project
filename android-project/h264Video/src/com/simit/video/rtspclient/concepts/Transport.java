
package com.simit.video.rtspclient.concepts;

import java.io.IOException;
import java.net.URI;

import com.simit.video.rtspclient.MissingHeaderException;


/**
 * This interface defines a transport protocol (TCP, UDP) or method (HTTP
 * tunneling). Transport also MUST enqueue a command if a connection is busy at
 * the moment it is issued.
 * 
 *
 */
public interface Transport
{
	void connect(URI to) throws IOException;

	void disconnect();

	void sendMessage(Message message) throws IOException, MissingHeaderException;

	void setTransportListener(TransportListener listener);

	void setUserData(Object data);

	boolean isConnected();
	
	 void receive(byte[] data);
}
