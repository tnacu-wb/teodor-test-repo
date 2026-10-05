package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {

  private String hotelCode;
  private String sourceSystem;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private PriceDto totalCost;
  private PriceDto cityTax;
  private List<RoomTypesDto> roomTypes;
  private int noOfRooms;
  private String customerReference;
  private int historyRecordNumber;
  private String purchaseOrder;
  private String bookingReference;
  private PriceDto prePaidAmount;
  private String paymentStatus;
  private boolean cancelled;
  private boolean checkInOnline;
  private boolean checkedIn;
  private String rateClass;
  private String checkInDate;
  private String promotionText;
  private String cellCodeLegend;
  private boolean carDataRequired;
  private BookingStatusDto bookingStatus;
  private Boolean amendable;
  private String guestHistoryNumber;
  private String bookedBy;
  private PriceDto bookingFee;
  private PriceDto outstandingAmount;
  private String frequentBooking;
  private boolean mpibooking;
  private String rateName;
  private Boolean cancelable;
  private String cancellationId;
  private String hotelName;
  private String leadGuest;
  private String leadGuestSurname;
  private int noOfNights;
  @JsonProperty("isCheckInOnlineAvailable")
  private boolean isCheckInOnlineAvailable;
  private BasketStatus basketStatus;
  private String hotelCountry;
  @JsonProperty("isDigitalKeyEligible")
  private boolean isDigitalKeyEligible;
  private String ciolErrorLabelKey;
}
