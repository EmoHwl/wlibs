package com.wlib.core.network;

/**
 * @author weiliang
 * 2015年11月16日
 * @说明:IP
 */
public class IPControler {
	enum IPType{
		RELEASE,DEBUG,DEVELOP;
	}
	public final static IPType CUR_IP_TYPE = IPType.DEBUG;
	class ReleaseIp extends AbsIP{
		/**
		 * 以下IP ant打包时会自动替换 ，请不要编辑
		 */
		//正式服
		public final static String IP = "http://app.huaguogou.com/appservice/";
		public final static String IMAGE_UPLOAD = "http://app.huaguogou.com/appservice/";
		public final static String DsImgIp = "http://file.hgyzsw.com/";
		
		//正式服 阿里云nginx代理
		public final static String IPAli = "http://120.25.216.67:18088/appservice/";
		public final static String IMAGE_UPLOADAli = "http://120.25.216.67:18088/appservice/";
		public final static String DsImgIpAli = "http://120.25.216.67:18089/";
				
		//正式服 腾讯云代理
		public final static String IPTX = "http://119.29.35.48:18088/appservice/";
		public final static String IMAGE_UPLOADTX = "http://119.29.35.48:18088/appservice/";
		public final static String DsImgIpTX = "http://119.29.35.48:18089/";
		
		@Override
		String[] getIPs() {
			return new String[]{IP,IPAli,IPTX};
		}
		
		@Override
		String[] getIMAGE_UPLOADs() {
			return new String[]{IMAGE_UPLOAD,IMAGE_UPLOADAli,IMAGE_UPLOADTX};
		}
		
		@Override
		String[] getDsImgIps() {
			return new String[]{DsImgIp,DsImgIpAli,DsImgIpTX};
		}
	}
	
	class DebugIp extends AbsIP{
		//测试服
		public final static String IP = "http://220.197.207.238:24931/appservice4/";
		public final static String IMAGE_UPLOAD = "http://220.197.207.238:24931/appservice4/";
		public final static String DsImgIp = "http://220.197.207.238:24993/";
		
		@Override
		String[] getIPs() {
			return new String[]{IP};
		}
		
		@Override
		String[] getIMAGE_UPLOADs() {
			return new String[]{IMAGE_UPLOAD};
		}
		
		@Override
		String[] getDsImgIps() {
			return new String[]{DsImgIp};
		}
	}
	
	class DevelopIp extends AbsIP{
		//开发服
		public final static String IP = "http://120.25.107.12:8090/appservice/";
		public final static String IMAGE_UPLOAD = "http://220.197.207.238:24931/appservice4/";
		public final static String DsImgIp = "http://220.197.207.238:24993/";
		
		@Override
		String[] getIPs() {
			return new String[]{IP};
		}
		
		@Override
		String[] getIMAGE_UPLOADs() {
			return new String[]{IMAGE_UPLOAD};
		}
		
		@Override
		String[] getDsImgIps() {
			return new String[]{DsImgIp};
		}
	}
	
	protected abstract class AbsIP{
		abstract String[] getIPs();
		abstract String[] getIMAGE_UPLOADs();
		abstract String[] getDsImgIps();
	}
	
	public static int getRandomIp(int max) {
		if (max == -1) {
			return -1;
		}
		int index = (int) (Math.random()*max);
		return index;
	}
	
	/**
	 * 获取IP地址数量
	 * @return
	 */
	public int getIpNumByType() {
		switch (IPControler.CUR_IP_TYPE) {
		case RELEASE:
			ReleaseIp releaseIp = new ReleaseIp();
			return releaseIp.getIPs().length;
		case DEBUG:
			DebugIp debugIp = new DebugIp();
			return debugIp.getIPs().length;
		case DEVELOP:
			DevelopIp developIp = new DevelopIp();
			return developIp.getIPs().length;
		default:
			return 0;
		}
	}
	
	/**
	 * 获取当前IP数组类型
	 * @return
	 */
	public AbsIP getIpArray() {
		switch (CUR_IP_TYPE) {
		case RELEASE:
			return new ReleaseIp();
		case DEBUG:
			return new DebugIp();
		case DEVELOP:
			return new DevelopIp();
		default:
			return new AbsIP() {
				
				@Override
				String[] getIPs() {
					return new String[]{};
				}
				
				@Override
				String[] getIMAGE_UPLOADs() {
					return new String[]{};
				}
				
				@Override
				String[] getDsImgIps() {
					return new String[]{};
				}
			};
		}
	}
	

}
