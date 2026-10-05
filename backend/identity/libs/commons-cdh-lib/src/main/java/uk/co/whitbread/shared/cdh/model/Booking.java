package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class Booking {

  @JsonProperty("id")
  private String id;

  @JsonProperty("SourceSystem")
  private String sourceSystem;

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("CustomerAccountId")
  private String customerAccountId;

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("GlobalCompanyId")
  private Long globalCompanyId;

  @JsonProperty("BartEmployeeId")
  private Long bartEmployeeId;

  @JsonProperty("AccountType")
  private String accountType;

  @JsonProperty("BookingType")
  private String bookingType;

  @JsonProperty("HotelName")
  private String hotelName;

  @JsonProperty("HotelCode")
  private String hotelCode;

  @JsonProperty("ArrivalDate")
  private String arrivalDate;

  @JsonProperty("DepartureDate")
  private String departureDate;

  @JsonProperty("Status")
  private String bookingStatus;

  @JsonProperty("StatusSortOrder")
  private Integer bookingStatusSortOrder;

  @JsonProperty("Amendable")
  private boolean amendable;

  @JsonProperty("Cancellable")
  private boolean cancellable;

  @JsonProperty("CancellationId")
  private String cancellationId;

  @JsonProperty("CancellationDate")
  private String cancellationDate;

  @JsonProperty("BookingMethod")
  private String bookingMethod;

  @JsonProperty("BookingMethodDescription")
  private String bookingMethodDescription;

  @JsonProperty("BookingDate")
  private String bookingDate;

  @JsonProperty("WalkIn")
  private boolean walkIn;

  @JsonProperty("ThirdPartyReference")
  private String thirdPartyReference;

  @JsonProperty("CustomerReference")
  private String customerReference;

  @JsonProperty("PurchaseOrder")
  private String purchaseOrder;

  @JsonProperty("IsPackage")
  private boolean isPackage;

  @JsonProperty("CellCode")
  private String cellCode;

  @JsonProperty("RatePlan")
  private String ratePlan;

  @JsonProperty("RateClass")
  private String rateClass;

  @JsonProperty("RateCategoryCode")
  private String rateCategoryCode;

  @JsonProperty("RateCategory")
  private String rateCategory;

  @JsonProperty("PrePaid")
  private boolean prePaid;

  @JsonProperty("PrePaidAmount")
  private Price prePaidAmount;

  @JsonProperty("Booker")
  private Booker booker;

  @JsonProperty("Rooms")
  private List<Room> rooms;

  @JsonProperty("UpsellTotalCost")
  private Price upsellTotalCost;

  @JsonProperty("TotalCost")
  private Price totalCost;

  @JsonProperty("PartitionKey")
  private String partitionKey;

  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;

  @JsonProperty("PaymentCard")
  private BusinessPaymentCard paymentCard;
}
