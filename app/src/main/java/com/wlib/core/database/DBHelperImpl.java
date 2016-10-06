package com.wlib.core.database;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.wlib.core.database.SQLTable.SQLMessage;
import com.wlib.core.database.model.DbMessage;

public class DBHelperImpl extends DBHelper{
	private static ArrayList<Integer> dbMessageIds;
	public DBHelperImpl(Context context) {
		super(context);
		openDb();
	}
	
	/**
	 * 获取已存id
	 * @return isReacquire 是否重新取
	 */
	public ArrayList<Integer> getDbMessageIds() {
		return getDbMessageIds(false);
	}
	
	/**
	 * 获取已存id
	 * @return isReacquire 是否重新取
	 */
	public ArrayList<Integer> getDbMessageIds(boolean isReacquire) {
		if (dbMessageIds == null||isReacquire) {
			if (dbMessageIds==null) {
				dbMessageIds = new ArrayList<Integer>();
			}
			getMessageIdList();
		}
		return dbMessageIds;
	}
	
	
	/**
	 * 添加一个消息
	 * @param dbMessage
	 */
	public void insertMessageRec(DbMessage dbMessage) {
		insertRec(dbMessage);
	}
	
	/**
	 * 根据id
	 * 删除消息记录
	 * @param ids
	 */
	public void delMessageRec(ArrayList<Integer> ids) {
		delRecByIds(SQLMessage.tableName, ids);
	}
	
	/**
	 * 更新记录
	 * @param map
	 * @param ids
	 */
	public void updateMessageRec(Map<String, Object> map,ArrayList<Integer> ids) {
		updateRecByIds(SQLMessage.tableName, map, ids);
	}
	
	/**
	 * 获取消息列表id
	 * @param ids
	 * @return
	 */
	private ArrayList<Integer> getMessageIdList() {
		dbMessageIds.clear();
		Cursor cursor  = queryRecId(SQLMessage.tableName,true);
		if (cursor!=null) {
			Log.i("getMessageIdList", "cursor.getCount()"+cursor.getCount());
			try {
				while (cursor.moveToNext()) {
					int id = cursor.getInt(0);
					dbMessageIds.add(id);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				cursor.close();
			}
		}
		return dbMessageIds;
	}
	
	/**
	 * 获取消息列表最大id
	 * @param ids
	 * @return
	 */
	public int getMessageMaxId() {
		return queryRecMaxId(SQLMessage.tableName);
	}
	
	public ArrayList<DbMessage> getAllMessageList() {
		return getMessageList(null);
	}
	
	/**
	 * 获取消息列表
	 * @param ids
	 * @return
	 */
	public ArrayList<DbMessage> getMessageList(ArrayList<Integer> ids) {
		ArrayList<DbMessage> messages = new ArrayList<DbMessage>();
		Cursor cursor  = queryRec(SQLMessage.tableName, ids,true);
		if (cursor!=null) {
			Log.i("getMessageList", "cursor.getCount()"+cursor.getCount());
			try {
			while (cursor.moveToNext()) {
				DbMessage dbMessage = new DbMessage();
					dbMessage = (DbMessage) setValues2Fields(cursor, dbMessage.getClass());
				if (dbMessage!=null) {
					messages.add(dbMessage);
				}
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				cursor.close();
			}
		}
		return messages;
	}
	
	/**
	 * 根据id查询消息记录
	 * @param id
	 * @return
	 */
	public DbMessage queryMsgRecBYId(int id) {
		Cursor cursor = queryRecBYId(SQLMessage.tableName, id);
		try {
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					DbMessage dbMessage = new DbMessage();
					dbMessage = (DbMessage) setValues2Fields(cursor, DbMessage.class);
					return dbMessage;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
