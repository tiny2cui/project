package com.simit.net.utils;
/**
 * 常量类,定义常量参数
 * @author jian.huang
 *
 */
public final class Constants {
	public final static int CODE_LOGIN=93,CODE_LOGOUT=96;
	public final static int INTERNAL_TCP_PORT = 20141,INTERNAL_UDP_PORT=20142,PUBLIC_PORT = 5665,LISTEN_PORT = 20146;
	public final static String BROABCAST_ADDRESS="255.255.255.255";
	public final static String PUBLIC_SERVER_ADDRESS = "192.168.1.112";
	
	//客户端自定义内部传输广播
	public static final String INTERIOR_SERVER_START ="interior_server_start",
						INTERIOR_SERVER_START_FAIL ="interior_server_start_fail",
						INTERIOR_SERVER_STOP ="interior_server_stop",
						INTERIOR_SERVER_STOP_FAIL ="interior_server_stop_fail",
						INTERIOR_SERVER_LOGIN ="interior_server_login",
						INTERIOR_SERVER_LOGIN_FAIL ="interior_server_login_fail",
						INTERIOR_SERVER_LOGOUT ="interior_server_logout",
						INTERIOR_SERVER_LOGOUT_FAIL ="interior_server_logout_fail";
	
	
}
