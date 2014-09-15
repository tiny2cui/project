package com.simit.net.utils;

public class TypeConvert {

	public static byte[] int2byte(int i){
		byte[] b = new byte[4];
		
		b[0] = (byte)(i >> 24);
		b[1] = (byte)(i >> 16);
		b[2] = (byte)(i >> 8);
		b[3] = (byte)(i);
		
		return b;
	}
	
	public static int byte2int(byte[] b){
		byte[] a = new byte[4];
		int i = a.length - 1, j = b.length - 1;
		
		for(; i >= 0; i--, j--){
			if(j >= 0){
				a[i] = b[i];
			} else {
				a[i] = 0; 
			}
		}
		
		int r0 = (a[0] & 0xFF) << 24;
		int r1 = (a[1] & 0xFF) << 16;
		int r2 = (a[2] & 0xFF) << 8;
		int r3 = (a[3] & 0xFF);
		
		return (r0 + r1 + r2 + r3);
	}

	public static byte[] short2byte(short s){
		byte[] b = new byte[2];
		
		b[0] = (byte)(s >> 8);
		b[1] = (byte)(s);
		
		return b;
	}
	
	public static short byte2short(byte[] b){
		
//		byte[] a = new byte[2];
//		
//		int i = 2, j = b.length - 1;
//		
//		for(; i >= 0; i--, j--) {
//			if(j >= 0) {
//				a[i] = b[j]; 
//			} else {
//				a[i] = 0; 
//			}
//		}
		
		short r0 = (short) ((b[0] & 0xFF) << 8);
		short r1 = (short) (b[1] & 0xFF); 

		return (short)(r0 + r1); 
	}
	
	public static String bytes2String(byte[] data){
		StringBuffer bf=new StringBuffer();
		if(data!=null && data.length>0){
			for(int i=0;i<data.length;i++){
				bf.append((short)(data[i])+",");
			}
			return bf.toString();
		}
		return "";
		
	}
}
