package com.gitee.mars.console.config;

import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MarsException.class)
    @ResponseStatus(HttpStatus.OK)
    public RespVo wealthException(MarsException e) {
        return RespVo.fail(e.getMsg(), e.getCode());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public RespVo exception(Exception e) {
        log.error("exception error:",e);
        return RespVo.fail(RespCode.FAIL.getMsg());
    }

    /*@ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public RespResult validationException(ValidationException e) {
        return RespResult.fail(e.getMsg());
    }*/


}
