package com.wlib.q.phone;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import com.wlib.q.L;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * @author weiliang
 * 2015年10月29日
 * @说明:
 * 需要权限：  
 * android.permission.READ_PHONE_STATE
 * android.permission.ACCESS_WIFI_STATE
 */
public class Device {
	public  int width;
	public  int height;
	/**改为需要的时候获取，避免在登录时提示获取权限**/
	private String phoneNumber;
	public  String  deviceId;
	public  String macAddress;
	public  String androidId;
	public  String sysVersion;
	public  String brand;
	public  String cpuAbi;
	
	private static Device mDeviceInfo;
	
	public static Device getInstance(Context context) {
		 if(mDeviceInfo == null)mDeviceInfo = new Device(context);
		 return mDeviceInfo;
	}
	
	private Device(Context context) {
		/*屏幕尺寸*/
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenW = dm.widthPixels;
		int screenH = dm.heightPixels;
		if (screenW > screenH) {
			int i = screenW;
			screenW = screenH;
			screenH = i;
		}
		width = screenW;
		height = screenH;
		
		//设备号
		deviceId = getTelephonyManager(context).getDeviceId();
		//系统版本
		sysVersion = "Android "+Build.VERSION.RELEASE;
		//设备型号
		brand = Build.BRAND+ " "+ Build.MODEL;
		
		if (Build.VERSION.SDK_INT < 8) {
			cpuAbi = Build.CPU_ABI;
		}else {
			cpuAbi = Build.CPU_ABI + (Build.CPU_ABI2.equals(Build.UNKNOWN)?"":","+Build.CPU_ABI2);
		}
		//
		WifiInfo wifiInfo = getWifiManager(context).getConnectionInfo();
		macAddress = wifiInfo == null?null :wifiInfo.getMacAddress();
		
		androidId =  Settings.Secure.getString(context.getContentResolver()	, Settings.Secure.ANDROID_ID);
	}
	
	public static String getPhoneNumber(Context context) {
		Device device = getInstance(context);
		if (device.phoneNumber == null) {
			device.phoneNumber = getTelephonyManager(context).getLine1Number();
			if(device.phoneNumber == null || device.phoneNumber.length() == 0 || device.phoneNumber.matches("0*")) device.phoneNumber = "";
		}
		return device.phoneNumber;
	}
	
	public static String getUniqueId(Context context) {
		//TODO
		String id = null;
		if (id == null) {
			Device device = getInstance(context);
			if (device.deviceId !=null) {
				id =  device.deviceId;
			}else if (device.androidId !=null) {
				id = device.androidId;
			}else {
				id = device.macAddress;
			}
		}
		return id;
	}
	
	private static WifiManager getWifiManager(Context context) {
		return (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	private static TelephonyManager getTelephonyManager(Context context) {
		return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public String getInfoStr(Context context){
		return "("+height+"x"+width+") /"+ getPhoneNumber(context)+"/"+deviceId+
				"/"+macAddress+"/"+androidId+"/"+sysVersion+"/"+brand+"/"+cpuAbi+"/";
	}
	
	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getNumCores() {
	    //Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }

	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        L.d("Device", "CPU Count: "+files.length);
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Print exception
	    	L.d("Device",  "CPU Count: Failed.");
	        e.printStackTrace();
	        //Default to return 1 core
	        return 1;
	    }
	}
}
