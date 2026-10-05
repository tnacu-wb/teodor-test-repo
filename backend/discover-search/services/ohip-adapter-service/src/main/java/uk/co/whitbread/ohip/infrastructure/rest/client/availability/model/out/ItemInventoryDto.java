package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInventoryDto {

  private String description;
  private String code;
  private String name;
  private List<InventoryAvailabilityDto> inventories;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ItemInventoryDto that = (ItemInventoryDto) o;
    return
        Objects.equals(description, that.description) && Objects.equals(code, that.code)
            && Objects.equals(name, that.name) && Objects.equals(inventories, that.inventories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, code, name, inventories);
  }
}
