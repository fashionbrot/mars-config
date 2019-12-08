package com.gitee.mars.console.interceptor;


import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.constant.MarsConst;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.util.CookieUtil;
import com.gitee.mars.common.util.JwtTokenUtil;
import com.gitee.mars.core.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class Interceptor implements HandlerInterceptor {

    @Autowired
    private MenuService menuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String  authValue  = CookieUtil.getCookieValue(request,MarsConst.AUTH_KEY,false);
        if (!StringUtils.isEmpty(authValue)){
            Long userId  = JwtTokenUtil.verifyTokenAndGetUser(authValue);
            if (userId==null){
                throw new MarsException(RespCode.SIGNATURE_MISMATCH);
            }
            if(!menuService.checkPermissionUrl(handler,request)){
                response.sendRedirect(url(request)+"/401");
            }
            return true;
        }

        String header=request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(header)) {
            response.setHeader("REQUIRE_AUTH","true");
            return false;
        }else {
            response.sendRedirect(url(request)+"/user/login");
        }
        return true;
    }

    public String url(HttpServletRequest request){
        String path = request.getContextPath();
        return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        menuService.loadMenuList(handler,request);
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(IsMenu.class)) {
                CookieUtil.rewriteCookie(request, response, true);
            }
        }
    }

}
