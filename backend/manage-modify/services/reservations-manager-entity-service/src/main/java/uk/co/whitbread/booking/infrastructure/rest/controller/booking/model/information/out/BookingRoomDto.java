package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingRoomDto {

  private String roomId;
  private BookingPriceDto roomCost;
  private String roomType;
  private Integer adults;
  private Integer children;
  private Boolean cot;
  private GuestDto guest;
  private Set<BookingPackagesDetailsDto> kidsMeal;
  private Set<BookingPackagesDetailsDto> adultsMeal;
  private Set<BookingPackagesDetailsDto> extrasItems;
}
