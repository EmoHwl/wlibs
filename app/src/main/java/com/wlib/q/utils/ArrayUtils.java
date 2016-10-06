package com.wlib.q.utils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author weiliang
 * 2015年10月30日
 * @说明:
 * 数组基础工具
 */
public class ArrayUtils {

	/**
	 * 添加一个元素
	 * @param array
	 * @param element
	 * @return
	 */
	public static <T> T[] addElement(T[] array, T element) {
		Class<?> ct = array.getClass().getComponentType();
		T[] newArray = (T[])Array.newInstance(ct, array.length + 1);
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length-1] = element;
		return newArray;
	}
	
	/**
	 * 数组转化为列表
	 * @param array
	 * @return
	 */
	public static <T> ArrayList<T> toArrayList(T[] array) {
		ArrayList<T> list = new ArrayList<T>();
		if(array != null) {
			for(T t : array) {
				list.add(t);
			}
		}
		return list;
	}
	
	/**
	 * 集合转化为列表
	 * @param collect
	 * @return
	 */
	public static <T> ArrayList<T> toArrayList(Collection<T> collect) {
		ArrayList<T> list = new ArrayList<T>();
		if(collect != null) {
			for(T t : collect) {
				list.add(t);
			}
		}
		return list;
	}
	
	public static <T> String[] toUpperCase(T[] array, Locale locale, boolean trim) {
		String[] newArray = null;
		if(array != null) {
			if (array instanceof String[]) {
				newArray = (String[])array;
			} else {
				newArray = new String[array.length];
			}
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					newArray[i] = null;
				} else {
					newArray[i] = array[i].toString();
					if (trim) newArray[i] = newArray[i].trim();
					newArray[i] = newArray[i].toUpperCase(locale);
				}
			}
		}
		return newArray;
	}

	public static <T> String[] toLowerCase(T[] array, Locale locale, boolean trim) {
		String[] newArray = null;
		if(array != null) {
			if (array instanceof String[]) {
				newArray = (String[])array;
			} else {
				newArray = new String[array.length];
			}
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					newArray[i] = null;
				} else {
					newArray[i] = array[i].toString();
					if (trim) newArray[i] = newArray[i].trim();
					newArray[i] = newArray[i].toLowerCase(locale);
				}
			}
		}
		return newArray;
	}
	
	//空数组判断
	public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length <= 0;
    }
	
	public static String[] toPathArray(File[] files) {
        if (files != null && files.length > 0) {
            String[] paths = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                paths[i] = files[i].getPath();
            }
            return paths;
        }
        return null;
    }
	
	// 列表为空 或者列表 长度为0
	public static <T> boolean emptyList(List<T> list) {
		if (list == null) {
			return true;
		}
		if (list.isEmpty()) {
			return true;
		}
		return false;
	}
}
