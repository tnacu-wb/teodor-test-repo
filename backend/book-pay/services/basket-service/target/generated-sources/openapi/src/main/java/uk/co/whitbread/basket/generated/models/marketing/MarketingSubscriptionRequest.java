package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.RegionSubscription;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MarketingSubscriptionRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MarketingSubscriptionRequest {

  private @Nullable Boolean businessClient;

  private @Nullable String countryCode;

  private @Nullable String customerId;

  private @Nullable String emailAddress;

  private String firstName;

  private String lastName;

  @Valid
  private List<@Valid RegionSubscription> regionSubscriptions = new ArrayList<>();

  @Valid
  private List<String> regions = new ArrayList<>();

  private @Nullable Boolean restaurantNewsletter;

  private @Nullable String sessionId;

  public MarketingSubscriptionRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MarketingSubscriptionRequest(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public MarketingSubscriptionRequest businessClient(Boolean businessClient) {
    this.businessClient = businessClient;
    return this;
  }

  /**
   * Get businessClient
   * @return businessClient
   */
  
  @Schema(name = "businessClient", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessClient")
  public Boolean getBusinessClient() {
    return businessClient;
  }

  public void setBusinessClient(Boolean businessClient) {
    this.businessClient = businessClient;
  }

  public MarketingSubscriptionRequest countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public MarketingSubscriptionRequest customerId(String customerId) {
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

  public MarketingSubscriptionRequest emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public MarketingSubscriptionRequest firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public MarketingSubscriptionRequest lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public MarketingSubscriptionRequest regionSubscriptions(List<@Valid RegionSubscription> regionSubscriptions) {
    this.regionSubscriptions = regionSubscriptions;
    return this;
  }

  public MarketingSubscriptionRequest addRegionSubscriptionsItem(RegionSubscription regionSubscriptionsItem) {
    if (this.regionSubscriptions == null) {
      this.regionSubscriptions = new ArrayList<>();
    }
    this.regionSubscriptions.add(regionSubscriptionsItem);
    return this;
  }

  /**
   * Get regionSubscriptions
   * @return regionSubscriptions
   */
  @Valid 
  @Schema(name = "regionSubscriptions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("regionSubscriptions")
  public List<@Valid RegionSubscription> getRegionSubscriptions() {
    return regionSubscriptions;
  }

  public void setRegionSubscriptions(List<@Valid RegionSubscription> regionSubscriptions) {
    this.regionSubscriptions = regionSubscriptions;
  }

  public MarketingSubscriptionRequest regions(List<String> regions) {
    this.regions = regions;
    return this;
  }

  public MarketingSubscriptionRequest addRegionsItem(String regionsItem) {
    if (this.regions == null) {
      this.regions = new ArrayList<>();
    }
    this.regions.add(regionsItem);
    return this;
  }

  /**
   * Get regions
   * @return regions
   */
  
  @Schema(name = "regions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("regions")
  public List<String> getRegions() {
    return regions;
  }

  public void setRegions(List<String> regions) {
    this.regions = regions;
  }

  public MarketingSubscriptionRequest restaurantNewsletter(Boolean restaurantNewsletter) {
    this.restaurantNewsletter = restaurantNewsletter;
    return this;
  }

  /**
   * Get restaurantNewsletter
   * @return restaurantNewsletter
   */
  
  @Schema(name = "restaurantNewsletter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurantNewsletter")
  public Boolean getRestaurantNewsletter() {
    return restaurantNewsletter;
  }

  public void setRestaurantNewsletter(Boolean restaurantNewsletter) {
    this.restaurantNewsletter = restaurantNewsletter;
  }

  public MarketingSubscriptionRequest sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   */
  
  @Schema(name = "sessionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionId")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketingSubscriptionRequest marketingSubscriptionRequest = (MarketingSubscriptionRequest) o;
    return Objects.equals(this.businessClient, marketingSubscriptionRequest.businessClient) &&
        Objects.equals(this.countryCode, marketingSubscriptionRequest.countryCode) &&
        Objects.equals(this.customerId, marketingSubscriptionRequest.customerId) &&
        Objects.equals(this.emailAddress, marketingSubscriptionRequest.emailAddress) &&
        Objects.equals(this.firstName, marketingSubscriptionRequest.firstName) &&
        Objects.equals(this.lastName, marketingSubscriptionRequest.lastName) &&
        Objects.equals(this.regionSubscriptions, marketingSubscriptionRequest.regionSubscriptions) &&
        Objects.equals(this.regions, marketingSubscriptionRequest.regions) &&
        Objects.equals(this.restaurantNewsletter, marketingSubscriptionRequest.restaurantNewsletter) &&
        Objects.equals(this.sessionId, marketingSubscriptionRequest.sessionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessClient, countryCode, customerId, emailAddress, firstName, lastName, regionSubscriptions, regions, restaurantNewsletter, sessionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketingSubscriptionRequest {\n");
    sb.append("    businessClient: ").append(toIndentedString(businessClient)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    regionSubscriptions: ").append(toIndentedString(regionSubscriptions)).append("\n");
    sb.append("    regions: ").append(toIndentedString(regions)).append("\n");
    sb.append("    restaurantNewsletter: ").append(toIndentedString(restaurantNewsletter)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
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

