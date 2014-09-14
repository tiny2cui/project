package com.tiny.chat.domain;

import java.io.Serializable;


public class DownLoading implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2580017624950347098L;

	private Integer idDownLoad;
	
	private String fileName;
	
	private String path;
	
	private Long downLoadLength;
	
	private Integer isFinish = 0;
	//ftp smb http
	private String type;
	
	private Integer idServer;
    private Integer IdMovie;
	private long totalLength;
	public DownLoading(){}
	
	
	public DownLoading(Integer idDownLoad,String fileName, String path, Long downLoadLength,
			Integer isFinish, String type, Integer idServer, long totalLength,Integer idMovie) {
		super();
		this.idDownLoad = idDownLoad;
		this.fileName = fileName;
		this.path = path;
		this.downLoadLength = downLoadLength;
		this.isFinish = isFinish;
		this.type = type;
		this.idServer = idServer;
		this.totalLength = totalLength;
		this.IdMovie = idMovie;
	}


	public Integer getIdDownLoad() {
		return idDownLoad;
	}

	public void setIdDownLoad(Integer idDownLoad) {
		this.idDownLoad = idDownLoad;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getDownLoadLength() {
		return downLoadLength;
	}

	public void setDownLoadLength(Long downLoadLength) {
		this.downLoadLength = downLoadLength;
	}

	public Integer getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(Integer isFinish) {
		this.isFinish = isFinish;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	public Integer getIdServer() {
		return idServer;
	}


	public void setIdServer(Integer idServer) {
		this.idServer = idServer;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public long getTotalLength() {
		return totalLength;
	}


	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}


	public Integer getIdMovie() {
		return IdMovie;
	}

	public void setIdMovie(Integer idMovie) {
		IdMovie = idMovie;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IdMovie == null) ? 0 : IdMovie.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownLoading other = (DownLoading) obj;
		if (IdMovie == null) {
			if (other.IdMovie != null)
				return false;
		} else if (!IdMovie.equals(other.IdMovie))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
