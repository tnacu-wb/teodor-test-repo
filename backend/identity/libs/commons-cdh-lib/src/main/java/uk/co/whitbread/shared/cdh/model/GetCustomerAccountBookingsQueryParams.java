package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCustomerAccountBookingsQueryParams {

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("Lastname")
  private String lastName;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("HotelCode")
  private String hotelCode;

  @JsonProperty("HotelName")
  private String hotelName;

  @JsonProperty("PostalCode")
  private String postalCode;

  @JsonProperty("Telephone")
  private String telephone;

  @JsonProperty("ArrivalDateFrom")
  private String arrivalDateFrom;

  @JsonProperty("ArrivalDateTo")
  private String arrivalDateTo;

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("PurchaseOrder")
  private String purchaseOrder;

  @JsonProperty("CustomerReference")
  private String customerReference;

  @JsonProperty("BartGuestProfileId")
  private String bartGuestProfileId;

  @JsonProperty("CustomerAccountId")
  private String customerAccountId;

  @JsonProperty("EmployeeId")
  private String employeeId;

  @JsonProperty("ThirdPartyReference")
  private String thirdPartyReference;

  @JsonProperty("Status")
  private String bookingStatus;

  @JsonProperty("PageSize")
  private Integer pageSize;

  @JsonProperty("ContinuationToken")
  private String continuationToken;

  @JsonProperty("PageNumber")
  private Integer pageNumber;

  @JsonProperty("OrderBy")
  private String orderBy;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;

  @JsonProperty("BookingsDatabaseSearch")
  private String bookingsDatabaseSearch;
}
