package uk.co.whitbread.employee.bulk.properties;

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
  private String innBusinessEmployeeActivationUrl;
  private String innBusinessEmployeeActivationUrlDe;

}
