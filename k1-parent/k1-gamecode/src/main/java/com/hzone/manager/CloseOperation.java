package com.hzone.manager;

/**
 * 停服操作
 * @author zehong.he
 *
 */
public interface CloseOperation {
	public void close()throws Exception;

	default int getOrder(){
		return 0;
	}
}
