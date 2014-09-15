package com.tiny.chat.socket;

import com.tiny.chat.domain.FramePacket;

public class MessageFactory {
	private static int sourceId=10;
	private static int destinationId=0;
	public MessageFactory() {
		
	}
	
	/**
	 * 80
	 * 客户端注册请求 
	 * @param type 客户端类型
	 * @return
	 */
	public static FramePacket getClientRegister(byte type){
		byte[] data=new byte[1];
		data[0]=type;
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_CLIENT_REGISTER_REQUEST, data);
		return packet;
	}
	
	/**
	 * 82
	 * 客户端注销请求 
	 * @param type 
	 * @return
	 */
	public static FramePacket getClientUnRegister(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_CLIENT_UNREGISTER_REQUEST, null);
		return packet;
	}
	
	/**
	 * 84
	 * 客户端类型更改请求 
	 * @param type 客户端类型
	 * @return
	 */
	public static FramePacket getClientChangeRegister(byte type){
		byte[] data=new byte[1];
		data[0]=type;
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_CLIENT_CHANGE_DATA_TYPE_REQUEST, data);
		return packet;
	}
	
	/**
	 * 87
	 * 客户端查询自己已注册类型
	 * @return
	 */
	public static FramePacket getClientRegisterType(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_CLIENT_DATA_TYPE_REQUEST, null);
		return packet;
	}
	
	/**
	 * 89
	 * 客户端查询查询友邻支持的网络应用类型
	 * @return
	 */
	public static FramePacket getFriendRegisterType(int desId){
		FramePacket packet = new FramePacket(sourceId, desId,FrameType.LAN_CLIENT_DATA_TYPE_REQUEST, null);
		return packet;
	}
	
	/**
	 * 91
	 * 客户端登录感知传输服务器请求
	 * @param type 请求登录公网或专网
	 * @return
	 */
	public static FramePacket getClientLogin(byte netType){
		byte[] data=new byte[1];
		data[0]=netType;
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_LOGIN_REQUEST, data);
		return packet;
	}
	
	/**
	 * 94
	 * 客户端注销感知传输服务器请求
	 * @param type 请求注销公网或专网
	 * @return
	 */
	public static FramePacket getClientLogout(byte netType){
		byte[] data=new byte[1];
		data[0]=netType;
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_LOGOUT_REQUEST, data);
		return packet;
	}
	
	/**
	 * 97
	 * 客户端查询自己已登录网络类型 
	 * @return
	 */
	public static FramePacket getClientNetType(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_NET_TYPE_REQUEST, null);
		return packet;
	}

	/**
	 * 101
	 * 客户端查询个人资料请求
	 * @return
	 */
	public static FramePacket getClientInfo(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_PERSONAL_INFORMATION_REQUEST, null);
		return packet;
	}
	
	/**
	 * 103
	 * 客户端查询友邻列表请求
	 * @return
	 */
	public static FramePacket getFriendList(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_FRIEND_LIST_REQUEST, null);
		return packet;
	}
	
	/**
	 * 105
	 * 客户端查询单个友邻资料请求
	 * @return
	 */
	public static FramePacket getFriendInfo(int desId){
		FramePacket packet = new FramePacket(sourceId, desId,FrameType.LAN_FRIEND_INFORMATION_REQUEST, null);
		return packet;
	}
	
	/**
	 * 107
	 * 客户端查询自身位置信息请求
	 * @return
	 */
	public static FramePacket getClientPosition(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_POSITION_REQUEST, null);
		return packet;
	}
	
	/**
	 * 109
	 * 客户端查询自身环境信息请求
	 * @return
	 */
	public static FramePacket getClientEnvironment(){
		FramePacket packet = new FramePacket(sourceId, destinationId,FrameType.LAN_ENVIROMENT_REQUEST, null);
		return packet;
	}
	
	
}
