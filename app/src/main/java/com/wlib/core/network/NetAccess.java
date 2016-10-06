package com.wlib.core.network;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wlib.core.network.AbsHttp.RequsetType;
import com.wlib.q.WlibApplication;
import com.wlib.q.phone.Device;
import com.wlib.q.utils.AppInfoUtils;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:访问http入口
 */
public class NetAccess {
	private static NetAccess netAccess = null;
	private HttpEngine httpEngine;
	private boolean isNeedThreadPool = false;
	
	public static NetAccess getInstance() {
		if (netAccess == null) {
			netAccess = new NetAccess();
		}
		return netAccess;
	}
	
	private HttpEngine getHttpEngine() {
		return (HttpEngine) HttpEngine.getInstance();
	}
	
	public void request(RequsetType method,String url,IHttpCallback iHttpCallback,final int addTimeOut) {
		request(method, url,null,null,iHttpCallback,-1,addTimeOut);
	}
	
	public void request(RequsetType method,String url,IHttpCallback iHttpCallback,int tag,final int addTimeOut) {
		request(method, url,null,null,iHttpCallback,tag,addTimeOut);
	}
	
	public void request(RequsetType method,String url,Map<String, Object> paramsMap,IHttpCallback iHttpCallback,int tag,final int addTimeOut) {
		request(method, url,paramsMap,null,iHttpCallback,tag,addTimeOut);
	}
	
	/**
	 * 配置请求项目
	 * @param method 请求类型
	 * @param url 
	 * @param paramsMap 请求参数
	 * @param iHttpCallback 回调函数
	 */
	public void request(final RequsetType method,final String url,final Map<String, Object> paramsMap,final Map<String, String> header,final IHttpCallback iHttpCallback,final int tag,final int addTimeOut) {
		httpEngine = getHttpEngine();
		if (isNetworkAvailable()) {
			if (isNeedThreadPool) {
				newCachedThreadPool().execute(new Runnable() {
					public void run() {
						httpEngine.requestType(method).url(url).params(paramsMap).header(header).timeout(addTimeOut).callback(iHttpCallback,tag);
					}
				});
			}else {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						httpEngine.requestType(method).url(url).params(paramsMap).header(header).timeout(addTimeOut).callback(iHttpCallback,tag);
					}
				}).start();
			}
		}else {
			iHttpCallback.onFailed(HttpEngine.NETWORK_ISNOT_ENDABLE, "没有网络");
		}
	}
	
	public static ExecutorService newCachedThreadPool() {    
	    return new ThreadPoolExecutor(Device.getNumCores(), Integer.MAX_VALUE,    
	                                 60L, TimeUnit.SECONDS,    
	                                  new SynchronousQueue<Runnable>());    
	}    
	
	/**
	 * 检查当前网络是否可用
	 * 
	 * @param context
	 * @return boolean
	 */
	public boolean isNetworkAvailable() {
		Context context = AppInfoUtils.appContext;
		if (context == null ) {
			return false;
		}
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isNeedThreadPool() {
		return isNeedThreadPool;
	}

	public void setNeedThreadPool(boolean isNeedThreadPool) {
		this.isNeedThreadPool = isNeedThreadPool;
	}
}
