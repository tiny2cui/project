package com.tiny.chat.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.adapter.ChatAdapter;
import com.tiny.chat.db.DBOperate;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.fragment.MessageFragment;
import com.tiny.chat.socket.FileSendDispose;
import com.tiny.chat.socket.FrameType;
import com.tiny.chat.socket.OnActiveChatActivityListenner;
import com.tiny.chat.socket.UDPSocketService;
import com.tiny.chat.utils.AudioRecorderUtils;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.DateUtils;
import com.tiny.chat.utils.FileUtils;
import com.tiny.chat.utils.IChatMessageHandler;
import com.tiny.chat.utils.ImageUtils;
import com.tiny.chat.utils.MyLog;
import com.tiny.chat.utils.TypeConvert;
import com.tiny.chat.view.ChatListView;
import com.tiny.chat.view.ScrollLayout;
import com.tiny.chat.view.EmoteInputView;
import com.tiny.chat.view.EmoticonsEditText;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends BaseMessageActivity implements OnActiveChatActivityListenner ,IChatMessageHandler{

    private static final String TAG = "SZU_ChatActivity";

    private static final int FILE_SELECT_CODE = 4;
    public static String IMAG_PATH;
    public static String THUMBNAIL_PATH;
    public static String VOICE_PATH;
    public static String FILE_PATH;
    private int mDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();        
//        changeActiveChatActivity(this); // 注册到changeActiveChatActivity

    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.getInstance().registerMessageHandler(this);
    }

    // 监听返回键
    @Override
    public void onBackPressed() {
//        if (mLayoutMessagePlusBar.isShown()) {
//            hidePlusBar();
//        }
//        else if (mInputView.isShown()) {
//            mIbTextDitorKeyBoard.setVisibility(View.GONE);
//            mIbTextDitorEmote.setVisibility(View.VISIBLE);
//            mInputView.setVisibility(View.GONE);
//        }
//        else if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
//            mIbTextDitorKeyBoard.setVisibility(View.VISIBLE);
//            mIbTextDitorEmote.setVisibility(View.GONE);
//            hideKeyBoard();
//        }
//        else if (mLayoutScroll.getCurScreen() == 1) {
//            mLayoutScroll.snapToScreen(0);
//        }
//        else {
//            finish();
//        }
    	finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().unRegisterMessageHandler(this);
    }

    @Override
    public void finish() {
//        removeActiveChatActivity(); // 移除监听
//        if (null != mDBOperate) {// 关闭数据库连接
//            mDBOperate.close();
//            mDBOperate = null;
//        }
        mRecordThread = null;
        Boolean b;
        super.finish();
    }

   
    @Override
    protected void initViews() {
//        mHeaderLayout = (HeaderLayout) findViewById(R.id.chat_header);
//        mHeaderLayout.init(HeaderStyle.TITLE_CHAT);
        mIvAvatar = (ImageView) findViewById(R.id.header_iv_logo);
        mClvList = (ChatListView) findViewById(R.id.chat_clv_list);
        mLayoutScroll = (ScrollLayout) findViewById(R.id.chat_slayout_scroll);
        mLayoutRounds = (LinearLayout) findViewById(R.id.chat_layout_rounds);
        mInputView = (EmoteInputView) findViewById(R.id.chat_eiv_inputview);

        mIbTextDitorPlus = (ImageButton) findViewById(R.id.chat_textditor_ib_plus);
        mIbTextDitorKeyBoard = (ImageButton) findViewById(R.id.chat_textditor_ib_keyboard);
        mIbTextDitorEmote = (ImageButton) findViewById(R.id.chat_textditor_ib_emote);
        mIvTextDitorAudio = (ImageView) findViewById(R.id.chat_textditor_iv_audio);
        mBtnTextDitorSend = (Button) findViewById(R.id.chat_textditor_btn_send);
        mEetTextDitorEditer = (EmoticonsEditText) findViewById(R.id.chat_textditor_eet_editer);

        mIbAudioDitorPlus = (ImageButton) findViewById(R.id.chat_audioditor_ib_plus);
        mIbAudioDitorKeyBoard = (ImageButton) findViewById(R.id.chat_audioditor_ib_keyboard);
        mIvAudioDitorAudioBtn = (ImageView) findViewById(R.id.chat_audioditor_iv_audiobtn);

        mLayoutFullScreenMask = (LinearLayout) findViewById(R.id.fullscreen_mask);
        mLayoutMessagePlusBar = (LinearLayout) findViewById(R.id.message_plus_layout_bar);
        mLayoutMessagePlusPicture = (LinearLayout) findViewById(R.id.message_plus_layout_picture);
        mLayoutMessagePlusCamera = (LinearLayout) findViewById(R.id.message_plus_layout_camera);
        mLayoutMessagePlusFile = (LinearLayout) findViewById(R.id.message_plus_layout_file);
        mLayoutMessagePlusAudio = (LinearLayout) findViewById(R.id.message_plus_layout_audio);
        mLayoutMessagePlusVideo = (LinearLayout) findViewById(R.id.message_plus_layout_video);
    }

    @Override
    protected void initEvents() {
//        mLayoutScroll.setOnScrollToScreen(this);
        mIbTextDitorPlus.setOnClickListener(this);
        mIbTextDitorEmote.setOnClickListener(this);
        mIbTextDitorKeyBoard.setOnClickListener(this);
        mBtnTextDitorSend.setOnClickListener(this);
        mIvTextDitorAudio.setOnClickListener(this);
        mEetTextDitorEditer.addTextChangedListener(this);
        mEetTextDitorEditer.setOnTouchListener(this);
        mIbAudioDitorPlus.setOnClickListener(this);
        mIbAudioDitorKeyBoard.setOnClickListener(this);

        mIvAudioDitorAudioBtn.setOnTouchListener(this);

        mLayoutFullScreenMask.setOnTouchListener(this);
        mLayoutMessagePlusPicture.setOnClickListener(this);
        mLayoutMessagePlusCamera.setOnClickListener(this);
        mLayoutMessagePlusFile.setOnClickListener(this);
        mLayoutMessagePlusCamera.setOnClickListener(this);
        mLayoutMessagePlusFile.setOnClickListener(this);
        mLayoutMessagePlusAudio.setOnClickListener(this);
        mLayoutMessagePlusVideo.setOnClickListener(this);

    }

    private void init() {
    	mDisplayId=getIntent().getIntExtra(MessageFragment.DISPLAY_ID, -1);
        sendFileStates = BaseApplication.sendFileStates;// 存储文件收发状态
        reciveFileStates = BaseApplication.recieveFileStates;
//        mID = SessionUtils.getLocalUserID();
//        mNickName = SessionUtils.getNickname();
//        mIMEI = SessionUtils.getDeviceId();
//        mPeople = getIntent().getParcelableExtra(People.ENTITY_PEOPLE);
//        mSenderID = mDBOperate.getIDByIMEI(mPeople.getDeviceId());// 获取聊天对象IMEI
        createSavePath();// 创建保存的文件夹目录
//        mMessagesList = mDBOperate.getScrollMessageOfChattingInfo(0, 5, mSenderID, mID);
//        mHeaderLayout.setTitleChat(
//                ImageUtils.getIDfromDrawable(this, People.AVATAR + mPeople.getAvatar()),
//                R.drawable.bg_chat_dis_active, mPeople.getNickname(), mPeople.getLogintime(),
//                R.drawable.ic_topbar_profile, new OnRightImageButtonClickListener());
        mInputView.setEditText(mEetTextDitorEditer);
        initRounds();      
        mAdapter=new ChatAdapter(this);
        mClvList.setAdapter(mAdapter);
        loadMessage();
    }
    
    public void loadMessage() {
		new AsyncTask<Void, Void, List<SMSMessage>>() {
			@Override
			protected List<SMSMessage> doInBackground(Void... params) {
			
					DBOperate dbOperate=DBOperate.getInstance(ChatActivity.this);
					List<SMSMessage> list=dbOperate.getMessagesByDeviceId(mDisplayId);
					//Thread.sleep(2000);
					// TODO 处理刷新逻辑获取数据

					return list;
				
			}

			@Override
			protected void onPostExecute(List<SMSMessage> result) {
				
				if(result!=null && result.size()>0){
					mList.clear();
					mAdapter.setList(result, true);
					mClvList.setSelection(result.size());
					
				}
				
				// TODO 刷新界面
				super.onPostExecute(result);
			}
		}.execute();
		
	}

    @Override
    public void doAction(int whichScreen) {
        switch (whichScreen) {
            case 0:
                ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsSelected);
                ((ImageView) mLayoutRounds.getChildAt(1)).setImageBitmap(mRoundsNormal);
                break;

            case 1:
                ((ImageView) mLayoutRounds.getChildAt(1)).setImageBitmap(mRoundsSelected);
                ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsNormal);
                mIbTextDitorKeyBoard.setVisibility(View.GONE);
                mIbTextDitorEmote.setVisibility(View.VISIBLE);
                if (mInputView.isShown()) {
                    mInputView.setVisibility(View.GONE);
                }
                hideKeyBoard();
                break;
        }
    }

    public void refreshAdapter() {
        mAdapter.setList(mList, true);
        mAdapter.notifyDataSetChanged();
        setLvSelection(mList.size());
    }

    public void setLvSelection(int position) {
        mClvList.setSelection(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) { 
            case R.id.chat_textditor_ib_plus:
                if (!mLayoutMessagePlusBar.isShown()) {
                    showPlusBar();
                }
                break;

            case R.id.chat_textditor_ib_emote:
                mIbTextDitorKeyBoard.setVisibility(View.VISIBLE);
                mIbTextDitorEmote.setVisibility(View.GONE);
                mEetTextDitorEditer.requestFocus();
                if (mInputView.isShown()) {
                    hideKeyBoard();
                }
                else {
                    hideKeyBoard();
                    mInputView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.chat_textditor_ib_keyboard:
                mIbTextDitorKeyBoard.setVisibility(View.GONE);
                mIbTextDitorEmote.setVisibility(View.VISIBLE);
                showKeyBoard();
                break;

            case R.id.chat_textditor_btn_send:
                String content = mEetTextDitorEditer.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    mEetTextDitorEditer.setText(null);
                    sendMessage(content, FrameType.INFO_MESSAGE);
                    loadMessage();
                    //                    refreshAdapter();
                }
                break;

            case R.id.chat_textditor_iv_audio:
                mLayoutScroll.snapToScreen(1);
                break;

            case R.id.chat_audioditor_ib_plus:
                if (!mLayoutMessagePlusBar.isShown()) {
                    showPlusBar();
                }
                break;

            case R.id.chat_audioditor_ib_keyboard:
                mLayoutScroll.snapToScreen(0);
                break;

                //图片发送
            case R.id.message_plus_layout_picture:
                ImageUtils.selectPhoto(ChatActivity.this);
                hidePlusBar();
                break;

            case R.id.message_plus_layout_camera:
                mCameraImagePath = ImageUtils.takePicture(ChatActivity.this);
                hidePlusBar();
                break;

            case R.id.message_plus_layout_file:
                showFileChooser();
                hidePlusBar();
                break;
            case R.id.message_plus_layout_audio:
                //语音通话
            	//TODO 
            	showAudioChooser();
                hidePlusBar();
                break;
            case R.id.message_plus_layout_video:
                //视频聊天
            	//TODO
            	showVideoChooser();
                hidePlusBar();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下按钮
                MyLog.i(TAG, "ACTION DOWN");

                if (recordState == RECORD_OFF) {
                    switch (v.getId()) {
                        case R.id.chat_textditor_eet_editer:
                            mIbTextDitorKeyBoard.setVisibility(View.GONE);
                            mIbTextDitorEmote.setVisibility(View.VISIBLE);
                            showKeyBoard();
                            break;

                        case R.id.fullscreen_mask:
                            hidePlusBar();
                            break;

                        case R.id.chat_audioditor_iv_audiobtn:
                            downY = event.getY();

                            mAudioRecorder = new AudioRecorderUtils();

//                            RECORD_FILENAME = System.currentTimeMillis()+ TextUtils.getRandomNumStr(3);
                            mAudioRecorder.setVoicePath(VOICE_PATH, RECORD_FILENAME);
                            recordState = RECORD_ON;
                            try {
                                mAudioRecorder.start();
                                recordTimethread();
                                showVoiceDialog(0);
                            }
                            catch (IOException e) {
                                e.printStackTrace();

                            }
                            break;
                    }
                }
                break;

//            case MotionEvent.ACTION_MOVE: // 滑动手指
//                float moveY = event.getY();
//                if (moveY - downY < -50) {
//                    isMove = true;
//                    showVoiceDialog(1);
//                }
//                else if (moveY - downY < -20) {
//                    isMove = false;
//                    showVoiceDialog(0);
//                }
//                break;

//            case MotionEvent.ACTION_UP: // 松开手指
//            	MyLog.i(TAG, "ACTION UP");
//                if (recordState == RECORD_ON) {
//                    recordState = RECORD_OFF;
//
//                    try {
//                        mRecordThread.interrupt();
//                        mRecordThread = null;
//                        mAudioRecorder.stop();
//                        voiceValue = 0.0;
//                    }
//                    catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    if (mRecordDialog.isShowing()) {
//                        mRecordDialog.dismiss();
//                    }
//
//                    if (!isMove) {
//                        if (recodeTime < MIN_RECORD_TIME) {
//                            showWarnToast("时间太短  录音失败");
//                        }
//                        else {
//                            mVoicePath = mAudioRecorder.getVoicePath();
////                            sendMessage(mVoicePath, CONTENT_TYPE.VOICE);
//                            refreshAdapter();
//                        }
//                    }
//
//                    isMove = false;
//                }
//                break;

        }

        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
//        	mIvTextDitorAudio.setVisibility(View.VISIBLE);
        	mIvTextDitorAudio.setVisibility(View.GONE);
            mBtnTextDitorSend.setVisibility(View.GONE);
        }
        else {
            mIvTextDitorAudio.setVisibility(View.GONE);
            mBtnTextDitorSend.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.INTENT_REQUEST_CODE_ALBUM:
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (data.getData() == null) {
                        return;
                    }
                    if (!FileUtils.isSdcardExist()) {
                        Toast.makeText(this, "SD卡不可用,请检查", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Uri uri = data.getData();
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            String path = cursor.getString(column_index);
                            mCameraImagePath = path;
//                            Bitmap bitmap = ImageUtils.getBitmapFromPath(path);
//                            if (ImageUtils.bitmapIsLarge(bitmap)) {
//                                ImageUtils.cropPhoto(this, this, path);
//                            }
//                            else {
                            	
                                if (path != null) {
                                    ImageUtils.createThumbnail(this, path, THUMBNAIL_PATH
                                            + File.separator);
                                    MyLog.i(TAG, "pic--->"+path);
                                    //sendMessage(path, CONTENT_TYPE.IMAGE);
                                    sendMessage(path, FrameType.INFO_FILE);
                                    loadMessage();
                                    //refreshAdapter();
                                }
                                
                            //}
                        }
                    }
                }
                break;

            case ImageUtils.INTENT_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (mCameraImagePath != null) {
//                        mCameraImagePath = ImageUtils.savePhotoToSDCard(
//                                ImageUtils.CompressionPhoto(mScreenWidth, mCameraImagePath, 2),
//                                ImageUtils.SD_IMAGE_PATH, null);
//                        ImageUtils.fliterPhoto(this, this, mCameraImagePath);
                    	 sendMessage(mCameraImagePath, FrameType.INFO_FILE);
                    	 loadMessage();
                    }
                }
                // mCameraImagePath = null;
                break;

//            case ImageUtils.INTENT_REQUEST_CODE_CROP:
//                if (resultCode == RESULT_OK) {
//                    String path = data.getStringExtra("path");
//                    mCameraImagePath = path;
//                    if (path != null) {
//                        ImageUtils.createThumbnail(this, path, THUMBNAIL_PATH + File.separator); // 生成缩略图
//                        sendMessage(path, CONTENT_TYPE.IMAGE);
//                        refreshAdapter();
//                    }
//                }
//                break;
//
//            case ImageUtils.INTENT_REQUEST_CODE_FLITER:
//                if (resultCode == RESULT_OK) {
//                    String path = data.getStringExtra("path");
//                    if (path != null) {
//                        ImageUtils.createThumbnail(this, path, THUMBNAIL_PATH + File.separator);
//                        sendMessage(path, CONTENT_TYPE.IMAGE);
//                        refreshAdapter();
//                    }
//                }
//                break;
//
            case FILE_SELECT_CODE: {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = uri.getPath();
                    MyLog.i("接收文件路径：", path);

                    if (path != null) {
                        sendFilePath = path;
                        sendMessage(sendFilePath, FrameType.INFO_FILE);
                        refreshAdapter();
                    }
                }
            }
                break;
        }
    }

    // 程序在开始运行的时候,调用以下函数创建存储图片语音文件目录
    private void createSavePath() {
        if (null != BaseApplication.IMAG_PATH) {
            int imei = mPeople.getmDevideId();
            IMAG_PATH = BaseApplication.IMAG_PATH + File.separator + imei;
            THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator + imei;
            VOICE_PATH = BaseApplication.VOICE_PATH + File.separator + imei;
            FILE_PATH = BaseApplication.FILE_PATH + File.separator + imei;
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(IMAG_PATH);// 如果目录不存在则创建目录
            if (!FileUtils.isFileExists(THUMBNAIL_PATH))
                FileUtils.createDirFile(THUMBNAIL_PATH);// 如果目录不存在则创建目录
            if (!FileUtils.isFileExists(VOICE_PATH))
                FileUtils.createDirFile(VOICE_PATH);
            if (!FileUtils.isFileExists(FILE_PATH))
                FileUtils.createDirFile(FILE_PATH);
        }
    }

    @Override
    public boolean isThisActivityMsg(SMSMessage msg) {
        // TODO 待完成
        if (mPeople.getmDevideId()==(msg.getSourceID())) { // 若消息与本activity有关，则接收
            mList.add(msg); // 将此消息添加到显示聊天list中
            return true;
        }
        return false;
    }

    
    public void processMessage(android.os.Message msg) {
        switch (msg.what) {
//            case IPMSGConst.IPMSG_SENDMSG:
//                refreshAdapter(); // 刷新ListView
//                break;
//
//            case IPMSGConst.IPMSG_RECEIVE_IMAGE_DATA: { // 图片开始发送
//                Log.d(TAG, "接收方确认图片请求,发送文件为" + mCameraImagePath);
//                tcpClient = TcpClient.getInstance(ChatActivity.this);
//                tcpClient.startSend();
//                tcpClient.sendFile(mCameraImagePath, mPeople.getIpaddress(),
//                        Message.CONTENT_TYPE.IMAGE);
//            }
//                break;
//
//            case IPMSGConst.IPMSG_RECIEVE_VOICE_DATA: { // 语音开始发送
//                Log.d(TAG, "接收方确认语音请求,发送文件为" + mVoicePath);
//                tcpClient = TcpClient.getInstance(ChatActivity.this);
//                tcpClient.startSend();
//                if (FileUtils.isFileExists(mVoicePath))
//                    tcpClient.sendFile(mVoicePath, mPeople.getIpaddress(),
//                            Message.CONTENT_TYPE.VOICE);
//            }
//                break;
//
//            case IPMSGConst.IPMSG_RECIEVE_FILE_DATA: { // 文件开始发送
//                Log.d(TAG, "接收方确认文件请求,发送文件为" + sendFilePath);
//                tcpClient = TcpClient.getInstance(ChatActivity.this);
//                tcpClient.startSend();
//                if (FileUtils.isFileExists(sendFilePath))
//                    tcpClient.sendFile(sendFilePath, mPeople.getIpaddress(),
//                            Message.CONTENT_TYPE.FILE);
//            }
//                break;

        } // end of switch
    }

    public void sendMessage(String content, int type) {
//        String nowtime = DateUtils.getNowtime();
//        Message msg = new Message(mIMEI, nowtime, content, type);
//        mMessagesList.add(msg);
//        mApplication.addLastMsgCache(mPeople.getDeviceId(), msg); // 更新消息缓存
    	int deviceId=BaseApplication.getInstance().getDeviceID();
    	int id=(int)System.currentTimeMillis();
    	byte[] idBytes=TypeConvert.int2byte(id);
    	byte[] data;
    	FramePacket packet;
    	byte[] textData;
    	SMSMessage message;
        switch (type) {
        
            case FrameType.INFO_MESSAGE:
            	textData=content.getBytes();
            	data=new byte[textData.length+4]; 
            	System.arraycopy(idBytes, 0, data, 0, idBytes.length);
            	System.arraycopy(textData, 0, data, idBytes.length, textData.length);
            	packet = new FramePacket(deviceId, mDisplayId, type, data);
            	message=new SMSMessage(packet, 0, DateUtils.getSimpleTime());
            	DBOperate.getInstance(this).saveMessage(message);
            	UDPSocketService.getInstance().postMessage(packet.getFramePacket(), packet.getFramePacket().length);
                break;
                
            case FrameType.INFO_FILE:
            	textData=content.getBytes();
            	data=new byte[textData.length+4]; 
            	System.arraycopy(idBytes, 0, data, 0, idBytes.length);
            	System.arraycopy(textData, 0, data, idBytes.length, textData.length);
            	packet = new FramePacket(deviceId, mDisplayId, type, data);
            	message=new SMSMessage(packet, 0, DateUtils.getSimpleTime());
            	DBOperate.getInstance(this).saveMessage(message);
            	FileSendDispose.getInstance().postFilePath(content, deviceId, mDisplayId);
            	break;

//            case IMAGE:
//                UDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SEND_IMAGE_DATA,
//                        mPeople.getIpaddress());
//                break;
//
//            case VOICE:
//                UDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SEND_VOICE_DATA,
//                        mPeople.getIpaddress());
//                break;
//
//            case FILE:
//                Message fileMsg = msg.clone();
//                fileMsg.setMsgContent(FileUtils.getNameByPath(msg.getMsgContent()));
//                UDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mPeople.getIpaddress(),
//                        fileMsg);
//                break;

        }
//
//        mDBOperate.addChattingInfo(mID, mSenderID, nowtime, content, type);// 新增方法
    }

    // 录音计时线程
    private void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    // 录音时显示Dialog
    private void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(ChatActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.drawable.record_cancel);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;

            default:
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
                mTvRecordDialogTxt.setText("向上滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    // 录音线程
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // 限制录音时长
                // if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                // imgHandle.sendEmptyMessage(0);
                // } else
                {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        // 获取音量，更新dialog
                        if (!isMove) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            setDialogImage();
        }
    };

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        if (voiceValue < 800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_01);
        }
        else if (voiceValue > 800.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_02);
        }
        else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_03);
        }
        else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_04);
        }
        else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_05);
        }
        else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_06);
        }
        else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_07);
        }
        else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_08);
        }
        else if (voiceValue > 4000.0 && voiceValue < 5000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_09);
        }
        else if (voiceValue > 5000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_10);
        }
        else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_11);
        }
        else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_12);
        }
        else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_13);
        }
        else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_14);
        }
    }

    // 录音时间太短时Toast显示
    void showWarnToast(String toastText) {
        Toast toast = new Toast(ChatActivity.this);
        LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(ChatActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short); // 图标

        TextView mTv = new TextView(ChatActivity.this);
        mTv.setText(toastText);
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景
        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.show();
    }

    /** 调用文件选择软件来选择文件 **/
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要发送的文件"), FILE_SELECT_CODE);
        }
        catch (ActivityNotFoundException ex) {
            Toast.makeText(ChatActivity.this, "缺少文件管理器", Toast.LENGTH_SHORT).show();
        }
    }
    
    /** 跳转至语音通话 **/
    private void showAudioChooser() {
        Intent intent = new Intent(this,AudioActivity.class);
        intent.putExtra(AudioActivity.DEVICE_ID, mDisplayId);
        intent.putExtra(AudioActivity.AUDIO_STATE_ID, AudioActivity.AUDIO_REQUEST);
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException ex) {
            Toast.makeText(ChatActivity.this, "缺少文件管理器", Toast.LENGTH_SHORT).show();
        }
    }
    
    /** 跳转至视频通话 **/
    private void showVideoChooser() {
        Intent intent = new Intent(this,VideoActivity.class);
        intent.putExtra(VideoActivity.DEVICE_ID, mDisplayId);
        intent.putExtra(VideoActivity.VIDEO_STATE_ID, VideoActivity.VIDEO_REQUEST);
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException ex) {
            Toast.makeText(ChatActivity.this, "缺少文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public void handlerMessage(ChatMessage message) {
		// TODO Auto-generated method stub
		if(ChatMessage.MESSAGE_TEXT_DATA.equals(message.getMessageId())){
			loadMessage();
		}
		
	}

}
