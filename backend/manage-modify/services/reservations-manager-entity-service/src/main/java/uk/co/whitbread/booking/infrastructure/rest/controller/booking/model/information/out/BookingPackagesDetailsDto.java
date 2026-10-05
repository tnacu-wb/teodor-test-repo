package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPackagesDetailsDto {

  private String packageCode;
  private String description;
  private BookingPriceDto totalPrice;
  private Integer noSelections;
}
