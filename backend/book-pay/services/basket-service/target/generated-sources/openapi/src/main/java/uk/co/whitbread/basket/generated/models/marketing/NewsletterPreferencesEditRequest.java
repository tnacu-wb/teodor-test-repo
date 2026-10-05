package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.EditSubscription;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NewsletterPreferencesEditRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NewsletterPreferencesEditRequest {

  private @Nullable String customerId;

  @Valid
  private List<@Valid EditSubscription> subscriptionData = new ArrayList<>();

  private @Nullable String userId;

  public NewsletterPreferencesEditRequest customerId(String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
   */
  
  @Schema(name = "customerId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerId")
  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public NewsletterPreferencesEditRequest subscriptionData(List<@Valid EditSubscription> subscriptionData) {
    this.subscriptionData = subscriptionData;
    return this;
  }

  public NewsletterPreferencesEditRequest addSubscriptionDataItem(EditSubscription subscriptionDataItem) {
    if (this.subscriptionData == null) {
      this.subscriptionData = new ArrayList<>();
    }
    this.subscriptionData.add(subscriptionDataItem);
    return this;
  }

  /**
   * Get subscriptionData
   * @return subscriptionData
   */
  @Valid 
  @Schema(name = "subscriptionData", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subscriptionData")
  public List<@Valid EditSubscription> getSubscriptionData() {
    return subscriptionData;
  }

  public void setSubscriptionData(List<@Valid EditSubscription> subscriptionData) {
    this.subscriptionData = subscriptionData;
  }

  public NewsletterPreferencesEditRequest userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsletterPreferencesEditRequest newsletterPreferencesEditRequest = (NewsletterPreferencesEditRequest) o;
    return Objects.equals(this.customerId, newsletterPreferencesEditRequest.customerId) &&
        Objects.equals(this.subscriptionData, newsletterPreferencesEditRequest.subscriptionData) &&
        Objects.equals(this.userId, newsletterPreferencesEditRequest.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId, subscriptionData, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsletterPreferencesEditRequest {\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    subscriptionData: ").append(toIndentedString(subscriptionData)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

