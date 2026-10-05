package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RoomStayDto {

  private LocalDate arrivalDate;
  private LocalDate departureDate;
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
