package com.simit.net.api;


import android.content.Context;

import com.simit.net.domain.FramePacket;
import com.simit.net.domain.NetType;

/**
 * 外部网络通信 接口
 * 用来处理WSN网络与Internet网络中手持服务器的交互
 * @author wen.cui
 *
 */
public interface IExternalNet {
	void login(Context context);
	void logout(Context context);
	void send(int id, byte[] data, int dataLen);
	void sendFrame(FramePacket framePacket);
	void receive(byte[] data, int dataLen,String ip);
	NetType getNetType();
}
