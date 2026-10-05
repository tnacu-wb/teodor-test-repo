package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.AxpGenerateOtpRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.GoogleWalletProvisioningRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.RegisterMobileDeviceRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.VerfiyOtpProvisionRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.AxpGenerateOtpResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.GoogleWalletProvisioningResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.RegisterMobileDeviceResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.VerifyOtpProvisionResponseDto;

@Mapper(componentModel = "spring")
public interface AxpMapper {

  OtpResponse toModel(AxpGenerateOtpResponseDto axpGenerateOtpResponseDto);

  OtpProvisionResponse toModel(VerifyOtpProvisionResponseDto verifyOtpProvisionResponseDto);

  @Mapping(source = "email", target = "destination")
  AxpGenerateOtpRequestDto toDto(OtpRequest otpRequest);


  @Mapping(source = "email", target = "otpDestination")
  @Mapping(source = "reservationId", target = "shortPropertyCode")
  VerfiyOtpProvisionRequestDto toDto(OtpProvisionRequest otpProvisionRequest);

  RegisterMobileDeviceRequestDto toRegisterMobileDeviceDto(RegisterMobileDeviceRequest request);

  RegisterMobileDeviceResponse toRegisterMobileDeviceResponse(RegisterMobileDeviceResponseDto dto);

  GoogleWalletProvisioningRequestDto toGoogleWalletProvisioningDto(GoogleWalletProvisioningRequest request);

  GoogleWalletProvisioningResponse toGoogleWalletProvisioningResponse(GoogleWalletProvisioningResponseDto response);
}
