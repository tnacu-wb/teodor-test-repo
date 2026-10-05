package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;

public interface ListOfValuesInPort {

  CancellationReasonsResponse getListOfCancellationReasons(String hotelId);

}
