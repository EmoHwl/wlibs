package com.wlib.core.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author weiliang 2015年11月27日
 * @说明:
 */
public class PingUtils {

	public static String pingIp(int pingNum, String m_strForNetAddress)
			throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(
				"/system/bin/ping -c " + pingNum + " -w 100" + m_strForNetAddress);
		int status = p.waitFor();
		String result;
		if (status == 0) {
			result = "success";
		} else {
			result = "failed";
		}
		String lost = new String();
		String delay = new String();
		BufferedReader buf = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String str = new String();

		// 读出所有信息并显示
		while ((str = buf.readLine()) != null) {
			str = str + "\r\n";
			return result +" "+str;
		}
		return null;
	}
}
