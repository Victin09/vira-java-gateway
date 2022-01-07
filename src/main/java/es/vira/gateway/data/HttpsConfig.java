package es.vira.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class HttpsConfig {
    private boolean enabled;
    private int port;
    private String keyPwd;
    private String keyPath;
}
