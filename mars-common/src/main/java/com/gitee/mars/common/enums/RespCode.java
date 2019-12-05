package com.gitee.mars.common.enums;

public enum RespCode {

    SUCCESS(0, "成功"),
    FAIL(1,"网络繁忙，请稍后重试"),


    NEED_LOGIN(10,"请重新登录"),
    PARAM_MISSING(11,"请求丢失参数"),
    USER_OR_PASSWORD_IS_ERROR(12,"用户名或者密码错误,请重新输入"),
    USER_NOT_EXIST(13,"用户不存在"),
    SIGNATURE_MISMATCH(14, "签名验证失败"),
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RespCode:[").append(code).append(":").append(msg).append("]");
        return sb.toString();
    }
}
