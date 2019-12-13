package com.github.fashionbrot.ribbon;

import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.ping.PingUrl;
import com.github.fashionbrot.ribbon.rule.RandomRule;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        ILoadBalancer loadBalancer =new BaseLoadBalancer();
        loadBalancer.setRule(new RandomRule());
        loadBalancer.setPing(new PingUrl());
        loadBalancer.addServers(Arrays.asList(Server.builder().host("www.baidu.com").port(80).scheme(SchemeEnum.HTTP).build()
                ,Server.builder().host("blog.csdn.net").port(80).path("health").scheme(SchemeEnum.HTTP).build()));

        for (int i=0 ;i<100;i++) {
            Server server = loadBalancer.chooseServer();
            if (server==null){
                System.out.println("server is null");
            }else {
                System.out.println(server.toString() + "i:" + i);
            }
        }

    }

}
