package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.ListOfValuesOutPort;

@Slf4j
@RequiredArgsConstructor
public class ListOfValuesInPortImpl implements ListOfValuesInPort {

  private final ListOfValuesOutPort listOfValuesOutPort;

  @Override
  public CancellationReasonsResponse getListOfCancellationReasons(String hotelId) {
    return this.listOfValuesOutPort.getListOfCancellationReasons(hotelId);
  }

}
