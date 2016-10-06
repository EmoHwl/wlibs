package com.wlib.core.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;

import com.google.gson.Gson;
import com.wlib.q.L;
import com.wlib.q.WlibApplication;
import com.wlib.q.utils.AppInfoUtils;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:
 */
public class HttpEngine extends AbsHttp{
	private Map<String,String> headers = new HashMap<String, String>();
	private Map<String, Object> paramsMap = new HashMap<String, Object>();
	private RequsetType rType = RequsetType.GET;
	private int timeout;
	private HttpEngine() {
		super();
		addHeader();
	}
	
	public static AbsHttp getInstance() {
		if (absInstance == null) {
			absInstance = new HttpEngine();
		}
		return absInstance;
	}
	
	public HttpEngine requestType(RequsetType rType) {
		this.rType = rType;
		return this;
	}
	
	public HttpEngine url(String url) {
		if (url!=null) {
			SERVICE_URL = url;
		}
		return this;
	}
	
	public HttpEngine timeout(int addTimeOut) {
		this.timeout = addTimeOut;
		return this;
	}
	
	/**
	 * 添加参数
	 * @param paramsMap
	 * @return
	 */
	public HttpEngine params(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
		return this;
	}
	
	/**
	 * 特殊要求
	 */
	private void addHeader() {
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("App-Type", "android");
		String[] version;
		try {
			version = AppInfoUtils.getVersionName(AppInfoUtils.appContext);
			String ver = "" + version[0];
			headers.put("App-Version", ver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		header(headers);
	}
	
	/**
	 * 添加header
	 * @param headers
	 */
	public HttpEngine header(Map<String, String> headers) {
		if (headers!=null) {
			for (String key : headers.keySet()) {
				this.headers.put(key, headers.get(key));
			}
		}
		return this;
	}

	/**
	 * 启动开始访问
	 * @param iHttpCallback
	 */
	public void callback(IHttpCallback iHttpCallback,int local){
		addTimeOut(timeout);
		switch (rType) {
		case GET_OR_POST:
		case GET:
			try {
				get(iHttpCallback,local);
			} catch (IOException e) {
				if (iHttpCallback!=null) {
					iHttpCallback.onFailed(IO_EXCEPTION, "IO流异常");
				}
				e.printStackTrace();
			}
			break;
		case POST:
			try {
				post(iHttpCallback,local);
			} catch (IOException e) {
				if (iHttpCallback!=null) {
					iHttpCallback.onFailed(IO_EXCEPTION, "IO流异常");
				}
				e.printStackTrace();
			}
			break;
		case PUT:
			try {
				put(iHttpCallback);
			} catch (IOException e) {
				if (iHttpCallback!=null) {
					iHttpCallback.onFailed(IO_EXCEPTION, "IO流异常");
				}
				e.printStackTrace();
			}
			break;
		default:
			rType = RequsetType.GET_OR_POST;
			callback(iHttpCallback,local);
			break;
		}
	}
	
	//获取基础连接
	private HttpURLConnection getConnection(IHttpCallback iHttpCallback) {
		HttpURLConnection connection = null;
		try {
			String data = paserParams(paramsMap);
			String urlStr = SERVICE_URL;
			if (rType == RequsetType.GET) {//判断参数存不存在
				data = data == null?"":data;
				if (urlStr.contains("?")) {
					urlStr = urlStr+"&"+data;
				}else {
					urlStr = urlStr+"?"+data;
				}
			}
			URL url = new URL(urlStr);
			L.i("SERVICE_URL", "SERVICE_URL = " + SERVICE_URL+"   data = "+data);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(REQUEST_MOTHOD);
			connection.setDoInput(true);
			connection.setUseCaches(false);
//			connection.setReadTimeout(TIME_OUT);
//			connection.setConnectTimeout(TIME_OUT);
			System.setProperty("sun.net.client.defaultConnectTimeout", TIME_OUT+""); 
			System.setProperty("sun.net.client.defaultReadTimeout",TIME_OUT+""); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Connetion", "keep-alive");
			connection.setRequestProperty("Response", "json");
			if (headers!=null) {
				for (String key : headers.keySet()) {
					connection.setRequestProperty(key, headers.get(key));
				}
			}
			connection.setChunkedStreamingMode(0);
		} catch (MalformedURLException e) {
			if (iHttpCallback!=null) {
				iHttpCallback.onFailed(MALFORMED_URL, "访问地址格式不对");
			}
			e.printStackTrace();
		} catch (IOException e) {
			if (iHttpCallback!=null) {
				iHttpCallback.onFailed(IO_EXCEPTION, "IO流异常");
			}
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 转化参数
	 * @param paramsMap
	 * @return
	 */
	private String paserParams(Map<String, Object> paramsMap) {
		if (paramsMap==null) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : paramsMap.keySet()) {
			stringBuilder.append(key);
			stringBuilder.append("=");
			try {
				stringBuilder.append(URLEncoder.encode(paramsMap.get(key)+"", ENCODE_CHARSET));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stringBuilder.append("&");
		}
		return stringBuilder.substring(0, stringBuilder.length() - 1);
	}
	
	//解析基层包
	private HttpData parseJson(String json) {
		HttpData httpData = new HttpData();
		Field[] fields = HttpData.class.getDeclaredFields();
		try {
			JSONObject jsonObject = new JSONObject(json);
			for (Field field : fields) {
				String fieldName = field.getName();
				L.d("Field", "fieldName = "+fieldName);
				L.d("Field", "field.toString() = "+field.toString());
				if (field.toString().contains("java.lang.String")) {
					if (jsonObject.has(fieldName)) {
						try {
							field.setAccessible(true);
							field.set(httpData, jsonObject.get(fieldName).toString());
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else if (field.toString().contains("int")||field.toString().contains("Integer")) {
					if (jsonObject.has(fieldName)) {
						try {
							field.setAccessible(true);
							String value = jsonObject.get(fieldName).toString();
							field.set(httpData, Integer.parseInt(value));
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return httpData;
	}
	
	@Override
	protected void post(IHttpCallback iHttpCallback,int local) throws IOException{
		REQUEST_MOTHOD = "POST";
		String data = paserParams(paramsMap);
		HttpURLConnection connection = getConnection(iHttpCallback);
		if (connection == null) {
			requestFailed(iHttpCallback, -1);
		}
		connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
		connection.connect();
		
		OutputStream os = connection.getOutputStream();
		os.write(data.getBytes());
		os.flush();
		
		if (connection.getResponseCode() == 200) {
			//获取响应的输入流对象
			InputStream is = connection.getInputStream();
			//创建字节输出流对象
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//定义读取的长度
			int len = 0;
			//定义缓冲区
			byte buffer[] = new byte[1024];
			//按照缓冲区的大小，循环读取
			while ((len = is.read(buffer))!=-1) {
				//根据读取的长度写入到os对象中
				baos.write(buffer,0,len);
			}
			//释放资源
			is.close();
			baos.close();
			connection.disconnect();
			//返回字符串
			final String result = new String(baos.toByteArray());
			L.i("", "result = "+result);
			HttpData httpData = parseJson(result);
			requestSuccess(iHttpCallback, httpData, local);
		}else {
			requestFailed(iHttpCallback, connection.getResponseCode());
			connection.disconnect();
		}
	}

	@Override
	protected void get(IHttpCallback iHttpCallback,int local) throws IOException{
		REQUEST_MOTHOD = "GET";
		HttpURLConnection connection = getConnection(iHttpCallback);
		if (connection == null) {
			requestFailed(iHttpCallback, -1);
		}
		connection.connect();
		
		if (connection.getResponseCode() == 200) {
			//获取响应的输入流对象
			InputStream is = connection.getInputStream();
			//创建字节输出流对象
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//定义读取的长度
			int len = 0;
			//定义缓冲区
			byte buffer[] = new byte[1024];
			//按照缓冲区的大小，循环读取
			while ((len = is.read(buffer))!=-1) {
				//根据读取的长度写入到os对象中
				baos.write(buffer,0,len);
			}
			//释放资源
			is.close();
			baos.close();
			connection.disconnect();
			//返回字符串
			final String result = new String(baos.toByteArray());
			L.i("", "result = "+result);
			HttpData httpData = parseJson(result);
			requestSuccess(iHttpCallback, httpData,local);
		}else {
			requestFailed(iHttpCallback, connection.getResponseCode());
			connection.disconnect();
		}
	}
	
	/**
	 * 解析成功返回
	 * @param iHttpCallback
	 * @param httpData
	 */
	private void requestSuccess(IHttpCallback iHttpCallback,HttpData httpData,int local) {
		int flag = httpData.getFlag();
		String info = httpData.getInfo();
		if (iHttpCallback!=null) {
			if (flag == 1) {
				iHttpCallback.onSuccess(httpData.getDatas(),httpData.getInfo(),local);
			}else {
				httpData.setLocal(local);
				iHttpCallback.onError(httpData);
			}
		}
	}
	
	/**
	 * 请求失败
	 * @param iHttpCallback
	 * @param responseCode
	 */
	private void requestFailed(IHttpCallback iHttpCallback,int responseCode) {
		if (iHttpCallback!=null) {
			iHttpCallback.onFailed(responseCode, "请求失败");
		}
	}

	@Override
	protected void put(IHttpCallback iHttpCallback)throws IOException {
		REQUEST_MOTHOD = "PUT";
	}

}
