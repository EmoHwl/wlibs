package com.wlib.base.model;

import java.util.Map;

/**
 * @author weiliang
 * 2015年11月24日
 * @说明:
 * 测试请求参数
 */
public class RequestParams extends BaseParams {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8915487696463356868L;
	
	private String uid;
	
	private int requestPage;
	
	private long time;
	
	private double money;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getRequestPage() {
		return requestPage;
	}

	public void setRequestPage(int requestPage) {
		this.requestPage = requestPage;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	public Map<String, Object> toMap() {
		return super.toMap(this);
	}
	
}
