package uk.co.whitbread.piba.account.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "worldline.piba.service"
)
@Data
public class WorldLineMockProperties {
    private String urlmock;
}
