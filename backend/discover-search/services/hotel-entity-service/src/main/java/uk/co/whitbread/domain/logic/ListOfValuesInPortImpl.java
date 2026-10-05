package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils.sanitize;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.domain.ports.secondary.ListOfValuesOutPort;

@Slf4j
public class ListOfValuesInPortImpl implements ListOfValuesInPort {

  private final ListOfValuesOutPort listOfValuesOutPort;

  public ListOfValuesInPortImpl(final ListOfValuesOutPort listOfValuesOutPort) {
    this.listOfValuesOutPort = listOfValuesOutPort;
  }

  @Override
  public CancellationReasonsResponse getListOfCancellationReasons(String hotelId) {
    log.debug("Entered getListOfCancellationReasons for hotelId={}", sanitize(hotelId));
    return this.listOfValuesOutPort.getListOfCancellationReasons(hotelId);
  }

}
