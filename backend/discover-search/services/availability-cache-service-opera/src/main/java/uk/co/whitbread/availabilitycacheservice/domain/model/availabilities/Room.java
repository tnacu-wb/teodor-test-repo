package uk.co.whitbread.availabilitycacheservice.domain.model.availabilities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotEmpty
  private String type;

  private Boolean substitutedType;

  private Integer adults;

  private Integer children;

  private Boolean cotRequired;

  @Valid
  private Price totalPrice;

  private Long qtyRequested;

  @NotNull
  private Integer quantityAvailable;

  @Builder.Default
  private Boolean limitedAvailability = false;
}
