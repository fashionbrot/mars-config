package com.gitee.mars.ribbon.ping;

import com.gitee.mars.ribbon.constants.GlobalConstants;
import com.gitee.mars.ribbon.enums.SchemeEnum;
import com.gitee.mars.ribbon.loadbalancer.Server;
import com.gitee.mars.ribbon.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Slf4j
public class PingUrl implements IPing {

    @Override
    public boolean isAlive(Server server) {
        boolean isSSL = server.getScheme() == SchemeEnum.HTTPS;
        String url = server.getServer()+"/";
        HttpClientUtil.HttpResult httpResult;
        try {
            httpResult = HttpClientUtil.httpGet(url,null,null, GlobalConstants.ENCODE,1000,isSSL,1000);
        } catch (Exception e) {
            log.error("isAlive error",e.getMessage());
            return false;
        }
        if (log.isDebugEnabled()){
            log.debug("isAlive url:{} result:{}",url,httpResult.toString());
        }
        if (httpResult.isSuccess()){
            return true;
        }
        return false;
    }

}
