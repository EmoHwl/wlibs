package com.wlib.core.database;

public class SQLTable {
	private static String createStr = "create table ";
	private static String createTable(String table) {
		return createStr + table; 
	}
	/**
	 * 消息对象 SQL
	 * @author admin
	 *
	 */
	public static class SQLMessage{
		public static String tableName = "message";
		public static String message_table_sql = createTable(tableName)+
				" (id integer primary key,uid integer default -1 ,type integer,"
				+ "content text not null default ''"
				+ ",title text not null default '',pushTime integer default 0,jumpTarget text default '',isRead integer default 0)";
	}
}
