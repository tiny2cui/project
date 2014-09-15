package com.simit.net.utils;

import android.util.Log;

/**
 * 自定义 log 管理日志，
 * @author wen.cui
 *
 */
public class MyLog {
  
	private static int V=0;//冗于信息
	private static int D=1;//调试信息  
	private static int I=2;//普通信息
	private static int W=3;//警告信息
	private static int E=4;//错误信息
	private static int LEVEL=5;//级别开关 当为-1是关闭所有的log信息
	//普通信息
	public static void i(String tag, String msg){
		    if(I<LEVEL){
			    Log.i(tag, msg);
		    }
	}
	//调试信息
	public static void d(String tag, String msg){
	       if(D<LEVEL){
	    	   Log.d(tag, msg);
	       }
	} 
	//错误信息
	public static void e(String tag, String error){
		if(E<LEVEL){
			Log.e(tag, error);
		}
	}
	//错误信息
	public static void e(String tag, String error,Throwable e){
		if(E<LEVEL){
			Log.e(tag, error,e);
		}
	}
	//警告信息
	public static void w(String tag, String error){
		if(W<LEVEL){
			Log.w(tag, error);
		}
	}
	////冗于信息
	public static void v(String tag, String error){
		if(V<LEVEL){
			Log.v(tag, error);
		}
	}
}
