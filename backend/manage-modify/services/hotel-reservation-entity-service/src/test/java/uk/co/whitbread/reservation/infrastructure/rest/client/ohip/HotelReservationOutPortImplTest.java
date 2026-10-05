package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import reactor.util.function.Tuples;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AddressDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AttachReservationProfileRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelInformationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmAmendOnReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateMemoRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MarketingPreferencesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypeChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetails;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.in.BusinessItems;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.StayingGuest;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestDetails;
import uk.co.whitbread.reservation.domain.model.in.UpdateBookerEmailRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationCustomer;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationRoomStay;
import uk.co.whitbread.reservation.domain.model.out.Customer;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.DonationPackage;
import uk.co.whitbread.reservation.domain.model.out.DonationPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationDetails;
import uk.co.whitbread.reservation.domain.model.out.ReservationId;
import uk.co.whitbread.reservation.domain.model.out.ReservationIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.out.SearchBooking;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhAddressMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhEmployeeMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;
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
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.AmendDistributionSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.SearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.CacheReservationResponseHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.HandleCiolRevertHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterTimeoutConfiguredClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationSingleCallResponseDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class HotelReservationOutPortImplTest {

  private HotelReservationOhipOutPortImpl hotelReservationOutPort;

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private CdhAdapterClient cdhAdapterClient;

  @Mock
  private ReservationResponseOhipMapper reservationResponseOhipMapper;

  @Mock
  private ReservationRequestOhipMapper reservationRequestOhipMapper;

  @Mock
  private ReservationByBasketRefOhipResponseMapper reservationByBasketRefOhipResponseMapper;

  @Mock
  private ConfirmReservationResponseOhipMapper confirmReservationResponseOhipMapper;

  @Mock
  private ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper;

  @Mock
  private ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper;

  @Mock
  private UpdateRateCodeRequestOhipMapper updateRateCodeRequestOhipMapper;

  @Mock
  private ReservationsPackagesOhipMapper reservationsPackagesOhipMapper;

  @Mock
  private BasketClient basketClient;

  @Mock
  private UpdatePackagesRequestOhipMapper updatePackagesRequestOhipMapper;

  @Mock
  private CancelResponseOhipMapper cancelResponseOhipMapper;

  @Mock
  private CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper;

  @Mock
  private CancelReservationResponseOhipMapper cancelReservationResponseOhipMapper;

  @Mock
  private UpdateDiscountRequestOhipMapper updateDiscountRequestOhipMapper;

  @Mock
  private HotelInformationOhipMapper hotelInformationOhipMapper;

  @Mock
  private SearchBookingsRequestOhipMapper searchBookingsRequestOhipMapper;

  @Mock
  private SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper;

  @Mock
  private UpdateReasonForStayOhipMapper updateReasonForStayOhipMapper;

  @Mock
  private CompanyQuestionAndAnswerOhipMapper companyQuestionAndAnswerOhipMapper;

  @Mock
  private BusinessItemsRequestOhipMapper businessItemsRequestOhipMapper;

  @Mock
  private SpecialRequestsOhipMapper specialRequestsOhipMapper;

  @Mock
  private UpdateReservationOverrideReasonsRequestOhipMapper
      updateReservationOverrideReasonsRequestOhipMapper;

  @Mock
  private UpdateReservationCcAgentIdRequestOhipMapper
      updateReservationCcAgentIdRequestOhipMapper;

  @Mock
  private DepositsResponseOhipMapper depositsResponseOhipMapper;

  @Mock
  private CancellationPoliciesOhipMapper cancellationPoliciesOhipMapper;

  @Mock
  private MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper;

  @Mock
  private CopyReservationsRequestOhipMapper copyReservationsRequestOhipMapper;

  @Mock
  private CopyReservationsResponseOhipMapper copyReservationsResponseOhipMapper;

  @Mock
  private ConfirmAmendRequestOhipMapper confirmAmendRequestOhipMapper;

  @Mock
  private AmendDistributionSingleCallRequestOhipMapper amendDistributionSingleCallRequestOhipMapper;

  @Mock
  private UpdateReservationRequestOhipMapper updateReservationRequestOhipMapper;

  @Mock
  private ReservationOhipMapper reservationOhipMapper;

  @Mock
  private RatePlansResponseOhipMapper ratePlansResponseOhipMapper;

  @Mock
  private DonationPackagesResponseOhipMapper donationPackagesResponseOhipMapper;
  @Mock
  private PackagesRequestMapper packagesRequestMapper;
  @Mock
  private PackagesResponseMapper packagesResponseMapper;

  @Mock
  private ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper;

  @Mock
  private RulesAdapterProperties rulesAdapterProperties;

  @Mock
  private UpdateBookerEmailRequestOhipMapper updateBookerEmailRequestOhipMapper;

  @Mock
  private DepositFoliosResponseOhipMapper depositFoliosResponseOhipMapper;

  @Mock
  private MemosOhipMapper memosOhipMapper;

  @Mock
  private AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper;

  @Mock
  private ReservationFileAttachmentRequestOhipMapper reservationFileAttachmentRequestOhipMapper;

  @Mock
  private PreCheckInRequestOhipMapper preCheckInRequestOhipMapper;

  @Mock
  private OhipAdapterTimeoutConfiguredClient ohipAdapterTimeoutConfiguredClient;

  private HotelAvailabilityRequestMapper requestMapper;

  @Mock
  private HotelAvailabilityMapper availabilityMapper;

  @Mock
  private UpdateReservationScheduledMapper updateReservationScheduledMapper;

  @Mock
  private LinkReservationToLeisureCustomerRequestOhipMapper
      linkReservationToLeisureCustomerRequestOhipMapper;
  @Mock
  private UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper;

  @Mock
  private CdhEmployeeMapper cdhEmployeeMapper;

  @Mock
  private CdhAddressMapper cdhAddressMapper;

  @Mock
  private HandleCiolRevertHelper handleCiolRevertHelper;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private CacheReservationResponseHelper cacheReservationResponseHelper = new CacheReservationResponseHelper(cacheManager);

  @Mock
  private UpdateRoomTypeOhipMapper updateRoomTypeOhipMapper;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  private final HotelReservationOhipException exception = new HotelReservationOhipException("message",
      "An error was returned by OHIP Adapter!", new Exception(), 900);

  private static UniqueIDType createUniqueIDType(String id, String type) {
    return UniqueIDType.builder()
        .id(id)
        .type(type)
        .build();
  }

  private static ConfirmationRoomStay getConfirmationRoomStayResponse() {
    return ConfirmationRoomStay.builder()
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now()).build();
  }

  public static ConfirmationCustomer getConfirmationCustomer() {
    return ConfirmationCustomer.builder()
        .givenName("Emma")
        .surName("Smith").build();
  }

  @BeforeEach
  public void before() {
    hotelReservationOutPort = new HotelReservationOhipOutPortImpl(ohipAdapterClient,
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

  @Test
  void testCreateReservation_success() {
    // Arrange
    when(reservationRequestOhipMapper.toDto(any())).thenReturn(new ReservationRequestDto());

    when(ohipAdapterClient.createReservation(any()))
        .thenReturn(new ReservationResponseDto());

    when(reservationResponseOhipMapper.toModel(any(ReservationResponseDto.class))).thenReturn(
        mockReservationResponse());

    // Act
    var reservationResponse = hotelReservationOutPort.createReservation("123",
        mockCreateReservationRequest(), "AQN-b6946473-b9c4-4669-81b6-57d7279602fa");

    // Assert
    verifyNoMoreInteractions(reservationRequestOhipMapper);
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(reservationResponseOhipMapper);
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  @Test
  void testGetReservationsByBasketReference_success() {
    // Arrange
    when(ohipAdapterClient.getReservationsByBasketReference(anyString(), anyString(), anyInt(),
        anyInt())).thenReturn(new ReservationsDetailsResponseDto());

    when(
        reservationResponseOhipMapper.toModel(
            any(ReservationsDetailsResponseDto.class))).thenReturn(
        new ReservationsDetailsResponse());

    // Act
    var reservations =
        hotelReservationOutPort.getReservationsByBasketReference("123", "456", 20, 0);

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(reservationResponseOhipMapper);
    assertNotNull(reservations);
  }


  @Test
  void testGetReservationByResId_success() {
    // Arrange
    when(ohipAdapterClient.sendGetReservationsByReservationId("TEST123456", "MANOLD"))
        .thenReturn(new ReservationIdDetailsDto());
    when(reservationResponseOhipMapper.toModel(any(ReservationIdDetailsDto.class)))
        .thenReturn(mockReservationByIdResponse());
    // Act
    var reservations =
        hotelReservationOutPort.getReservationsByReservationId("TEST123456", "MANOLD");

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(reservationResponseOhipMapper);
    assertNotNull(reservations);

  }

  @Test
  void testConfirmReservation_success() {
    // Arrange
    when(confirmReservationRequestOhipMapper.toDto(any()))
        .thenReturn(new ConfirmReservationRequestDto());

    when(ohipAdapterClient.sendConfirmReservationRequest(any()))
        .thenReturn(new ConfirmReservationResponseDto());

    when(confirmReservationResponseOhipMapper.toModel(any(ConfirmReservationResponseDto.class)))
        .thenReturn(
            new ConfirmReservationResponse(getConfirmedReservatinIds(),
                getConfirmationRoomStayResponse(),
                getConfirmationCustomer(),
                "LONEUS",
                "Reserved",
                true));

    // Act
    var reservationResponse = hotelReservationOutPort.confirmReservation(
        mockConfirmReservationRequest());

    // Assert
    verifyNoMoreInteractions(confirmReservationRequestOhipMapper);
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(confirmReservationResponseOhipMapper);
    assertThat(reservationResponse.getReservationIdList().get(1).getId(), is("852"));
    assertThat(reservationResponse.getReservationStatus(), is("Reserved"));
  }

  @Test
  void testCreateReservationGuest_success() {
    // Arrange
    when(reservationGuestRequestOhipMapper.toDto(any())).thenReturn(
        new ReservationGuestRequestDto());
    when(ohipAdapterClient.sendReservationGuestRequest(any()))
        .thenReturn(new ReservationGuestResponseDto());

    // Act
    hotelReservationOutPort.createReservationGuest(
        ReservationGuestRequest.builder().build());

    // Assert
    verifyNoMoreInteractions(reservationGuestRequestOhipMapper);
    verifyNoMoreInteractions(ohipAdapterClient);

  }

  @Test
  void testCreateReservationGuest_populateGuestAddresses() {
    // Arrange
    String profileAddressLine1 = "line1";
    String profilePostalCode = "123";

    GetEmployeeResponseDto employee = new GetEmployeeResponseDto();
    AddressDto address = new AddressDto();
    address.setAddressLine1(profileAddressLine1);
    address.setPostCode(profilePostalCode);
    employee.setAddress(address);

    ReservationGuestRequest reservationGuestRequest = ReservationGuestRequest.builder()
        .companyAccountId("company_id")
        .booker(BookerDetails.builder().emailAddress("john@wb.com").build())
        .stayingGuests(List.of(StayingGuest.builder()
            .stayingGuestDetails(StayingGuestDetails.builder()
                .employeeAccountId("employee_id")
                .build())
            .build()))
        .build();

    when(cdhAdapterClient.getEmployee(any())).thenReturn(employee);
    when(cdhAddressMapper.toStayingGuestAddressModel(address)).thenReturn(
        StayingGuestAddress.builder().addressLine1(profileAddressLine1)
            .postalCode(profilePostalCode).build());
    when(ohipAdapterClient.sendReservationGuestRequest(any()))
        .thenReturn(new ReservationGuestResponseDto());

    // Act
    hotelReservationOutPort.createReservationGuest(reservationGuestRequest);

    // Assert
    StayingGuestAddress guestAddress = reservationGuestRequest.getStayingGuests().get(0)
        .getStayingGuestDetails().getAddress();
    assertThat(guestAddress, notNullValue());
    assertThat(guestAddress.getAddressLine1(), is(profileAddressLine1));
    assertThat(guestAddress.getPostalCode(), is(profilePostalCode));
  }

  @Test
  void testCreateReservationGuest_populateGuestAddresses_requestGuestAlreadyHasAddress() {
    // Arrange
    String reqAddressLine1 = "reqline1";
    String reqPostalCode = "req123";

    ReservationGuestRequest reservationGuestRequest = ReservationGuestRequest.builder()
        .companyAccountId("company_id")
        .booker(BookerDetails.builder().emailAddress("john@wb.com").build())
        .stayingGuests(List.of(StayingGuest.builder()
            .stayingGuestDetails(StayingGuestDetails.builder()
                .employeeAccountId("employee_id")
                .address(StayingGuestAddress.builder().addressLine1(reqAddressLine1)
                    .postalCode(reqPostalCode).build())
                .build())
            .build()))
        .build();

    when(ohipAdapterClient.sendReservationGuestRequest(any()))
        .thenReturn(new ReservationGuestResponseDto());

    // Act
    hotelReservationOutPort.createReservationGuest(reservationGuestRequest);

    // Assert
    StayingGuestAddress guestAddress = reservationGuestRequest.getStayingGuests().get(0)
        .getStayingGuestDetails().getAddress();
    assertThat(guestAddress, notNullValue());
    assertThat(guestAddress.getAddressLine1(), is(reqAddressLine1));
    assertThat(guestAddress.getPostalCode(), is(reqPostalCode));
  }

  @Test
  void testCreateReservationGuest_populateGuestAddresses_requestEmployeeIdIsNull() {
    // Arrange
    ReservationGuestRequest reservationGuestRequest = ReservationGuestRequest.builder()
        .companyAccountId("company_id")
        .booker(BookerDetails.builder().emailAddress("john@wb.com").build())
        .stayingGuests(List.of(StayingGuest.builder()
            .stayingGuestDetails(StayingGuestDetails.builder().build())
            .build()))
        .build();

    when(ohipAdapterClient.sendReservationGuestRequest(any()))
        .thenReturn(new ReservationGuestResponseDto());

    // Act
    hotelReservationOutPort.createReservationGuest(reservationGuestRequest);

    // Assert
    StayingGuestAddress guestAddress = reservationGuestRequest.getStayingGuests().get(0)
        .getStayingGuestDetails().getAddress();
    assertThat(guestAddress, nullValue());
  }

  @Test
  void testCreateReservationGuest_populateGuestAddresses_profileAddressIsNull() {
    // Arrange
    ReservationGuestRequest reservationGuestRequest = ReservationGuestRequest.builder()
        .companyAccountId("company_id")
        .booker(BookerDetails.builder().emailAddress("john@wb.com").build())
        .stayingGuests(List.of(StayingGuest.builder()
            .stayingGuestDetails(StayingGuestDetails.builder()
                .employeeAccountId("employee_id")
                .build())
            .build()))
        .build();

    when(cdhAdapterClient.getEmployee(any())).thenReturn(new GetEmployeeResponseDto());
    when(ohipAdapterClient.sendReservationGuestRequest(any()))
        .thenReturn(new ReservationGuestResponseDto());

    // Act
    hotelReservationOutPort.createReservationGuest(reservationGuestRequest);

    // Assert
    StayingGuestAddress guestAddress = reservationGuestRequest.getStayingGuests().get(0)
        .getStayingGuestDetails().getAddress();
    assertThat(guestAddress, nullValue());
  }

  @Test
  void testCancelReservation_success() {
    // Arrange
    when(cancelReservationRequestOhipMapper.toDto(any(), any())).thenReturn(
        new CancelReservationRequestDto());
    when(ohipAdapterClient.sendCancelReservationRequest(any()))
        .thenReturn(new CancelReservationResponseDto());

    // Act
    hotelReservationOutPort.cancelReservation(
        CancelReservationRequest.builder().build(), null);

    // Assert
    verifyNoMoreInteractions(cancelReservationRequestOhipMapper);
    verifyNoMoreInteractions(ohipAdapterClient);

  }

  @Test
  void testGetCancel_success() {
    // Arrange
    when(ohipAdapterClient.sendGetCancelInformationRequest("TestHotelId",
        Collections.singleton("12345"), "2022-09-15T07:47:19 00:00"))
        .thenReturn(new CancelInformationResponseDto());

    when(
        cancelResponseOhipMapper.toModel(any(CancelInformationResponseDto.class))).thenReturn(
        new CancelInformationResponse(true));

    // Act
    var reservations =
        hotelReservationOutPort.getCancelInformation("TestHotelId",
            Collections.singleton("12345"), "2022-09-15T07:47:19 00:00");

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(reservationResponseOhipMapper);
    assertNotNull(reservations);
  }

  @Test
  void testGetReservationByExternalRefId_success() {
    // Arrange
    when(ohipAdapterClient.sendGetReservationsByExternalReferenceId("TEST123456"))
        .thenReturn(new ReservationDetailsEnhancedDto());
    when(reservationResponseOhipMapper.toModel(any(ReservationDetailsEnhancedDto.class)))
        .thenReturn(mockReservationDetailsEnhancedResponse());
    // Act
    var reservations =
        hotelReservationOutPort.getReservationsByExternalId("TEST123456");

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(reservationResponseOhipMapper);
    assertNotNull(reservations);

  }

  @Test
  void testGetReservationByExternalRefId_failure() {
    // Arrange
    when(ohipAdapterClient.sendGetReservationsByExternalReferenceId("ABCD123456"))
        .thenThrow(exception);

    // Act
    Exception thrownException = assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetReservationsByExternalReferenceId("ABCD123456"));

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    assertThat(thrownException, notNullValue());
    assertThat(thrownException.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void testSearchBookings_success() {
    // Arrange
    when(ohipAdapterClient.sendSearchBookings(any(SearchBookingsRequestDto.class)))
        .thenReturn(new SearchBookingsResponseDto());
    when(searchBookingsRequestOhipMapper.toDto(any(SearchBookingsRequest.class)))
        .thenReturn(new SearchBookingsRequestDto());
    when(searchBookingsResponseOhipMapper.toModel(any(SearchBookingsResponseDto.class)))
        .thenReturn(mockSearchBookingsResponse());
    // Act
    var reservations = hotelReservationOutPort.searchBookings(mockSearchBookingsRequest());

    // Assert
    assertNotNull(reservations);
    assertEquals(1, reservations.getBookings().size());
    assertFalse(reservations.isHasMore());
  }

  @Test
  void testSearchBookings_failure() {
    // Arrange
    var booking = SearchBookingsRequestDto.builder().build();
    when(searchBookingsRequestOhipMapper.toDto(any())).thenReturn(booking);
    when(ohipAdapterClient.sendSearchBookings(booking)).thenThrow(exception);

    var searchBookingRequest = mockSearchBookingsRequest();
    // Act
    Exception thrownException = assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationOutPort.searchBookings(searchBookingRequest));

    // Assert
    assertThat(thrownException, notNullValue());
    assertThat(thrownException.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void testUpdateCompanyQuestionAndAnswer_success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort
        .updateCompanyQuestionAndAnswerDetails(mockUpdateCompanyQuestionAndAnswerDetailsRequest()));
  }

  @Test
  void testUpdateDiscount_success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateDiscount(
        mockUpdateDiscountRequest()));
  }

  @Test
  void testUpdateBusinessItems_Success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(
        mockBusinessItemsRequest()));
  }

  @Test
  void testUpdateSpecialRequests_Success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateSpecialRequests(
        mockSpecialRequests()));
  }

  private SpecialRequests mockSpecialRequests() {
    return SpecialRequests.builder()
        .hotelId("MANOLD")
        .build();
  }

  @Test
  void updateReasonForStay_success() {
    //Arrange
    final var updateReasonForStayRequest = createReasonForStayRequest();
    final var updateReasonForStayRequestDto = mockUpdateReasonForStayDto();
    when(updateReasonForStayOhipMapper.toDto(any())).thenReturn(updateReasonForStayRequestDto);

    //Act
    hotelReservationOutPort.updateReasonForStay(updateReasonForStayRequest);

    // Assert
    verify(ohipAdapterClient, times(1))
        .sendUpdateReasonForStayRequest(updateReasonForStayRequestDto);

  }

  @Test
  void testUpdateReservationOverrideReasons_success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateReservationOverrideReasons(
        mockUpdateReservationOverrideReasonsRequest()));
  }

  @Test
  void testUpdateReservationCcAgentId_success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateReservationCcAgentId(
        mockUpdateReservationCcAgentIdRequest()));
  }

  @Test
  void testGetDepositsByResId_success() {
    String hotelID = "DAHMME";
    String resNo = "12345678";
    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(
        Collections.singletonList(Deposits.builder().paymentReference("3CPREFERENCE")
            .build()));
    // Arrange
    when(ohipAdapterClient.getDepositsByReservationId(hotelID, resNo))
        .thenReturn(new DepositsResponseDto());

    when(depositsResponseOhipMapper.toModel(any(DepositsResponseDto.class)))
        .thenReturn(depositsResponse);

    // Act
    var response = hotelReservationOutPort.getDepositsForReservationId(hotelID, resNo);

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(depositsResponseOhipMapper);
    assertThat(response.getDeposits().get(0).getPaymentReference(), is("3CPREFERENCE"));
  }

  @Test
  void getCancellationPolicies_success() {
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    // Arrange
    when(ohipAdapterClient.getCancellationPolicies(reservationIds, hotelId, null, null))
        .thenReturn(createCancellationPoliciesDto());

    when(cancellationPoliciesOhipMapper.toModel(createCancellationPoliciesDto()))
        .thenReturn(createCancellationPolicies());

    // Act
    var response = hotelReservationOutPort.getCancellationPolicies(
        reservationIds, hotelId, null, null);

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(depositsResponseOhipMapper);
    assertThat(response.getText(),
        is("Cancellations after 1pm on the day of arrival charged 100% of 1 night"));
  }

  @Test
  void getCancellationPolicies_ThrowsException() {
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    // Arrange
    when(ohipAdapterClient.getCancellationPolicies(reservationIds, hotelId, null, null))
        .thenThrow(HotelReservationOhipException.class);

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationOutPort.getCancellationPolicies(
            reservationIds, hotelId, null, null));
  }

  @Test
  void testGetMarketingPreferences_success() {
    String hotelID = "DAHMME";
    String resNo = "12345678";

    var marketingPreferencesResponse = new MarketingPreferencesResponse();
    marketingPreferencesResponse.setContactValue("mail@mail.com");
    marketingPreferencesResponse.setOptIn(true);
    marketingPreferencesResponse.setCustomer(new Customer("Mr", "Sarah", "Smith", "GB", "en"));

    // Arrange
    when(ohipAdapterClient.getMarketingPreferences(hotelID, resNo)).thenReturn(
        new MarketingPreferencesResponseDto());

    when(marketingPreferencesResponseOhipMapper.toModel(
        any(MarketingPreferencesResponseDto.class))).thenReturn(
        marketingPreferencesResponse);

    // Act
    var response = hotelReservationOutPort.getMarketingPreferences(hotelID, resNo);

    verifyNoMoreInteractions(ohipAdapterClient);
    verifyNoMoreInteractions(marketingPreferencesResponseOhipMapper);
    assertEquals("mail@mail.com", response.getContactValue());
    assertTrue(response.getOptIn());
    assertNotNull(response.getCustomer());
    assertEquals("Sarah", response.getCustomer().getFirstName());
  }

  @Test
  void deleteReservation_Success() {
    //Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";
    doNothing().when(ohipAdapterClient).deleteReservationRequest(anyString(), anyString());

    //Act
    hotelReservationOutPort.deleteReservation(hotelId, reservationId);

    //Assert
    verify(ohipAdapterClient).deleteReservationRequest(hotelId, reservationId);
  }

  @Test
  void testGetBookingAllowances_success() {
    String hotelId = "HOTELID";
    String reservationId = "1234567";
    List<String> basketAllowances = List.of("accommodation");
    var bookingAllowancesResponse = createBookingAllowancesResponse();

    // Arrange
    when(ohipAdapterClient.getBookingAllowances(anyString(), anyString(), any()))
        .thenReturn(bookingAllowancesResponse);

    // Act
    var response = hotelReservationOutPort.getBookingAllowances(hotelId, reservationId, basketAllowances);

    verifyNoMoreInteractions(ohipAdapterClient);
    assertNotNull(response);
    assertEquals(1, response.getBookingAllowances().size());
    assertEquals("dinner", response.getBookingAllowances().get(0).getAllowance());
    assertEquals(BigDecimal.TEN, response.getBookingAllowances().get(0).getBudget());
  }

  @Test
  void updateReservations_Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequest();
    when(reservationOhipMapper.toUpdateReservationsRequestDto(any(UpdateReservationsRequest.class)))
        .thenReturn(mockUpdateReservationsRequestDto());

    //Act
    hotelReservationOutPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(ohipAdapterClient, times(1)).updateReservations(mockUpdateReservationsRequestDto());
  }

  @Test
  void updateReservationsSingleCall_Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequest();
    var updateReservationsRequestDto = mockUpdateReservationsRequestDto();
    UpdateReservationRequest request = new UpdateReservationRequest();
    UpdateReservationRequestDto requestDto = new UpdateReservationRequestDto();
    requestDto.setReservationId("123");
    updateReservationsRequestDto.setUpdateReservationsRequest(List.of(requestDto));
    request.setReservationId(
        updateReservationsRequestDto.getUpdateReservationsRequest().get(0).getReservationId());
    updateReservationsRequest.setReservations(List.of(request));
    when(reservationOhipMapper.toUpdateReservationsRequestDto(any(UpdateReservationsRequest.class)))
        .thenReturn(updateReservationsRequestDto);

    //Act
    hotelReservationOutPort.updateReservationsSingleCall(updateReservationsRequest);

    //Assert
    verify(ohipAdapterClient, times(1)).updateReservations(updateReservationsRequestDto);
  }

  @Test
  void getCharityPackagesDetails_Success() {
    //Arrange
    when(ohipAdapterClient.sendCharityPackagesDetailsRequest(anyString(), anyList()))
        .thenReturn(new DonationPackagesResponseDto());
    when(donationPackagesResponseOhipMapper.toModel(
        any(DonationPackagesResponseDto.class))).thenReturn(
        DonationPackagesResponse.builder().donationPackages(List.of(new DonationPackage()))
            .build());

    //Act
    var response = hotelReservationOutPort.getCharityPackagesDetails("HOTELTEST",
        List.of("ZCHRY3"));

    //Assert
    assertNotNull(response);
  }

  @Test
  void getCharityPackagesDetails_NullResponse() {
    //Act
    var response = hotelReservationOutPort.getCharityPackagesDetails("HOTELTEST",
        List.of());

    //Assert
    assertNull(response);
  }

  @Test
  void movePaymentDetails_success() {
    //Arrange
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147890");

    doNothing().when(ohipAdapterClient).movePaymentDetails(anyString(), any());

    //Act
    hotelReservationOutPort.movePaymentDetails(hotelId, reservationIds);

    //Assert
    verify(ohipAdapterClient, times(1)).movePaymentDetails(hotelId, reservationIds);
  }

  @Test
  void getReservationsByBasketReservationIds_Success() {
    //Arrange
    when(ohipAdapterClient.sendGetReservationsByIds(anyString(), anyList(), anyBoolean(),
        anyBoolean(), anyBoolean()))
        .thenReturn(new ReservationByBasketRefResponseDto());
    when(reservationByBasketRefOhipResponseMapper.toModel(
        any(ReservationByBasketRefResponseDto.class)))
        .thenReturn(ReservationByBasketRefResponse.builder().hotelId("HOTELTEST").build());
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose()))
        .thenReturn(true);

    //Act
    var response = hotelReservationOutPort.getReservationsByIds("HOTELTEST",
        List.of("123456"), false, false, false);

    //Assert
    assertNotNull(response);
  }

  @Test
  void getReservationsByBasketReservationIds_SuccessWithTrue() {
    // Arrange
    when(ohipAdapterClient.sendGetReservationsByIds(anyString(), anyList(), anyBoolean(),
        anyBoolean(), anyBoolean()))
        .thenReturn(new ReservationByBasketRefResponseDto());

    when(reservationByBasketRefOhipResponseMapper.toModel(
        any(ReservationByBasketRefResponseDto.class)))
        .thenReturn(
            ReservationByBasketRefResponse.builder()
                .hotelId("HOTELTEST")
                .reservationByIdList(List.of())
                .build()
        );

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose()))
        .thenReturn(false);

    // Act
    var response = hotelReservationOutPort.getReservationsByIds(
        "HOTELTEST", List.of("123456"), false, false, false);

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({"true, false"})
  void getReservationsPackagesByBasketRef_ThrowsException(boolean mealInclusiveRate) {
    // Arrange
    when(basketClient.sendGetBasket(anyString())).thenThrow(HotelReservationOhipException.class);

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationOutPort.getReservationsPackagesByBasketRef("hotelTest", "basketRef",
            mealInclusiveRate));
  }

  @ParameterizedTest
  @CsvSource({"true, false", "true, true", "false, false", "false, true"})
  void getReservationsPackagesByBasketRef_Success(boolean mealInclusiveRate, boolean emptyRoomSelection) {
    // Arrange
    var basketResponse = new BasketDto();
    var basketItemDto = new BasketItemDto();
    basketItemDto.setSourceId("123");
    basketResponse.setStatus(StatusEnum.COMPLETED);
    basketResponse.setItems(List.of(basketItemDto));

    when(basketClient.sendGetBasket(anyString())).thenReturn(Tuples.of(basketResponse, "123"));

    when(ohipAdapterClient.getReservationsPackagesByIdsRequest(anyString(), anyList(),
        eq(mealInclusiveRate)))
        .thenReturn(new ReservationsPackagesResponseDto());
    if (emptyRoomSelection) {
      when(reservationsPackagesOhipMapper.toModel(
          any(ReservationsPackagesResponseDto.class))).thenReturn(
          ReservationsPackagesResponse.builder().build());
    } else {
      when(reservationsPackagesOhipMapper.toModel(
          any(ReservationsPackagesResponseDto.class))).thenReturn(
          ReservationsPackagesResponse.builder()
              .roomsSelections(List.of(new RoomsSelectionsByReservation("1",
                  List.of(new PackagesSelection("123", 1)))))
              .ratePlanCode("FLEXRATE")
              .roomsSelectionsAmendExtras(null)
              .build());
    }

    //Act
    var response = hotelReservationOutPort.getReservationsPackagesByBasketRef("hotelTest",
        "basketRef", mealInclusiveRate);

    //Assert
    assertNotNull(response);
    verify(basketClient).sendGetBasket(anyString());
    verify(ohipAdapterClient).getReservationsPackagesByIdsRequest(anyString(), anyList(), eq(mealInclusiveRate));
    verify(reservationsPackagesOhipMapper).toModel(any(ReservationsPackagesResponseDto.class));

  }

  @ParameterizedTest
  @CsvSource({"HSCKIN,false", "HSCOU2,false", "FI24HR,false", "FI24HR,true"})
  void getReservationsPackagesByBasketRef_SuccessAmend(String packageParam,
      boolean emptyRoomSelection) {
    // Arrange
    var basketResponse = new BasketDto();
    var basketItemDto1 = new BasketItemDto();
    basketItemDto1.setSourceId("123");
    var basketItemDto2 = new BasketItemDto();
    basketItemDto2.setSourceId("456");
    basketResponse.setStatus(StatusEnum.COMPLETED);
    basketResponse.setItems(List.of(basketItemDto1, basketItemDto2));
    basketResponse.setLinkAmendReservations(Map.of("123", "345"));
    var packagesResponse = ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(
            new RoomsSelectionsByReservation("345",
                List.of(
                    new PackagesSelection(packageParam, 1),
                    new PackagesSelection("BIBB", 1)))))
        .ratePlanCode("FLEXRATE")
        .build();
    var resEmptyRoomSelection = ReservationsPackagesResponse.builder().build();

    when(basketClient.sendGetBasket(anyString())).thenReturn(Tuples.of(basketResponse, "123"));

    when(ohipAdapterClient.getReservationsPackagesByIdsRequest(anyString(), anyList(),
        eq(false))).thenReturn(
        new ReservationsPackagesResponseDto());
    if (emptyRoomSelection) {
      when(reservationsPackagesOhipMapper.toModel(
          any(ReservationsPackagesResponseDto.class))).thenReturn(resEmptyRoomSelection);
    } else {
      when(reservationsPackagesOhipMapper.toModel(
          any(ReservationsPackagesResponseDto.class))).thenReturn(packagesResponse);
    }

    //Act
    var response = hotelReservationOutPort.getReservationsPackagesByBasketRef("hotelTest",
        "basketRef", false);

    //Assert
    assertNotNull(response);
    verify(basketClient).sendGetBasket(anyString());
    verify(ohipAdapterClient, times(2)).getReservationsPackagesByIdsRequest(anyString(), anyList(),
        eq(false));
    verify(reservationsPackagesOhipMapper, times(2)).toModel(
        any(ReservationsPackagesResponseDto.class));

    if (emptyRoomSelection) {
      assertEquals(0, response.getRoomsSelectionsAmendExtras().size());
    } else {
      assertEquals(1, response.getRoomsSelectionsAmendExtras().size());
      assertEquals("123", response.getRoomsSelectionsAmendExtras().get(0).getReservationId());
      assertEquals(List.of(new PackagesSelection(packageParam, 1)),
          response.getRoomsSelectionsAmendExtras().get(0).getPackagesSelection());
    }
  }

  @Test
  void updateReservationPackagesByReservationId_Success() {
    // Arrange
    var basketRef = "basketRef";
    var request = new UpdateReservationPackagesByIdRequest();
    request.setBasketReference(basketRef);
    var basketResponse = new BasketDto();
    basketResponse.setStatus(StatusEnum.COMPLETED);

    when(updatePackagesRequestOhipMapper.toDto(any())).thenReturn(
        new ReservationPackagesRequestDto());
    doNothing().when(ohipAdapterClient).sendUpdateReservationPackagesRequest(any());

    // Act
    var response = hotelReservationOutPort.updateReservationPackagesByReservationId(request);

    // Assert
    assertNotNull(response);
  }

  @Test
  void updateReservationPackagesByReservationId_SuccessPayPending() {
    // Arrange
    var basketRef = "basketRef";
    var request = new UpdateReservationPackagesByIdRequest();
    request.setBasketReference(basketRef);

    // Act
    var response = hotelReservationOutPort.updateReservationPackagesByReservationId(request);

    // Assert
    assertNotNull(response);
  }

  @Test
  void updateReservationPackages_PayPendingBasket_Success() {
    // Arrange
    var basketRef = "basketRef";
    var request = new ReservationPackagesRequest();
    request.setBasketReference(basketRef);
    request.setHotelId("HOTELTEST");
    request.setArrival("2024-01-01");
    request.setDeparture("2024-01-02");

    var basketResponse = new BasketDto();
    basketResponse.setStatus(StatusEnum.PAY_PENDING);

    when(basketClient.sendGetBasket(basketRef))
        .thenReturn(Tuples.of(basketResponse, "123"));
    when(updatePackagesRequestOhipMapper.toDto(any(), any()))
        .thenReturn(new ReservationPackagesRequestDto());
    doNothing().when(ohipAdapterClient).sendUpdateReservationPackagesRequest(any());

    // Act
    var response = hotelReservationOutPort.updateReservationPackages(request);

    // Assert
    assertNotNull(response);
    assertEquals(basketRef, response.getBasketReference());
    verify(basketClient).sendGetBasket(basketRef);
    verify(updatePackagesRequestOhipMapper).toDto(request, basketResponse);
    verify(ohipAdapterClient).sendUpdateReservationPackagesRequest(any());
  }

  @Test
  void updateReservationRateCode_Success() {
    // Arrange
    var request = new UpdateRequest();
    request.setBasketReferenceId("basketRef");

    when(basketClient.sendGetBasket(anyString())).thenReturn(Tuples.of(new BasketDto(), "123"));
    doNothing().when(ohipAdapterClient).sendUpdateRateCodeRequest(any());
    when(updateRateCodeRequestOhipMapper.toDto(any(), any())).thenReturn(
        new RatePlanChangeRequestDto());

    // Act
    var response = hotelReservationOutPort.updateReservationRateCode(request);

    // Assert
    assertNotNull(response);
  }

  @Test
  void updateRoomType_Success() {
    // Arrange
    var request = new UpdateRequest();
    request.setBasketReferenceId("basketRef");

    when(updateRoomTypeOhipMapper.toDto(any())).thenReturn(
        new RoomTypeChangeRequestDto());

    // Act
    var response = hotelReservationOutPort.updateRoomType(request);

    // Assert
    assertNotNull(response);
  }

  @Test
  void updateReservationRateCode_ThrowsException() {
    // Arrange
    var basketRef = "basketRef";
    var request = new UpdateRequest();
    request.setBasketReferenceId(basketRef);
    request.setHotelId("HotelTest");
    request.setRateCode("rate");

    when(basketClient.sendGetBasket(anyString())).thenThrow(BasketNotFoundException.class);

    // Act & Assert
    assertThrows(BasketNotFoundException.class,
        () -> hotelReservationOutPort.updateReservationRateCode(request));
  }

  @Test
  void getHotelInformation_Success() {
    // Arrange
    when(ohipAdapterClient.sendGetHotelInformationRequest(anyString())).thenReturn(
        new HotelInfoDto());
    when(hotelInformationOhipMapper.toModel(any())).thenReturn(
        new HotelInformationResponse("EST", "DE"));

    // Act
    var response = hotelReservationOutPort.getHotelInformation("HotelTest");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getHotelInformation_ThrowsException() {
    // Arrange
    when(ohipAdapterClient.sendGetHotelInformationRequest(anyString())).thenThrow(
        HotelReservationOhipException.class);

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationOutPort.getHotelInformation("HotelTest"));
  }

  @Test
  void getAmendSummaryDetails_Success() {
    // Arrange
    when(ohipAdapterClient.getAmendSummaryDetails(any())).thenReturn(
        new AmendSummaryAmountResponse());

    // Act
    var response = hotelReservationOutPort.getAmendSummaryDetails(new AmendSummaryAmountRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void amendEditRoom_Success() {
    // Arrange
    doNothing().when(ohipAdapterClient).sendUpdateReservationAmend(any());
    when(updateReservationRequestOhipMapper.toDto(any())).thenReturn(
        new UpdateReservationsRequestDto());

    // Act
    hotelReservationOutPort.amendEditRoom(new UpdateReservationsRequest());

    // Assert
    verify(ohipAdapterClient).sendUpdateReservationAmend(any());
  }

  @Test
  void confirmAmend_Success() {
    // Arrange
    when(ohipAdapterClient.sendConfirmAmend(any())).thenReturn(
        new ReservationByBasketRefResponseDto());
    when(reservationByBasketRefOhipResponseMapper.toModel(
        any(ReservationByBasketRefResponseDto.class))).thenReturn(
        new ReservationByBasketRefResponse());
    when(confirmAmendRequestOhipMapper.toRequestDto(any())).thenReturn(
        new ConfirmAmendOnReservationsRequestDto());

    // Act
    var response = hotelReservationOutPort.confirmAmend(ConfirmAmendOnReservationsRequest.builder()
        .build());

    // Assert
    assertNotNull(response);
  }

  @Test
  void confirmAmendForSingleCall_Success() {
    // Arrange
    when(ohipAdapterClient.sendConfirmAmendForSingleCall(any())).thenReturn(
        new ReservationByBasketRefResponseDto());
    when(reservationByBasketRefOhipResponseMapper.toModel(
        any(ReservationByBasketRefResponseDto.class))).thenReturn(
        new ReservationByBasketRefResponse());
    when(amendDistributionSingleCallRequestOhipMapper.toDto(any())).thenReturn(
        new AmendDistributionSingleCallRequestDto());

    // Act
    var response = hotelReservationOutPort.confirmAmendForSingleCall(AmendDistributionSingleCallRequest.builder()
        .build());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getRatePlans_Success() {
    String hotelId = "TESTHOTEL";
    // Arrange
    when(ohipAdapterClient.sendGetRatePlansRequest(any(), eq(hotelId))).thenReturn(
        new RatePlansResponseDto());
    when(ratePlansResponseOhipMapper.toModel(any())).thenReturn(
        RatePlansResponse.builder().build());

    // Act
    var response = hotelReservationOutPort.getRatePlans(List.of("rate"), hotelId);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getWbRoomTypes_Success() {
    // Arrange
    when(rulesAdapterProperties.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));

    // Act
    var response = hotelReservationOutPort.getWbRoomTypes();

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationsPackagesByIds_Success() {
    // Arrange
    when(ohipAdapterClient.getReservationsPackagesByIdsRequest(anyString(), anyList(), eq(false))).thenReturn(
        new ReservationsPackagesResponseDto());
    when(reservationsPackagesOhipMapper.toModel(any(ReservationsPackagesResponseDto.class))).thenReturn(
        ReservationsPackagesResponse.builder()
            .roomsSelections(List.of(new RoomsSelectionsByReservation("1",
                List.of(new PackagesSelection("123", 1)))))
            .ratePlanCode("FLEXRATE")
            .roomsSelectionsAmendExtras(null)
            .build());

    // Act
    var response = hotelReservationOutPort.getReservationsPackagesByIds("12", List.of("123"));

    // Assert
    assertNotNull(response);
  }

  @Test
  void deleteRoutingInstructions_success() {
    // Arrange
    final String hotelId = "HOTELTEST";
    final Set<String> reservationIds = Collections.singleton("147");

    doNothing().when(ohipAdapterClient).deleteRoutingInstructions(hotelId, reservationIds);

    // Act
    hotelReservationOutPort.deleteRoutingInstructions(hotelId, reservationIds);

    // Assert
    verify(ohipAdapterClient, times(1)).deleteRoutingInstructions(hotelId, reservationIds);
  }

  @Test
  void testUpdateBookerEmail_success() {
    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBookerEmail(
        mockUpdateBookerEmailRequest()));
  }

  private UpdateBookerEmailRequest mockUpdateBookerEmailRequest() {
    return UpdateBookerEmailRequest.builder()
        .hotelId("TESTHOTEL")
        .reservationIds(List.of("res1", "res3", "res2"))
        .emailAddress("secondEmail@domain.uk").build();
  }

  @Test
  void createMemo_Success() {
    // Arrange
    when(memosOhipMapper.toDto(any(CreateMemoRequest.class))).thenReturn(
        new CreateMemoRequestDto());
    when(ohipAdapterClient.createMemo(any())).thenReturn(new MemosResponseDto());
    when(memosOhipMapper.toModel(any())).thenReturn(new MemosResponse());

    // Act
    var response = hotelReservationOutPort.createMemo(new CreateMemoRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMemos_Success() {
    // Arrange
    when(ohipAdapterClient.getMemos(any(), anySet())).thenReturn(new MemosResponseDto());
    when(memosOhipMapper.toModel(any())).thenReturn(new MemosResponse());

    // Act
    var response = hotelReservationOutPort.getMemos("hotelId",
        Set.of("reservationId1, reservationId2"));

    // Assert
    assertNotNull(response);
  }

  @Test
  void attachProfileToReservations_Success() {
    // Arrange
    when(attachReservationProfileRequestOhipMapper.toDto(any()))
        .thenReturn(new AttachReservationProfileRequestDto());
    doNothing().when(ohipAdapterClient).attachProfileToReservations(any());

    // Act
    hotelReservationOutPort.attachProfileToReservations(AttachReservationProfileRequest.builder()
        .hotelId("TEST")
        .profileId("123")
        .reservationIds(Set.of("123", "456"))
        .build());

    // Assert
    verify(ohipAdapterClient, times(1))
        .attachProfileToReservations(any());
  }

  @Test
  void updateReservationSingleCall_Success() {
    // Arrange
    when(reservationRequestOhipMapper.toUpdateReservationDto(any()))
        .thenReturn(new UpdateReservationSingleCallResponseDto());
    when(confirmReservationResponseOhipMapper.toModel(any()))
        .thenReturn(new ConfirmReservationResponse());
    when(ohipAdapterClient.sendUpdateReservation(any()))
        .thenReturn(new ConfirmReservationResponseDto());


    // Act
    hotelReservationOutPort.updateReservation(new UpdateReservationSingleCallRequest());

    // Assert
    verify(ohipAdapterClient, times(1))
        .sendUpdateReservation(any());
  }

  @Test
  void updateReservationSingleCall_ThrowException() {
    // Arrange
    when(ohipAdapterClient.sendUpdateReservation(any())).thenThrow(exception);

    // Act
    Exception thrownException = assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservation(any()));

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    assertThat(thrownException, notNullValue());
    assertThat(thrownException.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void testAddAttachmentToReservation_success() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = getReservationFileAttachmentRequest();
    when(ohipAdapterTimeoutConfiguredClient.sendAddFileAttachmentToReservationRequest(any()))
        .thenReturn(getPreCheckInResponse("Success", "Attachment added successfully"));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void testAddAttachmentToReservation_error() {
    // Arrange
    ReservationFileAttachmentRequest request = getInvalidBase64Request();

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("File attachment is not a valid base64 string", response.getMessage());
  }

  @Test
  void testAttachmentToReservation_InvalidBase64() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = getReservationFileAttachmentRequest();
    request.setFileAttachment("invalid_base64_string");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("File attachment is not a valid base64 string", response.getMessage());
  }

  @Test
  void testAttachmentToReservation_FileSizeExceedsLimit() {
    // Arrange
    ReservationFileAttachmentRequest request = getLargeFileRequest();

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("File size exceeds the maximum limit of 10MB", response.getMessage());
  }

  @Test
  void testAddAttachmentToReservation_ThrowException() {
    // Arrange
    HotelReservationOhipException expectedException = new HotelReservationOhipException("message",
        "Error while trying to add attachment", new Exception(), 958);
    when(ohipAdapterTimeoutConfiguredClient.sendAddFileAttachmentToReservationRequest(
        any())).thenThrow(
        expectedException);

    // Act
    HotelReservationOhipException thrownException = assertThrows(
        HotelReservationOhipException.class, () -> {
          ohipAdapterTimeoutConfiguredClient.sendAddFileAttachmentToReservationRequest(any());
        });

    // Assert
    verifyNoMoreInteractions(ohipAdapterTimeoutConfiguredClient);
    assertThat(thrownException, notNullValue());
    assertThat(thrownException.getMessage(), is("Error while trying to add attachment"));
  }

  @Test
  void addAttachmentToReservation_SmallNonPdfFile_ShouldReturnPdfValidationFailure()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = getReservationFileAttachmentRequest();
    request.setFileAttachment(
        Base64.getEncoder().encodeToString(new byte[]{0x00, 0x01, 0x02, 0x03}));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File is not a valid PDF", response.getMessage());
  }

  @Test
  void testAttachmentToReservation_InvalidPDF() {
    // Arrange
    ReservationFileAttachmentRequest request = getInvalidPdfRequest();

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("File is not a valid PDF", response.getMessage());
  }

  @Test
  void testSaveReservationPreCheckIn_success() {
    // Arrange
    when(ohipAdapterClient.sendSaveReservationPreCheckInStatus(any()))
        .thenReturn(getPreCheckInResponse("Success", "Pre-checkin status saved successfully"));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(
        mockPreCheckInRequest());

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Pre-checkin status saved successfully", response.getMessage());
  }

  @Test
  void testSaveReservationPreCheckIn_error() {
    // Arrange
    when(ohipAdapterClient.sendSaveReservationPreCheckInStatus(any()))
        .thenReturn(getPreCheckInResponse("Error", "Error in saving pre-checkin status"));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(
        mockPreCheckInRequest());

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving pre-checkin status", response.getMessage());
  }

  @Test
  void testSaveReservationPreCheckIn_ThrowException() {
    // Arrange
    HotelReservationOhipException preCheckInException = new HotelReservationOhipException("message",
        "Error while trying to save pre-checkin status", new Exception(), 958);
    when(ohipAdapterClient.sendSaveReservationPreCheckInStatus(any())).thenThrow(
        preCheckInException);
    when(preCheckInRequestOhipMapper.toDto(any())).thenReturn(mockPreCheckInRequestDto());

    // Act
    PreCheckInRequest request = mockPreCheckInRequest();
    Exception thrownException = assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationOutPort.saveReservationPreCheckIn(request));

    // Assert
    verifyNoMoreInteractions(ohipAdapterClient);
    assertThat(thrownException, notNullValue());
    assertThat(thrownException.getMessage(), is("Error while trying to save pre-checkin status"));
  }

  @Test
  void deleteRegCardAttachment_Success() {
    //Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";
    doNothing().when(ohipAdapterClient).sendDeleteRegCardAttachment(anyString(), anyString());

    //Act
    hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId);

    //Assert
    verify(ohipAdapterClient, times(1))
        .sendDeleteRegCardAttachment(hotelId, reservationId);
  }

  @Test
  void deleteReservationPreCheckIn_Success() {
    //Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";
    doNothing().when(ohipAdapterClient)
        .sendDeleteReservationPreCheckInStatus(anyString(), anyString());

    //Act
    hotelReservationOutPort.deleteReservationPreCheckIn(hotelId, reservationId);

    //Assert
    verify(ohipAdapterClient, times(1))
        .sendDeleteReservationPreCheckInStatus(hotelId, reservationId);
  }

  @Test
  void updateReservationUdf_success() {
    doNothing().when(ohipAdapterClient).updateReservationAlerts(new UpdateReservationAlertsRequest());

    hotelReservationOutPort.updateReservationAlerts(new UpdateReservationAlertsRequest());

    verify(ohipAdapterClient, times(1))
        .updateReservationAlerts(new UpdateReservationAlertsRequest());
  }

    @Test
    void updateUdfc20_shouldCallOhipAdapterClient() {
        // Arrange
        UpdateReservationUdfsRequest request = UpdateReservationUdfsRequest.builder()
                .reservationIds(Set.of("RES1"))
                .hotelId("TEST_HOTEL")
                .udfs(null)
                .build();
        // Act
        hotelReservationOutPort.updateUdfc20(request);
        // Assert
        Mockito.verify(ohipAdapterClient).updateUdfc20(request);
    }

  private PreCheckInRequest mockPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .arrivalTime(LocalDate.of(1996, 7, 13))
        .hotelId("STUAIR")
        .reservationId("123456")
        .language("EN")
        .build();
  }

  private PreCheckInResponse getPreCheckInResponse(String status, String message) {
    return PreCheckInResponse.builder()
        .status(status)
        .message(message)
        .build();
  }

  private static ReservationFileAttachmentRequest getReservationFileAttachmentRequest() throws IOException {
    byte[] validPdfBytes = createValidPdf();
    return ReservationFileAttachmentRequest.builder()
        .fileName("testFile.pdf")
        .reservationId("123456")
        .hotelId("STUAIR")
        .fileAttachment(Base64.getEncoder().encodeToString(validPdfBytes))
        .build();
  }

  private static byte[] createValidPdf() throws IOException {
    try (PDDocument document = new PDDocument()) {
      document.addPage(new PDPage());
      byte[] pdfBytes;
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        document.save(outputStream);
        pdfBytes = outputStream.toByteArray();
      }
      return pdfBytes;
    }
  }

  private static ReservationFileAttachmentRequest getInvalidBase64Request() {
    return ReservationFileAttachmentRequest.builder()
        .fileName("testFile.pdf")
        .reservationId("123456")
        .hotelId("STUAIR")
        .fileAttachment("invalid_base64_string")
        .build();
  }

  private static ReservationFileAttachmentRequest getLargeFileRequest() {
    byte[] largeFile = new byte[11 * 1024 * 1024];
    largeFile[0] = 0x25;
    largeFile[1] = 0x50;
    largeFile[2] = 0x44;
    largeFile[3] = 0x46;
    largeFile[4] = 0x2D;
    return ReservationFileAttachmentRequest.builder()
        .fileName("largeFile.pdf")
        .reservationId("123456")
        .hotelId("STUAIR")
        .fileAttachment(Base64.getEncoder().encodeToString(largeFile))
        .build();
  }

  private static ReservationFileAttachmentRequest getInvalidPdfRequest() {
    return ReservationFileAttachmentRequest.builder()
        .fileName("testFile.pdf")
        .reservationId("123456")
        .hotelId("STUAIR")
        .fileAttachment(Base64.getEncoder().encodeToString("invalid pdf content".getBytes()))
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequest() {
    return UpdateReservationsRequest.builder()
        .reservations(List.of())
        .tempReservations(mockTempRes())
        .build();
  }

  private ReservationByBasketRefResponse mockTempRes() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .roomStay(RoomStayByIdResponse.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .build())
            .reservationId("123")
            .build()))
        .build();
  }

  private UpdateReservationsRequestDto mockUpdateReservationsRequestDto() {
    var updateResReqDto = new UpdateReservationsRequestDto();
    updateResReqDto.setUpdateReservationsRequest(List.of());
    updateResReqDto.setTempReservations(mockTempResDto());

    return updateResReqDto;
  }

  private ReservationByIdDetailsResponse mockReservationByIdResponse() {
    return ReservationByIdDetailsResponse.builder()
        .billing(BillingResponse.builder().lastName("Jhon").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .currencyCode("USD")
        .reservationIdDetailsResponse(mockReservationIdResponse()).build();
  }
  private ReservationIdDetailsResponse mockReservationIdResponse() {
    return ReservationIdDetailsResponse.builder()
        .reservations(ReservationId.builder()
            .reservation(List.of(ReservationDetails.builder()
                .hotelId("TESTID")
                .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.of(2023, 12, 30))
                    .build())
                .build()))
            .build())
        .build();
  }


  private ReservationByBasketRefResponseDto mockTempResDto() {
    ReservationByBasketRefResponseDto tempResDto = new ReservationByBasketRefResponseDto();
    ReservationByIdDto resById = new ReservationByIdDto();
    resById.setReservationId("123");
    RoomStayByIdDto roomStayByIdDto = new RoomStayByIdDto();
    roomStayByIdDto.setChildren(0);
    roomStayByIdDto.setAdults(1);
    resById.setRoomStay(roomStayByIdDto);
    tempResDto.setReservationByIdList(List.of(resById));
    return tempResDto;
  }

  private BookingAllowancesResponse createBookingAllowancesResponse() {
    return BookingAllowancesResponse.builder()
        .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .build()))
        .build();
  }

  private CancellationPoliciesResponseDto createCancellationPoliciesDto() {
    var cancellationPolicies = new CancellationPoliciesResponseDto();

    cancellationPolicies.setText(
        "Cancellations after 1pm on the day of arrival charged 100% of 1 night");
    cancellationPolicies.setTime("2022-04-04T01:00:00+01:00");
    return cancellationPolicies;
  }

  private CancellationPoliciesResponse createCancellationPolicies() {
    var cancellationPolicies = new CancellationPoliciesResponse();

    cancellationPolicies.setText(
        "Cancellations after 1pm on the day of arrival charged 100% of 1 night");
    cancellationPolicies.setTime("2022-04-04T01:00:00+01:00");
    return cancellationPolicies;
  }

  private UpdateReasonForStayRequestDto mockUpdateReasonForStayDto() {
    var updateReasonForStayRequestDto = new UpdateReasonForStayRequestDto();
    updateReasonForStayRequestDto.setHotelId("LONEUS");
    updateReasonForStayRequestDto.setReasonForStay("LEI");
    updateReasonForStayRequestDto.setReservationIds(Collections.singletonList("123456"));
    return updateReasonForStayRequestDto;
  }

  private UpdateReasonForStayRequest createReasonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .reservationIds(Collections.singletonList("123456"))
        .build();
  }

  private CompanyQuestionAndAnswerDetailsRequest mockUpdateCompanyQuestionAndAnswerDetailsRequest() {
    return CompanyQuestionAndAnswerDetailsRequest.builder()
        .hotelId("TestHotelId")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateDiscountRequest mockUpdateDiscountRequest() {
    return UpdateDiscountRequest.builder()
        .discountAmount(BigDecimal.valueOf(10L))
        .currency("USD")
        .hotelId("TestHotelId")
        .reservationIds(List.of("123456", "1234578"))
        .build();
  }

  private UpdateReservationOverrideReasonsRequest mockUpdateReservationOverrideReasonsRequest() {
    return UpdateReservationOverrideReasonsRequest.builder()
        .hotelId("HOTElCODE")
        .basketReference("GBM6919649")
        .reasonCode("ILL")
        .reasonName("Illness")
        .callerName("John Doe")
        .managerName("James Bond")
        .build();
  }

  private UpdateReservationCcAgentIdRequest mockUpdateReservationCcAgentIdRequest() {
    return UpdateReservationCcAgentIdRequest.builder()
        .hotelId("HOTElCODE")
        .ccAgentId("Jane.Doe@wb.com")
        .build();
  }

  private BusinessItemsRequest mockBusinessItemsRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(List.of("123456", "123457"))
        .hotelId("HOTELID")
        .businessItems(BusinessItems.builder()
            .businessNotes("TestingNote is authorized")
            .purchaseOrderNumber("10101010")
            .customReferenceNumber("11010101")
            .businessAllowances(List.of(BusinessAllowance.builder()
                .allowance("ALLOWANCE")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.FALSE)
                .build()))
            .build())
        .build();
  }

  private ReservationsDetailsEnhancedResponse mockReservationDetailsEnhancedResponse() {
    return ReservationsDetailsEnhancedResponse.builder()
        .billing(BillingResponse.builder().lastName("Jhon").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .currencyCode("USD")
        .reservations(Reservations.builder()
            .reservationInfo(List.of(ReservationInfo.builder()
                .hotelId("TESTID")
                .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.of(2023, 12, 30))
                    .build())
                .build()))
            .build())
        .build();
  }

  private ConfirmReservationRequest mockConfirmReservationRequest() {
    return ConfirmReservationRequest.builder()
        .build();
  }

  private ReservationRequest mockCreateReservationRequest() {
    return ReservationRequest.builder().build();
  }

  private List<OhipReservationCreationResponse> mockReservationCreationResponse() {
    return Collections.singletonList(OhipReservationCreationResponse.builder()
        .reservationId("1234")
        .createDateTime("2022-06-19")
        .roomStay(mockRoomStay())
        .build());
  }

  private OhipReservationResponse mockReservationResponse() {
    return OhipReservationResponse.builder()
        .reservations(mockReservationCreationResponse())
        .totalCost(BigDecimal.ONE)
        .build();
  }

  private RoomStay mockRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 5, 5))
        .departureDate(LocalDate.of(2022, 5, 7))
        .adultCount(2)
        .childCount(0)
        .ratePlanCode("AXWS")
        .roomType("SINGLE")
        .build();
  }

  private List<UniqueIDType> getConfirmedReservatinIds() {
    return List.of(createUniqueIDType("147", "Reservation"),
        createUniqueIDType("852", "Confirmation"));
  }

  private SearchBookingsRequest mockSearchBookingsRequest() {
    return SearchBookingsRequest.builder().hotelId("TestHotelId").bookingReference("TestBookingRef")
        .arrivalDate("2023-01-18").bookerLastName("TestBookerLastName").offset(0).limit(20).build();
  }

  public SearchBookingsResponse mockSearchBookingsResponse() {
    return SearchBookingsResponse.builder().bookings(Collections.singletonList(new SearchBooking()))
        .totalPages(1)
        .totalResults(1).hasMore(false).limit(20).offset(20).build();
  }

  private PreCheckInRequestDto mockPreCheckInRequestDto() {
    var request = new PreCheckInRequestDto();
    request.setArrivalTime(LocalDate.of(1996, 7, 13));
    request.setHotelId("STUAIR");
    request.setReservationId("123456");
    request.setLanguage("EN");
    return request;
  }

  @Test
  void testSaveDepositFolios_success() {
    // Arrange
    DepositFoliosRequest request = new DepositFoliosRequest();
    DepositFoliosRequestDto dto = new DepositFoliosRequestDto();

    when(depositFoliosResponseOhipMapper.toDto(request)).thenReturn(dto);

    // Act
    hotelReservationOutPort.saveDepositFolios(request);

    // Assert
    verify(depositFoliosResponseOhipMapper, times(1)).toDto(request);
    verify(ohipAdapterClient, times(1)).saveCharges(dto);
    verifyNoMoreInteractions(depositFoliosResponseOhipMapper, ohipAdapterClient);
  }
}
