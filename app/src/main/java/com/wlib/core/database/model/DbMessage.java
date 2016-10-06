package com.wlib.core.database.model;

import java.io.Serializable;

public class DbMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1553894673402366703L;

	private int id;

	/**
	 * 消息标题
	 */
	private String title;

	/**
	 * 消息内容
	 */
	private String content;

	/**
	 * 消息类型 0:普通通知,1:广告,2:商超商品,3:外卖商品,4:团购商品
	 */
	private int type;

	/**
	 * 跳转目标 广告为url链接,商品为商品id
	 */
	private String jump_target;
	private String jumpTarget;
	
	/**
	 * 推送时间
	 */
	private long push_time;
	private long pushTime;
	
	private String uid = "";
	
	/**
	 * 0/1已读为1，未读为0
	 */
	private int isRead;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getJumpTarget() {
		if (jump_target == null) {
			return jumpTarget;
		}
		return jump_target;
	}
	public void setJumpTarget(String jump_target) {
		this.jump_target = jump_target;
		this.jumpTarget = jump_target;
	}
	public Long getPushTime() {
		if (push_time == 0) {
			return pushTime;
		}
		return push_time;
	}
	public void setPushTime(Long push_time) {
		this.push_time = push_time;
		this.push_time = pushTime;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
}
