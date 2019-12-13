package com.github.fashionbrot.ribbon.ping;

import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
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
