package com.tiny.chat.db;


import java.util.List;

import com.tiny.chat.db.dao.imple.MessageDaoImpl;
import com.tiny.chat.domain.SMSMessage;
import android.content.Context;

public class DBOperate {
	private static DBOperate instance;
	
	public static DBOperate getInstance(Context context){
		if(instance==null){
			instance=new DBOperate(context);
		}
		return instance;
	}
	private MessageDaoImpl messageDao;
	
	public DBOperate(Context context) {
		messageDao=new MessageDaoImpl(context);
	}
	
	public SMSMessage saveMessage(SMSMessage entity){
		return messageDao.save(entity);
	}
	
	public void updateMessage(SMSMessage entity){
		messageDao.update(entity);
	}
	
	public List<SMSMessage> getLastMessages(){
		return messageDao.findLastMessages();
	}
	
	public List<SMSMessage> getMessagesByDeviceId(Integer id){
		return messageDao.findMessagesByDeviceId(id);
	}
	
	public SMSMessage getMessageById(Integer id){
		return messageDao.findByID(id);
	}
	
	
	
}
