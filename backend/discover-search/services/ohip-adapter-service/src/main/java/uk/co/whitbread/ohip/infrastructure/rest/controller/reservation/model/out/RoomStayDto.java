package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.RoomRatesDto;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RoomStayDto {

  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomRatesDto> roomRates;
  private int adultCount;
  private int childCount;
  private String roomClass;
  private String roomType;
  private int numberOfRooms;
  private String ratePlanCode;
  private CurrencyAmountTypeDto rateAmount;
  private boolean rateSuppressed;
  private String bookingChannelCode;
  private boolean fixedRate;
  private CurrencyAmountTypeDto totalAmount;
  private String marketCode;
  private String sourceCode;
  private String roomTypeCharged;
  private boolean roomNumberLocked;
  private boolean pseudoRoom;
}
