package com.gitee.mars.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
public class SystemUtil {

    public static final String LOCAL_IP = getHostAddress();

    public static final long IP_LAST_POINT = getLastPoint();

    public static final long getLastPoint(){
        String[] points = LOCAL_IP.split("\\.");
        long longPoint = sum(points);

        return longPoint/31;
    }

    public static final long getLastPoint(String ip){
        String[] points = ip.split("\\.");
        long longPoint = sum(points);

        return longPoint/31;
    }

    private static long sum(String[] longPoint){
        if (longPoint!=null){
            long temp = 0;
            for (String p : longPoint){
                temp += Long.valueOf(p);
            }
            return temp;
        }
        return 1;
    }



    private static String getHostAddress() {
        String address = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ip = inetAddresses.nextElement();
                    // 兼容不规范网段
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("get local host address error", e);
        }

        return address;
    }

    public static void main(String[] args) {
        System.out.println(SystemUtil.getHostAddress());
        System.out.println(getLastPoint("172.16.18.197"));
        System.out.println(getLastPoint());
    }

}
