
package com.simit.video.rtspclient.transport;

import com.simit.video.rtspclient.concepts.Message;
import com.simit.video.rtspclient.concepts.Transport;
import com.simit.video.rtspclient.concepts.TransportListener;

/**
 * Auxiliary class to make listener calls.
 * 
 * 
 * 
 */
class SafeTransportListener implements TransportListener
{
	private final TransportListener behaviour;

	public SafeTransportListener(TransportListener theBehaviour)
	{
		behaviour = theBehaviour;
	}

	@Override
	public void connected(Transport t)
	{
		if(behaviour != null)
			try
			{
				behaviour.connected(t);
			} catch(Throwable error)
			{
				behaviour.error(t, error);
			}
	}

	@Override
	public void dataReceived(Transport t, byte[] data, int size)
	{
		if(behaviour != null)
			try
			{
				behaviour.dataReceived(t, data, size);
			} catch(Throwable error)
			{
				behaviour.error(t, error);
			}
	}

	@Override
	public void dataSent(Transport t)
	{
		// TODO Auto-generated method stub
		if(behaviour != null)
			try
			{
				behaviour.dataSent(t);
			} catch(Throwable error)
			{
				behaviour.error(t, error);
			}

	}

	@Override
	public void error(Transport t, Throwable error)
	{
		if(behaviour != null)
			behaviour.error(t, error);
	}

	@Override
	public void error(Transport t, Message message, Throwable error)
	{
		if(behaviour != null)
			behaviour.error(t, message, error);
	}

	@Override
	public void remoteDisconnection(Transport t)
	{
		if(behaviour != null)
			try
			{
				behaviour.remoteDisconnection(t);
			} catch(Throwable error)
			{
				behaviour.error(t, error);
			}
	}

}
