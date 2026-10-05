package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.ContactChannel;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NewsletterPreferencesGetRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NewsletterPreferencesGetRequest {

  @Valid
  private List<String> brandCodes = new ArrayList<>();

  private @Nullable ContactChannel contactChannel;

  private String requestId;

  public NewsletterPreferencesGetRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NewsletterPreferencesGetRequest(String requestId) {
    this.requestId = requestId;
  }

  public NewsletterPreferencesGetRequest brandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
    return this;
  }

  public NewsletterPreferencesGetRequest addBrandCodesItem(String brandCodesItem) {
    if (this.brandCodes == null) {
      this.brandCodes = new ArrayList<>();
    }
    this.brandCodes.add(brandCodesItem);
    return this;
  }

  /**
   * Get brandCodes
   * @return brandCodes
   */
  
  @Schema(name = "brandCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brandCodes")
  public List<String> getBrandCodes() {
    return brandCodes;
  }

  public void setBrandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
  }

  public NewsletterPreferencesGetRequest contactChannel(ContactChannel contactChannel) {
    this.contactChannel = contactChannel;
    return this;
  }

  /**
   * Get contactChannel
   * @return contactChannel
   */
  @Valid 
  @Schema(name = "contactChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannel")
  public ContactChannel getContactChannel() {
    return contactChannel;
  }

  public void setContactChannel(ContactChannel contactChannel) {
    this.contactChannel = contactChannel;
  }

  public NewsletterPreferencesGetRequest requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsletterPreferencesGetRequest newsletterPreferencesGetRequest = (NewsletterPreferencesGetRequest) o;
    return Objects.equals(this.brandCodes, newsletterPreferencesGetRequest.brandCodes) &&
        Objects.equals(this.contactChannel, newsletterPreferencesGetRequest.contactChannel) &&
        Objects.equals(this.requestId, newsletterPreferencesGetRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandCodes, contactChannel, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsletterPreferencesGetRequest {\n");
    sb.append("    brandCodes: ").append(toIndentedString(brandCodes)).append("\n");
    sb.append("    contactChannel: ").append(toIndentedString(contactChannel)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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

