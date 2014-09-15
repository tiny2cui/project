package com.simit.audio.config;

public class NetConfig {
	/**
	 * 缓冲区大小
	 */
	public static final int BUFFER_SIZE = 1024;
	/**
	 * 服务器的IP
	 */
	public static String SERVER_HOST = "192.168.2.112";
	/**
	 * 服务器的监听端口
	 */
	public static final int SERVER_PORT = 1111;
	/**
	 * 客户端的IP
	 */
	public static  String CLIENT_IP = "192.168.2.107";//"192.168.2.107";
	/**
	 * 客户端的监听端口
	 */
	public static final int CLIENT_PORT = 7777;
	/**
	 * 手机端的状态：录音 or 播放
	 */
	public static final int AUDIO_STATUS_RECORDING = 0;
	
	public static final int AUDIO_STATUS_LISTENING = 1;

	public static void setServerHost(String ip) {
		System.out.println("修改后的服务器网址为  " + ip);
		SERVER_HOST = ip;
	}
	public static void setClientIp(String ip){
		CLIENT_IP=ip;
	}

}