package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEligibilityRequestDto {

  @NotNull
  private Region region;

  @NotNull
  private Channel channel;

  @NotEmpty
  private List<Platform> platforms;
}