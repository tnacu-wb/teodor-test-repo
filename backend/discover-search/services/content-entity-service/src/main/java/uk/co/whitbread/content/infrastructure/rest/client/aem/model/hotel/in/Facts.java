package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facts {

  @NotNull
  private List<@NotNull FactItem> factItems;

}