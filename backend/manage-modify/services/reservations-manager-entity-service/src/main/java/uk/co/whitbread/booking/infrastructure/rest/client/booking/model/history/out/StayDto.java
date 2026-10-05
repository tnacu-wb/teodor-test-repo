package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.BookingStatusStayDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayDto {

  private String hotelCode;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private PriceStayDto totalCost;
  private PriceStayDto cityTax;
  private List<RoomTypesStayDto> roomTypes;
  private int noOfRooms;
  private String customerReference;
  private int historyRecordNumber;
  private String purchaseOrder;
  private String confirmationNumber;
  private PriceStayDto prePaidAmount;
  private String paymentStatus;
  private boolean cancelled;
  private boolean checkInOnline;
  private boolean checkedIn;
  private String rateClass;
  private String checkInDate;
  private String promotionText;
  private String cellCodeLegend;
  private boolean carDataRequired;
  private BookingStatusStayDto bookingStatus;
  private boolean amendable;
  private String guestHistoryNumber;
  private String bookedBy;
  private PriceStayDto bookingFee;
  private PriceStayDto outstandingAmount;
  private String frequentBooking;
  private boolean mpibooking;
  private String rateName;
  private Boolean cancelable;
  private String cancellationId;
  private String hotelName;
  private String leadGuest;
  private String leadGuestSurname;
  private BookerDto booker;

}
