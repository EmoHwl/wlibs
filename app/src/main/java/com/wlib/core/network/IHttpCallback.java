package com.wlib.core.network;


/**
 * @author weiliang
 * 2015年11月23日
 * @说明: http 回调接口
 */
public interface IHttpCallback {
	
	/**
	 * @param data 成功数据json
	 * @param local 本地标识 
	 * @param info 信息
	 */
	void onSuccess(String data,String info,int local);
	
	/**
	 * 请求成功，存在错误
	 * @param httpData 包含local
	 */
	void onError(HttpData httpData);
	
    /**
     * @param flag 错误码
     * @param message 错误信息
     */
	void onFailed(int flag, String message);
}
