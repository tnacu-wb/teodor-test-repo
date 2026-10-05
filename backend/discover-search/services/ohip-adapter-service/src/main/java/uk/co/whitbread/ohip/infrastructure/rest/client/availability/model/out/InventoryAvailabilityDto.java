package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAvailabilityDto {

  private String date;
  private Integer total;
  private Integer available;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InventoryAvailabilityDto that = (InventoryAvailabilityDto) o;
    return
        Objects.equals(date, that.date) && Objects.equals(total, that.total) && Objects.equals(
            available, that.available);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, total, available);
  }
}
