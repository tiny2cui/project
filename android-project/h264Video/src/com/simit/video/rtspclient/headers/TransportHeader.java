
package com.simit.video.rtspclient.headers;

import java.util.Arrays;
import java.util.List;

import com.simit.video.rtspclient.concepts.Header;


/**
 * Models a "Transport" header from RFC 2326. According to specification, there may be parameters, which will be inserted as a list of strings, which follow below:
 * <code>
   parameter           =    ( "unicast" | "multicast" )
                       |    ";" "destination" [ "=" address ]
                       |    ";" "interleaved" "=" channel [ "-" channel ]
                       |    ";" "append"
                       |    ";" "ttl" "=" ttl
                       |    ";" "layers" "=" 1*DIGIT
                       |    ";" "port" "=" port [ "-" port ]
                       |    ";" "client_port" "=" port [ "-" port ]
                       |    ";" "server_port" "=" port [ "-" port ]
                       |    ";" "ssrc" "=" ssrc
                       |    ";" "mode" = <"> 1\#mode <">
   ttl                 =    1*3(DIGIT)
   port                =    1*5(DIGIT)
   ssrc                =    8*8(HEX)
   channel             =    1*3(DIGIT)
   address             =    host
   mode                =    <"> *Method <"> | Method
   </code>
 * @author paulo
 *
 */
public class TransportHeader extends Header
{
	public static final String NAME = "Transport";

	public static enum LowerTransport {
		TCP, UDP, DEFAULT
	};

	private LowerTransport transport;

	private List<String> parameters;

	public TransportHeader(String header)
	{
		super(header);
		String value = getRawValue();
		if(!value.startsWith("RTP/AVP"))
			throw new IllegalArgumentException("Missing RTP/AVP");
		int index = 7;
		if(value.charAt(index) == '/')
		{
			switch(value.charAt(++index))
			{
			case 'T':
				transport = LowerTransport.TCP;
				break;
			case 'U':
				transport = LowerTransport.UDP;
				break;
			default:
				throw new IllegalArgumentException("Invalid Transport: "
						+ value.substring(7));
			}
			index += 2;
		} else
			transport = LowerTransport.DEFAULT;
		++index;
		if(value.charAt(index) != ';' || index != value.length())
			throw new IllegalArgumentException("Parameter block expected");
		addParameters(value.substring(++index).split(";"));
	}

	public TransportHeader(LowerTransport transport, String... parameters)
	{
		super(NAME);
		this.transport = transport;
		addParameters(parameters);
	}

	void addParameters(String[] parameterList)
	{
		if(parameters == null)
			parameters = Arrays.asList(parameterList);
		else
			parameters.addAll(Arrays.asList(parameterList));
	}

	LowerTransport getTransport()
	{
		return transport;
	}

	public String getParameter(String part)
	{
		for(String parameter : parameters)
			if(parameter.startsWith(part))
				return parameter;
		throw new IllegalArgumentException("No such parameter named " + part);
	}

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder(NAME).append(": ").append("RTP/AVP");
		if(transport != LowerTransport.DEFAULT)
			buffer.append('/').append(transport);
		for(String parameter : parameters)
			buffer.append(';').append(parameter);
		return buffer.toString();
	}
}
