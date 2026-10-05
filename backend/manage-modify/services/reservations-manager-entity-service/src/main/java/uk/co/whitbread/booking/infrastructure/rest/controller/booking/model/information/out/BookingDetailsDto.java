package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.booking.domain.model.information.out.BookingPrice;

@Data
@Builder
public class BookingDetailsDto {

  private String bookingReference;
  private String basketReference;
  private String hotelCode;
  private LocalDate arrivalDate;
  private String bookingStatus;
  private LocalDate departureDate;
  private Integer nights;
  private Integer noOfRooms;
  private List<BookingRoomDto> rooms;
  private String rateType;
  private BookingPrice totalCost;
  private BookingPrice newTotal;
  private BookingPrice outstandingAmount;
  private BookingPrice previousTotal;
  private BookingPriceDto refund;
  private Boolean hotelHasCityTaxForLeisure;
  private BookingPriceDto prepaidAmount;
  private String paymentOption;
  private String sourceSystem;
  private CancellationInfoResponseDto cancellationInfoResponse;
  private BookingPackagesDetailsDto donationsPackage;
  private String rateDescription;
  private PaymentCard payment;
  private BookingPriceDto dinnerAllowance;
  private BookingPriceDto cityTaxTotal;
}
