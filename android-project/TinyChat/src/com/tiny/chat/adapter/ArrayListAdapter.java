package com.tiny.chat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 *抽象集合适配器。继承最基本的适配。
 *当需要重新写适配器的是可以继承此适配器。重写里面的getView方法
 *
 */
public abstract class ArrayListAdapter<T> extends BaseAdapter {
	//数据源
    protected List<T> mList;
    //上下文
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<Integer> mSelects = new ArrayList<Integer>();
    public ArrayListAdapter(Context context){
		this.mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
    /**
     * 返回的总条目数
     */
	@Override
	public int getCount() {
		if(mList!=null){
			return mList.size();
		}else{
			 return 0;
		}
	}
    /**
     * 返回每个Itenm的对应得对象
     */
	@Override
	public Object getItem(int position) {
		
		return mList==null?null:mList.get(position);
	}
    /**
     * 返回每个Item的id，在这里设置的是Item的position
     */
	@Override
	public long getItemId(int position) {
		
		return position;
	}
    /**
     * 抽象getView方法.目的是重写里面的getView
     */
	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
    /**
     * 获取数据源
     * @return mList 数据源
     */
	public List<T> getList() {
		return mList;
	}
     /**
      * 设置适配器的数据并刷新适配器
      * @param mList
      */
	public void setList(List<T> list,boolean refresh) {
		this.mList = list;
		if(refresh)
		  notifyDataSetChanged();
	}
	/**
	 * 在原来的数据add
	 * @param mList
	 */
	public void addList(List<T> list,boolean refresh) {
		if(mList!=null){
			 mList.addAll(list);
		}else{
			this.mList = list;
		}
		if(refresh)
		  notifyDataSetChanged();
		}
	/**
	 * 初始化
	 * @param mList
	 */
	public void setListNull() {
		this.mList = null;
		 notifyDataSetChanged();
	}
	/**
	 * 设置数据源是数组的形式
	 * @param list
	 */
	public void setList(T[] list,boolean refresh){
		ArrayList<T> arrayList = new ArrayList<T>(list.length);  
		for (T t : list) {  
			arrayList.add(t);  
		}  
		setList(arrayList,refresh);
	}
	
	/**
	 * 添加条目position
	 * @param position
	 */
	public void addInteger(int position) {
		if(!mSelects.contains(position)){
			mSelects.clear();
			mSelects.add(position);
		}
			
	}
	/**
	 * 添加条目position
	 * @param position
	 */
	public boolean deleteInteger(int position) {
		if(mSelects.contains(position)){
			mSelects.clear();
			return true;
		}
		return false;
	}
	
	public int getLocation(){
		if(mSelects==null||mSelects.size()==0)
			return -1;
		return mSelects.get(0);
	}
	
	public void clearPostion(){
		if(mSelects!=null)
			mSelects.clear();
	}
	
}
