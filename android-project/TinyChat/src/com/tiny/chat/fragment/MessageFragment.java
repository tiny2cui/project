package com.tiny.chat.fragment;
import java.util.ArrayList;
import java.util.List;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.activity.ChatActivity;
import com.tiny.chat.adapter.MessageAdapter;
import com.tiny.chat.db.DBOperate;
import com.tiny.chat.domain.Display;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.IChatMessageHandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageFragment extends BaseFragment implements
OnItemClickListener ,IChatMessageHandler{
	private ListView mListView;
	private TextView mEmptyView;
	private List<Display> mList = new ArrayList<Display>();
	private MessageAdapter mAdapter;
	public static String DISPLAY_ID="display_id";
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onRefresh();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_message, container, false);
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
		// TODO Auto-generated method stub
		super.onDestroy();
		mApplication.registerMessageHandler(this);
	}
	@Override
	protected void initViews() {
		mListView = (ListView) findViewById(R.id.friend_list);
		mEmptyView = (TextView) findViewById(R.id.friend_empty);
		
	}

	@Override
	protected void initEvents() {
		mListView.setOnItemClickListener(this);
		
		mListView.setEmptyView(mEmptyView);
		
	}

	@Override
	protected void init() {
		
		mAdapter = new MessageAdapter(mActivity);
		mAdapter.setList(mList, false);
		mListView.setAdapter(mAdapter);
		
	}

	

	
	public void onRefresh() {
		new AsyncTask<Void, Void, List<SMSMessage>>() {
			@Override
			protected List<SMSMessage> doInBackground(Void... params) {
			
					DBOperate dbOperate=DBOperate.getInstance(mActivity);
					List<SMSMessage> list=dbOperate.getLastMessages();
					//Thread.sleep(2000);
					// TODO 处理刷新逻辑获取数据

					return list;
				
			}

			@Override
			protected void onPostExecute(List<SMSMessage> result) {
				List<Display> list=new ArrayList<Display>();
				if(result!=null && result.size()>0){
					for(int i=0;i<result.size();i++){
						list.add(new Display(result.get(i),BaseApplication.getInstance().getDeviceID()));
					}
				}
				mList.clear();
				mAdapter.addList(list, true);
				// TODO 刷新界面
				super.onPostExecute(result);
			}
		}.execute();
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		//Toast.makeText(mActivity, "你点我作甚？", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(DISPLAY_ID, mList.get(position).getId());
		startActivity(intent);
	}

	@Override
	public void handlerMessage(ChatMessage message) {
		if(ChatMessage.MESSAGE_TEXT_DATA.equals(message.getMessageId())){
			onRefresh();
		}
		
	}

	
}
