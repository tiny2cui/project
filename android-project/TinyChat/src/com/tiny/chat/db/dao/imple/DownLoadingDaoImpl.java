package com.tiny.chat.db.dao.imple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.tiny.chat.db.dao.DownLoadingDao;
import com.tiny.chat.db.table.DownLoadingTable;
import com.tiny.chat.domain.DownLoading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 
 *
 * @author longtao.li
 * 2012-9-25
 *
 */
public class DownLoadingDaoImpl extends AbstractSqliteDao<DownLoading, Integer> implements
		DownLoadingDao {

	public DownLoadingDaoImpl(Context context) {
		super(context);
	}

	@Override
	public DownLoading save(DownLoading entity) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO downloading(strFileName,strPath,downloadLength,isFinish,strType,idServer,totalLength,idMovie) VALUES(?,?,?,?,?,?,?,?);";
		db.execSQL(sql, new Object[]{ entity.getFileName(), entity.getPath()
				, entity.getDownLoadLength(), entity.getIsFinish(), entity.getType(), entity.getIdServer(),entity.getTotalLength(),entity.getIdMovie()});
//		return findByIdLastRow();
		int idDownLoad =0;
		Cursor cursor = db.rawQuery("select max(idDownLoad) from downloading", null);
		if(cursor!=null&&cursor.moveToFirst()){
			idDownLoad = cursor.getInt(0);
		}
		entity.setIdDownLoad(idDownLoad);
		if(cursor!=null){
			cursor.close();
		}
//		MyLog.i("yanbo test", idDownLoad+"idDownLoad");
		return entity;
	}
	
	@Override
	public void delete(Integer id) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "DELETE FROM downloading WHERE idDownLoad=?;";
		db.execSQL(sql, new Object[] { id });  
	}
    @Override
	public void deleteDownloadForIdServer(Integer id) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "DELETE FROM downloading WHERE idServer=?;";
		db.execSQL(sql, new Object[] { id });  
	}

	@Override
    public void deleteFinish() {
    	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String sql = "DELETE FROM downloading WHERE isFinish=1;";
		db.execSQL(sql, new Object[] {  });  
    }
    
	@Override
	public DownLoading update(DownLoading entity) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase(); 
		String sql = "UPDATE downloading SET downloadLength=?,isFinish=?,totalLength =? WHERE idDownLoad=?";
		db.execSQL(sql, new Object[]{ entity.getDownLoadLength(), entity.getIsFinish() ,entity.getTotalLength(), entity.getIdDownLoad()});
		return entity;
	}
	
	@Override
	public DownLoading findByID(Integer id) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM downloading WHERE idDownLoad=?;";
		String [] selectionArgs = {id.toString()};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		DownLoading dl = null;
		if(cursor.moveToFirst()){
			dl = cursor2DownLoading(cursor);
		}
		if(cursor!=null)
			cursor.close();
		return dl;
	}
	@Override
	public  DownLoading getDownloadsForIdMovie(Integer idmovie) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM downloading WHERE idMovie=?;";
		String [] selectionArgs = {idmovie.toString()};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		DownLoading dl = null;
		if(cursor.moveToFirst()){
			dl = cursor2DownLoading(cursor);
		}
		if(cursor!=null)
			cursor.close();
		return dl;
	}

	@Override
	public List<DownLoading> findAll() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM downloading ORDER BY idDownLoad ASC;";
		String [] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<DownLoading> dls = new ArrayList<DownLoading>();
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			DownLoading dl = cursor2DownLoading(cursor);
			dls.add(dl);
		}
		if(cursor!=null)
			cursor.close();
		return dls;
	}

	private DownLoading cursor2DownLoading(Cursor cursor) {
		Integer idDownLoad = cursor.getInt(cursor.getColumnIndex(DownLoadingTable.ID_DOWNLOAD));
		String fileName = cursor.getString(cursor.getColumnIndex(DownLoadingTable.STR_FILENAME));
		String path = cursor.getString(cursor.getColumnIndex(DownLoadingTable.STR_PATH));
		long length = cursor.getLong(cursor.getColumnIndex(DownLoadingTable.DOWNLOAD_LENGTH));
		Integer isFinish = cursor.getInt(cursor.getColumnIndex(DownLoadingTable.IS_FINISH));
		String type = cursor.getString(cursor.getColumnIndex(DownLoadingTable.STR_TYPE));
		Integer idServer = cursor.getInt(cursor.getColumnIndex(DownLoadingTable.ID_SERVER));
		long total = cursor.getLong(cursor.getColumnIndex(DownLoadingTable.TOTAL_LENGTH));
		Integer idMovie = cursor.getInt(cursor.getColumnIndex(DownLoadingTable.ID_MOVIE));
		return new DownLoading(idDownLoad, fileName, path, length, isFinish, type, idServer, total,idMovie);
	}
	
	@Override
	public DownLoading findByIdLastRow() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT * FROM downloading WHERE idDownLoad=last_insert_rowid();";
		String [] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		DownLoading dl = null;
		if(cursor.moveToFirst()){
			dl = cursor2DownLoading(cursor);
		}
		if(cursor!=null)
			cursor.close();
		return dl;
	}

	@Override
	public HashSet<String> getDownPaths() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sql = "SELECT strPath FROM downloading ORDER BY idDownLoad ASC;";
		String [] selectionArgs = {};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		HashSet<String> dls = new HashSet<String>();
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			String path = cursor.getString(cursor.getColumnIndex(DownLoadingTable.STR_PATH));
			dls.add(path);
		}
		if(cursor!=null)
			cursor.close();
		return dls;
	}
	
	
}
