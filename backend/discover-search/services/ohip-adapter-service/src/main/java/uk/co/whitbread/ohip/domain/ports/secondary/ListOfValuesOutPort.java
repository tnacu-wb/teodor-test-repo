package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;

public interface ListOfValuesOutPort {

  CancellationReasonsResponse getListOfCancellationReasons(String hotelId);

}
