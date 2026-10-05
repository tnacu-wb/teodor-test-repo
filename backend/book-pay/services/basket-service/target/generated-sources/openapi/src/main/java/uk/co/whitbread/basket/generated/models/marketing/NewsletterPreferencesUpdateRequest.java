package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.Subscription;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NewsletterPreferencesUpdateRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NewsletterPreferencesUpdateRequest {

  private String correlationId;

  @Valid
  private List<@Valid Subscription> subscriptions = new ArrayList<>();

  public NewsletterPreferencesUpdateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NewsletterPreferencesUpdateRequest(String correlationId, List<@Valid Subscription> subscriptions) {
    this.correlationId = correlationId;
    this.subscriptions = subscriptions;
  }

  public NewsletterPreferencesUpdateRequest correlationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  /**
   * Get correlationId
   * @return correlationId
   */
  @NotNull 
  @Schema(name = "correlationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("correlationId")
  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public NewsletterPreferencesUpdateRequest subscriptions(List<@Valid Subscription> subscriptions) {
    this.subscriptions = subscriptions;
    return this;
  }

  public NewsletterPreferencesUpdateRequest addSubscriptionsItem(Subscription subscriptionsItem) {
    if (this.subscriptions == null) {
      this.subscriptions = new ArrayList<>();
    }
    this.subscriptions.add(subscriptionsItem);
    return this;
  }

  /**
   * Get subscriptions
   * @return subscriptions
   */
  @NotNull @Valid 
  @Schema(name = "subscriptions", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subscriptions")
  public List<@Valid Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(List<@Valid Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsletterPreferencesUpdateRequest newsletterPreferencesUpdateRequest = (NewsletterPreferencesUpdateRequest) o;
    return Objects.equals(this.correlationId, newsletterPreferencesUpdateRequest.correlationId) &&
        Objects.equals(this.subscriptions, newsletterPreferencesUpdateRequest.subscriptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(correlationId, subscriptions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsletterPreferencesUpdateRequest {\n");
    sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
    sb.append("    subscriptions: ").append(toIndentedString(subscriptions)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

