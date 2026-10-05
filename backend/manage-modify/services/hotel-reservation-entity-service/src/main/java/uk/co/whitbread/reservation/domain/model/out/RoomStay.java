package uk.co.whitbread.reservation.domain.model.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RoomStay {

  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomRates> roomRates;
  private int adultCount;
  private int childCount;
  private boolean cot;
  private String roomClass;
  private String roomType;
  private int numberOfRooms;
  private String ratePlanCode;
  private CurrencyAmountType rateAmount;
  private boolean rateSuppressed;
  private String bookingChannelCode;
  private boolean fixedRate;
  private CurrencyAmountType totalAmount;
  private String marketCode;
  private String sourceCode;
  private String roomTypeCharged;
  private boolean roomNumberLocked;
  private boolean pseudoRoom;
}
