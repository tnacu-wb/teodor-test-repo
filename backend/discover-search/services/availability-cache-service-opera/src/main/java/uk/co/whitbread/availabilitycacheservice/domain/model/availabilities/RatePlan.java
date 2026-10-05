package uk.co.whitbread.availabilitycacheservice.domain.model.availabilities;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.common.CommonRatePlan;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RatePlan extends CommonRatePlan {

  private String description;

  private String name;

  private String order;

  private String notes;

  @Valid
  private List<Room> rooms;

  @Valid
  private Price totalPrice;
}
