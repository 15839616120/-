package com.juyoufuli.common.config;

public class Constant {

    /**
     * 状态标识（0：删除；1：正常；）
     */
    public static final int DEL_FLAG_DELETE = 0;
    public static final int DEL_FLAG_NORMAL = 1;

    /**
     * 短信发送状态 1：等待回执，2：发送失败，3：发送成功
     */
    public static final int SMS_STATE_WAIT = 1;
    public static final int SMS_STATE_FAIL = 2;
    public static final int SMS_STATE_SUCCESS = 3;

    /**
     * 平台标识（1：用户中心；）
     */
    public static final int PLATFORM_USERCENTER_PARAM = 1;

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 业务标识（1：注册；2：验证码）
     */
    public static final int BUSINESS_REG_PARAM = 1;
    public static final int BUSINESS_VCODE_PARAM = 2;

    /**
     * 短信模板
     */
    public static final long   SMS_TEMPLATE_TEST = 126260129;
    /**
     * 组分隔符
     */
    public static final String GROUP_SEPARATOR   = "/";

    /**
     * u值
     */
    public static final String AUTHORIZATION_U_VALUE = "u";

    /**
     * 全局员工ID
     */
    public static final String TOKEN_AUTH_STAFF_ID = "CURRENT_AUTH_STAFF_ID";

    /**
     * 员工编号前缀
     */
    public static final String STAFF_JOB_NUM_PREFIX = "0711";

    /**
     * 员工编号自增
     */
    public static final String STAFF_INFO_INDEX = "staff_info_index";

    /**
     * 员工信息前缀
     */
    public static final String STAFF_INFO = "staff_info";

    /**
     * 组信息前缀
     */
    public static final String STAFF_GROUP = "staff_group";

    /**
     * 组名字与组ID关系
     */
    public static final String STAFF_GROUP_NAME_ = "staff_group_name_";
    /**
     * 组自增ID
     */
    public static final String STAFF_GROUP_INDEX = "staff_group_index";
    /**
     * 组对应工号前缀
     */
    public static final String STAFF_GROUP_RELATION = "staff_group_relation_";
    /**
     * 需要同步的组ID
     */
    public static final String STAFF_GROUP_SYNC      = "staff_group_sync";

    /**
     * 同步员工前缀
     */
    public static final String STAFF_SYNC = "staff_sync";

    /**
     * 员工登录令牌前缀
     */
    public static final String STAFF_LOGIN_U = "staff_login_u_";

    /**
     * 员工登录令牌前缀
     */
    public static final String STAFF_LOGIN_V = "staff_login_v_";

    /**
     * 员工登录令牌前缀
     */
    public static final String STAFF_LOGIN_V_ID = "staff_login_v_id_";
    /**
     * 员工登录令牌前缀
     */
    public static final String STAFF_LOGIN = "staff_login_";

    /**
     * 帐号信息前缀
     */
    public static final String STAFF_ACCOUNT = "staff_account_";

    /**
     * 身份证信息前缀
     */
    public static final String STAFF_ID_NUM = "staff_id_num_";

    /**
     * 安全码前缀
     */
    public static final String STAFF_SAFECODE = "staff_safecode_";

    /**
     * 员工中心服务名
     */
    public static final String STAFFCENTER_SERVICE_NAME = "staffcenter-service-provider";
    /**
     * 员工组排序升序
     */
    public static final String STAFF_GROUP_RISE = "staff_group_rise";
    /**
     * 员工组排序降序
     */
    public static final String STAFF_GROUP_DESC = "staff_group_desc";
    /**
     * 企业微信消息推送配置
     */
    public static final String ERRCODE = "errcode";
    public static final String ERRMSG = "errmsg";
    public static final String WECHAT_SENDMESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
    public static final String WECHAT_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String WECHAT_REQUEST_METHOD = "POST";
    public static final String WECHAT_CONTENT_TOUSER  = "LiangJinChao|ZhangHongWei|ZhaoLei|WuYiZhe";
    public static final String WECHAT_CONTENT_TOPARTY = "7";
    public static final String WECHAT_CONTENT_MSGTYPR = "text";
    public static final int WECHAT_CONTENT_AGENTID = 1000003;
    public static final int WECHAT_CONTENT_SAFE = 0;


}
