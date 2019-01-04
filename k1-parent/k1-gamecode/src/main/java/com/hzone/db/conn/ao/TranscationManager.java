package com.hzone.db.conn.ao;

import com.hzone.db.conn.dao.ConnectionDAO;



/**
 * 功能：事物管理
 * @author zehong.he
 *
 */
public class TranscationManager {
	/**
	 * 事物COMMIT
	 */
	public void afterTransaction(){
		ConnectionDAO.commit();
	}
	/**
	 * 开起事物
	 * @param method
	 */
	public void beforeTransaction(){
		ConnectionDAO.startTransaction();
	}
	/**
	 * 结束事物
	 * @param method
	 */
	public void endTransaction(){
		ConnectionDAO.endTransaction();
	}
	/**
	 * 错误回滚
	 * @param method
	 */
	public void onError(){
		ConnectionDAO.rollback();
	}
}
