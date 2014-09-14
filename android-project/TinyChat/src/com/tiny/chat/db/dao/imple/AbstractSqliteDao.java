package com.tiny.chat.db.dao.imple;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.tiny.chat.db.DatabaseHelper;
import com.tiny.chat.domain.SMSMessage;

import android.content.Context;
/**
 * 基于 Sqlite 的Dao,所有dao的实现都必须继承此抽象类
 * @author longtao.li
 *
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractSqliteDao<T, ID extends Serializable> extends AbstractGeneralDAOImpl<T, ID> {

	   DatabaseHelper mOpenHelper ;
	   public AbstractSqliteDao(Context context){
		   mOpenHelper = DatabaseHelper.getDatabaseHelper(context);
	   }
	@Override
	public T findByID(ID id) {
		return null;
	}
	@Override
	public List<T> findAll() {
		return null;
	}
	@Override
	public T save(T entity) {
		return null;
	}
	@Override
	public void save(T... entity) {
	}
	@Override
	public List<T> save(List<T> entitys) {
		return null;
	}
	@Override
	public T update(T entity) {
		return null;
	}
	@Override
	public T findByIdLastRow() {
		return null;
	}
	@Override
	public void update(Collection<T> entity) {
		
	}
	@Override
	public void saveOrUpdate(T entity) {
		
	}
	@Override
	public void saveOrUpdate(Collection<T> entities) {
		
	}
	@Override
	public void delete(T entity) {
		
	}
	@Override
	public void delete(ID id) {
		
	}
	@Override
	public void delete(Collection<T> entities) {
		
	}
	@Override
	public void marge(T eneity) {
		
	}
	@Override
	public void marge(T... eneity) {
	}
	@Override
	public int getCount() {
		return 0;
	}
	   
	   
}
