package com.fashion.mars.ribbon.ping;

import com.fashion.mars.ribbon.constants.GlobalConstants;
import com.fashion.mars.ribbon.enums.SchemeEnum;
import com.fashion.mars.ribbon.loadbalancer.Server;
import com.fashion.mars.ribbon.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PingUrl implements IPing {

    @Override
    public boolean isAlive(Server server) {
        boolean isSsl = server.getScheme() == SchemeEnum.HTTPS;
        String url = server.getServer()+"/";
        HttpClientUtil.HttpResult httpResult;
        try {
            httpResult = HttpClientUtil.httpGet(url,null,null, GlobalConstants.ENCODE,1000,isSsl,1000);
        } catch (Exception e) {
            log.error("isAlive error",e);
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
