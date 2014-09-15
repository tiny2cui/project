package com.simit.net.api.imple;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.simit.net.NetConfig;
import com.simit.net.api.IExternalNet;
import com.simit.net.domain.FramePacket;
import com.simit.net.domain.FriendIpRecord;
import com.simit.net.domain.NetType;
import com.simit.net.task.ReceivedDataThread;
import com.simit.net.utils.Constants;
import com.simit.net.utils.MyLog;
import com.simit.net.utils.PackageFactory;

/**
 * 私有局域网内通信
 * 
 * @author wen.cui
 */
public class PrivateExternalNet implements IExternalNet {

	private String TAG = "PrivateExternalNet";
	/* UDP */
	private DatagramSocket udpSendSocket;
	private DatagramSocket udpRecvSocket;
	private DatagramPacket udpSendPacket;
	private DatagramPacket udpRecvPacket;

	/* 侦听线程 */
	private ListenPortThread listenPortThread;

	/* 接收缓存器大小 */
	private final int RECV_BUFFER_LENGTH = 1500;

	/* 接收缓存区 */
	private byte[] recvBuf = new byte[RECV_BUFFER_LENGTH];

	ReceivedDataThread receivedDataThread = null;

	public PrivateExternalNet() {

		try {

			udpSendSocket = new DatagramSocket();
			udpRecvSocket = new DatagramSocket(Constants.LISTEN_PORT);
			listenPortThread = new ListenPortThread();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void login(final Context context) {
		// TODO Auto-generated method stub
		if (listenPortThread == null) {
			listenPortThread = new ListenPortThread();
		}
		listenPortThread.start();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// 通知所有用户，本人已上线
				byte[] frameByte;
				NetConfig app = NetConfig.getInstance();
				frameByte = PackageFactory.packNotification(app.getLocalProperty().getDeveiceId(), 0,Constants.CODE_LOGIN);
				boolean loginSuccess=sendOut(frameByte, frameByte.length,Constants.BROABCAST_ADDRESS);
				Intent intent=new Intent();
				if(loginSuccess){
					intent.setAction(Constants.INTERIOR_SERVER_START);
				}else{
					intent.setAction(Constants.INTERIOR_SERVER_START_FAIL);
				}
				context.sendBroadcast(intent);
				
				//获取在线好友列表
//				frameByte = PackageFactory.packNotification(app.getLocalProperty().getDeveiceId(), 0,FrameType.LAN_FRIEND_LIST_REQUEST);
//				sendOut(frameByte, frameByte.length,Constants.BROABCAST_ADDRESS);
				return null;
			}

		}.execute();

	}

	@Override
	public void logout(final Context context) {
		// TODO Auto-generated method stub
		listenPortThread.setRunThread(false); // 停止线程

		// 通知所有用户，本人已下线
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				byte[] frameByte;
				NetConfig app = NetConfig.getInstance();
				frameByte = PackageFactory.packNotification(app.getLocalProperty().getDeveiceId(), 0,Constants.CODE_LOGOUT);
				sendOut(frameByte, frameByte.length,Constants.BROABCAST_ADDRESS);
				if (udpSendSocket != null) {
					udpSendSocket.close();
				}
				if (udpRecvSocket != null) {
					udpRecvSocket.close();
				}
				Intent intent=new Intent();
				intent.setAction(Constants.INTERIOR_SERVER_STOP);
				context.sendBroadcast(intent);
				return null;
			}

		}.execute();

	}

	@Override
	public void receive(byte[] data, int dataLen, String ip) {
		FramePacket framePacket = new FramePacket(data, data.length);
		framePacket.setIpAddress(ip);
		// 处理获取到的每帧数据
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

		String ipAddrString = null;
		NetConfig app = NetConfig.getInstance();
		/*
		 * 通过ID获取IP，如果ID为空，则广播
		 */
		if (id != 0) {
			FriendIpRecord friendIpRecord = app.getFriendIpRecord();
			ipAddrString = friendIpRecord.getIp(id);

		} else {
			String tempIpAddr = app.getLocalProperty().getIpAddress();
			int pos = tempIpAddr.lastIndexOf('.');
		}

		try {
			// udpSendPacket = new DatagramPacket(data, dataLen,new
			// InetSocketAddress(ipAddrString, Constants.LISTEN_PORT));
			udpSendPacket = new DatagramPacket(data, dataLen,
					InetAddress.getByName(ipAddrString), Constants.LISTEN_PORT);
			if (udpSendSocket != null) {
				if (Constants.BROABCAST_ADDRESS.equals(ipAddrString)) {
					udpSendSocket.setBroadcast(true);
					MyLog.i(TAG, "发送广播消息");
				} else {
					udpSendSocket.setBroadcast(false);
				}
				udpSendSocket.send(udpSendPacket);
			}

		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public NetType getNetType() {
		// TODO Auto-generated method stub
		return NetType.WSN;
	}

	class ListenPortThread extends Thread {
		private boolean runThread = true;

		public ListenPortThread() {

		}

		public void run() {
			try {
				// DatagramSocket udpRecvSocket = new
				// DatagramSocket(Constants.LISTEN_PORT);
				while (runThread) {
					udpRecvPacket = new DatagramPacket(recvBuf,RECV_BUFFER_LENGTH);
					udpRecvSocket.receive(udpRecvPacket);
					receive(udpRecvPacket.getData(), udpRecvPacket.getLength(),udpRecvPacket.getAddress().getHostAddress());
				}

			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		public void setRunThread(boolean runThread) {
			this.runThread = runThread;
			if (udpRecvSocket != null) {
				udpRecvSocket.close();
			}

		}
	}

	/**
	 * 发送
	 * 
	 * @param framePacket
	 */
	private boolean sendOut(byte[] frame, int frameLength, String ipAddrString) {

		try {
			// udpSendPacket = new DatagramPacket(frame, frameLength,new
			// InetSocketAddress(ipAddrString, Constants.LISTEN_PORT));
			udpSendPacket = new DatagramPacket(frame, frameLength,
					InetAddress.getByName(ipAddrString), Constants.LISTEN_PORT);
			if (Constants.BROABCAST_ADDRESS.equals(ipAddrString)) {
				udpSendSocket.setBroadcast(true);
				MyLog.i(TAG, "发送广播消息");
			} else {
				udpSendSocket.setBroadcast(false);
			}
			udpSendSocket.send(udpSendPacket);
			return true;
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public void sendFrame(FramePacket framePacket) {
		int id = framePacket.getDestineID(); // 目标id为0，表示广播
		if (id == 0) {
			sendOut(framePacket.getFramePacket(), framePacket.getFrameLength(),
					Constants.BROABCAST_ADDRESS);
		} else {
			FriendIpRecord ipRecord = NetConfig.getInstance()
					.getFriendIpRecord();
			String ip = ipRecord.getIp(id);
			if (ip != null) {
				sendOut(framePacket.getFramePacket(),
						framePacket.getFrameLength(), ip);
			}

		}

	}

}
