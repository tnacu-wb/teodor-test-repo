package uk.co.whitbread.domain.model.migrationstatus.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class HotelsMigrationStatusRequest implements SelfValidation<HotelAvailabilityRequest> {

  @NotNull
  private List<String> hotelIds;

  public HotelsMigrationStatusRequest(List<String> hotelIds) {
    this.hotelIds = hotelIds;
    this.validateSelf();
  }
}
