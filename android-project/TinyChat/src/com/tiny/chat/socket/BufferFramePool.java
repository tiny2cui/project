package com.tiny.chat.socket;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.simit.audio.task.AudioDecoderThread;
import com.simit.audio.task.AudioRecorderThread;
import com.simit.video.client.Rtspclient;
import com.simit.video.server.RtspServer;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.activity.AudioActivity;
import com.tiny.chat.activity.VideoActivity;
import com.tiny.chat.db.DBOperate;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.domain.FriendInfo;
import com.tiny.chat.domain.OnlineDevice;
import com.tiny.chat.domain.People;
import com.tiny.chat.domain.Position;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.DateUtils;
import com.tiny.chat.utils.TypeConvert;

public class BufferFramePool extends Thread {
	private static String TAG = "BufferFramePool";
	private LinkedList<FramePacket> mListPackages = new LinkedList<FramePacket>();
	private boolean isRunning;
	private DBOperate dbOperate;
	private Semaphore sync;
	private BaseApplication application;
	private UDPSocketService mUdpSocketService;
	private int mDeviceId = -1;
	AudioRecorderThread audioRecorderThread;
	// AudioReceiverThread audioReceiverThread;
	AudioDecoderThread decoderThread;

	RtspServer rtspServer;
	Rtspclient rtspClient;

	public Semaphore getSync() {
		return sync;
	}

	public BufferFramePool(DBOperate dbOperate, Semaphore sync) {
		this.dbOperate = dbOperate;
		this.sync = sync;
		isRunning = true;
		application = BaseApplication.getInstance();
		mUdpSocketService = UDPSocketService.getInstance();
		mDeviceId = BaseApplication.getInstance().getDeviceID();
	}

	@Override
	public void run() {
		super.run();
		while (isRunning) {
			try {
				sync.acquire(1);
				FramePacket packet = mListPackages.remove();
				if (packet != null) {
					disposeReceiveFrameData(packet);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addFramePackage(FramePacket packet) {
		if (mListPackages != null) {
			mListPackages.add(packet);
		}

	}

	private void disposeReceiveFrameData(FramePacket framePacket) {

		short frameType = (short) framePacket.getFrameType();
		Log.i(TAG, "frameType--->" + frameType);
		ChatMessage chatMessage;
		FramePacket responsePacket;
		byte[] response = new byte[5];
		byte[] messageId;
		SMSMessage message;
		switch (frameType) {
		case FrameType.INFO_MESSAGE: // 短消息
			if (mDeviceId != framePacket.getDestineID()) {
				return;
			}
			message = new SMSMessage(framePacket, 0, DateUtils.getNowtime());
			chatMessage = new ChatMessage(ChatMessage.MESSAGE_TEXT_DATA,
					message);
			message = dbOperate.saveMessage(message);
			application.handleMessage(chatMessage);
			messageId = TypeConvert.int2byte(message.getIdMessage());
			System.arraycopy(messageId, 0, response, 0, 4);
			response[4] = 1;
			responsePacket = new FramePacket(framePacket.getDestineID(),
					framePacket.getSourceID(), FrameType.RESPONSE_MESSAGE,
					response);
			mUdpSocketService.postMessage(responsePacket.getFramePacket(),
					responsePacket.getFramePacket().length);
			break;
			
		case FrameType.RESPONSE_MESSAGE:
			if (mDeviceId != framePacket.getDestineID()) {
				return;
			}
			messageResponse(framePacket);
			
			break;
		case FrameType.INFO_AUDIO: // 語音数据

			break;

		case FrameType.INFO_VIDEO: // 視屏数据

			break;

		case FrameType.INFO_FILE: // 文件数据
			if (mDeviceId != framePacket.getDestineID()) {
				return;
			}
			FileReceiveDispose.saveFile(framePacket);
			break;

		case FrameType.LAN_LOGIN_RESPONSE:
			byte[] data = framePacket.getData();
			if (data[0] == 1) {
				// 登录成功
				chatMessage = new ChatMessage(
						ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, true);
			} else {
				chatMessage = new ChatMessage(
						ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, false);
			}
			application.handleMessage(chatMessage);
			break;

		case FrameType.LAN_LOGIN_BROADCAST: // 上線通知
			People loginPeople = new People(framePacket.getSourceID());
			application.addOnlineUsers(framePacket.getSourceID(), loginPeople);
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, loginPeople);
			application.handleMessage(chatMessage);
			// TODO 发送获取友邻资料、位置、环境等信息
			FramePacket friendInfoPacket = new FramePacket(application.getDeviceID(), framePacket.getSourceID(),
					FrameType.LAN_FRIEND_INFORMATION_REQUEST, null);
			mUdpSocketService.postMessage(friendInfoPacket.getFramePacket(),friendInfoPacket.getFramePacket().length);
			//位置信息
			friendInfoPacket.setFrameType(FrameType.LAN_POSITION_REQUEST);
			friendInfoPacket.setFrameSourceID(application.getDeviceID());
			friendInfoPacket.setFrameDestineID(framePacket.getSourceID());
			friendInfoPacket.setFrameDataLength(0);
			friendInfoPacket.packageItemsToFrame();
			mUdpSocketService.postMessage(friendInfoPacket.getFramePacket(),friendInfoPacket.getFramePacket().length);
			break;

		case FrameType.LAN_LOGOUT_BROADCAST: // 下線通知
			People logoutPeople = application.getOnlineUsers().remove(
					framePacket.getSourceID());
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_FRIEND_LOGOUT_DATA, logoutPeople);
			application.handleMessage(chatMessage);
			break;

		case FrameType.MESSAGE_AUDIO_CONTROL_START:
			// chatMessage=new
			// ChatMessage(ChatMessage.MESSAGE_AUDIO_CONTROL_START, null);
			// application.handleMessage(chatMessage);
			application.reward2Audio(framePacket.getSourceID(),
					AudioActivity.AUDIO_WAIT);
			break;

		case FrameType.MESSAGE_AUDIO_CONTROL_PLAY:

			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_AUDIO_CONTROL_PLAY, null);
			application.handleMessage(chatMessage);
			break;

		case FrameType.MESSAGE_AUDIO_CONTROL_REFUSES:

			chatMessage = new ChatMessage(ChatMessage.MESSAGE_AUDIO_CONTROL_REFUSES, null);
			application.handleMessage(chatMessage);
			break;

		case FrameType.MESSAGE_AUDIO_CONTROL_STOP:

			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_AUDIO_CONTROL_STOP, null);
			application.handleMessage(chatMessage);
			break;
		case FrameType.MESSAGE_AUDIO_DATA:

			if (decoderThread == null) {
				decoderThread = AudioDecoderThread.getInstance();
			}
			decoderThread.addData(framePacket.getData(),
					framePacket.getData().length);
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_S2C:
			if (rtspClient == null) {
				rtspClient = Rtspclient.getInstance();
			}
			rtspClient.handMessage(framePacket.getData(),
					framePacket.getFrameType());
			break;
		case FrameType.MESSAGE_VIDEO_DATA:
			if (rtspClient == null) {
				rtspClient = Rtspclient.getInstance();
			}
			rtspClient.handMessage(framePacket.getData(),
					framePacket.getFrameType());
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_C2S:
			if (rtspServer == null) {
				rtspServer = RtspServer.getInstance();
			}
			rtspServer.handMessage(framePacket.getData());
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_START:
			application.reward2Video(framePacket.getSourceID(),
					VideoActivity.VIDEO_WAIT);
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_PLAY:
			// 接收到对方应答消息
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_VIDEO_CONTROL_PLAY, null);
			application.handleMessage(chatMessage);
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_REFUSES:
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_VIDEO_CONTROL_REFUSES, null);
			application.handleMessage(chatMessage);
			break;

		case FrameType.MESSAGE_VIDEO_CONTROL_STOP:
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_VIDEO_CONTROL_STOP, null);
			application.handleMessage(chatMessage);
			break;
			
		case FrameType.LAN_FRIEND_INFORMATION:  // 友邻资料
			friendInfo(framePacket);

			break;

		case FrameType.INTENET_ONLINE_DEVICE_RESPONSE: // 处理用户返回数据
			
			responseOnlineDevice(framePacket.getData());
			People onlinePeople = new People(framePacket.getSourceID());
			application.addOnlineUsers(framePacket.getSourceID(), onlinePeople);
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, onlinePeople);
			application.handleMessage(chatMessage);
			break;
		case FrameType.LAN_POSITION_REQUEST:  // 获取位置信息请求
			Position position=application.getmUser().getFriendInfo().getPosition();
			FramePacket positionPacket=new FramePacket(application.getDeviceID(), framePacket.getSourceID(), FrameType.INFO_POSITION, position.getFrame());
			mUdpSocketService.postMessage(positionPacket.getFramePacket(),
					positionPacket.getFramePacket().length);
			break;	
			
		case FrameType.INFO_POSITION:
			People people = application.getOnlineUsers().get(framePacket.getSourceID());
			if(people!=null){
				people.getFriendInfo().setPosition(new Position(framePacket.getData()));
			}
			break;

		default:
			break;
		}

	}

	private void messageResponse(FramePacket framePacket){
		byte[] infoBytes = framePacket.getData();
		if(infoBytes==null || infoBytes.length!=5){
			return;
		}
		byte[] idBytes = new byte[4];
		System.arraycopy(infoBytes, 0, idBytes, 0, 4);
		int messageId=TypeConvert.byte2int(idBytes);
		SMSMessage message=dbOperate.getMessageById(messageId);
		if(message!=null){
			message.setMessageState(infoBytes[4]);
			dbOperate.updateMessage(message);
		}
		
		
		
	}
	private void friendInfo(FramePacket framePacket) {
		byte[] infoBytes = framePacket.getData();
		if(infoBytes==null || infoBytes.length<40){
			return;
		}
		byte[] userIdBytes=new byte[2];
		System.arraycopy(infoBytes, 0,userIdBytes, 0, 2);
		byte[] userNameBytes=new byte[32];
		System.arraycopy(infoBytes, 2,userNameBytes, 0, 32);
		byte[] ipBytes=new byte[4];
		System.arraycopy(infoBytes, 34,ipBytes, 0, 4);
		
		People people = application.getOnlineUsers().get(framePacket.getSourceID());
		//在线用户列表中无此用户则添加至在线用户列表中
		if(people==null){
			people=new People(framePacket.getSourceID());
			application.addOnlineUsers(framePacket.getSourceID(), people);
			ChatMessage chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, people);
			application.handleMessage(chatMessage);
		}
		
		FriendInfo friendInfo = people.getFriendInfo();
		friendInfo.setUserID(TypeConvert.byte2short(userIdBytes));
		friendInfo.setIp(TypeConvert.byte2int(ipBytes));
		friendInfo.setUserName(new String(userNameBytes).toString());
		friendInfo.setRegisterType(infoBytes[38]);
		friendInfo.setNetState(infoBytes[39]);
	}

	private void responseOnlineDevice(byte[] data) {
		OnlineDevice device;
		ChatMessage chatMessage;
		if (data.length == 30) {
			device = new OnlineDevice(data);
			People onlinePeople = new People(device.getDeviceId());
			application.addOnlineUsers(device.getDeviceId(), onlinePeople);
			chatMessage = new ChatMessage(
					ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, onlinePeople);
			application.handleMessage(chatMessage);
			// TODO 添加友邻资料、位置、环境等信息

		} else if (data.length > 30) {
			int num = data.length / 30;
			byte[] tem = new byte[30];
			for (int i = 0; i < num; i++) {
				System.arraycopy(data, i * 30, tem, 0, 30);
				device = new OnlineDevice(tem);
				People onlinePeople = new People(device.getDeviceId());
				application.addOnlineUsers(device.getDeviceId(), onlinePeople);
				chatMessage = new ChatMessage(ChatMessage.MESSAGE_FRIEND_LOGIN_DATA, onlinePeople);
				application.handleMessage(chatMessage);
				// TODO 添加友邻资料、位置、环境等信息
			}        

		}
	}

	public void stopThread() {
		isRunning = false;
		application = null;

	}

}
