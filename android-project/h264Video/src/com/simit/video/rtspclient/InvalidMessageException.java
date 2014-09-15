
package com.simit.video.rtspclient;

import java.io.IOException;

public class InvalidMessageException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1500523453819730537L;

	public InvalidMessageException()
	{
	}

	public InvalidMessageException(Throwable cause)
	{
		super(cause);
	}

}
