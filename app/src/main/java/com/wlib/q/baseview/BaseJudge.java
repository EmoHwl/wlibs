package com.wlib.q.baseview;

import android.app.Activity;

/**
 * 
 * @author huangweiliang
 *
 */
public class BaseJudge {
	
	public static boolean isActivityRunning(Activity activity) {
		return !activity.isFinishing();
	}
	
}
