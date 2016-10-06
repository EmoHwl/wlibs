package com.wlib.q;

import android.util.Log;

/**
 * @author weiliang
 * 2015年10月30日
 * @说明:
 */
public class L {
	private static final String sPrefix = "wlib.l-";
	private static String contextName(Object o) {
		if (o instanceof String) return sPrefix + (String)o;
		if (o instanceof Class) return sPrefix + ((Class<?>)o).getSimpleName();
		return sPrefix + o.getClass().getSimpleName();
	}

	public static void i(Object o, String s) {
		if(Debug.LOG) Log.i(contextName(o), String.valueOf(s));
	}

	public static void i(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.i(contextName(o), String.valueOf(s), e);
	}

	public static void i(Object o, Throwable e) {
		i(o, null, e);
	}

	public static void d(Object o, String s) {
		if(Debug.LOG) Log.d(contextName(o), String.valueOf(s));
	}

	public static void d(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.d(contextName(o), String.valueOf(s), e);
	}

	public static void d(Object o, Throwable e) {
		d(o, null, e);
	}

	public static void e(Object o, String s) {
		if(Debug.LOG) Log.e(contextName(o), String.valueOf(s));
		//发送错误统计数据

	}

	public static void e(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.e(contextName(o), String.valueOf(s), e);
		//发送错误统计数据

	}

	public static void e(Object o, Throwable e) {
		e(o, null, e);
	}

	public static void w(Object o, String s) {
		if(Debug.LOG) Log.w(contextName(o), String.valueOf(s));
	}

	public static void w(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.w(contextName(o), String.valueOf(s), e);
	}

	public static void w(Object o, Throwable e) {
		w(o, null, e);
	}
}
