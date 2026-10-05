package uk.co.whitbread.hotel.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.EnableAuthorization;
import uk.co.whitbread.shared.auth.config.ManagementServiceCondition;
import uk.co.whitbread.shared.auth.service.ManagementService;

@EnableDiscoveryClient
@EnableAuthorization
@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
public class HotelLoginApplication {

    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    public static void main(String[] args) {
        SpringApplication.run(HotelLoginApplication.class, args);
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.co.whitbread.bart.registeredguest.api", "uk.co.whitbread.bart.booking.api",
                "uk.co.whitbread.bart.business.api", "uk.co.whitbread.bart.auth0.api", "uk.co.whitbread.bart.business.auth0.api");
        return marshaller;
    }

    @Bean
    @Conditional(ManagementServiceCondition.class)
    public ManagementService leisureManagementService(Auth0Properties managementProperties) {
        managementProperties.setConnection(managementProperties.getB2cConnection());
        return new ManagementService(managementProperties);
    }

    @Bean
    @Conditional(ManagementServiceCondition.class)
    public ManagementService businessManagementService(Auth0Properties managementProperties) {
        managementProperties.setConnection(managementProperties.getB2bConnection());
        return new ManagementService(managementProperties);
    }
}
