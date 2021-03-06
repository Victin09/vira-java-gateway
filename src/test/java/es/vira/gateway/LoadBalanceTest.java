package es.vira.gateway;

import es.vira.gateway.loadbalance.ConsistentHash;
import es.vira.gateway.loadbalance.LeastActiveLoadBalance;
import es.vira.gateway.loadbalance.RandomLoadBalance;
import es.vira.gateway.loadbalance.RoundRobinLoadBalance;
import es.vira.gateway.mapping.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * load balance test
 *
 * @author Víctor Gómez
 */
public class LoadBalanceTest {
    /**
     * consistent hash
     */
    private ConsistentHash consistentHash;
    /**
     * random load balance
     */
    private RandomLoadBalance randomLoadBalance;
    /**
     * least active load balance
     */
    private LeastActiveLoadBalance leastActiveLoadBalance;
    /**
     * round robin load balance
     */
    private RoundRobinLoadBalance roundRobinLoadBalance;

    @Before
    public void setup() {
        Map<String, List<Mapper>> urlMapping = new HashMap<>();
        urlMapping.put("127.0.0.1:8080", Arrays.asList(
                new Mapper("127.0.0.1:8080", "123.125.115.110:80", 200),
                new Mapper("127.0.0.1:8080", "220.181.57.216:80", 100)));
        consistentHash = new ConsistentHash(urlMapping);
        randomLoadBalance = new RandomLoadBalance(urlMapping);
        leastActiveLoadBalance = new LeastActiveLoadBalance(urlMapping);
        roundRobinLoadBalance = new RoundRobinLoadBalance(urlMapping);
    }

    @Test
    public void testConsistentHash() {
        Assert.assertEquals("123.125.115.110:80",
                consistentHash.getLoadBalance("test", "127.0.0.1:8080", "::0").getTarget());
    }

    @Test
    public void testRandomLoadBalance() {
        Assert.assertNotNull(randomLoadBalance.getLoadBalance("test", "127.0.0.1:8080", "::0"));
    }

    @Test
    public void testLeastActiveLoadBalance() {
        Assert.assertNotNull(leastActiveLoadBalance.getLoadBalance("test", "127.0.0.1:8080", "::0"));
    }

    @Test
    public void testRoundRobinLoadBalance() {
        Assert.assertNotNull(roundRobinLoadBalance.getLoadBalance("test", "127.0.0.1:8080", "::0"));
    }
}
