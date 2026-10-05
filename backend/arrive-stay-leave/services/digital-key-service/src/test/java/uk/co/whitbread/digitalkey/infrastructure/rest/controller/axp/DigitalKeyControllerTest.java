package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.domain.ports.primary.DigitalKeyInPort;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class DigitalKeyControllerTest {

  @InjectMocks
  private DigitalKeyController digitalKeyControllerTest;

  @Mock
  private DigitalKeyInPort digitalKeyInPort;

  @Test
  void testGenerateOtpSuccess()  {
    OtpRequest request = new OtpRequest();
    request.setEmail("test@example.com");

    OtpResponse responseObj = new OtpResponse();
    responseObj.setSuccess(true);

    when(digitalKeyInPort.generateOtp(any(OtpRequest.class))).thenReturn(responseObj);

    final ResponseEntity<OtpResponse> response =
        digitalKeyControllerTest.generateOtp(request);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void testGenerateOtpPassProvioning()  {
    OtpProvisionRequest request = new OtpProvisionRequest();
    request.setOtpCode("123456");

    OtpProvisionResponse responseObj = new OtpProvisionResponse();
    responseObj.setCardTemplateIdentifier("123");
    when(digitalKeyInPort.passProvisioningWithOtp(any(OtpProvisionRequest.class))).thenReturn(responseObj);

    final ResponseEntity<OtpProvisionResponse> response =
        digitalKeyControllerTest.passProvisioningWithOtp(request);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void registerMobileDeviceTest() {
    RegisterMobileDeviceRequest request = new RegisterMobileDeviceRequest();
    request.setShortPropertyCode("LINMIL");
    request.setBookingReference("AKN9700904");
    request.setFirstName("Digital");
    request.setLastName("Keytest");
    request.setDeviceId("device-123");
    request.setDeviceManufacturer("Samsung");
    request.setDeviceModel("SM-F721B");
    request.setOsVersion("16");
    request.setAppVersion("4.35.0");

    RegisterMobileDeviceResponse responseObj = new RegisterMobileDeviceResponse();
    responseObj.setMobileDeviceId("uuid-123");
    when(digitalKeyInPort.registerMobileDevice(any(RegisterMobileDeviceRequest.class))).thenReturn(responseObj);
    ResponseEntity<RegisterMobileDeviceResponse> response = digitalKeyControllerTest.registerMobileDevice(request);

    assertNotNull(response);
    assertEquals("Equals", HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Equals","uuid-123", response.getBody().getMobileDeviceId());

    verify(digitalKeyInPort, times(1)).registerMobileDevice(any(RegisterMobileDeviceRequest.class));
  }

  @Test
  void googleWalletProvisioningWithOtpTest() {
    // Arrange
    GoogleWalletProvisioningRequest request = new GoogleWalletProvisioningRequest();
    request.setOtpCode("123456");
    request.setOtpType("email");
    request.setOtpDestination("test@mail.com");
    request.setShortPropertyCode("LINMIL");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setBookingReference("BOOK123");
    request.setLinkingToken("linking-token");
    request.setWalletUserId("wallet-user-id");

    GoogleWalletProvisioningResponse responseObj = new GoogleWalletProvisioningResponse();
    responseObj.setCredentialToken("WL4TMR33T7E4RFMR");
    responseObj.setProvisioningUrl(null);

    when(digitalKeyInPort.googleWalletProvisioningWithOtp(any(GoogleWalletProvisioningRequest.class))).thenReturn(responseObj);

    // Act
    ResponseEntity<GoogleWalletProvisioningResponse> response = digitalKeyControllerTest.googleWalletProvisioningWithOtp(request);

    // Assert
    assertNotNull(response);
    assertEquals("Status code mismatch", HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Credential token mismatch","WL4TMR33T7E4RFMR", response.getBody().getCredentialToken());
    verify(digitalKeyInPort, times(1)).googleWalletProvisioningWithOtp(any(GoogleWalletProvisioningRequest.class));
  }
}
