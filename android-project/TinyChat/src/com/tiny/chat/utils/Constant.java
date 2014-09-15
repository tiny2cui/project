package com.tiny.chat.utils;

public class Constant {

	//存储数据键值名称
	public static final String SHARENAME="tinyChat";  //存储名称
	public static final String NET_TYPE="net_type";
	public static final String REGISTER_TYPE="register_type";
	public static final String DEVICE_ID="device_id";
	
	//客户端自定义内部传输广播
	public static final String INTERIOR_SERVER_START ="interior_server_start",
					INTERIOR_SERVER_START_FAIL ="interior_server_start_fail",
					INTERIOR_SERVER_STOP ="interior_server_stop",
					INTERIOR_SERVER_STOP_FAIL ="interior_server_stop_fail",
					INTERIOR_SERVER_LOGIN ="interior_server_login",
					INTERIOR_SERVER_LOGIN_FAIL ="interior_server_login_fail",
					INTERIOR_SERVER_LOGOUT="interior_server_logout",
					INTERIOR_SERVER_LOGOUT_FAIL="interior_server_logout_fail";
	
	
	public Constant() {
	
	}
}
