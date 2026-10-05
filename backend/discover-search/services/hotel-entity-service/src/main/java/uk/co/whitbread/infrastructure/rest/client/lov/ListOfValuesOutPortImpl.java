package uk.co.whitbread.infrastructure.rest.client.lov;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.domain.ports.secondary.ListOfValuesOutPort;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.lov.mapper.CancellationReasonsMapper;

@Component
@RequiredArgsConstructor
public class ListOfValuesOutPortImpl implements ListOfValuesOutPort {

  private final OhipClient ohipClient;
  private final CancellationReasonsMapper cancellationReasonsMapper;

  @Override
  public CancellationReasonsResponse getListOfCancellationReasons(String hotelId) {
    return cancellationReasonsMapper.toDomainModel(ohipClient.getListOfCancellationReasons(hotelId));
  }

}
