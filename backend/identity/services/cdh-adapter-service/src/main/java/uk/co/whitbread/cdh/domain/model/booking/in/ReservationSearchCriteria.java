package uk.co.whitbread.cdh.domain.model.booking.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSearchCriteria {

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

  @JsonProperty("CancellationDate")
  private String cancellationDate;

  @JsonProperty("ArrivalDateFrom")
  private String arrivalDateFrom;

  @JsonProperty("ArrivalDateTo")
  private String arrivalDateTo;

  @JsonProperty("UpcomingDays")
  private Integer upcomingDays;

  @JsonProperty("PastDays")
  private Integer pastDays;

  @JsonProperty("CancelledDays")
  private Integer cancelledDays;
}
