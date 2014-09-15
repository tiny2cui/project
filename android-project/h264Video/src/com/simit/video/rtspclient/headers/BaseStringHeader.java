
package com.simit.video.rtspclient.headers;

import com.simit.video.rtspclient.HeaderMismatchException;
import com.simit.video.rtspclient.concepts.Header;


public class BaseStringHeader extends Header
{
	public BaseStringHeader(String name)
	{
		super(name);
	}

	public BaseStringHeader(String name, String header)
	{
		super(header);
		try
		{
			checkName(name);
		} catch(HeaderMismatchException e)
		{
			setName(name);
		}
	}
}
