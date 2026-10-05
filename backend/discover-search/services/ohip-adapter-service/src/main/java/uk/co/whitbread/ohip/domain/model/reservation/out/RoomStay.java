package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.checkin.out.RoomRates;

@Data
@Builder
@AllArgsConstructor
public class RoomStay {

  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomRates> roomRates;
  private String checkInTime;
  private String checkOutTime;
  private int adultCount;
  private int childCount;
  private Boolean cot;
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
  private BigDecimal roomPrice;
  private List<RatePerNight> ratesPerNight;
  private String cellCode;
  private String roomNumber;
  private String bookingChannel;
  private String promotionCode;
}
