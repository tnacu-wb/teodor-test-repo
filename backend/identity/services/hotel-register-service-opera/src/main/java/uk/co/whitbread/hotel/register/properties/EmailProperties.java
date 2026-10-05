package uk.co.whitbread.hotel.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private String companyActivationUrl;
    private String companyActivationUrlDe;
    private String innbCompanyActivationUrl;
    private String innbCompanyActivationUrlDe;
}
