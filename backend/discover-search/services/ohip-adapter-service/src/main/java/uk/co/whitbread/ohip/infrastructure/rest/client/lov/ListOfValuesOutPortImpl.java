package uk.co.whitbread.ohip.infrastructure.rest.client.lov;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.ListOfValuesOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.OhipListOfValuesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper.CancellationReasonsOhipMapper;


@RequiredArgsConstructor
@Slf4j
public class ListOfValuesOutPortImpl implements ListOfValuesOutPort {

  private final OhipListOfValuesClient ohipListOfValuesClient;

  private final CancellationReasonsOhipMapper cancellationReasonsOhipMapper;

  @Override
  public CancellationReasonsResponse getListOfCancellationReasons(String hotelId) {
    return cancellationReasonsOhipMapper.toCancellationReasonsResponseModel(
        ohipListOfValuesClient.getListOfCancellationReasons(hotelId));
  }
}
