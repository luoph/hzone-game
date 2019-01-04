package com.hzone.tool.quartz;

import com.hzone.manager.BaseManager;
import com.hzone.server.instance.InstanceFactory;

import org.quartz.Job;

/**
 * 任务调度父类
 * @author zehong.he
 *
 */
public abstract class BaseJob implements Job{

	protected <T extends BaseManager> T getManager(Class<T> cla){
		return InstanceFactory.get().getInstance(cla);
	}


    protected void catchException(Exception e) {
    }

    @FunctionalInterface
    protected interface MaybeExceptionTask {
        void execute();
    }

    protected void executeCatchException(MaybeExceptionTask task) {
        try {
            task.execute();
        } catch (Exception e) {
            catchException(e);
        }
    }
}
