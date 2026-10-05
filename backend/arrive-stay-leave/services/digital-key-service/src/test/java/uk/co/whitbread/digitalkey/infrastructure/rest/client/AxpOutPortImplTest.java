package uk.co.whitbread.digitalkey.infrastructure.rest.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.service.AxpClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.mapper.AxpMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.AxpGenerateOtpRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.GoogleWalletProvisioningRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.RegisterMobileDeviceRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.VerfiyOtpProvisionRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.AxpGenerateOtpResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.GoogleWalletProvisioningResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.RegisterMobileDeviceResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.VerifyOtpProvisionResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AxpOutPortImplTest {

  private AxpClient axpClient;
  private AxpMapper axpMapper;
  private OhipAdapterClient ohipAdapterClient;
  private AxpOutPortImpl axpOutPort;

  @BeforeEach
  void setUp() {
    axpClient = mock(AxpClient.class);
    axpMapper = mock(AxpMapper.class);
    ohipAdapterClient = mock(OhipAdapterClient.class);
    axpOutPort = new AxpOutPortImpl(axpClient, axpMapper, ohipAdapterClient);
  }

  @Test
  void testSendOtp() {
    OtpRequest otpRequest = new OtpRequest();
    AxpGenerateOtpRequestDto dto = new AxpGenerateOtpRequestDto();
    AxpGenerateOtpResponseDto responseDto = new AxpGenerateOtpResponseDto();
    OtpResponse expectedResponse = new OtpResponse();

    when(axpMapper.toDto(otpRequest)).thenReturn(dto);
    when(axpClient.generateOtp(dto)).thenReturn(responseDto);
    when(axpMapper.toModel(responseDto)).thenReturn(expectedResponse);

    OtpResponse actualResponse = axpOutPort.sendOtp(otpRequest);

    assertEquals(expectedResponse, actualResponse);
    assertEquals("email", dto.getType());
    verify(axpMapper).toDto(otpRequest);
    verify(axpClient).generateOtp(dto);
    verify(axpMapper).toModel(responseDto);
  }

  @Test
  void testVerifyOtpAndGetDigitalKey_withBillingInfo() {
    OtpProvisionRequest provisionRequest = new OtpProvisionRequest();
    provisionRequest.setBookingReference("BR123");

    ReservationDetailsEnhancedDto reservatinDetailsEnhancedDtoDto = new ReservationDetailsEnhancedDto();
    ReservationsDetailsResponseDto reservationsDetailsResponseDto = new ReservationsDetailsResponseDto();
    ReservationsDto reservationsDto = new ReservationsDto();
    ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
    ReservationGuestDto reservationGuest = new ReservationGuestDto();

    reservationGuest.setGivenName("John");
    reservationGuest.setSurname("Doe");
    reservationInfoDto.setReservationGuest(reservationGuest);
    reservationsDto.addReservationInfoItem(reservationInfoDto);
    reservationsDetailsResponseDto.setReservations(reservationsDto);
    reservatinDetailsEnhancedDtoDto.setReservationsDetailsResponse(reservationsDetailsResponseDto);
    VerfiyOtpProvisionRequestDto verifyDto = new VerfiyOtpProvisionRequestDto();
    VerifyOtpProvisionResponseDto verifyResponseDto = new VerifyOtpProvisionResponseDto();
    OtpProvisionResponse expectedResponse = new OtpProvisionResponse();

    when(ohipAdapterClient.sendGetReservationsByExternalReferenceId("BR123")).thenReturn(reservatinDetailsEnhancedDtoDto);
    when(axpMapper.toDto(provisionRequest)).thenReturn(verifyDto);
    when(axpClient.passProvisioningWithOtp(verifyDto)).thenReturn(verifyResponseDto);
    when(axpMapper.toModel(verifyResponseDto)).thenReturn(expectedResponse);

    OtpProvisionResponse actualResponse = axpOutPort.verifyOtpAndGetDigitalKey(provisionRequest);

    assertEquals(expectedResponse, actualResponse);
    assertEquals("email", verifyDto.getOtpType());
    assertEquals("John", verifyDto.getFirstName());
    assertEquals("Doe", verifyDto.getLastName());
  }

  @Test
  void testVerifyOtpAndGetDigitalKey_withoutBillingInfo() {
    OtpProvisionRequest provisionRequest = new OtpProvisionRequest();
    provisionRequest.setBookingReference("BR123");

    ReservationDetailsEnhancedDto reservationDto = new ReservationDetailsEnhancedDto(); // no billing
    VerfiyOtpProvisionRequestDto verifyDto = new VerfiyOtpProvisionRequestDto();
    VerifyOtpProvisionResponseDto verifyResponseDto = new VerifyOtpProvisionResponseDto();
    OtpProvisionResponse expectedResponse = new OtpProvisionResponse();

    when(ohipAdapterClient.sendGetReservationsByExternalReferenceId("BR123")).thenReturn(reservationDto);
    when(axpMapper.toDto(provisionRequest)).thenReturn(verifyDto);
    when(axpClient.passProvisioningWithOtp(verifyDto)).thenReturn(verifyResponseDto);
    when(axpMapper.toModel(verifyResponseDto)).thenReturn(expectedResponse);

    OtpProvisionResponse actualResponse = axpOutPort.verifyOtpAndGetDigitalKey(provisionRequest);

    assertEquals(expectedResponse, actualResponse);
    assertEquals("email", verifyDto.getOtpType());
    assertNull(verifyDto.getFirstName());
    assertNull(verifyDto.getLastName());
  }

  @Test
  void testRegisterMobileDevice_success() {
    RegisterMobileDeviceRequest request = new RegisterMobileDeviceRequest();
    request.setBookingReference("BOOK123");
    request.setDeviceId("device-123");

    RegisterMobileDeviceRequestDto requestDto = new RegisterMobileDeviceRequestDto();

    RegisterMobileDeviceResponseDto responseDto = new RegisterMobileDeviceResponseDto();
    responseDto.setMobileDeviceId("uuid-123");

    RegisterMobileDeviceResponse expectedResponse = new RegisterMobileDeviceResponse();
    expectedResponse.setMobileDeviceId("uuid-123");

    when(axpMapper.toRegisterMobileDeviceDto(request)).thenReturn(requestDto);
    when(axpClient.registerMobileDevice(requestDto)).thenReturn(responseDto);
    when(axpMapper.toRegisterMobileDeviceResponse(responseDto)).thenReturn(expectedResponse);

    RegisterMobileDeviceResponse actualResponse = axpOutPort.registerMobileDevice(request);

    assertNotNull(actualResponse);
    assertEquals("uuid-123", actualResponse.getMobileDeviceId());

    verify(axpMapper, times(1)).toRegisterMobileDeviceDto(request);
    verify(axpClient, times(1)).registerMobileDevice(requestDto);
    verify(axpMapper, times(1)).toRegisterMobileDeviceResponse(responseDto);
  }

  @Test
  void testGoogleWalletProvisioningWithOtp_success() {
    // Arrange
    GoogleWalletProvisioningRequest request = new GoogleWalletProvisioningRequest();
    request.setBookingReference("BOOK123");
    request.setShortPropertyCode("LINMIL");
    request.setLinkingToken("link-token");
    request.setWalletUserId("wallet-user");

    GoogleWalletProvisioningRequestDto requestDto = new GoogleWalletProvisioningRequestDto();
    GoogleWalletProvisioningResponseDto responseDto = new GoogleWalletProvisioningResponseDto();
    responseDto.setCredentialToken("WL4TMR33T7E4RFMR");
    GoogleWalletProvisioningResponse expectedResponse = new GoogleWalletProvisioningResponse();
    expectedResponse.setCredentialToken("WL4TMR33T7E4RFMR");

    when(axpMapper.toGoogleWalletProvisioningDto(request)).thenReturn(requestDto);
    when(axpClient.googleWalletProvisioningWithOtp(requestDto)).thenReturn(responseDto);
    when(axpMapper.toGoogleWalletProvisioningResponse(responseDto)).thenReturn(expectedResponse);

    // Act
    GoogleWalletProvisioningResponse actualResponse = axpOutPort.googleWalletProvisioningWithOtp(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals("WL4TMR33T7E4RFMR", actualResponse.getCredentialToken());
    verify(axpMapper, times(1)).toGoogleWalletProvisioningDto(request);
    verify(axpClient, times(1)).googleWalletProvisioningWithOtp(requestDto);
    verify(axpMapper, times(1)).toGoogleWalletProvisioningResponse(responseDto);
  }
}