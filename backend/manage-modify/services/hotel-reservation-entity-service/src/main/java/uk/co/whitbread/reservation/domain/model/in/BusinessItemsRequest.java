package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsRequest {

  @NotEmpty
  private List<String> reservationIds;

  @NotEmpty
  private String hotelId;

  private String companyId;

  @NotNull
  private BusinessItems businessItems;
  private String channel;
}
