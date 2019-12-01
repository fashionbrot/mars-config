package com.fashion.mars.ribbon.ping;

import com.fashion.mars.ribbon.loadbalancer.Server;

/**
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
