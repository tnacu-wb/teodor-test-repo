package uk.co.whitbread.feedback.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Data
public class ConfigProperties {

    @Value("${logging.request.time:false}")
    private Boolean logRequestTime;

    @Value("${request.timeout:15000}")
    private int requestTimeout;

    @Value("${crm.defaultFeedbackSource}")
    private String defaultFeedbackSource;
}
