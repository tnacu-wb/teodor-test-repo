package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayDetailsDto {

  private String confirmationNumber;
  private String hotelCode;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String rateText;
  private Integer nights;
  private Boolean prepaid;
  private Boolean carDataRequired;
  private String rateClass;
  private String rateDescription;
  private List<StayRoomDto> rooms;
  private List<StayRoomBreakdownDto> roomBreakdown;
  private StayUpsellBreakdown upsellBreakdown;
  private StayPriceDto totalCost;
  private StayPriceDto cityTax;
  private StayPriceDto prepaidAmount;
  private StayPriceDto roomCost;
  private Boolean cancelable;
  private Boolean amendable;
  private Boolean amendOnline;
  private StayPaymentCardDto payment;
}
