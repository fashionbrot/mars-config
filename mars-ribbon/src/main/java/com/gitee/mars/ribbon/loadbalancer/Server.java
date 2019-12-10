package com.gitee.mars.ribbon.loadbalancer;

import com.gitee.mars.ribbon.constants.GlobalConstants;
import com.gitee.mars.ribbon.enums.SchemeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Server {

    private String host;

    private int port = 80;

    private SchemeEnum scheme;

    private String path ;

    public String getServer(){

        if (scheme==SchemeEnum.HTTP){
            if (path == null || path.length()==0){
                return GlobalConstants.HTTP_SCHEME+host+":"+port;
            }
            return GlobalConstants.HTTP_SCHEME+host+":"+port + "/"+path;
        }else{
            if (path == null || path.length()==0) {
                return GlobalConstants.HTTPS_SCHEME + host + ":" + port;
            }
            return GlobalConstants.HTTPS_SCHEME + host + ":" + port + "/" + path;
        }
    }

    public String getServerIp(){
        return this.host+":"+port;
    }


}
