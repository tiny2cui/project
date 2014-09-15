package com.tiny.chat.service;

import com.simit.net.utils.IPv4Util;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.dialog.FlippingLoadingDialog;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.socket.FrameType;
import com.tiny.chat.socket.MessageFactory;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.Constant;
import com.tiny.chat.utils.MyLog;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.Vibrator;

public class MessageServer extends Service implements IPlayNotification {
	private static final String TAG = MessageServer.class.getName();
	private UDPSocketService udpSocketService;
	private static SoundPool notificationMediaplayer;
	private static Vibrator notificationVibrator;
	private static int notificationMediaplayerID;
	SharedPreferences mSharedPreferences;
	private int deviceId;
	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.i(TAG, "Service ----> onCreate");
		if (notificationMediaplayer == null) {
			notificationMediaplayer = new SoundPool(3,
					AudioManager.STREAM_SYSTEM, 5);
			notificationMediaplayerID = notificationMediaplayer.load(this,
					R.raw.crystalring, 1);
		}
		if (notificationVibrator == null) {
			notificationVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARENAME, Context.MODE_PRIVATE);
		udpSocketService = UDPSocketService.getInstance();
		udpSocketService.setContext(this);
		udpSocketService.setPlayNotification(this);
		udpSocketService.start();
		deviceId=BaseApplication.getInstance().getDeviceID();
	}

	@Override
	public void onDestroy() {
		MyLog.i(TAG, "Remote Service ----> onDestroy");
		super.onDestroy();
		sendLogout();
		udpSocketService.stopService();
		udpSocketService = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MyLog.i(TAG, "Remote Service ----> onStartCommand");
		sendLogin();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 新消息提醒 - 包括声音提醒、振动提醒
	 */
	@Override
	public void playNotification() {
		if (BaseApplication.getSoundFlag()) {
			MyLog.i("play--->", "play notifaction " + notificationMediaplayerID);
			notificationMediaplayer.play(notificationMediaplayerID, 1, 1, 0, 0,
					1);
		}
		if (BaseApplication.getVibrateFlag()) {
			notificationVibrator.vibrate(200);
		}

	}

	//客户端注册
	private void sendLogin() {
		//TODO ip地址通过配置保存
		int loginType=mSharedPreferences.getInt(Constant.NET_TYPE, 0);
		FramePacket packet = MessageFactory.getClientLogin((byte)loginType);
		udpSocketService.postMessage(packet.getFramePacket(),
				packet.getFrameLength(), IPv4Util.getLocalIpAddress(this));

	}
//	private void sendTest(){
//		String login = "login hello";
//		FramePacket packet = new FramePacket(deviceId, 0,FrameType.LAN_LOGIN_BROADCAST, login.getBytes());
//		udpSocketService.postMessage(packet.getFramePacket(),
//				packet.getFrameLength(), "255.255.255.255");
//	}
	
	//客户端注销
	private void sendLogout(){
		//TODO ip地址通过配置保存
		String login = "login hello";
		FramePacket packet = new FramePacket(2, 0,
				FrameType.LAN_LOGOUT_BROADCAST, login.getBytes());
		udpSocketService.postMessage(packet.getFramePacket(),
				packet.getFrameLength(), IPv4Util.getLocalIpAddress(this));
	}

	@Override
	public IBinder onBind(Intent intent) {
		MyLog.i(TAG, "Service ----> onBind");
		return null;
	}

}
