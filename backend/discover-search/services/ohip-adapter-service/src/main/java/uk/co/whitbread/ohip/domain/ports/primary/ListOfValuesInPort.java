package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;

public interface ListOfValuesInPort {

  CancellationReasonsResponse getListOfCancellationReasons(String hotelId);

}
