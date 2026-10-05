package uk.co.whitbread.company.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import uk.co.whitbread.shared.auth.config.AuthConfiguration;

@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
@ComponentScan(
    basePackages = "uk.co.whitbread",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = AuthConfiguration.class
    )
)
public class TestApplication {
}

