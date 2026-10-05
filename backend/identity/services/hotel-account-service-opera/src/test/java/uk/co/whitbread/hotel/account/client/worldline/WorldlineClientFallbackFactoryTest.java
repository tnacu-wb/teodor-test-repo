package uk.co.whitbread.hotel.account.client.worldline;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.hotel.account.service.worldline.model.ContactDetails;

class WorldlineClientFallbackFactoryTest {

  public static final String TRUSTED_PARTNER_CREDENTIALS = "trustedCredentials";
  public static final String TETHERED_USER_ID = "tetheredUserId";
  public static final String CULTURE_CODE = "de-DE";
  public static final String COMPANY_NUMBER = "35";
  public static final String IP_ADDRESS = "1.1.1.1";
  @Mock
  private Throwable throwable;

  private WorldlineClientFallbackFactory fallbackFactory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fallbackFactory = new WorldlineClientFallbackFactory();
  }

  @Test
  void testCreate() {
    WorldlineClient fallbackClient = fallbackFactory.create(throwable);

    assertNull(
        fallbackClient.getAccountInfo(TRUSTED_PARTNER_CREDENTIALS, TETHERED_USER_ID, CULTURE_CODE,
            COMPANY_NUMBER, IP_ADDRESS));

    assertNull(fallbackClient
        .updateUserContactDetails(TRUSTED_PARTNER_CREDENTIALS, CULTURE_CODE, COMPANY_NUMBER,
            IP_ADDRESS, TETHERED_USER_ID, TETHERED_USER_ID, ContactDetails.builder().build()));
  }
}
