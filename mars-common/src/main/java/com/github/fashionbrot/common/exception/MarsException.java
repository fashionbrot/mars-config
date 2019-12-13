package com.github.fashionbrot.common.exception;


import com.github.fashionbrot.common.enums.RespCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarsException extends RuntimeException {

    private int code;
    private String msg;


    public MarsException(String msg){
        super(msg);
        this.code = RespCode.FAIL.getCode();
        this.msg = msg;
    }

    public MarsException(int code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public MarsException(RespCode respCode){
        super(respCode.getMsg());
        this.code = respCode.getCode();
        this.msg = respCode.getMsg();
    }

    public MarsException(RespCode respCode,String msg){
        super(respCode.getMsg());
        this.code = respCode.getCode();
        this.msg = msg+respCode.getMsg();
    }

}
