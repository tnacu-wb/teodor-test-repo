package uk.co.whitbread.digitalkey.infrastructure.rest.client;

import static uk.co.whitbread.digitalkey.domain.utils.SanitizingUtils.sanitize;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.domain.model.axp.OtpType;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.domain.ports.secondary.AxpOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.service.AxpClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.mapper.AxpMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.AxpGenerateOtpRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.AxpGenerateOtpResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.VerifyOtpProvisionResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDto;


@RequiredArgsConstructor
@Slf4j
public class AxpOutPortImpl implements AxpOutPort {
  private final AxpClient axpClient;
  private final AxpMapper axpMapper;
  private final OhipAdapterClient ohipAdapterClient;


  @Override
  public OtpResponse sendOtp(OtpRequest request) {
    AxpGenerateOtpRequestDto axpGenerateOtpRequestDto = axpMapper.toDto(request);
    axpGenerateOtpRequestDto.setType(OtpType.EMAIL.getValue());
    AxpGenerateOtpResponseDto axpGenerateOtpResponseDto = axpClient.generateOtp(axpGenerateOtpRequestDto);
    return axpMapper.toModel(axpGenerateOtpResponseDto);
  }

  @Override
  public OtpProvisionResponse verifyOtpAndGetDigitalKey(OtpProvisionRequest otpProvisionRequest) {
    var reservationDetailsEnhancedDto =
        ohipAdapterClient.sendGetReservationsByExternalReferenceId(otpProvisionRequest.getBookingReference());
    var verifyOtpProvisionRequest = axpMapper.toDto(otpProvisionRequest);
    verifyOtpProvisionRequest.setOtpType(OtpType.EMAIL.getValue());
    List<ReservationInfoDto> reservationInfoList = Optional.ofNullable(reservationDetailsEnhancedDto)
        .map(ReservationDetailsEnhancedDto::getReservationsDetailsResponse)
        .map(ReservationsDetailsResponseDto::getReservations)
        .map(ReservationsDto::getReservationInfo)
        .orElse(Collections.emptyList());

    if (!reservationInfoList.isEmpty()) {
      verifyOtpProvisionRequest.setShortPropertyCode(reservationInfoList.get(0).getHotelId());
      ReservationGuestDto guestName = reservationInfoList.get(0).getReservationGuest();
      if (guestName != null) {
        verifyOtpProvisionRequest.setFirstName(guestName.getGivenName());
        verifyOtpProvisionRequest.setLastName(guestName.getSurname());
      }
    }

    VerifyOtpProvisionResponseDto verifyOtpProvisionResponseDto =
        axpClient.passProvisioningWithOtp(verifyOtpProvisionRequest);
    return axpMapper.toModel(verifyOtpProvisionResponseDto);
  }

  @Override
  public RegisterMobileDeviceResponse registerMobileDevice(RegisterMobileDeviceRequest request) {
    var dto = axpMapper.toRegisterMobileDeviceDto(request);
    var responseDto = axpClient.registerMobileDevice(dto);
    log.info("log_register_mobile_device : Device registered for ref={}, deviceId={}, mobileDeviceId={}", sanitize(
            request.getBookingReference()), sanitize(request.getDeviceId()), sanitize(responseDto.getMobileDeviceId()));
    return axpMapper.toRegisterMobileDeviceResponse(responseDto);
  }

  @Override
  public GoogleWalletProvisioningResponse googleWalletProvisioningWithOtp(GoogleWalletProvisioningRequest request) {
    var dto = axpMapper.toGoogleWalletProvisioningDto(request);
    var responseDto = axpClient.googleWalletProvisioningWithOtp(dto);
    log.info("log_google_wallet_provisioning : Provisioned for bookingRef={}, hotelId={}",
            sanitize(request.getBookingReference()), sanitize(request.getShortPropertyCode()));
    return axpMapper.toGoogleWalletProvisioningResponse(responseDto);
  }
}
