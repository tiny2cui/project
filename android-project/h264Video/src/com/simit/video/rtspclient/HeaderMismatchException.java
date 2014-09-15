
package com.simit.video.rtspclient;

/**
 * Exception thrown when a class is initialized with a header name different
 * than expected.
 * 
 * @author paulo
 * 
 */ 
public class HeaderMismatchException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6316852391642646327L;

	public HeaderMismatchException(String expected, String current)
	{
		super("expected " + expected + " but got " + current);
	}
}
