package com.tiny.chat.activity;



import com.tiny.chat.R;
import com.tiny.chat.domain.FriendInfo;
import com.tiny.chat.fragment.MessageFragment;
import com.tiny.chat.utils.RegisterType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


public class InfoActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "SZU_SettingMyInfoActivity";
	public static String USER_DATA="user_data";
	private static final int REQUEST_CODE = 1;
	private EditText mEtNickname, mServerIP;
	private ImageView mIvAvater;
//	private RadioButton rbPublicNet,rbPrivateNet;
	private CheckBox ckText, ckFile, ckVoice, ckVideo, ckSensor;
	private TextView tvDeviceId,tvNetState;
//	private boolean text,file,voice,video,sensor;
	private Button mBtnBack,mBtnNext,mBtnSendMessage;
	private int mAvatar;
	private String mNickname = "tiny";
	private FriendInfo friendInfo;
	int sourceID;
	private RegisterType registerType ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myprofile);
		initViews();
		initData();
		initEvents();
	}

	@Override
	protected void initViews() {
		mIvAvater = (ImageView) findViewById(R.id.setting_my_avater_img);
		mEtNickname = (EditText) findViewById(R.id.setting_my_nickname);
		mServerIP = (EditText) findViewById(R.id.et_server_ip);
		// registerType=(Spinner) findViewById(R.id.sp_register_type);
//		rbPublicNet = (RadioButton) findViewById(R.id.rb_public_net);
//		rbPrivateNet = (RadioButton) findViewById(R.id.rb_private_net);
		
		ckText = (CheckBox) findViewById(R.id.ck_text);
		ckFile = (CheckBox) findViewById(R.id.ck_file);
		ckVoice = (CheckBox) findViewById(R.id.ck_voice);
		ckVideo = (CheckBox) findViewById(R.id.ck_video);
		ckSensor = (CheckBox) findViewById(R.id.ck_sensor);

		tvDeviceId = (TextView) findViewById(R.id.tv_device_id);
		tvNetState = (TextView) findViewById(R.id.tv_netstate);
		mBtnBack = (Button) findViewById(R.id.setting_btn_back);
		mBtnNext = (Button) findViewById(R.id.setting_btn_next);
		mBtnSendMessage = (Button) findViewById(R.id.setting_btn_send_message);

	}

	@Override
	protected void initEvents() {
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnSendMessage.setOnClickListener(this);
		mIvAvater.setOnClickListener(this);
//		ckText.setOnClickListener(checkBoxClickListener);
//		ckFile.setOnClickListener(checkBoxClickListener);
//		ckVoice.setOnClickListener(checkBoxClickListener);
//		ckVideo.setOnClickListener(checkBoxClickListener);
//		ckSensor.setOnClickListener(checkBoxClickListener);
//		rbPublicNet.setOnClickListener(radioButtonClickListener);
//		rbPrivateNet.setOnClickListener(radioButtonClickListener);
		

	}

	private void initData() {
		Intent intent=this.getIntent();
		friendInfo=(FriendInfo)intent.getExtras().getSerializable(USER_DATA);
		sourceID=friendInfo.getSourceID();
		
//		sourceID=intent.getIntExtra(USER_DATA, 0);
		tvDeviceId.setText(sourceID+"");
		registerType = new RegisterType(friendInfo.getRegisterType());
		ckText.setChecked(registerType.isText());
		ckFile.setChecked(registerType.isFile());
		ckVoice.setChecked(registerType.isVoice());
		ckVideo.setChecked(registerType.isVideo());
		ckSensor.setChecked(registerType.isSensor());
		if((short)friendInfo.getNetState()==1){
//			rbPublicNet.setSelected(true);
			tvNetState.setText("公网");
		}else if((short)friendInfo.getNetState()==2){
			tvNetState.setText("专网");
		}
		
	}

	private OnClickListener radioButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			boolean checked = ((RadioButton) view).isChecked();
		    
		    // Check which radio button was clicked
		    switch(view.getId()) {
		        case R.id.rb_public_net:
		            if (checked){
		            	
		            }
		                
		            break;
		        case R.id.rb_private_net:
		            if (checked){
		            	
		            }       
		            break;
		    }
			
		}
	};
	
//	private OnClickListener checkBoxClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View view) {
//			// Is the view now checked?
//			boolean checked = ((CheckBox) view).isChecked();
//			switch (view.getId()) {
//			case R.id.ck_text:
//				text=checked;				
//				break;
//			case R.id.ck_file:
//				file=checked;
//				break;
//			case R.id.ck_voice:
//				voice=checked;
//				break;
//			case R.id.ck_video:
//				video=checked;
//				break;
//			case R.id.ck_sensor:
//				sensor=checked;
//				break;
//			}
//		}
//	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_btn_back:
			finish();
			break;

		case R.id.setting_btn_next:
			doNext();
			break;
		case R.id.setting_btn_send_message:
			//跳转至聊天界面
			Intent intent=new Intent(this, ChatActivity.class);
			intent.putExtra(MessageFragment.DISPLAY_ID,sourceID);
			startActivity(intent);
			break;
		case R.id.setting_my_avater_img:
			// Intent intent = new Intent(this, ChooseAvatarActivity.class);
			// startActivityForResult(intent, REQUEST_CODE);
			break;
		}

	}

	/**
	 * 登录资料完整性验证，不完整则无法登陆，完整则记录输入的信息。
	 * 
	 * @return boolean 返回是否为完整， 完整(true),不完整(false)
	 */
	private boolean isValidated() {
		mNickname = "";

		// if (TextUtils.isNull(mEtNickname)) {
		// showShortToast("请输入您的聊天昵称");
		// mEtNickname.requestFocus();
		// return false;
		// }

		mNickname = mEtNickname.getText().toString().trim(); // 获取昵称

		return true;
	}

	private void doNext() {
		if ((!isValidated())) {
			return;
		}
		setAsyncTask();

	}

	private void setAsyncTask() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在存储个人信息...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					// 在SD卡中存储登陆信息
//					SharedPreferences.Editor mEditor = getSharedPreferences(GlobalSharedName, Context.MODE_PRIVATE).edit();
//					mEditor.commit();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dismissLoadingDialog();

			}
		});
	}

	/**
	 * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
	 * 
	 * requestCode 请求码，即调用startActivityForResult()传递过去的值 resultCode
	 * 结果码，结果码用于标识返回数据来自哪个新Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				int result = data.getExtras().getInt("result");// 得到新Activity

				// 更换使用缓存机制的头像。 by hill
				mAvatar = result + 1;
				// mIvAvater.setImageBitmap(ImageUtils.getAvatar(mApplication,
			}
		}

	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub

	}

//	private  void setType(byte type){
//		RegisterType registerType=new RegisterType(type);
//		text=registerType.isText();
//		file=registerType.isFile();
//		voice=registerType.isVoice();
//		video=registerType.isVideo();
//		sensor=registerType.isSensor();
//	}
	
}
