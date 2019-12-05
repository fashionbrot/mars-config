package com.gitee.mars.console.interceptor;


import com.gitee.mars.common.constant.MarsConst;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String  authValue  = request.getHeader(MarsConst.AUTH_KEY);
        if (StringUtils.isEmpty(authValue)){
            throw new MarsException(RespCode.NEED_LOGIN);
        }
        Integer userId  = JwtTokenUtil.verifyTokenAndGetUser(authValue);
        if (userId==null){
            throw new MarsException(RespCode.SIGNATURE_MISMATCH);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

}
