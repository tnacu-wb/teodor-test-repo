package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferCommissionableStatus;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Search Request attributes
 */

@Schema(name = "ShopBaseRequest", description = "Search Request attributes")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ShopBaseRequest {

  private Integer adults = 1;

  private Integer children = 0;

  @Valid
  private List<@Min(0) @Max(18)Integer> childrenAges = new ArrayList<>();

  private Integer numberOfUnits = 1;

  private Boolean ratePlanCodeMatchOnly = false;

  /**
   * This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>
   */
  public enum AlternateOffersEnum {
    ALWAYS("Always"),
    
    WHEN_REQUESTED_NOT_AVAILABLE("WhenRequestedNotAvailable");

    private String value;

    AlternateOffersEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AlternateOffersEnum fromValue(String value) {
      for (AlternateOffersEnum b : AlternateOffersEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private AlternateOffersEnum alternateOffers = AlternateOffersEnum.ALWAYS;

  private OfferCommissionableStatus commissionableStatus = OfferCommissionableStatus.BOTH;

  public ShopBaseRequest adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Number of adults
   * minimum: 1
   * maximum: 10
   * @return adults
   */
  @Min(1) @Max(10) 
  @Schema(name = "adults", example = "1", description = "Number of adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public ShopBaseRequest children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Number of children
   * minimum: 0
   * maximum: 10
   * @return children
   */
  @Min(0) @Max(10) 
  @Schema(name = "children", example = "2", description = "Number of children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public ShopBaseRequest childrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
    return this;
  }

  public ShopBaseRequest addChildrenAgesItem(Integer childrenAgesItem) {
    if (this.childrenAges == null) {
      this.childrenAges = new ArrayList<>();
    }
    this.childrenAges.add(childrenAgesItem);
    return this;
  }

  /**
   * List of Age of the children. Element count must match the number of children
   * @return childrenAges
   */
  
  @Schema(name = "childrenAges", description = "List of Age of the children. Element count must match the number of children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenAges")
  public List<@Min(0) @Max(18)Integer> getChildrenAges() {
    return childrenAges;
  }

  public void setChildrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
  }

  public ShopBaseRequest numberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
    return this;
  }

  /**
   * Number of units
   * minimum: 1
   * maximum: 10
   * @return numberOfUnits
   */
  @Min(1) @Max(10) 
  @Schema(name = "numberOfUnits", example = "1", description = "Number of units", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfUnits")
  public Integer getNumberOfUnits() {
    return numberOfUnits;
  }

  public void setNumberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
  }

  public ShopBaseRequest ratePlanCodeMatchOnly(Boolean ratePlanCodeMatchOnly) {
    this.ratePlanCodeMatchOnly = ratePlanCodeMatchOnly;
    return this;
  }

  /**
   * If true, only rate plan code specified, otherwise public rate plan codes too
   * @return ratePlanCodeMatchOnly
   */
  
  @Schema(name = "ratePlanCodeMatchOnly", description = "If true, only rate plan code specified, otherwise public rate plan codes too", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCodeMatchOnly")
  public Boolean getRatePlanCodeMatchOnly() {
    return ratePlanCodeMatchOnly;
  }

  public void setRatePlanCodeMatchOnly(Boolean ratePlanCodeMatchOnly) {
    this.ratePlanCodeMatchOnly = ratePlanCodeMatchOnly;
  }

  public ShopBaseRequest alternateOffers(AlternateOffersEnum alternateOffers) {
    this.alternateOffers = alternateOffers;
    return this;
  }

  /**
   * This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>
   * @return alternateOffers
   */
  
  @Schema(name = "alternateOffers", description = "This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alternateOffers")
  public AlternateOffersEnum getAlternateOffers() {
    return alternateOffers;
  }

  public void setAlternateOffers(AlternateOffersEnum alternateOffers) {
    this.alternateOffers = alternateOffers;
  }

  public ShopBaseRequest commissionableStatus(OfferCommissionableStatus commissionableStatus) {
    this.commissionableStatus = commissionableStatus;
    return this;
  }

  /**
   * Get commissionableStatus
   * @return commissionableStatus
   */
  @Valid 
  @Schema(name = "commissionableStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commissionableStatus")
  public OfferCommissionableStatus getCommissionableStatus() {
    return commissionableStatus;
  }

  public void setCommissionableStatus(OfferCommissionableStatus commissionableStatus) {
    this.commissionableStatus = commissionableStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShopBaseRequest shopBaseRequest = (ShopBaseRequest) o;
    return Objects.equals(this.adults, shopBaseRequest.adults) &&
        Objects.equals(this.children, shopBaseRequest.children) &&
        Objects.equals(this.childrenAges, shopBaseRequest.childrenAges) &&
        Objects.equals(this.numberOfUnits, shopBaseRequest.numberOfUnits) &&
        Objects.equals(this.ratePlanCodeMatchOnly, shopBaseRequest.ratePlanCodeMatchOnly) &&
        Objects.equals(this.alternateOffers, shopBaseRequest.alternateOffers) &&
        Objects.equals(this.commissionableStatus, shopBaseRequest.commissionableStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, childrenAges, numberOfUnits, ratePlanCodeMatchOnly, alternateOffers, commissionableStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShopBaseRequest {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    childrenAges: ").append(toIndentedString(childrenAges)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    ratePlanCodeMatchOnly: ").append(toIndentedString(ratePlanCodeMatchOnly)).append("\n");
    sb.append("    alternateOffers: ").append(toIndentedString(alternateOffers)).append("\n");
    sb.append("    commissionableStatus: ").append(toIndentedString(commissionableStatus)).append("\n");
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

