package com.wlib.core.network;

import java.io.Serializable;

/**
 * @author weiliang
 * 2015年11月23日
 * @说明:
 */
public class HttpData implements Serializable,ICheckedValid{

	/**
	 * 
	 */
	private static final long serialVersionUID = -205478344593375300L;
	
	private int flag;
	
	private String info;
	
	private String datas;
	
	private int local;
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDatas() {
		return datas;
	}

	public void setDatas(String datas) {
		this.datas = datas;
	}
	
	@Override
	public String toString() {
		return "flag ="+flag+ "\n info="+info+
				"\n datas ="+datas;
	}

	@Override
	public boolean isCanUse() {
		return datas == null?false:true;
	}

	public int getLocal() {
		return local;
	}

	public void setLocal(int local) {
		this.local = local;
	}

}
