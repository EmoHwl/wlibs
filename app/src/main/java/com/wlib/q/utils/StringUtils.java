package com.wlib.q.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * @author weiliang
 * 2015年11月12日
 * @说明:
 */
public class StringUtils {

	/**
	 * 正则表达式查询字符串中的手机号码，并返回
	 * 如果包含多个则用逗号间隔
	 * @param num
	 * @return
	 */
	public static String numberCheck(String num) {
		if(num == null || num.length() == 0){return "";}
		  Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[34578]\\d{9})|(?:861[34578]\\d{9}))(?!\\d)"); 
			  Matcher matcher = pattern.matcher(num); 
			  StringBuffer bf = new StringBuffer(64); 
			  while (matcher.find()) { 
			      bf.append(matcher.group()).append(","); 
			  } 
			  int len = bf.length(); 
			  if (len > 0) { 
			     bf.deleteCharAt(len - 1); 
			  } 
		return bf.toString();
	}
	
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 * 20150622 huangweiliang 添加 第二文 4  已有 147号段
		 */
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
	
	public static boolean isEmpty(String s) {
		if (s == null||s.equals("")) {
			return true;
		}
		return false;
	}
}
