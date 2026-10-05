package uk.co.whitbread.hotel.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;

@ExtendWith(MockitoExtension.class)
class InnBusinessServiceTest {

  @Mock
  private WorldlineService worldlineService;

  @InjectMocks
  private InnBusinessService sut;

  @Test
  void shouldReturnAccountInfoSuccessfully() {
    String tetherUserGuid = "tetherGuid1";
    String clientIp = "127.0.0.1";
    Scheme scheme = Scheme.GB;
    AccountInfo expectedAccountInfo = new AccountInfo();

    when(worldlineService.getAccountInformation(tetherUserGuid, Scheme.GB, clientIp)).thenReturn(
        expectedAccountInfo);

    AccountInfo result = sut.getAccountInfo(tetherUserGuid, scheme, clientIp);

    assertEquals(expectedAccountInfo, result);
  }

}
