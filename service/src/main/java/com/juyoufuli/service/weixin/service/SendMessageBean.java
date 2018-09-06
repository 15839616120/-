package com.juyoufuli.service.weixin.service;

import com.google.gson.Gson;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.service.weixin.pojo.BaseMessage;
import com.juyoufuli.service.weixin.util.WeiXinUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * JC2
 */
@Component
public class SendMessageBean {
	private static Logger log = LoggerFactory.getLogger(SendMessageBean.class);
	public void sendMessage(String accessToken,BaseMessage message){
		Gson gson = new Gson();
		String jsonMessage =gson.toJson(message);
		String sendMessageUrl=Constant.WECHAT_SENDMESSAGE_URL.replace(Constant.WECHAT_ACCESS_TOKEN, accessToken);
		JSONObject jsonObject = WeiXinUtil.httpRequest(sendMessageUrl, Constant.WECHAT_REQUEST_METHOD, jsonMessage);
		if (null != jsonObject) {
			if (0 != jsonObject.getInt(Constant.ERRCODE)) {
				log.error("创建成员失败 errcode:{} errmsg:{}", jsonObject.getInt(Constant.ERRCODE), jsonObject.getString(Constant.ERRMSG));
			}  
		}  
	}
}
