package com.tiny.chat.db.dao;

import java.util.HashSet;

import com.tiny.chat.domain.DownLoading;

/**
 * 
 *
 * @author longtao.li
 * 2012-9-25
 *
 */
public interface DownLoadingDao extends GeneralDAO<DownLoading,Integer>{
	//如果暂时没想到什么特殊的需求，子类只需实现GeneralDAO基本dao层接口的方法就够了
	
	public void deleteFinish();
	
	public void deleteDownloadForIdServer(Integer id);
	
	public DownLoading getDownloadsForIdMovie(Integer idmovie);
	
	public HashSet<String> getDownPaths();
}
