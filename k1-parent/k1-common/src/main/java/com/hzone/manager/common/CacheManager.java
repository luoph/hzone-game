package com.hzone.manager.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.hzone.annotation.IOC;
import com.hzone.cfg.base.ValidateBean;
import com.hzone.constant.CM;
import com.hzone.db.ao.common.INBADataAO;
import com.hzone.manager.BaseManager;
import com.hzone.server.GameSource;
import com.hzone.server.ManagerOrder;

/**
 * 缓存配置管理
 * @author zehong.he
 *
 */
public class CacheManager extends BaseManager {
	
    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
    @IOC
    private INBADataAO nbaDataAO;

    public void resetCache() {
        if (GameSource.pool.equals("gm")) {
            return;
        }
        initCache();
        validateCache();
    }

    public void initCache() {
        initCache0();
    }

    /** 客户端需要一个静态方法初始化策划配置. 客户端只需要策划配置裸数据, 不需要 IOC 相关类 */
    public static void initCacheFromClient() {
        initCache0();
    }

    public static void initCache0() {
//        ConfigConsole.init(CM.GLOBAL_CONFIGS); TODO 
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Cache.getOrder();
    }

    @Override
    public void instanceAfter() {
        /**
         * 加载配置
         */
        CM.setJedisUtil(redis);
        if (GameSource.pool.equals("gm")) {
            log.info("==================GM节点不读Excel===================");
            return;
        }
        CM.reload();
        initCache();
        validateCache();
    }

    public static void validateCache() {
        try {
            Class<CM> cmclz = CM.class;
            ImmutableSet<ClassInfo> classes = ClassPath.from(cmclz.getClassLoader())
                .getTopLevelClasses(cmclz.getPackage().getName());
            for (ClassInfo ci : classes) {
                Class<?> clz = ci.load();
                boolean canValidate = false;
                for (Class<?> dclz : clz.getInterfaces()) {
                    if (dclz.isAssignableFrom(ValidateBean.class)) {
                        canValidate = true;
                        break;
                    }
                }
                if (!canValidate) {
                    continue;
                }
                log.info("validate excel console {}", clz.getName());
                ValidateBean vb = (ValidateBean) clz.newInstance();
                vb.validate();

            }
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("validate excel done.");
    }

}
