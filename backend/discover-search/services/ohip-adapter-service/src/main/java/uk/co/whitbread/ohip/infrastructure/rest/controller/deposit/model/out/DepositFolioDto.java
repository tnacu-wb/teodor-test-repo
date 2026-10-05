package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioChargeDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioDto {

  private String hotelId;

  private String reservationId;

  private String vatRegion;

  private List<DepositFolioChargeDto> charges;

}
