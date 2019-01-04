load("nashorn:mozilla_compat.js");
importPackage(com.ftkj.db.conn.dao);
importPackage(com.ftkj.tool.redis);
importPackage(com.ftkj.tool.zookeep);
//importClass(com.ftkj.db.conn.dao);
//importClass(com.ftkj.redis);
$.setShardid(101);
$.setCharset("zh");
$.setZKPath("/zgame");
$.setPool("logic");
$.setServerName("logic-181");
$.setServerDate("2018-01-01");
//StartupContextImpl

//---zookeep------------------------------------------- 
zk = new ZookeepConfig();
zk.setIpPort("192.168.10.70:2181");
zk.setUser("tim");
zk.setPwd("xgame2016");

//---redis--------------------------------------------
j = new Jredis();
j.setHost("192.168.10.181");
j.setPort(6379);
j.setDatabase(0);
j.setPassword("zgame2017");
j.setConnectionCount(32);

$.addResource(ResourceType.Jredis,new JedisUtil(j));

/*
jdbc_game= new Jdbc();
jdbc_game.setUsername("root");
jdbc_game.setPassword("123456");
jdbc_game.setDriver("com.mysql.jdbc.Driver");
jdbc_game.setUrl("jdbc:mysql://192.168.10.69:3306/x_game_101?useUnicode=true&characterEncoding=utf8");
jdbc_game.setMaxStatements(1000);
*/

jdbc_game= new Jdbc();
jdbc_game.setUsername("root");
jdbc_game.setPassword("zgame2017");
jdbc_game.setDriver("com.mysql.jdbc.Driver");
jdbc_game.setUrl("jdbc:mysql://192.168.10.181:3306/nba_101?useUnicode=true&characterEncoding=utf8mb4");
jdbc_game.setStatementsCacheSize(400);

jdbc_data= new Jdbc();
jdbc_data.setUsername("root");
jdbc_data.setPassword("zgame2017");
jdbc_data.setDriver("com.mysql.jdbc.Driver");
jdbc_data.setUrl("jdbc:mysql://192.168.10.181:3306/nba_data?useUnicode=true&characterEncoding=utf8mb4");
jdbc_data.setStatementsCacheSize(400);

// jdbc_nba= new Jdbc();
// jdbc_nba.setUsername("root");
// jdbc_nba.setPassword("zgame2017");
// jdbc_nba.setDriver("com.mysql.jdbc.Driver");
// jdbc_nba.setUrl("jdbc:mysql://192.168.10.181:3306/nba_data?useUnicode=true&characterEncoding=utf8");
// jdbc_nba.setStatementsCacheSize(400);

jdbc_oplog = new Jdbc();
jdbc_oplog.setUsername("root");
jdbc_oplog.setPassword("zgame2017");
jdbc_oplog.setDriver("com.mysql.jdbc.Driver");
jdbc_oplog.setUrl("jdbc:mysql://192.168.10.181:3306/op_log_101?useUnicode=true&characterEncoding=utf8mb4");
jdbc_oplog.setStatementsCacheSize(400);

nba_ds = new Database(jdbc_data);

$.addResource(ResourceType.DB_nba, nba_ds);
$.addResource(ResourceType.DB_data, nba_ds);
$.addResource(ResourceType.DB_game, new Database(jdbc_game));
$.addResource(ResourceType.DB_Op_Log, new Database(jdbc_oplog));
$.addResource(ResourceType.Zookeep, zk);
