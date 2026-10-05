package uk.co.whitbread.reservation.domain.model.availability.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;


@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class HotelInventoryRequest implements SelfValidation<HotelInventoryRequest> {

  @NotNull
  private String hotelId;

  @NotBlank
  private String dateRangeStart;

  @NotBlank
  private String dateRangeEnd;

  public HotelInventoryRequest(String hotelId, String dateRangeStart, String dateRangeEnd) {
    this.hotelId = hotelId;
    this.dateRangeStart = dateRangeStart;
    this.dateRangeEnd = dateRangeEnd;
    this.validateSelf();
  }
}
