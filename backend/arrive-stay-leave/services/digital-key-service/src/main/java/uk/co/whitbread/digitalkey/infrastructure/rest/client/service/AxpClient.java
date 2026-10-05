package uk.co.whitbread.digitalkey.infrastructure.rest.client.service;

import static uk.co.whitbread.digitalkey.ErrorCode.Constants.ALLIANTS_INTERNAL_ERROR;
import static uk.co.whitbread.digitalkey.ErrorCode.Constants.BAD_REQUEST;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.digitalkey.ErrorCode;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpConstants;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.AxpProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.AxpErrorResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.AxpServiceException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception.ResourceNotFoundException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.AxpGenerateOtpRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.GoogleWalletProvisioningRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.RegisterMobileDeviceRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in.VerfiyOtpProvisionRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.AxpGenerateOtpResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.GoogleWalletProvisioningResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.RegisterMobileDeviceResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out.VerifyOtpProvisionResponseDto;

@Slf4j
@Component
public class AxpClient {

  public static final String AXP_EXCEPTION_MSG =
      "An exception was returned by the Axp Service";
  private static final String BEARER = "Bearer ";
  private final WebClient axpWebClient;
  private final AxpProperties axpProperties;

  public AxpClient(@Qualifier("axpWebClient") WebClient axpWebClient,
                   AxpProperties axpProperties) {
    this.axpWebClient = axpWebClient;
    this.axpProperties = axpProperties;
  }

  public AxpGenerateOtpResponseDto generateOtp(AxpGenerateOtpRequestDto axpGenerateOtpRequestDto) {
    return axpWebClient
        .post()
        .uri(uriBuilder -> uriBuilder
            .path(axpProperties.getGenerateOtpEndPoint())
            .build(axpProperties.getBrandId()))
        .header(HttpHeaders.AUTHORIZATION, BEARER + axpProperties.getApiKey())
        .body(Mono.just(axpGenerateOtpRequestDto), AxpGenerateOtpRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Error while generating OTP with AXP");
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new AxpServiceException(ErrorCode.ALLIANTS_INTERNAL_ERROR,
                  "An error was returned by AXP service!"));
        })
        .bodyToMono(AxpGenerateOtpResponseDto.class)
        .block();
  }

  public VerifyOtpProvisionResponseDto passProvisioningWithOtp(
      VerfiyOtpProvisionRequestDto verfiyOtpProvisionRequestDto) {
    return axpWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(axpProperties.getPassProvisioningWithOtp())
            .build(axpProperties.getBrandId()))
        .header(HttpHeaders.AUTHORIZATION, BEARER + axpProperties.getApiKey())
        .body(Mono.just(verfiyOtpProvisionRequestDto), VerfiyOtpProvisionRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(AxpErrorResponse.class).flatMap(error -> {
              String errorMessgage = error.getError();
              if (response.statusCode() == HttpStatus.BAD_REQUEST
                  && AxpConstants.INVALID_OR_EXPIRED_OTP.equals(errorMessgage)) {
                return Mono.error(new AxpServiceException(ErrorCode.INVALID_CODE, AxpConstants.INVALID_OR_EXPIRED_OTP));
              } else if (response.statusCode() == HttpStatus.NOT_FOUND) {
                ErrorCode mappedCode = switch (errorMessgage) {
                  case AxpConstants.LOCATION_NOT_FOUND -> ErrorCode.LOCATION_NOT_FOUND;
                  case AxpConstants.BOOKING_NOT_FOUND -> ErrorCode.BOOKING_NOT_FOUND;
                  case AxpConstants.PROFILE_NOT_FOUND -> ErrorCode.PROFILE_NOT_FOUND;
                  default -> ErrorCode.ALLIANTS_INTERNAL_ERROR;
                };
                return Mono.error(new ResourceNotFoundException(mappedCode, errorMessgage));
              } else {
                return Mono.error(new AxpServiceException(ErrorCode.ALLIANTS_INTERNAL_ERROR,
                    AxpConstants.UNHANDLED_AXP_ERROR + errorMessgage));
              }
            })
        )
        .bodyToMono(VerifyOtpProvisionResponseDto.class)
        .block();
  }

  public RegisterMobileDeviceResponseDto registerMobileDevice(RegisterMobileDeviceRequestDto requestDto) {
    return axpWebClient.post().uri(uriBuilder -> uriBuilder
        .path(axpProperties.getRegisterMobileDeviceEndpoint())
        .build(axpProperties.getBrandId()))
        .header(HttpHeaders.AUTHORIZATION, BEARER + axpProperties.getApiKey())
        .bodyValue(requestDto)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AxpServiceException(ErrorCode.ALLIANTS_BAD_REQUEST, BAD_REQUEST));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AxpServiceException(ErrorCode.ALLIANTS_INTERNAL_ERROR, ALLIANTS_INTERNAL_ERROR));
        })
        .bodyToMono(RegisterMobileDeviceResponseDto.class)
        .doOnError(throwable -> log.error("Error while registering mobile device", throwable))
        .block();
  }

  public GoogleWalletProvisioningResponseDto googleWalletProvisioningWithOtp(
          GoogleWalletProvisioningRequestDto requestDto) {
    return axpWebClient.post().uri(uriBuilder -> uriBuilder
        .path(axpProperties.getGoogleWalletProvisioningWithOtp())
        .build(axpProperties.getBrandId()))
        .header(HttpHeaders.AUTHORIZATION, BEARER + axpProperties.getApiKey())
        .bodyValue(requestDto)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AxpServiceException(ErrorCode.ALLIANTS_BAD_REQUEST, BAD_REQUEST));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AxpServiceException(ErrorCode.ALLIANTS_INTERNAL_ERROR, ALLIANTS_INTERNAL_ERROR));
        })
        .bodyToMono(GoogleWalletProvisioningResponseDto.class)
        .doOnError(ex -> log.error("Error during Google Wallet provisioning", ex))
        .block();
  }
}


