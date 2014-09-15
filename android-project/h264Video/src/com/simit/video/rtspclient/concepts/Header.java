package com.simit.video.rtspclient.concepts;

import com.simit.video.rtspclient.HeaderMismatchException;

public class Header {
	private String name;

	private String value;

	/**
	 * Constructs a new header.
	 * 
	 * @param header
	 *            if the character ':' (colon) is not found, it will be the name
	 *            of the header. Otherwise, this constructor parses the header
	 *            line.
	 */
	public Header(String header) {
		int colon = header.indexOf(':');
		if (colon == -1)
			name = header;
		else {
			name = header.substring(0, colon);
			value = header.substring(++colon).trim();
		}
	}

	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getRawValue() {
		return value;
	}

	public void setRawValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof String)
			return getName().equals(obj);
		if (obj instanceof Header)
			return getName().equals(((Header) obj).getName());
		return false;
	}

	protected final void checkName(String expected) {
		if (!expected.equalsIgnoreCase(getName()))
			throw new HeaderMismatchException(expected, getName());
	}

	protected final void setName(String name) {
		value = this.name;
		this.name = name;
	}
}
