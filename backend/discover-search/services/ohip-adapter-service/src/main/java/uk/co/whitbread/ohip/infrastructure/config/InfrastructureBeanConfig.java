package uk.co.whitbread.ohip.infrastructure.config;

import jakarta.validation.Validator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.ohip.domain.logic.AmendInPortImpl;
import uk.co.whitbread.ohip.domain.logic.AvailabilityConfigProperties;
import uk.co.whitbread.ohip.domain.logic.CheckInInPortImpl;
import uk.co.whitbread.ohip.domain.logic.DepositFoliosInPortImpl;
import uk.co.whitbread.ohip.domain.logic.EckohInPortImpl;
import uk.co.whitbread.ohip.domain.logic.HotelAvailabilityInPortImpl;
import uk.co.whitbread.ohip.domain.logic.HotelInfoInPortImpl;
import uk.co.whitbread.ohip.domain.logic.HotelReservationInPortImpl;
import uk.co.whitbread.ohip.domain.logic.ListOfValuesInPortImpl;
import uk.co.whitbread.ohip.domain.logic.MealsIncludedConfigProperties;
import uk.co.whitbread.ohip.domain.logic.PackagesInPortImpl;
import uk.co.whitbread.ohip.domain.logic.PreferenceInPortImpl;
import uk.co.whitbread.ohip.domain.logic.ProfileInPortImpl;
import uk.co.whitbread.ohip.domain.logic.RatePlansInPortImpl;
import uk.co.whitbread.ohip.domain.logic.RoomAllocationInPortImpl;
import uk.co.whitbread.ohip.domain.logic.UdfsInImpl;
import uk.co.whitbread.ohip.domain.logic.utils.HotelDetailsInPortImpl;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.ohip.domain.ports.primary.AmendInPort;
import uk.co.whitbread.ohip.domain.ports.primary.CheckInInPort;
import uk.co.whitbread.ohip.domain.ports.primary.DepositFoliosInPort;
import uk.co.whitbread.ohip.domain.ports.primary.EckohInPort;
import uk.co.whitbread.ohip.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.ohip.domain.ports.primary.HotelDetailsInPort;
import uk.co.whitbread.ohip.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.ohip.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.ohip.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.ohip.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.ohip.domain.ports.primary.PreferenceInPort;
import uk.co.whitbread.ohip.domain.ports.primary.ProfileInPort;
import uk.co.whitbread.ohip.domain.ports.primary.RatePlansInPort;
import uk.co.whitbread.ohip.domain.ports.primary.RoomAllocationInPort;
import uk.co.whitbread.ohip.domain.ports.primary.UdfsInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.AmendOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.CheckInOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.EckohOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelDetailsOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.ListOfValuesOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.PackagesOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.PreferenceOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.ProfileOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RoomAllocationOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.UdfsOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.amend.AmendOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.HotelAvailabilityOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityByIdsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelInventoryStatisticsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelRoomInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.ItemsInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.MultiAvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.PriceBreakdownMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.CheckInOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.mapper.UpdateReservationCommentOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip.OhipCheckInClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.EckohOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.mapper.EckohChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.EckohOhipClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.OhipFrontDeskClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.HotelInfoOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.RoomTypesInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.OhipHotelConfigClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ListOfValuesOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.OhipListOfValuesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper.CancellationReasonsOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.HotelDetailsOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.mapper.HotelStatusMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.properties.OhipHotelDetailsClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.PackagesOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipPackagesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipRestaurantsClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties.PromotionProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.PreferencesOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.mapper.HotelPreferencesMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.ohip.OhipPreferencesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ProfileOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipCreateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipProfileMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipUpdateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.OhipProfileClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.RatePlansOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.NegotiatedRatesMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.PromotionCodeMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlanInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlansMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.HotelReservationOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.AttachReservationProfileRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.BookerProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.BusinessItemsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CancelReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CancelReservationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ChangeReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CompanyProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CompanyQuestionAndAnswerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ConfirmReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ConfirmationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CopyReservationsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CopyReservationsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFolioCriteriaMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFoliosRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFoliosRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.GuestDetailsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.LinkReservationToLeisureCustomerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MarketingPreferencesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MemosOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MovePaymentDetailsOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PackagesRequestDtoOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PackagesSelectionMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PayOnArrivalReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PostedDepositsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PriceBreakdownOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RatePlanChangeRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAccompanyingGuestProfileRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAccompanyingGuestRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAlertMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationBookerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationCompanyRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationGuestRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationGuestResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationPackagesOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RoomRateOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RoomTypeChangeRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.SearchBookingsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.SpecialReqRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateBookerEmailOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateBookerReservationProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateCancellationPolicyRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateCustomReferenceNumberOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateDiscountRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdatePreferencesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReasonForStayRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReasonForStayResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationAttachedProfilesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationCcAgentIdRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationOverrideReasonsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.RoomAllocationOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.mapper.ReservationPreferenceMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.OhipRoomAllocationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper.BaseRateResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper.RoomSubstitutionResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.udfs.UdfsOutPortImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.udfs.mapper.UpdateReservationOverrideUdfsRequestOhipMapper;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
public class InfrastructureBeanConfig {
  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public HotelReservationOutPort ohipAdapterResponse(
      OhipReservationClient ohipReservationClient,
      ReservationResponseOhipMapper reservationResponseOhipMapper,
      ReservationRequestOhipMapper reservationRequestOhipMapper,
      ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper,
      ConfirmationResponseOhipMapper confirmationResponseOhipMapper,
      RoomRateOhipMapper roomRateOhipMapper,
      ReservationOhipProperties reservationOhipProperties,
      ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper,
      ReservationAccompanyingGuestRequestOhipMapper reservationAccompanyingGuestRequestOhipMapper,
      ReservationGuestResponseOhipMapper reservationGuestResponseOhipMapper,
      ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper,
      ReservationCompanyRequestOhipMapper reservationCompanyRequestOhipMapper,
      ReservationPackagesOhipMapper reservationPackagesOhipMapper,
      OhipPackagesClient ohipPackagesClient,
      PackagesRequestDtoOhipMapper packagesRequestDtoOhipMapper,
      OhipAvailabilityClient ohipAvailabilityClient,
      RatePlanChangeRequestOhipMapper rateChangeRequestOhipMapper,
      RoomTypeChangeRequestOhipMapper roomTypeChangeRequestOhipMapper,
      PackageGroupsRequestOhipMapper packageGroupsRequestOhipMapper,
      DepositFoliosRequestOhipMapper depositFoliosRequestOhipMapper,
      PackagesSelectionMapper packagesSelectionMapper,
      CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper,
      CancelReservationResponseOhipMapper cancelReservationResponseOhipMapper,
      PriceBreakdownOhipMapper priceBreakdownOhipMapper,
      UpdateDiscountRequestOhipMapper discountRequestOhipMapper,
      RulesAgentClient rulesAgentClient,
      DepositFoliosRequestMapper depositFoliosRequestMapper,
      DepositFolioCriteriaMapper depositFolioCriteriaMapper,
      SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper,
      UpdateReasonForStayRequestOhipMapper updateReasonForStayRequestOhipMapper,
      UpdateReasonForStayResponseOhipMapper updateReasonForStayResponseOhipMapper,
      BusinessItemsRequestOhipMapper businessItemsRequestOhipMapper,
      SpecialReqRequestOhipMapper specialReqRequestOhipMapper,
      UpdateReservationOverrideReasonsRequestOhipMapper updateReservationOverrideReasonsRequestOhipMapper,
      UpdateReservationCcAgentIdRequestOhipMapper updateReservationCcAgentIdRequestOhipMapper,
      DepositsResponseOhipMapper depositsResponseOhipMapper,
      PostedDepositsResponseOhipMapper postedDepositsResponseOhipMapper,
      OhipFrontDeskClient frontDeskClient,
      UpdateCancellationPolicyRequestOhipMapper updateCancellationPolicyRequestOhipMapper,
      MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper,
      CopyReservationsRequestOhipMapper copyReservationsRequestOhipMapper,
      CopyReservationsResponseOhipMapper copyReservationsResponseOhipMapper,
      RulesAgentProperties rulesAgentProperties,
      UpdateReservationRequestOhipMapper updateReservationRequestMapper,
      PayOnArrivalReservationRequestOhipMapper payOnArrivalReservationRequestOhipMapper,
      MovePaymentDetailsOhipMapper movePaymentDetailsOhipMapper,
      BookerProfileOhipMapper bookerProfileOhipMapper,
      CompanyProfileOhipMapper companyProfileOhipMapper,
      UpdateReservationAttachedProfilesRequestOhipMapper updateReservationAttachedProfilesRequestOhipMapper,
      OhipProperties ohipProperties,
      UpdateBookerReservationProfileOhipMapper updateBookerReservationProfileOhipMapper,
      UpdateBookerEmailOhipMapper updateBookerEmailOhipMapper,
      CompanyQuestionAndAnswerRequestOhipMapper companyQuestionAndAnswerRequestOhipMapper,
      MemosOhipMapper memosOhipMapper,
      AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper,
      UpdateCustomReferenceNumberOhipMapper updateCustomReferenceNumberOhipMapper,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      ConcurrentTracer concurrentTracer,
      ReservationAccompanyingGuestProfileRequestOhipMapper reservationAccompanyingGuestProfileRequestOhipMapper,
      LinkReservationToLeisureCustomerRequestOhipMapper linkReservationToLeisureCustomerRequestOhipMapper,
      UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper,
      ChangeReservationRequestOhipMapper changeReservationRequestOhipMapper,
      RoomSubstitutionResponseMapper roomSubstitutionResponseMapper,
      GuestDetailsMapper guestDetailsMapper, ReservationAlertMapper reservationAlertMapper,
      ApiLimitsService apiLimitsService) {
    return new HotelReservationOutPortImpl(
        ohipReservationClient,
        ohipAvailabilityClient,
        reservationResponseOhipMapper,
        reservationRequestOhipMapper,
        confirmReservationRequestOhipMapper,
        rateChangeRequestOhipMapper,
        roomTypeChangeRequestOhipMapper,
        confirmationResponseOhipMapper,
        roomRateOhipMapper,
        reservationOhipProperties,
        reservationGuestRequestOhipMapper,
        reservationAccompanyingGuestRequestOhipMapper,
        reservationGuestResponseOhipMapper,
        reservationBookerRequestOhipMapper,
        reservationCompanyRequestOhipMapper,
        reservationPackagesOhipMapper,
        ohipPackagesClient,
        packagesRequestDtoOhipMapper,
        depositFoliosRequestOhipMapper,
        packageGroupsRequestOhipMapper,
        packagesSelectionMapper,
        cancelReservationRequestOhipMapper,
        cancelReservationResponseOhipMapper,
        priceBreakdownOhipMapper,
        discountRequestOhipMapper,
        rulesAgentClient,
        depositFoliosRequestMapper,
        depositFolioCriteriaMapper,
        searchBookingsResponseOhipMapper,
        updateReasonForStayRequestOhipMapper,
        updateReasonForStayResponseOhipMapper,
        businessItemsRequestOhipMapper,
        specialReqRequestOhipMapper,
        updateReservationOverrideReasonsRequestOhipMapper,
        updateReservationCcAgentIdRequestOhipMapper,
        depositsResponseOhipMapper,
        postedDepositsResponseOhipMapper,
        frontDeskClient,
        updateCancellationPolicyRequestOhipMapper,
        marketingPreferencesResponseOhipMapper,
        copyReservationsRequestOhipMapper,
        copyReservationsResponseOhipMapper,
        rulesAgentProperties,
        updateReservationRequestMapper,
        payOnArrivalReservationRequestOhipMapper,
        movePaymentDetailsOhipMapper,
        bookerProfileOhipMapper,
        companyProfileOhipMapper,
        updateReservationAttachedProfilesRequestOhipMapper,
        ohipProperties,
        updateBookerReservationProfileOhipMapper,
        updateBookerEmailOhipMapper,
        companyQuestionAndAnswerRequestOhipMapper,
        memosOhipMapper,
        attachReservationProfileRequestOhipMapper,
        updateCustomReferenceNumberOhipMapper,
        unleashWrapper,
        concurrentTracer,
        reservationAccompanyingGuestProfileRequestOhipMapper,
        linkReservationToLeisureCustomerRequestOhipMapper,
        updatePreferencesRequestOhipMapper,
        changeReservationRequestOhipMapper,
        roomSubstitutionResponseMapper,
        guestDetailsMapper, reservationAlertMapper, apiLimitsService);
  }

  @Bean
  public HotelReservationInPort reservationUseCase(HotelReservationOutPort ohipClientPort,
      HotelAvailabilityOutPort availabilityOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new HotelReservationInPortImpl(ohipClientPort, availabilityOutPort, unleashWrapper);
  }

  @Bean
  public DepositFoliosInPort createDepositFoliosBean(HotelReservationOutPort reservationOutPort) {
    return new DepositFoliosInPortImpl(reservationOutPort);
  }


  @Bean
  public HotelAvailabilityOutPort createAvailabilityOhipPortBean(
      AvailabilityRequestMapper availabilityRequestMapper,
      AvailabilityByIdsRequestMapper availabilityByIdsRequestMapper,
      OhipAvailabilityClient ohipAvailabilityClient,
      OhipProfileClient ohipProfileClient,
      OhipRatePlansClient ohipRatePlansClient,
      PriceBreakdownMapper priceBreakdownMapper,
      ItemsInventoryMapper itemsInventoryMapper,
      HotelRoomInventoryMapper hotelRoomInventoryMapper,
      RulesAgentClient rulesAgentClient,
      MultiAvailabilityRequestMapper multiAvailabilityRequestMapper,
      AvailabilityOhipProperties availabilityProperties,
      HotelInventoryStatisticsMapper hotelInventoryStatisticsMapper,
      UnleashWrapper<FeatureFlag> unleashWrapper,
      ExecutorService restrictionsExecutorService,
      ApiLimitsService apiLimitsService, ConcurrentTracer concurrentTracer) {
    return new HotelAvailabilityOutPortImpl(availabilityRequestMapper,
        availabilityByIdsRequestMapper, ohipAvailabilityClient,
        ohipProfileClient, ohipRatePlansClient,
        rulesAgentClient, priceBreakdownMapper, itemsInventoryMapper,
        hotelRoomInventoryMapper, multiAvailabilityRequestMapper,
        availabilityProperties, hotelInventoryStatisticsMapper, unleashWrapper, apiLimitsService, concurrentTracer);
  }

  @Bean
  public HotelAvailabilityInPort createAvailabilityPortBean(
      HotelAvailabilityOutPort hotelBookingPort, RulesAgentOutPort rulesAgentOutPort,
      MealsIncludedConfigProperties mealsConfig,
      AvailabilityConfigProperties availabilityProperties,
      ConcurrentTracer concurrentTracer,
      ExecutorService restrictionsExecutorService, UnleashWrapper<FeatureFlag> unleashWrapper,
      RatePlansOutPort ratePlansOutPort, Executor availabilityExecutor) {
    return new HotelAvailabilityInPortImpl(hotelBookingPort, rulesAgentOutPort, mealsConfig,
        availabilityProperties, concurrentTracer, restrictionsExecutorService, unleashWrapper,
        ratePlansOutPort, availabilityExecutor);
  }

  @Bean
  public RulesAgentOutPort createRulesAgentOutPortBean(
      RoomSubstitutionResponseMapper roomSubstitutionResponseMapper, BaseRateResponseMapper baseRateResponseMapper,
      RulesAgentClient rulesAgentClient) {
    return new RulesAgentOutPortImpl(roomSubstitutionResponseMapper, baseRateResponseMapper, rulesAgentClient);
  }

  @Bean
  public PackagesOutPort createPackagesOhipPortBean(
      PackagesRequestOhipMapper packagesRequestOhipMapper,
      DonationPackagesRequestOhipMapper donationPackagesRequestOhipMapper,
      OhipPackagesClient ohipPackagesClient, OhipRestaurantsClient ohipRestaurantsClient,
      PackagesResponseOhipMapper packagesResponseOhipMapper,
      PackageGroupsRequestOhipMapper packageGroupsRequestOhipMapper,
      PackageGroupResponseOhipMapper packageGroupResponseOhipMapper,
      DonationPackagesResponseOhipMapper donationPackagesResponseOhipMapper,
      ConcurrentTracer concurrentTracer) {
    return new PackagesOutPortImpl(packagesRequestOhipMapper, donationPackagesRequestOhipMapper,
        packagesResponseOhipMapper, donationPackagesResponseOhipMapper,
        ohipPackagesClient, ohipRestaurantsClient, concurrentTracer,
        packageGroupsRequestOhipMapper, packageGroupResponseOhipMapper);
  }

  @Bean
  public PackagesInPort createPackagesPortBean(PackagesOutPort packagesOutPort) {
    return new PackagesInPortImpl(packagesOutPort);
  }

  @Bean
  public RatePlansInPort createRatePlansPortBean(RatePlansOutPort ratePlansOutPort) {
    return new RatePlansInPortImpl(ratePlansOutPort);
  }

  @Bean
  public RatePlansOutPort createRatePlansOutPortBean(RatePlansMapper ratePlansMapper,
      NegotiatedRatesMapper negotiatedRatesMapper,
      OhipRatePlansClient ohipRatePlansClient,
      RatePlanInfoMapper ratePlanInfoMapper,
      PromotionCodeMapper promotionCodeMapper) {
    return new RatePlansOutPortImpl(ratePlansMapper, negotiatedRatesMapper, ohipRatePlansClient,
        ratePlanInfoMapper, promotionCodeMapper);
  }


  @Bean
  public HotelInfoOutPort createHotelInfoOhipPortBean(
      OhipHotelConfigClient ohipHotelConfigClient,
      OhipAvailabilityClient ohipAvailabilityClient,
      HotelInfoMapper hotelInfoMapper,
      RoomTypesInfoMapper roomTypesInfoMapper) {
    return new HotelInfoOutPortImpl(ohipHotelConfigClient, ohipAvailabilityClient,
        hotelInfoMapper, roomTypesInfoMapper);
  }

  @Bean
  public HotelInfoInPort createHotelInfoPortBean(HotelInfoOutPort hotelInfoOutPort) {
    return new HotelInfoInPortImpl(hotelInfoOutPort);
  }

  @Bean
  public HotelDetailsOutPortImpl createHotelDetailsOhipPortBean(
      OhipHotelDetailsClient ohipHotelDetailsClient,
      HotelStatusMapper hotelStatusMapper) {
    return new HotelDetailsOutPortImpl(ohipHotelDetailsClient, hotelStatusMapper);
  }

  @Bean
  public HotelDetailsInPort createHoteDetailsPortBean(HotelDetailsOutPort hotelDetailsOutPort) {
    return new HotelDetailsInPortImpl(hotelDetailsOutPort);
  }

  @Bean
  public EckohOutPort createEckohOutPort(EckohOhipClient eckohOhipClient,
      EckohChangeRequestMapper eckohChangeRequestMapper, UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new EckohOutPortImpl(eckohOhipClient, eckohChangeRequestMapper, unleashWrapper);
  }

  @Bean
  public EckohInPort createEckohInPort(EckohOutPort eckohOutPort) {
    return new EckohInPortImpl(eckohOutPort);
  }

  @Bean
  public ListOfValuesOutPort createListOfValuesOutPort(
      OhipListOfValuesClient ohipListOfValuesClient,
      CancellationReasonsOhipMapper cancellationReasonsOhipMapper) {
    return new ListOfValuesOutPortImpl(ohipListOfValuesClient, cancellationReasonsOhipMapper);
  }

  @Bean
  public ListOfValuesInPort createListOfValuesInPort(ListOfValuesOutPort listOfValuesOutPort) {
    return new ListOfValuesInPortImpl(listOfValuesOutPort);
  }

  @Bean
  public AmendOutPort amendOutPort(OhipReservationClient ohipReservationClient) {
    return new AmendOutPortImpl(ohipReservationClient);
  }

  @Bean
  public AmendInPort amendInPort(AmendOutPort amendOutPort) {
    return new AmendInPortImpl(amendOutPort);
  }

  @Bean
  public RoomAllocationInPort createRoomAllocationInPort(
      RoomAllocationOutPort roomAllocationOutPort) {
    return new RoomAllocationInPortImpl(roomAllocationOutPort);
  }

  @Bean
  public RoomAllocationOutPort createRoomAllocationOutPort(
      OhipRoomAllocationClient ohipRoomAllocationClient,
      OhipReservationClient ohipReservationClient,
      ReservationPreferenceMapper reservationPreferenceMapper) {
    return new RoomAllocationOutPortImpl(ohipRoomAllocationClient, ohipReservationClient,
        reservationPreferenceMapper);
  }

  @Bean
  public ProfileInPort createProfileInPort(ProfileOutPort profileOutPort) {
    return new ProfileInPortImpl(profileOutPort);
  }

  @Bean
  public ProfileOutPort createProfileOutPort(OhipProfileClient ohipProfileClient,
      OhipProfileMapper ohipProfileMapper, OhipReservationClient ohipReservationClient,
      OhipUpdateProfileRequestMapper ohipUpdateProfileRequestMapper,
      OhipCreateProfileRequestMapper ohipCreateProfileRequestMapper) {
    return new ProfileOutPortImpl(ohipProfileClient, ohipReservationClient, ohipProfileMapper,
        ohipUpdateProfileRequestMapper, ohipCreateProfileRequestMapper);
  }

  @Bean
  public CheckInInPort createCheckInInPort(CheckInOutPort checkInOutPort) {
    return new CheckInInPortImpl(checkInOutPort);
  }

  @Bean
  public CheckInOutPort createCheckInOutPort(OhipCheckInClient ohipCheckInClient,
      UpdateReservationCommentOhipMapper updateReservationCommentOhipMapper) {
    return new CheckInOutPortImpl(ohipCheckInClient, updateReservationCommentOhipMapper);
  }

  @Bean
  public PreferenceOutPort createPreferenceOutPort(OhipPreferencesClient ohipPreferencesClient,
      HotelPreferencesMapper hotelPreferencesMapper) {
    return new PreferencesOutPortImpl(ohipPreferencesClient, hotelPreferencesMapper);
  }

  @Bean
  public PreferenceInPort createPreferenceInPort(PreferenceOutPort preferenceOutPort) {
    return new PreferenceInPortImpl(preferenceOutPort);
  }

  @Bean
  public UdfsInPort udfsInPort(UdfsOutPort udfsOutPort) {
    return new UdfsInImpl(udfsOutPort);
  }

  @Bean
  public UdfsOutPort udfsOutPort(OhipReservationClient ohipReservationClient,
      UpdateReservationOverrideUdfsRequestOhipMapper mapper) {
    return new UdfsOutPortImpl(ohipReservationClient, mapper);
  }
}