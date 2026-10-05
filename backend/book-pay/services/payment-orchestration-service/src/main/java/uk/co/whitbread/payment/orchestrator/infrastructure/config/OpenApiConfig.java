package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata configuration for the Payment Orchestration Service.
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI paymentOrchestrationOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Payment Orchestration Service API")
            .description("Unified payment orchestration API. Web (Secure Fields) and mobile "
                + "(Mobile SDK v4) payments are initialized through a single polymorphic "
                + "POST /api/payments/init endpoint discriminated by paymentMethod. "
                + "Authorization is synchronous and Datatrans callbacks arrive on the "
                + "gateway-scoped POST /api/payments/webhooks/datatrans endpoint.")
            .version("2.0.0")
            .contact(new Contact()
                .name("Book & Pay Squad")
                .email("book-pay@whitbread.co.uk")));
  }
}
