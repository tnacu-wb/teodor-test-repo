package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;

public interface ListOfValuesOutPort {

  CancellationReasonsResponse getListOfCancellationReasons(String hotelId);

}
