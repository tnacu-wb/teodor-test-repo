package uk.co.whitbread.hotel.card.service;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.client.piba.PibaAccountClient;
import uk.co.whitbread.hotel.card.client.piba.model.TetheredGuidDetails;
import uk.co.whitbread.hotel.card.client.piba.model.TetheredUserRequest;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;

@Slf4j
@Service
@RequiredArgsConstructor
public class PibaAccountService {

  private final PibaAccountClient pibaAccountClient;

  public void registerTetheredUser(String companyId, Scheme scheme, String employeeId,
      String tetheredUserGuid, String authorization) {
    log.info(
        "Register tethered guid in CDH request for company id {}, employee id {} and tetheredGuids id {}",
        companyId, employeeId, tetheredUserGuid);

    var tetheredUserRequest = new TetheredUserRequest();
    tetheredUserRequest.setCompanyId(companyId);
    tetheredUserRequest.setScheme(scheme);
    tetheredUserRequest.setTetheredGuids(List.of(new TetheredGuidDetails(
        employeeId,
        tetheredUserGuid)));
    pibaAccountClient.registerTetheredUser(authorization, tetheredUserRequest);
  }

}
