package com.gitee.mars.console.config;

import com.gitee.mars.common.annotation.NoReturnValue;
import com.gitee.mars.common.vo.RespVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice {


    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return !methodParameter.hasMethodAnnotation(NoReturnValue.class);
    }

    @Override
    public Object beforeBodyWrite(Object value, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        RespVo vo;
        if (value instanceof RespVo){
            vo = (RespVo) value;
        }else{
            vo = RespVo.success(value);
        }
        return vo;
    }
}
