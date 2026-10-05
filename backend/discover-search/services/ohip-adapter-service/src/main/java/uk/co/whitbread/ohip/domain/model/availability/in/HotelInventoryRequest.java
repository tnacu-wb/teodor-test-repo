package uk.co.whitbread.ohip.domain.model.availability.in;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class HotelInventoryRequest implements SelfValidation<HotelInventoryRequest> {

  private String hotelId;
  private String dateRangeStart;
  private String dateRangeEnd;

  public HotelInventoryRequest(String hotelId, String dateRangeStart, String dateRangeEnd) {
    this.hotelId = hotelId;
    this.dateRangeStart = dateRangeStart;
    this.dateRangeEnd = dateRangeEnd;
    this.validateSelf();
  }

}
