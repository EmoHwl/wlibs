package com.wlib.core.network;

import com.wlib.core.network.IPControler.AbsIP;

/**
 * @author weiliang
 * 2015年11月16日
 * @说明：IP调度
 */
public class IPDispatcher {
	static int index = 0;//默认-1
	private IPControler ipControler;
	public IPDispatcher() {
		this(null);
	}
	
	public IPDispatcher(IPControler ipControler) {
		if (ipControler == null) {
			ipControler = new IPControler();
		}else {
			this.ipControler = ipControler;
		}
	}
	
	/**
	 * 重选IP
	 * @throws Exception 
	 */
	public void reSetIp() throws Exception {
		index = IPControler.getRandomIp(ipControler.getIpNumByType());
		if (index == -1) {
			throw new Exception("未知IP类型");
		}
		AbsIP absIP = ipControler.getIpArray();
		Contant.IP = absIP.getIPs()[index];
		String tempIMAGE_UPLOAD = getIPaddress(absIP.getIMAGE_UPLOADs(), index);
		Contant.IMAGE_UPLOAD = tempIMAGE_UPLOAD == null?Contant.IMAGE_UPLOAD:tempIMAGE_UPLOAD;
		String tempDsImgIp = getIPaddress(absIP.getDsImgIps(), index);
		Contant.DsImgIp = tempDsImgIp == null?Contant.DsImgIp:tempDsImgIp;
		System.out.println("Contant.IP = "+Contant.IP);
		System.out.println("Contant.IMAGE_UPLOAD = "+Contant.IMAGE_UPLOAD);
		System.out.println("Contant.DsImgIp = "+Contant.DsImgIp);
	}
	
	protected String getIPaddress(String[] ipArr,int index) {
		if (ipArr == null||ipArr.length == 0) {
			return null;
		}
		int length = ipArr.length;
		if (index + 1 >= length) {
			return ipArr[0];
		}
		return ipArr[index];
	}
}
