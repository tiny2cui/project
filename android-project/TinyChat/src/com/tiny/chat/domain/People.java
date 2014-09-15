package com.tiny.chat.domain;

import java.io.Serializable;

public class People implements Serializable{
	private static final long serialVersionUID = 9176662866641908241L;
	
	 /** 用户信息常量 **/
   private FriendInfo friendInfo;
   private int msgCount;        // 未读消息数

   public People(int sourceID) {
       msgCount = 0; // 初始化为0
       friendInfo=new FriendInfo();
       friendInfo.setSourceID(sourceID);
       
   }
   
   public People(FramePacket frame) {
       msgCount = 0; // 初始化为0
       friendInfo=new FriendInfo();
       friendInfo.setSourceID(frame.getSourceID());
       byte data[]=frame.getData();
       friendInfo.setRegisterType(data[0]);
       
   }
   
  
   public int getMsgCount() {
       return this.msgCount;
   }

   public void setMsgCount(int paramMsgCount) {
       this.msgCount = paramMsgCount;
   }

   public int describeContents() {
       return 0;
   }
   
   public FriendInfo getFriendInfo() {
		return friendInfo;
	}

	public void setUserData(FriendInfo info) {
		this.friendInfo = info;
	}
 	
 	@Override
 	public boolean equals(Object o) {
 		if(this==o){
 			return true;
 		}
 		if(o==null){
 			return false;
 		}
 		if(getClass() !=o.getClass()){
 			return false;
 		}
 		People people= (People)o;
 		if(people!=null && people.getFriendInfo()!=null){
 			FriendInfo data=people.getFriendInfo();
 			if(data.getSourceID()==friendInfo.sourceID){
 				return true;
 			}
 		}
 		return false;
 	}
 	
 	@Override
 	public int hashCode() {
 		final int prime=31;
 		int result=1;
 		result=prime * result +friendInfo.sourceID;
 		return result;
 	}

}

