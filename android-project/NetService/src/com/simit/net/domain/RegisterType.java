package com.simit.net.domain;


/*
 * 传输数据类型
 */

public class RegisterType {
	public static final short TEXT = 1;
	public static final short FILE = 2;
	public static final short VOICE = 4;
	public static final short VIDEO = 8;
	public static final short SENSOR = 16;

	private byte registerType;
	
	public RegisterType(){
		
	}
	
	public void SetRegisterType(byte registerType){
		this.registerType = registerType;
	}
	
	public byte GetRegisterType(){
		return registerType;
	}
}
