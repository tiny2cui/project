package com.tiny.chat.domain;

import java.io.Serializable;

import com.tiny.chat.utils.TypeConvert;

public class Position implements Serializable {
	
	private static final long serialVersionUID = 5499336898003799975L;
	private int messageId;
	private byte latitudeDegree;            //纬度_度
	private byte latitudeMinute;            //纬度_分
	private byte latitudeSecond;            //纬度_秒
	private short latitudeMillisecond;       //纬度_毫秒
	private byte latitudeIdentification;    //纬度_标识
	private byte longitudeDegree;           //经度_度
	private byte longitudeMinute;           //经度_分
	private byte longitudeSecond;           //经度_秒
	private short longitudeMillisecond;      //经度_毫秒
	private byte longitudeIdentification;   //经度_标识
	private float elevation;                //高程
	private short timeYear;                 //年
	private byte timeMonth;                 //月
	private byte timeDay;                   //日
	private byte timeHour;                  //时
	private byte timeMinute;                //分
	private byte timeSecond;                //秒
	private byte timeHundredthSecond;       //百分之一秒
	
	
	public Position(int messageId, byte latitudeDegree,byte latitudeMinute,byte latitudeSecond,short latitudeMillisecond,
			byte latitudeIdentification,byte longitudeDegree,byte longitudeMinute,byte longitudeSecond,short longitudeMillisecond,
			byte longitudeIdentification,float elevation,short timeYear,byte timeMonth,byte timeDay,byte timeHour,byte timeMinute,
			byte timeSecond,byte timeHundredthSecond) {
		this.messageId=messageId;
		this.latitudeDegree=latitudeDegree;
		this.latitudeMinute=latitudeMinute;
		this.latitudeSecond=latitudeSecond;
		this.latitudeMillisecond=latitudeMillisecond;
		this.latitudeIdentification=latitudeIdentification;
		this.longitudeDegree=longitudeDegree;
		this.longitudeMinute=longitudeMinute;
		this.longitudeSecond=longitudeSecond;
		this.longitudeMillisecond=longitudeMillisecond;
		this.latitudeIdentification=longitudeIdentification;
		this.elevation=elevation;
		this.timeYear=timeYear;
		this.timeMonth=timeMonth;
		this.timeDay=timeDay;
		this.timeHour=timeHour;
		this.timeSecond=timeSecond;
		this.timeHundredthSecond=timeHundredthSecond;
		
	}
	public Position(){
		
	}

	public Position(byte[] data){
		if(data==null || data.length<28){
			return;
		}
		latitudeDegree = data[4];
		latitudeMinute = data[5];
		latitudeSecond = data[6];
		//设置纬度毫秒
		byte[] temp=new byte[2];
		
		System.arraycopy(data, 7, temp, 0, 2);
		latitudeMillisecond = TypeConvert.byte2short(temp);
		latitudeIdentification = data[9];
		
		longitudeDegree = data[10];
		longitudeMinute = data[11];
		longitudeSecond = data[12];
		//设置经度毫秒
		System.arraycopy(data, 13, temp, 0, 2);
		longitudeMillisecond = TypeConvert.byte2short(temp);
		longitudeIdentification = data[15];
		
		temp=new byte[4];
		System.arraycopy(data, 16, temp, 0, 4);
		elevation = TypeConvert.byte2float(temp);
		
		temp=new byte[2];
		System.arraycopy(data, 20, temp, 0, 2);
		timeYear = TypeConvert.byte2short(temp);
		
		timeMonth =data[22];
		timeDay =data[23];
		timeHour =data[24];
		timeMinute =data[25];
		timeSecond =data[26];
		timeHundredthSecond =data[27];
		
	}
	public byte getLatitudeDegree() {
		return latitudeDegree;
	}



	public void setLatitudeDegree(byte latitudeDegree) {
		this.latitudeDegree = latitudeDegree;
	}



	public byte getLatitudeMinute() {
		return latitudeMinute;
	}



	public void setLatitudeMinute(byte latitudeMinute) {
		this.latitudeMinute = latitudeMinute;
	}



	public byte getLatitudeSecond() {
		return latitudeSecond;
	}



	public void setLatitudeSecond(byte latitudeSecond) {
		this.latitudeSecond = latitudeSecond;
	}



	public short getLatitudeMillisecond() {
		return latitudeMillisecond;
	}



	public void setLatitudeMillisecond(byte latitudeMillisecond) {
		this.latitudeMillisecond = latitudeMillisecond;
	}



	public byte getLatitudeIdentification() {
		return latitudeIdentification;
	}



	public void setLatitudeIdentification(byte latitudeIdentification) {
		this.latitudeIdentification = latitudeIdentification;
	}



	public byte getLongitudeDegree() {
		return longitudeDegree;
	}



	public void setLongitudeDegree(byte longitudeDegree) {
		this.longitudeDegree = longitudeDegree;
	}



	public byte getLongitudeMinute() {
		return longitudeMinute;
	}



	public void setLongitudeMinute(byte longitudeMinute) {
		this.longitudeMinute = longitudeMinute;
	}



	public byte getLongitudeSecond() {
		return longitudeSecond;
	}



	public void setLongitudeSecond(byte longitudeSecond) {
		this.longitudeSecond = longitudeSecond;
	}



	public short getLongitudeMillisecond() {
		return longitudeMillisecond;
	}



	public void setLongitudeMillisecond(byte longitudeMillisecond) {
		this.longitudeMillisecond = longitudeMillisecond;
	}



	public byte getLongitudeIdentification() {
		return longitudeIdentification;
	}



	public void setLongitudeIdentification(byte longitudeIdentification) {
		this.longitudeIdentification = longitudeIdentification;
	}



	public float getElevation() {
		return elevation;
	}



	public void setElevation(float elevation) {
		this.elevation = elevation;
	}



	public short getTimeYear() {
		return timeYear;
	}



	public void setTimeYear(short timeYear) {
		this.timeYear = timeYear;
	}



	public byte getTimeMonth() {
		return timeMonth;
	}



	public void setTimeMonth(byte timeMonth) {
		this.timeMonth = timeMonth;
	}



	public byte getTimeDay() {
		return timeDay;
	}



	public void setTimeDay(byte timeDay) {
		this.timeDay = timeDay;
	}



	public byte getTimeHour() {
		return timeHour;
	}



	public void setTimeHour(byte timeHour) {
		this.timeHour = timeHour;
	}



	public byte getTimeMinute() {
		return timeMinute;
	}



	public void setTimeMinute(byte timeMinute) {
		this.timeMinute = timeMinute;
	}



	public byte getTimeSecond() {
		return timeSecond;
	}



	public void setTimeSecond(byte timeSecond) {
		this.timeSecond = timeSecond;
	}



	public byte getTimeHundredthSecond() {
		return timeHundredthSecond;
	}



	public void setTimeHundredthSecond(byte timeHundredthSecond) {
		this.timeHundredthSecond = timeHundredthSecond;
	}
	
	public byte[] getFrame(){
		byte[] frame=new byte[28];
		//未加入报文id
		frame[4] = latitudeDegree;
		frame[5] = latitudeMinute;
		frame[6] = latitudeSecond;
		//设置纬度毫秒
		System.arraycopy(TypeConvert.short2byte(latitudeMillisecond), 0, frame, 7, 2);
		frame[9] = latitudeIdentification;
		
		frame[10] = longitudeDegree;
		frame[11] = longitudeMinute;
		frame[12] = longitudeSecond;
		//设置经度毫秒
		System.arraycopy(TypeConvert.short2byte(longitudeMillisecond), 0, frame, 13, 2);
		frame[15] = longitudeIdentification;
		System.arraycopy(TypeConvert.float2byte(elevation), 0, frame, 16, 4);
		System.arraycopy(TypeConvert.short2byte(timeYear), 0, frame, 20, 2);
		frame[22] = timeMonth;
		frame[23] = timeDay;
		frame[24] = timeHour;
		frame[25] = timeMinute;
		frame[26] = timeSecond;
		frame[27] = timeHundredthSecond;
		return frame;
	}
	
	
	
	
}
