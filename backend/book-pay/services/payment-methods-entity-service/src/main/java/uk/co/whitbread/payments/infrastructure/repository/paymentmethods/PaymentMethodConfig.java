package uk.co.whitbread.payments.infrastructure.repository.paymentmethods;

import static java.util.Objects.requireNonNull;

import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.Assert;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out.DefaultPaymentMethodsDto;

@Configuration
@PropertySource(value = "classpath:paymentMethods.yml",
    factory = PaymentMethodConfig.YamlPropertySourceFactory.class)
public class PaymentMethodConfig {

  @Bean(name = "main")
  @ConfigurationProperties(prefix = "configuration")
  public DefaultPaymentMethodsDto defaultPaymentMethods() {
    return new DefaultPaymentMethodsDto();
  }

  @Bean(name = "ccui")
  @ConfigurationProperties(prefix = "configuration-ccui")
  public DefaultPaymentMethodsDto defaultPaymentCcuiMethods() {
    return new DefaultPaymentMethodsDto();
  }

  static class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public org.springframework.core.env.PropertySource<?> createPropertySource(String name,
        EncodedResource encodedResource) {
      YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
      factory.setResources(encodedResource.getResource());

      Properties properties = factory.getObject();
      var fileName = requireNonNull(encodedResource.getResource().getFilename());

      Assert.notNull(properties, "Property source must not be null");
      return new PropertiesPropertySource(fileName, properties);
    }
  }
}