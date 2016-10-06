package com.wlib.q.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:
 */
public class AppInfoUtils {
	
	public static Context appContext = null;

	/**
	 * 获取当前版本名
	 * @param mContext
	 * @return
	 * @throws Exception
	 */
	public static String[] getVersionName(Context mContext) throws Exception {
			String[] version = new String[2];
			String pkName = mContext.getPackageName();
			try {
				PackageInfo pi = mContext.getPackageManager().getPackageInfo(
						pkName, 1);
				version[0] = pi.versionName;
				version[1] = pi.versionCode+"";
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return version;
	}
	
	/**
	 * app 信息
	 * @return
	 */
	private String getAppInfo(Context mContext) {
 		try {
 			String pkName = mContext.getPackageName();
 			String versionName = mContext.getPackageManager().getPackageInfo(
 					pkName, 0).versionName;
 			int versionCode = mContext.getPackageManager()
 					.getPackageInfo(pkName, 0).versionCode;
 			return pkName + "   " + versionName + "  " + versionCode;
 		} catch (Exception e) {
 		}
 		return null;
 	}
	
	/**
     * 获取渠道名
     * @param ctx 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(Activity ctx,String key) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.get(key)+"";
                        Log.i("channelName", "channelName = "+channelName);
                    }
                }
  
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }
}
