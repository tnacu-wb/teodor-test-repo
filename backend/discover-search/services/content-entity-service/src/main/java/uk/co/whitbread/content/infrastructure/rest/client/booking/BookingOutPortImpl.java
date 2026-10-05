package uk.co.whitbread.content.infrastructure.rest.client.booking;

import static uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient.CHANNEL_PI;

import io.getunleash.UnleashContext;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.RateClassification;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.domain.model.feature.FeatureFlag;
import uk.co.whitbread.content.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemBookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemRateInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.RateClassificationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.RatesConfigCommonDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.AemRateOverridesDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateOverrideDetailsDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.RatePlanDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.RatePlansResponseDto;

@RequiredArgsConstructor
@Slf4j
public class BookingOutPortImpl implements BookingOutPort {

  private final BookingInformationMapper bookingInformationMapper;
  private final BookingInformationRequestMapper bookingInformationRequestMapper;
  private final RateInformationRequestMapper rateInformationRequestMapper;
  private final RateInformationMapper rateInformationMapper;
  private final BookingAemClient aemClient;
  private final OhipAdapterClient ohipAdapterClient;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public BookingInformation getBookingInformation(
      BookingInformationRequest bookingInformationRequest) {
    log.debug(
        "Entered getBookingInformation with country={}, language={}, bookingFlowId={}, ratePlanCodes={}",
        bookingInformationRequest.getCountry(), bookingInformationRequest.getLanguage(),
        bookingInformationRequest.getBookingFlowId(), bookingInformationRequest.getRatePlanCodes());

    var request = bookingInformationRequestMapper.toDtoModel(bookingInformationRequest);
    AemBookingInformationDto aemBookingInformationDto =
        aemClient.getBookingInformation(request);

    if (!CollectionUtils.isEmpty(bookingInformationRequest.getRatePlanCodes())) {
      Collections.sort(bookingInformationRequest.getRatePlanCodes());
      var ratePlansResponse = ohipAdapterClient.sendGetRatePlansRequest(
          bookingInformationRequest.getRatePlanCodes(), bookingInformationRequest.getHotelId());

      var rateCategories = ratePlansResponse.getRatePlans().stream()
          .map(r -> r.getClassifications().getRateCategory()).toList();

      var rateDisplaySets = ratePlansResponse.getRatePlans().stream()
          .map(r -> r.getClassifications().getDisplaySet()).toList();

      aemBookingInformationDto.getTermsAndConditions().removeIf(t ->
          !rateCategories.contains(t.getRateCategory()) || !rateDisplaySets.contains(
              t.getRateDisplaySet()));

      aemBookingInformationDto.getPaymentInfoMessages().removeIf(p ->
          !rateCategories.contains(p.getRateCategory()) || !rateDisplaySets.contains(
              p.getRateDisplaySet()));
    }
    RateInformationRequestAemDto rateInformationRequestAemDto =
        mapToRateInformationRequestAemDto(bookingInformationRequest, aemBookingInformationDto);
    AemRateInformationDto aemRateInformationDto =
        aemClient.getRateInformationForHotel(rateInformationRequestAemDto);

    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFreeFnbExtras(),
            UnleashContext.builder().build())) {
      filterPackages(bookingInformationRequest, aemBookingInformationDto, rateInformationRequestAemDto,
              aemRateInformationDto);
    }

    return bookingInformationMapper.toDomainModel(aemBookingInformationDto);
  }

  @Override
  public RateInformation getRateInformation(RateInformationRequest rateInformationRequest) {
    log.debug("Entered getRateInformation with country={}, language={}, brand={}, hotel={}",
        rateInformationRequest.getCountry(), rateInformationRequest.getLanguage(),
        rateInformationRequest.getBrand(), rateInformationRequest.getHotelId());

    var request = rateInformationRequestMapper.toDto(rateInformationRequest);
    AemRateInformationDto aemRateInformationDto =
        StringUtils.isEmpty(rateInformationRequest.getHotelId())
            ? aemClient.getRateInformationForBrand(request)
            : aemClient.getRateInformationForHotel(request);

    if (CollectionUtils.isEmpty(request.getRatePlans())) {
      RatesConfigCommonDto ratesConfigCommon = aemRateInformationDto.getRatesConfigCommon();
      var ratePlans = new ArrayList<>(getDefaultRates(ratesConfigCommon, request));
      ratePlans.addAll(getPromotionalRates(ratesConfigCommon, request));
      ratePlans.addAll(getPromotionalDiscountRates(ratesConfigCommon, request));
      request.setRatePlans(ratePlans);
    } else {
      Collections.sort(request.getRatePlans());
    }

    var ratesOverrideDetails = aemClient.getRatesOverrideDetails(request);
    var ratePlansResponse = ohipAdapterClient.sendGetRatePlansRequest(request.getRatePlans(),
        request.getHotelId());
    var response =
        createRateInformation(ratePlansResponse, aemRateInformationDto, ratesOverrideDetails);

    var responseRatePlanCodes = response.getRateClassifications().stream()
        .map(RateClassification::getRatePlanCode)
        .toList();

    var unavailableRatePlanCodes = request.getRatePlans().stream()
        .filter(r -> !responseRatePlanCodes.contains(r))
        .toList();

    if (!unavailableRatePlanCodes.isEmpty()) {
      log.warn("Unable to find rates: {}", unavailableRatePlanCodes);
    }

    return response;
  }

  @NotNull
  private static List<String> getDefaultRates(RatesConfigCommonDto ratesConfigCommon,
      RateInformationRequestAemDto rateInformationRequest) {
    if (ratesConfigCommon == null || StringUtils.isEmpty(ratesConfigCommon.getDefaultRates())) {
      log.warn("No default rates found in AEM for request: {}", rateInformationRequest);
      return new ArrayList<>();
    }
    return Arrays.stream(ratesConfigCommon.getDefaultRates().split(","))
        .map(String::trim)
        .filter(r -> !r.isEmpty())
        .toList();
  }

  @NotNull
  private List<String> getPromotionalRates(RatesConfigCommonDto ratesConfigCommon,
      RateInformationRequestAemDto rateInformationRequest) {
    if (ratesConfigCommon == null || StringUtils.isEmpty(ratesConfigCommon.getPromotionalRates())) {
      log.warn("No promotional rates found in AEM for request: {}", rateInformationRequest);
      return new ArrayList<>();
    }
    return Arrays.stream(ratesConfigCommon.getPromotionalRates().split(","))
        .map(String::trim)
        .filter(r -> !r.isEmpty())
        .toList();
  }

  @NotNull
  private List<String> getPromotionalDiscountRates(RatesConfigCommonDto ratesConfigCommon,
      RateInformationRequestAemDto rateInformationRequest) {
    if (ratesConfigCommon == null
        || StringUtils.isEmpty(ratesConfigCommon.getPromotionalDiscountRates())) {
      log.warn("No promotional discount rates found in AEM for request: {}",
          rateInformationRequest);
      return new ArrayList<>();
    }
    return Arrays.stream(ratesConfigCommon.getPromotionalDiscountRates().split(","))
        .map(String::trim)
        .filter(r -> !r.isEmpty())
        .toList();
  }

  @Override
  public RateInformation getHotelRateInformation(
      RateInformationRequest rateInformationRequest) {
    log.debug("Entered getHotelRateInformation with country={}, language={}, brand={}, hotel={}",
        rateInformationRequest.getCountry(), rateInformationRequest.getLanguage(),
        rateInformationRequest.getBrand(), rateInformationRequest.getHotelId());

    var request = rateInformationRequestMapper.toDto(rateInformationRequest);
    AemRateInformationDto aemRateInformationDto = aemClient.getRateInformationForHotel(request);
    return rateInformationMapper.toDomainModel(aemRateInformationDto);
  }


  private RateInformation createRateInformation(RatePlansResponseDto ratePlansResponse,
      AemRateInformationDto aemRateInformationDto, AemRateOverridesDto rateOverridesDto) {

    var rateInformation = RateInformation.builder();
    var rateClassificationsMap = getRateInformationByCategoryAndDisplaySet(aemRateInformationDto);
    var ratesOverridesMap = getRatesOverridesMap(rateOverridesDto);

    if (!CollectionUtils.isEmpty(ratePlansResponse.getRatePlans())) {
      ratePlansResponse.getRatePlans().forEach(ratePlan -> {
            var rateClassification = rateClassificationsMap.get(getRateClassificationsKey(ratePlan));

            if (rateClassification != null) {
              rateInformation.rateClassification(
                  mapToRateClassification(ratePlan, rateClassification, ratesOverridesMap));
            }
          }
      );
    }

    return rateInformation.build();
  }

  private Map<String, RateOverrideDetailsDto> getRatesOverridesMap(
      AemRateOverridesDto rateOverridesDto) {
    return rateOverridesDto.getRateOverrides().stream()
        .collect(Collectors.toMap(RateOverrideDetailsDto::getRatePlanCode, Function.identity()));
  }

  private RateClassification mapToRateClassification(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {

    return RateClassification.builder()
        .rateClassification(ratePlanDto.getRatePlanCode())
        .ratePlanCode(ratePlanDto.getRatePlanCode())
        .rateDisplaySet(ratePlanDto.getClassifications().getDisplaySet())
        .rateCategory(ratePlanDto.getClassifications().getRateCategory())
        .rateOrder(getRateClassificationOrder(ratePlanDto, rateClassification, rateOverridesDto))
        .rateName(getRateClassificationName(ratePlanDto, rateClassification, rateOverridesDto))
        .rateDescription(getRateClassificationDescription(ratePlanDto, rateClassification, rateOverridesDto))
        .rateLongDescription(getRateClassificationLongDescription(ratePlanDto, rateClassification, rateOverridesDto))
        .additionalDescription(
            getRateClassificationAdditionalDescription(ratePlanDto, rateOverridesDto))
        .rateNotes(getRateClassificationNotes(ratePlanDto, rateClassification, rateOverridesDto))
        .rateTags(getRateTags(ratePlanDto, rateOverridesDto))
        .build();
  }

  private List<String> getRateTags(RatePlanDto ratePlanDto,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return Optional.ofNullable(rateOverridesDto.get(ratePlanDto.getRatePlanCode()))
        .map(RateOverrideDetailsDto::getRateTags)
        .filter(tags -> !tags.isEmpty())
        .orElse(List.of());
  }

  private String getRateClassificationOrder(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) 
        && StringUtils.isNotBlank(rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateOrder())
        ? rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateOrder()
        : rateClassification.getRateOrder();
  }

  private String getRateClassificationDescription(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) 
        && StringUtils.isNotBlank(rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateDescription())
        ? rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateDescription()
        : rateClassification.getRateDescription();
  }

  private String getRateClassificationLongDescription(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) 
        && StringUtils.isNotBlank(rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateLongDescription())
        ? rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateLongDescription()
        : rateClassification.getRateLongDescription();
  }

  private String getRateClassificationNotes(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) 
        && StringUtils.isNotBlank(rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateNotes())
        ? rateOverridesDto.get(ratePlanDto.getRatePlanCode()).getRateNotes()
        : rateClassification.getRateNotes();
  }

  private String getRateClassificationName(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) ? rateOverridesDto.get(
        ratePlanDto.getRatePlanCode()).getRateName()
        : getRateName(ratePlanDto, rateClassification);
  }

  private String getRateName(RatePlanDto ratePlanDto,
      RateClassificationDto rateClassification) {
    return StringUtils.isNotBlank(rateClassification.getRateName())
        ? rateClassification.getRateName()
        : ratePlanDto.getPrimaryDetails().getDescription()
            .getDefaultText();
  }

  private String getRateClassificationAdditionalDescription(RatePlanDto ratePlanDto,
      Map<String, RateOverrideDetailsDto> rateOverridesDto) {
    return rateOverridesDto.containsKey(ratePlanDto.getRatePlanCode()) ? rateOverridesDto.get(
            ratePlanDto.getRatePlanCode())
        .getAdditionalDescription() : "";
  }

  private String getRateClassificationsKey(RatePlanDto ratePlanDto) {
    return ratePlanDto.getClassifications().getDisplaySet() + "-"
        + ratePlanDto.getClassifications().getRateCategory();
  }

  private Map<String, RateClassificationDto> getRateInformationByCategoryAndDisplaySet(
      AemRateInformationDto aemRateInformationDto) {
    return aemRateInformationDto.getRateClassifications().stream()
        .collect(Collectors.toMap(rateClassification -> rateClassification.getRateDisplaySet() + "-"
            + rateClassification.getRateCategory(), Function.identity(), (k1, k2) -> k1));
  }

  private void filterPackages(BookingInformationRequest bookingInformationRequest,
      AemBookingInformationDto aemBookingInformationDto,
      RateInformationRequestAemDto rateInformationRequestAemDto,
      AemRateInformationDto aemRateInformationDto) {
    RatesConfigCommonDto ratesConfigCommon = aemRateInformationDto.getRatesConfigCommon();

    var promotionalRates = getPromotionalRates(ratesConfigCommon, rateInformationRequestAemDto);
    var isPromoRate = StringUtils.isNotEmpty(bookingInformationRequest.getReservationRatePlanCode())
        && promotionalRates.contains(bookingInformationRequest.getReservationRatePlanCode());

    var promotionalPackage =
        null == ratesConfigCommon || StringUtils.isEmpty(ratesConfigCommon.getPromotionalPackage())
            ? "" : ratesConfigCommon.getPromotionalPackage().trim();
    aemBookingInformationDto.getUpsellitemsConfiguration().removeIf(upsellItem ->
        isPromoRate != promotionalPackage.equals(upsellItem.getCode()));
  }

  private static RateInformationRequestAemDto mapToRateInformationRequestAemDto(
      BookingInformationRequest bookingInformationRequest,
      AemBookingInformationDto aemBookingInformationDto) {
    return RateInformationRequestAemDto.builder()
        .country(bookingInformationRequest.getCountry())
        .language(bookingInformationRequest.getLanguage())
        .brand(aemBookingInformationDto.getBrand())
        .hotelId(bookingInformationRequest.getHotelId())
        .ratePlans(bookingInformationRequest.getRatePlanCodes())
        .channel(CHANNEL_PI)
        .build();
  }
}
