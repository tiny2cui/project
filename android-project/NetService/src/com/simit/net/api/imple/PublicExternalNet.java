package com.simit.net.api.imple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.simit.net.NetConfig;
import com.simit.net.api.IExternalNet;
import com.simit.net.domain.FramePacket;
import com.simit.net.domain.LocalProperty;
import com.simit.net.domain.NetType;
import com.simit.net.task.ReceivedDataThread;
import com.simit.net.utils.Constants;
import com.simit.net.utils.FrameType;
import com.simit.net.utils.IPv4Util;
import com.simit.net.utils.MyLog;
import com.simit.net.utils.PackageFactory;
import com.simit.net.utils.TypeConvert;

public class PublicExternalNet implements IExternalNet {

	// "m02.simit.com";

	private String TAG="PublicExternalNet";
	private final int PUBLIC_SERVER_UDP_PORT=6665;
    private DatagramSocket udpSendSocket;	
	private DatagramPacket udpSendPacket;
	private DatagramPacket udpRecvPacket;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	ReceivedDataThread receivedDataThread = null;
	private ListenUDPPortThread udpReceiveThread;
//	private Handler mHandler;
	private LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();;

	private Context mContext;
	/* 接收缓存器大小 */
	private final int RECV_BUFFER_LENGTH = 1500;

	/* 接收缓存区 */
	private byte[] recvBuf = new byte[RECV_BUFFER_LENGTH];
	public PublicExternalNet() {
		
//			HandlerThread mHandlerThread = new HandlerThread("simit");
//			mHandlerThread.start();
//			mHandler = new Handler(mHandlerThread.getLooper());
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try{
					if(socket!=null){
						socket.close();
					}
					socket = new Socket(Constants.PUBLIC_SERVER_ADDRESS, Constants.PUBLIC_PORT);
					udpSendSocket=new DatagramSocket();
					if(socket.isConnected()){
						inputStream = socket.getInputStream();
						outputStream = socket.getOutputStream();
						new ListenPortThread().start();
						
						//byte[] frameByte = NetCommunicateProtocal.packNotification(localProperty.getLocalId(), 0,Constants.CODE_LOGIN);
//						byte[] ip=IPv4Util.ipToBytesByInet(localProperty.getIpAddress());
//						byte[] frameByte = PackageFactory.packLoginServer(localProperty.getUserId(), localProperty.getPassword(), ip, localProperty.getLocalId(), 1);
//						sendTCP(0, frameByte, frameByte.length);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					//TODO 连接服务器失败
				}
				return null;
			}
			
		}.execute();
//		BaseThread.getInstance().postTask(new Runnable() {
//				
//				@Override
//				public void run() {
//					try{
//						socket = new Socket(Constants.PUBLIC_SERVER_ADDRESS, Constants.PUBLIC_PORT);
//						udpSendSocket=new DatagramSocket();
//						if(socket.isConnected()){
//							inputStream = socket.getInputStream();
//							outputStream = socket.getOutputStream();
//							new ListenPortThread().start();
//							//byte[] frameByte = NetCommunicateProtocal.packNotification(localProperty.getLocalId(), 0,Constants.CODE_LOGIN);
////							byte[] ip=IPv4Util.ipToBytesByInet(localProperty.getIpAddress());
////							byte[] frameByte = PackageFactory.packLoginServer(localProperty.getUserId(), localProperty.getPassword(), ip, localProperty.getLocalId(), 1);
////							sendTCP(0, frameByte, frameByte.length);
//						}
//					}catch(Exception ex){
//						ex.printStackTrace();
//						//TODO 连接服务器失败
//					}
//
//				}
//			});
	}

	@Override
	public void login(Context context) {
		this.mContext=context;
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				byte[] ip=IPv4Util.ipToBytesByInet(localProperty.getIpAddress());
				byte[] frameByte = PackageFactory.packLoginServer(localProperty.getUserId(), localProperty.getPassword(), ip, localProperty.getDeveiceId(), 1);
				sendTCP(0, frameByte, frameByte.length);
				
//				frameByte = PackageFactory.packNotification(localProperty.getDeveiceId(), 1, FrameType.INTENET_ONLINE_DEVICE_REQUEST);						
//				sendTCP(0,frameByte, frameByte.length);
				MyLog.i(TAG, "login server");
				return null;
			}
			
		}.execute();
		
//		BaseThread.getInstance().postTask(new Runnable() {
//			@Override
//			public void run() {
//				byte[] ip=IPv4Util.ipToBytesByInet(localProperty.getIpAddress());
//				byte[] frameByte = PackageFactory.packLoginServer(localProperty.getUserId(), localProperty.getPassword(), ip, localProperty.getDeveiceId(), 1);
//				sendTCP(0, frameByte, frameByte.length);	
//				MyLog.i(TAG, "login server");
//			} 
//		});

	}

	@Override
	public void logout(final Context context) {
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				byte[] frameByte = PackageFactory.packNotification(NetConfig.getInstance().getLocalProperty().getDeveiceId(), 0, Constants.CODE_LOGOUT);
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
				frameByte = PackageFactory.packUdpData(commond, localProperty.getDeveiceId(), 1);
				sendUDP(frameByte, frameByte.length);
				//停止监听UDP数据接收
				if(udpReceiveThread!=null){
					udpReceiveThread.setRunThread(false);
					udpReceiveThread=null;
				}
				Intent intent=new Intent();
				intent.setAction(Constants.INTERIOR_SERVER_STOP);
				context.sendBroadcast(intent);
				return null;
			}
			
		}.execute();
//		BaseThread.getInstance().postTask(new Runnable() {
//			
//			@Override
//			public void run() {
//				byte[] frameByte = PackageFactory.packNotification(NetConfig.getInstance().getLocalProperty().getDeveiceId(), 0, Constants.CODE_LOGOUT);
//				sendTCP(0, frameByte, frameByte.length);
//				try {
//					if(socket!=null){
//						socket.close();
//					}
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//				
//				byte[] commond=new byte[1];
//				commond[0]=0x01;
//				frameByte = PackageFactory.packUdpData(commond, localProperty.getDeveiceId(), 1);
//				sendUDP(frameByte, frameByte.length);
//				//停止监听UDP数据接收
//				if(udpReceiveThread!=null){
//					udpReceiveThread.setRunThread(false);
//					udpReceiveThread=null;
//				}
//				
//			}
//		});
			
		
	}

	@Override
	public void receive(byte[] data, int dataLen, String ip) {
		// 处理获取到的每帧数据
		FramePacket framePacket = new FramePacket(data, data.length);
		framePacket.setIpAddress(ip);
		if(framePacket.getFrameType()==FrameType.INTENET_LOGIN_RESPONSE){
			byte[] result=framePacket.getData();
			if(result[0]==0x01 || result[0]==0x02 || result[0]==0x03){
				byte[] send=new byte[1];
				send[0]=0x01;
				byte[] frameByte = PackageFactory.packUdpData(send, localProperty.getDeveiceId(), 1);
				sendUDP(frameByte, frameByte.length);
				
				Intent intent=new Intent();
				intent.setAction(Constants.INTERIOR_SERVER_STOP);
				mContext.sendBroadcast(intent);
				
				udpReceiveThread=new ListenUDPPortThread();
				udpReceiveThread.start();
			}
		}
		
		if (receivedDataThread == null) {
			receivedDataThread = new ReceivedDataThread();
			receivedDataThread.addFramePacket(framePacket);
			receivedDataThread.start();
		} else {
			receivedDataThread.addFramePacket(framePacket);
		}
	}

	@Override
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
			if(udpSendSocket!=null){
				udpSendSocket.send(udpSendPacket);
				MyLog.i(TAG, "send UDP data-->"+TypeConvert.bytes2String(data));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
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
					}else{
						try {
							this.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}catch (Exception e) {					
					e.printStackTrace();
					MyLog.i(TAG, "链接断开");
					break;
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

	@Override
	public NetType getNetType() {
		// TODO Auto-generated method stub
		return NetType.INTERNET;
	}

	@Override
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
