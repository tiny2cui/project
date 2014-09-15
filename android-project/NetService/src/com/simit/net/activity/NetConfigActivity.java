package com.simit.net.activity;

import com.simit.R;
import com.simit.net.NetConfig;
import com.simit.net.domain.LocalProperty;
import com.simit.net.domain.NetType;
import com.simit.net.service.NetService;
import com.simit.net.utils.IPv4Util;
import com.simit.net.utils.MyLog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class NetConfigActivity extends Activity implements OnClickListener {

	private Button serviceStart,serviceStop,serviceSetUp ;
	private RadioButton radioWSN,radioIntenet;
	private EditText userName,userId,userPassword,deveiceId;
	private TextView infoTextView;
	private static String TAG="NetConfig";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serviceStart = (Button) findViewById(R.id.button_start);
		serviceStop = (Button) findViewById(R.id.button_stop);
		serviceSetUp= (Button) findViewById(R.id.button_setup);
		infoTextView= (TextView) findViewById(R.id.info);
		radioWSN= (RadioButton) findViewById(R.id.radio_WSN);
		radioIntenet= (RadioButton) findViewById(R.id.radio_INTENET);
		
		userName= (EditText) findViewById(R.id.user_name);
		userId= (EditText) findViewById(R.id.user_id);
		userPassword= (EditText) findViewById(R.id.user_password);
		deveiceId= (EditText) findViewById(R.id.deveice_id);
		LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();
		localProperty.setDeveiceId(7);
		localProperty.setUserId(7);
		localProperty.setPassword("12345678");
		localProperty.setNetType(NetType.WSN);
		localProperty.setIpAddress(IPv4Util.getLocalIpAddress(this));
		serviceStart.setOnClickListener(this);
		serviceStop.setOnClickListener(this);
		serviceSetUp.setOnClickListener(this);
		radioWSN.setOnClickListener(this);
		radioIntenet.setOnClickListener(this);
		showInfo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
//		case R.id.button_start:
//			StartNetService();
//            break;
//		case R.id.button_stop:
//			StopNetService();
//            break;
//		case R.id.radio_WSN:
//            if (((RadioButton) v).isChecked()){
//            	NetConfig.getInstance().getLocalProperty().setNetType(NetType.WSN);
//            	showInfo();
//            }
//                
//            break;
//		case R.id.radio_INTENET:
//            if (((RadioButton) v).isChecked()){
//            	NetConfig.getInstance().getLocalProperty().setNetType(NetType.INTERNET);
//            	showInfo();
//            }
//                
//            break;
//		case R.id.button_setup:
//			String idString=userId.getText().toString();
//			String name=userName.getText().toString();
//			String passwordStr=userPassword.getText().toString();
//			String deviceStr=deveiceId.getText().toString();
//			int id=Integer.valueOf(idString);
//			int deveiceId=Integer.valueOf(deviceStr);
//			LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();
//			localProperty.setName(name);
//			localProperty.setUserId(id);
//			localProperty.setPassword(passwordStr);
//			localProperty.setDeveiceId(deveiceId);
//			showInfo();
//            break;
		}
		
	}


	private void StartNetService() {
		Intent i = new Intent(NetConfigActivity.this, NetService.class);
		startService(i);
		MyLog.d(TAG, "button : start service");
	}
	
	private void StopNetService() {
		Intent i = new Intent(NetConfigActivity.this, NetService.class);
		stopService(i);
		MyLog.d(TAG, "button : stop service");
	}
	
	private void showInfo(){
		LocalProperty localProperty=NetConfig.getInstance().getLocalProperty();
		infoTextView.setText(" userName-->"+localProperty.getName()+"\n userID   -->"+localProperty.getUserId()+"\n userPassword   -->"+new String(localProperty.getPassword())+"\n deveiceID   -->"+localProperty.getDeveiceId()+"\n IP        -->"+localProperty.getIpAddress()+"\n NetType-->"+localProperty.getNetType());
	}
 
}
