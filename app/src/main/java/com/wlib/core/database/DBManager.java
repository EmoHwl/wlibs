package com.wlib.core.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.wlib.core.database.SQLTable.SQLMessage;
import com.wlib.core.database.model.DbMessage;
import com.wlib.q.file.SpUtils;
import com.wlib.q.utils.ArrayUtils;

/**
 * 数据库出口
 * @author huangweiliang
 *
 */
public class DBManager {
	private static DBHelperImpl dbHelperImpl;
	private SpUtils mySpUtils;
	public DBManager(Context context) {
		dbHelperImpl = new DBHelperImpl(context);
		mySpUtils = SpUtils.getInstance();
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (dbHelperImpl!=null) {
			dbHelperImpl.closeDb();
		}
			
	}
	
	public DBHelperImpl getDbHelperImpl() {
		return dbHelperImpl;
	}
	
	/**
	 * 在内存保存最大消息最大id
	 */
	private void saveMaxId() {
		int maxId = dbHelperImpl.getMessageMaxId();
		int cacheMaxId = mySpUtils.getMsgMaxId();
		if (cacheMaxId < maxId) {
			mySpUtils.setMsgMaxId(maxId);
		} 
	}
	
	/** 
	 * 检查更新消息表，删除超过30条
	 * @param dbMessages
	 * @param flag 0删除本地未能匹配id,非0不删除 
	 */
	public void checkMessage(ArrayList<DbMessage> dbMessages,int flag) {
		ArrayList<Integer> saveIds = new ArrayList<Integer>();//已存消息列表
		ArrayList<Integer> localIds = dbHelperImpl.getDbMessageIds();
		for (DbMessage dbMessage : dbMessages) {
			int mId = dbMessage.getId();
			if (localIds.contains(mId)) {//已存消息
				if (flag == 0) {
					saveIds.add(mId);
				}
			}else {//新增消息
				dbHelperImpl.insertMessageRec(dbMessage);
			}
		}
		
		if (!ArrayUtils.emptyList(saveIds)) {//需删除消息
			localIds.removeAll(saveIds);
			if (!localIds.isEmpty()) {
				dbHelperImpl.delMessageRec(localIds);
			}
		}
		
		localIds =  dbHelperImpl.getDbMessageIds(true);//重新保存id
		if (!ArrayUtils.emptyList(localIds)) {
			if (localIds.size() > 30) {
				for (int i = 30; i < localIds.size(); i++) {
					delDbMessage(localIds.get(i));
				}
			}
		}
		saveMaxId();
	}
	

	/**
	 * 获取所有消息列表
	 */
	public ArrayList<DbMessage> getAllMessage() {
		ArrayList<DbMessage> dbMessages = new ArrayList<DbMessage>();
		dbMessages.addAll(dbHelperImpl.getAllMessageList());
		return dbMessages;
	}
	
	/**
	 * 更新已读未读
	 * @param map
	 * @param ids
	 */
	public void updateDbMessageIsRead(int id) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isRead", 1);
		map.put("uid", mySpUtils.getUid());
		dbHelperImpl.updateMessageRec(map, ids);
	}
	
	/**
	 * 删除消息
	 * @param id
	 */
	public void delDbMessage(int id) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		dbHelperImpl.delMessageRec(ids);
	}
	
	/**
	 * 获取未读数量
	 * @param isRead = 0
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public int getMessageNumById() {
		Map<String, Map<Integer, Object>> mapMap = new HashMap<String, Map<Integer,Object>>();
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, 0);
		mapMap.put("isRead", map);
		int num = dbHelperImpl.getRecNum(SQLMessage.tableName, mapMap);
		return num >30?30:num;
	}
	
	/**
	 * 获取总数量
	 * @param isRead = 0
	 * @return
	 */
	public int getMessageTotal() {
		int num = dbHelperImpl.getRecNum(SQLMessage.tableName);
		return num >30?30:num;
	}
	
	/**
	 * 
	 * 判断是否包含未读消息
	 * @return
	 */
	public boolean isExitUnreadMessage(){
		return getMessageNumById() > 0;
	}
}
