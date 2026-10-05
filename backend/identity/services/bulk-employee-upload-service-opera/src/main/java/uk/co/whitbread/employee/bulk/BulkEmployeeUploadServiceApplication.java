package uk.co.whitbread.employee.bulk;

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
import org.springframework.context.annotation.Import;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.unleash.features.config.UnleashAutoConfiguration;
import uk.co.whitbread.shared.auth.EnableAuthorization;

@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableAuthorization
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
@Import(UnleashAutoConfiguration.class)
public class BulkEmployeeUploadServiceApplication {

	@Value("${swagger.api.version}")
	String swaggerApiVersion;

	@Bean
	public GroupedOpenApi openApiDocumentation(){
		return GroupedOpenApi.builder()
				.group("Api")
				.packagesToScan("uk.co.whitbread.employee.bulk")
				.pathsToMatch("/**")
				.build();
	}

	@Bean
	public GroupedOpenApi openApiInternalDocumentation(){
		return GroupedOpenApi.builder()
				.group("Internal")
				.pathsToMatch("/bulkemployeeupload")
				.packagesToExclude("uk.co.whitbread.employee.bulk")
				.build();
	}

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI().info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("Bulk Employee Upload API Documentation")
				.description("API Documentation")
				.version(swaggerApiVersion);
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
		marshaller.setPackagesToScan("uk.co.whitbread.shared.azureemail.genericEmail.api");
		return marshaller;
	}

	static void main(String[] args) {
		SpringApplication.run(BulkEmployeeUploadServiceApplication.class, args);
	}
}
