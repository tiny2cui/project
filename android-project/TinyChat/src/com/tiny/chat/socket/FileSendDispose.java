package com.tiny.chat.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.utils.MyLog;
import com.tiny.chat.utils.TypeConvert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;

public class FileSendDispose {
	private Handler mHandler;
	private static int SIZE = 1024;
	private byte[] buffer = new byte[SIZE];
	public static FileSendDispose instance;
	public static FileSendDispose getInstance(){
		if(instance==null){
			instance=new FileSendDispose();
		}
		return instance;
	}
	public FileSendDispose() {
		HandlerThread handlerThread = new HandlerThread("sendFile");
		handlerThread.start();
		mHandler = new Handler(handlerThread.getLooper());
		
	}

	public void postFilePath(final String path, final int sourceId,final int destineId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				sendFile(path, sourceId, destineId);
			}
		});
	}

	private synchronized void sendFile(String path, int sourceId, int destineId) {
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		int messageId = (int) System.currentTimeMillis();
		byte[] idBytes = TypeConvert.int2byte(messageId);
		System.arraycopy(idBytes, 0, buffer, 0, 4);
		int size = -1;
		InputStream inputStream = null;
//		OutputStream outputStream=null;
		byte[] pathBytes=file.getName().getBytes();
		System.arraycopy(pathBytes, 0, buffer, 4, pathBytes.length);
		FramePacket framePacket = new FramePacket(sourceId, destineId,
				FrameType.INFO_FILE, buffer, pathBytes.length+4);
		UDPSocketService.getInstance().postMessage(framePacket.getFramePacket(),framePacket.getFramePacket().length);
		try {
			inputStream = new FileInputStream(file);
//			outputStream = new FileOutputStream(new File("sdcard/test.ppt"));
			int i=1;
			while ((size = inputStream.read(buffer, 4, SIZE - 4)) != -1) {
				if(i==255){
					i=1;
				}
				framePacket = new FramePacket(sourceId, destineId,FrameType.INFO_FILE, buffer, size);
				framePacket.setFrameType(FrameType.INFO_FILE);
				framePacket.setFrameSourceID(sourceId);
				framePacket.setFrameDestineID(destineId);
				framePacket.setFrameData(buffer, size+4);
				framePacket.setFrameSerialNo(i);
				framePacket.packageItemsToFrame();
				MyLog.i("file-->send", "file--"+i+"-->len-"+size);//+new String(framePacket.getFramePacket()).toString());
				UDPSocketService.getInstance().postMessage(framePacket.getFramePacket(),framePacket.getFramePacket().length);
//				outputStream.write(buffer, 4, size);
//				outputStream.flush();
				i++;
			}
			framePacket = new FramePacket();
			framePacket.setFrameType(FrameType.INFO_FILE);
			framePacket.setFrameSourceID(sourceId);
			framePacket.setFrameDestineID(destineId);
			framePacket.setFrameData(buffer, 10);
			framePacket.setFrameSerialNo(255);
			framePacket.packageItemsToFrame();
			MyLog.i("file-->send", "file--"+255+"-->len-"+size+new String(framePacket.getFramePacket()).toString());
			UDPSocketService.getInstance().postMessage(framePacket.getFramePacket(),framePacket.getFramePacket().length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
//					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	

}
