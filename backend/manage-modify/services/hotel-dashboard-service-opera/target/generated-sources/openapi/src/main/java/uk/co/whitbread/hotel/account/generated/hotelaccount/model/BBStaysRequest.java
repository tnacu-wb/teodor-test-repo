package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BBStaysRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BBStaysRequest {

  private @Nullable Boolean business;

  private @Nullable String companyId;

  private @Nullable String continuationToken;

  private @Nullable String employeeId;

  /**
   * Gets or Sets filterType
   */
  public enum FilterTypeEnum {
    NAME("NAME"),
    
    ARRIVAL_DATE("ARRIVAL_DATE"),
    
    CONFIRM_NUMBER("CONFIRM_NUMBER");

    private String value;

    FilterTypeEnum(String value) {
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
    public static FilterTypeEnum fromValue(String value) {
      for (FilterTypeEnum b : FilterTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable FilterTypeEnum filterType;

  private @Nullable String filterValue;

  private @Nullable Boolean includeCheckInBookings;

  /**
   * Gets or Sets sortOrder
   */
  public enum SortOrderEnum {
    DEFAULT("DEFAULT");

    private String value;

    SortOrderEnum(String value) {
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
    public static SortOrderEnum fromValue(String value) {
      for (SortOrderEnum b : SortOrderEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable SortOrderEnum sortOrder;

  /**
   * Gets or Sets typeOfBooking
   */
  public enum TypeOfBookingEnum {
    FUTURE("FUTURE"),
    
    PAST("PAST"),
    
    CANCELLED("CANCELLED");

    private String value;

    TypeOfBookingEnum(String value) {
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
    public static TypeOfBookingEnum fromValue(String value) {
      for (TypeOfBookingEnum b : TypeOfBookingEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable TypeOfBookingEnum typeOfBooking;

  public BBStaysRequest business(Boolean business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public Boolean getBusiness() {
    return business;
  }

  public void setBusiness(Boolean business) {
    this.business = business;
  }

  public BBStaysRequest companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public BBStaysRequest continuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * Get continuationToken
   * @return continuationToken
   */
  
  @Schema(name = "continuationToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continuationToken")
  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public BBStaysRequest employeeId(String employeeId) {
    this.employeeId = employeeId;
    return this;
  }

  /**
   * Get employeeId
   * @return employeeId
   */
  
  @Schema(name = "employeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeId")
  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public BBStaysRequest filterType(FilterTypeEnum filterType) {
    this.filterType = filterType;
    return this;
  }

  /**
   * Get filterType
   * @return filterType
   */
  
  @Schema(name = "filterType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filterType")
  public FilterTypeEnum getFilterType() {
    return filterType;
  }

  public void setFilterType(FilterTypeEnum filterType) {
    this.filterType = filterType;
  }

  public BBStaysRequest filterValue(String filterValue) {
    this.filterValue = filterValue;
    return this;
  }

  /**
   * Get filterValue
   * @return filterValue
   */
  
  @Schema(name = "filterValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filterValue")
  public String getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }

  public BBStaysRequest includeCheckInBookings(Boolean includeCheckInBookings) {
    this.includeCheckInBookings = includeCheckInBookings;
    return this;
  }

  /**
   * Get includeCheckInBookings
   * @return includeCheckInBookings
   */
  
  @Schema(name = "includeCheckInBookings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeCheckInBookings")
  public Boolean getIncludeCheckInBookings() {
    return includeCheckInBookings;
  }

  public void setIncludeCheckInBookings(Boolean includeCheckInBookings) {
    this.includeCheckInBookings = includeCheckInBookings;
  }

  public BBStaysRequest sortOrder(SortOrderEnum sortOrder) {
    this.sortOrder = sortOrder;
    return this;
  }

  /**
   * Get sortOrder
   * @return sortOrder
   */
  
  @Schema(name = "sortOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortOrder")
  public SortOrderEnum getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(SortOrderEnum sortOrder) {
    this.sortOrder = sortOrder;
  }

  public BBStaysRequest typeOfBooking(TypeOfBookingEnum typeOfBooking) {
    this.typeOfBooking = typeOfBooking;
    return this;
  }

  /**
   * Get typeOfBooking
   * @return typeOfBooking
   */
  
  @Schema(name = "typeOfBooking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("typeOfBooking")
  public TypeOfBookingEnum getTypeOfBooking() {
    return typeOfBooking;
  }

  public void setTypeOfBooking(TypeOfBookingEnum typeOfBooking) {
    this.typeOfBooking = typeOfBooking;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BBStaysRequest bbStaysRequest = (BBStaysRequest) o;
    return Objects.equals(this.business, bbStaysRequest.business) &&
        Objects.equals(this.companyId, bbStaysRequest.companyId) &&
        Objects.equals(this.continuationToken, bbStaysRequest.continuationToken) &&
        Objects.equals(this.employeeId, bbStaysRequest.employeeId) &&
        Objects.equals(this.filterType, bbStaysRequest.filterType) &&
        Objects.equals(this.filterValue, bbStaysRequest.filterValue) &&
        Objects.equals(this.includeCheckInBookings, bbStaysRequest.includeCheckInBookings) &&
        Objects.equals(this.sortOrder, bbStaysRequest.sortOrder) &&
        Objects.equals(this.typeOfBooking, bbStaysRequest.typeOfBooking);
  }

  @Override
  public int hashCode() {
    return Objects.hash(business, companyId, continuationToken, employeeId, filterType, filterValue, includeCheckInBookings, sortOrder, typeOfBooking);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BBStaysRequest {\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    filterType: ").append(toIndentedString(filterType)).append("\n");
    sb.append("    filterValue: ").append(toIndentedString(filterValue)).append("\n");
    sb.append("    includeCheckInBookings: ").append(toIndentedString(includeCheckInBookings)).append("\n");
    sb.append("    sortOrder: ").append(toIndentedString(sortOrder)).append("\n");
    sb.append("    typeOfBooking: ").append(toIndentedString(typeOfBooking)).append("\n");
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

