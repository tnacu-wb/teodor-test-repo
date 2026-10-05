package uk.co.whitbread.shared.azureemail.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "azure.pti-email-service")
public class PTIEmailProperties extends AzureEmailProperties {

}
