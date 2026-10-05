package uk.co.whitbread.booking.domain.model.history.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {

  private String hotelCode;
  private String sourceSystem;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private Price totalCost;
  private Price cityTax;
  private List<RoomTypes> roomTypes;
  private int noOfRooms;
  private String customerReference;
  private int historyRecordNumber;
  private String purchaseOrder;
  private String bookingReference;
  private Price prePaidAmount;
  private String paymentStatus;
  private boolean cancelled;
  private boolean checkInOnline;
  private boolean checkedIn;
  private String rateClass;
  private String checkInDate;
  private String promotionText;
  private String cellCodeLegend;
  private boolean carDataRequired;
  private BookingStatus bookingStatus;
  private Boolean amendable;
  private String guestHistoryNumber;
  private String bookedBy;
  private Price bookingFee;
  private Price outstandingAmount;
  private String frequentBooking;
  private boolean mpibooking;
  private String rateName;
  private Boolean cancelable;
  private String cancellationId;
  private String hotelName;
  private String leadGuest;
  private String leadGuestSurname;
  private int noOfNights;
  private boolean isCheckInOnlineAvailable;
  private BasketStatus basketStatus;
  private Booker booker;
  private String hotelCountry;
  private boolean isDigitalKeyEligible;
  private String ciolErrorLabelKey;
}
