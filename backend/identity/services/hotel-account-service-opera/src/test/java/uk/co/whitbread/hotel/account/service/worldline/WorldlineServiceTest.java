package uk.co.whitbread.hotel.account.service.worldline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feign.FeignException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccount;
import uk.co.whitbread.hotel.account.client.worldline.WorldlineClient;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfoResponse;
import uk.co.whitbread.hotel.account.client.worldline.model.ContactDetailsResponse;
import uk.co.whitbread.hotel.account.config.WorldlineProperties;
import uk.co.whitbread.hotel.account.config.WorldlineProperties.PropertiesByLocation;
import uk.co.whitbread.hotel.account.exceptions.WorldlineServiceException;
import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;

@ExtendWith(MockitoExtension.class)
class WorldlineServiceTest {

  public static final String CULTURE_GB_CODE = "en-GB";
  public static final String GB_USERNAME = "gb-username";
  public static final String GB_PASSWORD = "gb-password";
  public static final String CULTURE_DE_CODE = "de-DE";
  public static final String DE_USERNAME = "de-username";
  public static final String DE_PASSWORD = "de-password";
  public static final String TETHERED_USER_ID = "D9600E94-AD84-411C-A8A7-D277A1E3E7D9";
  public static final String GB_COMPANY_NUMBER = "35";
  public static final String DE_COMPANY_NUMBER = "91";
  public static final String IP_ADDRESS = "1.1.1.1";

  @InjectMocks
  private WorldlineService worldlineService;

  @Mock
  private WorldlineClient worldlineClient;

  @Mock
  private WorldlineProperties worldlineProperties;

  @Mock
  private FeignException feignException;

  @Test
  void getAccountInformation_shouldReturnAccountInfo_whenWorldlineReturnsData() {
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    AccountInfo expected = new AccountInfo();
    AccountInfoResponse response = new AccountInfoResponse();
    response.setData(expected);
    when(worldlineClient.getAccountInfo(any(), any(), any(), any(), any())).thenReturn(response);

    AccountInfo result = worldlineService.getAccountInformation(TETHERED_USER_ID, Scheme.GB, IP_ADDRESS);

    assertEquals(expected, result);
  }

  @Test
  void getAccountInformation_shouldThrowWorldlineServiceException_whenNoDataReturned() {
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    AccountInfoResponse response = new AccountInfoResponse(); // data is null
    when(worldlineClient.getAccountInfo(any(), any(), any(), any(), any())).thenReturn(response);

    assertThrows(WorldlineServiceException.class, () ->
        worldlineService.getAccountInformation(TETHERED_USER_ID, Scheme.GB, IP_ADDRESS)
    );
  }

  @Test
  void getAccountInformation_shouldThrowWorldlineServiceException_whenFeignExceptionThrown() {
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    when(worldlineClient.getAccountInfo(any(), any(), any(), any(), any()))
        .thenThrow(Mockito.mock(FeignException.class));

    assertThrows(WorldlineServiceException.class, () ->
        worldlineService.getAccountInformation(TETHERED_USER_ID, Scheme.GB, IP_ADDRESS)
    );
  }

  @Test
  void updateContactDetails_success() {
    //Given
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    when(worldlineClient.updateUserContactDetails(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(ContactDetailsResponse.builder().build());

    //When
    worldlineService.updateContactDetails(buildPibaAccount(), buildCustomerRequest());

    //Then
    verify(worldlineClient).updateUserContactDetails(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  void updateContactDetails_missingFieldsOnTheRequest() {
    //Given
    var contactDetails = new CustomerRequest();
    contactDetails.setContactDetail(new ContactDetail());

    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    when(worldlineClient.updateUserContactDetails(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(ContactDetailsResponse.builder().build());

    //When
    worldlineService.updateContactDetails(buildPibaAccount(), contactDetails);

    //Then
    verify(worldlineClient).updateUserContactDetails(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  void updateContactDetails_whenValidationFails_throwsWorldlineServiceException() {
    //Given
    PibaAccount pibaAccount = buildPibaAccount();
    CustomerRequest customerRequest = buildCustomerRequest();
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    when(worldlineClient.updateUserContactDetails(any(), any(), any(), any(), any(), any(), any()))
    .thenThrow(feignException);
    when(feignException.contentUTF8()).thenReturn(
        """
            {
                "responseCode": "422",
                "data": null,
                "errors": [
                    {
                        "code": "InvalidTitle",
                        "target": "Title",
                        "message": "Title is invalid."
                    }
                ]
            }
            """);
    //Then
    assertThrows(WorldlineServiceException.class,
        () -> worldlineService.updateContactDetails(pibaAccount, customerRequest));
  }

  @Test
  void updateContactDetails_throwsWorldlineServiceException() {
    //Given
    PibaAccount pibaAccount = buildPibaAccount();
    CustomerRequest customerRequest = buildCustomerRequest();
    when(worldlineProperties.getGb()).thenReturn(getGbProperties());
    when(worldlineClient.updateUserContactDetails(any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(feignException);
    when(feignException.contentUTF8()).thenReturn(
        """
            {
                "responseCode": "400",
                "data": null,
                "errors": [
                    {
                        "code": "400",
                        "target": "Request",
                        "message": "Company Number header is missing."
                    }
                ]
            }
            """);
    //Then
    assertThrows(WorldlineServiceException.class,
        () -> worldlineService.updateContactDetails(pibaAccount, customerRequest));
  }

  private PibaAccount buildPibaAccount() {
    return PibaAccount.builder().accountName("accountName").accountNumber("accountNumber")
        .apiUserGuid("apiUserGuid").tetheredGuid("tetheredGuid").scheme(Scheme.GB)
        .schemeCustomerId(35).build();
  }

  @NotNull
  private CustomerRequest buildCustomerRequest() {
    var customerRequest = new CustomerRequest();

    var contactDetail = new ContactDetail();
    contactDetail.setTitle("title");
    contactDetail.setFirstName("firstName");
    contactDetail.setLastName("lastName");
    contactDetail.setEmail("email");
    contactDetail.setTelephone("phone");
    contactDetail.setMobile("mobile");

    customerRequest.setContactDetail(contactDetail);

    return customerRequest;
  }

  private static PropertiesByLocation getGbProperties() {
    return new PropertiesByLocation(CULTURE_GB_CODE, GB_USERNAME, GB_PASSWORD, GB_COMPANY_NUMBER);
  }

}
