package com.tiny.chat.db.table;
/**
 * 记录正在下载的文件信息
 * @author longtao.li
 *
 */
public class DownLoadingTable {

	private DownLoadingTable(){
		
	}
	
	/**
 	 * table name
 	 * <P>
 	 * Type: TEXT
 	 * </P>
 	 */
 	public static final String TABLE_NAME = "downloading";
     
       /**
		 * Primary Key
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String ID_DOWNLOAD = "idDownLoad";
		
		/**
		 * file name
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String STR_FILENAME = "strFileName";
		
		/**
		 * file absolute path
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String STR_PATH = "strPath";
		
		/**
		 * length of download
		 * <P>
		 * Type: Long
		 * </P>
		 */
		public static final String DOWNLOAD_LENGTH = "downloadLength";
		
		/**
		 * finish?
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String IS_FINISH = "isFinish";
		
		
		
		/**
		 * type // FilesTable中查看对应类型
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String STR_TYPE = "strType";
		
		/**
		 * idServer 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String ID_SERVER = "idServer";
		
		
		public static final String ID_MOVIE="idMovie";
		
		/**
		 * total length
		 * <P>
		 * Type: Long
		 * </P>
		 */
		public static final String TOTAL_LENGTH = "totalLength";
}
