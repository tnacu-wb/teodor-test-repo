package uk.co.whitbread.ohip.domain.model.availability.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class InventoryAvailability implements SelfValidation<InventoryAvailability> {

  private String date;
  private Integer total;
  private Integer available;

  public InventoryAvailability(String date, Integer total, Integer available) {
    this.date = date;
    this.total = total;
    this.available = available;
    this.validateSelf();
  }
}
