package com.tiny.chat.activity;

import java.util.ArrayList;
import java.util.List;
import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.fragment.FriendFragment;
import com.tiny.chat.fragment.MessageFragment;
import com.tiny.chat.fragment.SettingFragment;
import com.tiny.chat.service.MessageServer;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.IChatMessageHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabActivity extends FragmentActivity implements
		IChatMessageHandler {

	private ViewPager mPager;// 页卡内容
	private List<Fragment> fragments; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView tvMessageCount, tvFriend, tvSetting;// 页卡头标
	private LinearLayout layout_message;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	// private UDPSocketService udpSocketService;

	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(MainTabActivity.this, MessageServer.class);
		startService(intent);
		setContentView(R.layout.layout_main);
		initView();
		initViewPager();
		// udpSocketService=UDPSocketService.getInstance();
		// udpSocketService.start();
		// sendLogin();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.table, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication app = BaseApplication.getInstance();
		app.unRegisterMessageHandler(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseApplication app = BaseApplication.getInstance();
		app.registerMessageHandler(this);
	}

	/**
	 * 初始化
	 */
	private void initView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		offset = 0;// 计算偏移量
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		bmpW = screenW / 3;//
		cursor.setMinimumWidth(bmpW);
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
		layout_message = (LinearLayout) findViewById(R.id.linear_message);
		// tvMessage = (TextView) findViewById(R.id.tv_message);
		tvMessageCount = (TextView) findViewById(R.id.tv_message_count);
		tvFriend = (TextView) findViewById(R.id.tv_friend);
		tvSetting = (TextView) findViewById(R.id.tv_setting);
		layout_message.setOnClickListener(new MyOnClickListener(0));
		tvFriend.setOnClickListener(new MyOnClickListener(1));
		tvSetting.setOnClickListener(new MyOnClickListener(2));
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		fragments = new ArrayList<Fragment>();
		fragments.add(new MessageFragment());
		fragments.add(new FriendFragment());
		fragments.add(new SettingFragment());
		mPager.setAdapter(new MainFragmentPagerAdapter(
				getSupportFragmentManager(), fragments));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setOffscreenPageLimit(3);
	}

	@Override
	public void handlerMessage(ChatMessage message) {
		if (ChatMessage.MESSAGE_TEXT_DATA.equals(message.getMessageId())) {
			// SMSMessage smsMessage=(SMSMessage)message.getMessageData();
			tvMessageCount.setText("" + count++);
			// Toast.makeText(this, smsMessage.getContent(),
			// Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (R.id.action_map == itemId) {
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			// Toast.makeText(this, "map", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int postion) {
			Animation animation = null;
			switch (postion) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			// mMediaLibraryController.setCurrentPageNum(postion);
			currIndex = postion;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public MainFragmentPagerAdapter(FragmentManager fm,
				List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

	}

	private void loadLogin() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {

					//TODO 添加登录事件

					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

			}
		};
	}

}
