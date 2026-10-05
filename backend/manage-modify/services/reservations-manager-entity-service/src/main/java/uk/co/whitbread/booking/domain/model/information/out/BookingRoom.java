package uk.co.whitbread.booking.domain.model.information.out;

import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingRoom {

  private String roomId;
  private BookingPrice roomCost;
  private String roomType;
  private Integer adults;
  private Integer children;
  private Boolean cot;
  private Guest guest;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;
  private Set<BookingPackagesDetails> kidsMeal;
  private Set<BookingPackagesDetails> adultsMeal;
  private Set<BookingPackagesDetails> extrasItems;
}
