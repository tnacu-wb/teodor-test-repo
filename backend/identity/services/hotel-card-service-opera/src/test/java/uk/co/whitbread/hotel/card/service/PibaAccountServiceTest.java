package uk.co.whitbread.hotel.card.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.client.piba.PibaAccountClient;
import uk.co.whitbread.hotel.card.client.piba.model.TetheredGuidDetails;
import uk.co.whitbread.hotel.card.client.piba.model.TetheredUserRequest;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;

@ExtendWith(MockitoExtension.class)
class PibaAccountServiceTest {

  public static final String COMPANY_ID = "companyId";
  public static final String EMPLOYEE_ID = "employeeId";
  public static final String TETHERED_USER_GUID = "tetheredUserGuid";
  public static final String ACCESSED_BY = "accessedBy";
  @Mock
  private PibaAccountClient pibaAccountClient;

  @InjectMocks
  private PibaAccountService pibaAccountService;

  @Test
  void registerTetheredUser_ShouldNotThrowError() {
    // Arrange
    doNothing().when(pibaAccountClient)
        .registerTetheredUser(ACCESSED_BY,
            getTetheredUserRequest());

    // Act
    pibaAccountService.registerTetheredUser(COMPANY_ID, Scheme.GB, EMPLOYEE_ID, TETHERED_USER_GUID,
        ACCESSED_BY);

    // Assert
    verify(pibaAccountClient, times(1)).registerTetheredUser(any(), any());
  }

  private static TetheredUserRequest getTetheredUserRequest() {
    return new TetheredUserRequest(Scheme.GB, COMPANY_ID,
        List.of(new TetheredGuidDetails(EMPLOYEE_ID, TETHERED_USER_GUID)));
  }
}
