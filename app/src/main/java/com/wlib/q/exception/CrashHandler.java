package com.wlib.q.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.wlib.q.L;
import com.wlib.q.file.FileVersioned;
import com.wlib.q.phone.Device;
import com.wlib.q.utils.AppInfoUtils;

import android.content.Context;

public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.getName();
	private static CrashHandler instance;
	
	//系统默认的UncaughtException处理类  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    
    //程序的Context对象  
    private Context mContext; 
    
    //用于格式化日期,作为日志文件名的一部分  
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    private CurActivityHandler mCurActivityHandler;
    private static HashMap<String, Object> crashMap = new HashMap<String, Object>();
    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {  
    }  
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        if (instance == null)  
            instance = new CrashHandler();  
        return instance;  
    }  
    public interface CurActivityHandler{
    	void onHandler();
    }
    public void setmCurActivityHandler(CurActivityHandler mCurActivityHandler) {
		this.mCurActivityHandler = mCurActivityHandler;
	}
    /** 
     * 初始化 
     */  
    public void init(Context context) {  
        mContext = context;  
        //获取系统默认的UncaughtException处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        //设置该CrashHandler为程序的默认处理器      
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(thread, ex)&& mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理  
           mDefaultHandler.uncaughtException(thread, ex); 
           L.i(TAG, "info : "+ex);    
		}else {
			if ("mian".equals(thread.getName())) {
				mDefaultHandler.uncaughtException(thread, ex); 
			}else {
				L.e(TAG, "error clear: "+ex);    
				if (mCurActivityHandler!=null) {
					mCurActivityHandler.onHandler();
				}
			}
		}
	}
	
	private boolean handleException(Thread thread, Throwable ex) {
		if (ex == null) {
			return false;
		}
		String crashInfo = "";
		String dateStr = formatter.format(new Date(System.currentTimeMillis()));
		crashInfo += thread+" \nexception="+ex.toString()+"";
		crashInfo = getDeviceInfo()+ " \ndate="+dateStr+" \n" + crashInfo;
		L.e(TAG, crashInfo);
//		crashMap.put(dateStr+" "+thread.getId(), crashInfo);
//		saveLocalCrashInfo();
		return true;
	}
	
	//将crash保存在本地
	private void saveLocalCrashInfo() {
		String info = "";
		for (String key:crashMap.keySet()) {
			info += crashMap.get(key)+"\n";
		}
		FileVersioned.saveAsFile(mContext, mContext.getCacheDir().getAbsolutePath(), "localFile", info);
	}

	//获取设备信息
	private String getDeviceInfo() {
		String dInfo = "";
		Device device = Device.getInstance(mContext);
		dInfo += "brand="+device.brand+" sysVersion="+device.sysVersion+" ";
		try {
			String[] appInfo = AppInfoUtils.getVersionName(mContext);
			dInfo += "versionName="+appInfo[0] +"  versionCode="+ appInfo[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dInfo;
	}
}
