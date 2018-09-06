package com.juyoufuli.service.service;

import com.juyoufuli.common.result.ResultBean;

/**
 * @Desc: 练手接口
 * @Company: 聚优福利
 * @Author  wuyz@huayingcul.com
 * @Date  2018/9/5 15:24
 */
public interface GroupCopyService {

    ResultBean addStaffGroup(Integer staffId, String groupName, Long parentId);
}
