package uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_RATES_OVERRIDE_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_RATE_INFORMATION_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemBookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemRateInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.AemRateOverridesDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.BookingInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class BookingAemClient {

  public static final String CHANNEL_PI = "PI";

  private final AemProperties aemProperties;
  private final WebClient aemWebClient;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
            value = "BookingInformationCache")
  public AemBookingInformationDto getBookingInformation(
      BookingInformationRequestAemDto bookingInformationRequestDto) {
    log.debug("Entered getBookingInformation with country={}, language={}, bookingFlowId={}",
        bookingInformationRequestDto.getCountry(), bookingInformationRequestDto.getLanguage(),
        bookingInformationRequestDto.getBookingFlowId());

    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(aemProperties.getBookingInformationEndpoint())
                .build(bookingInformationRequestDto.getCountry(),
                    bookingInformationRequestDto.getLanguage(),
                    bookingInformationRequestDto.getBookingFlowId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_BOOKING_INFORMATION_EXCEPTION, "Unable to get booking information.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemBookingInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
          value = "RateInformationForBrandCache", key = "{#rateInformationRequestAemDto.country, "
          + "#rateInformationRequestAemDto.language, #rateInformationRequestAemDto.brand, "
          + "#rateInformationRequestAemDto.channel}")
  public AemRateInformationDto getRateInformationForBrand(
          RateInformationRequestAemDto rateInformationRequestAemDto) {
    log.debug(
        "Entered getRateInformationForBrand with country={}, language={}, brand={}, channel={}",
        rateInformationRequestAemDto.getCountry(), rateInformationRequestAemDto.getLanguage(),
        rateInformationRequestAemDto.getBrand(), rateInformationRequestAemDto.getChannel());

    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder
                .path(
                    CHANNEL_PI.equals(rateInformationRequestAemDto.getChannel())
                        ? aemProperties.getRateInformationEndpoint() :
                        aemProperties.getBbRateInformationEndpoint())
                .build(rateInformationRequestAemDto.getCountry(),
                    rateInformationRequestAemDto.getLanguage(),
                    rateInformationRequestAemDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_RATE_INFORMATION_EXCEPTION,
                  String.format("Unable to get rate information for brand: %s",
                      rateInformationRequestAemDto.getBrand()),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemRateInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
            value = "RateInformationForHotelCache", key = "{#rateInformationRequestAemDto.country, "
            + "#rateInformationRequestAemDto.language, #rateInformationRequestAemDto.brand, "
            + "#rateInformationRequestAemDto.channel, #rateInformationRequestAemDto.hotelId}")
  public AemRateInformationDto getRateInformationForHotel(
          RateInformationRequestAemDto rateInformationRequestAemDto) {
    log.debug("Entered getRateInformationForHotel with country={}, language={}, brand={}, hotel={}",
        rateInformationRequestAemDto.getCountry(), rateInformationRequestAemDto.getLanguage(),
        rateInformationRequestAemDto.getBrand(), rateInformationRequestAemDto.getHotelId());

    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    CHANNEL_PI.equals(rateInformationRequestAemDto.getChannel())
                        ? aemProperties.getRateInformationForHotelEndpoint() :
                        aemProperties.getBbRateInformationForHotelEndpoint())
                .build(rateInformationRequestAemDto.getCountry(),
                    rateInformationRequestAemDto.getLanguage(),
                    rateInformationRequestAemDto.getBrand(),
                    rateInformationRequestAemDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION,
                  String.format("Unable to get booking information for hotel %s",
                      rateInformationRequestAemDto.getHotelId()),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemRateInformationDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
            value = "RateOverridesDetailsCache", key = "{#rateInformationRequestAemDto.country, "
            + "#rateInformationRequestAemDto.language, #rateInformationRequestAemDto.brand, "
            + "#rateInformationRequestAemDto.channel}")
  public AemRateOverridesDto getRatesOverrideDetails(
          RateInformationRequestAemDto rateInformationRequestAemDto) {
    log.debug("Entered getRatesOverrideDetails with country={}, language={}, brand={}",
        rateInformationRequestAemDto.getCountry(), rateInformationRequestAemDto.getLanguage(),
        rateInformationRequestAemDto.getBrand());

    return aemWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(
                CHANNEL_PI.equals(rateInformationRequestAemDto.getChannel())
                    ? aemProperties.getRatesOverrideEndpoint() :
                    aemProperties.getBbRatesOverrideEndpoint())
            .build(rateInformationRequestAemDto.getCountry(),
                rateInformationRequestAemDto.getLanguage(),
                rateInformationRequestAemDto.getBrand()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_RATES_OVERRIDE_EXCEPTION,
                  "Unable to get rates override details.",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(AemRateOverridesDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
