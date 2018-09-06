package com.juyoufuli.common.config;

public enum BusinessEnum {

    UNKNOWN_EXCEPTION(-99, "未知异常"),
    INNER_EXCEPTION(-100, "内部异常"),
    OK(0, "success"),
    UNKNOWN(500, "未知异常"),
    TOKEN_EXPIRED_ERROR(-401, "令牌已过期,请重新登录."),
    ACCOUNT_NOT_NULL(-101, "参数错误：账户不能为空"),
    IMPORT_DATA_NULL(-101, "参数错误：导入数据不能为空"),
    STAFF_USERNAME_NULL(-101, "参数错误：姓名不能为空"),
    GENDER_VERIFY_ERROR(-101, "参数错误：性别输入有误"),
    ID_CARD_NOT_NULL(-101, "参数错误：身份证号码不能为空"),
    ID_CARD_VERIFY_ERROR(-101, "参数错误：身份证号码输入有误"),
    ID_CARD_VERIFY_EXIST(-101, "参数错误：身份证号码已存在"),
    JOB_NUM_NOT_NULL(-101, "参数错误：员工编号不能为空"),
    JOB_NUM_VERIFY_ERROR(-101, "参数错误：员工编号输入有误"),
    PHONE_NOT_NULL(-101, "参数错误：手机号码不能为空！"),
    PHONE_VERIFY_ERROR(-101, "参数错误：手机号码输入有误"),
    TEL_VERIFY_ERROR(-101, "参数错误：座机号码输入有误"),
    EMAIL_NOT_NULL(-101, "参数错误：邮箱不能为空"),
    EMAIL_VERIFY_ERROR(-101, "参数错误：邮箱输入有误"),
    GROUPS_VERIFY_ERROR(-101, "参数错误：所属组传入有误"),
    PWD_NOT_NULL(-101, "参数错误：密码不能为空"),
    DUTY_NOT_NULL(-101, "参数错误：职务不能为空"),
    STAFF_STATE_NOT_NULL(-101, "参数错误：状态不能为空"),
    STAFF_STATE_ERROR(-101, "参数错误：状态格式正确"),
    AVATAR_NOT_NULL(-101, "参数错误：头像不能为空"),
    AVATAR_VERIFY_ERROR(-101, "参数错误：头像格式不正确"),
    STAFF_GROUP_IS_NULL(-101, "员工组不存在"),
    STAFF_GROUP_RDB_NO_CHANGE(-101, "同步数据到mysql:无更新数据"),
    STAFF_GROUP_NO_TYPE(-101, "参数错误：请传入正确的状态值"),
    STAFF_GROUP_NOT_CHANGE(-101, "未检测到更改"),
    STAFF_GROUP_NOT_DEL_NOT_NULL(-101, "无法删除非空组"),
    STAFF_GROUP_ADD_ERROR(-101,"员工编组插入失败"),
    STAFF_GROUP_DATA_IS_NULL(-101,"组数据为空"),
    STAFF_GROUP_NAME_REPETITION(-101,"同一节点下组名重复"),
    STAFF_GROUP_UPDATE_ERROR(-101,"员工编组修改失败"),
    STAFF_GROUP_ID_ERROR(-101,"参数错误：请传入正确的组id"),
    STAFF_GROUP_STATUS_IS_ERROR(-101,"组状态异常"),
    STAFF_GROUPP_CHILD_STAFF_IS_NULL(-101,"组下员工为空"),
    STAFF_GROUP_CHILD_NODE_IS_NULL(-101,"组子节点为空"),
    STAFF_GROUP_STATE_ERROR(-101,"参数错误：请传入正确的状态值"),
    STAFF_GROUP_NAME_ERROR(-101,"参数错误：组名不为空!"),
    STAFF_GROUP_PARENT_ID_ERROR(-101,"参数错误：请传入正确的父ID"),
    STAFF_GROUP_PARENT_DATA_IS_NULL(-101,"组父数据为空"),
    STAFF_GROUP_U_IS_NULL(-101,"参数错误：U不能为空"),
    STAFF_GROUP_ID_IS_NULL(-101,"参数错误：组ID不能为空"),
    STAFF_GROUP_SYNC_ERROR(-101,"组同步失败"),
    STAFF_ACCOUNT_EXISTS(-101, "参数错误：员工已存在"),
    STAFF_STAFFID_IS_NULL(-101, "员工工号不存在"),
    STAFF_SAFECODE_EXISTS(-101, "参数错误：安全码不能为空"),
    STAFF_SAFECODE_ERROR(-101, "参数错误：安全码错误"),
    STAFF_BIRTHDAY_ERROR(-101, "参数错误：生日格式不正确"),
    STAFF_NOT_ACCOUNT(-101, "帐号不存在"),
    STAFF_EMAIL_NULL(-101, "账号没有填写邮箱"),
    STAFF_NOT_USERINFO(-101, "用户不存在"),
    STAFF_PWD_INCORRECT(-102, "密码不正确"),
    STAFF_TOKEN_INCORRECT(-103, "令牌不正确"),
    STAFF_TOKEN_INVALID(-104, "令牌失效"),
    STAFF_REQUEST_METHOD_IS_NULL(-101, "请求方法类型为空"),
    STAFF_TOKEN_STALE(-105, "令牌已过期"),
    STAFF_TOKEN_NOT_SAME(-105, "令牌与平台不一致"),
    STAFF_NOT_LOGIN(-106, "用户未登录"),
    STAFF_LOGIN_PASSWORD_NULL(-107,"登录密码为空"),
    STAFF_LOGIN_PLATFORM_NULL(-108,"平台为空"),
    STAFF_LOGIN_ACCOUNT_NULL(-109,"登录账号为空"),
    STAFF_LOGIN_U_NULL(-110,"登录U盾为空"),
    STAFF_LOGIN_V_NULL(-111,"登录V盾为空"),
    STAFF_GROUP_RETAIN_ONE(-112,"员工所属组至少保留一个");

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    BusinessEnum(int code, String msg) {
        this.code= code;
        this.msg= msg;
    }

    public static BusinessEnum getEnum(int code) {
        for (BusinessEnum ele : BusinessEnum.values()) {
            if (ele.getCode() == code) {
                return ele;
            }
        }
        return null;
    }


}
