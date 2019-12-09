package com.fashion.mars.ribbon.rule;

import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public interface IRule {

    /**
     * Choose a server from load balancer.
     * @return
     */
    Server choose(ILoadBalancer lb);

}
