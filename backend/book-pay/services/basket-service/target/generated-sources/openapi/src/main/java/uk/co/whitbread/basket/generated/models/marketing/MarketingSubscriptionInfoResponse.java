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
 * MarketingSubscriptionInfoResponse
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MarketingSubscriptionInfoResponse {

  private @Nullable Boolean businessClient;

  private @Nullable String countryCode;

  private @Nullable String emailAddress;

  private @Nullable String firstName;

  private @Nullable String lastName;

  @Valid
  private List<@Valid RegionSubscription> regionSubscriptions = new ArrayList<>();

  @Valid
  private List<String> regions = new ArrayList<>();

  private @Nullable Boolean restaurantNewsletter;

  private @Nullable String sessionId;

  private @Nullable Boolean subscribedStatus;

  public MarketingSubscriptionInfoResponse businessClient(Boolean businessClient) {
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

  public MarketingSubscriptionInfoResponse countryCode(String countryCode) {
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

  public MarketingSubscriptionInfoResponse emailAddress(String emailAddress) {
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

  public MarketingSubscriptionInfoResponse firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public MarketingSubscriptionInfoResponse lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public MarketingSubscriptionInfoResponse regionSubscriptions(List<@Valid RegionSubscription> regionSubscriptions) {
    this.regionSubscriptions = regionSubscriptions;
    return this;
  }

  public MarketingSubscriptionInfoResponse addRegionSubscriptionsItem(RegionSubscription regionSubscriptionsItem) {
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

  public MarketingSubscriptionInfoResponse regions(List<String> regions) {
    this.regions = regions;
    return this;
  }

  public MarketingSubscriptionInfoResponse addRegionsItem(String regionsItem) {
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

  public MarketingSubscriptionInfoResponse restaurantNewsletter(Boolean restaurantNewsletter) {
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

  public MarketingSubscriptionInfoResponse sessionId(String sessionId) {
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

  public MarketingSubscriptionInfoResponse subscribedStatus(Boolean subscribedStatus) {
    this.subscribedStatus = subscribedStatus;
    return this;
  }

  /**
   * Get subscribedStatus
   * @return subscribedStatus
   */
  
  @Schema(name = "subscribedStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subscribedStatus")
  public Boolean getSubscribedStatus() {
    return subscribedStatus;
  }

  public void setSubscribedStatus(Boolean subscribedStatus) {
    this.subscribedStatus = subscribedStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = (MarketingSubscriptionInfoResponse) o;
    return Objects.equals(this.businessClient, marketingSubscriptionInfoResponse.businessClient) &&
        Objects.equals(this.countryCode, marketingSubscriptionInfoResponse.countryCode) &&
        Objects.equals(this.emailAddress, marketingSubscriptionInfoResponse.emailAddress) &&
        Objects.equals(this.firstName, marketingSubscriptionInfoResponse.firstName) &&
        Objects.equals(this.lastName, marketingSubscriptionInfoResponse.lastName) &&
        Objects.equals(this.regionSubscriptions, marketingSubscriptionInfoResponse.regionSubscriptions) &&
        Objects.equals(this.regions, marketingSubscriptionInfoResponse.regions) &&
        Objects.equals(this.restaurantNewsletter, marketingSubscriptionInfoResponse.restaurantNewsletter) &&
        Objects.equals(this.sessionId, marketingSubscriptionInfoResponse.sessionId) &&
        Objects.equals(this.subscribedStatus, marketingSubscriptionInfoResponse.subscribedStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessClient, countryCode, emailAddress, firstName, lastName, regionSubscriptions, regions, restaurantNewsletter, sessionId, subscribedStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketingSubscriptionInfoResponse {\n");
    sb.append("    businessClient: ").append(toIndentedString(businessClient)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    regionSubscriptions: ").append(toIndentedString(regionSubscriptions)).append("\n");
    sb.append("    regions: ").append(toIndentedString(regions)).append("\n");
    sb.append("    restaurantNewsletter: ").append(toIndentedString(restaurantNewsletter)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    subscribedStatus: ").append(toIndentedString(subscribedStatus)).append("\n");
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

