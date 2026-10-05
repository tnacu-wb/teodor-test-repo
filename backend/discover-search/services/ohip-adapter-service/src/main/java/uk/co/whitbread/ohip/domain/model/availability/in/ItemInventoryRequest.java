package uk.co.whitbread.ohip.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ItemInventoryRequest implements SelfValidation<ItemInventoryRequest> {

  @NotEmpty
  String hotelId;
  @NotEmpty
  String startDate;
  @NotEmpty
  String endDate;
  List<String> itemCodes;

  public ItemInventoryRequest(String hotelId, String startDate, String endDate, List<String> itemCodes) {
    this.hotelId = hotelId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.itemCodes = itemCodes;
    this.validateSelf();
  }
}
