package uk.co.whitbread.hotel.card.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.client.account.model.AccessLevel;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhAuthorizationService {

  public boolean isSuperAccessLevelUser(CdhEmployeeDetails tokenDetails) {
    return AccessLevel.SUPER.name().equals(tokenDetails.getAccessLevel().toUpperCase());
  }

  public boolean isSameEntity(String idFromToken, String idFromPath) {
    return Objects.equals(idFromToken, idFromPath);
  }

}
