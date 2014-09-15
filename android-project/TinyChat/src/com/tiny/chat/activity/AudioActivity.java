package com.tiny.chat.activity;

import com.simit.audio.task.AudioDecoderThread;
import com.simit.audio.task.AudioRecorderThread;
import com.simit.audio.util.SendAndReceive;
import com.simit.net.NetConfig;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.socket.FrameType;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.IChatMessageHandler;
import com.tiny.chat.utils.MyLog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AudioActivity extends FragmentActivity implements OnClickListener,
		IChatMessageHandler {
	private String TAG = "AudioActivity";
	private TextView tvAudioPromp;
	private LinearLayout optionLayout;
	private Button btnAudio, btnAnswer, btnRefuses;
	private int audioState;
	public static String DEVICE_ID = "device_id";
	public static String AUDIO_STATE_ID = "audio_state_id";
	public static int AUDIO_REQUEST = 1, AUDIO_WAIT = 2;
	private int deviceId;
	private Handler mHandler;
	AudioRecorderThread audioRecorderThread;
	AudioDecoderThread decoderThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		initView();
		deviceId = getIntent().getIntExtra(DEVICE_ID, 0);
		audioState = getIntent().getIntExtra(AUDIO_STATE_ID, 0);
		if (audioState == AUDIO_REQUEST) {
			requestAudio();
		} else if (audioState == AUDIO_WAIT) {
			tvAudioPromp.setText(deviceId + "发起会话邀请");
			optionLayout.setVisibility(View.VISIBLE);
			btnAudio.setVisibility(View.GONE);
		}
		mHandler=new Handler();
	}

	private void initView() {
		tvAudioPromp = (TextView) findViewById(R.id.tv_audio_prompt);
		btnAudio = (Button) findViewById(R.id.btn_audio);
		btnAnswer = (Button) findViewById(R.id.btn_answer_audio);
		btnRefuses = (Button) findViewById(R.id.btn_refuses_audio);
		optionLayout = (LinearLayout) findViewById(R.id.ll_answer_refuses_audio);
		btnAnswer.setOnClickListener(this);
		btnRefuses.setOnClickListener(this);
		btnAudio.setOnClickListener(this);

	}

	@Override
	protected void onResume() {

		super.onResume();
		BaseApplication app = BaseApplication.getInstance();
		app.registerMessageHandler(this);
	}

	@Override
	protected void onPause() {
		BaseApplication app = BaseApplication.getInstance();
		app.unRegisterMessageHandler(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_audio:
			if (AUDIO_REQUEST == audioState) {
				stopAudio("終止通話");
			}
			break;
		case R.id.btn_answer_audio:
			answerAudio();
			break;
		case R.id.btn_refuses_audio:
			refusesAudio();
			break;
		}

	}

	private void sendData1(byte[] data, int frameType) {
		FramePacket framePacket = new FramePacket(NetConfig.getInstance()
				.getLocalProperty().getDeveiceId(), deviceId, frameType, data);
		UDPSocketService.getInstance().postMessage(framePacket.getFramePacket(),framePacket.getFramePacket().length);

	}

	/**
	 * 請求通話
	 */
	private void requestAudio() {
		tvAudioPromp.setText("等待对方接听。。。");
		optionLayout.setVisibility(View.GONE);
		btnAudio.setVisibility(View.VISIBLE);
		btnAudio.setText("取消");
		byte[] data = new byte[1];
		data[0] = 0;
		sendData1(data, FrameType.MESSAGE_AUDIO_CONTROL_START);
		
	}

	/**
	 * 拒絕通話
	 */
	private void refusesAudio() {
		tvAudioPromp.setText("已拒绝与对方通话。。。");
		sendData1(new byte[1], FrameType.MESSAGE_AUDIO_CONTROL_REFUSES);
	}

	private void stopAudio(String str) {
		tvAudioPromp.setText(str);
		sendData1(new byte[1], FrameType.MESSAGE_AUDIO_CONTROL_STOP);
		decoderThread = AudioDecoderThread.getInstance();
		audioRecorderThread = AudioRecorderThread.getInstance();
			if(audioRecorderThread!=null){
				audioRecorderThread.stopRecording();
				audioRecorderThread.release();
				audioRecorderThread=null;
			}
			if(decoderThread!=null){
				decoderThread.stopDecoding();
				decoderThread.release();
				decoderThread=null;
			}
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					finish();
				}
			}, 5000);	
			
	}

	/**
	 * 應答通話
	 */
	private void answerAudio() {
		// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
		MyLog.i(TAG, ChatMessage.MESSAGE_AUDIO_CONTROL_PLAY);
		sendData1(new byte[1], FrameType.MESSAGE_AUDIO_CONTROL_PLAY);
		if (audioRecorderThread == null) {
			audioRecorderThread = AudioRecorderThread.getInstance();
		}
		audioRecorderThread.startRecording(new SendAndReceive() {

			@Override
			public void sendData(byte[] data, int frameType) {
				sendData1(data, frameType);
			}
		});
		
		// 启动解码器
		if (decoderThread == null) {
			decoderThread = AudioDecoderThread.getInstance();
		}
		decoderThread.startDecoding();
		
		tvAudioPromp.setText("正在通话");
		optionLayout.setVisibility(View.GONE);
		btnAudio.setVisibility(View.VISIBLE);
		btnAudio.setText("挂断");
		
	}

	@Override
	public void handlerMessage(ChatMessage message) {
//		if (ChatMessage.MESSAGE_AUDIO_CONTROL_START.equals(message
//				.getMessageId())) {
//			// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
//			MyLog.i(TAG, ChatMessage.MESSAGE_AUDIO_CONTROL_START);
//			if (audioRecorderThread == null) {
//				audioRecorderThread = AudioRecorderThread.getInstance();
//			}
//			audioRecorderThread.startRecording(new SendAndReceive() {
//
//				@Override
//				public void sendData(byte[] data, int frameType) {
//					sendData1(data, frameType);
//				}
//			});
//			sendData1(new byte[1], FrameType.MESSAGE_AUDIO_CONTROL_PLAY);
//			// 启动解码器
//			if (decoderThread == null) {
//				decoderThread = AudioDecoderThread.getInstance();
//			}
//			decoderThread.startDecoding();
//
//		} else 
		
			if (ChatMessage.MESSAGE_AUDIO_CONTROL_PLAY.equals(message
				.getMessageId())) {
			// 接收到对方请求通话命令 1：启动语音编码 2：启动解码器接收数据
			MyLog.i(TAG, ChatMessage.MESSAGE_AUDIO_CONTROL_PLAY);
			if (audioRecorderThread == null) {
				audioRecorderThread = AudioRecorderThread.getInstance();
			}
			audioRecorderThread.startRecording(new SendAndReceive() {
				@Override
				public void sendData(byte[] data, int frameType) {
					sendData1(data, frameType);
				}
			});
			btnAudio.setText("挂断");
			// 启动解码器
			if (decoderThread == null) {
				decoderThread = AudioDecoderThread.getInstance();
			}
			decoderThread.startDecoding();
			
		}else if (ChatMessage.MESSAGE_AUDIO_CONTROL_REFUSES.equals(message
				.getMessageId())) {
			tvAudioPromp.setText("对方拒绝语音通话");
			if (decoderThread == null) {
				decoderThread = AudioDecoderThread.getInstance();
			}
			decoderThread.stopDecoding();
			decoderThread=null;
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					finish();
				}
			},5000);
			
		}else if(ChatMessage.MESSAGE_AUDIO_CONTROL_STOP.equals(message
				.getMessageId())) {
			stopAudio("对方终止了语音通话");
			
		}

	}
}
