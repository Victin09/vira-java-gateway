package es.vira.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Config {
        private String mode;
        private HttpConfig http;
        private HttpsConfig https;
        private ThreadPoolConfig threadPool;
        private MappingConfig mapping;
        private StaticConfig staticContent;
        private CorsConfig cors;
        private FlowLimitsConfig flowLimits;
        private FilterConfig filter;

//        "consul": {
//        "enable": true,
//                "host": "192.168.0.236:8500",
//                "mapping": {
//            "test": [
//            "localhost:8080"
//      ]
//        }
//    },
//        "zk": {
//        "enable": true,
//                "host": "127.0.0.1:2181",
//                "mapping": {
//            "test": [
//            "localhost:8080"
//      ]
//        }
//    }
//    }
}
