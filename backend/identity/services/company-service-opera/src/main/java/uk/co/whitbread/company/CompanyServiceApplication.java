package uk.co.whitbread.company;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.unleash.features.config.UnleashAutoConfiguration;
import uk.co.whitbread.shared.auth.EnableAuthorization;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableDiscoveryClient
@EnableAsync
@EnableAuthorization
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
@RequiredArgsConstructor
@Import(UnleashAutoConfiguration.class)
public class CompanyServiceApplication {

    @Value("${swagger.api.version}")
    String swaggerApiVersion;

    @Bean
    public GroupedOpenApi openApiDocumentation(){
        return GroupedOpenApi.builder()
            .group("Api")
            .packagesToScan("uk.co.whitbread.company")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi openApiInternalDocumentation(){
        return GroupedOpenApi.builder()
            .group("Internal")
            .pathsToMatch("/company")
            .packagesToExclude("uk.co.whitbread.company")
            .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Company API Documentation")
            .description("API Documentation")
            .version(swaggerApiVersion);
    }

    @Bean
    public WebServiceTemplate emailWebServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.co.whitbread.shared.azureemail.genericEmail.api");
        return marshaller;
    }

    public static void main(String[] args) {
        SpringApplication.run(CompanyServiceApplication.class, args);
    }

}
