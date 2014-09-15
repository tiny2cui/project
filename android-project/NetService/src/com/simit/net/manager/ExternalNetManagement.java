package com.simit.net.manager;

import android.content.Context;

import com.simit.net.NetConfig;
import com.simit.net.domain.NetType;
import com.simit.net.service.NetService;


/**
 * 外网管理模块
 * 
 * 外网切换
 * 
 * @author admin
 *
 */
public class ExternalNetManagement {
	
	private NetService			netService;
	private NetType				currentNetType;
	private NetConfig app;
	
//	public ExternalNetManagement(NetService netService){
//		this.netService = netService;
//		app=NetConfig.getInstance();
//		currentNetType = app.getLocalProperty().getNetType();
//	}
//	
//	public void netConnect(NetType netType){
//		switch (netType) {
//		/*
//		 * 退出网络
//		 * 1. 先退出当前网络
//		 * 2. 反初始化动作
//		 * 3. 设置当前网络为OFFLINE
//		 */
//		case OFFLINE:
//			netService.GetExternalNet().logout();	// 退出当前网络
//			switch (currentNetType) {				// 根据当前网络类型反初始化
//			case WSN:
//				netService.UninitPrivateExternalNet();	
//				break;
//			case INTERNET:
//				netService.UninitPublicExternalNet();
//				break;
//			case OFFLINE:
//			default:
//				break;
//			}
//			app.getLocalProperty().setNetType(NetType.OFFLINE);	// 设置当前网络类型为OFFLINE
//			break;
//
//		/*
//		 * 切换到专网
//		 * 根据当前网络状态进行切换
//		 * 1. OFFLINE  --> WSN
//		 * 		a) 初始化网络类型为专网
//		 * 		b) 网络登录
//		 * 2. INTERNET --> WSN
//		 * 		a) 先登出网络
//		 * 		b) 初始化网络类型为专网
//		 * 		c) 网络登录
//		 * 3. WSN --> WSN
//		 * 		不做动作
//		 * 
//		 * 代码简化说明：在case INTERNET 时没有break，因为INTERNET就比OFFLINE多做一个登出网络的动作
//		 */
//		case WSN:
//			switch (currentNetType) {
//			case INTERNET:
//				netService.GetExternalNet().logout();
//				
//			case OFFLINE:
//				netService.initExternalNet(netType);
//				netService.GetExternalNet().login(null);
//				break;
//				
//			case WSN:
//			default:
//				break;
//			}
//			
//			app.getLocalProperty().setNetType(NetType.WSN);	// 设置当前网络类型为WSN
//			break;
//			
//		/*
//		 * 切换至公网
//		 * 基本同上
//		 */
//		case INTERNET:
//			switch (currentNetType) {
//			case WSN:
//				netService.GetExternalNet().logout();
//				
//			case OFFLINE:
//				netService.initExternalNet(netType);
//				netService.GetExternalNet().login(null);
//				break;
//				
//			case INTERNET:
//			default:
//				break;
//			}
//			app.getLocalProperty().setNetType(NetType.INTERNET);	// 设置当前网络类型为INTERNET
//			
//			break;
//			
//		default:
//			break;
//		}
//	}
}
