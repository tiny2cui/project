
package com.simit.video.rtspclient.headers;

public class RangeHeader extends BaseStringHeader
{
	public static final String NAME = "Range";

	public RangeHeader()
	{
		super(NAME);
	}
	
	public RangeHeader(String header)
	{
		super(NAME, header);
	}
	
}
