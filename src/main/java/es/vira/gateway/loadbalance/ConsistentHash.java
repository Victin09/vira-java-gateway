package es.vira.gateway.loadbalance;

import es.vira.gateway.constant.LoadBalanceConstant;
import es.vira.gateway.mapping.Mapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * consistent hash
 *
 * @author Víctor Gómez
 * @since 2.1.0
 */
@Slf4j
public class ConsistentHash extends UrlMapping {
    /**
     * hash circle
     */
    private Map<String, Node> hashCircle;

    public ConsistentHash(Map<String, List<Mapper>> urlMapping) {
        super(urlMapping);
        // build hash circle
        for (Map.Entry<String, List<Mapper>> entry : urlMapping.entrySet()) {
            List<Mapper> mappers = entry.getValue();
            int sum = 0;
            for (Mapper mapper : mappers) {
                sum += mapper.getWeight();
            }
            List<Node> nodes = new LinkedList<>();
            for (Mapper mapper : mappers) {
                int count = LoadBalanceConstant.MAX_NODE_SIZE * mapper.getWeight() / sum;
                StringBuilder sb = new StringBuilder(mapper.getTarget()).append("#");
                // ensure it contains one node
                if (count < 1) {
                    count = 1;
                }
                for (int i = 0; i < count; ++i) {
                    int hashCode = sb.append(i).toString().hashCode();
                    nodes.add(new Node(mapper, hashCode));
                }
            }
            // sort
            Collections.sort(nodes);

            Map<String, Node> hashLoop = new HashMap<>(urlMapping.size() / 3 * 4);
            Node head = new Node();
            Node next = head;
            for (Node node : nodes) {
                next.next = node;
                next.next.prev = next;
                next = next.next;
            }
            head.next.prev = next;
            next.next = head.next;
            hashLoop.put(entry.getKey(), head);
            this.hashCircle = hashLoop;
        }
        log.info("ConsistentHash load completed.");
    }

    @Override
    public Mapper getLoadBalance(String name, String host, String ip) {
        int hashCode = ip.hashCode();
        Node node = hashCircle.get(name);
        if (node == null) {
            return null;
        }
        if (hashCode > LoadBalanceConstant.MID_INT) {
            node = node.prev;
            while (node.mapper != null) {
                if (node.hashCode > hashCode && node.mapper.isOnline()) {
                    break;
                }
                node = node.prev;
            }
        } else {
            node = node.next;
            while (node.mapper != null) {
                if (node.hashCode < hashCode && node.mapper.isOnline()) {
                    break;
                }
                node = node.next;
            }
        }
        return node.mapper;
    }

    /**
     * node
     */
    @NoArgsConstructor
    private class Node implements Comparable<Node> {
        /**
         * mapper
         */
        private Mapper mapper;
        /**
         * hash code
         */
        private Integer hashCode;
        /**
         * post node
         */
        private Node next;
        /**
         * pre node
         */
        private Node prev;

        private Node(Mapper mapper, Integer hashCode) {
            this.mapper = mapper;
            this.hashCode = hashCode;
        }

        @Override
        public int compareTo(Node o) {
            return hashCode.compareTo(o.hashCode);
        }
    }
}
