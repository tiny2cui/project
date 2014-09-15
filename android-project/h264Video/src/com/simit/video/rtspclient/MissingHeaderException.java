
package com.simit.video.rtspclient;

public class MissingHeaderException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3027257996891420069L;

	public MissingHeaderException(String header)
	{
		super("Header " + header + " wasn't found.");
	}
}
