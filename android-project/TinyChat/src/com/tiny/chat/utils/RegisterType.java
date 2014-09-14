package com.tiny.chat.utils;


public class RegisterType {
	
	public static final byte TEXT = 1;
	public static final byte FILE = 2;
	public static final byte VOICE = 4;
	public static final byte VIDEO = 8;
	public static final byte SENSOR = 16;
	
	private boolean text,file,voice,video,sensor;
	
	public RegisterType(byte type){
		text=((type&RegisterType.TEXT)==RegisterType.TEXT);
		file=((type&RegisterType.FILE)==RegisterType.FILE);
		voice=((type&RegisterType.VOICE)==RegisterType.VOICE);
		video=((type&RegisterType.VIDEO)==RegisterType.VIDEO);
		sensor=((type&RegisterType.SENSOR)==RegisterType.SENSOR);
	}
	public static byte getRegisterType(boolean text,boolean file,boolean voice,boolean video,boolean sensor){
		byte value=0;
		if(text){
			value=(byte)(value | TEXT);
		}
		if(file){
			value=(byte)(value | FILE);
		}
		if(voice){
			value=(byte)(value | VOICE);
		}
		if(video){
			value=(byte)(value | VIDEO);
		}
		if(sensor){
			value=(byte)(value | SENSOR);
		}
		return value;
	}
	public boolean isText() {
		return text;
	}
	public boolean isFile() {
		return file;
	}
	public boolean isVoice() {
		return voice;
	}
	public boolean isVideo() {
		return video;
	}
	public boolean isSensor() {
		return sensor;
	}
	
	
	
}
