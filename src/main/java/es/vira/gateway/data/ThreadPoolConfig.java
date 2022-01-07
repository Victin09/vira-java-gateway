package es.vira.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ThreadPoolConfig {
    private int core;
    private int max;
    private int timeout;
}
