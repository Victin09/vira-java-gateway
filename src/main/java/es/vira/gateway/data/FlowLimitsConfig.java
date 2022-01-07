package es.vira.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class FlowLimitsConfig {
    private boolean enabled;
    private int timeout;
    private int rate;
    private int maxSize;
}
