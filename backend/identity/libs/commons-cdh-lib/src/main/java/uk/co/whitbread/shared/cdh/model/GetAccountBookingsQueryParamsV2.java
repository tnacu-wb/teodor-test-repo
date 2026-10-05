package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAccountBookingsQueryParamsV2 {

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

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("PurchaseOrder")
  private String purchaseOrder;

  @JsonProperty("CustomerReference")
  private String customerReference;

  @JsonProperty("CustomerAccountId")
  private String customerAccountId;

  @JsonProperty("ThirdPartyReference")
  private String thirdPartyReference;

  @JsonProperty("Status")
  private String status;

  @JsonProperty("PageSize")
  private Integer pageSize;

  @JsonProperty("ContinuationToken")
  private String continuationToken;

  @JsonProperty("PageNumber")
  private Integer pageNumber;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;

  @JsonProperty("BookingsDatabaseSearch")
  private Boolean bookingsDatabaseSearch;

  @JsonProperty("ReservationId")
  private Integer reservationId;

  @JsonProperty("BookingDate")
  private String bookingDate;

  @JsonProperty("ArrivalDateFrom")
  private String arrivalDateFrom;

  @JsonProperty("ArrivalDateTo")
  private String arrivalDateTo;
}
