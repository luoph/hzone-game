package com.hzone.db.conn.dao;

public enum ResourceType {
	DB_data("DB_data"),	//策划配置和运营活动数据库
	DB_game("DB_game"),	//每个分区数据库
	DB_nba("DB_nba"),	//实时球员数据库
	DB_Op_Log("DB_oplog"),	//运营日志数据库(玩家操作日志)

	Jredis("Jredis"),	
	Zookeep("Zookeep"),	
	;
	
    private final String resName;   
    
    ResourceType(String resName) { 
        this.resName = resName;         
    } 

    public String getResName() { 
        return resName; 
    }    
   

}
