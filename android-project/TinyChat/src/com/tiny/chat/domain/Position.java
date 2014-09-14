package com.tiny.chat.domain;

import java.io.Serializable;

public class Position implements Serializable {
	
	private static final long serialVersionUID = 5499336898003799975L;
	private byte latitudeDegree;            //纬度_度
	private byte latitudeMinute;            //纬度_分
	private byte latitudeSecond;            //纬度_秒
	private byte latitudeMillisecond;       //纬度_毫秒
	private byte latitudeIdentification;    //纬度_标识
	private byte longitudeDegree;           //经度_度
	private byte longitudeMinute;           //经度_分
	private byte longitudeSecond;           //经度_秒
	private byte longitudeMillisecond;      //经度_毫秒
	private byte longitudeIdentification;   //经度_标识
	private float elevation;                //高程
	private short timeYear;                 //年
	private byte timeMonth;                 //月
	private byte timeDay;                   //日
	private byte timeHour;                  //时
	private byte timeMinute;                //分
	private byte timeSecond;                //秒
	private byte timeHundredthSecond;       //百分之一秒
	
	
	public Position() {
		
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



	public byte getLatitudeMillisecond() {
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



	public byte getLongitudeMillisecond() {
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
	
	
	
	
}
