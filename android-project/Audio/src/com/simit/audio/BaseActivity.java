package com.simit.audio;

import com.simit.audio.util.Tags;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onCreate()");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onStart()");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onResume()");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onPause()");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onStop()");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝" + "onDestroy()");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.d(Tags.ActivityLifeCycle.toString(), "TaskId=" + this.getTaskId()
				+ " Name=" + this.getClass().getSimpleName() + " 操作＝"
				+ "onBackPressed()");
	}

}
