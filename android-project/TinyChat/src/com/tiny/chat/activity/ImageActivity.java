package com.tiny.chat.activity;


import com.tiny.chat.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageActivity extends Activity{
	private ViewPager mPicPager;
	private PicPagerAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		mPicPager=(ViewPager)findViewById(R.id.vp_image);
		adapter=new PicPagerAdapter(this);
		mPicPager.setAdapter(adapter);
	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	class PicPagerAdapter extends PagerAdapter{
	
		private Context mContext;
		private LayoutInflater mInflater;
		public PicPagerAdapter(Context context){
			this.mContext = context;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			//View view = mInflater.inflate(R.layout., null);
			//GalleryTouchImageView galleryTouchImageView = new GalleryTouchImageView(mContext,mManager,mSlideController);
			ImageView imageView=new ImageView(mContext);
			//galleryTouchImageView.setTag(position);
			//final Picture picture = mPictures.get(position);
			//galleryTouchImageView.loadImage(picture);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			imageView.setBackgroundResource(R.drawable.avatar12);
			container.addView(imageView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return imageView; //super.instantiateItem(container, position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((View)object);
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view==object;
		}
	}
	
}
