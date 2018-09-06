package com.juyoufuli.entity.bean;


import java.io.Serializable;
import java.util.List;

public class GroupNodeBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String url;
	private List<GroupNodeBean> children;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<GroupNodeBean> getChildren() {
		return children;
	}
	public void setChildren(List<GroupNodeBean> children) {
		this.children = children;
	}
	
	
	
}
