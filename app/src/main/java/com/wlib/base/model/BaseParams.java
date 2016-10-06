package com.wlib.base.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.wlib.q.L;

import android.util.Log;

/**
 * @author weiliang
 * 2015年11月24日
 * @说明: 基础请求参数
 */
public abstract class BaseParams implements Serializable{
	protected Map<String, Object> map = new HashMap<String, Object>();
	
	protected Map<String, Object> toMap(BaseParams baseParams){
		Field[] fields = baseParams.getClass().getDeclaredFields();
		map.clear();
		for (Field field : fields) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Type type = field.getType();
			try {
				if (!fieldName.equals("serialVersionUID")) {
				if (field.getType() == String.class) {
					String value = (String) field.get(baseParams);
					if (value!=null) {
						map.put(fieldName, value);
					}
				}else if (field.getType() == int.class||field.getType() == Integer.class) {
					int value = field.getInt(baseParams);
					map.put(fieldName, value);
				}else if (field.getType() == double.class||field.getType() == Double.class) {
					double value = field.getDouble(baseParams);
					map.put(fieldName, value);
				}else if (field.getType() == long.class||field.getType() == Long.class) {
					long value = field.getLong(baseParams);
					map.put(fieldName, value);
				}
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	};
}
