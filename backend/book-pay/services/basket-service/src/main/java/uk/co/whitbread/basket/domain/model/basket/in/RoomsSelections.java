package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsSelections {
  private List<PackagesSelection> packagesSelection;
}
