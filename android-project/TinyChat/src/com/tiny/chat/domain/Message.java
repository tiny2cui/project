package com.tiny.chat.domain;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = -258001763201456456L;
	protected int idMessage;  //报文ID
	protected int sourceID;
	protected int destionID;
	protected int messageType;
	protected int serialNumber;
	protected int conversationID;
	public Message() {
		
	}
	
	
	/**
	 * 
	 * @param idMessage 报文ID
	 * @param sourceID  源ID
	 * @param destionID 目的ID
	 * @param messageType 消息类型
	 * @param serialNumber 序列号
	 */
	public Message(int idMessage,int sourceID,int destionID,int messageType,int serialNumber){
		this.idMessage=idMessage;
		this.sourceID=sourceID;
		this.destionID=destionID;
		this.messageType=messageType;
		this.serialNumber=serialNumber;
		this.conversationID=(int)Math.pow(sourceID, 2) + (int)Math.pow(destionID,2);
	}
	
	
	
	public int getIdMessage() {
		return idMessage;
	}
	public void setIdMessage(int idMessage) {
		this.idMessage = idMessage;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public int getDestionID() {
		return destionID;
	}
	public void setDestionID(int destionID) {
		this.destionID = destionID;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getConversationID() {
		return conversationID;
	}

	public void setConversationID(int conversationID) {
		this.conversationID = conversationID;
	}

	protected FramePacket getFramePacket(byte[] data){
		return new FramePacket(sourceID, destionID, messageType, data);
	}
}
