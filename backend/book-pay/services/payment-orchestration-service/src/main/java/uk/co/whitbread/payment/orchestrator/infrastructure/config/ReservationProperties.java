package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for Hotel Reservation Entity Service integration.
 *
 * <p>Maps to the {@code integrations.reservation} section in application.yml.
 * The host is configurable per environment via the {@code RESERVATION_HOST}
 * environment variable.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "integrations.reservation")
public class ReservationProperties {

  /** Base URL for the Hotel Reservation Entity Service. */
  @NotBlank(message = "integrations.reservation.host must not be blank")
  private String host = "http://localhost:9103";

  /** Endpoint path template for retrieving a reservation by basket reference. */
  private String reservationEndpoint = "/v1/reservations/basket/{basketReference}";

  /** HTTP connection timeout for reservation service calls. */
  private Duration connectTimeout = Duration.ofSeconds(5);

  /** HTTP read timeout for reservation service calls. */
  private Duration readTimeout = Duration.ofSeconds(10);
}
