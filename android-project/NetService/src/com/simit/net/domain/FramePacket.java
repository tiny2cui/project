package com.simit.net.domain;
import android.R.integer;

import com.simit.net.utils.TypeConvert;

public class FramePacket {

	private TypeConvert tc;
	private byte frameHeaderFlag[]=new byte[2]; // 帧头
	
	private byte[] frameHeader; // 帧头
	private byte[] dataContent; // 帧内数据内容
	private byte[] framePacket; // 完整帧

	private int lenFrameHeader=11; // 帧头长度，不包含数据内容和CRC
	// private int lenFrameHeaderFlag = 2;
	private final int lenSourceID = 2;
	private final int lenDestineID = 2;
	private final int lenFrameType = 2;
	private final int lenFrameSerial = 1;
	private final int lenDataLength = 2;  //数据长度所占字节
//	private int lenData=-1; //数据长度
	private final int lenCRC = 1;

	// private int posFrameHeaderFlag;
	private int posSourceID;
	private int posDestineID;
	private int posFrameType;
	private int posFrameSerial;
	private int posDataLength;
	private int posData;
	private int posCRC;

	// 数据帧成员
	private short sourceID=-1; // 源地址
	private short destineID=-1; // 目的地址
	private short frameType=-1; // 帧类型
	private byte serialNo; // 序号
	private short dataLength=-1; // 数据长度

	private String ipAddress;
	private int port;

	private void init() {
		frameHeaderFlag[0]=(byte)0xD5;
		frameHeaderFlag[1]=(byte)0xC8;
		posSourceID = 2;
		posDestineID = posSourceID + lenSourceID;
		posFrameType = posDestineID + lenDestineID;
		posFrameSerial = posFrameType + lenFrameType;
		posDataLength = posFrameSerial + lenFrameSerial;
		posData = posDataLength + lenDataLength;
	}

	/*
	 * 根据以下几个参数生成一帧数据
	 */
	public FramePacket(int sourceId, int destineId, int frameType, byte[] data) {
		init();
		//申请帧空间，dataLength为数据内容长度，12 为其他元素长度之和
		if(data!=null && data.length>0){
			framePacket = new byte[12 + data.length];

			//数据长度
			dataLength=(short)data.length;
			framePacket[9] = (byte) ((data.length >>> 8) & 0xFF);
			framePacket[10] = (byte) (data.length & 0xFF);
			
			//数据内容
			if(data.length==1){
				framePacket[11] = data[0];
			}else if (data.length > 1){
				System.arraycopy(data, 0, framePacket, 11, data.length);
			}
			//CRC, 暂时以1为结尾
			framePacket[12 + data.length - 1] = 1;
		}else{
			framePacket = new byte[12];

			//数据长度
			framePacket[9] = (byte) 0;
			framePacket[10] = (byte) 0;

			//CRC, 暂时以1为结尾
			framePacket[11] = 1;
		}
		
		//设置帧标示位
		framePacket[0] = (byte) 0xD5;
		framePacket[1] = (byte) 0xC8;

		//设置源ID
		framePacket[2] = (byte) ((short)(sourceId >>> 8) & 0xFF); 
		framePacket[3] = (byte) ((short)sourceId & 0xFF); 
		

		//设置目的ID
		framePacket[4] = (byte) ((short)(destineId >>> 8) & 0xFF);
		framePacket[5] = (byte) ((short)destineId & 0xFF);
		

		//设置类型
		framePacket[6] = (byte) (((short)frameType >>> 8) & 0xFF);
		framePacket[7] = (byte) ((short)frameType & 0xFF);
		

		//数据序列号
		framePacket[8] = serialNo;
	    
	}

	/*
	 * 根据以下几个参数生成一帧数据
	 */
	public FramePacket(int sourceId, int destineId, int frameType, byte[] data,int dataLenth) {
		init();
		//申请帧空间，dataLength为数据内容长度，12 为其他元素长度之和
		if(data!=null && dataLenth>0){
			framePacket = new byte[12 + dataLenth];

			//数据长度
			dataLength=(short)dataLenth;
			framePacket[9] = (byte) ((dataLenth >>> 8) & 0xFF);
			framePacket[10] = (byte) (dataLenth & 0xFF);
			
			//数据内容
			if(dataLenth==1){
				framePacket[11] = data[0];
			}else if (dataLenth > 1){
				System.arraycopy(data, 0, framePacket, 11, dataLenth);
			}
			//CRC, 暂时以1为结尾
			framePacket[12 + dataLenth - 1] = 1;
		}else{
			framePacket = new byte[12];

			//数据长度
			framePacket[9] = (byte) 0;
			framePacket[10] = (byte) 0;

			//CRC, 暂时以1为结尾
			framePacket[11] = 1;
		}
		
		//设置帧标示位
		framePacket[0] = (byte) 0xD5;
		framePacket[1] = (byte) 0xC8;

		//设置源ID
		framePacket[2] = (byte) ((short)(sourceId >>> 8) & 0xFF); 
		framePacket[3] = (byte) ((short)sourceId & 0xFF); 
		

		//设置目的ID
		framePacket[4] = (byte) ((short)(destineId >>> 8) & 0xFF);
		framePacket[5] = (byte) ((short)destineId & 0xFF);
		

		//设置类型
		framePacket[6] = (byte) (((short)frameType >>> 8) & 0xFF);
		framePacket[7] = (byte) ((short)frameType & 0xFF);
		

		//数据序列号
		framePacket[8] = serialNo;
	    
	}

	public FramePacket(byte[] frame, int len) {
		init();
		if(len==lenFrameHeader-2){
			frameHeader = new byte[2+len];
			frameHeader[0]=frameHeaderFlag[0];
			frameHeader[1]=frameHeaderFlag[1];
			System.arraycopy(frame, 0, frameHeader, 2, lenFrameHeader-2);
			
			framePacket=new byte[frameHeader.length];
			System.arraycopy(frameHeader, 0,framePacket , 0,frameHeader.length);
		}else{
			frameHeader = new byte[lenFrameHeader];
			frameHeader[0]=frameHeaderFlag[0];
			frameHeader[1]=frameHeaderFlag[1];
			System.arraycopy(frame, 2, frameHeader, 2, lenFrameHeader-2);
			if(len>lenFrameHeader){
				
				dataContent = new byte[getDataLength()];
				if(dataContent.length>0){
					if(dataContent.length==1){
						dataContent[0]=frame[11];
						
					}else {
						System.arraycopy(frame, 11,dataContent , 0,dataContent.length);
					}
					
					
				}
				framePacket=new byte[12+dataContent.length];
				System.arraycopy(frame, 0,framePacket , 0,framePacket.length);
				
			}
		} 
		
		
	}
	public void setFrameSourceID(int sourceID) {
		this.sourceID = (short) sourceID;
	}

	public void setFrameDestineID(int destineID) {
		this.destineID = (short) destineID;
	}

	public void setFrameType(int frameType) {
		
		this.frameType = (short) frameType;
	}

	public void setFrameSerialNo(int serialNo) {
		this.serialNo = (byte) serialNo;
	}

	public void setFrameDataLength(int dataLength) {
		this.dataLength = (short) dataLength;
	}

	public void setFrameDataContent(byte[] dataContent) {
		this.dataContent = new byte[dataContent.length];
		System.arraycopy(dataContent, 0, this.dataContent, 0,
				dataContent.length);
	}

	/*
	 * 将数据元素组合成一帧数据
	 */
	public boolean packageItemsToFrame() { 
		if(dataLength>=0){
			/*
			 * 申请帧空间，dataLength为数据内容长度，12 为其他元素长度之和
			 */
		    framePacket = new byte[12 + dataLength];

			/*
			 * 设置帧标示位
			 */
			framePacket[0] = (byte) 0xD5;
			framePacket[1] = (byte) 0xC8;

			/*
			 * 设置源ID
			 */
			framePacket[2] = (byte) ((sourceID >>> 8) & 0xFF); // 高字节在前
			framePacket[3] = (byte) (sourceID & 0xFF); // 低字节在后

			/*
			 * 设置目的ID
			 */
			framePacket[4] = (byte) ((destineID >>> 8) & 0xFF);
			framePacket[5] = (byte) (destineID & 0xFF);
			

			/*
			 * 设置类型
			 */
			framePacket[6] = (byte) ((frameType >>> 8) & 0xFF);
			framePacket[7] = (byte) (frameType & 0xFF);
			

			/*
			 * 数据序列号
			 */
			framePacket[8] = serialNo;

			/*
			 * 数据长度
			 */
			framePacket[9] = (byte) ((dataLength >>> 8) & 0xFF);
			framePacket[10] = (byte) (dataLength & 0xFF);

			/*
			 * 数据内容
			 */
			if (dataLength > 0)
				System.arraycopy(dataContent, 0, framePacket, 11, dataLength);

			/*
			 * CRC, 暂时以1为结尾
			 */
			framePacket[12 + dataLength - 1] = 1;
			return true;
		}
		return false;
		
	}

	public byte[] getFramePacket() {
		return framePacket;
	}

	public void setFormattedPacket(byte[] frameHeader) {
		if (this.frameHeader.length == 0)
			this.frameHeader = new byte[frameHeader.length];

		System.arraycopy(frameHeader, 0, this.frameHeader, 0,
				frameHeader.length);

		dataLength =(short) frameHeader.length;

		posCRC = posData + dataLength;
	}

	/*
	 * 将帧头与帧数据合并为一个完整的帧
	 */
	public void headerData2Frame(byte[] frameData, int lenData) {
		framePacket = new byte[lenFrameHeader + lenData];
		System.arraycopy(frameHeader, 0, framePacket, 0, lenFrameHeader);
		System.arraycopy(frameData, 0, framePacket, lenFrameHeader, lenData);
	}
	
	public void setFrameData(byte[] data, int lenData) {
		dataLength=(short)lenData;
		dataContent = new byte[lenData];
		System.arraycopy(data, 0, dataContent, 0, lenData);
	}

	public boolean gheckPacketValid() {
		// if(posCRC == 0)
		// return false;
		// else
		return true;
	}

	public int getFrameLength() {
		return framePacket.length;
	}

	
	public int getSourceID() {
		byte[] tempByte = new byte[2];
		System.arraycopy(frameHeader, posSourceID, tempByte, 0, lenSourceID);
		return (short) TypeConvert.byte2short(tempByte);
	}

	public int getDestineID() {
		byte[] tempByte = new byte[2];

		if(frameHeader!=null){
			System.arraycopy(frameHeader, posDestineID, tempByte, 0, lenDestineID);
		}else{
			System.arraycopy(framePacket, posDestineID, tempByte, 0, lenDestineID);
		}
		
		return (int) TypeConvert.byte2short(tempByte);
	}

	public int getFrameType() {
		byte[] tempByte = new byte[2];
		System.arraycopy(frameHeader, posFrameType, tempByte, 0, lenFrameType);
		return (int) TypeConvert.byte2short(tempByte);
	}

	public int getFrameSerial() {
		byte tempByte;

		tempByte = frameHeader[posFrameSerial];
		return (int) (tempByte);
	}

	public int getDataLength() {
		byte[] tempByte = new byte[2];
		System.arraycopy(frameHeader, posDataLength, tempByte, 0, lenDataLength);
		dataLength= TypeConvert.byte2short(tempByte);
		return dataLength;
	}

	public byte[] getData() {
		if(dataLength>0){
			byte[] tempByte = new byte[dataLength];
			if(dataLength==1){
				tempByte[0]=framePacket[posData];
			}else{
				System.arraycopy(framePacket, posData, tempByte, 0, dataLength);
			}
			
			return tempByte;
		}
		return null;
		
	}
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public FramePacket() {
		init();
	}

}
