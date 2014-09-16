package com.tiny.chat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.simit.net.NetConfig;
import com.simit.net.domain.LocalProperty;
import com.simit.net.domain.NetType;
import com.simit.net.service.NetService;
import com.simit.net.utils.IPv4Util;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.dialog.BaseDialog;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.service.MessageServer;
import com.tiny.chat.socket.MessageFactory;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.Constant;
import com.tiny.chat.utils.IChatMessageHandler;
import com.tiny.chat.utils.RegisterType;
import com.tiny.chat.view.SettingSwitchButton;

public class SettingFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener, DialogInterface.OnClickListener,IChatMessageHandler {

//	private Button mAboutUsButton;
//	private Button mDeleteAllChattingInfoButton;
	private Button btnExit,btnLogin,btnLogout,btnStartServer,btnStopServer;

//	private ImageView mSettingInfoButton;
	private SettingSwitchButton mSoundSwitchButton;
	private SettingSwitchButton mVibrateSwitchButton;
//	private RelativeLayout mSettingInfoLayoutButton;
	private RadioButton rbPublicNet,rbPrivateNet;
	private CheckBox ckText, ckFile, ckVoice, ckVideo, ckSensor;
	private TextView tvServerState,tvClientState;
	private boolean text,file,voice,video,sensor;
	private EditText mEtClientId, mEtClientPwd, mEtServerIP;
	private LinearLayout mServerLoginLayout;
	private BaseDialog mDeleteCacheDialog; // 提示窗口
	private BaseDialog mExitDialog;

	private int mDialogFlag;
	private String serverIP;
	private Broadcast myBroadcast;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();
		localProperty.setDeveiceId(7);
		localProperty.setUserId(7);
		localProperty.setPassword("12345678");
		localProperty.setNetType(NetType.WSN);
		localProperty.setIpAddress(IPv4Util.getLocalIpAddress(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_setting, container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override	
	public void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.INTERIOR_SERVER_LOGIN);
		filter.addAction(Constant.INTERIOR_SERVER_LOGIN_FAIL);
		filter.addAction(Constant.INTERIOR_SERVER_LOGOUT);
		filter.addAction(Constant.INTERIOR_SERVER_LOGOUT_FAIL);
		filter.addAction(Constant.INTERIOR_SERVER_START);
		filter.addAction(Constant.INTERIOR_SERVER_START_FAIL);
		filter.addAction(Constant.INTERIOR_SERVER_STOP);
		filter.addAction(Constant.INTERIOR_SERVER_STOP_FAIL);
		myBroadcast =new Broadcast();
		getActivity().registerReceiver(myBroadcast, filter);
			
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onResume() {
		super.onResume();
		mApplication.registerMessageHandler(this);	
	}

	@Override
	public void onPause() {

		super.onPause();
	}
	
	@Override	
	public void onDestroy() {			
		super.onDestroy();
		mApplication.unRegisterMessageHandler(this);
		getActivity().unregisterReceiver(myBroadcast);
	}

	@Override
	protected void initViews() {
//		mSettingInfoButton = (ImageView) findViewById(R.id.btn_setting_my_information);
//		mSettingInfoLayoutButton = (RelativeLayout) findViewById(R.id.setting_my_info_layout);
		mSoundSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_sound);
		mVibrateSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_vibration);
//		mDeleteAllChattingInfoButton = (Button) findViewById(R.id.btn_delete_all_chattinginfo);
		//mAboutUsButton = (Button) findViewById(R.id.btn_about_us);
		
		rbPublicNet = (RadioButton) findViewById(R.id.rb_public_net);
		rbPrivateNet = (RadioButton) findViewById(R.id.rb_private_net);
		
		tvServerState = (TextView) findViewById(R.id.tv_server_state);
		tvClientState = (TextView) findViewById(R.id.tv_client_state);
		
		ckText = (CheckBox) findViewById(R.id.ck_text);
		ckFile = (CheckBox) findViewById(R.id.ck_file);
		ckVoice = (CheckBox) findViewById(R.id.ck_voice);
		ckVideo = (CheckBox) findViewById(R.id.ck_video);
		ckSensor = (CheckBox) findViewById(R.id.ck_sensor);
		
		mEtServerIP = (EditText) findViewById(R.id.et_server_ip);
		mEtClientId = (EditText) findViewById(R.id.et_client_id);
		mEtClientPwd= (EditText) findViewById(R.id.et_client_pwd);
		
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogout = (Button) findViewById(R.id.btn_logout);
		btnStartServer = (Button) findViewById(R.id.btn_start_server);
		btnStopServer = (Button) findViewById(R.id.btn_stop_server);
		btnExit = (Button) findViewById(R.id.btn_exit_application);
		mServerLoginLayout=(LinearLayout)findViewById(R.id.ll_user_name_pwd);
		serverIP=IPv4Util.getLocalIpAddress(getActivity());
		mEtServerIP.setText(serverIP);
	}

	@Override
	protected void initEvents() {
//		mSettingInfoButton.setOnClickListener(this);
//		mSettingInfoLayoutButton.setOnClickListener(this);
		mSoundSwitchButton.setOnCheckedChangeListener(this);
		mVibrateSwitchButton.setOnCheckedChangeListener(this);
//		mDeleteAllChattingInfoButton.setOnClickListener(this);
//		mAboutUsButton.setOnClickListener(this);
		ckText.setOnClickListener(checkBoxClickListener);
		ckFile.setOnClickListener(checkBoxClickListener);
		ckVoice.setOnClickListener(checkBoxClickListener);
		ckVideo.setOnClickListener(checkBoxClickListener);
		ckSensor.setOnClickListener(checkBoxClickListener);
		rbPrivateNet.setOnClickListener(radioButtonClickListener);
		rbPublicNet.setOnClickListener(radioButtonClickListener);
		btnLogin.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		btnStartServer.setOnClickListener(this);
		btnStopServer.setOnClickListener(this);
		btnExit.setOnClickListener(this);

	}

	@Override
	protected void init() {
		mDeleteCacheDialog = BaseDialog.getDialog(mActivity,
				R.string.dialog_tips, "删除聊天记录会删除所有接收的图片、语音和文件。", "确 定", this,
				"取 消", this);
		mDeleteCacheDialog.setButton1Background(R.drawable.btn_default_popsubmit);

		mExitDialog = BaseDialog.getDialog(mActivity, R.string.dialog_tips,
				"确定要退出软件", "确 定", this, "取 消", this);
		mExitDialog.setButton1Background(R.drawable.btn_default_popsubmit);

		mSoundSwitchButton.setChecked(BaseApplication.getSoundFlag());
		mVibrateSwitchButton.setChecked(BaseApplication.getVibrateFlag());

	}
	

	private OnClickListener checkBoxClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// Is the view now checked?
			boolean checked = ((CheckBox) view).isChecked();
			switch (view.getId()) {
			case R.id.ck_text:
				text=checked;				
				break;
			case R.id.ck_file:
				file=checked;
				break;
			case R.id.ck_voice:
				voice=checked;
				break;
			case R.id.ck_video:
				video=checked;
				break;
			case R.id.ck_sensor:
				sensor=checked;
				break;
			}
		}
	};

	private OnClickListener radioButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			boolean checked = ((RadioButton) view).isChecked();
		    
		    // Check which radio button was clicked
		    switch(view.getId()) {
		        case R.id.rb_public_net:
		            if (checked){
		            	mServerLoginLayout.setVisibility(View.VISIBLE);
		            	NetConfig.getInstance().getLocalProperty().setNetType(NetType.INTERNET);
		            }    
		            break;
		        case R.id.rb_private_net:
		            if (checked){
		            	mServerLoginLayout.setVisibility(View.GONE);
		            	NetConfig.getInstance().getLocalProperty().setNetType(NetType.WSN);
		            }       
		            break;
		    }
			
		}
	};

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (mDialogFlag) {
		case 1:
			if (which == 0) {
				// setAsyncTask(1);
			} else if (which == 1) {
				mDeleteCacheDialog.dismiss();
			}
			break;
		case 2:
			if (which == 0) {
				// setAsyncTask(2);
				Intent intent = new Intent(getActivity(), MessageServer.class);
				getActivity().stopService(intent);
				mExitDialog.dismiss();
				getActivity().finish();
			} else if (which == 1) {
				mExitDialog.dismiss();
			}
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkbox_sound:
			buttonView.setChecked(isChecked);
			BaseApplication.setSoundFlag(isChecked);
			break;

		case R.id.checkbox_vibration:
			buttonView.setChecked(isChecked);
			BaseApplication.setVibrateFlag(isChecked);
			break;

		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			byte type=RegisterType.getRegisterType(text, file, voice, video, sensor);
			FramePacket loginPacket=MessageFactory.getClientRegister(type);
			serverIP=mEtServerIP.getText().toString();
			UDPSocketService.getInstance().postMessage(loginPacket.getFramePacket(), loginPacket.getFramePacket().length,serverIP);
			break;
		case R.id.btn_logout:
			FramePacket loginoutPacket=MessageFactory.getClientUnRegister();
			serverIP=mEtServerIP.getText().toString();
			UDPSocketService.getInstance().postMessage(loginoutPacket.getFramePacket(), loginoutPacket.getFramePacket().length,serverIP);
			break;
		
		case R.id.btn_start_server:
			startServer();
			break;
		case R.id.btn_stop_server:
			stopServer();
			break;
		case R.id.btn_exit_application:
			mDialogFlag = 2;
			mExitDialog.show();
			break;

		default:
			break;
		}
	}

	@Override
	public void handlerMessage(ChatMessage message) {
		if (ChatMessage.MESSAGE_LOGIN_DATA.equals(message.getMessageId())) {
			boolean isLogin=(Boolean)message.getMessageData();
			if(isLogin){
				//TODO 登录成功
			}else{
				//TODO 登录失败
			}
		} 
		
	}
	
	private void startServer(){
		String idString=mEtClientId.getText().toString();
//		String name=userName.getText().toString();
		String passwordStr=mEtClientPwd.getText().toString();
//		String deviceStr=deveiceId.getText().toString();
		int id=Integer.valueOf(idString);
		int deveiceId=BaseApplication.getInstance().getDeviceID();
		LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();
//		localProperty.setName(name);
		localProperty.setUserId(id);
		localProperty.setPassword(passwordStr);
		localProperty.setDeveiceId(deveiceId);
		Intent i = new Intent(getActivity(), NetService.class);
		getActivity().startService(i);
	}
	
	private void stopServer(){
		Intent i = new Intent(getActivity(), NetService.class);
		getActivity().stopService(i);
	}
	
	 
	class Broadcast extends BroadcastReceiver { 
		
		@Override   
		public void onReceive(Context context, Intent intent) {
	           
			if(Constant.INTERIOR_SERVER_START.equals(intent.getAction())){
	          //服务器已经启动
	          // Toast.makeText(getActivity(), "服务已经启动", Toast.LENGTH_SHORT).show();
	           tvServerState.setText("启动状态：已开启");
			}else if(Constant.INTERIOR_SERVER_START_FAIL.equals(intent.getAction())){
			   tvServerState.setText("启动状态：已关闭");
			}else if(Constant.INTERIOR_SERVER_STOP.equals(intent.getAction())){
			   tvServerState.setText("启动状态：已关闭");
			}else if(Constant.INTERIOR_SERVER_STOP_FAIL.equals(intent.getAction())){
			   tvServerState.setText("启动状态：已关闭");
			}	       
		}    
	}

}
