package com.github.fashionbrot.ribbon.ping;

import com.github.fashionbrot.ribbon.loadbalancer.Server;


/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/8 22:45
 * Interface that defines how we "ping" a server to check if its alive
 */
public interface IPing {

    /**
     * Checks whether the given <code>Server</code> is "alive" i.e. should be
     * considered a candidate while loadbalancing
     *
     */
    boolean isAlive(Server server);



}
