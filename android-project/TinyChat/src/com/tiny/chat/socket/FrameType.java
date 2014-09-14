package com.tiny.chat.socket;


public class FrameType {
	
	
	public static final short INFO_MESSAGE 				= 1;		// 短消息	
	public static final short INFO_AUDIO 				= 11;		// 音频
	public static final short INFO_VIDEO 				= 15;		// 视频
	public static final short INFO_FILE 				= 19;		// 文件
	
	public static final short RESPONSE_MESSAGE 			= 4;		// 报文回执
	
	//add intenet	
	public static final short INTENET_ONLINE_DEVICE_REQUEST 	    = 47;		// 获取公网在线设备请求		
	public static final short INTENET_ONLINE_DEVICE_RESPONSE 	    = 48;		// 公网在线设备信息
		
	public static final short INTENET_LOGIN_REQUEST 				= 57;		// 远程服务器登录
	public static final short INTENET_LOGIN_RESPONSE 				= 58;		// 远程服务器登录结果
	public static final short INTENET_TRANSMIT 				        = 59;		// 远程服务器转发数据
	
	
	public static final short LAN_CLIENT_REGISTER_REQUEST 				= 80;		// 客户端注册请求
	public static final short LAN_CLIENT_REGISTER_RESPONSE 			    = 81;		// 客户端注册请求响应
	public static final short LAN_CLIENT_UNREGISTER_REQUEST 			= 82;		// 客户端注销请求
	public static final short LAN_CLIENT_UNREGISTER_RESPONSE 			= 83;		// 客户端注销请求响应
	public static final short LAN_CLIENT_CHANGE_DATA_TYPE_REQUEST 		= 84;		// 客户端类型更改请求
	public static final short LAN_CHANGE_DATA_TYPE_BROADCAST			= 85;		// 更改类型通知（广播）
	public static final short LAN_CLIENT_CHANGE_DATA_TYPE_RESPONSE		= 86;		// 客户端类型更改响应
	public static final short LAN_CLIENT_DATA_TYPE_REQUEST		= 87;		// 客户端查询自己已注册类型请求
	public static final short LAN_CLIENT_DATA_TYPE_RESPONSE		= 88;		// 客户端查询自己已注册类型响应
	public static final short LAN_SUPPORT_DATA_TYPE_REQUEST		= 89;		// 查询友邻支持数据类型请求
	public static final short LAN_SUPPORT_DATA_TYPE_RESPONSE		= 90;		// 查询友邻支持数据类型响应
	public static final short LAN_LOGIN_REQUEST					    = 91;		// 网络登录请求（公网或者专网）
	public static final short LAN_LOGIN_RESPONSE					= 92;		// 网络类型登录请求响应
	public static final short LAN_LOGIN_BROADCAST					= 93;		// 网络登录通知（广播）
	public static final short LAN_LOGOUT_REQUEST					= 94;		// 网络登出请求
	public static final short LAN_LOGOUT_RESPONSE					= 95;		// 网络登出请求响应
	public static final short LAN_LOGOUT_BROADCAST					= 96;		// 网络登出通知（广播）
	public static final short LAN_NET_TYPE_REQUEST				    = 97;		// 查询自己在公网还是专网请求
	public static final short LAN_NET_TYPE_RESPONSE				    = 98;		// 查询自己在公网还是专网请求响应
	public static final short LAN_PERSONAL_INFORMATION			    = 99;		// 自身资料
	public static final short LAN_FRIEND_INFORMATION			    = 100;		// 友邻资料
	public static final short LAN_PERSONAL_INFORMATION_REQUEST			= 101;		// 获取个人资料请求
	public static final short LAN_FRIEND_LIST_REQUEST			= 103;		// 获取友邻列表请求
	public static final short LAN_FRIEND_INFORMATION_REQUEST			= 105;		// 获取友邻资料
	public static final short LAN_POSITION_REQUEST			= 107;		// 客户端查询自身位置信息
	public static final short LAN_ENVIROMENT_REQUEST			= 109;		// 客户端查询自身环境信息

	
	//语音控制指令
	public static final int MESSAGE_AUDIO_CONTROL_START=200,
			                MESSAGE_AUDIO_CONTROL_PLAY=201,
			                MESSAGE_AUDIO_CONTROL_STOP=202,
			                MESSAGE_AUDIO_CONTROL_REFUSES=203,
			                MESSAGE_AUDIO_DATA=204;
	//视频控制指令
	public static final int MESSAGE_VIDEO_CONTROL_START=300,
                            MESSAGE_VIDEO_CONTROL_PLAY=301,
                            MESSAGE_VIDEO_CONTROL_STOP=302,
                            MESSAGE_VIDEO_CONTROL_REFUSES=303,
                            MESSAGE_VIDEO_DATA=304,
	                        MESSAGE_VIDEO_CONTROL_C2S=305,
	                        MESSAGE_VIDEO_CONTROL_S2C=306;
	
	
	
	                       
	
}