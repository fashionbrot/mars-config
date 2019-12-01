package com.fashion.mars.ribbon.rule;

import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;

public interface IRule {

    /**
     * Choose a server from load balancer.
     * @return
     */
    Server choose(ILoadBalancer lb);

}
