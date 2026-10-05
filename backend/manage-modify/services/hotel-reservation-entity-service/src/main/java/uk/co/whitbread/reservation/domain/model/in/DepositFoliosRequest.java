package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFoliosRequest {
  private List<DepositFolioRequest> depositFolios;
}