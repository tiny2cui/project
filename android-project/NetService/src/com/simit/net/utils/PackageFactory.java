package com.simit.net.utils;

import com.simit.net.domain.FramePacket;
import com.simit.net.utils.FrameType;
import com.simit.net.utils.TypeConvert;
/**
 *  网络通信协议类
 *  
 *  用于封装各类网络通信协议
 *  
 * @author admin
 *
 */
public class PackageFactory {
	
	public PackageFactory(){
		
	}
	
	/**
	 * 内网登录操作
	 * @param sourceID
	 * @param destineID
	 * @param frameType 93登录、96登出
	 * @return
	 */
	public static byte[] packNotification(int sourceID, int destineID,int frameType){
		FramePacket			framePacket = new FramePacket();
		framePacket.setFrameSourceID(sourceID);
		framePacket.setFrameDestineID(destineID);
		framePacket.setFrameType(frameType);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(0);	
		framePacket.packageItemsToFrame();	
		return framePacket.getFramePacket();
	}
	
	/*
	 * 登出信息
	 */
	public static byte[] packLogoutNotification(int sourceID, int destineID){
		FramePacket			framePacket = new FramePacket();
		framePacket.setFrameSourceID(sourceID);
		framePacket.setFrameDestineID(destineID);
		framePacket.setFrameType(FrameType.LAN_LOGOUT_BROADCAST);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(0);
		framePacket.packageItemsToFrame();
		return framePacket.getFramePacket();
	}
	/**
	 * 封装外网登录报文
	 * @param sourceID
	 * @param destineID
	 * @return
	 */
	public static byte[] packLoginServer(int userId,byte[] password ,byte[] userIp,int sourceID, int destineID){
		byte[] data=new byte[30];
		
		data[0]=0; //用户类型
		//用户ID 2 byte
		byte[] bUserId=TypeConvert.short2byte((short)userId);
		System.arraycopy(bUserId, 0, data, 1,2);
		//用户密码 8 byte
		System.arraycopy(password, 0, data, 3,password.length>8?8:password.length);
		//设备ID 2
		byte[] bDeviceId=TypeConvert.short2byte((short)sourceID);
		System.arraycopy(bDeviceId, 0, data, 11,2);
		//用户IP 4
		System.arraycopy(userIp, 0, data, 13,4);
		//设置是否在线
		data[29]=1;
		FramePacket			framePacket = new FramePacket();

		framePacket.setFrameSourceID(sourceID);
		framePacket.setFrameDestineID(destineID);//设置目的ID
		framePacket.setFrameType(FrameType.INTENET_LOGIN_REQUEST);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(data.length);
		framePacket.setFrameData(data, data.length);
		framePacket.packageItemsToFrame();
		return framePacket.getFramePacket();
	}
	
	public static byte[] packUdpData(byte[] data,int sourceID, int destineID){
		FramePacket			framePacket = new FramePacket();

		framePacket.setFrameSourceID(sourceID);
		framePacket.setFrameDestineID(destineID);//设置目的ID
		framePacket.setFrameType(FrameType.INTENET_TRANSMIT);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(data.length);
		framePacket.setFrameData(data, data.length);
		framePacket.packageItemsToFrame();
		return framePacket.getFramePacket();
	}
	
	public static byte[] packTextMessage(String str,int sourceID, int destineID){
		FramePacket			framePacket = new FramePacket();
		byte[] strByte=str.getBytes();
		byte[] idByte=TypeConvert.int2byte(1);
		byte[] data=new byte[strByte.length+idByte.length];
		System.arraycopy(idByte, 0, data, 0,4); 
		System.arraycopy(strByte, 0, data, 4,strByte.length);
		framePacket.setFrameSourceID(sourceID); 
		framePacket.setFrameDestineID(destineID);//设置目的ID
		framePacket.setFrameType(FrameType.INFO_MESSAGE);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(data.length);
		framePacket.setFrameData(data, data.length);
		framePacket.packageItemsToFrame();
		return framePacket.getFramePacket();
	}
	
	public static byte[] packData(byte[] data,int sourceID, int destineID,int frameType){
		FramePacket			framePacket = new FramePacket();

		framePacket.setFrameSourceID(sourceID);
		framePacket.setFrameDestineID(destineID);//设置目的ID
		framePacket.setFrameType(frameType);
		framePacket.setFrameSerialNo(0);
		framePacket.setFrameDataLength(data.length);
		framePacket.setFrameData(data, data.length);
		framePacket.packageItemsToFrame();
		return framePacket.getFramePacket();
	}
	 

}
