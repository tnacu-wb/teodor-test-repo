package uk.co.whitbread.common.validators.password;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "passwords")
public class PasswordProperties {

    private Map<String, PasswordConfig> configs;

}
