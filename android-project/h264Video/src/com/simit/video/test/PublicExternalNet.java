package com.simit.video.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
public class PublicExternalNet  {

	// "m02.simit.com";

	private String TAG="PublicExternalNet";
	private final int PUBLIC_SERVER_UDP_PORT=6665;
	
    private DatagramSocket udpSendSocket;	
	private DatagramPacket udpSendPacket;
	private DatagramPacket udpRecvPacket;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ListenUDPPortThread udpReceiveThread;
	private Handler mHandler;
	
	private String ipString="192.168.1.100";
	private int				deveiceId;
	private String			name="com";
	private String          password="12345678";
	private int          userId;
//	private LocalProperty localProperty=M02Application.getInstance().getLocalProperty();;

	/* 接收缓存器大小 */
	private final int RECV_BUFFER_LENGTH = 1024;

	/* 接收缓存区 */
	private byte[] recvBuf = new byte[RECV_BUFFER_LENGTH];
	public PublicExternalNet() {
		
			HandlerThread mHandlerThread = new HandlerThread("simit");
			mHandlerThread.start();
			deveiceId=7;
			userId=7;
			mHandler = new Handler(mHandlerThread.getLooper());
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					try{ 
						socket = new Socket(Constants.PUBLIC_SERVER_ADDRESS, Constants.PUBLIC_PORT);
						udpSendSocket=new DatagramSocket();
						if(socket.isConnected()){
							inputStream = socket.getInputStream();
							outputStream = socket.getOutputStream();
							new ListenPortThread().start();
							//byte[] frameByte = NetCommunicateProtocal.packNotification(localProperty.getLocalId(), 0,Constants.CODE_LOGIN);
//							byte[] ip=IPv4Util.ipToBytesByInet(localProperty.getIpAddress());
//							byte[] frameByte = PackageFactory.packLoginServer(localProperty.getUserId(), localProperty.getPassword(), ip, localProperty.getLocalId(), 1);
//							sendTCP(0, frameByte, frameByte.length);
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}

				}
			});
	}

	
	public void login() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				byte[] ip=IPv4Util.ipToBytesByInet(ipString);
				byte[] frameByte = PackageFactory.packLoginServer(userId, password.getBytes(), ip, deveiceId, 1);
				sendTCP(0, frameByte, frameByte.length);	
				Log.i(TAG, "login server");
			} 
		});

	}

	
	public void logout() {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				byte[] frameByte = PackageFactory.packNotification(deveiceId, 0, Constants.CODE_LOGOUT);
				sendTCP(0, frameByte, frameByte.length);
				try {
					if(socket!=null){
						socket.close();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (Exception e){
					e.printStackTrace();
				}
				
				byte[] commond=new byte[1];
				commond[0]=0x01;
				frameByte = PackageFactory.packUdpData(commond, deveiceId, 1);
				sendUDP(frameByte, frameByte.length);
//				//停止监听UDP数据接收
//				if(udpReceiveThread!=null){
//					udpReceiveThread.setRunThread(false);
//					udpReceiveThread=null;
//				}
				
			}
		});
			
		
	}

	
	public void receive(byte[] data, int dataLen, String ip) {
		// 处理获取到的每帧数据
		FramePacket framePacket = new FramePacket(data, data.length);
		framePacket.setIpAddress(ip);
		if(framePacket.getFrameType()==FrameType.INTENET_LOGIN_RESPONSE){
			byte[] result=framePacket.getData();
			if(result[0]==0x02){
				byte[] send=new byte[1];
				send[0]=0x01;
				byte[] frameByte = PackageFactory.packUdpData(send, deveiceId, 1);
				sendUDP(frameByte, frameByte.length);
				
			}
		}
		
		
	}

	
	public void send(int id, byte[] data, int dataLen) {
		
		sendUDP(data, dataLen);
		
	}
	

	private void sendTCP(int id, byte[] data, int dataLen){
		try {
			if(outputStream!=null){
				outputStream.write(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendUDP(byte[] data, int dataLen){

		try {
			
			udpSendPacket = new DatagramPacket(data, dataLen,
					InetAddress.getByName(Constants.PUBLIC_SERVER_ADDRESS), PUBLIC_SERVER_UDP_PORT);
			udpSendSocket.send(udpSendPacket);
		Log.i(TAG, "send UDP data-->"+TypeConvert.bytes2String(data));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	 class ListenPortThread extends Thread {

		private byte[] readBuf;
		private final int bufSize = 2500;
		private int readSize;

		public ListenPortThread() {
			readBuf = new byte[bufSize];
		}

		public void run() {
			while (true) {
				try {  
					readSize = inputStream.read(readBuf, 0, bufSize);	
					if(readSize!=-1){
						AnalyzeReadBuffer(readBuf, readSize);
					}
					
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}

		private void AnalyzeReadBuffer(byte[] buf, int size) {
			receive(buf, size, null);
		}
	}
	
	class ListenUDPPortThread extends Thread {
		private boolean runThread = true;

		public ListenUDPPortThread() {

		}

		public void run() {
			try {
				while (runThread) {
					udpRecvPacket = new DatagramPacket(recvBuf,
							RECV_BUFFER_LENGTH);
					udpSendSocket.receive(udpRecvPacket);
					receive(udpRecvPacket.getData(), udpRecvPacket.getLength(),
							udpRecvPacket.getAddress().getHostAddress());
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void setRunThread(boolean runThread) {
			this.runThread = runThread;
		}
	}	
	public void sendFrame(FramePacket framePacket) {
		try {
			if(outputStream!=null){
				outputStream.write(framePacket.getFramePacket());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
