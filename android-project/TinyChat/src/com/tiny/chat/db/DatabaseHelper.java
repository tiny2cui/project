package com.tiny.chat.db;

import com.tiny.chat.db.table.MessageTable;
import com.tiny.chat.utils.MyLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * This class helps open, create, and upgrade the database file.
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "chat.db";
	
	/**
	 * 单例
	 */
	private static DatabaseHelper mDatabaseHelper;
	 
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	/**
	 * 返回DatabaseHelper实例
	 */
	public static synchronized DatabaseHelper getDatabaseHelper(Context context){
		if(mDatabaseHelper == null){
			mDatabaseHelper = new DatabaseHelper(context);
		}
		return mDatabaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTableMessage(db);
	} 

//	private void createTableDownLoding(SQLiteDatabase db) {
//		String sql =  
//				  "CREATE TABLE " + DownLoadingTable.TABLE_NAME + " ("
//							+ DownLoadingTable.ID_DOWNLOAD + " INTEGER PRIMARY KEY,"
//							+ DownLoadingTable.STR_FILENAME + " TEXT,"
//							+ DownLoadingTable.STR_PATH + " TEXT,"
//							+ DownLoadingTable.DOWNLOAD_LENGTH + " LONG,"
//							+ DownLoadingTable.IS_FINISH + " INTEGER DEFAULT 0,"
//							+ DownLoadingTable.STR_TYPE + " TEXT,"
//							+ DownLoadingTable.ID_SERVER + " INTEGER,"
//							+ DownLoadingTable.TOTAL_LENGTH + " LONG, "
//							+DownLoadingTable.ID_MOVIE+" INTEGER"
//							+ ");";
//		  db.execSQL(sql);
//		
//	}
	
	private void createTableMessage(SQLiteDatabase db) {
		String sql =  
				  "CREATE TABLE " + MessageTable.TABLE_NAME + " ("
							+ MessageTable.ID_MESSAGE + " INTEGER PRIMARY KEY,"
							+ MessageTable.CONVERSATION_ID + " INTEGER,"
							+ MessageTable.SEND_ID + " INTEGER,"
							+ MessageTable.RECEIVE_ID + " INTEGER,"
							+ MessageTable.CONTENT + " TEXT,"
							+ MessageTable.DATE + " TEXT,"
							+ MessageTable.MESSAGE_TYPE + " INTEGER,"
							+MessageTable.MESSAGE_STATE+" INTEGER"
							+ ");";
		  db.execSQL(sql);
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      MyLog.i("db","oldVersion"+oldVersion+"newVersion"+newVersion);
//		 String dropDownload ="DROP TABLE "+DownLoadingTable.TABLE_NAME;
//		 db.execSQL(dropDownload);
//		 String file = "DROP TABLE "+FilesTable.TABLE_NAME;
//		 db.execSQL(file);
//		 String movie = "DROP TABLE "+MovieTable.TABLE_NAME;
//		 db.execSQL(movie);
//		 String gener = "DROP TABLE "+GenreTable.TABLE_NAME;
//		 db.execSQL(gener);
//		 String generLinkMovie = "DROP TABLE "+GenreLinkMovie.TABLE_NAME;
//		 db.execSQL(generLinkMovie);
//		 String actor = "DROP TABLE "+ActorsTable.TABLE_NAME;
//		 db.execSQL(actor);
//		 String actorlinkMovie = "DROP TABLE "+ActorLinkMovieTable.TABLE_NAME;
//		 db.execSQL(actorlinkMovie);
//		 String shareServer = "DROP TABLE "+ShareServerTable.TABLE_NAME;
//		 db.execSQL(shareServer);
//		 String album = "DROP TABLE "+AlbumTable.TABLE_NAME;
//		 db.execSQL(album);
//		 onCreate(db);
//      //数据库版本是3 
//      int Jump=-1;
//      if(oldVersion==1||oldVersion==2){
//    	      String drop ="DROP TABLE "+DownLoadingTable.TABLE_NAME;
//    	      db.execSQL(drop);
//    	      createTableDownLoding(db);
//    	      Jump=3;
//       
//      }
//      //数据库版本是4 	
//      int jumPFour = -1;
//    if(oldVersion==3||Jump==3){
//    	  String add = "ALTER TABLE "+ MovieTable.TABLE_NAME +" ADD "+ MovieTable.LASTTIME +" LONG DEFAULT 0;";
//    	  db.execSQL(add);
//    	  jumPFour=4;
//      }
//    //数据库版本是5	  
//    if(oldVersion==4||jumPFour==4){
//    	 String delete = "DELETE FROM "+ AlbumTable.TABLE_NAME +" WHERE "+ AlbumTable.TYPE_ORIGIN +" = 0;";
//    	 db.execSQL(delete);
//    }
//    
//    if(newVersion == 6){
//        String drop ="DROP TABLE "+DownLoadingTable.TABLE_NAME;
//	      db.execSQL(drop);
//	      createTableDownLoding(db);
//	      String addSubPath = "ALTER TABLE "+ MovieTable.TABLE_NAME +" ADD "+ MovieTable.SUBPATH +" TEXT;";
//    	  db.execSQL(addSubPath);
//    	  String addSubDir = "ALTER TABLE "+ MovieTable.TABLE_NAME +" ADD "+ MovieTable.SUBDIR +" TEXT;";
//    	  db.execSQL(addSubDir);
//    }
	}
}
