package com.fashion.mars.ribbon.rule;

import com.fashion.mars.ribbon.AbstractLoadBalancerRule;
import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundRobinRule extends AbstractLoadBalancerRule {

    private AtomicInteger nextServerCyclicCounter;

    public RoundRobinRule() {
        nextServerCyclicCounter = new AtomicInteger(0);
    }

    @Override
    public Server choose(ILoadBalancer lb) {
        if (lb == null) {
            log.warn("no load balancer");
            return null;
        }

        Server server = null;
        int count = 0;
        while (server == null && count++ < 10) {
            List<Server> allServers = lb.getAllServers();
            int serverCount = allServers.size();

            if ( serverCount == 0) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            server = allServers.get(nextServerIndex);

            if (server!=null && lb.getPing().isAlive(server)){
                return server;
            }
        }

        if (count >= 10) {
            log.warn("No available alive servers after 10 tries from load balancer: "
                    + lb);
        }
        return server;
    }


    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}
