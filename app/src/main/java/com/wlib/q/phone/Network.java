package com.wlib.q.phone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wlib.q.L;

/**
 * @author weiliang
 * 2015年10月29日
 * @说明:
 * 需要权限：
 * android.permission.ACCESS_NETWORK_STATE
 * android.permission.ACCESS_WIFI_STATE
 * Ping或获取本机IP需要权限：
 * android.permission.INTERNET
 * 设置漫游状态需要权限：
 * android.permission.WRITE_SETTINGS
 */
public class Network {

	public static Type getConnectedNetworkType(Context context) {
		NetworkInfo networkInfo = getConnectManager(context).getActiveNetworkInfo();
		if (networkInfo ==null ||networkInfo.getState()!=NetworkInfo.State.CONNECTED) {
			return Type.NO_NET;
		}
		
		return getType(networkInfo);
	}
	public static State getNetworkState(Context context) {
		NetworkInfo networkInfo = getConnectManager(context).getActiveNetworkInfo();
		if (networkInfo == null) {
			return State.DISCONNECTED;
		}
		
		State state  = State.UNKNOWN;
		
		switch (networkInfo.getState()) {
		case CONNECTING:
			state = State.CONNECTING;
			break;
		case CONNECTED:
			state =State.CONNECTED;
			break;
		case SUSPENDED:
			state = State.SUSPENDED;
			break;
		case DISCONNECTING:
			state = State.DISCONNECTING;
			break;
		case DISCONNECTED:
			state = State.DISCONNECTED;
			break;
		case UNKNOWN:
			state = State.UNKNOWN;
			break;
		default:
			break;
		}
		return state;
	}
	
	//判断网络连接
	public static boolean isNetConneted(Context context) {
		NetworkInfo networkInfo = getConnectManager(context).getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.getState() == NetworkInfo.State.CONNECTED;
	}
	
	//是否wifi网络
	public static boolean isWifiConneted(Context context) {
		return getConnectedNetworkType(context) == Type.WIFI;
	}

	//是否4G网络
	public static boolean is4GConnected(Context context) {
		return getConnectedNetworkType(context) == Type.G4;
	}
	
	//是否3G网络
	public static boolean is3GConnected(Context context) {
		return getConnectedNetworkType(context) == Type.G3;
	}

	//是否2G网络
	public static boolean is2GConnected(Context context) {
		return getConnectedNetworkType(context) == Type.G2;
	}
	
	//是否漫游网络
	public static boolean isRoaming(Context context) {
		NetworkInfo networkInfo = getConnectManager(context).getActiveNetworkInfo();
		boolean isMobile =  (networkInfo!=null&&networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
		boolean isRoaming = isMobile&&getTelephonyManager(context).isNetworkRoaming();
		return isRoaming;
	}
	
	//设置飞行模式
	public static void setAirplaneMode(Context context , boolean on) {
		Settings.System.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, on?1:0);
	}
	
	//判断是否飞行模式
	public static boolean isAirplaneMode(Context context) {
		return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,0)!=0;
	}
	
	//打开网络设置
	public static void openNetGraceful(Context context) {
		try {
			context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
		} catch (Exception e) {
			context.startActivity(new Intent(Settings.ACTION_SETTINGS));
		}
	}
	
	public static String IP_Host(String host, boolean format) {
		try {
			String ip = InetAddress.getByName(host).getHostAddress();
			return format?ip.replace('.', '_'):ip;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String IP_Me(boolean format) {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL("http://whois.pconline.com.cn/ipJson.jsp").openConnection();
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setReadTimeout(3000);
		connection.setConnectTimeout(3000);
		
		connection.setRequestProperty("Accept", "Application/json;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Window NT 5.1) AppleWebKit/535.11"+
				" (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
		int statusCode = connection.getResponseCode();
		String ip = null;
		if (statusCode == 200) {
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
			String result = new String(baos.toByteArray(),"utf-8");
			L.i("", "result = "+result);
			if (!TextUtils.isEmpty(result)) {
				result = result.substring(result.indexOf("{\""), result.indexOf(");")).trim();
				JSONObject jsonObject = new JSONObject(result);
				if (format) {
					ip =  jsonObject.getString("addr").replace(" ", "")+jsonObject.getString("ip").replace('.', '_');
				}else {
					ip = jsonObject.getString("addr")+jsonObject.getString("ip");
				}
			}
			return ip;
		} 
		return ip;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String DNS(int n ,boolean format) {
		String dns = null;
		Process  process = null;
		LineNumberReader reader = null;
		try {
			final String CMD = "getprop net.dns"+(n<=1?1:2);
			
			process = Runtime.getRuntime().exec(CMD);
			reader  = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine())!=null) {
				dns = line.trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (reader!=null) reader.close();
				if (process!=null) process.destroy();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		return format?(dns!=null?dns.replace('.', '_'):dns):dns;
	}
	
	public static String PING(String host, boolean format) {
		final int PACKAGES = 4;
		String info = null;
		String print = null;
		Process process = null;
		LineNumberReader reader = null;
		try {
			final String CMD = "ping -c " + PACKAGES + " " + host;
			if(format) {
				info = "ping-c" + PACKAGES + "-" + host.replace('.', '_');
			}else {
				print = CMD + "\n";
			}

			process = Runtime.getRuntime().exec(CMD);
			reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));

			String line = null;
			boolean start = false;
			int index = -1;
			while ((line = reader.readLine()) != null) {
				if(!format) {
					print += line + "\n";
				}else {
					line = line.trim();
					if(line.toLowerCase().startsWith("ping")) {
						line = line.substring(0, line.indexOf(')'));
						line = line.replace("(", "");
						line = line.replace(' ', '-');
						line = line.replace('.', '_');
						start = true;
					}else if(start) {
						index = line.indexOf(':');
						if(index > 0) {
							//取得ttl=53部分

							line = line.substring(index+1).trim();
							index = line.indexOf(' ');
							line = line.substring(index+1, line.indexOf(' ', index+3)).trim();
							line = line.replace('=', '_');
							start = false;
						}else {
							start = false;
							continue;
						}
					}else if(line.startsWith(""+PACKAGES)) {
						index = line.indexOf(',');
						line = line.substring(index+1).trim();
						line = line.substring(0, line.indexOf(' ')).trim();
						line = line + "in" + PACKAGES + "received";
					}else if(line.startsWith("rtt")) {
						line = line.replaceFirst(" ", "-");
						line = line.replace(" ", "");
						line = line.replace('/', '-');
						line = line.replace('.', '_');
						line = line.replace("=", "--");
					}else {
						continue;
					}
					if(info == null) info = line;
					info += "--" + line;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(reader != null) reader.close();
				if(process != null) process.destroy();	//测试发现，可能会抛异常

			} catch (IOException e) {
				//e.printStackTrace();

			}
		}
		return format ? info : print;
	}
	
	//获取网络类型
	private static Type getType(NetworkInfo networkInfo) {
		Type type;
		//状态有：TYPE_WIFI、TYPE_MOBILE、TYPE_MOBILE_MMS等
		if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				type = Type.WIFI;
		}else {
			switch (networkInfo.getSubtype()) {
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				type = Type.NO_NET;
				break;
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
					type = Type.G2;
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_IDEN:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					type = Type.G3;
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					type = Type.G4;
					break;
				default:
					type = Type.G4;
					break;
			}
		}
		return type;
	}
	
	private static ConnectivityManager getConnectManager(Context context) {
		return (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private static TelephonyManager getTelephonyManager(Context context) {
		return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public enum State {
		CONNECTING,
		CONNECTED,
		/**网络连接被禁用**/
		SUSPENDED,
		DISCONNECTING,
		DISCONNECTED,
		UNKNOWN
	}

	public enum Type {
		NO_NET,
		/**2G网络**/
		G2,
		/**3G网络**/
		G3,
		/**4G或更快的网络**/
		G4,
		WIFI;
	}
}
