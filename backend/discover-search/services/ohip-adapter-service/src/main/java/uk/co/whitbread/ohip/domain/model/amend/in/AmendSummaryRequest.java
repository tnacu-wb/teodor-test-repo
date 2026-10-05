package uk.co.whitbread.ohip.domain.model.amend.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmendSummaryRequest {
  private String hotelId;
  private List<String> reservationIds;
}
