package uk.co.whitbread.digitalkey.domain.logic;

import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.UDFC20;
import static uk.co.whitbread.digitalkey.domain.utils.SanitizingUtils.sanitize;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CharacterUdfOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.CharacterUdf;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;

@AllArgsConstructor
@Slf4j
public class CharacterUdfInPortImpl implements CharacterUdfInPort {

  private final CharacterUdfOutPort characterUdfOutPort;

  public void updateUdfc20(String reservationId, String hotelId, String ciolStatus) {
    log.info("log_check_in : Request to update UDFC20: ReservationId = {}, CiolStatus = {}",
            sanitize(reservationId), sanitize(ciolStatus));
    UpdateUdfc20Request request = UpdateUdfc20Request.builder()
                .reservationIds(Set.of(reservationId))
                .hotelId(hotelId)
                .udfs(List.of(new CharacterUdf(UDFC20, ciolStatus)))
                .build();
    characterUdfOutPort.updateUdfc20(request);
  }

}