package com.wlib.core.network;

import java.io.IOException;

import com.wlib.q.L;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:
 * http 下载抽象类
 */
public abstract class AbsHttp {
	protected String SERVICE_URL = "";
	protected String REQUEST_MOTHOD = "GET";
	protected String ENCODE_CHARSET = "UTF-8";
	protected int TIME_OUT = 3000;
	
	protected void addTimeOut(int addTime) {
		if (TIME_OUT == 3000) {
			TIME_OUT = TIME_OUT + (addTime>0?addTime:0);
		}
	}
	
	protected final static int HTTP_DATA_SUCCESS  = 1;
	
	public static int MALFORMED_URL = 601;//请求地址格式不对
	public static int IO_EXCEPTION = 602;//io异常
	public static int NETWORK_ISNOT_ENDABLE = 666;//没有网络
	
	public enum RequsetType{
		GET_OR_POST,GET,POST,PUT
	};
	
	protected static AbsHttp absInstance = null;
	
	protected AbsHttp() {
		// TODO Auto-generated constructor stub
	}
	 
	protected abstract void post(IHttpCallback iHttpCallback,int local) throws IOException;  
	
	protected abstract void get(IHttpCallback iHttpCallback,int local) throws IOException;
	
	//通常指定了资源的存放位置
	protected abstract void put(IHttpCallback iHttpCallback) throws IOException;
}
