

package com.simit.video.server;

import java.io.IOException;
import java.net.InetAddress;

import com.simit.video.stream.AbstractPacketizer;
import com.simit.video.test.SendAndReceive;

import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

/**
 *  A MediaRecorder that streams what it records using a packetizer specified with setPacketizer 
 *  Use it just like a regular MediaRecorder except for setOutputFile() that you don't need to call
 */
public abstract class MediaStream extends MediaRecorder implements Stream {

	protected static final String TAG = "MediaStream";
	private int bufferSize=5000;
	private static int id = 0;
	private int socketId;
	private LocalServerSocket lss = null;
	private LocalSocket receiver, sender = null;
	protected AbstractPacketizer packetizer = null;
	protected boolean streaming = false;
	protected String sdpDescriptor;

	// If you mode==MODE_DEFAULT the MediaStream will just act as a regular MediaRecorder
	// By default: mode = MODE_STREAMING and MediaStream forwards data to the packetizer
	public static final int MODE_STREAMING = 0;
	public static final int MODE_DEFAULT = 1;
	protected int mode = MODE_STREAMING;
	
	public MediaStream() {
		super();
		
		try {
			lss = new LocalServerSocket("net.majorkernelpanic.librtp-"+id);
		} catch (IOException e1) {
			//throw new IOException("Can't create local socket !");
		}
		socketId = id;
		id++;
		
	}

	public void setDestination(InetAddress dest, int dport) {
		this.packetizer.setDestination(dest, dport);
	}
	
	public void setSender(SendAndReceive send) {
		this.packetizer.setSender(send);
	}
	
	public int getDestinationPort() {
		return 5006;
		//return this.packetizer.getRtpSocket().getPort();
	}
	
	public int getLocalPort() {
		return this.packetizer.getRtpSocket().getLocalPort();
	}
	
	public void setMode(int mode) throws IllegalStateException {
		if (!streaming) {
			this.mode = mode;
		}
		else {
			throw new IllegalStateException("Can't call setMode() while streaming !");
		}
	}
	
	public AbstractPacketizer getPacketizer() {
		return packetizer;
	}
	
	public boolean isStreaming() {
		return streaming;
	}
	
	public void prepare() throws IllegalStateException,IOException {
		if (mode==MODE_STREAMING) {
			createSockets();
			// We write the ouput of the camera in a local socket instead of a file !			
			// This one little trick makes streaming feasible quiet simply: data from the camera
			// can then be manipulated at the other end of the socket
			setOutputFile(sender.getFileDescriptor());
		}
		super.prepare();
	}
	
	public void start() throws IllegalStateException {
		try {
			super.start();
			if (mode==MODE_STREAMING) {
				// receiver.getInputStream contains the data from the camera
				// the packetizer encapsulates this stream in an RTP stream and send it over the network
				packetizer.setInputStream(receiver.getInputStream());
				packetizer.start();
			}
			streaming = true;
		} catch (IOException e) {
			throw new IllegalStateException("Something happened with the local sockets :/ Start failed");
		} catch (NullPointerException e) {
			throw new IllegalStateException("setPacketizer() should be called before start(). Start failed");
		}
	}
	
	public void stop() {
		if (mode==MODE_STREAMING) packetizer.stop();
		if (streaming) {
			try {
				super.stop();
			}
			catch (IllegalStateException ignore) {}
			catch (RuntimeException ignore) {}
			finally {
				super.reset();
				streaming = false;
				closeSockets();
			}
		}
	}
	
	public int getSSRC() {
		return getPacketizer().getRtpSocket().getSSRC();
	}
	
	public abstract String generateSessionDescriptor()  throws IllegalStateException, IOException;
	
	private void createSockets() throws IOException {
		receiver = new LocalSocket();
		receiver.connect( new LocalSocketAddress("net.majorkernelpanic.librtp-" + socketId ) );
		receiver.setReceiveBufferSize(bufferSize);
		receiver.setSendBufferSize(bufferSize);
		sender = lss.accept();
		sender.setReceiveBufferSize(bufferSize);
		sender.setSendBufferSize(bufferSize); 
	}
	
	private void closeSockets() {
		try {
			sender.close();
			receiver.close();
		} catch (IOException ignore) {}
	}
	
	public void release() {
		stop();
		try {
			lss.close();
		}
		catch (IOException ignore) {}
		super.release();
	}
	
}
