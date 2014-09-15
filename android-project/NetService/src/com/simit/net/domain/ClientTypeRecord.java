package com.simit.net.domain;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.simit.net.domain.Client;
import com.simit.net.utils.IPv4Util;

/**
 * @category
 * 客户端类型记录类
 * 
 * 记录注册到服务端的客户类型以及对应的通信链接
 * 主要记录OutputStream，用于服务发送至客户端数据
 * 
 * @author admin
 *
 */

public class ClientTypeRecord {

	private ArrayList<Client>	clientRecords;
	private ArrayList<String>	clientIPAddress;
	public ClientTypeRecord(){
		clientRecords = new ArrayList<Client>();
		clientIPAddress= new ArrayList<String>();
	}

//	public void record(byte clientType, Socket socket){
//		Client			client=null;
//		boolean exist=false;
//		String ipAddr=socket.getInetAddress().getHostAddress();
//				
//		int clientId=IPv4Util.ipToInt(ipAddr);
//		for(int i = 0; i < clientRecords.size(); i++){
//			if(clientId==clientRecords.get(i).getClientId()){
//				clientRecords.get(i).setClientType(clientType);
//				exist=true;
//			}else{
//				byte type=(byte)((~clientType)&clientRecords.get(i).getClientType());
//				clientRecords.get(i).setClientType(type);
//			}
//		}
//		
//		if(!exist){
//			client = new Client(clientType, clientId, 0, socket);
//			clientRecords.add(client);
//		}
//	}
	
	public void record(byte clientType, String ipAddr,int port){
		Client			client=null;
		boolean exist=false;	
		int clientId=IPv4Util.ipToInt(ipAddr);
		for(int i = 0; i < clientRecords.size(); i++){
			if(clientId==clientRecords.get(i).getClientId()){
				clientRecords.get(i).setClientType(clientType);
				exist=true;
			}else{
				byte type=(byte)((~clientType)&clientRecords.get(i).getClientType());
				clientRecords.get(i).setClientType(type);
			}
		}
		
		if(!exist){
			client = new Client(clientType, clientId, 0, ipAddr,port);
			clientRecords.add(client);
		}
	}
	
	public void delete(int clientId){
		for(int i = 0; i < clientRecords.size(); i++){
			if(clientRecords.get(i).getClientId()==clientId){
				clientRecords.remove(i);
			}
		}
	}
	
	public byte getRegisteredType(int id){
		for (Client clientRecord : clientRecords) {
			if(clientRecord.getClientId()==id)
				return clientRecord.getClientType();
		}
		
		return (byte)0;
	}
	
	/**
	 * 根据类型获取客户端
	 * @param dataType
	 * @return
	 */
	public Client getClient(byte dataType){
		for (Client clientRecord : clientRecords) {
			if((int)(dataType & clientRecord.getClientType()) != 0){
				return clientRecord;
			}
		}
		return null;
	}
	public List<Client> getClients(){
		return clientRecords;
	}
	public int getNumberOfClients(){
		return clientRecords.size();
	}
}
