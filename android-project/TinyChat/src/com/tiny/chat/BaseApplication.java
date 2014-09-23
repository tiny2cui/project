package com.tiny.chat;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import com.simit.audio.util.IPv4Util;
import com.tiny.chat.activity.AudioActivity;
import com.tiny.chat.activity.VideoActivity;
import com.tiny.chat.domain.People;
import com.tiny.chat.domain.Position;
import com.tiny.chat.file.FileState;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.Constant;
import com.tiny.chat.utils.FileUtils;
import com.tiny.chat.utils.IChatMessageHandler;

import android.R.integer;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class BaseApplication extends Application {

	private static BaseApplication instance; // 唯一实例
	// 消息容器
	private Set<IChatMessageHandler> mMessageHandlers = new HashSet<IChatMessageHandler>();
	private Handler mHandler;

	private int deviceID=0;
	private People mUser;
	
	/** mEmoticons 表情 **/
	public static Map<String, Integer> mEmoticonsId;
	public static List<String> mEmoticons;
	public static List<String> mEmoticons_Zem;
	public static List<String> mEmoticons_Zemoji;
	/** 缓存 **/
	private Map<String, SoftReference<Bitmap>> mAvatarCache;
	
	private HashMap<String, String> mLastMsgCache; // 最后一条消息缓存，以IMEI为KEY
	private ArrayList<People> mUnReadPeople; // 未读用户队列
	private HashMap<String, String> mLocalUserSession; // 本机用户Session信息
	//private List<People> mOnlineUsers=new ArrayList<People>(); // 在线用户集合，以IMEI为KEY
	private HashMap<Integer, People> mOnlineUsers = new HashMap<Integer, People>();
	private List<People> mMessageUsers = new ArrayList<People>();
	public static HashMap<String, FileState> sendFileStates; // 存放文件状态
	public static HashMap<String, FileState> recieveFileStates; // 存放文件状态
	
	// 本地图像、缩略图、声音、文件存储路径
	public static String IMAG_PATH;
	public static String THUMBNAIL_PATH=FileUtils.getSDPath()+ File.separator+ "tinyChat"+File.separator+"thumbnail" +File.separator;
	public static String VOICE_PATH;
	public static String FILE_PATH;
	public static String SAVE_PATH;

	// 静音、震动开关
	private static boolean isSOUND = true;
	private static boolean isVIBRATE = true;

	// private Map<String,Message> map=new HashMap<String, Message>();

	@Override
	public void onCreate() {
		super.onCreate();
		if (instance == null) {
			instance = this;
		}
		mHandler = new Handler();
		mEmoticonsId = new HashMap<String, Integer>();
		mEmoticons = new ArrayList<String>();
		mEmoticons_Zem = new ArrayList<String>();
		mEmoticons_Zemoji = new ArrayList<String>();
		mLocalUserSession = new HashMap<String, String>(15); // 存储用户登陆信息

		mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
		// 预载表情
		for (int i = 1; i < 64; i++) {
			String emoticonsName = "[zem" + i + "]";
			int emoticonsId = getResources().getIdentifier("zem" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zem.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}
		for (int i = 1; i < 59; i++) {
			String emoticonsName = "[zemoji" + i + "]";
			int emoticonsId = getResources().getIdentifier("zemoji_e" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zemoji.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}

		// 默认启动响铃与振动提醒
		isSOUND = true;
		isVIBRATE = true;

		SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARENAME, Context.MODE_PRIVATE);
		deviceID=mSharedPreferences.getInt(Constant.DEVICE_ID, -1);
		if(deviceID==-1){
			deviceID=new Random().nextInt(100);
			mSharedPreferences.edit().putInt(Constant.DEVICE_ID,deviceID).commit();
		}
		mUser=new People(deviceID);
		//测试数据
		Position position= new Position(2, (byte)45,(byte) 45, (byte)45, (short)123, (byte)0, (byte)45, (byte)45, (byte)45, (short)123, (byte)0, 35f, (short)2014, (byte)8, (byte)13, (byte)12, (byte)25, (byte)40, (byte)10);
		mUser.getFriendInfo().setPosition(position);
	}

	public void addAvatarCache(String paramAvatarName, Bitmap bitmap) {
		mAvatarCache.put(paramAvatarName, new SoftReference<Bitmap>(bitmap));
	}

	public Reference<Bitmap> getAvatarCache(String paramAvatarName) {
		return mAvatarCache.get(paramAvatarName);
	}

	public void removeAvatarCache(String paramAvatarName) {
		mAvatarCache.remove(paramAvatarName);
	}

	public Map<String, SoftReference<Bitmap>> getAvatarCache() {
		return mAvatarCache;
	}

	public HashMap<Integer,People> getOnlineUsers() {
		return mOnlineUsers;
	}
	
	public void addOnlineUsers(int key,People people) {
		if(!mOnlineUsers.containsKey(key) && key!=deviceID){
			mOnlineUsers.put(key, people);
		}
		
		
	}


	/**
	 * 移除在线用户
	 * 
	 * @param paramIMEI
	 *            需要移除的用户IMEI
	 * @param paramtype
	 *            操作类型，0:清空在线列表，1:移除指定用户
	 */
	public void removeOnlineUser( int deviceId) {
		if(mOnlineUsers.containsKey(deviceId)){
			mOnlineUsers.remove(deviceId);
		}
		
	}

	
	/**
	 * 获取BaseApplication实例 单例模式，返回唯一实例
	 * 
	 * @return instance
	 */
	public static BaseApplication getInstance() {
		return instance;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e("BaseApplication", "onLowMemory");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.e("BaseApplication", "onTerminate");
	}

	public HashMap<String, String> getUserSession() {
		return mLocalUserSession;
	}

	/* 设置声音提醒 */
	public static boolean getSoundFlag() {
		return isSOUND;
	}

	public static void setSoundFlag(boolean sound_flag) {
		isSOUND = sound_flag;
	}

	/* 设置震动提醒 */
	public static boolean getVibrateFlag() {
		return isVIBRATE;
	}

	public static void setVibrateFlag(boolean vibrate_flag) {
		isVIBRATE = vibrate_flag;
	}

	/**
	 * 注册消息
	 * 
	 * @param handler
	 */
	public void registerMessageHandler(IChatMessageHandler handler) {
		mMessageHandlers.add(handler);
	}

	/**
	 * 删除消息
	 * 
	 * @param handler
	 */
	public void unRegisterMessageHandler(IChatMessageHandler handler) {
		mMessageHandlers.remove(handler);
		
	}

	//處理消息
	public void handleMessage(final ChatMessage message) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for(IChatMessageHandler handler : mMessageHandlers){
					handler.handlerMessage(message);
				}
			}
		});		
	}
	
	//延時一秒處理消息
	public void handleDelayMessage(final ChatMessage message) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				for(IChatMessageHandler handler : mMessageHandlers){
					handler.handlerMessage(message);
				}				
			}
		},1000);
	}

	public int getDeviceID() {
		return deviceID;
	}

	public String getLocalIP(){
		return IPv4Util.getLocalIpAddress(getApplicationContext());
	}
	
	public void reward2Audio(int deviceId,int state){
		Intent intent=new Intent(getApplicationContext(), AudioActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(AudioActivity.DEVICE_ID, deviceId);
		intent.putExtra(AudioActivity.AUDIO_STATE_ID, state);
		startActivity(intent);
	}
	public void reward2Video(int deviceId,int state){
		Intent intent=new Intent(getApplicationContext(), VideoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(VideoActivity.DEVICE_ID, deviceId);
		intent.putExtra(VideoActivity.VIDEO_STATE_ID, state);
		
		startActivity(intent);
	}
	public People getmUser() {
		return mUser;
	}
}
