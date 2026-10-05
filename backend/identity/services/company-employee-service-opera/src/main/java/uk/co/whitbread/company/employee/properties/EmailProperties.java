package uk.co.whitbread.company.employee.properties;

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

    private String employeeActivationUrl;
    private String employeeActivationUrlDe;
    private String loginUrl;
    private String loginUrlDe;
    private String innbCompanyActivationUrl;
    private String innbCompanyActivationUrlDe;
    private String innbEmployeeActivationUrl;
    private String innbEmployeeActivationUrlDe;
}
