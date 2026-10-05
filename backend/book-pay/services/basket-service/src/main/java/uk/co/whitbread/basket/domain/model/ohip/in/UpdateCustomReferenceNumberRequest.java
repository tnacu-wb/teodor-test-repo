package uk.co.whitbread.basket.domain.model.ohip.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomReferenceNumberRequest {

  @NotEmpty
  private Set<String> reservationIds;

  @NotEmpty
  private String hotelId;

  @NotNull
  private String customReferenceNumber;

}
