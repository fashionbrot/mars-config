package com.gitee.mars.common.util;

import com.gitee.mars.common.constant.MarsConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Cookie 工具类
 */
public final class CookieUtil {


    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String realName, String roleName, String token,boolean isBase64) {
        CookieUtil.setCookie(request, response, MarsConst.REAL_NAME,isBase64? Base64Utils.encodeToString(realName.getBytes()):realName,
                30 * 60, true,false);
//        CookieUtil.setCookie(request, response, MarsConst.ROLE_NAME,isBase64? Base64Utils.encodeToString(roleName.getBytes()):roleName, 30 * 60, true);
        CookieUtil.setCookie(request, response, MarsConst.AUTH_KEY, token, 30 * 60, true,true);
    }

    /**
     * 重写 Cookie的值,
     *
     * @param request
     * @param response
     * @param isDecoder
     */
    public static void rewriteCookie(HttpServletRequest request, HttpServletResponse response, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList != null) {
            String token = getCookieValue(cookieList, MarsConst.AUTH_KEY, true);
            String realName = getCookieValue(cookieList, MarsConst.REAL_NAME, true);
            String roleName = getCookieValue(cookieList, MarsConst.ROLE_NAME, true);
            if (StringUtils.isNotEmpty(token)) {
                Long userId = JwtTokenUtil.verifyTokenAndGetUser(token);
                if (userId != null) {
                    token = JwtTokenUtil.createToken(userId, realName, roleName);
                    if (StringUtils.isNotEmpty(token)) {
                        setCookie(request, response, realName, roleName, token,false);
                    }
                }
            }
        }
    }


    public static String getCookieValue(Cookie[] cookieList, String cookieName, boolean isDecoder) {
        if(cookieList==null || cookieList.length==0){
            return "";
        }
        for (int i = 0; i < cookieList.length; i++) {
            if (cookieList[i].getName().equals(cookieName)) {
                if (isDecoder) {
                    try {
                        return URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    return cookieList[i].getValue();
                }
            }
        }
        return "";
    }

    /**
     * 得到Cookie的值,
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                    } else {
                        retValue = cookieList[i].getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }




    /**
     * 设置Cookie的值 在指定时间内生效, 编码参数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge, boolean isEncode,boolean isHttpOnly) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, isEncode,isHttpOnly);
    }



    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response) {

        doSetCookie(request, response,MarsConst.AUTH_KEY, "", -1, false,false);
        doSetCookie(request, response,MarsConst.REAL_NAME, "", -1, false,false);
        doSetCookie(request, response,MarsConst.ROLE_NAME, "", -1, false,false);
    }


    /**
     * 设置Cookie的值，并使其在指定时间内生效
     *
     * @param cookieMaxAge cookie生效的最大秒数
     */
    private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                          String cookieName, String cookieValue, int cookieMaxAge, boolean isEncode,boolean isHttpOnly) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxAge > 0) {
                cookie.setMaxAge(cookieMaxAge);
            }
            if (null != request) {// 设置域名的cookie
                String domainName = getDomainName(request);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setHttpOnly(isHttpOnly);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 得到cookie的域名
     */
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;

        String serverName = request.getRequestURL().toString();
        if (serverName == null || "".equals(serverName)) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            serverName = serverName.substring(7);
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = "." + domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }

}