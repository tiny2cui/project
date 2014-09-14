package com.tiny.chat.db.dao.imple;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tiny.chat.db.dao.GeneralDAO;
import com.tiny.chat.db.table.MessageTable;
import com.tiny.chat.domain.SMSMessage;
public class MessageDaoImpl extends AbstractSqliteDao<SMSMessage, Integer>
		implements GeneralDAO<SMSMessage, Integer> {

	public MessageDaoImpl(Context context) {
		super(context);
	}

	@Override
	public SMSMessage save(SMSMessage entity) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO message(conversationID,sendID,receiveID,messageType,content,date,messageState) VALUES(?,?,?,?,?,?,?);";
		db.execSQL(sql,new Object[] { entity.getConversationID(),entity.getSourceID(), entity.getDestionID(),
						entity.getMessageType(), entity.getContent(),
						entity.getDate(), entity.getMessageState(),
						 });
		int idMessage = 0;
		Cursor cursor = db.rawQuery("select max(idMessage) from message",null);
		if (cursor != null && cursor.moveToFirst()) {
			idMessage = cursor.getInt(0);
		}
		entity.setIdMessage(idMessage);
		if (cursor != null) {
			cursor.close();
		}
		
		return entity;
	}

	@Override
	public void delete(Integer id) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "DELETE FROM message WHERE idMessage=?;";
		db.execSQL(sql, new Object[] { id });
	}

	

	@Override
	public SMSMessage update(SMSMessage entity) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "UPDATE message SET messageState=? WHERE idMessage=?";
		db.execSQL(sql,new Object[] { entity.getMessageState(),entity.getIdMessage() });
		return entity;
	}

	@Override
	public SMSMessage findByID(Integer id) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM message WHERE idMessage=?;";
		String[] selectionArgs = { id.toString() };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		SMSMessage dl = null;
		if (cursor.moveToFirst()) {
			dl = cursor2Message(cursor);
		}
		if (cursor != null)
			cursor.close();
		return dl;
	}

	
	@Override
	public List<SMSMessage> findAll() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM message ORDER BY idDownLoad ASC;";
		String[] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<SMSMessage> dls = new ArrayList<SMSMessage>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			SMSMessage dl = cursor2Message(cursor);
			dls.add(dl);
		}
		if (cursor != null)
			cursor.close();
		return dls;
	}

	private SMSMessage cursor2Message(Cursor cursor) {
		Integer idMessage = cursor.getInt(cursor
				.getColumnIndex(MessageTable.ID_MESSAGE));
		Integer sendID = cursor.getInt(cursor
				.getColumnIndex(MessageTable.SEND_ID));
		Integer receiveID = cursor.getInt(cursor
				.getColumnIndex(MessageTable.RECEIVE_ID));
		Integer messageType = cursor.getInt(cursor
				.getColumnIndex(MessageTable.MESSAGE_TYPE));
		String content = cursor.getString(cursor
				.getColumnIndex(MessageTable.CONTENT));
		String date = cursor.getString(cursor
				.getColumnIndex(MessageTable.DATE));
		Integer messageState = cursor.getInt(cursor
				.getColumnIndex(MessageTable.MESSAGE_STATE));	
		return new SMSMessage(idMessage, sendID, receiveID, messageType,-1, content,messageState,  date);
		
	}

	@Override
	public SMSMessage findByIdLastRow() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM message WHERE idMessage=last_insert_rowid();";
		String[] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		SMSMessage dl = null;
		if (cursor.moveToFirst()) {
			dl = cursor2Message(cursor);
		}
		if (cursor != null)
			cursor.close();
		return dl;
	}
	
	
	public  List<SMSMessage> findLastMessages() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT Max(idMessage) AS idMessage, sendID,receiveID,messageType,content,date,messageState FROM message GROUP BY conversationID ORDER BY idMessage DESC;";
		String[] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<SMSMessage> dls = new ArrayList<SMSMessage>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			SMSMessage dl = cursor2Message(cursor);
			dls.add(dl);
		}
		if (cursor != null)
			cursor.close();
		return dls;
	}
	
	public  List<SMSMessage> findMessagesByDeviceId(Integer id) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM message WHERE sendID=? OR receiveID=?;";
		String[] selectionArgs = {id.toString(),id.toString()};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<SMSMessage> dls = new ArrayList<SMSMessage>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			SMSMessage dl = cursor2Message(cursor);
			dls.add(dl);
		}
		if (cursor != null)
			cursor.close();
		return dls;
	}
	
	//SELECT * FROM message WHERE sendID=3 OR receiveID=3

}
