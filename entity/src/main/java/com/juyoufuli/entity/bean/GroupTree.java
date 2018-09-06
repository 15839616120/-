package com.juyoufuli.entity.bean;

import java.util.List;

public class GroupTree {
    /**
     * group_id:组id
     */
    private Long groupId;

    /**
     * group_name:组名
     */
    private String groupName;
    
    /**
     * children:子节点树
     */
    private List<GroupTree> children;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<GroupTree> getChildren() {
		return children;
	}

	public void setChildren(List<GroupTree> children) {
		this.children = children;
	} 
    
}
