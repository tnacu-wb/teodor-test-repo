package uk.co.whitbread.cdh.domain.model.booking.out;

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
public class Results {

  @JsonProperty("id")
  private String id;

  @JsonProperty("SourceSystem")
  private String sourceSystem;

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("PartitionKey")
  private String partitionKey;

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

  @JsonProperty("CrmCompanyId")
  private String crmCompanyId;

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
  private String status;

  @JsonProperty("StatusSortOrder")
  private String statusSortOrder;

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

  @JsonProperty("DCTMCUserName")
  private String dctmcUserName;

  @JsonProperty("IATANumber")
  private String iataNumber;

  @JsonProperty("IsPackage")
  private boolean isPackage;

  @JsonProperty("CellCode")
  private String cellCode;

  @JsonProperty("CellCodeId")
  private Integer cellCodeId;

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

  @JsonProperty("PaymentCard")
  private PaymentCard paymentCard;

  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;

  @JsonProperty("Booker")
  private Booker booker;

  @JsonProperty("Rooms")
  private List<Rooms> rooms;

  @JsonProperty("UpsellTotalCost")
  private Price upsellTotalCost;

  @JsonProperty("TotalCost")
  private Price totalCost;

  @JsonProperty("ActiveTotalCost")
  private Price activeTotalCost;
}
