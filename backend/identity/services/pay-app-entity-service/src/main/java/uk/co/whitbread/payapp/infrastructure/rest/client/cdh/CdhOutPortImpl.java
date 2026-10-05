package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.payapp.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class CdhOutPortImpl implements CdhOutPort {

  private final CdhClient cdhClient;
  private final PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;

  @Override
  public List<TetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email) {
    var cdhResponse = cdhClient.getTetheredGuids(companyId, employeeId, email);
    return pibaTetheredGuidResponseMapper.toDto(cdhResponse);
  }
}
