package uk.co.whitbread.payapp.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;

public interface CdhOutPort {

  List<TetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email);
}
