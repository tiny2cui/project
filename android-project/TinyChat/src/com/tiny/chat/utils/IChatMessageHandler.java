package com.tiny.chat.utils;
/**
 * 消息处理者
 * 实现此接口获取消息通知
 */
public interface IChatMessageHandler {
	void handlerMessage(ChatMessage message);
}
