package uk.co.whitbread.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "softbundles")

public class SoftBundlesProperties {

  private List<String> nonMealPackageIds = new ArrayList<>();
}

