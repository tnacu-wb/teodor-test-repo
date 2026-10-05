package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.payments.BusinessSiteDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReconciliationRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("ReconciliationRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReconciliationRequestDto {

  private BusinessSiteDto businessSite;

  private String requestId;

  public ReconciliationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReconciliationRequestDto(BusinessSiteDto businessSite, String requestId) {
    this.businessSite = businessSite;
    this.requestId = requestId;
  }

  public ReconciliationRequestDto businessSite(BusinessSiteDto businessSite) {
    this.businessSite = businessSite;
    return this;
  }

  /**
   * Get businessSite
   * @return businessSite
   */
  @NotNull @Valid 
  @Schema(name = "businessSite", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessSite")
  public BusinessSiteDto getBusinessSite() {
    return businessSite;
  }

  public void setBusinessSite(BusinessSiteDto businessSite) {
    this.businessSite = businessSite;
  }

  public ReconciliationRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Unique reference for transaction provided by consumer.
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", example = "a0a9f782-98ee-468c-9839-30c487c832a3", description = "Unique reference for transaction provided by consumer.", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ReconciliationRequestDto reconciliationRequest = (ReconciliationRequestDto) o;
    return Objects.equals(this.businessSite, reconciliationRequest.businessSite) &&
        Objects.equals(this.requestId, reconciliationRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessSite, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReconciliationRequestDto {\n");
    sb.append("    businessSite: ").append(toIndentedString(businessSite)).append("\n");
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

