
package com.simit.video.rtspclient.concepts;

/**
 * Listener for transport events. Implementations of {@link Transport}, when
 * calling a listener method, must catch all errors and submit them to the
 * error() method.
 * 
 * @author paulo
 * 
 */
public interface TransportListener
{
	void connected(Transport t) throws Throwable;

	void error(Transport t, Throwable error);

	void error(Transport t, Message message, Throwable error);

	void remoteDisconnection(Transport t) throws Throwable;

	void dataReceived(Transport t, byte[] data, int size) throws Throwable;

	void dataSent(Transport t) throws Throwable;
}
