package com.tiny.chat.domain;

import java.io.Serializable;

import com.tiny.chat.utils.TypeConvert;

public class SMSMessage extends Message implements Serializable {
	private static final long serialVersionUID = -258001762495056325L;
	
	private String content;   //短消息内容
	private int messageState; //消息状态
	private String date;      //接收信息的时间
	

	public SMSMessage() {
		
	}
	
	/**
	 * 
	 * @param idMessage
	 * @param sourceID
	 * @param destionID
	 * @param messageType
	 * @param serialNumber
	 * @param content
	 * @param messageState
	 */
	public SMSMessage(int idMessage,int sourceID,int destionID,int messageType,int serialNumber,String content, int messageState,String date){
		super(idMessage, sourceID, destionID, messageType, serialNumber);
		this.content=content;
		this.messageState=messageState;
		this.date=date;
	}
	
	/**
	 * 
	 * @param framePacket
	 * @param messageState 消息发送及阅读状态
	 * @param date
	 */
	public SMSMessage(FramePacket framePacket,int messageState,String date ){
		super(0, framePacket.getSourceID(), framePacket.getDestineID(), framePacket.getFrameType(), framePacket.getFrameSerial());
		this.messageState=messageState;
		this.date=date;
		byte[] data=framePacket.getData();
		if(data.length>4){
			byte[] id=new byte[4];
			System.arraycopy(data, 0, id, 0, 4);
			this.idMessage=TypeConvert.byte2int(id);
			this.content=new String(data, 4, data.length-4);
		}	
	}
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public int getMessageState() {
		return messageState;
	}

	public void setMessageState(int messageState) {
		this.messageState = messageState;
	}
	
	/**
	 * 获取报文发送数据帧
	 * @return
	 */
	public byte[] getFrameData(){
		if(content!=null){
			byte[] contentBytes=content.getBytes();
			byte[] idBytes=TypeConvert.int2byte(idMessage);
			byte[] data=new byte[contentBytes.length+idBytes.length];
			System.arraycopy(idBytes, 0, data, 0, idBytes.length);
			System.arraycopy(contentBytes, 0, data, idBytes.length, contentBytes.length);
			FramePacket packet=getFramePacket(data);
			return packet.getFramePacket();
		}
		return null;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
