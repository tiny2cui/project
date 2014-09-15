package com.tiny.chat.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.activity.InfoActivity;
import com.tiny.chat.adapter.FriendAdapter;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.domain.People;
import com.tiny.chat.domain.FriendInfo;
import com.tiny.chat.socket.MessageFactory;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.IChatMessageHandler;
import com.tiny.chat.view.PullRefreshListView;
import com.tiny.chat.view.PullRefreshListView.OnCancelListener;
import com.tiny.chat.view.PullRefreshListView.OnRefreshListener;

public class FriendFragment extends BaseFragment implements
		OnItemClickListener, OnRefreshListener, OnCancelListener,OnClickListener,
		IChatMessageHandler {
	private PullRefreshListView mListView;
	private TextView mEmptyView;
	private List<People> mList = new ArrayList<People>();
	private FriendAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_friend, container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
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
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		mListView = (PullRefreshListView) findViewById(R.id.friend_list);
		mEmptyView = (TextView) findViewById(R.id.friend_empty);
	}

	@Override
	protected void initEvents() {
		mListView.setOnItemClickListener(this);
		mListView.setOnRefreshListener(this);
		mListView.setOnCancelListener(this);
		mListView.setEmptyView(mEmptyView);
		mEmptyView.setOnClickListener(this);

	}

	@Override
	protected void init() {
		mAdapter = new FriendAdapter(mActivity);
		mAdapter.setList(getRefreshList(), false);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onCancel() {
		clearAsyncTask();
		mListView.onRefreshComplete();

	}

	@Override
	public void onRefresh() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
					// TODO 处理刷新逻辑获取数据

					return true;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				mListView.onRefreshComplete();
				// TODO 刷新界面
				
				super.onPostExecute(result);
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		Intent intent = new Intent(getActivity(), ChatActivity.class);
//		intent.putExtra(MessageFragment.DISPLAY_ID, mList.get(position-1).getUserData().getSourceID());
//		startActivity(intent);
		
		Intent intent = new Intent(getActivity(), InfoActivity.class);
//		intent.putExtra(InfoActivity.USER_DATA, mList.get(position-1).getUserData().getSourceID());
		Bundle bundle=new Bundle();
		bundle.putSerializable(InfoActivity.USER_DATA, mList.get(position-1).getFriendInfo());
		intent.putExtras(bundle);
		startActivity(intent);
		// Toast.makeText(mActivity, "你点我作甚？", Toast.LENGTH_SHORT).show();
		

	}

	@Override
	public void handlerMessage(ChatMessage message) {
		if (ChatMessage.MESSAGE_FRIEND_LOGIN_DATA.equals(message.getMessageId())) {
			// People people=(People)message.getMessageData();
			mAdapter.setList(getRefreshList(), true);
		} else if (ChatMessage.MESSAGE_FRIEND_LOGOUT_DATA.equals(message.getMessageId())) {
			// People people=(People)message.getMessageData();
			mAdapter.setList(getRefreshList(), true);
		}

	}
	
	
	private List<People> getRefreshList(){
		HashMap<Integer, People> map=mApplication.getOnlineUsers();
		if(map.size()>0){
			mList.clear();
			for(Map.Entry<Integer, People> entry : map.entrySet() ){
				mList.add(entry.getValue());
			}
		}
//		//TODO 添加测试数据
//		People p1=new People(12);
//		UserData d1=new UserData();
//		d1.setDestionID(13);
//		d1.setRegisterType((byte)0x07);
//		d1.setIp(1234);
//		d1.setSourceID(67);
//		p1.setUserData(d1);
//		mList.add(p1);
		return mList;
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.friend_empty){
			//获取在线好友
			Toast.makeText(getActivity(), "获取在线好友", Toast.LENGTH_SHORT).show();
			FramePacket loginPacket=MessageFactory.getFriendList();
			String serverIP=BaseApplication.getInstance().getLocalIP();
			UDPSocketService.getInstance().postMessage(loginPacket.getFramePacket(), loginPacket.getFramePacket().length,serverIP);
		}
		
	}
	

}
