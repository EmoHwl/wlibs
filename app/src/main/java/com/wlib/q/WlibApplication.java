package com.wlib.q;

import com.wlib.q.exception.CrashHandler;
import com.wlib.q.exception.CrashHandler.CurActivityHandler;
import com.wlib.q.utils.AppInfoUtils;

import android.app.Application;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:
 */
public class WlibApplication extends Application {
	
	private static CurActivityHandler mCurActivityHandler;
	
	public static void setmCurActivityHandler(CurActivityHandler mCurActivityHandler) {
		WlibApplication.mCurActivityHandler = mCurActivityHandler;
		CrashHandler.getInstance().setmCurActivityHandler(mCurActivityHandler);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AppInfoUtils.appContext = this;
//		CrashHandler uCrashHandler =  CrashHandler.getInstance();
//		uCrashHandler.init(AppInfoUtils.appContext);
	}
}
