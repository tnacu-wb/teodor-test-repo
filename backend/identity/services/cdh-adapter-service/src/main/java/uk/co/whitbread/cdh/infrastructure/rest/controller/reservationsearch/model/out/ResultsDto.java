package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultsDto {

  private String id;
  private String sourceSystem;
  private String bookingReference;
  private String partitionKey;
  private String customerAccountId;
  private String companyAccountId;
  private String employeeAccountId;
  private String bartGuestHistoryNumber;
  private Long globalCompanyId;
  private Long bartEmployeeId;
  private String crmCompanyId;
  private String accountType;
  private String bookingType;
  private String hotelName;
  private String hotelCode;
  private String arrivalDate;
  private String departureDate;
  private String status;
  private String statusSortOrder;
  private boolean amendable;
  private boolean cancellable;
  private String cancellationId;
  private String cancellationDate;
  private String bookingMethod;
  private String bookingMethodDescription;
  private String bookingDate;
  private boolean walkIn;
  private String thirdPartyReference;
  private String customerReference;
  private String purchaseOrder;
  private String dctmcUserName;
  private String iataNumber;
  private boolean isPackage;
  private String cellCode;
  private Integer cellCodeId;
  private String ratePlan;
  private String rateClass;
  private String rateCategoryCode;
  private String rateCategory;
  private boolean prePaid;
  private PriceDto prePaidAmount;
  private PaymentCardDto paymentCard;
  private EmployeeAnswersDto employeeAnswers;
  private BookerDto booker;
  private List<RoomsDto> rooms;
  private PriceDto upsellTotalCost;
  private PriceDto totalCost;
  private PriceDto activeTotalCost;
}
