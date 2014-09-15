package com.speex;

public class Speex {
	 /** quality 解压缩编码的质量
     * 1 : 4kbps (very noticeable artifacts, usually intelligible) 
     * 2 : 6kbps (very noticeable artifacts, good intelligibility) 
     * 4 : 8kbps (noticeable artifacts sometimes) 
     * 6 : 11kpbs (artifacts usually only noticeable with headphones) 
     * 8 : 15kbps (artifacts not usually noticeable) 
     */  
	public static final int DEFAULT_COMPRESSION = 8; 
    /**
     * modeID 不用的窄宽模式对应不同的编码状态：编码的framesize
     * 0:modeID for the defined narrowband mode
     * 1:modeID for the defined wideband mode
     * 2:modeID for the defined ultra-wideband mode
     */
    public static final int speexModeId = 0;

	static {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static native int open(int compression, int speexModeId);

	public static native int getFrameSize();

	public static native int decode(byte encoded[], short lin[], int size);

	public static native int encode(short lin[], int offset, byte encoded[], int size);

	public static native void close();
}
