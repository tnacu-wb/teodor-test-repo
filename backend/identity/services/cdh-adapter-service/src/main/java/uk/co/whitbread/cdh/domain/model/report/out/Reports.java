package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reports {

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("BookingStatus")
  private String bookingStatus;

  @JsonProperty("TotalCost")
  private Price totalCost;

  @JsonProperty("RoomCost")
  private Price roomCost;

  @JsonProperty("UpsellTotalCost")
  private Price upsellTotalCost;

  @JsonProperty("Rooms")
  private Integer rooms;

  @JsonProperty("Nights")
  private Integer nights;

  @JsonProperty("NoOfAdults")
  private Integer noOfAdults;

  @JsonProperty("NoOfChildren")
  private Integer noOfChildren;

  @JsonProperty("BookingDate")
  private String bookingDate;

  @JsonProperty("ArrivalDate")
  private String arrivalDate;

  @JsonProperty("DepartureDate")
  private String departureDate;

  @JsonProperty("LeadTime")
  private Integer leadTime;

  @JsonProperty("CustomerReference")
  private String customerReference;

  @JsonProperty("PurchaseOrder")
  private String purchaseOrder;

  @JsonProperty("RateCategory")
  private String rateCategory;

  @JsonProperty("Booker")
  private Booker booker;

  @JsonProperty("CardType")
  private String cardType;

  @JsonProperty("CardNumber")
  private String cardNumber;

  @JsonProperty("HotelName")
  private String hotelName;

  @JsonProperty("Guest")
  private Booker guest;

  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;

}
