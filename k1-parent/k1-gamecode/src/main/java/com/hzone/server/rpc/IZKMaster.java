package com.hzone.server.rpc;


/**
 * 节点注册为主节点后，会调用该方法。
 * manager实现该接口，表示当节点注册为主节点后，会调用该方法初始化。
 * @author zehong.he
 *
 */
public interface IZKMaster {
	
	public void masterInit(String nodeName);
	
}
