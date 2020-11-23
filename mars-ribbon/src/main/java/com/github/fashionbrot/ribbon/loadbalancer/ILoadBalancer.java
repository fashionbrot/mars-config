package com.github.fashionbrot.ribbon.loadbalancer;

import com.github.fashionbrot.ribbon.ping.IPing;
import com.github.fashionbrot.ribbon.rule.IRule;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public interface ILoadBalancer {


    /**
     * Initial list of servers.
     * This API also serves to add additional ones at a later time
     * The same logical server (host:port) could essentially be added multiple times
     * (helpful in cases where you want to give more "weightage" perhaps ..)
     *
     * @param newServers new servers to add
     */
    void addServers(List<Server> newServers);

    void setServer(String serverAddress,String healthUrl);

    /**
     * @return All known servers, both reachable and unreachable.
     */
    List<Server> getAllServers();

    Server chooseServer();

    void setRule(IRule rule);

    IRule getRule();

    void setPing(IPing ping);

    IPing getPing();
}
