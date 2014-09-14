package com.tiny.chat.adapter;

import java.io.File;


import com.tiny.chat.BaseApplication;
import com.tiny.chat.R;
import com.tiny.chat.domain.Message;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.file.FileState;
import com.tiny.chat.socket.FrameType;
import com.tiny.chat.utils.FileUtils;
import com.tiny.chat.utils.ImageUtils;
import com.tiny.chat.utils.SessionUtils;
import com.tiny.chat.view.EmoticonsTextView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChatAdapter extends ArrayListAdapter<SMSMessage> {

    private static String TAG = "ChatAdapter";

    private static final int TYPE_LEFT_TEXT = 0;
    private static final int TYPE_LEFT_IMAGE = 1;
    private static final int TYPE_LEFT_VOICE = 2;
    private static final int TYPE_LEFT_FILE = 3;

    private static final int TYPE_RIGHT_TEXT = 4;
    private static final int TYPE_RIGHT_IMAGE = 5;
    private static final int TYPE_RIGHT_VOICE = 6;
    private static final int TYPE_RIGHT_FILE = 7;

    private static final int ITEM_POSITION = -1;

    private ListView mListView;
   
    private Bitmap mAvatarBitmap;
    private Bitmap mImageContentBitmap;

    private MediaPlayer mMediaPlayer;
    private boolean isPlay = false; // 播放状态

    public ChatAdapter(Context context) {
        super(context);
//        TcpClient.setHandler(mHandler); // 绑定handler
//        TcpService.setHandler(mHandler);

    }

 
 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SMSMessage msg = (SMSMessage) getItem(position);
        if (SessionUtils.isItself(String.valueOf(msg.getDestionID()))) {
//            mAvatarBitmap = ImageUtils.getAvatar(mApplication, mContext, People1.AVATAR
//                    + SessionUtils.getAvatar());
        }
        else {
//            mPeople = mApplication.getOnlineUsers().get(position); // 获取用户对象
//            mAvatarBitmap = ImageUtils.getAvatar(mApplication, mContext, People1.AVATAR
//                    + mPeople.getAvatar());
        }
        int messageType = getItemViewType(position);

        ViewHolder holder = null;
        OnClick listener = null;

        if (convertView == null) {

            holder = new ViewHolder();
            listener = new OnClick();

            switch (messageType) {
                case TYPE_LEFT_TEXT:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_text, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mEtvTextContent = (EmoticonsTextView) holder.mView
                            .findViewById(R.id.message_etv_msgtext);

                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_IMAGE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_image, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mIvImageContent = (ImageView) holder.mView
                            .findViewById(R.id.message_iv_msgimage);

                    holder.mLayoutMessageContainer.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_VOICE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_voice, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mIvVoiceImage = (ImageView) holder.mView
                            .findViewById(R.id.voice_message_iv_msgimage);

                    holder.mIvVoiceImage.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_FILE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_file, null);

                    holder.mIvFileBg = (ImageView) holder.mView.findViewById(R.id.messgae_file_bg);
                    holder.mHtvLoadingProcess = (TextView) holder.mView
                            .findViewById(R.id.message_file_htv_loading_text);
                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);

                    holder.mIvFileBg.setClickable(true);
                    holder.mIvFileBg.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_TEXT:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_text, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mEtvTextContent = (EmoticonsTextView) holder.mView
                            .findViewById(R.id.message_etv_msgtext);

                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_IMAGE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_image, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mIvImageContent = (ImageView) holder.mView
                            .findViewById(R.id.message_iv_msgimage);

                    holder.mLayoutMessageContainer.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_VOICE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_voice, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mIvVoiceImage = (ImageView) holder.mView
                            .findViewById(R.id.voice_message_iv_msgimage);

                    holder.mIvVoiceImage.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_FILE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (TextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_file, null);

                    holder.mIvFileBg = (ImageView) holder.mView.findViewById(R.id.messgae_file_bg);
                    holder.mHtvLoadingProcess = (TextView) holder.mView
                            .findViewById(R.id.message_file_htv_loading_text);
                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);

                    holder.mIvFileBg.setClickable(true);
                    holder.mIvFileBg.setOnClickListener(listener);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;
            }
            convertView.setTag(holder);
            convertView.setTag(holder.mLayoutMessageContainer.getId(), listener); // 对监听对象保存

        }
        else {
            holder = (ViewHolder) convertView.getTag();
            listener = (OnClick) convertView.getTag(holder.mLayoutMessageContainer.getId());
        }

        switch (messageType) {
            case TYPE_LEFT_TEXT:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvLeftAvatar.setImageBitmap(mAvatarBitmap);
                holder.mEtvTextContent.setText(msg.getContent());
                break;

            case TYPE_LEFT_IMAGE:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvLeftAvatar.setImageBitmap(mAvatarBitmap);
                mImageContentBitmap = getImageBitmap(msg);
                holder.mIvImageContent.setImageBitmap(mImageContentBitmap);
                break;

            case TYPE_LEFT_VOICE:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvLeftAvatar.setImageBitmap(mAvatarBitmap);
                holder.mIvVoiceImage.setImageResource(R.drawable.voicerecord_left);
                break;

            case TYPE_LEFT_FILE:
                holder.mHtvLoadingProcess.setTag(FileUtils.getNameByPath(msg.getContent()));
                holder.mHtvLoadingProcess.setTag(ITEM_POSITION, position);

                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvLeftAvatar.setImageBitmap(mAvatarBitmap);

                if (FileUtils.isFileExists(msg.getContent())) {
                    holder.mHtvLoadingProcess.setVisibility(View.GONE);
                }
                else {
                    holder.mHtvLoadingProcess.setVisibility(View.VISIBLE);
                   // holder.mHtvLoadingProcess.setText(msg.get + "");

                }
                break;

            case TYPE_RIGHT_TEXT:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvRightAvatar.setImageBitmap(mAvatarBitmap);
                holder.mEtvTextContent.setText(msg.getContent());
                break;

            case TYPE_RIGHT_IMAGE:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvRightAvatar.setImageBitmap(mAvatarBitmap);
                mImageContentBitmap = getImageBitmap(msg);
                holder.mIvImageContent.setImageBitmap(mImageContentBitmap);
                break;

            case TYPE_RIGHT_VOICE:
                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvRightAvatar.setImageBitmap(mAvatarBitmap);
                holder.mIvVoiceImage.setImageResource(R.drawable.voicerecord_right);
                break;

            case TYPE_RIGHT_FILE:

                holder.mHtvLoadingProcess.setTag(FileUtils.getNameByPath(msg.getContent()));
                holder.mHtvLoadingProcess.setTag(ITEM_POSITION, position);

                holder.mHtvTimeStampTime.setText(msg.getDate());
                holder.mIvRightAvatar.setImageBitmap(mAvatarBitmap);

                if (FileUtils.isFileExists(msg.getContent())) {
                    holder.mHtvLoadingProcess.setVisibility(View.GONE);
                }
                else {
                    holder.mHtvLoadingProcess.setVisibility(View.VISIBLE);
                //    holder.mHtvLoadingProcess.setText(msg.getPercent() + "");

                }
                break;
        }

        listener.setMessage(msg);
        return convertView;
    }

    class OnClick implements OnClickListener {
        SMSMessage messageType;

        public void setMessage(SMSMessage paramMsg) {
            this.messageType = paramMsg;
        }

        @Override
        public void onClick(View v) {
            switch (messageType.getMessageType()) {
//                case IMAGE:
//                    Intent imgIntent = new Intent(mContext, ImageBrowserActivity.class);
//                    imgIntent.putExtra(ImageBrowserActivity.IMAGE_TYPE,
//                            ImageBrowserActivity.TYPE_PHOTO);
//                    imgIntent.putExtra(ImageBrowserActivity.PATH, message.getContent());
//                    mContext.startActivity(imgIntent);
//                    ((ChatActivity) mContext).overridePendingTransition(R.anim.zoom_enter, 0);
//
//                    break;
//
//                case VOICE:
//                    // 播放录音
//                    final ImageView imgView = (ImageView) v;
//                    if (!isPlay) {
//                        mMediaPlayer = new MediaPlayer();
//                        String filePath = message.getContent();
//                        try {
//                            mMediaPlayer.setDataSource(filePath);
//                            mMediaPlayer.prepare();
//                            imgView.setImageResource(R.drawable.voicerecord_stop);
//                            isPlay = true;
//                            mMediaPlayer.start();
//                            // 设置播放结束时监听
//                            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    if (isPlay) {
//                                        imgView.setImageResource(R.drawable.voicerecord_right);
//                                        isPlay = false;
//                                        mMediaPlayer.stop();
//                                        mMediaPlayer.release();
//                                    }
//                                }
//                            });
//                        }
//                        catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else {
//                        if (mMediaPlayer.isPlaying()) {
//                            mMediaPlayer.stop();
//                            mMediaPlayer.release();
//                            isPlay = false;
//                        }
//                        else {
//                            isPlay = false;
//                            mMediaPlayer.release();
//                        }
//                        imgView.setImageResource(R.drawable.voicerecord_right);
//                    }
//
//                    break;

                case (int)FrameType.INFO_FILE:
                    Intent fileIntent = new Intent();
                    fileIntent.setType("*/*");
                    fileIntent.setData(Uri.parse("file://"
                            + FileUtils.getPathByFullPath(messageType.getContent())));
                    mContext.startActivity(fileIntent);
                    break;

                default:
                    break;

            }

        }
    }

    /**
     * 根据数据源的position返回需要显示的的layout的type
     * 
     * */
    @Override
    public int getItemViewType(int position) {

        Message msg = (Message) getItem(position);
        int type = -1;
        if (SessionUtils.isItself(String.valueOf(msg.getSourceID()))) {
            switch (msg.getMessageType()) {
                case (int)FrameType.INFO_MESSAGE:
                    type = TYPE_RIGHT_TEXT;
                    break;

//                case IMAGE:
//                    type = 5;
//                    break;
//
//                case VOICE:
//                    type = 6;
//                    break;

                case (int)FrameType.INFO_FILE:
                    type = 7;
                    break;
            }

        }
        else {
            switch (msg.getMessageType()) {
                case (int)FrameType.INFO_MESSAGE:
                    type = TYPE_LEFT_TEXT;
                    break;

//                case IMAGE:
//                    type = 1;
//                    break;
//
//                case VOICE:
//                    type = 2;
//                    break;

                case (int)FrameType.INFO_FILE:
                    type = 3;
                    break;
            }
        }
        return type;
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message paramMsg) {
            updateView(paramMsg);
        }
    };

    private void updateView(android.os.Message paramMsg) {
        FileState file = (FileState) paramMsg.obj;

        if (mListView != null) {
            TextView htvView = (TextView) mListView.findViewWithTag(FileUtils
                    .getNameByPath(file.fileName));

            if (htvView != null) {
                int itemPosition = (Integer) htvView.getTag(ITEM_POSITION);
                int visiblePos = mListView.getFirstVisiblePosition();
                int offset = itemPosition - visiblePos;

                SMSMessage itemMessage = (SMSMessage) getItem(itemPosition);
             //   itemMessage.setPercent(file.percent);

                // 只有在可见区域才更新
                if (offset < 0)
                    return;

                else {
                    if (file.percent != 100) {
                        htvView.setText(file.percent + "");
                    }
                    else {
                        htvView.setVisibility(View.GONE);
                    }
                }

            }
        }
    }

    private Bitmap getImageBitmap(SMSMessage msg) {
        String imagePath = BaseApplication.THUMBNAIL_PATH + File.separator + msg.getSourceID()
                + File.separator + FileUtils.getNameByPath(msg.getContent());
        Bitmap bitmap = ImageUtils.getBitmapFromPath(imagePath);
        
        Log.i(TAG, "聊天图片：" + imagePath);
        
        if (mImageContentBitmap == null)
            mImageContentBitmap = ImageUtils.getBitmapFromPath(msg.getContent());
        return bitmap;
    }

    /**
     * 返回所有的layout的数量
     * 
     * */
    @Override
    public int getViewTypeCount() {
        return 8;
    }

    static class ViewHolder {

        private TextView mHtvTimeStampTime; // 时间
        private LinearLayout mLayoutMessageContainer; // 消息容器
        private View mView;

        private EmoticonsTextView mEtvTextContent; // 文本内容
        private ImageView mIvImageContent; // 图像内容
        private ImageView mIvVoiceImage; // 声音图像
        private TextView mHtvLoadingProcess; // 下载进度条
        private ImageView mIvFileBg;

        private ImageView mIvLeftAvatar; // 左边的头像
        private ImageView mIvRightAvatar; // 右边的头像

    }

}
