package uk.co.whitbread.company.employee;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
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

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "uk.co.whitbread")
@EnableAuthorization
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
@Import(UnleashAutoConfiguration.class)
public class CompanyEmployeeServiceApplication {

	private final String swaggerApiVersion;

	public CompanyEmployeeServiceApplication(@Value("${swagger.api.version}") String swaggerApiVersion) {
		this.swaggerApiVersion = swaggerApiVersion;
	}

	@Bean
	public GroupedOpenApi openApiDocumentation() {
		return GroupedOpenApi.builder()
				.group("Api")
				.packagesToScan("uk.co.whitbread.company.employee")
				.pathsToMatch("/**")
				.addOperationCustomizer(operationCustomizer())
				.addOpenApiCustomizer(openApiCustomizer())
				.build();
	}

	@Bean
	public GroupedOpenApi openApiInternalDocumentation() {
		return GroupedOpenApi.builder()
				.group("Internal")
				.pathsToMatch("/companyemployee")
				.packagesToExclude("uk.co.whitbread.company.employee")
				.build();
	}

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI().info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("Company Employee Microservice API Documentation")
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
		marshaller.setPackagesToScan("uk.co.whitbread.bart.business.api", "uk.co.whitbread.shared.azureemail.genericEmail.api");
		return marshaller;
	}

	@Bean
	public WebServiceTemplate webServiceTemplateGuestDetails() {
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMarshaller(marshallerGuestDetails());
		template.setUnmarshaller(marshallerGuestDetails());
		return template;
	}

	@Bean
	public Jaxb2Marshaller marshallerGuestDetails() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("uk.co.whitbread.bart.business.api", "uk.co.whitbread.bart.selfregister.api");
		return marshaller;
	}

	@Bean
	public WebServiceTemplate webServiceTemplateCorporateAccount() {
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMarshaller(marshallerCorporateAccount());
		template.setUnmarshaller(marshallerCorporateAccount());
		return template;
	}

	@Bean
	public Jaxb2Marshaller marshallerCorporateAccount() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("uk.co.whitbread.bart.business.api");
		return marshaller;
	}

	@Bean
	OperationCustomizer operationCustomizer() {
		return ((operation, handlerMethod) -> {
			operation.addParametersItem(new Parameter()
					.name("company-id")
					.description("Required for authorization")
					.$ref("string")
					.in("header")
					.required(true));
			operation.addParametersItem(new Parameter()
					.name("employee-id")
					.description("Required for authorization")
					.$ref("string")
					.in("header")
					.required(true));
			return operation;
		});
	}

	@Bean
	OpenApiCustomizer openApiCustomizer() {
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
				ApiResponses apiResponses = operation.getResponses();

				ApiResponse unauthorizedApiResponse = new ApiResponse().description("Unauthorized")
						.content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType()));
				apiResponses.addApiResponse("401", unauthorizedApiResponse);

				ApiResponse badRequestApiResponse = new ApiResponse().description("Bad Request")
						.content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType()));
				apiResponses.addApiResponse("403", badRequestApiResponse);
			}));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(CompanyEmployeeServiceApplication.class, args);
	}
}
