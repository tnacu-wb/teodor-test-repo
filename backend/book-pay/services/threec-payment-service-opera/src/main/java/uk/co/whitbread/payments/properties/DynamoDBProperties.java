package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "amazon.dynamodb")
@Configuration
@Data
public class DynamoDBProperties {

    private String endpoint;
    private String tableName;
    private String accessKey;
    private String secretKey;
    private int maxConcurrency;
    private int maxPendingConnectionAcquires;
    private int connectionAcquisitionTimeout;
    private int connectionTimeout;
    private int readTimeout;
    private int apiCallAttemptTimeout;
}
