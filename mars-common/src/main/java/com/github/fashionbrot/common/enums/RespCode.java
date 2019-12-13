package com.github.fashionbrot.common.enums;

public enum RespCode {

    SUCCESS(0, "成功"),
    FAIL(1,"网络繁忙，请稍后重试"),


    NEED_LOGIN(10,"请重新登录"),
    PARAM_MISSING(11,"请求丢失参数"),
    USER_OR_PASSWORD_IS_ERROR(12,"用户名或者密码错误,请重新输入"),
    USER_NOT_EXIST(13,"用户不存在"),
    SIGNATURE_MISMATCH(14, "签名验证失败"),

    SAVE_ERROR(100,"保存失败,请稍后再试"),
    UPDATE_ERROR(101,"更新失败,请稍后再试"),
    DELETE_ERROR(102,"删除失败,请稍后再试"),

    EXIST_ERROR(200,"已存在"),

    NOT_PERMISSION_ERROR(300,"无权限操作，请开通权限后再来操作"),
    HISTORY_CONFIG_NOT_EXIST(301,"历史配置不存在，请刷新重试"),
    HISTORY_ROLL_BACK_ERROR(302,"你要回滚的文件已删除,不能回滚"),
    ENV_NOT_EXIST(303,"环境不存在"),
    PUBLISH_ERROR(304,"发布失败"),
    ROLL_BACK_ERROR(305,"回滚失败，请稍后再试"),
    ;

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    RespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RespCode:[").append(code).append(":").append(msg).append("]");
        return sb.toString();
    }
}
