package com.tiny.chat.activity;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import com.simit.audio.task.AudioDecoderThread;
import com.simit.audio.task.AudioRecorderThread;
import com.simit.net.NetConfig;
import com.simit.video.client.Rtspclient;
import com.simit.video.rtspclient.concepts.ClientResponse;
import com.simit.video.rtspclient.concepts.Request.Method;
import com.simit.video.server.RtspServer;
import com.simit.video.server.Session;
import com.simit.video.stream.VideoQuality;
import com.simit.video.test.FramePacket;
import com.simit.video.test.SendAndReceive;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.socket.FrameType;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.IChatMessageHandler;
import com.tiny.chat.utils.MyLog;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class VideoActivity extends Activity implements OnClickListener,
		OnSharedPreferenceChangeListener, SendAndReceive ,IChatMessageHandler{
	private String TAG=VideoActivity.class.getName();
	private SurfaceView camera,sfv;
	private SurfaceHolder holder,sfh;
	private TextView tvVideoPromp;
	private LinearLayout optionLayout;
	private Button btnVideo, btnAnswer, btnRefuses; 
	private VideoQuality defaultVideoQuality = new VideoQuality();
	private SharedPreferences settings;
	private PowerManager.WakeLock wl;
	private RtspServer rtspServer;
//	private TextView console, ip;

	// client
	private Rtspclient rtspClient;
	private Bitmap bitmap;
	private Canvas canvas;
	private int width = 320;
	private int height = 240;
	public static int Surface_width;
	public static int Surface_height;
	private Draw_Image mDraw_Image;
	private String s_uri = "";
	private int rtsp_port = 8086;
	DatagramSocket socket;
	DatagramPacket sendPacket;
	public static String DEVICE_ID = "device_id";
	public static String VIDEO_STATE_ID = "video_state_id";
	public static int VIDEO_REQUEST = 1, VIDEO_WAIT = 2;
	private int deviceId, videoState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		initView();
		
//		console = (TextView) findViewById(R.id.console);
//		ip = (TextView) findViewById(R.id.ip);
		
		initParameters();
		initSession();

		rtspServer = RtspServer.getInstance();
		rtspServer.init(rtsp_port, handler, this);
		try {
			rtspClient = Rtspclient.getInstance();
			rtspClient.init(this,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		initDrawPannel();
		if(videoState==VIDEO_REQUEST){
			requestVideo();
		}else if(videoState==VIDEO_WAIT){
			tvVideoPromp.setText(deviceId+"发起视频邀请");
			optionLayout.setVisibility(View.VISIBLE);
			btnVideo.setVisibility(View.GONE);
		}

	}

	private void initView(){
		tvVideoPromp=(TextView)findViewById(R.id.tv_video_prompt);
		optionLayout=(LinearLayout)findViewById(R.id.ll_answer_refuses_video);
		btnVideo =(Button)findViewById(R.id.btn_video); 
		btnAnswer=(Button)findViewById(R.id.btn_answer_video);
		btnRefuses=(Button)findViewById(R.id.btn_refuses_video); 
		btnVideo.setOnClickListener(this);
		btnAnswer.setOnClickListener(this);
		btnRefuses.setOnClickListener(this);
		camera = (SurfaceView) findViewById(R.id.surface_local);
		camera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder = camera.getHolder();
		
		sfv = (SurfaceView) this.findViewById(R.id.surface_remote);
		sfh = sfv.getHolder();
		sfh.addCallback(new SurfaceCallBack());
		bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		mDraw_Image = new Draw_Image();
		
		
		deviceId = getIntent().getIntExtra(DEVICE_ID, 0);
		videoState = getIntent().getIntExtra(VIDEO_STATE_ID, 0);
		
		
		
	}
	
	/**
	 * 请求通话
	 */
	private void requestVideo(){
		tvVideoPromp.setText("等待对方应答。。。");
		optionLayout.setVisibility(View.GONE);
		btnVideo.setVisibility(View.VISIBLE);
		btnVideo.setText("取消");
		byte[] data=new byte[1];
		sendData(data, FrameType.MESSAGE_VIDEO_CONTROL_START);
	}
	
	/**
	 * 拒绝通话
	 */
	private void refuseVideo(){
		tvVideoPromp.setText("已拒绝与对方通话。。。");
		sendData(new byte[1], FrameType.MESSAGE_VIDEO_CONTROL_REFUSES);
	}
	
	/**
	 * 应答通话
	 */
	private void answerVideo(){
		// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
		MyLog.i(TAG, ChatMessage.MESSAGE_VIDEO_CONTROL_PLAY);
		sendData(new byte[1], FrameType.MESSAGE_VIDEO_CONTROL_PLAY);
		optionLayout.setVisibility(View.GONE);
		btnVideo.setVisibility(View.VISIBLE);
		btnVideo.setText("挂断");
		
		
	}
	/**
	 * 停止通话
	 */
	private void stopVideo(){
		if(videoState==VIDEO_REQUEST){
			//请求端终止
			rtspClient.do_pause();
			mDraw_Image.stopThread();
			//rtspServer.stop();
		}else if(videoState==VIDEO_WAIT){
			//应答端终止
			//rtspClient.do_pause();
			//mDraw_Image.stopThread();
			rtspServer.stop();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// Step1 init parameters from sharepreference
	void initParameters() {
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		defaultVideoQuality.resX = settings.getInt("video_resX", 320);
		defaultVideoQuality.resY = settings.getInt("video_resY", 240);
		defaultVideoQuality.frameRate = Integer.parseInt(settings.getString(
				"video_framerate", "15"));
		defaultVideoQuality.bitRate = Integer.parseInt(settings.getString(
				"video_bitrate", "500")) * 1000; // 500 kb/s
		s_uri = settings.getString("video_dst", "rtsp://192.168.0.2:8086/");
		settings.registerOnSharedPreferenceChangeListener(this);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"net.majorkernelpanic.spydroid.wakelock");

	}

	// step 2
	void initSession() {
		Session.setSurfaceHolder(holder);
		Session.setDefaultVideoQuality(defaultVideoQuality);
		// Session.setDefaultAudioEncoder(settings.getBoolean("stream_audio",
		// true)?Integer.parseInt(settings.getString("audio_encoder", "1")):0);
		Session.setDefaultVideoEncoder(settings
				.getBoolean("stream_video", true) ? Integer.parseInt(settings
				.getString("video_encoder", "1")) : 0);

	}

	// step 3
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("video_resX")) {
			defaultVideoQuality.resX = sharedPreferences.getInt("video_resX",
					640);
			Session.setDefaultVideoQuality(defaultVideoQuality);
		} else if (key.equals("video_resY")) {
			defaultVideoQuality.resY = sharedPreferences.getInt("video_resY",
					480);
			Session.setDefaultVideoQuality(defaultVideoQuality);
		} else if (key.equals("video_framerate")) {
			defaultVideoQuality.frameRate = Integer.parseInt(sharedPreferences
					.getString("video_framerate", "15"));
			Session.setDefaultVideoQuality(defaultVideoQuality);
		} else if (key.equals("video_bitrate")) {
			defaultVideoQuality.bitRate = Integer.parseInt(sharedPreferences
					.getString("video_bitrate", "500")) * 1000;
			Session.setDefaultVideoQuality(defaultVideoQuality);
		} else if (key.equals("stream_audio") || key.equals("audio_encoder")) {
			Session.setDefaultAudioEncoder(sharedPreferences.getBoolean(
					"stream_audio", false) ? Integer.parseInt(sharedPreferences
					.getString("audio_encoder", "1")) : 0);
		} else if (key.equals("stream_video") || key.equals("video_encoder")) {
			Session.setDefaultVideoEncoder(sharedPreferences.getBoolean(
					"stream_video", true) ? Integer.parseInt(sharedPreferences
					.getString("video_encoder", "1")) : 0);
		} else if (key.equals("enable_http")) {

		} else if (key.equals("enable_rtsp")) {
			if (sharedPreferences.getBoolean("enable_rtsp", true)) {
				rtspServer = new RtspServer();
				rtspServer.init(rtsp_port, handler, this);
			} else {
				if (rtspServer != null)
					rtspServer = null;
			}
		}
	}

	// step 4 handler
	// The Handler that gets information back from the RtspServer
	private final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

//			case RtspServer.MESSAGE_LOG:
//				log((String) msg.obj);
//				break;
//
//			case RtspServer.MESSAGE_ERROR:
//				log((String) msg.obj);
//				break;

			case Session.MESSAGE_START:
				// Sent when streaming starts
				// logo.setAlpha(100);
				camera.setBackgroundDrawable(null);
				break;

			case Session.MESSAGE_STOP:
				// Sent when streaming ends
				camera.setBackgroundDrawable(null);
				// logo.setAlpha(255);
				break;

//			case Session.MESSAGE_ERROR:
//				log((String) msg.obj);
//				break;

			}
		}

	};

	// step 5 log
//	public void log(String s) {
//		String t = console.getText().toString();
//		if (t.split("\n").length > 8) {
//			console.setText(t.substring(t.indexOf("\n") + 1, t.length()));
//		}
//		console.append(Html.fromHtml(s + "<br />"));
//	}

//	private void displayIpAddress(WifiInfo wifiInfo) {
//		if (wifiInfo != null && wifiInfo.getNetworkId() > -1) {
//			int i = wifiInfo.getIpAddress();
//			ip.setText("rtsp://");
//			ip.append(String.format("%d.%d.%d.%d", i & 0xff, i >> 8 & 0xff,
//					i >> 16 & 0xff, i >> 24 & 0xff));
//			ip.append(":8086/");
//		} else {
//			ip.setText("Wifi should be enabled !");
//		}
//	}

	// step 6 options

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Intent intent;

		switch (item.getItemId()) {
		// case R.id.option:
		// // Starts QualityListActivity where user can change the streaming
		// // quality
		// intent = new Intent(this.getBaseContext(), OptionsActivity.class);
		// startActivityForResult(intent, 0);
		// return true;
		case R.id.do_play:
			rtspClient.do_option("*");
			mDraw_Image.startThread();
			return true;
		case R.id.do_stop:
			rtspClient.do_pause();
			mDraw_Image.stopThread();
			rtspServer.stop();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// step 7 do start
	public void onStart() {
		super.onStart();
		// Lock screen
		wl.acquire();
	}

	public void onStop() {
		super.onStop();
		wl.release();
	}

	public void onResume() {
		super.onResume();
		BaseApplication app = BaseApplication.getInstance();
		app.registerMessageHandler(this);
		// Determines if user is connected to a wireless network & displays ip
//		WifiManager wifiManager = (WifiManager) this
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//		displayIpAddress(wifiInfo);

		rtspServer.start();

		// DatagramPacket packet=new DatagramPacket(new byte[1024], 1024);
		// startServers();

		// registerReceiver(wifiStateReceiver,new
		// IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

	}

	public void onPause() {
		super.onPause();
		BaseApplication app = BaseApplication.getInstance();
		app.unRegisterMessageHandler(this);
		stopServers();
		// unregisterReceiver(wifiStateReceiver);
	}

	private void stopServers() {
		if (rtspServer != null)
			rtspServer.stop();
	}

	// private void startServers() {
	//
	// if (rtspServer != null) {
	// try {
	// rtspServer.start();
	// } catch (IOException e) {
	// log("RtspServer could not be started : " + e.getMessage());
	// }
	// }
	// PublicExternalNet net = new PublicExternalNet();
	// net.login();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// socket = new DatagramSocket();
	// byte[] buffer = new byte[1550];
	// DatagramPacket packet = new DatagramPacket(new byte[1], 1);
	// while (true) {
	// try {
	// packet.setData(buffer);
	// packet.setLength(buffer.length);
	// socket.receive(packet);
	// receive(packet.getData(), packet.getLength(),
	// packet.getAddress().getHostAddress());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// } catch (SocketException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }).start();
	// }

	@Override
	public void sendData(byte[] data, int frameType) {

		// FramePacket framePacket = new FramePacket(7, 8, frameType, data);
		// sendPacket.setData(framePacket.getFramePacket());
		// sendPacket.setLength(framePacket.getFramePacket().length);
		// try {
		// socket.send(sendPacket);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		FramePacket framePacket = new FramePacket(NetConfig.getInstance()
				.getLocalProperty().getDeveiceId(), deviceId, frameType, data);
		UDPSocketService.getInstance().postMessage(framePacket.getFramePacket(),framePacket.getFramePacket().length);

	}

	// private void receive(byte[] data, int dataLen, String ip) {
	//
	// // 处理获取到的每帧数据
	// FramePacket framePacket = new FramePacket(data, data.length);
	// framePacket.setIpAddress(ip);
	// if (framePacket.getFrameType() == Constants.MESSAGE_VIDEO_CONTROL_S2C
	// || framePacket.getFrameType() == Constants.MESSAGE_VIDEO_DATA) {
	// rtspClient.handMessage(framePacket.getData(),
	// framePacket.getFrameType());
	// } else if (framePacket.getFrameType() ==
	// Constants.MESSAGE_VIDEO_CONTROL_C2S) {
	// rtspServer.handMessage(framePacket.getData());
	// }
	// }

//	private void initDrawPannel() {
//		sfv = (SurfaceView) this.findViewById(R.id.surface_remote);
//		sfh = sfv.getHolder();
//		sfh.addCallback(new SurfaceCallBack());
//		bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
//		mDraw_Image = new Draw_Image();
//	}

	ClientResponse response = new ClientResponse() {

		@Override
		public void response(Method method) {
			switch (method) {
			case OPTIONS:
				if(rtspClient!=null){
					rtspClient.responseOPTIONS();
				}
				break;
			case DESCRIBE:

				if(rtspClient!=null){
					rtspClient.responseDESCRIBE();
				}
				break;

			case SETUP:

				if(rtspClient!=null){
					rtspClient.responseSETUP();
				}
				break;
			case PLAY:

				break;
			case TEARDOWN:
				if(rtspClient!=null){
					rtspClient.responseTEARDOWN();
				}
				break;
			}

		}
	};

	class SurfaceCallBack implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int format, int width,
				int height) {
			VideoActivity.Surface_width = width;
			VideoActivity.Surface_height = height;

		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
		}
	}

	class Draw_Image extends Thread {
		private volatile Thread runner;

		public void startThread() {
			if (runner == null) {
				runner = new Thread(this);
				runner.start();
			}
		}

		public void stopThread() {
			if (runner != null) {
				Thread moribund = runner;
				runner = null;
				moribund.interrupt();
			}
		}

		public void run() {
			while (Thread.currentThread() == runner) {
				// int temp = 0;
				if (rtspClient.isStream()) {
					if (rtspClient.getLVP().getDecoder().isGetData()) {
						ByteBuffer buffer = ByteBuffer.wrap(rtspClient.getLVP()
								.getDecoder().getData());
						bitmap.copyPixelsFromBuffer(buffer);
						Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
								Surface_width, Surface_height, false);

						// if((temp%20)==0){
						// saveBitmap(bmp,null,"pic"+temp,CompressFormat.JPEG);
						// }
						// temp++;

						canvas = sfh.lockCanvas(new Rect(0, 0, Surface_width,
								Surface_height));
						canvas.drawBitmap(bmp, 0, 0, new Paint());
						sfh.unlockCanvasAndPost(canvas);

					}
				}
			}
		};

		// public File saveBitmap(Bitmap bitmap, String directory,
		// String filename, CompressFormat format) {
		//
		// if (directory == null) {
		// directory = "sdcard/test";
		// } else {
		// // Check if the given directory exists or try to create it.
		// File file = new File(directory);
		// if (!file.isDirectory() && !file.mkdirs()) {
		// return null;
		// }
		// }
		//
		// File file = null;
		// OutputStream os = null;
		//
		// try {
		// filename = (format == CompressFormat.PNG) ? filename + ".png"
		// : filename + ".jpg";
		// file = new File(directory, filename);
		// os = new FileOutputStream(file);
		// bitmap.compress(format, 100, os);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// os.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// return file;
		// }

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_video:
			//挂断
			stopVideo();
			break;
		case R.id.btn_answer_video:
			//同意请求
			answerVideo();
			break;
		case R.id.btn_refuses_video:
			//拒绝请求
			refuseVideo();
			break;
		}
		
	}

	@Override
	public void handlerMessage(ChatMessage message) {
		if (ChatMessage.MESSAGE_VIDEO_CONTROL_PLAY.equals(message
				.getMessageId())) {
			// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
			MyLog.i(TAG, ChatMessage.MESSAGE_VIDEO_CONTROL_PLAY);
			rtspClient.do_option("*");
			mDraw_Image.startThread();
		}else if (ChatMessage.MESSAGE_VIDEO_CONTROL_REFUSES.equals(message
				.getMessageId())) {
			// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
			MyLog.i(TAG, ChatMessage.MESSAGE_VIDEO_CONTROL_REFUSES);
			tvVideoPromp.setVisibility(View.VISIBLE);
			tvVideoPromp.setText("对方拒绝视频通信");
			optionLayout.setVisibility(View.GONE);
			btnVideo.setVisibility(View.GONE);
			
		}else if(ChatMessage.MESSAGE_VIDEO_CONTROL_STOP.equals(message
				.getMessageId())){
			stopVideo();
		}
		
	}

}
