package com.juyoufuli.entity.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.lang.DateUtils;

import java.util.Date;


/**
 * 实体Entity支持类
 */
public  abstract class BaseEntity {


	/**
	 * gmt_create:创建时间
	 */
	@JSONField(format = DateUtils.PATTERN_DEFAULT_ON_SECOND)
	protected Date gmtCreate;

	/**
	 * gmt_modified:修改时间
	 */
	@JSONField(format = DateUtils.PATTERN_DEFAULT_ON_SECOND)
	protected Date gmtModified;

	/**
	 * state:状 态 1正常 2删除
	 */
	protected int state;
	
	public BaseEntity() {
		super();
		this.state = Constant.DEL_FLAG_NORMAL;
	}

	
	/**
	 * 插入之前执行方法，需要手动调用
	 */
	public void preInsert(){
		this.gmtModified = new Date();
		this.gmtCreate = this.gmtModified;
	}
	
	/**
	 * 更新之前执行方法，需要手动调用
	 */
	public void preUpdate(){
		this.gmtModified = new Date();
	}


	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}