package com.wlib.q.file;

import android.content.SharedPreferences;

/**
 * @author weiliang
 * SharedPreferences 工具类 
 */
public class SpUtils extends BaseSp{
	static SharedPreferences sp;
	static SpUtils spUtils;
	
	private SpUtils() {
		filename = "wlibs";
		init();
	}
	
	public static SpUtils getInstance(){
		if (sp == null||spUtils == null) {
			spUtils = new SpUtils();
		}
		return spUtils;
	}

	// 取出用戶ID
	public String getUid() {
		return (String)getValue("uid", "-1");
	}
	
	public void setMsgMaxId(int id) {
		setValue("MsgMaxId", id);
	}
	
	public Integer getMsgMaxId() {
		return (Integer)getValue("MsgMaxId", 0);
	}
}
