package com.tiny.chat.db.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.tiny.chat.domain.SMSMessage;

/**
 * General DAO 所有dao的基本接口
 * 
 * @author longtao.li
 * 
 * @param <T>
 * @param <ID>
 */
public interface GeneralDAO<T, ID extends Serializable> {

	/**
	 * 根据id从表中查询数据再封装成bean返回
	 * 
	 * @param id
	 * @return T bean or null
	 */
	T findByID(ID id);

	/**
	 * 查找数据库表，将数据封装成实体的并存放到List
	 * @return 存放实体的集合
	 */
	List<T> findAll();

	// 未实现
	// PagingVO getPagingData(int currentPage, int pageSize);

	/**
	 * 将传递的参数实体存入数据库
	 * @param entity
	 * @return
	 */
	T save(T entity);

	/**
	 * 
	 * @param entity
	 */
	void save(T... entity);
	
	
	List<T> save(List<T> entitys);

	/**
	 * 更新数据库表的某一行
	 * @param entity
	 * @return
	 */
	T update(T entity);
	
	/**
	 * 根据最后插入的 id 查找对应的 实体
	 * @return
	 */
	T findByIdLastRow();

	void update(Collection<T> entity);

	void saveOrUpdate(T entity);

	void saveOrUpdate(Collection<T> entities);

	void delete(T entity);

	void delete(ID id);

	void delete(Collection<T> entities);

	void marge(T eneity);

	void marge(T... eneity);

	int getCount();
}
