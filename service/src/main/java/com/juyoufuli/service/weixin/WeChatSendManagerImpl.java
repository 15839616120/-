package com.juyoufuli.service.weixin;

import com.juyoufuli.common.config.Constant;
import com.juyoufuli.service.weixin.pojo.Text;
import com.juyoufuli.service.weixin.pojo.TextMessage;
import com.juyoufuli.service.weixin.service.SendMessageBean;
import com.juyoufuli.service.weixin.util.WeiXinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JC2
 */
@Component
public class WeChatSendManagerImpl implements WeChatSendManager {

    @Value("${wechat.properties.appid}")
    private String          appid;
    @Value("${wechat.properties.appsecret}")
    private String          appsecret;
    @Autowired
    private SendMessageBean sendMessageBean;
    @Override
    public void sendTextMessage(String content) {
        TextMessage message=new TextMessage();
        message.setTouser(Constant.WECHAT_CONTENT_TOUSER);
        //message.setToparty(Constant.WECHAT_CONTENT_TOPARTY);
        //message.setTouser("LiangJinChao");
        message.setMsgtype(Constant.WECHAT_CONTENT_MSGTYPR);
        message.setAgentid(Constant.WECHAT_CONTENT_AGENTID);

        Text text=new Text();
        text.setContent(content);
        message.setText(text);
        message.setSafe(Constant.WECHAT_CONTENT_SAFE);
        String accessToken= WeiXinUtil.getAccessToken(appid,appsecret).getToken();
        sendMessageBean.sendMessage(accessToken, message);
    }
}
