package uk.co.whitbread.content.domain.model.hotel.out;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Facts {

  @NotNull
  private List<@NotNull FactItem> factItems;
}
