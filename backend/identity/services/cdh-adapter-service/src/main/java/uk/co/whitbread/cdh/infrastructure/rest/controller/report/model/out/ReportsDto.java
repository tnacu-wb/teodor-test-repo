package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportsDto {

  private String bookingReference;
  private String bookingStatus;
  private PriceDto totalCost;
  private PriceDto roomCost;
  private PriceDto upsellTotalCost;
  private Integer rooms;
  private Integer nights;
  private Integer noOfAdults;
  private Integer noOfChildren;
  private String bookingDate;
  private String arrivalDate;
  private String departureDate;
  private Integer leadTime;
  private String customerReference;
  private String purchaseOrder;
  private String rateCategory;
  private BookerDto booker;
  private String cardType;
  private String cardNumber;
  private String hotelName;
  private BookerDto guest;
  private EmployeeAnswersDto employeeAnswers;
}
