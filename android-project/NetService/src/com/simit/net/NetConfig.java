package com.simit.net;

import com.simit.net.domain.ClientTypeRecord;
import com.simit.net.domain.FriendIpRecord;
import com.simit.net.domain.LocalProperty;

public class NetConfig {
	/**
     * log 日志标签
     */
	public static final String TAG = "M02Application";
	private static NetConfig instance;
	private ClientTypeRecord	clientTypeRecord; //记录客户端链接类型
	private FriendIpRecord friendIpRecord;  //记录友邻ip
	private LocalProperty localProperty; //记录本地配置
	
	/**
	 * 获取 Application 实例
	 * @return
	 */	  
	public static NetConfig getInstance() {
		if(instance==null){
			instance=new NetConfig();
			instance.init();
		}
			return instance;
		}
	private void init(){
		clientTypeRecord = new ClientTypeRecord();
		friendIpRecord =new FriendIpRecord();
		localProperty =new LocalProperty();
		
	}
	
	/**
	 * 获取记录链接类型实例
	 * @return
	 */
	public ClientTypeRecord getClientTypeRecord() {
		if(clientTypeRecord==null){
			clientTypeRecord=new ClientTypeRecord();
			
		}
		return clientTypeRecord;
		
	}
	/**
	 * 获取友邻ip记录实例
	 * @return
	 */
	public FriendIpRecord getFriendIpRecord() {
		if(friendIpRecord==null){
			friendIpRecord=new FriendIpRecord();
		}
		return friendIpRecord;
	}

	/**
	 * 获取本地配置实例
	 * @return
	 */
	public LocalProperty getLocalProperty() {
		if(localProperty==null){
			localProperty=new LocalProperty();
		}
		return localProperty;
	}
}
