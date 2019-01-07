package com.hzone.constant;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.joda.time.DateTime;

import com.hzone.annotation.ExcelBean;
import com.hzone.manager.common.CacheManager;
import com.hzone.manager.skill.SkillBean;
import com.hzone.server.RedisKey;
import com.hzone.server.instance.InstanceFactory;
import com.hzone.tool.redis.JedisUtil;
import com.hzone.tool.zookeep.ZookeepServer;
import com.hzone.util.ByteUtil;
import com.hzone.util.excel.ExcelConfigBean;
import com.hzone.util.excel.ExcelRead;

/**
 * 配置加载器
 * @author zehong.he
 *
 */
public class CM {
	
    private static final Logger log = LogManager.getLogger(CM.class);

    @ExcelBean(name = "Skill", sheet = "skill", clazz = SkillBean.class)
    public static List<SkillBean> skillBeanList = Collections.emptyList();


    public static void reload() {
        final String readLocalConfig = System.getProperty("x3.excel.local");
        final boolean readZKConfig = (readLocalConfig == null ||
            readLocalConfig.isEmpty()
            || Boolean.parseBoolean(readLocalConfig) == Boolean.FALSE);

        // FIXME 监控excel配置变化，后面可以考虑细化到单个excel文件级别
        Watcher w = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
                    reloadConfig(readZKConfig);
                    manager.resetCache();
                } catch (Exception e) {
                    log.error("reload excel error " + e.getMessage(), e);
                }
                // reloadConfig();
                ZookeepServer.getInstance().exists(ZookeepServer.getConfigPath(), this);
            }
        };

        // 监听配置文件父节点
        ZookeepServer.getInstance().exists(ZookeepServer.getConfigPath(), w);
        reloadConfig(readZKConfig);
    }

    private static void reloadConfig(boolean readZKConfig) {

        // ZookeepServer.getInstance().getBytes(node)

        // // 读取excel配置
        // ExcelConsole.init();
        //
        init(readZKConfig);
    }

    public static void check() {
        // TODO 检查是否有没读取成功的字段；
    }

    /**
     * 调试excel配置文件路径
     */
    public static String debugExcelPath = CM.class.getResource("/").getPath()
        + "../excel";

    static {
        String excelPath = System.getProperty("x3.excel.local.path");
        if (excelPath != null && !excelPath.isEmpty()) {
            debugExcelPath = excelPath;
        }
    }

    /**
     * excel文件名称
     *
     * @param key
     * @param isZKServer 当false时读取本地，方便测试
     * @return
     */
    public static byte[] getExcelByte(boolean isZKServer, String key) {
        //从zk服务器取
        if (isZKServer) {
            return ZookeepServer.getInstance().getBytes(
                ZookeepServer.getConfigPath() + "/" + key);
        }
        //本地配置
        try {
            String filename = getFileName(key);
            log.debug("读本地excel[{}]", filename);
            File file = new File(filename);
            if (!file.exists()) {
                log.error("配置文件不存在{}", filename);
                return new byte[0];
            }
            return ByteUtil.toByteArray(new File(filename));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /** 本地配置 */
    private static String getFileName(String key) {
        return debugExcelPath + File.separatorChar + key + ".xlsx";
    }

    public static void init(boolean readZKConfig) {
        log.info("init excel. zk {} debugpath {} zkpath {} redis {}", readZKConfig, debugExcelPath, ZookeepServer.getConfigPath(), jedisUtil != null);
        Field[] list = CM.class.getDeclaredFields();
        Map<String, byte[]> configSource = new HashMap<>(list.length);

        log.info("excel convert to bean..");
        for (Field field : list) {
            if (!field.isAnnotationPresent(ExcelBean.class)) {
                continue;
            }
            ExcelBean anno = field.getAnnotation(ExcelBean.class);
            try {
                log.trace("读取 excel {} sheet {} 开始 {}", anno.name(), anno.sheet(), DateTime.now());
                ExcelConfigBean ecb;
                if (readZKConfig) {//from zk
                    byte[] fileBytes = configSource.computeIfAbsent(anno.name(), key -> getExcelByte(readZKConfig, key));
                    ecb = ExcelRead.readFile(fileBytes, anno.sheet(), anno.name());
                } else {//from local
                    ecb = readExcelFromLocal(configSource, anno);
                }

                log.trace("读取 excel {} sheet {} 结束 {} ecb {}", anno.name(), anno.sheet(), DateTime.now(), ecb);
                //                if (anno.sheet().equals("BDMoney")){
                //                    System.out.println();
                //                }
                if (ecb == null) {
                    log.error("配置文件不存在{}-{}", anno.name(), anno.sheet());
                    continue;
                }
                readExcel(field, anno, ecb);
            } catch (Exception e) {
                log.error("配置文件读取报错" + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        log.info("excel convert to bean done");
    }

    private static ExcelConfigBean readExcelFromLocal(Map<String, byte[]> configSource, ExcelBean anno) throws Exception {
        boolean readZKConfig = false;
        String filename = getFileName(anno.name());
        File f = new File(filename);
        String cacheKey = RedisKey.Local_Config + anno.sheet() + filename;
        long lastTime = f.lastModified();

        ExcelConfigBean ecb = null;
        JedisUtil jedisUtil = getJedisUtil();
        if (jedisUtil != null) {
            LocalConfigCache lcc = jedisUtil.getObj(cacheKey);
            if (lcc != null && lcc.getLastModifiedTime() == lastTime) {//没有变化, 直接返回缓存的内容, 加速启动
                ecb = lcc.getContent();
            }
        }
        if (ecb == null) {
            byte[] fileBytes = configSource.computeIfAbsent(anno.name(), key -> getExcelByte(readZKConfig, key));
            ecb = ExcelRead.readFile(fileBytes, anno.sheet(), anno.name());
            if (jedisUtil != null) {
                jedisUtil.set(cacheKey, new LocalConfigCache(lastTime, ecb), (int) TimeUnit.DAYS.toSeconds(1));
            }
        }
        return ecb;
    }

    /**
     * 读
     *
     * @param field
     * @param anno
     */
    @SuppressWarnings("rawtypes")
    private static void readExcel(Field field, ExcelBean anno,
                                  ExcelConfigBean bean) {
        // System.err.println("开始初始化"+anno.name()+"文件");
        field.setAccessible(true);
        boolean isNull = false;
        try {
            if (anno.clazz() != Map.class) {
                List list = bean.converToBeanList(anno.clazz());
                if (list == null) {
                    isNull = true;
                }
                field.set(List.class, list);
            } else {
                Map map = null;
                if ("".equals(anno.key()) || "".equals(anno.value())) {
                    map = bean.converToMap();
                } else {
                    map = bean.converToMap(anno.key(), anno.value());
                }
                if (map == null) {
                    isNull = true;
                }
                field.set(Map.class, map);
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } finally {
            //            log.trace(field.getName());
            // System.err.println(field.getName());
        }
        //
        if (isNull) {
            log.error(field.getName() + "[" + anno.name() + "]:读取配置为空！");
        }

    }

    public static void clear() {
        // 加完完后要释放掉；

    }

    private static JedisUtil jedisUtil;

    public static void setJedisUtil(JedisUtil jedisUtil) {
        CM.jedisUtil = jedisUtil;
    }

    private static JedisUtil getJedisUtil() {
        return jedisUtil;
    }

    /** 本地策划配置缓存 */
    public static final class LocalConfigCache implements Serializable {
        private static final long serialVersionUID = 2;
        /** excel 最后修改时间 */
        private long lastModifiedTime;
        /** excel 内容 */
        private ExcelConfigBean content;

        public LocalConfigCache() {
        }

        public LocalConfigCache(long lastModifiedTime, ExcelConfigBean content) {
            this.lastModifiedTime = lastModifiedTime;
            this.content = content;
        }

        public long getLastModifiedTime() {
            return lastModifiedTime;
        }

        public ExcelConfigBean getContent() {
            return content;
        }
    }
}
