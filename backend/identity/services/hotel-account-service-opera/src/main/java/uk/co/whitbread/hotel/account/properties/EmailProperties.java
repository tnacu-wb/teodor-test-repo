package uk.co.whitbread.hotel.account.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private String piResetPasswordUrl;
    private String innBusinessResetPasswordUrl;
    private String innBusinessResetPasswordUrlDe;
}
