package uk.co.whitbread.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.domain.logic.AvailabilitiesFromOpera;
import uk.co.whitbread.domain.logic.AvailabilitiesResponseFromAvCache;
import uk.co.whitbread.domain.logic.AvailabilitiesResponseFromOpera;
import uk.co.whitbread.domain.logic.AvailabilityCacheSearchInPortImpl;
import uk.co.whitbread.domain.logic.DistanceFromSearchInPortImpl;
import uk.co.whitbread.domain.logic.GroupBookingInPortImpl;
import uk.co.whitbread.domain.logic.HotelAvailabilitiesInPortImpl;
import uk.co.whitbread.domain.logic.HotelAvailabilitiesInPortV2Impl;
import uk.co.whitbread.domain.logic.HotelAvailabilityCheckRules;
import uk.co.whitbread.domain.logic.HotelAvailabilityInPortImpl;
import uk.co.whitbread.domain.logic.HotelInfoInPortImpl;
import uk.co.whitbread.domain.logic.ListOfValuesInPortImpl;
import uk.co.whitbread.domain.logic.MlosCommonLogic;
import uk.co.whitbread.domain.logic.PackagesInPortImpl;
import uk.co.whitbread.domain.logic.RulesAgentInPortImpl;
import uk.co.whitbread.domain.logic.RulesAgentValidations;
import uk.co.whitbread.domain.logic.SnowdropInformation;
import uk.co.whitbread.domain.logic.SoftBundlesAndRatesLogic;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.domain.ports.primary.AvailabilityCacheSearchInPort;
import uk.co.whitbread.domain.ports.primary.DistanceFromSearchInPort;
import uk.co.whitbread.domain.ports.primary.GroupBookingInPort;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilitiesInPort;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.CdhAdapterOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.DistanceFromSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.GroupBookingOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.domain.ports.secondary.ListOfValuesOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.PackagesOutPort;
import uk.co.whitbread.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.infrastructure.rest.client.companyentity.CompanyEntityServiceOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.opera.OnSaleFlagOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.opera.mapper.HotelStatusMapper;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache(
      AvailabilityCacheV1SearchOutPort avCaheOutPort,
      RulesAgentOutPort rulesAgentOutPort,
      OnSaleFlagOutPort onSaleFlagOutPortImpl,
      SnowdropInformation snowdropInformation) {
    return new AvailabilitiesResponseFromAvCache(avCaheOutPort, rulesAgentOutPort,
        onSaleFlagOutPortImpl, snowdropInformation);
  }

  @Bean
  public AvailabilitiesResponseFromOpera availabilitiesResponseFromOpera(
      SnowdropInformation snowdropInformation,
      HotelAvailabilityOutPort ohipAdapterOutPort,
      OnSaleFlagOutPort onSaleFlagOutPortImpl) {
    return new AvailabilitiesResponseFromOpera(snowdropInformation, ohipAdapterOutPort, onSaleFlagOutPortImpl);
  }

  @Bean
  public MlosCommonLogic mlosCommonLogic(UnleashWrapper<FeatureFlag> unleashWrapper,
        HotelAvailabilityOutPort availabilityOhipPort) {
    return new MlosCommonLogic(unleashWrapper, availabilityOhipPort);
  }

  @Bean
  public HotelAvailabilityInPort availabilityPort(
          final RulesAgentInPort rulesAgentInPort, OnSaleFlagOutPort onSaleFlagOutPortImpl,
          final BasketServiceOutPort basketServiceOutPort,
          final HotelAvailabilityOutPort ohipClientPort,
          final AvailabilityCacheV1SearchOutPort availabilityCacheV1SearchOutPort,
          final CompanyProperties companyProperties,
          final HotelAvailabilityCheckRules hotelAvailabilityCheckRules,
          final PackagesInPort packagesInPort,
          final ConcurrentTracer concurrentTracer,
          final UnleashWrapper<FeatureFlag> unleashWrapper,
          final RulesAgentOutPort rulesAgentOutPort,
          final ContentServiceOutPort contentServiceOutPort,
          final PromotionProperties promotionProperties,
          final MlosCommonLogic mlosCommonLogic,
          final RateSuppressionProperties rateSuppressionProperties,
          final SoftBundlesAndRatesLogic softBundlesAndRatesLogic,
          final PromotionOutPort promotionOutPort,
          final CdhAdapterOutPort cdhAdapterOutPort,
          final CompanyEntityServiceOutPortImpl companyEntityServiceOutPort) {
    return new HotelAvailabilityInPortImpl(ohipClientPort,
        availabilityCacheV1SearchOutPort, rulesAgentInPort, onSaleFlagOutPortImpl,
        basketServiceOutPort,
        companyProperties, packagesInPort, hotelAvailabilityCheckRules, concurrentTracer,
        unleashWrapper,
        rulesAgentOutPort, contentServiceOutPort, promotionProperties, mlosCommonLogic,
        rateSuppressionProperties, softBundlesAndRatesLogic, promotionOutPort, cdhAdapterOutPort,
        companyEntityServiceOutPort);
  }

  @Bean
  public OnSaleFlagOutPort onSaleFlagOutPort(
      final HotelStatusMapper hotelStatusMapper,
      final CacheSearchOutPort cacheSearchOutPort) {
    return new OnSaleFlagOutPortImpl(hotelStatusMapper, cacheSearchOutPort);
  }

  @Bean
  public GroupBookingInPort groupBookingPort(final GroupBookingOutPort groupBookingOutPort) {
    return new GroupBookingInPortImpl(groupBookingOutPort);
  }

  @Bean
  public PackagesInPort packagesInPort(
      PackagesOutPort packagesOutPort) {
    return new PackagesInPortImpl(packagesOutPort);
  }

  @Bean
  public DistanceFromSearchInPort distanceFromSearchInPort(
      DistanceFromSearchOutPort distanceFromSearchOutPort) {
    return new DistanceFromSearchInPortImpl(distanceFromSearchOutPort);
  }

  @Bean
  public AvailabilityCacheSearchInPort availabilityCacheSearchInPort(
      AvailabilityCacheSearchOutPort availabilityCacheSearchOutPort) {
    return new AvailabilityCacheSearchInPortImpl(availabilityCacheSearchOutPort);
  }

  @Bean
  public HotelAvailabilitiesInPort hotelSearchInPort(
      AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache,
      AvailabilitiesResponseFromOpera availabilitiesResponseFromOpera,
      RulesAgentValidations rulesAgentValidations,
      CacheSearchOutPort cacheSearchOutPort, ContentServiceOutPort contentServiceOutPort,
      AuthenticatedUserService authenticatedUserService, ConcurrentTracer concurrentTracer) {
    return new HotelAvailabilitiesInPortImpl(availabilitiesResponseFromAvCache,
        availabilitiesResponseFromOpera,
        rulesAgentValidations, cacheSearchOutPort, contentServiceOutPort,
        authenticatedUserService, concurrentTracer);
  }

  @Bean("hotelSearchInPortV2")
  public HotelAvailabilitiesInPort hotelSearchInPortV2(
      AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache,
      RulesAgentValidations rulesAgentValidations,
      CacheSearchOutPort cacheSearchOutPort, ContentServiceOutPort contentServiceOutPort,
      HotelAvailabilityOutPort hotelAvailabilityOutPort,
      SnowdropInformation snowdropInformation, OnSaleFlagOutPort onSaleFlagOutPortImpl,
      AuthenticatedUserService authenticatedUserService,
      AvailabilitiesFromOpera availabilitiesFromOpera,
      ConcurrentTracer concurrentTracer,
      UnleashWrapper<FeatureFlag> unleashWrapper, RulesAgentOutPort rulesAgentOutPort,
      MlosCommonLogic mlosCommonLogic) {
    return new HotelAvailabilitiesInPortV2Impl(availabilitiesResponseFromAvCache,
        rulesAgentValidations, cacheSearchOutPort, contentServiceOutPort, hotelAvailabilityOutPort,
        authenticatedUserService, snowdropInformation, onSaleFlagOutPortImpl, availabilitiesFromOpera,
        concurrentTracer, unleashWrapper, rulesAgentOutPort, mlosCommonLogic);
  }

  @Bean
  public AvailabilitiesFromOpera availabilitiesFromOpera(
      HotelAvailabilityOutPort hotelAvailabilityOutPort) {
    return new AvailabilitiesFromOpera(hotelAvailabilityOutPort);
  }

  @Bean
  public HotelAvailabilityCheckRules hotelAvailabilityCheckRules(ContentServiceOutPort contentServiceOutPort,
      RulesAgentOutPort rulesAgentOutPort) {
    return new HotelAvailabilityCheckRules(contentServiceOutPort, rulesAgentOutPort);
  }

  @Bean
  public RulesAgentInPort rulesAgentInPort(RulesAgentOutPort rulesAgentOutPort) {
    return new RulesAgentInPortImpl(rulesAgentOutPort);
  }

  @Bean
  public HotelInfoInPort hotelInfoInPort(HotelInfoOutPort hotelInfoOutPort) {
    return new HotelInfoInPortImpl(hotelInfoOutPort);
  }

  @Bean
  public ListOfValuesInPort listOfValuesInPort(ListOfValuesOutPort listOfValuesOutPort) {
    return new ListOfValuesInPortImpl(listOfValuesOutPort);
  }

  @Bean
  public SoftBundlesAndRatesLogic softBundlesAndRatesLogic(SoftBundlesProperties softBundlesProperties) {
    return new SoftBundlesAndRatesLogic(softBundlesProperties);
  }
}
