package com.simit.net.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.simit.net.NetConfig;
import com.simit.net.api.IExternalNet;
import com.simit.net.api.imple.PrivateExternalNet;
import com.simit.net.api.imple.PublicExternalNet;
import com.simit.net.domain.NetType;
import com.simit.net.task.InternalUDPServiceThread;
import com.simit.net.utils.MyLog;

public class NetService extends Service {
	
	private static final String TAG = "NetService";  

	private IExternalNet		externNet;  
//	private InternalTCPServiceThread		internalTCPServiceThread;
	private InternalUDPServiceThread    internalUDPServiceThread;
	private NetType				netType;
	private Handler mHandler;
	
	

    @Override  
    public IBinder onBind(Intent intent) {
        MyLog.i(TAG, "Service ----> onBind");  
        return null;
    }

    @Override  
    public void onCreate() {
        MyLog.i(TAG, "Service ----> onCreate"); 
        //内网通信
//        internalTCPServiceThread = new InternalTCPServiceThread(this);
//        internalTCPServiceThread.start();		// 启动内部网络通信线程
        mHandler=new Handler();
        internalUDPServiceThread=new InternalUDPServiceThread(this);
        internalUDPServiceThread.start();
        super.onCreate();
    }

  
	@Override  
    public void onDestroy() {
        MyLog.i(TAG, "Remote Service ----> onDestroy");  
        if(internalUDPServiceThread!=null){
        	internalUDPServiceThread.stop();
        }
        externNet.logout(this.getApplicationContext());
        super.onDestroy();

    }

    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i(TAG, "Remote Service ----> onStartCommand");
        netType=NetConfig.getInstance().getLocalProperty().getNetType();
        initExternalNet(netType);  
        
        return super.onStartCommand(intent, flags, startId);
    }
    
    public void initExternalNet(final NetType netType){
    	
    	if(netType!=null){
    		if(externNet!=null){
    			externNet.logout(getApplicationContext());
    			mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if(netType==NetType.WSN){
		        			externNet = new PrivateExternalNet();   
		        		}else if(netType==NetType.INTERNET){
		        			externNet = new PublicExternalNet();
		        		}
		        		externNet.login(getApplicationContext());
						
					}
				}, 500);
    		}else{
    			if(netType==NetType.WSN){
        			externNet = new PrivateExternalNet();   
        		}else if(netType==NetType.INTERNET){
        			externNet = new PublicExternalNet();
        		}
        		externNet.login(this.getApplicationContext());
    		}
    		
    	}
    }
    
    public IExternalNet GetExternalNet(){
    	
    	return externNet;
    }
    
    public void UninitPublicExternalNet(){
    	externNet = null;
    }
    
    public void UninitPrivateExternalNet(){
    	externNet = null;
    }
    
  
    

}
