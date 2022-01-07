package es.vira.gateway.mapping;

import es.vira.gateway.constant.LoadBalanceConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * proxy mapper
 *
 * @author Víctor Gómez
 * @since 1.4.0
 */
@ToString
@Slf4j
public class Mapper {
    /**
     * host
     */
    @Getter
    private String name;
    /**
     * target url
     */
    @Getter
    private String target;
    /**
     * weight
     */
    @Getter @Setter
    private int weight;
    /**
     * exception count
     */
    private AtomicInteger exceptionCount;

    public Mapper(String name, String target, Integer weight) {
        this.name = name;
        this.target = target;
        // weight default 100
        this.weight = weight == null || weight < 0 ? 100 : weight;
        this.exceptionCount = new AtomicInteger(0);
    }

    /**
     * rest exception count
     */
    public void restExceptionCount() {
        exceptionCount.set(0);
    }

    /**
     * target service unreachable
     *
     * @return error service
     */
    public String exception() {
        exceptionCount.incrementAndGet();
        if (!isOnline()) {
            log.error("{} offline.", target);
            // put it to survival check list
            SurvivalChecker.add(this);
        }
        return target;
    }

    @Override
    public int hashCode() {
        return (name + "#" + target).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mapper) {
            Mapper m = (Mapper) obj;
            return target.equals(m.target) && name.equals(m.target);
        }
        return false;
    }

    /**
     * check for mapper health
     *
     * @return online or offline
     */
    public boolean isOnline() {
        return exceptionCount.get() < LoadBalanceConstant.OFFLINE_COUNT;
    }
}
