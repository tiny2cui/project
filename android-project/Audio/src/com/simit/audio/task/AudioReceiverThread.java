package com.simit.audio.task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.simit.audio.config.AudioConfig;
import com.simit.audio.config.NetConfig;
import com.simit.audio.util.Tags;

import android.util.Log;

/**
 * 音频接收器
 * @author CAS
 *
 */
public class AudioReceiverThread implements Runnable {
	private static AudioReceiverThread receiver;
	/**
	 * 接收客户端的ip
	 */
	private String clientIp;
	/**
	 * 接收客户端的端口
	 */
	private int clientPort;
	/**
	 * 接收客户端的socket
	 */
	private DatagramSocket socket;
	/**
	 * 接收客户端的数据包
	 */
	private DatagramPacket receivePkt;
	/**
	 * 接收客户端的数据
	 */
	private byte[] packetBuf;
	/**
	 * 控制【接收】线程结束
	 */
	private boolean isReceiving = false;

	/**
	 * 解码器
	 */
	private AudioDecoderThread decoder;

	public static AudioReceiverThread getInstance() {
		if (receiver == null) {
			receiver = new AudioReceiverThread();
		}
		return receiver;
	}
	/**
	 * 初始化接收客户端的参数
	 */
	private AudioReceiverThread() {
		Log.d(Tags.AudioDemo.toString(), "AudioReceiver = " + "开启接收器");
		
		if (socket == null || receivePkt == null) {
			try {
				
				clientIp = NetConfig.CLIENT_IP;
				clientPort = NetConfig.CLIENT_PORT;
				
				/*socket = new DatagramSocket(clientPort + 1, InetAddress.getByName(clientIp));*/
				
				// 指定另一个客户端（两台客户端）
				socket = new DatagramSocket(NetConfig.SERVER_PORT);
				
				Log.d(Tags.UDP.toString(), "AudioReceiver LocalAddress=" + socket.getLocalAddress() + 
						" " + "LocalPort=" + socket.getLocalPort() +
						" " + "Address=" + socket.getInetAddress() + 
						" " + "Port=" + socket.getPort()
						);
				
				packetBuf = new byte[AudioConfig.packetSize];
				receivePkt = new DatagramPacket(packetBuf, packetBuf.length);
				
			} catch (SocketException e) {
				e.printStackTrace();
			} 
//			catch (UnknownHostException e) {
//				e.printStackTrace();
//			}
		}
	}

	public void startRecieving() {
		if (null == receiver || null == socket || null == receivePkt) {
			Log.d(Tags.AudioDemo.toString(), "AudioReceiver error = "
					+ "initial receiver error!");
			return;
		}

		// 启动解码器
		decoder = AudioDecoderThread.getInstance();
		decoder.startDecoding();

		// 开始接收
		Log.d(Tags.AudioDemo.toString(), "AudioReceiver = " + "开始接收");
		this.isReceiving = true;
		new Thread(this).start();
	}

	public void stopRecieving() {
		// 停止接收
		isReceiving = false;
		Log.d(Tags.AudioDemo.toString(), "AudioReceiver = " + "停止接收");
		
		// 停止解码
		decoder.stopDecoding();
		decoder.release();
	}

	public boolean isRecieving() {
		return this.isReceiving;
	}

	public void release() {
		if (receivePkt != null) {
			receivePkt = null;
		}
		if (socket != null) {
			socket.close();
			socket = null;
		}
		receiver = null;
	}

	public void run() {
		while (isReceiving) {
			
			Log.d(Tags.Threader.toString(), "AudioReceiver = " + "接收ing");
			
			try {
				if(socket != null) {
					socket.receive(receivePkt);
				}
				
				// 接收Log：当调试开启Log，调试完毕关闭Log，不关闭会影响音频的连贯性
//				Log.d(Tags.UDP.toString(), "AudioReceiver　HostAddress＝" + receivePkt.getAddress().getHostAddress() + 
//						" " + "Port＝" + receivePkt.getPort() + 
//						" " + "length＝" + receivePkt.getLength());
//			
//				String buffer = "[";
//				for(byte b : receivePkt.getData()) {
//					buffer += b + " ";
//				}
//				buffer += "]";
//				Log.d(Tags.UDP.toString(), "接收Data=" + buffer);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(receivePkt != null) {
				// 等待解码
				decoder.addData(receivePkt.getData(), receivePkt.getLength());
			}
			
		}

		/*// 停止解码
		decoder.stopDecoding();
		
		release();*/
	}

}