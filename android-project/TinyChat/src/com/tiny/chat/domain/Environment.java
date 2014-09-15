package com.tiny.chat.domain;

import java.io.Serializable;

public class Environment implements Serializable {

	private static final long serialVersionUID = 305861263557820638L;
	private int temperature;     //温度
	private int humidity;        //湿度
	private int elevation;       //海拔
	private int airPressure;     //气压
	private int lightIntensity;  //光强
	private int directionAngle;  //方向角
	
	
	public Environment() {
		
	}
	
	public Environment(int temperature,int humidity,int elevation,int airPressure,int lightIntensity,int directionAngle){
		this.temperature=temperature;
		this.humidity=humidity;
		this.elevation=elevation;
		this.airPressure=airPressure;
		this.lightIntensity=lightIntensity;
		this.directionAngle=directionAngle;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getElevation() {
		return elevation;
	}

	public void setElevation(int elevation) {
		this.elevation = elevation;
	}

	public int getAirPressure() {
		return airPressure;
	}

	public void setAirPressure(int airPressure) {
		this.airPressure = airPressure;
	}

	public int getLightIntensity() {
		return lightIntensity;
	}

	public void setLightIntensity(int lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	public int getDirectionAngle() {
		return directionAngle;
	}

	public void setDirectionAngle(int directionAngle) {
		this.directionAngle = directionAngle;
	}
	
}
