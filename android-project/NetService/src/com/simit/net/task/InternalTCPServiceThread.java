package com.simit.net.task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.simit.net.service.NetService;
import com.simit.net.utils.Constants;

public class InternalTCPServiceThread extends Thread{
	
	private ServerSocket 			server;
	private Socket					clientSocket;
	private NetService				netService;
	private boolean runThread=true;
	public InternalTCPServiceThread(NetService netService){
		this.netService = netService;
	}
	
	public void run(){
		try{
			server = new ServerSocket(Constants.INTERNAL_TCP_PORT);
			while(runThread){
				clientSocket = server.accept();
				InternalTCPTransThread internalTCPTransThread = new InternalTCPTransThread(netService, clientSocket);
				internalTCPTransThread.SetThreadRun();
				internalTCPTransThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setThreadWork(boolean runThread) {
		this.runThread = runThread;
		if(!runThread){
			try {
				if(server!=null){
					server.close();
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
}