package com.tiny.chat.socket;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tiny.chat.BaseApplication;
import com.tiny.chat.db.DBOperate;
import com.tiny.chat.domain.FramePacket;
import com.tiny.chat.domain.SMSMessage;
import com.tiny.chat.utils.ChatMessage;
import com.tiny.chat.utils.DateUtils;
import com.tiny.chat.utils.FileUtils;
import com.tiny.chat.utils.MyLog;
import com.tiny.chat.utils.TypeConvert;

public class FileReceiveDispose {
	private static int messageID;
	// private static int percent;
	private static int receiveFileSerial = -1;
	private static String receiveFileName;
	private static FileOutputStream outputStream;
    private static File file;
    private static SMSMessage message;
	// private static int SIZE=1024;
	// private byte[] buffer=new byte[SIZE];
	public FileReceiveDispose() {

	}

	public static void saveFile(FramePacket framePacket,DBOperate dbOperate) {
		int serial = framePacket.getFrameSerial();
		byte[] data = framePacket.getFramePacket();
		byte[] idBytes = new byte[4];
		System.arraycopy(data, 11, idBytes, 0, 4);
		int id = TypeConvert.byte2int(idBytes);
		if (serial == 0) {
			// 传入文件名
			MyLog.i("file-->receive1", "file--" + serial);
			receiveFileSerial = serial;
			messageID = id;
			if (data.length > 4) {

				receiveFileName = new String(data, 15, data.length-16);
				String dir = BaseApplication.THUMBNAIL_PATH+ framePacket.getSourceID();
				FileUtils.createDirFile(dir);
				String receiveFilePath = dir + File.separator + receiveFileName;
				file = FileUtils.createNewFile(receiveFilePath);
				if(!file.exists()){
					return;
				}
				try {
					outputStream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				//存储进数据库
				message = new SMSMessage(framePacket, 0, DateUtils.getSimpleTime());
				message = dbOperate.saveMessage(message);
				
				
			}
		} 
		
		else if (serial == -1) {
			MyLog.i("file-->receive3", "file--" + serial);
			// 文件发送结束
			if (outputStream != null) {
				try {
					ChatMessage chatMessage = new ChatMessage(ChatMessage.MESSAGE_TEXT_DATA,
							message);
					BaseApplication.getInstance().handleMessage(chatMessage);
					outputStream.close();
					outputStream = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			if(file!=null){
//				try {
//					//InputStream input=new FileInputStream(file);
//					
//					Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());//;.decodeStream(input);
//					OutputStream output=new FileOutputStream(new File(FileUtils.getSDPath() + "/tinyChat/test.jpg"));
//					bitmap.compress(CompressFormat.JPEG, 100, output);
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
		}
		else{
//		else if (serial !=-1 && (receiveFileSerial == serial - 1 || (serial == -128 && receiveFileSerial == 127) || serial == 1 && receiveFileSerial == -2 )) {
			// 接收文件
			MyLog.i("file-->receive2", "file--" + serial);
			receiveFileSerial = serial;
//			if (outputStream != null && messageID == id) {
			if (outputStream != null) {
				
				try {
					//MyLog.i("file-->receive", "file--" + serial + "-->"+ new String(data).toString());
					outputStream.write(data, 15, data.length - 16);
					outputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} 
		
//		else {
//			// 文件接收结束或失败
//			MyLog.i("file-->receive", "file--" + serial);
//			receiveFileSerial = -1;
//			
//			if (outputStream != null) {
//				try {
//					outputStream.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				outputStream = null;
//			}
//		}
		

	}

}
