package com.fashion.mars.ribbon.loadbalancer;

import com.fashion.mars.ribbon.ping.IPing;
import com.fashion.mars.ribbon.rule.IRule;

import java.util.List;

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
