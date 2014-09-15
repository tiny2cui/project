package com.tiny.chat.domain;

import java.io.Serializable;

import android.R.integer;

import com.tiny.chat.R;

/**
 * 
 * @author admin
 *
 */
public class People1 implements Serializable{
	private static final long serialVersionUID = 9176662866641908241L;
	
	 /** 用户信息常量 **/
    public static final String ID = "ID";
    public static final String NICKNAME = "Nickname";
    public static final String GENDER = "Gender";
    public static final String DEVICEID = "DeviceId";
    public static final String DEVICE = "Device";
    public static final String AVATAR = "avatar";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "Age";    
    public static final String CONSTELLATION = "Constellation";
    public static final String ONLINESTATEINT = "OnlineStateInt";
    public static final String ISCLIENT = "isClient";
    public static final String IPADDRESS = "Ipaddress";
    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static final String LOGINTIME = "LoginTime";
    public static final String ENTITY_PEOPLE = "entity_people";
    private int mDevideId; // ID
	private int mAvatar; // 头像
    private String mDevice; // 设备 Android PC
    private String mNickname; // 昵称
    private String mConstellation; // 星座
    private String mGender; // 性别
    private int mGenderId; // 性别对应的图片资源ResId
    private int mGenderBgId; // 性别对应的背景资源ResId
    private int mAge; // 年龄
    private String mIpaddress; // IP地址
    private String mLogintime;// 登陆时间
    private int onlineStateInt; // 在线状态
    private int msgCount; // 未接收消息数
    private int type; 

	public static final int MESSAGE=0,FRIEND=1; 

    public People1(int id) {
        msgCount = 0; // 初始化为0
        mDevideId=id;
    }

    public People1(int paramDeviceId, int paramAvatar, String paramDevice,
            String paramNickname, String paramGender, int paramAge, String paramConstellation,
            String paramIP, String paramLogintime) {
        super();
        this.mDevideId=paramDeviceId;
        this.mAvatar = paramAvatar;
        this.mDevice = paramDevice;
        this.mNickname = paramNickname;
        setGender(paramGender);
        this.mAge = paramAge;
        this.mConstellation = paramConstellation;
        this.mIpaddress = paramIP;
        this.mLogintime = paramLogintime;
    }

  
    public int getmDevideId() {
		return mDevideId;
	}

	public void setmDevideId(int mDevideId) {
		this.mDevideId = mDevideId;
	}
	
    public int getAvatar() {
        return this.mAvatar;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }
  
    public String getDevice() {
        return this.mDevice;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }
   
    public String getNickname() {
        return this.mNickname;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
    }

   
    public String getGender() {
        return this.mGender;
    }

    public void setGender(String paramGender) {
        this.mGender = paramGender;
        if ("女".equals(paramGender)) {
            setGenderId(R.drawable.ic_user_female);
            setGenderBgId(R.drawable.bg_gender_female);
        }
        else {
            setGenderId(R.drawable.ic_user_male);
            setGenderBgId(R.drawable.bg_gender_male);
        }
    }

   
    public int getGenderId() {
        return this.mGenderId;
    }

    public void setGenderId(int paramGenderId) {
        this.mGenderId = paramGenderId;
    }

   
    public int getGenderBgId() {
        return this.mGenderBgId;
    }

    public void setGenderBgId(int paramGenderBgId) {
        this.mGenderBgId = paramGenderBgId;
    }

   
    public int getAge() {
        return this.mAge;
    }

    public void setAge(int paramAge) {
        this.mAge = paramAge;
    }

    
    public String getConstellation() {
        return this.mConstellation;
    }

    public void setConstellation(String paramConstellation) {
        this.mConstellation = paramConstellation;
    }

 
    public String getIpaddress() {
        return this.mIpaddress;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    
    public String getLogintime() {
        return this.mLogintime;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

   
    public int getOnlineStateInt() {
        return this.onlineStateInt;
    }

    public void setOnlineStateInt(int paramOnlineState) {
        this.onlineStateInt = paramOnlineState;
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
    public int getType() {
  		return type;
  	}

  	public void setType(int type) {
  		this.type = type;
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
  		People1 other=(People1)o;
  		if(mDevideId!=other.mDevideId){
  			return false;
  		}
  		if(mIpaddress ==null){
  			if(other.mIpaddress!=null){
  				return false;
  			}
  		}else if(mIpaddress.equals(other.mIpaddress)){
  			return false;
  		}
  		
  		return true;
  	}
  	
  	@Override
  	public int hashCode() {
  		final int prime=31;
  		int result=1;
  		result=prime * result +mDevideId;
  		result=prime * result + ((mIpaddress==null) ? 0: mIpaddress.hashCode());
  		return result;
  	}

}
