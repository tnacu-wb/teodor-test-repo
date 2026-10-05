package uk.co.whitbread.business.tether;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import uk.co.whitbread.shared.auth.EnableAuthorization;

@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
@EnableAuthorization
@EnableCaching
public class BusinessTetherServiceApplication {

	@Value("${swagger.api.version}")
	String swaggerApiVersion;

	@Bean
	public GroupedOpenApi swaggerApiDocumentation(){
		return GroupedOpenApi.builder()
				.group("Api")
				.packagesToScan("uk.co.whitbread.business.tether")
				.pathsToMatch("/**")
				.build();
	}

	@Bean
	public GroupedOpenApi swaggerInternalDocumentation(){
		return GroupedOpenApi.builder()
				.group("Internal")
				.packagesToExclude("uk.co.whitbread.business.tether")
				.pathsToMatch("/businesstether")
				.build();
	}

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI().info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("Business Tether Microservice API Documentation")
				.description("API Documentation")
				.version(swaggerApiVersion);
	}

	public static void main(String[] args) {
		SpringApplication.run(BusinessTetherServiceApplication.class, args);
	}
}
