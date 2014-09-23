package com.tiny.chat.adapter;

import com.tiny.chat.R;
import com.tiny.chat.domain.People;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendAdapter extends ArrayListAdapter<People> {

	public FriendAdapter(Context context) {
		super(context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_user, null);
			holder = new ViewHolder();

			holder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.user_item_iv_avatar);
			holder.mIvDevice = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_device);
			holder.mHtvName = (TextView) convertView
					.findViewById(R.id.user_item_htv_name);
			holder.mLayoutGender = (LinearLayout) convertView
					.findViewById(R.id.user_item_layout_gender1);
			holder.mIvGender = (ImageView) convertView
					.findViewById(R.id.user_item_iv_gender);
			holder.mHtvAge = (TextView) convertView
					.findViewById(R.id.user_item_htv_age);
			holder.mHtvTime = (TextView) convertView
					.findViewById(R.id.user_item_htv_time);
			holder.mHtvLastMsg = (TextView) convertView
					.findViewById(R.id.user_item_htv_lastmsg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// NearByPeople people = (NearByPeople) getItem(position);
		// holder.mIvAvatar.setImageBitmap(ImageUtils.getAvatar(mApplication,
		// mContext,
		// NearByPeople.AVATAR + people.getAvatar()));
		// holder.mHtvName.setText(people.getNickname());
		// holder.mLayoutGender.setBackgroundResource(people.getGenderBgId());
		// holder.mIvGender.setImageResource(people.getGenderId());
		// holder.mHtvAge.setText(people.getAge() + "");
		// holder.mHtvTime.setText(DateUtils.getBetweentime(people.getLogintime()));
		// holder.mHtvLastMsg.setText(mApplication.getLastMsgCache(people.getIMEI()));
		// holder.mIvDevice.setImageResource(R.drawable.ic_userinfo_android);
		People people=mList.get(position);
		holder.mHtvName.setText(""+people.getFriendInfo().getSourceID());
		return convertView;
	}

	class ViewHolder {
		ImageView mIvAvatar;
		ImageView mIvDevice;
		TextView mHtvName;
		LinearLayout mLayoutGender;
		ImageView mIvGender;
		TextView mHtvAge;
		TextView mHtvTime;
		TextView mHtvLastMsg;
	}

}
