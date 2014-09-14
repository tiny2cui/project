package com.tiny.chat.db.dao.imple;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import com.tiny.chat.db.dao.GeneralDAO;
import com.tiny.chat.utils.MyLog;
/**
 * GeneralDAO的抽象实现
 *
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractGeneralDAOImpl<T, ID extends Serializable> implements GeneralDAO<T, ID>{

	private static final String TAG = "AbstractGeneralDAOImpl";

	protected final Class<T> persistenceClass ;
	
	protected final Class<T> pkClass ;
	
	@SuppressWarnings("unchecked")
	protected AbstractGeneralDAOImpl(){
		
		this.persistenceClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];//T.class
		this.pkClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		
		MyLog.i(TAG,"解析泛型参数 T : " + getPersistenceClass().getName());
	}

	protected final  Class<T> getPersistenceClass() {
		return persistenceClass;
	}
	
	protected final Class<T> getPkClass() {
		return pkClass;
	}
	
}
