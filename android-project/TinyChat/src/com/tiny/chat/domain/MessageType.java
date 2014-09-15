package com.tiny.chat.domain;

import java.io.Serializable;

/**
 * 消息实体类
 */
public class MessageType implements Serializable {
	private static final long serialVersionUID = -258001762495056325L;

	//
	private Integer idMessage;
	// 发送端设备ID
	private Integer sendID;
	private Integer receiveID;
	private Integer messageType;
	private Integer messageState;
	private String content;
	private String date;
	private int percent;

	public MessageType() {

	}

	/**
	 * 
	 * @param idMessage
	 * @param sendID
	 * @param receiveID
	 * @param messageType
	 * @param messageState
	 * @param content
	 * @param date
	 */
	public MessageType(Integer idMessage, Integer sendID, Integer receiveID,
			Integer messageType, Integer messageState, String content,
			String date) {
		this.idMessage = idMessage;
		this.sendID = sendID;
		this.receiveID = receiveID;
		this.messageType = messageType;
		this.messageState = messageState;
		this.content = content;
		this.date = date;
	}

	/**
	 * 
	 * @param packet
	 *            接收到的原始数据帧
	 * @param messageState
	 * @param date
	 */
	public MessageType(FramePacket packet, Integer messageState, String date) {

		this.idMessage = -1;
		this.sendID = packet.getSourceID();
		this.receiveID = packet.getDestineID();
		this.messageType = packet.getFrameType();
		this.content = new String(packet.getData());
		this.messageState = messageState;
		this.date = date;
	}

	public Integer getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(Integer idMessage) {
		this.idMessage = idMessage;
	}

	public Integer getSendID() {
		return sendID;
	}

	public void setSendID(Integer sendID) {
		this.sendID = sendID;
	}

	public Integer getReceiveID() {
		return receiveID;
	}

	public void setReceiveID(Integer receiveID) {
		this.receiveID = receiveID;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Integer getMessageState() {
		return messageState;
	}

	public void setMessageState(Integer messageState) {
		this.messageState = messageState;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

}
