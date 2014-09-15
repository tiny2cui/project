
package com.simit.video.rtspclient.headers;

import com.simit.video.rtspclient.concepts.Header;

public class BaseIntegerHeader extends Header
{
	
	private int value;
	
	public BaseIntegerHeader(String name)
	{
		super(name);
		String text = getRawValue();
		if(text != null) value = Integer.parseInt(text);
	}
	
	public BaseIntegerHeader(String name, int value)
	{
		super(name);
		setValue(value);
	}

	public BaseIntegerHeader(String name, String header)
	{
		super(header);
		checkName(name);
		value = Integer.parseInt(getRawValue());
	}

	public final void setValue(int newValue)
	{
		value = newValue;
		setRawValue(String.valueOf(value));
	}
	
	public final int getValue()
	{
		return value;
	}
}
