package com.simit.net.task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.simit.net.NetConfig;
import com.simit.net.domain.Client;
import com.simit.net.domain.ClientTypeRecord;
import com.simit.net.domain.FramePacket;
import com.simit.net.domain.FriendIpRecord;
import com.simit.net.domain.LocalServerInfo;
import com.simit.net.domain.NetType;
import com.simit.net.domain.RegisterType;
import com.simit.net.utils.Constants;
import com.simit.net.utils.FrameType;
import com.simit.net.utils.IPv4Util;
import com.simit.net.utils.MyLog;
import com.simit.net.utils.PackageFactory;
import com.simit.net.utils.TypeConvert;

/**
 * 处理接收的数据线程
 * @author wen.cui
 *
 */
public class ReceivedDataThread extends Thread {

	private String TAG="ReceivedDataThread";
	private boolean runTask=true;
	FramePacket								framePacket;
	private DatagramSocket udpSocket;
	private ArrayList<FramePacket>			framePackets;
    private final Semaphore sync;

	public ReceivedDataThread(){
		try {
			udpSocket=new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		framePackets = new ArrayList<FramePacket>();
		sync=new Semaphore(0);
	}
	

	public void addFramePacket(FramePacket framePacket){
		framePackets.add(framePacket);
		sync.release();
		
	}
	
	public void run(){

		while(runTask){
			try {
				sync.acquire(1);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			if(framePackets!=null && framePackets.size()>0){
				framePacket = framePackets.remove(0);
				if(framePacket.getSourceID()!=NetConfig.getInstance().getLocalProperty().getDeveiceId()){
					int frameType = framePacket.getFrameType();
					MyLog.i(TAG, "receive type-->"+frameType+"\n data-->"+TypeConvert.bytes2String(framePacket.getFramePacket()));
					byte clientType=0;
					switch (frameType) {
					case FrameType.INFO_MESSAGE:			//短消息
						clientType=TypeConvert.short2byte(RegisterType.TEXT)[1];
						sendData2Client(framePacket,clientType);
						break; 
					case FrameType.INFO_AUDIO:			//音频
					    clientType=TypeConvert.short2byte(RegisterType.VOICE)[1];
						sendData2Client(framePacket,clientType);
						break;
					case FrameType.INFO_VIDEO:			//视频
						clientType=TypeConvert.short2byte(RegisterType.VIDEO)[1];
						sendData2Client(framePacket,clientType);
						break;
					case FrameType.INFO_FILE:			//文件
						clientType=TypeConvert.short2byte(RegisterType.FILE)[1];
						sendData2Client(framePacket,clientType);
						break;
					case FrameType.INTENET_LOGIN_RESPONSE:			//登录服务器结果
						handLoginResult(framePacket);
						break;
					case FrameType.LAN_CHANGE_DATA_TYPE_BROADCAST:			//更改客户端类型通知
						onFriendRegisterTypeChanged(framePacket);
						break;
						
					case FrameType.LAN_SUPPORT_DATA_TYPE_REQUEST:			// 查询友邻网络服务支持类型
						onQueryServiceSupportType(framePacket);
						break;

					case FrameType.LAN_LOGIN_BROADCAST:			// 网络登录通知
						onFriendLogin(framePacket);
						break;
						
					case FrameType.LAN_LOGOUT_BROADCAST:			// 网络登出通知
						onFriendLogout(framePacket);
						break;
						
					case FrameType.LAN_PERSONAL_INFORMATION:			// 自身资料
						sendData2Clients(framePacket);
						break;	
					
					case FrameType.LAN_FRIEND_INFORMATION:			// 友邻资料
						sendData2Clients(framePacket);
						break;	
					case FrameType.LAN_FRIEND_LIST_REQUEST:			// 私有网络获取友邻列表请求
						//onResponseInfo(framePacket);
						onResponseFriendList(framePacket);
						break;	
					case FrameType.LAN_FRIEND_INFORMATION_REQUEST:			// 私有网络获取友邻资料请求
						onResponseInfo(framePacket);
						break;
						
					case FrameType.INTENET_ONLINE_DEVICE_RESPONSE:			// 私有网络获取友邻资料请求
						MyLog.i(TAG, "data-->"+framePacket.getFramePacket());
						sendData2Clients(framePacket);
						break;

//					case 104:			// 获取友邻资料
//						OnFriendInfo(framePacket);
//						break;
						
					default:
						sendData2Clients(framePacket);
						break;
					}
				}
				
				
				
			} 
		}
	}
	
	private void handLoginResult(FramePacket framePacket) {
		byte[] data=framePacket.getData();
		NetConfig.getInstance().getLocalProperty().setLoginServerState(data[0]);
		sendData2Clients(framePacket);
		
	}


	/*
	 * 友邻更改注册类型通知
	 * 将内容传输给所有已注册的客户端
	 */
	private void onFriendRegisterTypeChanged(FramePacket framePacket){
		sendData2Clients(framePacket);
	}
	private void sendData2Client(FramePacket framePacket,byte clientType){
		ClientTypeRecord recodeRecord=NetConfig.getInstance().getClientTypeRecord();
		if(recodeRecord!=null){
			Client client=recodeRecord.getClient(clientType);
			byte[] data=framePacket.getFramePacket();
			if(client!=null){
				send2Client(data, data.length, client.getIp(), client.getPort());
			}
			
		}
	}
	
	private void sendData2Clients(FramePacket framePacket){
		ClientTypeRecord recodeRecord=NetConfig.getInstance().getClientTypeRecord();
		if(recodeRecord!=null){
			List<Client> clients=recodeRecord.getClients();
			if(clients!=null && clients.size()>0){
				for(int i=0;i<clients.size();i++){
					Client client=clients.get(i);
					byte[] data=framePacket.getFramePacket();
					send2Client(data, data.length, client.getIp(), client.getPort());
				}
			}
		}
		
	}
	
	/*
	 * 查询网络服务类型
	 * 将结果发送回请求端
	 */
	private void onQueryServiceSupportType(FramePacket framePacket){
		NetConfig app=NetConfig.getInstance();
		byte[]		supportType = new byte[1];
		int			friendId = framePacket.getSourceID();
		
		int			localId = app.getLocalProperty().getDeveiceId();
		
		supportType[0] = app.getLocalProperty().getSupportTypes();
		
		FramePacket		responsePacket = new FramePacket(localId, friendId, 85, supportType);
		responsePacket.setIpAddress(framePacket.getIpAddress());
		sendFrame(responsePacket);
	}
	
	/*
	 * 友邻网络登录通知
	 * 将友邻登录信息发送到已注册的所有客户端
	 */
	private void onFriendLogin(FramePacket framePacket){
		int			friendId = framePacket.getSourceID();
		String ipString=framePacket.getIpAddress();
		//
		FriendIpRecord friendIpRecord=NetConfig.getInstance().getFriendIpRecord();
		friendIpRecord.addFriend((short)friendId, ipString);
		sendData2Clients(framePacket);
		
	}
	
	/*
	 * 友邻网络登出通知
	 */
	private void onFriendLogout(FramePacket framePacket){
		int			friendId = framePacket.getSourceID();
		FriendIpRecord friendIpRecord=NetConfig.getInstance().getFriendIpRecord();
		friendIpRecord.deleteFriend(friendId);
		sendData2Clients(framePacket);
	}
	
	private void onResponseFriendList(FramePacket framePacket){
		NetConfig app=NetConfig.getInstance();
		if(app.getLocalProperty().getNetType()==NetType.INTERNET){

			//公网转发数据
			sendFrame(framePacket);	
		}else{
			FramePacket			framePacketOut = new FramePacket();
			framePacketOut.setFrameSourceID(app.getLocalProperty().getDeveiceId());
			framePacketOut.setFrameDestineID(framePacket.getSourceID());
			framePacketOut.setFrameType(Constants.CODE_LOGIN);
			framePacketOut.setFrameSerialNo(0);
			framePacketOut.setFrameDataLength(0);	
			framePacketOut.packageItemsToFrame();	
			framePacketOut.setIpAddress(framePacket.getIpAddress());
			sendFrame(framePacketOut);	
		}
		
	}
	
	/*
	 *  获取友邻资料请求
	 *  
	 */
	//TODO 未连接到客户端
	private void onResponseInfo(FramePacket framePacket){
		int			friendId = framePacket.getSourceID();
		NetConfig app=NetConfig.getInstance();
		if(app.getLocalProperty().getNetType()==NetType.INTERNET){

			//公网转发数据
			sendFrame(framePacket);	
		}else{
			FramePacket framePacketOut = new FramePacket();
			framePacketOut.setIpAddress(framePacket.getIpAddress());
			//构造本地服务器自身的数据（友邻资料）
			//id username ip 注册类型、网络状态
			MyLog.i(TAG, "from ip--->"+framePacket.getIpAddress());
			String ipAddress=app.getLocalProperty().getIpAddress();
			String userName=app.getLocalProperty().getName();
			int userId=app.getLocalProperty().getUserId();
			int ip=IPv4Util.ipToInt(ipAddress);
			int sourceId=app.getLocalProperty().getDeveiceId();
			List<Client> clients= app.getClientTypeRecord().getClients();
			int type=0;
			if(clients!=null && clients.size()>0){ 
				type=clients.get(0).getClientType();
				for(int i=0;i<clients.size();i++){
					type=(int)type | (int)clients.get(i).getClientType();
				}
				
			}
			
			NetType state = app.getLocalProperty().getNetType();
			byte netState=0;
			if(state==NetType.INTERNET){
				netState=1;
			}else if(state==NetType.WSN){
				netState=2;
			}else{
				netState=0;
				
			}
			//网络类型暂时不传入 枚举转为字节
			byte[] friendData=new byte[40];
			//构建友邻数据
			byte[] id=TypeConvert.short2byte((short)userId);
			byte[] name=userName.getBytes();
			byte[] ipByte=TypeConvert.int2byte(ip);
			int length=name.length>32?32:name.length;
			System.arraycopy(id, 0, friendData, 0, 2);   //友邻id
			System.arraycopy(name, 0, friendData, 2, length); //用户名称
			System.arraycopy(ipByte, 0, friendData, 34, 4); //ip地址
			friendData[38]=(byte)type;  //注册类型
			friendData[39]=(byte)netState; //网络状态

			framePacketOut.setFrameSourceID((short) sourceId);
			framePacketOut.setFrameDestineID((short)friendId);
			framePacketOut.setFrameType(FrameType.LAN_FRIEND_INFORMATION);
			framePacketOut.setFrameSerialNo((byte) 1);
			framePacketOut.setFrameData(friendData, 40);
			framePacketOut.packageItemsToFrame(); // 生成数据帧
			sendFrame(framePacketOut);	
		}
		
		
		
	}
	
	/*
	 * 获得友邻资料
	 * 通知所有已注册客户端更新友邻资料
	 */
	private void OnFriendInfo(FramePacket framePacket){
		int				friendId = framePacket.getSourceID();
		LocalServerInfo		userInfo;
		byte[]			data = framePacket.getData();
		
		/*
		 * 解析data数据
		 */
		
		/* user name - 32个字节 */
		String			userName;
		byte[]			name = new byte[32];
		
		System.arraycopy(data, 0, name, 0, 32);
		userName = name.toString();
		
		/* ip address - 4个字节 */
		String			ipAddrString;
		byte[]			ipAddr = new byte[4];
		byte[]			temp = new byte[1];
		
		System.arraycopy(data, 32, ipAddr, 0, 4);
		
		temp[0] = ipAddr[0];					
		ipAddrString = temp.toString();			// 192
		
		ipAddrString += ".";					// 192.
		
		temp[0] = ipAddr[1];					
		ipAddrString += temp.toString();		//192.168
		
		ipAddrString += ".";					// 192.168.
		
		temp[0] = ipAddr[2];
		ipAddrString += temp.toString();		// 192.168.0
		
		ipAddrString += ".";					// 192.168.0.
		
		temp[0] = ipAddr[3];
		ipAddrString += temp.toString();		// 192.168.0.1
		
		/* RegisterType - 1个字节 */
		RegisterType		registerType = new RegisterType();
		byte[]				registerTypeByte = new byte[1];
		
		System.arraycopy(data, 36, registerTypeByte, 0, 1);
		registerType.SetRegisterType(registerTypeByte[0]);
		
		/* NetType - 1个字节*/
		NetType				netType;
		byte[]				netTypeByte = new byte[1];
		
		System.arraycopy(data, 37, netTypeByte, 0, 1);
		netType = NetType.WSN;
		userInfo=new LocalServerInfo(registerType, netType, friendId, userName, ipAddrString);
	}
	public void setRunTask(boolean runTask) {
		this.runTask = runTask;
	}
	/**
	 *  发送
	 * @param framePacket
	 */
	private void sendOut(byte[] frame, int frameLength, String ipAddrString){
		
		try {
			DatagramPacket udpSendPacket = new DatagramPacket(
					frame, 
					frameLength,
					new InetSocketAddress(ipAddrString, Constants.LISTEN_PORT));
			if(udpSocket!=null){
				udpSocket.send(udpSendPacket);
			}
			
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	} 
	 
	/**
	 *  发送
	 * @param framePacket
	 */
	private void send2Client(byte[] frame, int frameLength, String ipAddrString,int port){
		
		try {
			DatagramPacket udpSendPacket = new DatagramPacket(
					frame, 
					frameLength,
					new InetSocketAddress(ipAddrString, port));
			if(udpSocket!=null){
				udpSocket.send(udpSendPacket);
			}
			
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	} 

	
	private void sendFrame(FramePacket framePacket) {
		int id=framePacket.getDestineID(); //目标id为0，表示广播
		if(id==0){
			sendOut(framePacket.getFramePacket(), framePacket.getFrameLength() ,Constants.BROABCAST_ADDRESS);
		}else{
			//FriendIpRecord ipRecord=NetConfig.getInstance().getFriendIpRecord();
			//String ip=ipRecord.getIp(id);
			String ip=framePacket.getIpAddress();
			MyLog.i(TAG, "to ip--->"+ip);
			if(ip!=null){
				sendOut(framePacket.getFramePacket(), framePacket.getFrameLength() ,ip);
			}
			
		}
		
	}
}
