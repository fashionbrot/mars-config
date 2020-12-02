package com.github.fashionbrot.common.util;

import java.util.ArrayList;
import java.util.List;

public class ServerUtil {



    public static List<String> getServerList(String serverAddress,String url){

        String[] server = serverAddress.split(",");
        List<String> serverList=new ArrayList<>(server.length);
        if (StringUtil.isNotEmpty(serverAddress)) {
            for (String s : server) {
                String[] svr = s.split(":");
                int port = 80;
                if (svr.length == 2) {
                    port = StringUtil.parseInteger(svr[1], 80);
                }
                String ip = svr[0];
                if (!ip.startsWith("http")){
                    ip="http://"+ip;
                }
                ip = ip+":"+port+"/api/config/value/cluster/sync";
                serverList.add(ip);
            }
            return serverList;
        }
        return serverList;
    }

}
