package com.tiny.chat.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.util.Log;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.db.DBOperate;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.domain.SendData;
import com.tiny.chat.service.IPlayNotification;
import com.tiny.chat.utils.MyLog;

/**
 * UDP服务类 负责接收与发送数据
 * @author admin
 *
 */
public class UDPSocketService extends Thread implements Runnable{
	private final String TAG="UDPSocketService";
	
	private Semaphore syncSend,syncReceive;
	private List<SendData> sendDatas;
	private BufferFramePool mBufferFramePool;
	private SendThread sendThread;
	private DataThread dataThread;
	
	private DatagramSocket udpSocket;
	private DatagramPacket receivePacket;
//	private DBOperate mDBOperate;// 新增数据库类可以操作用户数据库和聊天信息数据库
	private  static UDPSocketService service;
	private static int BUFFER_SIZE=1500;
	private byte[] buffer=new byte[BUFFER_SIZE];
	private Context mContext;
	private IPlayNotification iPlayNotification;
	private boolean running=true;
	final byte FRAME_HEAD_FLAG_D5 = (byte) 0xD5;
	final byte FRAME_HEAD_FLAG_C8 = (byte) 0xC8;
	
	public static UDPSocketService getInstance(){
		if(service!=null){
			return service;
		}else{
			service=new UDPSocketService();
			return service;
		}
	}
	
	public UDPSocketService() {
	
		init();
	}
	
	@Override
	public void run() {
		super.run();
		
		try {
			DBOperate mDBOperate=DBOperate.getInstance(mContext);
			mBufferFramePool=new BufferFramePool(mDBOperate,syncReceive );
			udpSocket=new DatagramSocket();
			receivePacket=new DatagramPacket(buffer, BUFFER_SIZE);
			sendThread=new SendThread(syncSend, sendDatas, udpSocket);
			sendThread.running=true;
			sendThread.start();
			mBufferFramePool.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while(running){
			//开启监听线程
			try {
				udpSocket.receive(receivePacket);
				
			} catch (IOException e) {
				stopService();
				e.printStackTrace();
				MyLog.i(TAG, "UDP数据包接收失败！线程停止");
				break;
			}
			if(receivePacket.getLength()==0){
				MyLog.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
				continue;
			}
			byte[] frameData = receivePacket.getData();
			int len = receivePacket.getLength();
			/*
			 * 找到帧头标志
			 */
			if (frameData == null || frameData.length < 11) { // 读入错误或者已读到最后
				continue;
			}
			
			processPacket(frameData,len);
			// 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
			if (receivePacket != null) {
				receivePacket.setLength(BUFFER_SIZE);
			}	
								
		}
		stopService();
	}
	
	private void processPacket(byte[] frameData,int len){
		if ((byte) frameData[0] == FRAME_HEAD_FLAG_D5) { // 第一个字节对上，继续读第二个字节
			if ((byte) frameData[1] == FRAME_HEAD_FLAG_C8) { // 第二个字节也对上，读取后面的帧
				//
				
				FramePacket framePacket = new FramePacket(frameData, len);
				
				framePacket.setIpAddress(receivePacket.getAddress().getHostAddress().toString());
				//iPlayNotification.playNotification();
				mBufferFramePool.addFramePackage(framePacket);
				syncReceive.release();
				
			}
		}
		
		
	}
	
	private void init(){
		sendDatas= Collections.synchronizedList(new LinkedList<SendData>());
		syncSend = new Semaphore(0);
		syncReceive = new Semaphore(0);	
		
	}
	
	//发送消息
	public void postMessage(byte[] data,int len,String ip){		
		sendDatas.add(new SendData(data, len, ip,MessageConstant.PORT));
		syncSend.release();
	}  
	
	//发送消息
	public void postMessage(byte[] data,int len){		
		String ip=BaseApplication.getInstance().getLocalIP();			
		sendDatas.add(new SendData(data, len,ip,MessageConstant.PORT));			
		syncSend.release();
		}
	
	public void postMessage(SendData sendData){		
		//sendData.setDestinationPort(MessageConstant.PORT);
		sendDatas.add(sendData);
		syncSend.release();
	}  
	
	//停止网络接受与发送数据服务
	public void stopService(){
		if(sendThread!=null){
			sendThread.running=false;
			sendThread.interrupt();
			sendThread=null;
		}
		receivePacket=null;
		if(udpSocket!=null){
			udpSocket.close();
			udpSocket=null;
		}
		running=false;
	}
	
	public void startVideoThread(){
		dataThread = new DataThread();
		dataThread.start();
	}
	
	public void stopVideoThread(){
		if(dataThread!=null){
			dataThread.stopThread();
		}
		
	}

	/**
	 * 接收视频数据线程
	 * @author admin
	 *
	 */
	class DataThread extends Thread implements Runnable{
		public boolean running = true;
		private DatagramSocket recDataSocket;
		private DatagramPacket recDataPacket;
		public DataThread() {
			
		}
		
		@Override
		public void run() {
			//接收视频数据
			super.run();
			try {
				recDataSocket=new DatagramSocket(MessageConstant.DATA_PORT);
				recDataPacket=new DatagramPacket(buffer, BUFFER_SIZE);
				while(running){
					//
					recDataSocket.receive(recDataPacket);
					if(recDataPacket.getLength()==0){
						MyLog.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
						continue;
					}
					
					byte[] frameData = recDataPacket.getData();
					int len = receivePacket.getLength();
					/*
					 * 找到帧头标志
					 */
					if (frameData == null || frameData.length < 11) { // 读入错误或者已读到最后
						continue;
					}				
					processPacket(frameData, len);	
					// 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
					if (recDataPacket != null) {
						recDataPacket.setLength(BUFFER_SIZE);
					}	
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
					
		}
		public void stopThread(){
			this.running=false;
		}
	}
	
	
	
	/**
	 * 发送数据线程，负责将缓冲区待发送的内容通过UDP发送出去
	 * @author admin
	 *
	 */
	class SendThread extends Thread implements Runnable{
		public boolean running = true;
		private final Semaphore sync;
		private final List<SendData> sendDatas;
		private final DatagramSocket socket;
		private DatagramPacket packet;
		private SendData sendData;
		public SendThread(Semaphore sync,List<SendData> send,DatagramSocket socket ) {
			this.sync=sync;
			this.sendDatas=send;
			this.socket=socket;
			packet=new DatagramPacket(new byte[1024], 1024);
		}
		
		
		@Override
		public void run(){
			super.run();
			try{
				while(running){
					sync.acquire(1);
					sendData=sendDatas.remove(0);
					packet.setData(sendData.getData());
					packet.setLength(sendData.getLen());
					packet.setAddress(InetAddress.getByName(sendData.getDestinationIP()));
					packet.setPort(sendData.getDestinationPort());
					MyLog.i("send data", "len--->"+sendData.getLen()+"ip-->"+sendData.getDestinationIP()+"port-->"+sendData.getDestinationPort());
					socket.send(packet);
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG,"IOException: "+e.getMessage());
				e.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void setContext(Context context){
		this.mContext=context;
	}
	
	public void setPlayNotification(IPlayNotification pNotification){
		this.iPlayNotification=pNotification;
	}
	
}
