
package com.simit.video.rtspclient.concepts;

import com.simit.video.rtspclient.MissingHeaderException;
import com.simit.video.rtspclient.headers.CSeqHeader;


/**
 * Models a RTSP Message
 * 
 * 
 * 
 */
public interface Message
{
	static String RTSP_TOKEN = "RTSP/";

	static String RTSP_VERSION = "1.0";

	static String RTSP_VERSION_TOKEN = RTSP_TOKEN + RTSP_VERSION;

	/**
	 * 
	 * @return the Message line (the first line of the message)
	 */
	String getLine();

	/**
	 * Returns a header, if exists
	 * 
	 * @param name
	 *          Name of the header to be searched
	 * @return value of that header
	 * @throws MissingHeaderException
	 */
	Header getHeader(String name) throws MissingHeaderException;

	/**
	 * Convenience method to get CSeq.
	 * 
	 * @return
	 */
	CSeqHeader getCSeq();

	/**
	 * 
	 * @return all headers in the message, except CSeq
	 */
	Header[] getHeaders();

	/**
	 * Adds a new header or replaces if one already exists. If header to be added
	 * is a CSeq, implementation MUST keep reference of this header.
	 * 
	 * @param header
	 */
	void addHeader(Header header);

	/**
	 * 
	 * @return message as a byte array, ready for transmission.
	 */
	byte[] getBytes() throws MissingHeaderException;

	/**
	 * 
	 * @return Entity part of message, it exists.
	 */
	EntityMessage getEntityMessage();

	/**
	 * 
	 * @param entity
	 *          adds an entity part to the message.
	 * @return this, for easier construction.
	 */
	Message setEntityMessage(EntityMessage entity);
}
