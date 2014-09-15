package com.tiny.chat.utils;

import java.util.HashMap;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.domain.People1;
public class SessionUtils {
    private static HashMap<String, String> mlocalUserSession = BaseApplication.getInstance()
            .getUserSession();

    public static void setBirthday(String birthday){
    	mlocalUserSession.put(People1.BIRTHDAY, birthday);
    }
    
    public static String getBirthday(){
    	return mlocalUserSession.get(People1.BIRTHDAY);
    }
    /**
     * 获取用户数据库id
     * @return
     */
    public static int getLocalUserID(){
        return Integer.parseInt(mlocalUserSession.get(People1.ID));
    }

    /**
     * 获取本地IP
     * 
     * @return localIPaddress
     */
    public static String getLocalIPaddress() {
        return mlocalUserSession.get(People1.IPADDRESS);
    }

    /**
     * 获取热点IP
     * 
     * @return serverIPaddress
     */
    public static String getServerIPaddress() {
        return mlocalUserSession.get(People1.SERVERIPADDRESS);
    }

    /**
     * 获取昵称
     * 
     * @return Nickname
     */
    public static String getNickname() {
        return mlocalUserSession.get(People1.NICKNAME);
    }

    /**
     * 获取性别
     * 
     * @return Gender
     */
    public static String getGender() {
        return mlocalUserSession.get(People1.GENDER);
    }

    /**
     * 获取ID
     * 
     * @return ID
     */
    public static String getDeviceId() {
        
        return String.valueOf(BaseApplication.getInstance().getDeviceID());
    }

    /**
     * 获取数据库用户编号
     * 
     * @return id
     */
    // public int getID() {
    // return Integer.parseInt(GlobalSession.get(ID));
    // }

    /**
     * 获取设备品牌型号
     * 
     * @return device
     */
    public static String getDevice() {
        return mlocalUserSession.get(People1.DEVICE);
    }

    /**
     * 获取头像编号
     * 
     * @return AvatarNum
     */
    public static int getAvatar() {
        return Integer.parseInt(mlocalUserSession.get(People1.AVATAR));
    }

    /**
     * 获取星座
     * 
     * @return
     */
    public static String getConstellation() {
        return mlocalUserSession.get(People1.CONSTELLATION);
    }

    /**
     * 获取年龄
     * 
     * @return Age
     */
    public static int getAge() {
        return Integer.parseInt(mlocalUserSession.get(People1.AGE));
    }

    /**
     * 获取登录状态编码
     * 
     * @return OnlineStateInt
     */
    public static int getOnlineStateInt() {
        return Integer.parseInt(mlocalUserSession.get(People1.ONLINESTATEINT));
    }

    /**
     * 获取是否为客户端
     * 
     * @return isClient
     */
    public static boolean getIsClient() {
        return Boolean.parseBoolean(mlocalUserSession.get(People1.ISCLIENT));
    }

    /**
     * 获取登录时间
     * 
     * @return Data 登录时间 年月日
     */
    public static String getLoginTime() {
        return mlocalUserSession.get(People1.LOGINTIME);
    }
    
    /**
     * 设置用户数据库id
     * @param paramID
     */
    public static void setLocalUserID(int paramID){
        mlocalUserSession.put(People1.ID, String.valueOf(paramID));
    }

    /**
     * 设置登录时间
     * 
     * @param paramLoginTime
     */
    public static void setLoginTime(String paramLoginTime) {
        mlocalUserSession.put(People1.LOGINTIME, paramLoginTime);
    }

    /**
     * 设置本地IP
     * 
     * @param paramLocalIPaddress
     *            本地IP地址值
     */
    public static void setLocalIPaddress(String paramLocalIPaddress) {
        mlocalUserSession.put(People1.IPADDRESS, paramLocalIPaddress);
    }

    /**
     * 设置热点IP
     * 
     * @param paramServerIPaddress
     *            热点IP地址值
     */
    public static void setServerIPaddress(String paramServerIPaddress) {
        mlocalUserSession.put(People1.SERVERIPADDRESS, paramServerIPaddress);
    }

    /**
     * 设置昵称
     * 
     * @param paramNickname
     * 
     */
    public static void setNickname(String paramNickname) {
        mlocalUserSession.put(People1.NICKNAME, paramNickname);
    }

    /**
     * 设置星座
     * 
     * @param paramConstellation
     */
    public static void setConstellation(String paramConstellation) {
        mlocalUserSession.put(People1.CONSTELLATION, paramConstellation);
    }

    /**
     * 设置性别
     * 
     * @param paramGender
     * 
     */
    public static void setGender(String paramGender) {
        mlocalUserSession.put(People1.GENDER, paramGender);
    }

    /**
     * 设置IMEI
     * 
     * @param paramIMEI
     *            本机的IMEI值
     */
    public static void setIMEI(String paramIMEI) {
        mlocalUserSession.put(People1.DEVICEID, paramIMEI);
    }

    /**
     * 设置设备品牌型号
     * 
     * @param paramDevice
     */
    public static void setDevice(String paramDevice) {
        mlocalUserSession.put(People1.DEVICE, paramDevice);
    }

    /**
     * 设置登陆状态编码
     * 
     * <p>
     * 登陆编码：0 - 在线 , 1 - 忙碌 , 2 - 隐身 , 3 - 离开
     * </p>
     * 
     * @param paramOnlineStateInt
     *            登陆状态的具体编码
     */
    public static void setOnlineStateInt(int paramOnlineStateInt) {
        mlocalUserSession.put(People1.ONLINESTATEINT, String.valueOf(paramOnlineStateInt));
    }

    /**
     * 设置头像编号
     * 
     * @param paramAvatar
     *            选择的头像编号
     */
    public static void setAvatar(int paramAvatar) {
        mlocalUserSession.put(People1.AVATAR, String.valueOf(paramAvatar));
    }

    /**
     * 设置年龄
     * 
     * @param paramAge
     */
    public static void setAge(int paramAge) {
        mlocalUserSession.put(People1.AGE, String.valueOf(paramAge));
    }

    /**
     * 设置是否为客户端
     * 
     * @param paramIsClient
     */
    public static void setIsClient(boolean paramIsClient) {
        mlocalUserSession.put(People1.ISCLIENT, String.valueOf(paramIsClient));
    }
    
    public static boolean isItself(String paramIMEI){
        if(paramIMEI == null){
            return false;
        }
        else if(getDeviceId().equals(paramIMEI)){
            return true;
        }
        return false;
    }

    /** 清空全局登陆Session信息 **/
    public static void clearSession() {
        mlocalUserSession.clear();
    }

}
