package uk.co.whitbread.hotel.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "register")
@Data
public class RegisterProperties {

    private String secretKey;
    private String salt;
    private String staticIV;
}
