package com.simit.video.test;


public class FrameType {
	
	
	public static final short INFO_MESSAGE 				= 1;		// 短消息
	public static final short INFO_AUDIO 				= 11;		// 音频
	public static final short INFO_VIDEO 				= 15;		// 视频
	public static final short INFO_FILE 				= 19;		// 文件
	
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
	public static final short LAN_NET_TYPE_REQUEST				= 97;		// 查询自己在公网还是专网请求
	public static final short LAN_NET_TYPE_RESPONSE				= 98;		// 查询自己在公网还是专网请求响应
	public static final short LAN_PERSONAL_INFORMATION			= 99;		// 自身资料
	public static final short LAN_FRIEND_INFORMATION			= 100;		// 友邻资料
	public static final short LAN_PERSONAL_INFORMATION_REQUEST			= 101;		// 获取个人资料请求
	public static final short LAN_FRIEND_LIST_REQUEST			= 103;		// 获取友邻列表请求
	public static final short LAN_FRIEND_INFORMATION_REQUEST			= 105;		// 获取友邻资料
	public static final short LAN_POSITION_REQUEST			= 107;		// 客户端查询自身位置信息
	public static final short LAN_ENVIROMENT_REQUEST			= 109;		// 客户端查询自身环境信息


	//视频控制指令
	public static final int MESSAGE_VIDEO_CONTROL_START=300,
	                        MESSAGE_VIDEO_CONTROL_PLAY=301,
	                        MESSAGE_VIDEO_CONTROL_STOP=302,
	                        MESSAGE_VIDEO_CONTROL_REFUSES=303,
	                        MESSAGE_VIDEO_DATA=304,
		                    MESSAGE_VIDEO_CONTROL_C2S=305,
		                    MESSAGE_VIDEO_CONTROL_S2C=306;
}

//public enum FrameType{
//	CLIENT_REGISTER_REQUEST((short)80),					// 客户端注册请求
//	CLIENT_REGISTER_RESPONSE((short)81),				// 客户端注册请求响应
//	CLIENT_UNREGISTER_REQUEST((short)82),				// 客户端注销请求
//	CLIENT_UNREGISTER_RESPONSE((short)83),				// 客户端注销请求响应
//	CLIENT_CHANGE_DATA_TYPE_REQUEST((short)84),			// 客户端类型更改请求
//	CHANGE_DATA_TYPE_BROADCAST((short)85),				// 更改类型通知（广播）)
//	CLIENT_CHANGE_DATA_TYPE_RESPONSE((short)86),		// 客户端类型更改响应
//	CLIENT_QUERY_DATA_TYPE_REQUEST((short)87),			// 客户端查询自己已注册类型请求
//	CLIENT_QUERY_DATA_TYPE_RESPONSE((short)88),			// 客户端查询自己已注册类型响应
//	QUERY_SUPPORT_DATA_TYPE_REQUEST((short)89),			// 查询友邻支持数据类型请求
//	QUERY_SUPPORT_DATA_TYPE_RESPONSE((short)90),		// 查询友邻支持数据类型响应
//	LOGIN_NET_REQUEST((short)91),						// 网络登录请求（公网或者专网）
//	LOGIN_NET_RESPONSE((short)92),						// 网络类型登录请求响应
//	LOGIN_NET_BROADCAST((short)93),						// 网络登录通知（广播）
//	LOGOUT_NET_REQUEST((short)94),						// 网络登出请求
//	LOGOUT_NET_RESPONSE((short)95),						// 网络登出请求响应
//	LOGOUT_NET_BROADCAST((short)96),					// 网络登出通知（广播）
//	QUERY_NET_TYPE_REQUEST((short)97),					// 查询自己在公网还是专网请求
//	QUERY_NET_TYPE_RESPONSE((short)98),					// 查询自己在公网还是专网请求响应
//	UPDATE_PERSONAL_INFORMATION((short)99),				// 更新个人信息
//	QUERY_PERSONAL_INFORMATION((short)100);				// 获取个人信息请求
//	
//	private short value = (short)0;
//	
//	private FrameType(short value){
//		this.value = value;
//	}
//	
//	public short value(){
//		return this.value;
//	}
//}