package uk.co.whitbread.booking.domain.model.information.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.booking.domain.model.migration.out.PmsSource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetails {

  private String bookingReference;
  private String basketReference;
  private String hotelCode;
  private String bookingStatus;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private Integer nights;
  private Integer noOfRooms;
  private List<BookingRoom> rooms;
  private BookingPrice totalCost;
  private BookingPrice newTotal;
  private BookingPrice outstandingAmount;
  private BookingPrice previousTotal;
  private BookingPrice refund;
  private String rateType;
  private Boolean hotelHasCityTaxForLeisure;
  private BookingPrice prepaidAmount;
  private String rateDescription;
  private CancellationInfoResponse cancellationInfoResponse;
  private BookingPackagesDetails donationsPackage;
  private String paymentOption;
  private PmsSource sourceSystem;
  private PaymentCard payment;
  private BookingPrice dinnerAllowance;
  private BookingPrice cityTaxTotal;
}
