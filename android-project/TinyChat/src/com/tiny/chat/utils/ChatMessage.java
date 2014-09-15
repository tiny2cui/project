package com.tiny.chat.utils;

/**
 * 消息
 * @author admin
 *
 */
public final class ChatMessage {

	
	private String mMessageId;
	private Object mMessageData;
    
	public static final String MESSAGE_TEXT_DATA = "message.text.data";
	public static final String MESSAGE_FILE_DATA = "message.file.data";
	public static final String MESSAGE_FRIEND_LOGIN_DATA = "message.friend.login.data";
	public static final String MESSAGE_FRIEND_LOGOUT_DATA = "message.friend.logout.data";
	public static final String MESSAGE_LOGIN_DATA = "message.login.data";
	public static final String MESSAGE_LOGOUT_DATA = "message.logout.data";
	
	public static final String MESSAGE_AUDIO_CONTROL_START="message.audio.control.start",
			                   MESSAGE_AUDIO_CONTROL_PLAY="message.audio.control.play",
			                   MESSAGE_AUDIO_CONTROL_STOP="message.audio.control.stop",
			                   MESSAGE_AUDIO_CONTROL_REFUSES="message.audio.control.refuses",					                   
		                       MESSAGE_AUDIO_DATA="message.audio.control.data";
	public static final String MESSAGE_VIDEO_CONTROL_START="message.video.control.start",
                               MESSAGE_VIDEO_CONTROL_PLAY="message.video.control.play",
                               MESSAGE_VIDEO_CONTROL_STOP="message.video.control.stop",
                               MESSAGE_VIDEO_CONTROL_REFUSES="message.video.control.refuses",					                   
                               MESSAGE_VIDEO_DATA="message.video.control.data";
	
	public ChatMessage(String id, Object data) {
		this.mMessageId = id;
		this.mMessageData = data;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public Object getMessageData() {
		return mMessageData;
	}
}
