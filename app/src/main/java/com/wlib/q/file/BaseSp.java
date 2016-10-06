package com.wlib.q.file;

import java.math.BigDecimal;

import com.wlib.q.WlibApplication;
import com.wlib.q.utils.AppInfoUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author weiliang
 * 2015年11月24日
 * @说明:
 */
public class BaseSp {
	protected static String filename = "wlibs";
	static SharedPreferences sp;
	Context context;
	public BaseSp() {
		init();
	}
	
	public BaseSp(Context context) {
		this.context = context;
		init();
	}
	
	protected void init() {
		if (context!=null) {
			sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		}else {
			sp = AppInfoUtils.appContext.getSharedPreferences(filename, Context.MODE_PRIVATE);
		}
	}
	
	public SharedPreferences getSp() {
		if (sp == null) {
			init();
		}
		return sp;
	}
	
	/**
	 * 保存SharedPreferences公共方法
	 * @param key
	 * @param value
	 */
	public void setValue(String key,Object value) {
		Editor editor = getSp().edit();
		if (value instanceof String) {
			editor.putString(key, value.toString());
		}else if (value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		}else if (value instanceof Long) {
			editor.putLong(key, (Long)value);
		}else if ((value instanceof Double)||(value instanceof Float)) {
			String valueStr = value+"";
			editor.putFloat(key, Float.valueOf(valueStr));
		}else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		}
		editor.commit();
	}
	
	/**
	 * 获取值
	 * @param key
	 * @param dfValue
	 */
	public Object getValue(String key,Object dfValue) {
		if (dfValue instanceof String) {
			return (String)getSp().getString(key, dfValue.toString());
		}else if (dfValue instanceof Integer) {
			return (Integer)getSp().getInt(key, (Integer)dfValue);
		}else if (dfValue instanceof Long) {
			return (Long)getSp().getLong(key, (Long)dfValue);
		}else if ((dfValue instanceof Double)) {
			String valueStr = dfValue+"";
			String floatValue =  (Float)getSp().getFloat(key, Float.valueOf(valueStr))+"";
			BigDecimal bigDecimal = new BigDecimal(floatValue);
			return bigDecimal.doubleValue();
		}else if ((dfValue instanceof Float)) {
			String valueStr = dfValue+"";
			return (Float)getSp().getFloat(key, Float.valueOf(valueStr));
		}else if (dfValue instanceof Boolean) {
			return getSp().getBoolean(key, (Boolean)dfValue);
		}
		return dfValue;
	}
	
	/**
	 * 删除值
	 * @param key
	 */
	public void remove(String key) {
		Editor editor = getSp().edit();
		editor.remove(key); 
		editor.commit();
	}
}
