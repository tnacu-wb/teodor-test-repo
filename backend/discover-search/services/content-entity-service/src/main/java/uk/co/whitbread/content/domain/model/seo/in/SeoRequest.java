package uk.co.whitbread.content.domain.model.seo.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoRequest implements SelfValidation<SeoRequest> {

  private String hotelId;

  @NotEmpty
  private String page;

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  private String bookingFlowId;

}
