package com.tiny.chat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tiny.chat.R;
import com.tiny.chat.adapter.ChatAdapter;
import com.tiny.chat.domain.People1;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.file.FileState;
import com.tiny.chat.utils.AudioRecorderUtils;
import com.tiny.chat.utils.ImageUtils;
import com.tiny.chat.view.ChatListView;
import com.tiny.chat.view.EmoteInputView;
import com.tiny.chat.view.EmoticonsEditText;
import com.tiny.chat.view.ScrollLayout;
import com.tiny.chat.view.ScrollLayout.OnScrollToScreenListener;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BaseMessageActivity extends FragmentActivity implements OnScrollToScreenListener,
        OnClickListener, OnTouchListener, TextWatcher {

    protected ChatListView mClvList;
    protected ScrollLayout mLayoutScroll;
    protected LinearLayout mLayoutRounds;
    protected EmoteInputView mInputView;

    protected ImageButton mIbTextDitorPlus;
    protected ImageButton mIbTextDitorKeyBoard;
    protected ImageButton mIbTextDitorEmote;
    protected EmoticonsEditText mEetTextDitorEditer;
    protected Button mBtnTextDitorSend;
    protected ImageView mIvTextDitorAudio;
    protected ImageView mIvAvatar;

    protected ImageButton mIbAudioDitorPlus;
    protected ImageButton mIbAudioDitorKeyBoard;
    protected ImageView mIvAudioDitorAudioBtn;

    protected LinearLayout mLayoutFullScreenMask;
    protected LinearLayout mLayoutMessagePlusBar;
    protected LinearLayout mLayoutMessagePlusPicture;
    protected LinearLayout mLayoutMessagePlusCamera;
    protected LinearLayout mLayoutMessagePlusFile;
    protected LinearLayout mLayoutMessagePlusAudio;
    protected LinearLayout mLayoutMessagePlusVideo;
    
    protected Bitmap mRoundsSelected;
    protected Bitmap mRoundsNormal;

    protected List<SMSMessage> mList = new ArrayList<SMSMessage>(); // 消息列表
    protected ChatAdapter mAdapter;
    protected People1 mPeople; // 聊天的对象
//    protected SqlDBOperate mDBOperate;// 新增数据库类可以操作用户数据库和聊天信息数据库
    protected String mCameraImagePath;

    // 录音变量
    protected String mVoicePath;
    // private static final int MAX_RECORD_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
    protected static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    protected static final int RECORD_OFF = 0; // 不在录音
    protected static final int RECORD_ON = 1; // 正在录音
    protected String RECORD_FILENAME; // 录音文件名

    protected TextView mTvRecordDialogTxt;
    protected ImageView mIvRecVolume;

    protected Dialog mRecordDialog;
    protected AudioRecorderUtils mAudioRecorder;
    protected Thread mRecordThread;

    protected int recordState = 0; // 录音状态
    protected float recodeTime = 0.0f; // 录音时长
    protected double voiceValue = 0.0; // 录音的音量值
    protected boolean isMove = false; // 手指是否移动
    protected float downY;

    // 文件传输变量   
    protected String sendFilePath;   //文件路径
//    protected TcpClient tcpClient = null;
//    protected TcpService tcpService = null;
    protected HashMap<String, FileState> sendFileStates;
    protected HashMap<String, FileState> reciveFileStates;

    protected String mNickName;
    protected String mIMEI;
    protected int mID;
    protected int mSenderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        initViews();
        initEvents();
//        mDBOperate = new SqlDBOperate(this); // 新增数据库操作类，可以操作用户表和聊天信息表
    }
   
//    protected class OnRightImageButtonClickListener implements onRightImageButtonClickListener {
//
//        @Override
//        public void onClick() {
//            Intent intent = new Intent(BaseMessageActivity.this, OtherProfileActivity.class);
//            intent.putExtra(NearByPeople.ENTITY_PEOPLE, mPeople);
//            startActivity(intent);
//            finish();
//        }
//    }

    protected abstract void initViews();
    protected abstract void initEvents();
    protected void showKeyBoard() {
        if (mInputView.isShown()) {
            mInputView.setVisibility(View.GONE);
        }
        mEetTextDitorEditer.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(
                mEetTextDitorEditer, 0);
    }

    protected void hideKeyBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                BaseMessageActivity.this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showPlusBar() {
        mLayoutFullScreenMask.setEnabled(true);
        mLayoutMessagePlusBar.setEnabled(true);
        mLayoutMessagePlusPicture.setEnabled(true);
        mLayoutMessagePlusCamera.setEnabled(true);
        mLayoutMessagePlusFile.setEnabled(true);
        mLayoutMessagePlusAudio.setEnabled(true);
        mLayoutMessagePlusVideo.setEnabled(true);
        Animation animation = AnimationUtils.loadAnimation(BaseMessageActivity.this,
                R.anim.controller_enter);
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.VISIBLE);
        mLayoutFullScreenMask.setVisibility(View.VISIBLE);
    }

    protected void hidePlusBar() {
        mLayoutFullScreenMask.setEnabled(false);
        mLayoutMessagePlusBar.setEnabled(false);
        mLayoutMessagePlusPicture.setEnabled(false);
        mLayoutMessagePlusCamera.setEnabled(false);
        mLayoutMessagePlusFile.setEnabled(false);
        mLayoutMessagePlusAudio.setEnabled(false);
        mLayoutMessagePlusVideo.setEnabled(false);
        mLayoutFullScreenMask.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(BaseMessageActivity.this,
                R.anim.controller_exit);
        animation.setInterpolator(AnimationUtils.loadInterpolator(BaseMessageActivity.this,
                android.R.anim.anticipate_interpolator));
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.GONE);
    }

    protected void initRounds() {
        mRoundsSelected = ImageUtils.getRoundBitmap(BaseMessageActivity.this, getResources()
        		.getColor(R.color.msg_short_line_selected));
        mRoundsNormal = ImageUtils.getRoundBitmap(BaseMessageActivity.this, getResources()
                .getColor(R.color.msg_short_line_normal));
        int mChildCount = mLayoutScroll.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            ImageView imageView = (ImageView) LayoutInflater.from(BaseMessageActivity.this)
                    .inflate(R.layout.include_message_shortline, null);
            imageView.setImageBitmap(mRoundsNormal);
            mLayoutRounds.addView(imageView);
        }
        ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsSelected);
    }

}
