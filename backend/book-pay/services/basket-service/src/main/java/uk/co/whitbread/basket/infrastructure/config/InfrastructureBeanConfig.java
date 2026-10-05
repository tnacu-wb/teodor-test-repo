package uk.co.whitbread.basket.infrastructure.config;

import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.createConfirmationPaymentDetails;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.basket.domain.logic.BackgroundChargeInPortImpl;
import uk.co.whitbread.basket.domain.logic.BasketInPortImpl;
import uk.co.whitbread.basket.domain.logic.CcuiEckohInPortImpl;
import uk.co.whitbread.basket.domain.logic.CcuiPaymentInPortImpl;
import uk.co.whitbread.basket.domain.logic.EmailNotificationInPortImpl;
import uk.co.whitbread.basket.domain.logic.EmailNotificationService;
import uk.co.whitbread.basket.domain.logic.EmailNotificationServiceImpl;
import uk.co.whitbread.basket.domain.logic.PaymentInPortImpl;
import uk.co.whitbread.basket.domain.logic.RefundInPortImpl;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.logic.config.ThirdpartyBookingProperties;
import uk.co.whitbread.basket.domain.logic.config.ThreecProp;
import uk.co.whitbread.basket.domain.logic.mapper.CcuiPaymentDomainMapper;
import uk.co.whitbread.basket.domain.logic.mapper.CompanyAddressMapper;
import uk.co.whitbread.basket.domain.logic.mapper.DepositFolioResponseMapper;
import uk.co.whitbread.basket.domain.logic.mapper.PaymentResponseWebhookMapper;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.domain.ports.primary.BackgroundChargeInPort;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.domain.ports.primary.CcuiEckohInPort;
import uk.co.whitbread.basket.domain.ports.primary.CcuiPaymentInPort;
import uk.co.whitbread.basket.domain.ports.primary.EmailNotificationInPort;
import uk.co.whitbread.basket.domain.ports.primary.PaymentInPort;
import uk.co.whitbread.basket.domain.ports.primary.RefundInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BookingCompletedOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BookingConfirmationDetailsConverterPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiEckohOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiPaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CdhSearchCompaniesOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.DepositFolioOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.MarketingOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentResponseConverterPort;
import uk.co.whitbread.basket.domain.ports.secondary.PromoOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.infrastructure.queue.BasketOrderOutPortImpl;
import uk.co.whitbread.basket.infrastructure.queue.BookingCompletedOutPortImpl;
import uk.co.whitbread.basket.infrastructure.queue.EmailNotificationOutPortImpl;
import uk.co.whitbread.basket.infrastructure.queue.RefundOutPortImpl;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;
import uk.co.whitbread.basket.infrastructure.queue.producer.BookingCompletedProducer;
import uk.co.whitbread.basket.infrastructure.queue.producer.EmailNotificationProducer;
import uk.co.whitbread.basket.infrastructure.queue.producer.RefundProducer;
import uk.co.whitbread.basket.infrastructure.repository.BasketOutPortImpl;
import uk.co.whitbread.basket.infrastructure.repository.BasketRepository;
import uk.co.whitbread.basket.infrastructure.repository.DepositFolioOutPortImpl;
import uk.co.whitbread.basket.infrastructure.repository.PrepaidDepositRepository;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.CcuiEckohOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.CcuiPaymentOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.BillingAddressRequestOhipMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.CustomReferenceNumberRequestOhipMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.BasketOhipOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.PaymentOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.properties.InitiatePaymentProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.PromoOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.RedeemPromoCodeResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.PromoServiceClient;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.HotelReservationOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.BusinessItemsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.CompanyQuestionAndAnswerDetailsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationAlertsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.RulesAgentOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.BusinessAllowanceRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.mapper.VatRuleResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.RulesAgentClient;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.utils.CacheHelper;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public CleanUpTime cleanUpTimeUtils(
      @Value("${basket.cleanUpTimeSeconds.openStatus}") long openValue,
      @Value("${basket.cleanUpTimeSeconds.processingStatus}") long processingValue,
      @Value("${basket.cleanUpTimeSeconds.amendingStatus}") long amendingValue,
      @Value("${basket.cleanUpTimeSeconds.amendedStatus}") long amendedValue,
      @Value("${basket.cleanUpTimeSeconds.payPendingStatus}") long payPendingValue,
      @Value("${basket.cleanUpTimeSeconds.completedStatus}") long completedValue,
      @Value("${basket.cleanUpTimeSeconds.cancelledStatus}") long cancelledValue,
      @Value("${basket.cleanUpTimeSeconds.amendFailedStatus}") long amendFailedValue,
      @Value("${basket.cleanUpTimeSeconds.failedStatus}") long failedValue,
      @Value("${basket.cleanUpTimeSeconds.preCheckedInStatus}") long preCheckedInValue,
      @Value("${basket.cleanUpTimeSeconds.preCheckedOutStatus}") long preCheckedOutValue,
      @Value("${basket.cleanUpTimeSeconds.ciolRcFailedStatus}") long ciolRcFailedValue) {
    return CleanUpTime.builder()
        .openValue(openValue)
        .processingValue(processingValue)
        .amendingValue(amendingValue)
        .amendedValue(amendedValue)
        .payPendingValue(payPendingValue)
        .completedValue(completedValue)
        .cancelledValue(cancelledValue)
        .amendFailedValue(amendFailedValue)
        .failedValue(failedValue)
        .preCheckedInValue(preCheckedInValue)
        .preCheckedOutValue(preCheckedOutValue)
        .ciolRcFailedValue(ciolRcFailedValue)
        .build();
  }

  @Bean
  public BasketOutPort basketOutPort(
      BasketRepository basketRepository,
      BasketEntityMapper basketEntityMapper,
      BasketIdService basketIdService,
      HotelInfoOutPort hotelInfoOutPort,
      CleanUpTime cleanUpTime,
      OhipAdapterClient ohipAdapterClient) {
    return new BasketOutPortImpl(basketRepository, basketEntityMapper,
        basketIdService, hotelInfoOutPort, cleanUpTime, ohipAdapterClient);
  }

  @Bean
  public DepositFolioOutPort depositFolioOutPort(PrepaidDepositRepository depositRepository) {
    return new DepositFolioOutPortImpl(depositRepository);
  }

  @Bean
  public HotelReservationOutPort hotelReservationOutPort(ReservationClient resrvationClient,
      ReservationResponseMapper responseMapper, BusinessItemsMapper businessItemsMapper,
      CompanyQuestionAndAnswerDetailsMapper companyQuestionAndAnswerDetailsMapper,
      DepositsResponseMapper depositsResponseMapper,
      MarketingPreferencesResponseMapper marketingPreferencesResponseMapper,
      AttachReservationProfileRequestMapper attachReservationProfileRequestMapper,
      UpdateReservationRequestMapper updateReservationRequestMapper,
      ReservationAlertsMapper reservationAlertsMapper,
      InitiatePaymentProperties initiatePaymentProperties,
      CacheHelper cacheHelper) {
    return new HotelReservationOutPortImpl(resrvationClient, responseMapper,
        businessItemsMapper, companyQuestionAndAnswerDetailsMapper,
        depositsResponseMapper, marketingPreferencesResponseMapper,
        attachReservationProfileRequestMapper, updateReservationRequestMapper,
        reservationAlertsMapper, initiatePaymentProperties, cacheHelper);
  }

  @Bean
  public RulesAgentOutPort rulesAgentOutPort(RulesAgentClient rulesAgentClient,
      BusinessAllowanceRuleResponseMapper businessAllowanceRuleResponseMapper,
      VatRuleResponseMapper vatRuleResponseMapper) {
    return new RulesAgentOutPortImpl(rulesAgentClient, businessAllowanceRuleResponseMapper,
        vatRuleResponseMapper);
  }

  @Bean
  public BookingCompletedOutPort bookingCompletedOutPort(
      BookingCompletedProducer bookingCompletedProducer) {
    return new BookingCompletedOutPortImpl(bookingCompletedProducer);
  }

  @Bean
  public BasketOrderOutPort basketOrderOutPort(BasketOrderProducer basketOrderProducer) {
    return new BasketOrderOutPortImpl(basketOrderProducer);
  }

  @Bean
  public BasketOhipOutPort basketOhipOutPort(OhipAdapterClient ohipAdapterClient,
      BillingAddressRequestOhipMapper billingAddressRequestOhipMapper,
      CustomReferenceNumberRequestOhipMapper customReferenceNumberRequestOhipMapper) {
    return new BasketOhipOutPortImpl(ohipAdapterClient, billingAddressRequestOhipMapper,
        customReferenceNumberRequestOhipMapper);
  }

  @Bean
  public PaymentInPort paymentInPort(@Value("${basket.basketValidity}") Long basketValidity,
      BasketOrderOutPort basketOrderOutPort,
      HotelReservationOutPort reservationOutPort,
      PaymentOutPort paymentOutPort, BasketOutPort basketOutPort,
      ContentOutPort contentOutPort,
      EmailNotificationService emailNotificationService,
      RefundOutPort refundOutPort,
      RulesAgentOutPort rulesAgentOutPort,
      DistributionProperties distributionProperties,
      ThreecProp threecProperties,
      PaymentResponseWebhookMapper paymentResponseWebhookMapper,
      ConcurrentTracer concurrentTracer,
      BasketOhipOutPort basketOhipOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      CdhSearchCompaniesOutPort cdhSearchCompaniesOutPort,
      AuthenticatedUserService authenticatedUserService,
      CompanyAddressMapper companyAddressMapper,
      CleanUpTime cleanUpTime) {
    return new PaymentInPortImpl(basketValidity, basketOrderOutPort, reservationOutPort,
        paymentOutPort, basketOutPort,
        contentOutPort, emailNotificationService, refundOutPort, rulesAgentOutPort,
        distributionProperties, threecProperties,
        paymentResponseWebhookMapper, concurrentTracer, basketOhipOutPort, unleashWrapper,
        cdhSearchCompaniesOutPort, authenticatedUserService, companyAddressMapper, cleanUpTime);
  }

  @Bean
  public BasketInPort basketInPort(@Value("${basket.basketStatusPolling}") Long basketStatusPolling,
      CleanUpTime cleanUpTime,
      BasketOutPort basketOutPort,
      EmailNotificationService emailNotificationService,
      RefundOutPort refundOutPort,
      BasketOrderOutPort basketOrderOutPort,
      BookingCompletedOutPort bookingCompletedOutPort,
      HotelReservationOutPort hotelReservationOutPort,
      DepositFolioOutPort depositFolioOutPort,
      ConcurrentTracer concurrentTracer,
      MarketingOutPort marketingOutPort,
      AuthenticatedUserService authenticatedUserService,
      @Value("${basket.basketValidity}") Long basketValidity,
      RulesAgentOutPort rulesAgentOutPort,
      ContentOutPort contentOutPort,
      DistributionProperties distributionProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      PromoOutPort promoOutPort) {
    return new BasketInPortImpl(basketStatusPolling, cleanUpTime, basketOutPort,
        depositFolioOutPort, emailNotificationService, refundOutPort, basketOrderOutPort,
        bookingCompletedOutPort, hotelReservationOutPort, marketingOutPort, concurrentTracer,
        authenticatedUserService, basketValidity, rulesAgentOutPort, contentOutPort,
        distributionProperties, unleashWrapper, promoOutPort);
  }

  @Bean
  public CcuiEckohOutPort ccuiEckohOutPort(CcuiEckohPaymentMapper ccuiEckohPaymentMapper) {
    return new CcuiEckohOutPortImpl(ccuiEckohPaymentMapper);
  }

  @Bean
  public CcuiEckohInPort ccuiEckohInPort(BasketOutPort basketOutPort, PaymentOutPort paymentOutPort,
      CcuiEckohOutPort ccuiEckohOutPort, HotelReservationOutPort hotelReservationOutPort) {
    return new CcuiEckohInPortImpl(basketOutPort, paymentOutPort, ccuiEckohOutPort,
        hotelReservationOutPort);
  }

  @Bean
  public CcuiPaymentOutPort ccuiPaymentOutPort(
      PaymentsClient paymentsClient,
      CcuiEckohPaymentMapper ccuiEckohPaymentMapper,
      CcuiEckohPaymentResponseMapper ccuiEckohPaymentResponseMapper) {
    return new CcuiPaymentOutPortImpl(paymentsClient, ccuiEckohPaymentMapper,
        ccuiEckohPaymentResponseMapper);
  }

  @Bean
  public CcuiPaymentInPort ccuiPaymentInPort(@Value("${basket.basketValidity}") Long basketValidity,
      BasketOrderOutPort basketOrderOutPort,
      HotelReservationOutPort reservationOutPort,
      PaymentOutPort paymentOutPort,
      BasketOutPort basketOutPort,
      CcuiPaymentOutPort ccuiPaymentOutPort,
      ContentOutPort contentOutPort,
      EmailNotificationService emailNotificationService,
      RefundOutPort refundOutPort,
      RulesAgentOutPort rulesAgentOutPort,
      ThreecProp threecProperties,
      BasketOhipOutPort basketOhipOutPort,
      CcuiPaymentDomainMapper ccuiPaymentDomainMapper,
      ConcurrentTracer concurrentTracer,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      AuthenticatedUserService authenticatedUserService,
      CleanUpTime cleanUpTime) {
    return new CcuiPaymentInPortImpl(basketValidity, basketOrderOutPort, reservationOutPort,
        paymentOutPort, basketOutPort, ccuiPaymentOutPort, contentOutPort, emailNotificationService,
        refundOutPort, rulesAgentOutPort, threecProperties, basketOhipOutPort,
        ccuiPaymentDomainMapper,
        concurrentTracer, unleashWrapper, authenticatedUserService, cleanUpTime);
  }

  @Bean
  public EmailNotificationInPort emailInPort(BasketOutPort basketOutPort,
      HotelReservationOutPort reservationOutPort,
      EmailNotificationService emailNotificationService) {
    return new EmailNotificationInPortImpl(basketOutPort, reservationOutPort,
        emailNotificationService);
  }

  @Bean
  public EmailNotificationOutPort emailNotificationOutPort(
      EmailNotificationProducer emailNotificationProducer,
      ReservationClient reservationClient) {
    return new EmailNotificationOutPortImpl(emailNotificationProducer, reservationClient);
  }

  @Bean
  public RefundOutPort refundOutPort(RefundProducer refundProducer, PaymentsClient paymentsClient,
      PaymentRequestMapper paymentRequestMapper,
      PaymentResponseMapper paymentResponseMapper) {
    return new RefundOutPortImpl(refundProducer, paymentsClient, paymentRequestMapper,
        paymentResponseMapper);
  }

  @Bean
  public RefundInPort refundInPort(BasketOutPort basketOutPort,
      RefundOutPort refundOutPort,
      HotelReservationOutPort reservationOutPort) {
    return new RefundInPortImpl(basketOutPort, refundOutPort, reservationOutPort);
  }

  @Bean
  public EmailNotificationService emailNotificationService(
      HotelReservationOutPort reservationOutPort,
      EmailNotificationOutPort emailNotificationOutPort, PaymentOutPortImpl paymentOutPort,
      DistributionProperties distributionProperties, UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new EmailNotificationServiceImpl(reservationOutPort, emailNotificationOutPort,
        paymentOutPort, distributionProperties, unleashWrapper);
  }

  @Bean
  public PromoOutPort promoOutPort(PromoServiceClient promoServiceClient,
      PromoKindResponseMapper promoKindResponseMapper,
      RedeemPromoCodeResponseMapper redeemPromoCodeResponseMapper) {
    return new PromoOutPortImpl(promoServiceClient,
        promoKindResponseMapper, redeemPromoCodeResponseMapper);
  }

  @Bean
  public BackgroundChargeInPort backgroundChargeInPort(
      HotelReservationOutPort hotelReservationOutPort,
      PaymentOutPort paymentOutPort,
      RulesAgentOutPort rulesAgentOutPort,
      BasketOutPort basketOutPort,
      BasketInPort basketInPort,
      RefundOutPort refundOutPort,
      CleanUpTime cleanUpTime,
      DepositFolioResponseMapper mapper,
      ThirdpartyBookingProperties thirdpartyBookingProperties) {
    return new BackgroundChargeInPortImpl(hotelReservationOutPort, paymentOutPort,
        rulesAgentOutPort, basketOutPort, basketInPort, refundOutPort, cleanUpTime, mapper,
        thirdpartyBookingProperties);
  }

  @Bean
  public PaymentResponseConverterPort paymentResponseConverterPort(
      PaymentResponseWebhookMapper paymentResponseWebhookMapper) {
    return paymentResponseWebhookMapper::toPaymentsResponseModel;
  }

  @Bean
  public BookingConfirmationDetailsConverterPort bookingConfirmationDetailsConverterPort(
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    return (basket, hotelPaymentInformation, paymentResponse, paymentProvider) ->
        createConfirmationPaymentDetails(basket.getChannel(), hotelPaymentInformation,
            paymentResponse, unleashWrapper, paymentProvider);
  }
}
