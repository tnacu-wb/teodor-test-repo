package uk.co.whitbread.digitalkey.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.domain.ports.secondary.AxpOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DigitalKeyInPortTest {

  @Mock
  private AxpOutPort axpOutPort;

  @Mock
  private AxpProperties axpProperties;

  private DigitalKeyInPortImpl digitalKeyInPortImpl;


  @BeforeEach
  void init() {
    digitalKeyInPortImpl = new DigitalKeyInPortImpl(axpOutPort);
  }

  @Test
  void testGemerateOtp_success() {
    // Arrange
    when(axpOutPort.sendOtp(any()))
        .thenReturn(createOtpResponse());

    // Act
    var response = digitalKeyInPortImpl.generateOtp(createOtpRequest());

    // Assert
    assertTrue(response.isSuccess());
  }

  @Test
  void testPassProvisioningWithOtp_success() {
    // Arrange
    when(axpOutPort.verifyOtpAndGetDigitalKey(any()))
        .thenReturn(createOtpProvisionResponse());

    // Act
    var response = digitalKeyInPortImpl.passProvisioningWithOtp(createOtpProvisionRequest());

    // Assert
    assertEquals("cred-123", response.getProvisioningCredentialIdentifier());
    assertEquals("env-prod", response.getServerEnvironmentIdentifier());
  }


  private OtpRequest createOtpRequest() {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setEmail("test@test.com");
    return otpRequest;
  }

  private OtpResponse createOtpResponse() {
    OtpResponse otpResponse = new OtpResponse();
    otpResponse.setSuccess(true);
    return otpResponse;
  }

  private OtpProvisionRequest createOtpProvisionRequest() {
    OtpProvisionRequest otpProvisionRequest = new OtpProvisionRequest();
    otpProvisionRequest.setOtpCode("123456");
    otpProvisionRequest.setEmail("test@test.com");
    otpProvisionRequest.setBookingReference("BOOK123");
    otpProvisionRequest.setReservationId("12345");
    return otpProvisionRequest;
  }

  private OtpProvisionResponse createOtpProvisionResponse() {
    OtpProvisionResponse otpProvisionResponse = new OtpProvisionResponse();
    otpProvisionResponse.setProvisioningCredentialIdentifier("cred-123");
    otpProvisionResponse.setSharingInstanceIdentifier("share-456");
    otpProvisionResponse.setCardConfigurationIdentifier("config-789");
    otpProvisionResponse.setCardTemplateIdentifier("template-101");
    otpProvisionResponse.setServerEnvironmentIdentifier("env-prod");
    otpProvisionResponse.setAccountHash("hash-abc123");
    otpProvisionResponse.setRelyingPartyIdentifier("party-xyz");
    return otpProvisionResponse;
  }

  @Test
  void testRegisterMobileDevice_callsOutPort() {
    // Arrange
    RegisterMobileDeviceRequest request = new RegisterMobileDeviceRequest();
    RegisterMobileDeviceResponse response = new RegisterMobileDeviceResponse();
    when(axpOutPort.registerMobileDevice(request)).thenReturn(response);
    // Act
    RegisterMobileDeviceResponse result = digitalKeyInPortImpl.registerMobileDevice(request);
    // Assert
    assertEquals(response, result);
    verify(axpOutPort).registerMobileDevice(request);
  }

  @Test
  void testGoogleWalletProvisioningWithOtp_callsOutPort() {
    // Arrange
    GoogleWalletProvisioningRequest request = new GoogleWalletProvisioningRequest();
    request.setBookingReference("BOOK123");

    GoogleWalletProvisioningResponse response = new GoogleWalletProvisioningResponse();
    response.setCredentialToken("WL4TMR33T7E4RFMR");

    when(axpOutPort.googleWalletProvisioningWithOtp(request)).thenReturn(response);

    // Act
    GoogleWalletProvisioningResponse result = digitalKeyInPortImpl.googleWalletProvisioningWithOtp(request);

    // Assert
    assertEquals(response, result);
    verify(axpOutPort).googleWalletProvisioningWithOtp(request);
  }
}
