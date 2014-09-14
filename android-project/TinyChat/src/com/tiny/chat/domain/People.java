package com.tiny.chat.domain;

import java.io.Serializable;

public class People implements Serializable{
	private static final long serialVersionUID = 9176662866641908241L;
	
	 /** 用户信息常量 **/
   private FriendInfo userData;
   private int msgCount;        // 未读消息数

   public People(int sourceID) {
       msgCount = 0; // 初始化为0
       userData=new FriendInfo();
       userData.setSourceID(sourceID);
       
   }
   
   public People(FramePacket frame) {
       msgCount = 0; // 初始化为0
       userData=new FriendInfo();
       userData.setSourceID(frame.getSourceID());
       byte data[]=frame.getData();
       userData.setRegisterType(data[0]);
       
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
   
   public FriendInfo getUserData() {
		return userData;
	}

	public void setUserData(FriendInfo userData) {
		this.userData = userData;
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
 		if(people!=null && people.getUserData()!=null){
 			FriendInfo data=people.getUserData();
 			if(data.getSourceID()==userData.sourceID){
 				return true;
 			}
 		}
 		return false;
 	}
 	
 	@Override
 	public int hashCode() {
 		final int prime=31;
 		int result=1;
 		result=prime * result +userData.sourceID;
 		return result;
 	}

}

