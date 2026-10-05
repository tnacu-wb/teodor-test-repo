package uk.co.whitbread.hotel.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;

@Service
@Slf4j
@RequiredArgsConstructor
public class InnBusinessService {

  private final WorldlineService worldlineService;

  public AccountInfo getAccountInfo(
      String tetherUserGuid, Scheme scheme, String clientIp) {

    return worldlineService.getAccountInformation(tetherUserGuid,
        scheme,
        clientIp);
  }

}
