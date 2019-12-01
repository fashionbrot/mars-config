package com.fashion.mars.ribbon.rule;

import com.fashion.mars.ribbon.AbstractLoadBalancerRule;
import com.fashion.mars.ribbon.loadbalancer.BaseLoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Random;

@Slf4j
public class RandomRule extends AbstractLoadBalancerRule {

    Random rand;


    public RandomRule() {
        rand = new Random();
    }

    @Override
    public Server choose(ILoadBalancer lb) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        int count =0;
        while (server == null) {
            if (count>10){
                return null;
            }
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = rand.nextInt(serverCount);
            server = allList.get(index);
            count++;
            if (server != null && lb.getPing().isAlive(server)) {
                return (server);
            }
        }

        return server;
    }
}
