package uk.co.whitbread.reservation.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.reservation.domain.logic.AmendDistributionLogicInPortImpl;
import uk.co.whitbread.reservation.domain.logic.AmendLogicInPortImpl;
import uk.co.whitbread.reservation.domain.logic.AmendPayNowLogic;
import uk.co.whitbread.reservation.domain.logic.CheckInOnlineLogic;
import uk.co.whitbread.reservation.domain.logic.CheckOutOnlineLogic;
import uk.co.whitbread.reservation.domain.logic.DigitalKeyFeature;
import uk.co.whitbread.reservation.domain.logic.HotelReservationInPortImpl;
import uk.co.whitbread.reservation.domain.logic.ManageBookingInPortImpl;
import uk.co.whitbread.reservation.domain.logic.ManageBookingLogic;
import uk.co.whitbread.reservation.domain.logic.ReservationCleanup;
import uk.co.whitbread.reservation.domain.logic.ReservationCleanupImpl;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.ports.primary.AmendDistributionLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
import uk.co.whitbread.reservation.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.CacheOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.CdhSearchBookingOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.BusinessBookerConfigProperties;
import uk.co.whitbread.reservation.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.CheckOutOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.CompanyProperties;
import uk.co.whitbread.reservation.domain.properties.DigitalKeyProperties;
import uk.co.whitbread.reservation.domain.properties.DistributionProperties;
import uk.co.whitbread.reservation.domain.properties.PackageProperties;
import uk.co.whitbread.reservation.domain.properties.PromotionProperties;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.BasketOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.BasketMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.BasketUpdateRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.CancelBasketRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.PaymentMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.RefundMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.cache.CacheOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhAddressMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhEmployeeMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.ContentOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelPaymentInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.IndexHeaderResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.NotesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.SearchRulesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.HotelAccountOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.mapper.CustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.HotelReservationOhipOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.AmendDistributionSingleCallRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.AttachReservationProfileRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.BusinessItemsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancellationPoliciesOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CompanyQuestionAndAnswerOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmAmendRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CopyReservationsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CopyReservationsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DepositFoliosResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DepositsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DonationPackagesResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.EmailRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelAvailabilityMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelAvailabilityRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelInformationOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.LinkReservationToLeisureCustomerRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.MarketingPreferencesResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.MemosOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.PreCheckInRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.RatePlansResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationBookerRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationByBasketRefOhipResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationFileAttachmentRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationGuestRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationsPackagesOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SearchBookingsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SearchBookingsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SpecialRequestsOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateBookerEmailRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateDiscountRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdatePackagesRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdatePreferencesRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateRateCodeRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReasonForStayOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationCcAgentIdRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationOverrideReasonsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationScheduledMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateRoomTypeOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.CacheReservationResponseHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.HandleCiolRevertHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterTimeoutConfiguredClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.PromotionOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.PromotionClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.RulesOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.BusinessAllowanceRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.ChannelRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.MaxNightsRuleMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.MaxRoomRuleMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.RulesAmendmentMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.SingleOccupancySupplementResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.VatRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.RulesAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
@SuppressWarnings("squid:S107")
public class HotelReservationServiceConfig {

  @Bean
  public HotelReservationOhipOutPort hotelReservationOutbound(
      OhipAdapterClient ohipAdapterClient,
      CdhAdapterClient cdhAdapterClient,
      ReservationResponseOhipMapper reservationResponseOhipMapper,
      ReservationRequestOhipMapper reservationRequestOhipMapper,
      ConfirmReservationResponseOhipMapper confirmReservationResponseOhipMapper,
      ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper,
      ReservationByBasketRefOhipResponseMapper reservationByBasketRefOhipResponseMapper,
      ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper,
      BasketClient basketClient,
      UpdatePackagesRequestOhipMapper updatePackagesRequestOhipMapper,
      UpdateRateCodeRequestOhipMapper updateRateCodeRequestOhipMapper,
      ReservationsPackagesOhipMapper reservationsPackagesOhipMapper,
      CancelResponseOhipMapper cancelResponseOhipMapper,
      CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper,
      CancelReservationResponseOhipMapper cancelReservationResponseOhipMapper,
      UpdateDiscountRequestOhipMapper updateDiscountRequestOhipMapper,
      HotelInformationOhipMapper hotelInformationOhipMapper,
      SearchBookingsRequestOhipMapper searchBookingsRequestOhipMapper,
      SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper,
      UpdateReasonForStayOhipMapper updateReasonForStayOhipMapper,
      CompanyQuestionAndAnswerOhipMapper companyQuestionAndAnswerOhipMapper,
      BusinessItemsRequestOhipMapper businessItemsRequestOhipMapper,
      SpecialRequestsOhipMapper specialRequestsOhipMapper,
      UpdateReservationOverrideReasonsRequestOhipMapper updateReservationOverrideReasonsRequestOhipMapper,
      UpdateReservationCcAgentIdRequestOhipMapper updateReservationCcAgentIdRequestOhipMapper,
      DepositsResponseOhipMapper depositsResponseOhipMapper,
      CancellationPoliciesOhipMapper cancellationPoliciesOhipMapper,
      MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper,
      CopyReservationsRequestOhipMapper copyReservationsRequestOhipMapper,
      CopyReservationsResponseOhipMapper copyReservationsResponseOhipMapper,
      ConfirmAmendRequestOhipMapper confirmAmendRequestOhipMapper,
      AmendDistributionSingleCallRequestOhipMapper amendDistributionSingleCallRequestOhipMapper,
      UpdateReservationRequestOhipMapper updateReservationRequestOhipMapper,
      ReservationOhipMapper reservationOhipMapper,
      RatePlansResponseOhipMapper ratePlansResponseOhipMapper,
      DonationPackagesResponseOhipMapper donationPackagesResponseOhipMapper,
      PackagesRequestMapper packagesRequestMapper,
      PackagesResponseMapper packagesResponseMapper,
      ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper,
      DepositFoliosResponseOhipMapper depositFoliosResponseOhipMapper,
      RulesAdapterProperties rulesAdapterProperties,
      UpdateBookerEmailRequestOhipMapper updateBookerEmailRequestOhipMapper,
      MemosOhipMapper memosOhipMapper,
      AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper,
      ReservationFileAttachmentRequestOhipMapper reservationFileAttachmentRequestOhipMapper,
      PreCheckInRequestOhipMapper preCheckInRequestOhipMapper,
      HotelAvailabilityRequestMapper requestMapper,
      HotelAvailabilityMapper availabilityMapper,
      OhipAdapterTimeoutConfiguredClient ohipAdapterTimeoutConfiguredClient,
      UpdateReservationScheduledMapper updateReservationScheduledMapper,
      LinkReservationToLeisureCustomerRequestOhipMapper linkReservationToLeisureCustomerRequestOhipMapper,
      UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper,
      CdhEmployeeMapper cdhEmployeeMapper,
      CdhAddressMapper cdhAddressMapper,
      HandleCiolRevertHelper handleCiolRevertHelper,
      CacheReservationResponseHelper cacheReservationResponseHelper,
      UpdateRoomTypeOhipMapper updateRoomTypeOhipMapper,
      UnleashWrapper<FeatureFlag> unleashWrapper) {

    return new HotelReservationOhipOutPortImpl(ohipAdapterClient,
        cdhAdapterClient,
        reservationResponseOhipMapper,
        reservationRequestOhipMapper,
        confirmReservationResponseOhipMapper,
        confirmReservationRequestOhipMapper,
        reservationByBasketRefOhipResponseMapper,
        reservationGuestRequestOhipMapper,
        basketClient,
        updatePackagesRequestOhipMapper,
        updateRateCodeRequestOhipMapper,
        reservationsPackagesOhipMapper,
        cancelResponseOhipMapper,
        cancelReservationRequestOhipMapper,
        cancelReservationResponseOhipMapper,
        updateDiscountRequestOhipMapper,
        hotelInformationOhipMapper,
        searchBookingsRequestOhipMapper,
        searchBookingsResponseOhipMapper,
        updateReasonForStayOhipMapper,
        companyQuestionAndAnswerOhipMapper,
        businessItemsRequestOhipMapper,
        specialRequestsOhipMapper,
        updateReservationOverrideReasonsRequestOhipMapper,
        updateReservationCcAgentIdRequestOhipMapper,
        depositsResponseOhipMapper,
        cancellationPoliciesOhipMapper,
        marketingPreferencesResponseOhipMapper,
        copyReservationsRequestOhipMapper,
        copyReservationsResponseOhipMapper,
        confirmAmendRequestOhipMapper,
        amendDistributionSingleCallRequestOhipMapper,
        updateReservationRequestOhipMapper,
        reservationOhipMapper,
        ratePlansResponseOhipMapper,
        donationPackagesResponseOhipMapper,
        packagesRequestMapper,
        packagesResponseMapper,
        reservationBookerRequestOhipMapper,
        depositFoliosResponseOhipMapper,
        rulesAdapterProperties,
        updateBookerEmailRequestOhipMapper,
        memosOhipMapper,
        attachReservationProfileRequestOhipMapper,
        reservationFileAttachmentRequestOhipMapper,
        preCheckInRequestOhipMapper,
        requestMapper,
        availabilityMapper,
        ohipAdapterTimeoutConfiguredClient,
        updateReservationScheduledMapper,
        linkReservationToLeisureCustomerRequestOhipMapper,
        updatePreferencesRequestOhipMapper,
        cdhEmployeeMapper,
        cdhAddressMapper,
        handleCiolRevertHelper,
        cacheReservationResponseHelper,
        updateRoomTypeOhipMapper,
        unleashWrapper);
  }

  @Bean
  public BasketOutPort basketOutPort(
      BasketClient basketClient, BasketMapper basketMapper, PaymentMapper paymentMapper,
      RefundMapper refundMapper, BasketUpdateRequestMapper basketUpdateRequestMapper,
      BasketProperties basketProperties, DepositFoliosRequestMapper depositFoliosRequestMapper,
      DepositFoliosResponseMapper depositFoliosResponseMapper,
      EmailRequestMapper emailRequestMapper,
      CancelBasketRequestMapper cancelBasketRequestMapper) {
    return new BasketOutPortImpl(basketClient, basketMapper, paymentMapper, refundMapper,
        basketUpdateRequestMapper, basketProperties, depositFoliosRequestMapper,
        depositFoliosResponseMapper, emailRequestMapper, cancelBasketRequestMapper);
  }

  @Bean
  public ContentOutPort contentOutPort(ContentClient contentClient,
      IndexHeaderResponseMapper indexHeaderResponseMapper,
      NotesResponseMapper notesResponseMapper,
      HotelPaymentInformationMapper hotelPaymentInformationMapper,
      HotelInformationMapper hotelInformationMapper, SearchRulesResponseMapper searchRulesResponseMapper,
      CacheOutPort cacheOutPort) {
    return new ContentOutPortImpl(contentClient, indexHeaderResponseMapper, notesResponseMapper,
        hotelPaymentInformationMapper, hotelInformationMapper, searchRulesResponseMapper, cacheOutPort);
  }

  @Bean
  public ManageBookingInPort manageBookingInPort(

      HotelReservationOhipOutPort hotelReservationOhipOutPort,
      BasketOutPort basketOutPort,
      RulesOutPort rulesOutPort,
      ContentOutPort contentOutPort,
      AmendLogicInPort amendLogic,
      AuthenticatedUserService authenticatedUserService,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      BusinessBookerConfigProperties businessBookerConfigProperties,
      CdhSearchBookingOutPort cdhSearchBookingOutPort,
      ManageBookingLogic manageBookingLogic,
      CheckInOnlineLogic checkInOnlineLogic,
      CheckOutOnlineLogic checkOutOnlineLogic,
      DigitalKeyFeature digitalKeyFeature,
      ThirdpartyBookingProperties otaBookingProperties
  ) {
    return new ManageBookingInPortImpl(hotelReservationOhipOutPort, basketOutPort, rulesOutPort,
        contentOutPort, amendLogic, authenticatedUserService,
        unleashWrapper, businessBookerConfigProperties, cdhSearchBookingOutPort,
        manageBookingLogic, checkInOnlineLogic, checkOutOnlineLogic, digitalKeyFeature,
        otaBookingProperties);
  }

  @Bean
  public HotelReservationInPort hotelReservationPort(
      HotelReservationOhipOutPort hotelReservationOutboundPort,
      HotelAvailabilityOutPort hotelAvailabilityOutPort,
      BasketOutPort basketOutPort,
      RulesOutPort rulesOutPort,
      CdhSearchBookingInPort cdhSearchBookingInPort,
      ContentOutPort contentOutPort,
      HotelAccountOutPort hotelAccountOutPort,
      AmendLogicInPort amendLogic,
      ManageBookingInPort manageBookingInPort,
      AuthenticatedUserService authenticatedUserService,
      BusinessBookerConfigProperties businessBookerConfigProperties,
      AmendDistributionLogicInPort amendDistributionLogic,
      ReservationCleanup reservationCleanUp,
      AmendPayNowLogic amendPayNowLogic,
      CompanyProperties companyProperties,
      DistributionProperties distributionProperties,
      ConcurrentTracer concurrentTracer, @Value("${opera.confirmation.config.bic.enabled}") final Boolean flag,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      PromotionProperties promotionProperties,
      PackageProperties packageProperties,
      ManageBookingLogic manageBookingLogic,
      PromotionOutPort promotionOutPort) {
    return new HotelReservationInPortImpl(hotelReservationOutboundPort, hotelAvailabilityOutPort,
        basketOutPort, rulesOutPort,
        cdhSearchBookingInPort, contentOutPort, hotelAccountOutPort, amendLogic, manageBookingInPort,
        authenticatedUserService,
        businessBookerConfigProperties, amendDistributionLogic, reservationCleanUp,
        amendPayNowLogic, companyProperties, distributionProperties,
        concurrentTracer, flag, unleashWrapper, promotionProperties, packageProperties, manageBookingLogic,
        promotionOutPort);
  }

  @Bean
  public HotelAccountOutPort hotelAccountOutPort(
      HotelAccountClient hotelAccountClient,
      CustomerRequestMapper customerRequestMapper) {
    return new HotelAccountOutPortImpl(hotelAccountClient, customerRequestMapper);
  }

  @Bean
  public RulesOutPort rulesOutPort(
          RulesAdapterClient rulesAdapterClient,
          RulesAmendmentMapper rulesAmendmentMapper,
          MaxRoomRuleMapper maxRoomRuleMapper,
          MaxNightsRuleMapper maxNightsRuleMapper,
          ChannelRuleResponseMapper channelRuleResponseMapper,
          VatRuleResponseMapper vatRuleResponseMapper,
          BusinessAllowanceRuleResponseMapper businessAllowanceRuleResponseMapper,
          SingleOccupancySupplementResponseMapper singleOccupancySupplementResponseMapper) {
    return new RulesOutPortImpl(rulesAdapterClient, rulesAmendmentMapper, maxRoomRuleMapper,
        maxNightsRuleMapper, channelRuleResponseMapper, vatRuleResponseMapper,
        businessAllowanceRuleResponseMapper, singleOccupancySupplementResponseMapper);
  }

  @Bean
  public CacheOutPort cacheOutPort(@Qualifier("cacheManagerForContentEntityService") CacheManager cacheManager) {
    return new CacheOutPortImpl(cacheManager);
  }

  @Bean
  public AmendLogicInPort amendLogicInPort(BasketOutPort basketOutPort,
      HotelReservationOhipOutPort hotelReservationOhipOutPort,
      AuthenticatedUserService authenticatedUserService,
      ContentOutPort contentOutPort,
      ReservationCleanup reservationCleanUp,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new AmendLogicInPortImpl(basketOutPort, hotelReservationOhipOutPort,
        authenticatedUserService, contentOutPort, reservationCleanUp, unleashWrapper);
  }

  @Bean
  public AmendDistributionLogicInPort amendDistributionLogicInPort(
          BasketOutPort basketOutPort,
          AmendLogicInPort amendLogicInPort,
          UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new AmendDistributionLogicInPortImpl(basketOutPort, amendLogicInPort);
  }

  @Bean
  public ReservationCleanup reservationCleanUp(BasketOutPort basketOutPort,
      HotelReservationOhipOutPort hotelReservationOhipOutPort) {
    return new ReservationCleanupImpl(basketOutPort, hotelReservationOhipOutPort);
  }

  @Bean
  public AmendPayNowLogic amendPayNowLogic(HotelReservationOhipOutPort hotelReservationOhipOutPort,
      BasketOutPort basketOutPort) {
    return new AmendPayNowLogic(hotelReservationOhipOutPort, basketOutPort);
  }

  @Bean
  public ManageBookingLogic manageBookingLogic(BasketOutPort basketOutPort,
      RulesOutPort rulesOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper, ContentOutPort contentOutPort,
      ThirdpartyBookingProperties otaBookingProperties) {
    return new ManageBookingLogic(basketOutPort, rulesOutPort, unleashWrapper, contentOutPort,
        otaBookingProperties);
  }

  @Bean
  public CheckInOnlineLogic checkInOnlineLogic(CheckInOnlineProperties checkInOnlineProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper, ThirdpartyBookingProperties thirdpartyBookingProperties) {
    return new CheckInOnlineLogic(checkInOnlineProperties, thirdpartyBookingProperties, unleashWrapper);
  }

  @Bean
  public CheckOutOnlineLogic checkOutOnlineLogic(CheckOutOnlineProperties checkOutOnlineProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper, CheckInOnlineProperties checkInOnlineProperties) {
    return new CheckOutOnlineLogic(checkOutOnlineProperties, unleashWrapper, checkInOnlineProperties);
  }

  @Bean
  public DigitalKeyFeature digitalKeyLogic(DigitalKeyProperties digitalKeyProperties,
                                           UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new DigitalKeyFeature(digitalKeyProperties, unleashWrapper);
  }

  @Bean
  public PromotionOutPort promotionOutPort(PromotionClient promotionClient,
      PromoKindResponseMapper promoKindResponseMapper) {
    return new PromotionOutPortImpl(promotionClient, promoKindResponseMapper);
  }
}