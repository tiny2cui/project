package com.tiny.chat.domain;

public class Display {
	private int id ;//往来消息的源ID,
	private String content; //最后一条消息
	private String date; //最后一条消息发送（接收）时间
	public Display() {

	}
	public Display(int id){
		this.id=id;
	}
	public Display(SMSMessage message,int deveiceId){
		this.id=(deveiceId==message.getSourceID()? message.getDestionID():message.getSourceID());
		//this.id=message.getSourceID();
		this.content=message.getContent();
		this.date=message.getDate();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
}
