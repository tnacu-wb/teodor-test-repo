package uk.co.whitbread.ohip.domain.logic;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils.createCancelReservationRequest;
import static uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils.createCancelReservationRequestWithOverride;
import static uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils.mockRoomRateReservationWithPredefinedRates;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.exceptions.PolicyCodeMismatchException;
import uk.co.whitbread.ohip.domain.exceptions.UnavailableRatesException;
import uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.checkin.out.CharacterUDFs;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AmountType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannelSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendForSingleRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailInfoTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfo;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfoSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeAddresses;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeEmailsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RateType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuests;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomOccupancy;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRate;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.TotalType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequestSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequestSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateRoomRateRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.Customer;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPoliciesResponseSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.Deposits;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.Guarantee;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddressSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoDetailsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSummarySingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ResCashieringType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ResCashieringTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponseSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByIdResponseSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCreationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationEmailNotificationsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationTaxTypeInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationTaxTypeInfoSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStayByIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelReservationOutPort;

@ExtendWith(MockitoExtension.class)
class HotelReservationPortImplTest {

  private static final String RESERVATION_ID = "1268956";

  private HotelReservationInPortImpl hotelReservationInPort;

  @Mock
  private HotelReservationOutPort hotelReservationOutPort;

  @Mock
  private HotelAvailabilityOutPort hotelAvailabilityOutPort;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;


  @BeforeEach
  void init() {
    hotelReservationInPort = new HotelReservationInPortImpl(hotelReservationOutPort,
        hotelAvailabilityOutPort, unleashWrapper);
  }

  @Test
  void createReservation__Status201() {

    var reservationRequest = ReservationTestUtils.mockReservationRequest();

    when(hotelReservationOutPort.createReservation(reservationRequest)).thenReturn(mockReservationResponse());
    when(hotelAvailabilityOutPort.getRatesInfo(any())).thenReturn(
        singletonList(mockAvailabilityRoomPriceBreakdown(123)));

    // Act
    var reservationResponse = hotelReservationInPort.createReservation(reservationRequest);

    // Assert
    assertThat(reservationResponse, notNullValue());
    assertEquals(BigDecimal.ONE, reservationResponse.getTotalCost());
  }

  @Test
  void createReservation__addMissingPromoCode() {

    var reservationRequest = ReservationTestUtils.mockReservationRequest();
    reservationRequest.getReservations().get(0).getRoomRates()
        .setRatePlanCode("STDDIS20");

    var mockReservationResponse = mockReservationResponse();
    mockReservationResponse.getReservations().get(0).getRoomStay().setPromotionCode("ST20RU");
    when(hotelReservationOutPort.createReservation(reservationRequest)).thenReturn(
        mockReservationResponse);
    when(hotelAvailabilityOutPort.getRatesInfo(any())).thenReturn(
        singletonList(mockAvailabilityRoomPriceBreakdown(123)));

    // Act
    var reservationResponse = hotelReservationInPort.createReservation(reservationRequest);

    // Assert
    assertThat(reservationResponse, notNullValue());
    assertEquals("ST20RU", reservationResponse.getReservations().get(0)
        .getRoomStay().getPromotionCode());
  }

  @Test
  void createReservationForAmend__Status201() {

    var reservationRequest = ReservationTestUtils.mockReservationRequest();
    when(hotelReservationOutPort.getWbRoomTypes()).thenReturn(
        List.of("DB", "SB", "FAM", "DIS", "TWIN"));
    when(hotelReservationOutPort.createReservation(reservationRequest)).thenReturn(
        mockReservationResponse());
    when(hotelAvailabilityOutPort.getRatesInfo(any())).thenReturn(
        singletonList(mockAvailabilityRoomPriceBreakdown(123)));
    when(hotelReservationOutPort.getSubstitutionListFromRule(anyString(), anyInt(), anyInt(),
        anyString())).thenReturn(List.of(RoomSubstitution.builder().type("DBLWIN").build(),
        RoomSubstitution.builder().type("DOUBLE").build(),
        RoomSubstitution.builder().type("FMTRPL").build(),
        RoomSubstitution.builder().type("FMQUAD").build()));
    when(hotelAvailabilityOutPort.getRoomTypesFromHotelInventory(anyString(), anyString(),
        anyString(), anyInt())).thenReturn(List.of("DOUBLE"));

    // Act
    var reservationResponse = hotelReservationInPort.processCreateReservation(reservationRequest);

    // Assert
    assertThat(reservationResponse, notNullValue());
    assertEquals(BigDecimal.ONE, reservationResponse.getTotalCost());
  }

  @Test
  void updateReservationsToPayOnArrival__success() {
    //Arrange
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean())).thenReturn(
        mockReservationByBasketRefResponse());

    //Act
    hotelReservationInPort.updateReservationsToPayOnArrival("LONEUS", Set.of("12345"));

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservationsToPayOnArrival(any());
  }


  @Test
  void updateReservationsWithExternalReference__success() {

    //Arrange
    var hotelId = "hotelId";
    var reservationId = "1244";
    var externalReference = "31243";

    //Act
    hotelReservationInPort.updateReservationsWithExternalRef(hotelId, Set.of(reservationId), externalReference);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservationsWithExternalRef(any(), any(), any());
  }

  @Test
  void createReservation__UnavailableRates() {

    // Arrange
    var reservationRequest = ReservationTestUtils.mockReservationRequest();

    when(hotelAvailabilityOutPort.getRatesInfo(any())).thenReturn(singletonList(mockAvailabilityRoomPriceBreakdown(0)));

    // Act
    assertThrows(UnavailableRatesException.class, () ->
        hotelReservationInPort.createReservation(reservationRequest));
  }


  @Test
  void createReservation__throwPolicyCodeException() {

    // Arrange
    when(hotelAvailabilityOutPort.getRatesInfo(any())).thenReturn(
        singletonList(mockAvailabilityRoomPriceBreakdown(123)));

    var reservationRequest = ReservationTestUtils.mockReservationRequest();

    DepositPolicies depositPolicy1 = DepositPolicies.builder().policyCode("OA").build();
    DepositPolicies depositPolicy2 = DepositPolicies.builder().policyCode("D1").build();

    ReservationResponse reservationResponse = mockReservationResponse();
    reservationResponse.getReservations().get(0).setDepositPolicies(List.of(depositPolicy1, depositPolicy2));

    when(hotelReservationOutPort.createReservation(reservationRequest)).thenReturn(reservationResponse);

    // Act
    assertThrows(PolicyCodeMismatchException.class, () ->
        hotelReservationInPort.createReservation(reservationRequest));
  }

  private AvailabilityRoomPriceBreakdown mockAvailabilityRoomPriceBreakdown(int price) {
    return AvailabilityRoomPriceBreakdown.builder()
        .totalNetAmount(BigDecimal.valueOf(price))
        .dailyPrices(singletonList(AvailabilityDailyPrice.builder()
            .netPrice(BigDecimal.valueOf(price))
            .build()))
        .build();
  }

  @Test
  void confirmReservation__success() {

    var reservationRequest = OhipTestUtils.createConfirmReservationRequest();

    when(hotelReservationInPort.confirmReservation(reservationRequest)).thenReturn(
        OhipTestUtils.mockConfirmReservationResponse());

    // Act
    var reservationResponse = hotelReservationOutPort.confirmReservation(reservationRequest);

    // Assert
    assertThat(reservationResponse, notNullValue());
  }


  @Test
  void getReservationsByExternalReferenceIds__Status200() {
    // Arange
    final String hotelId = "TKINPT";
    final String externalReferenceId = "1234";
    final int limit = 20;
    final int offset = 0;
    when(hotelReservationOutPort.getReservationsByExternalReferenceIds("TKINPT",
        singletonList(externalReferenceId), limit, offset)).thenReturn(
        OhipTestUtils.createReservationsDetailsResponse(hotelId, externalReferenceId));

    // Act
    var reservationsDetailsResponse = hotelReservationInPort.getReservationsByExternalReferenceIds(
        hotelId, singletonList(externalReferenceId), limit, offset);

    // Assert
    assertNotNull(reservationsDetailsResponse);
    assertEquals(2, reservationsDetailsResponse.getReservations().getReservationInfo().size());
    assertEquals(externalReferenceId,
        reservationsDetailsResponse.getReservations().getReservationInfo().get(0)
            .getExternalReferences().get(0).getId());
    assertEquals(externalReferenceId,
        reservationsDetailsResponse.getReservations().getReservationInfo().get(1)
            .getExternalReferences().get(0).getId());
  }

  @Test
  void getReservationsByReservationId__Status200() {
    // Arange
    final String hotelId = "TKINPT";
    final String reservationId = "123456";
    when(hotelReservationOutPort.getReservationsByReservationId("TKINPT",
        reservationId)).thenReturn(
        OhipTestUtils.createReservationIdDetailResponse(hotelId, reservationId));

    // Act
    var reservationsDetailsResponse = hotelReservationInPort.getReservationsByReservationId(
        hotelId,reservationId);

    // Assert
    assertNotNull(reservationsDetailsResponse);
    assertEquals(2, reservationsDetailsResponse.getReservationIdResponse().getReservations().getReservation().size());
    assertEquals(reservationId,
        reservationsDetailsResponse.getReservationIdResponse().getReservations().getReservation().get(0).getReservationIdList().get(0).getId());
    assertEquals(reservationId,
        reservationsDetailsResponse.getReservationIdResponse().getReservations().getReservation().get(1)
            .getReservationIdList().get(0).getId());
  }
  @Test
  void getReservationsByIds__Success() {

    //Arrange
    final String hotelId = "TKINPT";
    final Set<String> reservationId = Collections.singleton("12345");
    when(hotelReservationOutPort.getReservationsByIds(hotelId, reservationId, false, true, false)).thenReturn(mock(
        ReservationByBasketRefResponse.class));

    //Act
    var reservationByBasketRefResponse = hotelReservationInPort.getReservationsByIds(hotelId,
        reservationId, false, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
  }

  @Test
  void getReservationsByIds_operaUiRes_Success() {

    //Arrange
    final String hotelId = "TKINPT";
    final Set<String> reservationId = Collections.singleton("12345");

    DepositPolicies dp1 = createDepositPolicies(new BigDecimal(100),
            new BigDecimal(200), "OA");
    DepositPolicies dp2 = createDepositPolicies(new BigDecimal(300),
            new BigDecimal(500), "OA");

    ReservationById res1 = createReservations(Arrays.asList(dp1, dp2));
    res1.setReservationId("12345");
    res1.setGuarantee(Guarantee.builder().guaranteeCode("DRV").build());
    var reservationByBasketRefResponseReq = ReservationByBasketRefResponse.builder()
            .reservationByIdList(List.of(res1))
            .build();

    when(hotelReservationOutPort.getReservationsByIds(hotelId, reservationId, false, true, true))
            .thenReturn(reservationByBasketRefResponseReq);

    //Act
    var reservationByBasketRefResponse = hotelReservationInPort.getReservationsByIds(hotelId,
            reservationId, false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
  }

  @Test
  void getReservationsPackagesByIds__Success() {

    //Arrange
    final String hotelId = "HOTELTEST";
    final Set<String> reservationId = Collections.singleton("12345");
    when(hotelReservationOutPort.getReservationsPackagesByIds(hotelId, reservationId))
        .thenReturn(mockReservationsPackagesResponse());

    //Act
    var reservationsPackagesByReservationsIds = hotelReservationInPort
        .getReservationsPackagesByReservationsIds(hotelId,
            reservationId);

    //Assert
    assertNotNull(reservationsPackagesByReservationsIds);
    assertNotNull(reservationsPackagesByReservationsIds.getRoomsSelections());
    assertNotNull(reservationsPackagesByReservationsIds.getRoomsSelections().get(0));
    assertNotNull(
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection());
    assertNotNull(
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0));
    assertEquals(2,
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0).getNoSelections());
    assertEquals("TEST",
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0).getId());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void getReservationsPackagesRateInclusiveMealsByIds__Success() {

    //Arrange
    final String hotelId = "HOTELTEST";
    final Set<String> reservationId = Collections.singleton("12345");
    when(hotelReservationOutPort.getReservationsPackagesMealInclusiveRateByReservationsIds(hotelId, reservationId))
        .thenReturn(mockReservationsPackagesResponse());

    //Act
    var reservationsPackagesByReservationsIds = hotelReservationInPort
        .getReservationsPackagesMealInclusiveRateByReservationsIds(hotelId,
            reservationId);

    //Assert
    assertNotNull(reservationsPackagesByReservationsIds);
    assertNotNull(reservationsPackagesByReservationsIds.getRoomsSelections());
    assertNotNull(reservationsPackagesByReservationsIds.getRoomsSelections().get(0));
    assertNotNull(
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection());
    assertNotNull(
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0));
    assertEquals(2,
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0).getNoSelections());
    assertEquals("TEST",
        reservationsPackagesByReservationsIds.getRoomsSelections().get(0).getPackagesSelection()
            .get(0).getId());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void createReservationGuest_success() {
    //Arrange
    final String hotelId = "TKINPT";
    final Set<String> reservationId = Collections.singleton("12345");

    when(hotelReservationOutPort.createReservationGuest(any()))
        .thenReturn(new ReservationGuestResponse(hotelId, reservationId.stream().toList()));

    //Act
    var response = hotelReservationInPort.createReservationGuest(createValidReservationGuestRequest());

    //Assert
    assertThat(response, notNullValue());

    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void getReservationsByIds__PolicyCodeOA__Success() {
    //Arrange
    DepositPolicies dp1 = createDepositPolicies(new BigDecimal(100),
        BigDecimal.ZERO, "OA");
    DepositPolicies dp2 = createDepositPolicies(new BigDecimal(300),
        BigDecimal.ZERO, "OA");
    DepositPolicies dp3 = createDepositPolicies(new BigDecimal(300),
        BigDecimal.ZERO, "OA");
    DepositPolicies dp4 = createDepositPolicies(new BigDecimal(300),
        BigDecimal.ZERO, "OA");
    ReservationById res1 = createReservations(Arrays.asList(dp1, dp2));
    ReservationById res2 = createReservations(Arrays.asList(dp3, dp4));

    var totalAmount = new BigDecimal(1000);

    when(
        hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(createReservationByBasketRefResponse(Arrays.asList(res1, res2), totalAmount,
            null, null, null));

    //Act
    var response = hotelReservationInPort.getReservationsByIds(
        "hotelId", Set.of("24335"), false, false, false);

    //Assert
    assertThat(response, notNullValue());
    assertNull(response.getBalanceOutstanding());
    assertNull(response.getNewTotal());
    assertNull(response.getPreviousTotal());
    assertEquals(BigDecimal.valueOf(1000), response.getTotalCost());
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getStartDate());
    assertEquals("OA", response.getPolicyCode());
  }

  @Test
  void getReservationsByIds__PolicyCodeD1__Success() {
    //Arrange
    DepositPolicies dp1 = createDepositPolicies(null,
        new BigDecimal(200), "D1");
    DepositPolicies dp2 = createDepositPolicies(new BigDecimal(300),
        new BigDecimal(500), "D1");
    DepositPolicies dp3 = createDepositPolicies(new BigDecimal(300),
        new BigDecimal(100), "D1");
    DepositPolicies dp4 = null;
    ReservationById res1 = createReservations(Arrays.asList(dp1, dp2));
    ReservationById res2 = createReservations(Arrays.asList(dp3, dp4));

    var balanceOutstanding = new BigDecimal(600);
    var totalAmount = new BigDecimal(800);
    var newTotal = new BigDecimal(800);
    var previousTotal = new BigDecimal(800);
    when(
        hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(createReservationByBasketRefResponse(Arrays.asList(res1, res2),
            totalAmount, balanceOutstanding, newTotal, previousTotal));

    //Act
    var response = hotelReservationInPort.getReservationsByIds("hotelId",
            Set.of("13242"), false,false,false);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(BigDecimal.valueOf(600), response.getBalanceOutstanding());
    assertEquals(BigDecimal.valueOf(800), response.getNewTotal());
    assertEquals(BigDecimal.valueOf(800), response.getPreviousTotal());
    assertThat(response.getTotalCost(), notNullValue());
    assertEquals("D1", response.getPolicyCode());
  }

  @Test
  void getReservationsByIds__PolicyCodeMismatch__Error() {
    //Arrange
    DepositPolicies dp1 = createDepositPolicies(new BigDecimal(100),
        new BigDecimal(200), "OA");
    DepositPolicies dp2 = createDepositPolicies(new BigDecimal(300),
        new BigDecimal(500), "OA");
    DepositPolicies dp3 = createDepositPolicies(new BigDecimal(300),
        new BigDecimal(100), "D1");
    DepositPolicies dp4 = createDepositPolicies(new BigDecimal(300),
        new BigDecimal(150), "OA");
    ReservationById res1 = createReservations(Arrays.asList(dp1, dp2));
    ReservationById res2 = createReservations(Arrays.asList(dp3, dp4));
    FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);
    when(featureFlag.getMobileAcceptsOtaBooking()).thenReturn(new FeatureFlag.Feature());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(
        hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(createReservationByBasketRefResponse(Arrays.asList(res1, res2), null,
            null, null, null));

    //Assert
    assertThrows(PolicyCodeMismatchException.class, () ->
        hotelReservationInPort.getReservationsByIds("hotelId", Set.of("343545"), false, false, false)
    );
  }

  @Test
  void saveReservation__Success() {
    //Arrange
    HotelReservationInPort hotelReservationInPort = mock(HotelReservationInPort.class);

    //Act
    hotelReservationInPort.updateReservationPackages(createSavePackagesRequest());

    //Assert
    verify(hotelReservationInPort, times(1)).updateReservationPackages(createSavePackagesRequest());
  }

  @Test
  void cancelReservation_WithoutOverride__Success() {
    //Arrange
    final List<String> cancellationIds = Collections.singletonList("98876");
    when(hotelReservationOutPort.cancelReservation(any()))
        .thenReturn(new CancelReservationResponse(cancellationIds, null));

    //Act
    var response = hotelReservationInPort.cancelReservation(createCancelReservationRequest());

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void cancelReservation_WithOverride__Success() {
    //Arrange
    final List<String> cancellationIds = Collections.singletonList("98876");
    when(hotelReservationOutPort.cancelReservation(any()))
        .thenReturn(new CancelReservationResponse(cancellationIds, null));

    //Act
    var response = hotelReservationInPort.cancelReservation(
        createCancelReservationRequestWithOverride());

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void getCancelInformation__Success() {
    //Arrange

    when(hotelReservationOutPort.getCancelInformation(anyString(), anySet(), any()))
        .thenReturn(new CancelInformationResponse(true));

    //Act
    var response = hotelReservationInPort.getCancelInformation(anyString(), anySet(), any());

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getIsCancellable());
    assertEquals(true, response.getIsCancellable());
  }

  @Test
  void changeReservationRatePlan__Success() {
    //Arrange
    HotelReservationOutPort hotelReservationOutPort = mock(HotelReservationOutPort.class);

    //Act
    hotelReservationInPort.changeReservationRatePlan(mockRatePlanChangeRequest());

    //Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void changeReservationRoomType__Success() {
    //Arrange
    HotelReservationOutPort hotelReservationOutPort = mock(HotelReservationOutPort.class);

    //Act
    hotelReservationInPort.changeReservationRoomType(mockRoomTypeChangeRequest());

    //Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void updateReservationPackages__Success() {
    //Arrange
    ReservationPackagesRequest rpr = createReservationPackagesRequest();

    //Actual
    hotelReservationInPort.updateReservationPackages(rpr);

    //Assert
    verify(hotelReservationOutPort, times(0)).updateReservationPackages(rpr);

  }

  @Test
  void updateReservationPackages__EmptyReservationsIds() {
    //Arrange
    ReservationPackagesRequest rpr = ReservationPackagesRequest.builder()
        .reservationsId(Collections.emptyList())
        .build();

    //Actual
    hotelReservationInPort.updateReservationPackages(rpr);

    //Assert
    verify(hotelReservationOutPort, times(0)).updateReservationPackages(rpr);

  }

  @Test
  void updateReservationPackages__NullReservationsId() {
    //Arrange
    ReservationPackagesRequest rpr = ReservationPackagesRequest.builder()
        .reservationsId(null)
        .build();

    //Actual
    hotelReservationInPort.updateReservationPackages(rpr);

    //Assert
    verify(hotelReservationOutPort, times(0)).updateReservationPackages(rpr);

  }


  @Test
  void updateReservationPackages__NullRoomSelections() {
    //Arrange
    ReservationPackagesRequest rpr = ReservationPackagesRequest.builder()
        .reservationsId(List.of("1234"))
        .hotelId("EDIPAR")
        .arrival("2022-12-25")
        .departure("2022-12-29")
        .roomsSelections(null)
        .previousRoomsSelections(new ArrayList<>())
        .build();

    //Actual
    hotelReservationInPort.updateReservationPackages(rpr);

    //Assert
    verify(hotelReservationOutPort, times(0)).updateReservationPackages(rpr);

  }

  @Test
  void updateReservationPackages__NullPreviousRoomSelections() {
    //Arrange
    ReservationPackagesRequest rpr = ReservationPackagesRequest.builder()
        .reservationsId(List.of("1234"))
        .hotelId("EDIPAR")
        .arrival("2022-12-25")
        .departure("2022-12-29")
        .roomsSelections(new ArrayList<>())
        .previousRoomsSelections(null)
        .build();

    //Actual
    hotelReservationInPort.updateReservationPackages(rpr);

    //Assert
    verify(hotelReservationOutPort, times(0)).updateReservationPackages(rpr);

  }

  @Test
  void updateDiscount__Success() {
    //Arrange
    UpdateDiscountRequest discountRequest = createUpdateDiscountRequest();
    doNothing().when(hotelReservationOutPort).updateDiscount(any());

    //Act
    hotelReservationInPort.updateDiscount(discountRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateDiscount(discountRequest);
  }

  @Test
  void updateBusinessItems__Success() {
    //Arrange
    var businessItemsRequest = createBusinessItemsRequest();
    doNothing().when(hotelReservationOutPort).updateBusinessItems(any());

    //Act
    hotelReservationInPort.updateBusinessItems(businessItemsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateBusinessItems(businessItemsRequest);
  }

  @Test
  void updateCustomReferenceNumber__Success() {
    //Arrange
    var customReferenceNumberRequest = UpdateCustomReferenceNumberRequest
        .builder()
        .hotelId("FRESUD")
        .reservationIds(Set.of("12345"))
        .customReferenceNumber("amendedReference")
        .build();
    doNothing().when(hotelReservationOutPort).updateCustomReferenceNumber(any());

    //Act
    hotelReservationInPort.updateCustomReferenceNumber(customReferenceNumberRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateCustomReferenceNumber(
        customReferenceNumberRequest);
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__Success() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest = createCompanyQuestionAndAnswerDetailsRequest();
    doNothing().when(hotelReservationOutPort).updateCompanyQuestionAndAnswerDetails(any());

    //Act
    hotelReservationInPort.updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest);
  }
  @Test
  void updateReservationDistr_Success() {
    //Arrange
    var paymentRequest = OhipTestUtils.createConfirmReservationRequest();
    var businessItemsRequest = createBusinessItemsRequest();
    var guestDetailsRequest = createValidReservationGuestRequest();
    var specialRequest = createSpecialRequests();
    var packageRequest = createSavePackagesRequest();
    when(hotelReservationInPort.updateReservationSingleCall(businessItemsRequest, specialRequest,
        guestDetailsRequest, packageRequest, paymentRequest))
        .thenReturn(OhipTestUtils.mockConfirmReservationResponse());
    //Act
    var response = hotelReservationInPort.updateReservationSingleCall(businessItemsRequest,
        specialRequest, guestDetailsRequest, packageRequest, paymentRequest);
    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void updateSpecialRequests__Success() {
    //Arrange
    var specialRequests = createSpecialRequests();
    doNothing().when(hotelReservationOutPort).updateSpecialRequests(any());

    //Act
    hotelReservationInPort.updateSpecialRequests(specialRequests);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateSpecialRequests(specialRequests);
  }

  @Test
  void updateCompanyQuestionAndAnswerRequests__Success() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest =
        CompanyQuestionAndAnswerDetailsRequest.builder().hotelId("LONEUS")
        .reservationIds(Set.of("123456"))
            .companyQuestionAndAnswerDetails(new CompanyQuestionAndAnswerDetails()).build();

    doNothing().when(hotelReservationOutPort).updateCompanyQuestionAndAnswerDetails(any());

    //Act
    hotelReservationInPort.updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).
        updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest);
  }

  private SpecialRequests createSpecialRequests(){
    return SpecialRequests.builder()
        .hotelId("MANOLD")
        .specialRequests(List.of("SING"))
        .reservationIds(List.of("1234"))
        .build();
  }

  @Test
  void getReservationsByExternalReferenceId__Status() {
    // Arange
    final String hotelId = "TKINPT";
    final String externalReferenceId = "1234";
    final String currency = "GBP";
    final String policyCode = "OA";

    when(hotelReservationOutPort.getReservationsByExternalRefId(externalReferenceId))
        .thenReturn(OhipTestUtils.createReservationDetailsEnhancedResponse(hotelId, externalReferenceId));

    // Act
    var reservationDetailsEnhanced = hotelReservationInPort
            .getReservationsByExternalRefId(externalReferenceId);

    // Assert
    assertNotNull(reservationDetailsEnhanced);
    List<ReservationInfo> reservationInfo =
        reservationDetailsEnhanced.getReservationsDetailsResponse().getReservations().getReservationInfo();
    assertEquals(2, reservationInfo.size());
    assertEquals(externalReferenceId, reservationInfo.get(0).getExternalReferences().get(0).getId());
    assertEquals(externalReferenceId, reservationInfo.get(1).getExternalReferences().get(0).getId());
    assertNotNull(reservationDetailsEnhanced.getBilling());
    assertNotNull(reservationDetailsEnhanced.getBilling().getLastName());
    assertNotNull(reservationDetailsEnhanced.getBilling().getEmail());
    assertNotNull(reservationDetailsEnhanced.getTotalCost());
    assertEquals(currency, reservationDetailsEnhanced.getCurrencyCode());
    assertEquals(policyCode, reservationDetailsEnhanced.getPolicyCode());

  }

  @Test
  void getReservationsByReservationId__Status() {
    // Arange
    final String hotelId = "TKINPT";
    final String reservationId = "123456";

    when(hotelReservationOutPort.getReservationsByReservationId(hotelId,reservationId))
        .thenReturn(OhipTestUtils.createReservationByIdDetailsResponse(hotelId, reservationId));

    // Act
    var reservationDetailsEnhanced = hotelReservationInPort
        .getReservationsByReservationId(hotelId,reservationId);

    // Assert
    assertNotNull(reservationDetailsEnhanced);
    List<ReservationDetails> reservationDetails =
        reservationDetailsEnhanced.getReservationIdResponse().getReservations().getReservation();
    assertEquals(2, reservationDetails.size());
    assertEquals(reservationId, reservationDetails.get(0).getReservationIdList().get(0).getId());
    assertEquals(reservationId, reservationDetails.get(1).getReservationIdList().get(0).getId());

  }
  @Test
  void updateReasonForStay__Success() {

    //Arrange
    var updateReasonForStayRequest = mockUpdateReasonForStayRequest();
    when(hotelReservationOutPort.updateReasonForStay(any())).thenReturn(mockUpdateReasonForStayResponse());

    //Act
    var updateReasonForStayResponse = hotelReservationInPort.updateReasonForStay(updateReasonForStayRequest);

    //Assert
    assertNotNull(updateReasonForStayResponse);
    assertEquals("LONEUS", updateReasonForStayResponse.getHotelId());
    assertEquals("123456", updateReasonForStayResponse.getReservationIds().get(0));
    verify(hotelReservationOutPort, times(1)).updateReasonForStay(updateReasonForStayRequest);
  }

  @Test
  void updateReservationOverrideReasons__Success() {
    //Arrange
    var updateReservationOverrideReasonsRequest =
        mockUpdateReservationOverrideReasonsRequest();
    doNothing().when(hotelReservationOutPort).updateReservationOverrideReasons(any());

    //Act
    hotelReservationInPort.updateReservationOverrideReasons(updateReservationOverrideReasonsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservationOverrideReasons(updateReservationOverrideReasonsRequest);
  }

  @Test
  void updateReservationPayeeInfo__Success() {
    //Arrange
    doNothing().when(hotelReservationOutPort).updateRoutingInstructionsWithPayeeInfo(any(), any());

    //Act
    hotelReservationInPort.updateRoutingInstructionsWithPayeeInfo("hotelId", Set.of("12345"));

    //Assert
    verify(hotelReservationOutPort, times(1))
            .updateRoutingInstructionsWithPayeeInfo("hotelId", Set.of("12345"));
  }

  @Test
  void updateReservationCcAgentId__Success() {
    //Arrange
    var updateReservationCcAgentIdRequest =
        mockUpdateReservationCcAgentIdRequest();
    doNothing().when(hotelReservationOutPort).updateReservationCcAgentId(any(), anyBoolean());

    //Act
    hotelReservationInPort.updateReservationCcAgentId(updateReservationCcAgentIdRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservationCcAgentId(updateReservationCcAgentIdRequest, false);
  }

  @Test
  void searchReservationsWithoutWildcard__Success() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder().bookingReference("GBH0293230").build();
    ArgumentCaptor<BookingSearchCriteria> captor = ArgumentCaptor.forClass(BookingSearchCriteria.class);

    //Act
    hotelReservationInPort.searchBookings(bookingSearchCriteria);

    //Assert
    verify(hotelReservationOutPort, times(1)).searchBookings(captor.capture());

    assertEquals("GBH0293230", captor.getValue().getBookingReference());
  }

  @Test
  void searchReservationsWithWildcard__Success() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder().bookingReference("AQNR456520").build();
    ArgumentCaptor<BookingSearchCriteria> captor = ArgumentCaptor.forClass(BookingSearchCriteria.class);

    //Act
    hotelReservationInPort.searchBookings(bookingSearchCriteria);

    //Assert
    verify(hotelReservationOutPort, times(1)).searchBookings(captor.capture());

    assertEquals("AQNR456520-%", captor.getValue().getBookingReference());
  }

  @Test
  void testGetDepositsByResId_success() {
    String hotelID = "DAHMME";
    String resNo = "12345678";
    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(Arrays.asList(Deposits.builder().paymentReference("3CPREFERENCE")
            .build()));
    // Arrange
    when(hotelReservationOutPort.getDepositsForReservationId(hotelID, resNo))
            .thenReturn(depositsResponse);

    // Act
    var response = hotelReservationInPort.getDepositsForReservationId(hotelID, resNo);

    // Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
    assertThat(response.getDeposits().get(0).getPaymentReference(), is("3CPREFERENCE"));
  }

  @Test
  void getCancellationPolicies_success() {
    String hotelId = "LONEUS";
    Set<String> reservationIds = Collections.singleton("147");

    // Arrange
    when(hotelReservationOutPort.getCancellationPolicies(reservationIds, hotelId, null, null, null))
            .thenReturn(createCancellationPolicies());

    // Act
    var response = hotelReservationInPort.getCancellationPolicies
            (reservationIds, hotelId, null, null);

    // Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
    assertThat(response.getText(), is("Cancellations after 1pm on the day of arrival charged 100% of 1 night"));
    assertThat(response.getTime(), is("2022-03-04T01:00:00+00:00"));
  }

  @Test
  void testGetMarketingPreferences_success() {
    String hotelID = "DAHMME";
    String resNo = "12345678";

    var marketingPreferences = MarketingPreferencesResponse.builder()
        .customer(Customer.builder().title("Mr").firstName("Sarah").lastName("Smith").country("GB").build())
        .contactValue("sarah.smith.@withbread.com").optIn(true).build();

    // Arrange
    when(hotelReservationOutPort.getMarketingPreferences(hotelID, resNo)).thenReturn(marketingPreferences);

    // Act
    var response = hotelReservationInPort.getMarketingPreferences(hotelID, resNo);

    // Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
    assertEquals("sarah.smith.@withbread.com", response.getContactValue());
    assertTrue(response.getOptIn());
  }

  @Test
  void copyReservations_success() {
    var copyReservationsRequest = createCopyReservationsRequest();
    var copyReservationsResponse = createCopyReservationsResponse();
    // Arrange
    when(hotelReservationOutPort.copyReservations(any())).thenReturn(copyReservationsResponse);

    // Act
    var response = hotelReservationInPort.copyReservations(copyReservationsRequest);

    // Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
    assertThat(response.getReservations(), is(copyReservationsResponse.getReservations()));
  }

  @Test
  void getBookingAllowances_success() {
    String hotelId = "LONEUS";
    String reservationId = "123456";

    // Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.getBookingAllowances(hotelId, reservationId, List.of()));
  }

  @Test
  void deleteReservation_success() {
    String reservationId = "1124025";
    String hotelId = "HOTELTEST";
    //Arrange
    doNothing().when(hotelReservationOutPort).deleteReservation(anyString(), anyString());

    //Act
    hotelReservationInPort.deleteReservation(hotelId, reservationId);

    //Assert
    verify(hotelReservationOutPort, times(1)).deleteReservation(anyString(), anyString());
  }

  @Test
  void updateEditRoom__Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
            .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void updateEditRoom__nullReservationEmailNotifications() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getTempReservations().getReservationByIdList().get(0).setReservationEmailNotifications(null);
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void updateEditRoom__paymentCardNotNull() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getTempReservations().getReservationByIdList().get(0).setPaymentCard(new ReservationPaymentCardTypeSingleCall());
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void updateEditRoom__depositPoliciesSingleCallNotNull() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getTempReservations().getReservationByIdList().get(0).setDepositPolicies(List.of
        (new DepositPoliciesResponseSingleCall(new CurrencyAmountTypeSingleCall(), new CurrencyAmountTypeSingleCall(), "")));
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void updateEditRoom__addressNotNull() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getTempReservations().getReservationByIdList().get(0).getReservationGuestList().get(0).setAddress(new GuestAddressSingleCall());
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"ALTERNATE", "INCOGNITO", "EXTERNAL", "PHONETIC", "default"})
  void updateEditRoom__NameType(String nameType) {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getReservations().get(0).getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer()
        .getPersonName().get(0).setNameType(nameType);
    var changeReservation = List.of(mockChangeReservation());

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(changeReservation);
    //Act
    List<ChangeReservation> changeReservations = hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void confirmAmendForSingleCall__success() {
    List<UpdateReservationsRequestSingleCall> editUpdateRequest = List.of(
            mockUpdateReservationsRequestRequestSingleCall());
    var stayUpdateRequest = mockUpdateReservationsRequestRequestSingleCall();
    stayUpdateRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();
    UpdateReservationPackagesByIdRequest updatePackageRequest = UpdateReservationPackagesByIdRequest.builder()
            .hotelId("TEST").basketReference("TEST").arrival("2023-04-24").departure("2023-04-24")
            .roomsSelections(List.of(new RoomsSelectionsByReservationId()))
            .previousRoomsSelections(List.of(new RoomsSelectionsByReservationId())).build();
    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(editUpdateRequest)
            .stayDateUpdateRequest(stayUpdateRequest).bookingAllowancesRequest(businessItemsRequest)
            .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
            .updateReservationPackagesByIdRequest(updatePackageRequest).build();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
            any(),
            anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
            .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);
    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
            .thenReturn(List.of(mockChangeReservation()));
    when(hotelReservationOutPort.confirmAmendSingleCall(
            any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
            reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertNotNull(build.getEditRoomRequest());
  }

  @Test
  void confirmAmendForSingleCall__AlteratePackageSuccess() {
    List<UpdateReservationsRequestSingleCall> editUpdateRequest = List.of(
        mockUpdateReservationsRequestRequestSingleCall());
    var stayUpdateRequest = mockUpdateReservationsRequestRequestSingleCall();
    stayUpdateRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();

    UpdateReservationPackagesByIdRequest updatePackageRequest = UpdateReservationPackagesByIdRequest.builder()
        .hotelId("TESTSS").basketReference("TEST").arrival("2023-04-24").departure("2023-04-24")
        .roomsSelections(List.of(new RoomsSelectionsByReservationId()))
        .previousRoomsSelections(List.of(new RoomsSelectionsByReservationId())).build();

    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(editUpdateRequest)
        .stayDateUpdateRequest(stayUpdateRequest).bookingAllowancesRequest(businessItemsRequest)
        .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
        .updateReservationPackagesByIdRequest(updatePackageRequest).build();

    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
        any(),
        anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
        .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(List.of(mockChangeReservation()));
    when(hotelReservationOutPort.confirmAmendSingleCall(
        any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
        reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertNotNull(build.getEditRoomRequest());
  }

  @Test
  void confirmAmendForSingleCall__nullReservationPackagesRequest() {
    List<UpdateReservationsRequestSingleCall> editUpdateRequest = List.of(
        mockUpdateReservationsRequestRequestSingleCall());
    var stayUpdateRequest = mockUpdateReservationsRequestRequestSingleCall();
    stayUpdateRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();
    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(editUpdateRequest)
        .stayDateUpdateRequest(stayUpdateRequest).bookingAllowancesRequest(businessItemsRequest)
        .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
        .updateReservationPackagesByIdRequest(null).build();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
        any(),
        anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
        .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(List.of(mockChangeReservation()));
    when(hotelReservationOutPort.confirmAmendSingleCall(
        any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
        reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertNotNull(build.getEditRoomRequest());
  }

  @Test
  void confirmAmendForSingleCall__EmptyEditRoomRequest() {
    var stayUpdateRequest = mockUpdateReservationsRequestRequestSingleCall();
    stayUpdateRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();
    UpdateReservationPackagesByIdRequest updatePackageRequest = UpdateReservationPackagesByIdRequest.builder()
        .hotelId("TEST").basketReference("TEST").arrival("2023-04-24").departure("2023-04-24")
        .roomsSelections(List.of(new RoomsSelectionsByReservationId()))
        .previousRoomsSelections(List.of(new RoomsSelectionsByReservationId())).build();
    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(List.of())
        .stayDateUpdateRequest(stayUpdateRequest).bookingAllowancesRequest(businessItemsRequest)
        .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
        .updateReservationPackagesByIdRequest(updatePackageRequest).build();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
        any(),
        anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
        .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(hotelReservationOutPort.confirmAmendSingleCall(
        any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
        reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertTrue(build.getEditRoomRequest().isEmpty());
  }

  @Test
  void confirmAmendForSingleCall__NullEditRoomRequest() {
    var stayUpdateRequest = mockUpdateReservationsRequestRequestSingleCall();
    stayUpdateRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();
    UpdateReservationPackagesByIdRequest updatePackageRequest = UpdateReservationPackagesByIdRequest.builder()
        .hotelId("TEST").basketReference("TEST").arrival("2023-04-24").departure("2023-04-24")
        .roomsSelections(List.of(new RoomsSelectionsByReservationId()))
        .previousRoomsSelections(List.of(new RoomsSelectionsByReservationId())).build();
    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(null)
        .stayDateUpdateRequest(stayUpdateRequest).bookingAllowancesRequest(businessItemsRequest)
        .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
        .updateReservationPackagesByIdRequest(updatePackageRequest).build();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
        any(),
        anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
        .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(hotelReservationOutPort.confirmAmendSingleCall(
        any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
        reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertNull(build.getEditRoomRequest());
  }

  @Test
  void confirmAmendForSingleCall__success_noDateUpdateRequest() {
    List<UpdateReservationsRequestSingleCall> editUpdateRequest = List.of(
        mockUpdateReservationsRequestRequestSingleCall());
    BookerDetailsCnpRequest bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    SpecialRequests specialRequests = createSpecialRequests();
    BusinessItemsRequest businessItemsRequest = createBusinessItemsRequest();
    UpdateReservationPackagesByIdRequest updatePackageRequest = UpdateReservationPackagesByIdRequest.builder()
        .hotelId("TEST").basketReference("TEST").arrival("2023-04-24").departure("2023-04-24")
        .roomsSelections(List.of(new RoomsSelectionsByReservationId()))
        .previousRoomsSelections(List.of(new RoomsSelectionsByReservationId())).build();
    ConfirmAmendForSingleRequest build = ConfirmAmendForSingleRequest.builder().editRoomRequest(editUpdateRequest)
        .stayDateUpdateRequest(null).bookingAllowancesRequest(businessItemsRequest)
        .bookerDetailsCnpRequest(bookerDetailsCnpRequest).specialRequests(List.of(specialRequests))
        .updateReservationPackagesByIdRequest(updatePackageRequest).build();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getEditRoomChangeReservations(any(), anyBoolean(), anyString()))
        .thenReturn(List.of(mockChangeReservation()));
    when(hotelReservationOutPort.confirmAmendSingleCall(
        any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(
        reservationByBasketRefResponse);

    ReservationByBasketRefResponse response = hotelReservationInPort.confirmAmendForSingleCall(build);

    assertNotNull(response);
    assertNull(build.getStayDateUpdateRequest());
    assertNull(response.getReservationByIdList().get(0).getDepositPolicies());
    assertNull(response.getReservationByIdList().get(0).getPaymentCard());
  }

  @Test
  void updateStayDate__Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequestSingleCall();
    updateReservationsRequest.getReservations().get(0).getRoomStay().setRoomRates(null);
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    when(hotelReservationOutPort.getStayDateChangeReservation(
            any(),
            anyBoolean(), anyString(), anyBoolean(), eq(null), eq(null)))
            .thenReturn(List.of(mockChangeReservation()));

    when(hotelReservationOutPort.getReservationsByIds(
        anyString(),
        anySet(),
        anyBoolean(),
        anyBoolean(),
        anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);
    //Act
    List<ChangeReservation> changeReservations =
        hotelReservationInPort.updateStayDateOrEditRoom(updateReservationsRequest);

    //Assert
    assertNotNull(changeReservations);
  }

  @Test
  void updateReservation__Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequest();
    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestRequest();
    updateReservationsRequestNoTemp.setTempReservations(null);
    doNothing().when(hotelReservationOutPort).updateReservations(updateReservationsRequestNoTemp, false, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservations(updateReservationsRequestNoTemp, false, null);
  }

  @Test
  void updateReservationPredefinedRates__Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestPredefinedRatesRequest();
    updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay().setRoomRates(null);
    updateReservationsRequest.setLinkAmendReservations(Map.of("454657", RESERVATION_ID));

    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestPredefinedRatesRequest();
    updateReservationsRequestNoTemp.setCompanyId(null);
    var reservationByBasketRefResponse = mockReservationByBasketRefActualDates();
    updateReservationsRequestNoTemp.setTempReservations(null);
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
        .setRoomRates(List.of(createRoomRate(new BigDecimal(87),
            LocalDate.now().plusDays(1).toString(),
            LocalDate.now().plusDays(1).toString()),
            createRoomRate(new BigDecimal(65),
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(1).toString()),
            createRoomRate(new BigDecimal(65),
                LocalDate.now().plusDays(2).toString(),
                LocalDate.now().plusDays(2).toString())
            ));
    updateReservationsRequestNoTemp.setNewRatesReservation(null);

    updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay()
        .setArrivalDate(LocalDate.now().plusDays(1).toString());
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
        .setArrivalDate(LocalDate.now().plusDays(1).toString());
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    doNothing().when(hotelReservationOutPort)
        .updateReservations(updateReservationsRequestNoTemp, true, null, true, null, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservations(updateReservationsRequestNoTemp, true,
        null, true, null, null);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void updateReservation_onlyStayDatesNoPackages_OverlapInFutureSuccess() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestOnlyStayDatesRequestNoBookingChannel();
    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestOnlyStayDatesRequestNoBookingChannel();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();
    updateReservationsRequestNoTemp.setTempReservations(null);
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
            .setRoomRates(createRoomRatesWithOverlapInFuture());

    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);

    doNothing().when(hotelReservationOutPort).updateReservations(updateReservationsRequestNoTemp, true, null, true, null, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservations(updateReservationsRequestNoTemp, true,
            null, true, null, null);
  }

  @Test
  void updateReservation_onlyStayDatesNoPackages_OverlapInPastSuccess() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();
    updateReservationsRequestNoTemp.setTempReservations(null);
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
            .setRoomRates(createRoomRatesWithOverlapInPast());

    updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay().setArrivalDate("2023-04-21");
    updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay().setDepartureDate("2023-04-25");

    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay().setArrivalDate("2023-04-21");
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay().setDepartureDate("2023-04-25");

    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);

    doNothing().when(hotelReservationOutPort).updateReservations(updateReservationsRequestNoTemp, true, null, true, null, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservations(updateReservationsRequestNoTemp, true,
            null, true, null, null);
  }

  @Test
  void updateReservation_onlyStayDatesWithPackages_Success() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();

    updateReservationsRequestNoTemp.setTempReservations(null);
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay().setRoomRates(createRoomRatesWithOverlapInFuture());

    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);

    doNothing().when(hotelReservationOutPort).updateReservations(updateReservationsRequestNoTemp, true, null, true, null, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservations(updateReservationsRequestNoTemp, true,
            null, true, null, null);
  }

  @Test
  void updateReservation_onlyStayDatesWithPackages_Success2() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var updateReservationsRequestNoTemp = mockUpdateReservationsRequestOnlyStayDatesRequest();
    var reservationByBasketRefResponse = mockReservationByBasketRefForAmendStayDates();
    updateReservationsRequestNoTemp.setTempReservations(null);
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
            .setRoomRates(List.of(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-25")
            .endDate("2023-04-27")
            .fixedRate(false)
            .build()));

    updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay()
        .setArrivalDate("2023-04-25");
    updateReservationsRequestNoTemp.getUpdateReservationsRequest().get(0).getRoomStay()
            .setArrivalDate("2023-04-25");
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);
    doNothing().when(hotelReservationOutPort)
        .updateReservations(updateReservationsRequestNoTemp, true, null, true, null, null);

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateReservations(updateReservationsRequestNoTemp, true,
        null, true, null, null);
  }

  @Test
  void updateReservation_WhenSpecialRequestsAreProvidedByTheSubstitutionRule_ThenForwardedToOutPort() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequest();
    var expectedRequest = mockUpdateReservationsRequestRequest();
    expectedRequest.setTempReservations(null);
    expectedRequest.getUpdateReservationsRequest().get(0).setSpecialRequests(List.of("DBLE"));
    expectedRequest.getUpdateReservationsRequest().get(0).getRoomStay().getRoomRates().get(0).setRoomType("DOUBLE");
    when(hotelReservationOutPort.getWbRoomTypes()).thenReturn(List.of("FAM"));
    when(hotelReservationOutPort.getSubstitutionListFromRule("FAM", 2, 2, "PI"))
        .thenReturn(List.of(new RoomSubstitution("DOUBLE", false, "DBLE", null, null)));
    when(hotelAvailabilityOutPort.getRoomTypesFromHotelInventory(updateReservationsRequest.getUpdateReservationsRequest().get(0).getHotelId(),
        updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay().getArrivalDate(),
        updateReservationsRequest.getUpdateReservationsRequest().get(0).getRoomStay().getDepartureDate(),
        1))
        .thenReturn(List.of("DOUBLE"));

    doNothing().when(hotelReservationOutPort).updateReservations(any(), eq(false), eq(null));

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservations(expectedRequest, false, null);
  }

  @Test
  void updateReservation_WhenSpecialRequestsAreProvidedByTheUser_ThenForwardedToOutPort() {
    //Arrange
    var updateReservationsRequest = mockUpdateReservationsRequestRequest();
    updateReservationsRequest.getUpdateReservationsRequest().get(0).setSpecialRequests(List.of("USER"));
    var expectedRequest = mockUpdateReservationsRequestRequest();
    expectedRequest.setTempReservations(null);
    expectedRequest.getUpdateReservationsRequest().get(0).setSpecialRequests(List.of("USER"));

    doNothing().when(hotelReservationOutPort).updateReservations(any(), eq(false), eq(null));

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservations(expectedRequest, false, null);
  }

  @Test
  void updateReservation_ShouldSetFixedRateToTrue_WhenOnlyUpdatingGuestName() {
    //Arrange
    var currentReservation = mockReservationByBasketRefResponseForGuestNameUpdate();
    var updateReservationsRequest = mockUpdateReservationsRequestForGuestNameUpdate();
    updateReservationsRequest.setTempReservations(currentReservation);

    ArgumentCaptor<UpdateReservationsRequest> requestCaptor = ArgumentCaptor.forClass(UpdateReservationsRequest.class);

    doNothing().when(hotelReservationOutPort).updateReservations(any(), eq(false), any());

    //Act
    hotelReservationInPort.updateReservations(updateReservationsRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .updateReservations(requestCaptor.capture(), eq(false), any());

    var capturedRequest = requestCaptor.getValue();
    var roomRates = capturedRequest.getUpdateReservationsRequest().get(0).getRoomStay().getRoomRates();

    assertNotNull(roomRates, "Room rates should not be null");
    assertFalse(roomRates.isEmpty(), "Room rates should not be empty");
    roomRates.forEach(roomRate ->
        assertTrue(roomRate.getFixedRate(), "fixedRate should be true when only updating guest name"));
  }

  @Test
  void copyPayment_Success(){
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = new HashSet<>(Arrays.asList("1234", "5678"));
    //Arrange
    HotelReservationOutPort hotelReservationOutPort = mock(HotelReservationOutPort.class);

    //Act
    hotelReservationInPort.movePaymentDetails(hotelId, reservationIds);

    //Assert
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void confirmAmend__Success() {
    //Arrange
    var request = ReservationTestUtils.mockConfirmAmendOnReservationsRequest();
    var reservationByBasketRefResponse = mockReservationByBasketRefResponse();
    var reservationByBasketRefResponseRateInfoExcluded = mockReservationByBasketRefResponseRateInfoExcluded(true);
    var featureFlag = new FeatureFlag();
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
        .thenReturn(reservationByBasketRefResponseRateInfoExcluded);
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(true), anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui())).thenReturn(false);
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation =  new HotelReservationType();
    var roomStay  = new RoomStayType();
    var rate =  new RoomRateType();
    var guest = new ResGuestType();
    guest.setPrimary(false);
    rate.setRatePlanCode("FLEXRATE");
    roomStay.setArrivalDate(LocalDate.now());
    roomStay.setRoomRates(List.of(rate));
    reservation.setRoomStay(roomStay);
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    reservation.setReservationGuests(List.of(guest));
    when(hotelReservationOutPort.getReservationsByIdsLight(any(),anySet()))
        .thenReturn(List.of(resLight));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    // Act
    var response = hotelReservationInPort.confirmAmend(request);

    // Assert
    assertThat(response, notNullValue());

    //Validate Address
    assertThat(response.getReservationByIdList().get(0).getReservationBooker().getAddress().getAddressLine1(), notNullValue());
  }


  @ParameterizedTest
  @ValueSource(strings = {"PI", "BB", "CCUI"})
  void confirmAmend__Success__Enable_FF_Duplicate_Profile_Same_Profile_Id(String channel) {
    //Arrange
    var request = ReservationTestUtils.mockConfirmAmendOnReservationsRequest();
    request.setBookingChannel(BookingChannel.builder().channel(channel).subchannel("WEB").build());
    var reservationByBasketRefResponse = mockReservationByBasketRefResponse();
    var reservationByBasketRefResponseRateInfoExcluded = mockReservationByBasketRefResponseRateInfoExcluded(true);
    reservationByBasketRefResponseRateInfoExcluded.getReservationByIdList().forEach(reservationById -> reservationById.getReservationGuestList().forEach(reservationGuest -> reservationGuest.setProfileId("profileId")));
    reservationByBasketRefResponse.getReservationByIdList().forEach(reservationById -> reservationById.getReservationGuestList().forEach(reservationGuest -> reservationGuest.setProfileId("profileId")));
    var featureFlag = new FeatureFlag();
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
        .thenReturn(reservationByBasketRefResponseRateInfoExcluded);
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(true), anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi())).thenReturn(true);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb())).thenReturn(true);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui())).thenReturn(true);
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation =  new HotelReservationType();
    var roomStay  = new RoomStayType();
    var rate =  new RoomRateType();

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("profileId");
    uniqueIdType.setType("reservationGuest");

    uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    profileType.setProfileType(ProfileTypeType.GUEST);

    ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIdType));
    resGuestTypeProfileInfo.setProfile(profileType);

    ResGuestType reservationGuest = new ResGuestType();
    reservationGuest.setPrimary(Boolean.TRUE);
    reservationGuest.setProfileInfo(resGuestTypeProfileInfo);

    rate.setRatePlanCode("FLEXRATE");
    roomStay.setArrivalDate(LocalDate.now());
    roomStay.setRoomRates(List.of(rate));
    reservation.setRoomStay(roomStay);
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    reservation.setReservationGuests(List.of(reservationGuest));

    when(hotelReservationOutPort.getReservationsByIdsLight(any(),anySet()))
        .thenReturn(List.of(resLight));

    // Act
    var response = hotelReservationInPort.confirmAmend(request);

    // Assert
    assertThat(response, notNullValue());

    //Validate Profile id
    assertThat(response.getReservationByIdList().get(0).getReservationGuestList().get(0).getProfileId(), notNullValue());
    assertEquals("profileId", response.getReservationByIdList().get(0).getReservationGuestList().get(0).getProfileId());
  }


  @ParameterizedTest
  @ValueSource(strings = {"PI","BB", "CCUI"})
  void confirmAmend__Success__Enable_FF_Duplicate_Profile_Different_Profile_Id(String channel) {
    //Arrange
    var request = ReservationTestUtils.mockConfirmAmendOnReservationsRequest();
    request.setBookingChannel(BookingChannel.builder().channel(channel).subchannel("WEB").build());
    var reservationByBasketRefResponse = mockReservationByBasketRefResponse();
    var reservationByBasketRefResponseRateInfoExcluded = mockReservationByBasketRefResponseRateInfoExcluded(true);
    reservationByBasketRefResponseRateInfoExcluded.getReservationByIdList().forEach(reservationById -> reservationById.getReservationGuestList().forEach(reservationGuest -> reservationGuest.setProfileId("profileId")));
    reservationByBasketRefResponse.getReservationByIdList().forEach(reservationById -> reservationById.getReservationGuestList().forEach(reservationGuest -> reservationGuest.setProfileId("profileId")));
    var featureFlag = new FeatureFlag();
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
        .thenReturn(reservationByBasketRefResponseRateInfoExcluded);
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(true), anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi())).thenReturn(true);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb())).thenReturn(true);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui())).thenReturn(true);
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation =  new HotelReservationType();
    var roomStay  = new RoomStayType();
    var rate =  new RoomRateType();

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("update");
    uniqueIdType.setType("reservationGuest");

    uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    profileType.setProfileType(ProfileTypeType.GUEST);

    ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIdType));
    resGuestTypeProfileInfo.setProfile(profileType);

    ResGuestType reservationGuest = new ResGuestType();
    reservationGuest.setPrimary(Boolean.TRUE);
    reservationGuest.setProfileInfo(resGuestTypeProfileInfo);

    rate.setRatePlanCode("FLEXRATE");
    roomStay.setArrivalDate(LocalDate.now());
    roomStay.setRoomRates(List.of(rate));
    reservation.setRoomStay(roomStay);
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    reservation.setReservationGuests(List.of(reservationGuest));

    when(hotelReservationOutPort.getReservationsByIdsLight(any(),anySet()))
        .thenReturn(List.of(resLight));

    // Act
    hotelReservationInPort.confirmAmend(request);

    // Assert
    verify(hotelReservationOutPort, times(1)).isProfileUpdated(anyString(), anyString(),anyString());
  }


  @Test
  void confirmAmend_markAsPayOnArrival_Success() {
    //Arrange
    var request = ReservationTestUtils.mockConfirmAmendOnReservationsRequest();
    request.setMarkAsPayOnArrival(true);
    var reservationByBasketRefResponse = mockReservationByBasketRefResponse();
    var reservationByBasketRefResponseRateInfoExcluded = mockReservationByBasketRefResponseRateInfoExcluded(false);
    var featureFlag = new FeatureFlag();
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
            .thenReturn(reservationByBasketRefResponseRateInfoExcluded);
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(true), anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui())).thenReturn(false);
    doNothing().when(hotelReservationOutPort)
            .updateReservationsToPayOnArrival(reservationByBasketRefResponseRateInfoExcluded.getReservationByIdList());
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation =  new HotelReservationType();
    var roomStay  = new RoomStayType();
    var rate =  new RoomRateType();
    var guest = new ResGuestType();
    guest.setPrimary(false);
    rate.setRatePlanCode("FLEXRATE");
    roomStay.setArrivalDate(LocalDate.now());
    roomStay.setRoomRates(List.of(rate));
    reservation.setRoomStay(roomStay);
    reservation.setReservationGuests(List.of(guest));
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    when(hotelReservationOutPort.getReservationsByIdsLight(any(),anySet()))
            .thenReturn(List.of(resLight));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    // Act
    var response = hotelReservationInPort.confirmAmend(request);

    // Assert
    assertThat(response, notNullValue());
  }

  @Test
  void updateBookerDetails_Success() {
    var bookerDetailsCnpRequest = createbookerDetailsCnpRequest();
    //Arrange
    doNothing().when(hotelReservationOutPort).updateBookerDetails(bookerDetailsCnpRequest);

    //Act
    hotelReservationInPort.updateBookersDetails(bookerDetailsCnpRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateBookerDetails(bookerDetailsCnpRequest);
  }

  @Test
  void updateBillingAddress_Success() {

    //Arrange
    var billingAddressRequest = updateBillingAddressRequest();

    doNothing().when(hotelReservationOutPort).updateBillingAddress(any());

    //Act
    hotelReservationInPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateBillingAddress(billingAddressRequest);

  }

  @Test
  void updateBillingAddressCcui_Success() {

    //Arrange
    var billingAddressRequest = updateBillingAddressCcuiRequest();

    doNothing().when(hotelReservationOutPort).updateBillingAddressCcui(any());

    //Act
    hotelReservationInPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(hotelReservationOutPort, times(1)).updateBillingAddressCcui(billingAddressRequest);

  }


  @Test
  void delete__routingInstructionsSuccess() {
    //Arrange
    final String hotelId = "FRAMTI";
    final Set<String> reservationIds = Collections.singleton("12345");
    doNothing().when(hotelReservationOutPort).deleteRoutingInstruction(hotelId, reservationIds);

    //Act
    hotelReservationInPort.deleteRoutingInstruction(hotelId, reservationIds);

    //Assert
    verify(hotelReservationOutPort, times(1)).deleteRoutingInstruction(hotelId, reservationIds);
  }

  @Test
  void testUpdateBookerEmail_success() {
    // Arrange
    String hotelID = "DAHMME";
    String reservationNo = "12345678";
    String emailAddress = "secondEmail@domain.uk";
    UpdateBookerEmailRequest updateBookerEmailRequest =
        mockUpdateBookerEmailRequest(hotelID, reservationNo, emailAddress);

    doNothing().when(hotelReservationOutPort).updateBookerEmail(
        updateBookerEmailRequest);

    // Act
    hotelReservationInPort.updateBookerEmail(updateBookerEmailRequest);

    // Assert
    verify(hotelReservationOutPort, times(1)).updateBookerEmail(updateBookerEmailRequest);

  }

  @Test
  void createMemo_Success() {
    // Arrange
    when(hotelReservationOutPort.createMemo(any())).thenReturn(new MemosResponse());

    // Act
    var response = hotelReservationInPort.createMemo(new CreateMemoRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMemos_Success() {
    // Arrange
    when(hotelReservationOutPort.getMemos(any(), anySet())).thenReturn(new MemosResponse());

    // Act
    var response = hotelReservationInPort.getMemos("hotelId", Set.of("reservationId"));

    // Assert
    assertNotNull(response);
  }

  @Test
  void attachProfileToReservation_Success() {
    // Arrange
    doNothing().when(hotelReservationOutPort).attachProfileToReservations(any());
    var request = new AttachReservationProfileRequest();

    // Act
    hotelReservationInPort.attachProfileToReservations(request);

    //Assert
    verify(hotelReservationOutPort, Mockito.times(1))
        .attachProfileToReservations(request);
  }

  @Test
  void addAttachmentToReservation_success() {
    //Arrange
    when(hotelReservationOutPort.addAttachmentToReservation(any()))
        .thenReturn(getPreCheckInResponse("Success", "Attachment added successfully"));

    //Act
    var response = hotelReservationInPort.addAttachmentToReservation(
        mockReservationFileAttachmentRequest());

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void saveReservationPreCheckIn_success() {
    //Arrange
    when(hotelReservationOutPort.saveReservationPreCheckIn(any()))
        .thenReturn(getPreCheckInResponse("Success", "Pre-CheckIn status saved successfully"));

    //Act
    var response = hotelReservationInPort.saveReservationPreCheckIn(mockPreCheckInRequest());

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(hotelReservationOutPort);
  }

  @Test
  void deleteReservationPreCheckIn_Success() {
    // Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";
    doNothing().when(hotelReservationOutPort).deleteReservationPreCheckIn(hotelId, reservationId);

    // Act
    hotelReservationInPort.deleteReservationPreCheckIn(hotelId, reservationId);

    // Assert
    verify(hotelReservationOutPort, times(1))
        .deleteReservationPreCheckIn(hotelId, reservationId);
  }

  @Test
  void deleteRegCardAttachment_Success() {
    // Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";
    doNothing().when(hotelReservationOutPort).deleteRegCardAttachment(hotelId, reservationId);

    // Act
    hotelReservationInPort.deleteRegCardAttachment(hotelId, reservationId);

    // Assert
    verify(hotelReservationOutPort, times(1)).deleteRegCardAttachment(hotelId, reservationId);
  }

  @Test
  void linkReservationToLeisureCustomer__Success() {
    //Arrange
    var linkReservationToLeisureCustomerRequest =
        ReservationTestUtils.mockLinkReservationToLeisureCustomerRequest();
    doNothing().when(hotelReservationOutPort).linkReservationToLeisureCustomer(any());

    //Act
    hotelReservationInPort.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);

    //Assert
    verify(hotelReservationOutPort, times(1))
        .linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);
  }

  @Test
  void confirmAmend_NonRefundableBooking_False() {
    //Arrange
    var request = ReservationTestUtils.mockConfirmAmendOnReservationsRequest();
    var reservationByBasketRefResponse = mockReservationByBasketRefResponse();
    var reservationByBasketRefResponseRateInfoExcluded = mockReservationByBasketRefResponseRateInfoExcluded(true);
    var featureFlag = new FeatureFlag();
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(false), anyBoolean()))
        .thenReturn(reservationByBasketRefResponseRateInfoExcluded);
    when(hotelReservationOutPort.getReservationsByIds(anyString(), anySet(), anyBoolean(), eq(true), anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb())).thenReturn(false);
    when(unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui())).thenReturn(false);
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation =  new HotelReservationType();
    var roomStay  = new RoomStayType();
    var rate =  new RoomRateType();
    var guest = new ResGuestType();
    guest.setPrimary(false);
    rate.setRatePlanCode("FLEXRATE");
    roomStay.setArrivalDate(LocalDate.now());
    roomStay.setRoomRates(List.of(rate));
    reservation.setRoomStay(roomStay);
    reservation.setReservationGuests(List.of(guest));
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    when(hotelReservationOutPort.getReservationsByIdsLight(any(),anySet()))
        .thenReturn(List.of(resLight));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    // Act
    var response = hotelReservationInPort.confirmAmend(request);

    // Assert
    assertThat(response, notNullValue());

    //Validate Address
    assertThat(response.getReservationByIdList().get(0).getReservationBooker().getAddress().getAddressLine1(), notNullValue());
  }

  @Test
  void getReservationAmounts_success() {
    ReservationAmounts reservationAmounts = ReservationAmounts.buildResAmountWithZero();
    when(hotelReservationOutPort.getReservationAmounts(anyString(), any(Set.class))).thenReturn(
        reservationAmounts);

    ReservationAmounts result = hotelReservationInPort.getReservationAmounts("FRAMTI", Set.of("7788123"));

    assertNotNull(result);
    assertEquals(reservationAmounts.getOutStandingCostOfStay(), result.getOutStandingCostOfStay());
  }

  @Test
  void testGetReservationsByIds_ShouldReturnResponse() {
    // Arrange
    String hotelId = "HOTEL123";
    Set<String> reservationIds = Set.of("RES1", "RES2");
    ReservationLightweightResponse mockResponse = new ReservationLightweightResponse(List.of());

    when(hotelReservationOutPort.getReservationsByIds(hotelId, reservationIds)).thenReturn(mockResponse);

    // Act
    ReservationLightweightResponse response = hotelReservationInPort.getReservationsByIds(hotelId, reservationIds);

    // Assert
    assertEquals(mockResponse, response);
    verify(hotelReservationOutPort).getReservationsByIds(hotelId, reservationIds);
  }

  @Test
  void getReservationPaymentMethod__Success() {
    //Arrange
    String hotelId = "hotelId";
    List<String> reservationIds = List.of("res1");
   var payment =  ReservationPaymentCardType.builder().build();
   var reservationPayment = ReservationsPaymentCardType.builder().paymentCardType(payment).build();
    when(hotelReservationOutPort.getReservationPaymentMethod(hotelId, reservationIds)).thenReturn(
        List.of(reservationPayment));

    //Act
    var result = hotelReservationInPort.getReservationMethodPayment(hotelId, reservationIds);

    //Assert
    assertNotNull(result);
    assertEquals(List.of(reservationPayment), result);
    verify(hotelReservationOutPort).getReservationPaymentMethod(hotelId, reservationIds);
  }

  private PreCheckInRequest mockPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .arrivalTime(new Date())
        .hotelId("STUAIR")
        .reservationId("123456")
        .build();
  }

  private ReservationFileAttachmentRequest mockReservationFileAttachmentRequest() {
    return ReservationFileAttachmentRequest.builder()
        .fileAttachment("Base64 string")
        .description("Test attachment")
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .global(false)
        .reservationId("1613333")
        .overwriteExistingFile(true)
        .hotelId("STUAIR")
        .build();
  }

  private PreCheckInResponse getPreCheckInResponse(String status, String message) {
    return PreCheckInResponse.builder()
        .status(status)
        .message(message)
        .build();
  }

  private UpdateBookerEmailRequest mockUpdateBookerEmailRequest(
      String hotelID,
      String reservationNo,
      String emailAddress) {
    return UpdateBookerEmailRequest.builder()
        .hotelId(hotelID)
        .emailAddress(emailAddress)
        .reservationIds(Set.of(reservationNo))
        .build();
  }

  private ChangeReservation mockChangeReservation() {

    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("123456");
    reservationIdItem.setType("Reservation");

    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setHotelId("HOTELCODE");
    hotelReservationInstructionType.setReservationIdList(
            Collections.singletonList(reservationIdItem));

    ReservationPackageType reservationPackages = new ReservationPackageType();
    reservationPackages.setEndDate(LocalDate.of(2022, 4, 3));
    reservationPackages.setStartDate(LocalDate.of(2022, 4, 2));
    reservationPackages.setPackageCode("PIBTEST");

    ReservationPackageScheduleType reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setReservationDate(reservationPackages.getStartDate());
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(24.99));
    reservationPackageScheduleType.setConsumptionDate(reservationPackages.getStartDate());

    PackageCodeHeaderType packageCodeHeaderType = new PackageCodeHeaderType();

    reservationPackages.setPackageHeaderType(packageCodeHeaderType);
    reservationPackages.setScheduleList(Collections.singletonList(reservationPackageScheduleType));
    hotelReservationInstructionType.setReservationPackages(
            Collections.singletonList(reservationPackages));
    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(Collections.singletonList(hotelReservationInstructionType));

    return changeReservation;

  }

  private CopyReservationsRequest createCopyReservationsRequest() {
    return CopyReservationsRequest.builder()
        .reservationIds(Set.of("123456"))
        .hotelId("LONEUS")
        .externalReferenceId("ABC567890")
        .build();
  }

  private CopyReservationsResponse createCopyReservationsResponse() {
    return CopyReservationsResponse.builder()
        .reservations(Arrays.asList(new CopyReservationResponse("857716", "2023-02-23T15:21:22Z"),
            new CopyReservationResponse("857717", "2023-02-23T15:22:22Z")))
        .build();
  }

  private UpdateReservationOverrideReasonsRequest mockUpdateReservationOverrideReasonsRequest() {
    return UpdateReservationOverrideReasonsRequest.builder()
        .hotelId("BERALX")
        .reasonCode("ILL")
        .reasonName("Medical Appointents")
        .callerName("John Doe")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateReservationCcAgentIdRequest mockUpdateReservationCcAgentIdRequest() {
    return UpdateReservationCcAgentIdRequest.builder()
        .hotelId("BERALX")
        .ccAgentId("Jane.Doe@wb.com")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateReasonForStayResponse mockUpdateReasonForStayResponse() {
    return UpdateReasonForStayResponse.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("123456"))
        .build();
  }

  private UpdateReasonForStayRequest mockUpdateReasonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .reservationIds(List.of("264401"))
        .build();
  }

  private ReservationPackagesRequest createReservationPackagesRequest() {
    return ReservationPackagesRequest.builder()
        .reservationsId(List.of("1234"))
        .hotelId("EDIPAR")
        .arrival("2022-12-25")
        .departure("2022-12-29")
        .roomsSelections(new ArrayList<>())
        .previousRoomsSelections(new ArrayList<>())
        .build();
  }

  private UpdateDiscountRequest createUpdateDiscountRequest() {
    return UpdateDiscountRequest.builder()
        .reservationIds(Set.of("1234"))
        .hotelId("DUSOST")
        .discountAmount(BigDecimal.valueOf(10))
        .currency("EUR")
        .build();
  }

  private BusinessItemsRequest createBusinessItemsRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .businessItems(BusinessItems.builder()
            .businessNotes("Dinner is authorized.")
            .purchaseOrderNumber("1010101010")
            .customReferenceNumber("11001100")
            .businessAllowances(List.of(BusinessAllowance.builder()
                .budget(BigDecimal.ZERO)
                .allowance("SOME_ALLOWANCE")
                .isAuthorised(Boolean.TRUE)
                .build()))
            .build())
        .build();
  }

  private CompanyQuestionAndAnswerDetailsRequest createCompanyQuestionAndAnswerDetailsRequest() {
    return CompanyQuestionAndAnswerDetailsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .companyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetails.builder()
            .customerReferenceQuestionAndAnswer(CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                "Test").build())
            .purchaseOrderQuestionAndAnswer(CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                "Test").build())
            .userDefinedQuestionAndAnswers(List.of(CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                "Test").build())).build()).build();
  }

  private ReservationById createReservations(List<DepositPolicies> depositPolicies) {
    return ReservationById
        .builder()
        .depositPolicies(depositPolicies)
        .reservationPackageList(List.of(ReservationPackagesDetailsResponse.builder()
            .packageCode("PIBBEV")
            .startDate("2023-10-20")
            .endDate("2023-10-20")
            .build()))
        .build();
  }

  private ReservationByBasketRefResponse createReservationByBasketRefResponse(
      List<ReservationById> reservations, BigDecimal totalAmount, BigDecimal balanceOutstanding,
      BigDecimal newTotal, BigDecimal previousTotal) {
    return ReservationByBasketRefResponse
        .builder()
        .reservationByIdList(reservations)
        .totalCost(totalAmount)
        .newTotal(newTotal)
        .previousTotal(previousTotal)
        .balanceOutstanding(balanceOutstanding)
        .build();
  }

  private DepositPolicies createDepositPolicies(BigDecimal amountDue, BigDecimal amountPaid,
      String policyCode) {
    return DepositPolicies
        .builder()
        .amountDue(CurrencyAmountType
            .builder()
            .amount(amountDue)
            .currencyCode("GBP")
            .build())
        .amountPaid(CurrencyAmountType
            .builder()
            .amount(amountPaid)
            .build())
        .policyCode(policyCode)
        .build();
  }

  private List<ReservationCreationResponse> mockReservationCreationResponse() {
    return singletonList(ReservationCreationResponse.builder()
        .reservationId("1234")
        .createDateTime("2022-06-19")
        .roomStay(mockRoomStay())
        .depositPolicies(
            singletonList(createDepositPolicies(BigDecimal.ONE, BigDecimal.ONE, "D1")))
        .build());
  }

  private ReservationResponse mockReservationResponse() {
    return ReservationResponse.builder()
        .reservations(mockReservationCreationResponse())
        .totalCost(BigDecimal.ONE)
        .build();
  }

  private RoomStay mockRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 06, 05))
        .departureDate(LocalDate.of(2022, 06, 07))
        .adultCount(2)
        .childCount(2)
        .ratePlanCode("AXWS")
        .roomType("SINGLE")
        .build();
  }

  private ReservationGuestRequest createValidReservationGuestRequest() {

    final String hotelId = "MANOLD";

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    final BookerDetails booker = BookerDetails
        .builder()
        .title("Mrs")
        .firstName("John")
        .lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com")
        .mobile("+39567463783")
        .address(address)
        .acceptFutureMailing(Boolean.FALSE)
        .build();

    final StayingGuestAddress guestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    final StayingGuestAdditionalDetails guestAdditionalDetails = StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("Briton")
        .passportNumber("EC1A1BB")
        .build();

    final StayingGuestDetails guest = StayingGuestDetails
        .builder()
        .title("Mrs")
        .firstName("Debbie")
        .lastName("Doe")
        .address(guestAddress)
        .additionalDetails(guestAdditionalDetails)
        .build();

    final StayingGuest stayingGuests = StayingGuest.builder()
        .reservationId("1234567")
        .sameAsBooker(false)
        .stayingGuestDetails(guest)
        .build();

    return ReservationGuestRequest.builder()
        .booker(booker)
        .stayingGuests(List.of(stayingGuests))
        .hotelId(hotelId)
        .reasonForStay("LEI")
        .sendEmailConfirmation(Boolean.TRUE)
        .sendEmailInvoice(Boolean.TRUE)
        .build();
  }

  private ReservationPackagesResponse mockReservationsPackagesResponse() {
    return ReservationPackagesResponse.builder()
        .roomsSelections(singletonList(
            uk.co.whitbread.ohip.domain.model.reservation.out.RoomsSelections.builder()
                .packagesSelection(singletonList(
                    uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection.builder()
                        .noSelections(2).id("TEST").build())).build()))
        .build();
  }


  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(singletonList("123456"));
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("PIBTEST");
    packagesSelection.setPackageGroup("MDP");

    return packagesSelection;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(singletonList(createPackagesSelection()));

    return roomsSelections;
  }

  private RatePlanRoomTypeChangeRequest mockRatePlanChangeRequest() {
    return RatePlanRoomTypeChangeRequest.builder()
        .hotelId("HOTELCODE")
        .reservationIds(Collections.singletonList("100100"))
        .basketReferenceId("HOTELCODE1001001")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .currency("GBP")
        .rateCode("FLEXRATE")
        .roomTypes(Collections.singletonList("DOUBLE"))
        .adultsNumber(Collections.singletonList(1))
        .childrenNumber(Collections.singletonList(0))
        .build();
  }

  private RatePlanRoomTypeChangeRequest mockRoomTypeChangeRequest() {
    return RatePlanRoomTypeChangeRequest.builder()
        .hotelId("HOTELCODE")
        .reservationIds(Collections.singletonList("100100"))
        .basketReferenceId("HOTELCODE1001001")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .currency("GBP")
        .rateCode("FLEXRATE")
        .roomTypes(Collections.singletonList("VDOUBL"))
        .adultsNumber(Collections.singletonList(1))
        .childrenNumber(Collections.singletonList(0))
        .build();
  }

  private CancellationPoliciesResponse createCancellationPolicies() {
    return CancellationPoliciesResponse.builder()
            .text("Cancellations after 1pm on the day of arrival charged 100% of 1 night")
            .time("2022-03-04T01:00:00+00:00")
            .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestRequest() {

    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("DE")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id(RESERVATION_ID)
        .build();

    var roomOccupancy = new RoomOccupancy(2, 2);
    var rateType = RateType.builder()
            .rate(List.of(uk.co.whitbread.ohip.domain.model.reservation.in.AmountType.builder()
                    .base(uk.co.whitbread.ohip.domain.model.reservation.in.TotalType.builder()
                            .amountBeforeTax(new BigDecimal(87))
                            .currencyCode("USD")
                            .build())
                    .start("2023-04-24")
                    .end("2023-04-24")
                    .build()))
            .build();

    var roomRate = new RoomRate(rateType, "FAM", "FLEXRATE", roomOccupancy,
        "2023-04-24", "2023-04-24", true);

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay("2023-04-24",
        "2023-04-24", roomOccupancy, List.of(roomRate));

    List<ReservationGuests> reservationGuestsList = new ArrayList<>();
    ProfileType profileType = new ProfileType();
    PersonNameType personName = new PersonNameType();
    personName.setGivenName("givenName");
    personName.setSurname("surname");
    personName.setNameTitle("mrs.");
    personName.setNameType(PersonNameTypeType.PRIMARY);

    CustomerType customerType = new CustomerType();
    customerType.setPersonName(List.of(personName));
    profileType.setCustomer(customerType);

    AddressType addressType = new AddressType();
    addressType.setAddressLine(List.of("line 1", "line 2", "line 3", "line4"));
    AddressInfoType addressInfoType = new AddressInfoType();
    addressInfoType.setAddress(addressType);
    ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    profileType.setAddresses(profileTypeAddresses);

    var reservationGuests = ReservationGuests.builder()
            .profileInfo(ProfileInfo.builder()
                    .profile(profileType)
                    .build())
            .build();

    reservationGuestsList.add(reservationGuests);

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(resType)
        .roomStay(roomStay)
        .reservationGuests(reservationGuestsList)
        .build();

    ReservationByBasketRefResponse tempReservations = mockReservationByBasketRefResponse();

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
            .tempReservations(tempReservations)
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestPredefinedRatesRequest() {

    var distributionBoockingChannel = BookingChannel.builder()
        .channel("DISTR")
        .subchannel("AGENCY")
        .language("N/A")
        .build();

    var res1 = uk.co.whitbread.ohip.domain.model.reservation.in.Reservation.builder()
        .adults(1)
        .children(0)
        .hotelId("TestHotelId")
        .roomRates(mockRoomRateReservationWithPredefinedRates())
        .arrival(LocalDate.now().plusDays(1).toString())
        .departure(LocalDate.now().plusDays(3).toString())
        .externalReferenceId(RESERVATION_ID)
        .build();

    var retVal = mockUpdateReservationsRequestCurrentDates();
    retVal.setBookingChannel(distributionBoockingChannel);
    retVal.setNewRatesReservation(List.of(res1));
    retVal.setDistributionIATANumber("12345678");
    return retVal;
  }

  private UpdateReservationsRequestSingleCall mockUpdateReservationsRequestRequestSingleCall() {

    var bc = BookingChannelSingleCall.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .build();

    var roomOccupancy = new RoomOccupancy(2, 2);

    var roomRate = new UpdateRoomRateRequest("FAM", "FLEXRATE", roomOccupancy,
            "2023-04-24", "2023-04-24");

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.UpdateRoomStayRequest("2023-04-24",
            "2023-04-24", roomOccupancy, List.of(roomRate));

    List<ReservationGuestsSingleCall> reservationGuestsList = new ArrayList<>();
    ProfileTypeSingleCall profileType = new ProfileTypeSingleCall();
    PersonNameTypeSingleCall personName = new PersonNameTypeSingleCall();
    personName.setGivenName("givenName");
    personName.setSurname("surname");
    personName.setNameTitle("mrs.");
    personName.setNameType("Primary");
    EmailTypeSingleCall emailTypeSingleCall = new EmailTypeSingleCall("email@email.com");
    EmailInfoTypeSingleCall emailInfoTypeSingleCall = new EmailInfoTypeSingleCall(emailTypeSingleCall);
    ProfileTypeEmailsSingleCall profileTypeEmailsSingleCall = new ProfileTypeEmailsSingleCall(
            List.of(emailInfoTypeSingleCall));


    CustomerTypeSingleCall customerType = new CustomerTypeSingleCall();
    customerType.setPersonName(List.of(personName));
    profileType.setCustomer(customerType);
    profileType.setEmails(profileTypeEmailsSingleCall);
    var reservationGuests = ReservationGuestsSingleCall.builder()
            .profileInfo(ProfileInfoSingleCall.builder()
                    .profile(profileType)
                    .build())
            .build();

    reservationGuestsList.add(reservationGuests);

    var updateReservationReq = UpdateReservationRequestSingleCall.builder()
            .hotelId("TestHotelId")
            .roomStay(roomStay)
            .reservationGuests(reservationGuestsList)
            .reservationId(RESERVATION_ID)
            .build();

    ReservationByBasketRefResponseSingleCall tempReservations = mockReservationByBasketRefResponseSingleCall();

    return UpdateReservationsRequestSingleCall.builder()
            .bookingChannel(bc)
            .reservations(List.of(updateReservationReq))
            .tempReservations(tempReservations)
            .build();
  }

  private ReservationByBasketRefResponseSingleCall mockReservationByBasketRefResponseSingleCall () {
    List<ReservationByIdResponseSingleCall> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
            .pricePerNight(new BigDecimal(123.34))
            .startDate("2023-04-24").build();

    ratePerNightList.add(ratePerNight);
    List<ReservationByIdGuestsResponse> reservationGuestList = new ArrayList<>();
    ReservationByIdGuestsResponse reservationGuest = ReservationByIdGuestsResponse.builder()
            .type("Primary")
            .givenName("givenName")
            .surName("surname")
            .build();
    reservationGuestList.add(reservationGuest);
    ReservationEmailNotificationsSingleCall emailNotificationsSingleCall = new ReservationEmailNotificationsSingleCall(
            false, false
    );
    ReservationByIdResponseSingleCall reservationById = ReservationByIdResponseSingleCall.builder()
            .reservationEmailNotifications(emailNotificationsSingleCall)
            .roomStay(RoomStayByIdResponse.builder()
                    .sourceCode("TEST")
                    .arrivalDate("2023-04-24")
                    .departureDate("2023-04-24")
                    .adultsNumber(1)
                    .childrenNumber(0)
                    .ratesPerNight(ratePerNightList)
                    .roomType("FMQUAD")
                    .ratePlanCode("FLEXRATE")
                    .build())
            .reservationGuestList(reservationGuestList)
            .reservationBooker(ReservationBookerSingleCall.builder()
                    .address(ReservationBookerAddress.builder()
                            .addressLine1("SAID BLOCK")
                            .addressLine2("NEW ARENA Layout")
                            .addressLine3("JADERAN")
                            .cityName("LONDON")
                            .postalCode("SWA 1AA")
                            .countryCode("GB").build()).build())
            .rateInfo(RateInfoSingleCall.builder()
                    .summary(RateInfoSummarySingleCall.builder()
                            .details(List.of(RateInfoDetailsSingleCall.builder()
                                    .net(new BigDecimal(45))
                                    .currencyCode("GBP")
                                    .summaryDate("2023-04-24")
                                    .build()))
                            .build())
                    .build())
            .cashiering(ResCashieringTypeSingleCall.builder()
                    .taxType(ReservationTaxTypeInfoSingleCall.builder()
                            .code("UK")
                            .build())
                    .build())
            .reservationId(RESERVATION_ID)
            .build();
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponseSingleCall.builder()
            .reservationByIdList(reservationByIdList).build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponse () {
    List<ReservationById> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
            .pricePerNight(new BigDecimal(87))
            .startDate("2023-04-24").build();

    ratePerNightList.add(ratePerNight);
    List<ReservationGuest> reservationGuestList = new ArrayList<>();
    ReservationGuest reservationGuest = ReservationGuest.builder()
            .type("Primary")
            .givenName("givenName")
            .surname("surname")
            .build();
    reservationGuestList.add(reservationGuest);
    ReservationById reservationById = ReservationById.builder()
            .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.parse("2023-04-24"))
                    .departureDate(LocalDate.parse("2023-04-24"))
                    .adultCount(1)
                    .childCount(0)
                    .ratesPerNight(ratePerNightList)
                    .roomType("FMQUAD")
                    .ratePlanCode("FLEXRATE")
                    .build())
            .reservationGuestList(reservationGuestList)
            .reservationBooker(ReservationBooker.builder()
                    .address(ReservationBookerAddress.builder()
                            .addressLine1("SAID BLOCK")
                            .addressLine2("NEW ARENA Layout")
                            .addressLine3("JADERAN")
                            .cityName("LONDON")
                            .postalCode("SWA 1AA")
                            .countryCode("GB").build()).build())
            .rateInfo(RateInfo.builder()
                    .summary(RateInfoSummary.builder()
                            .details(List.of(RateInfoDetails.builder()
                                            .net(new BigDecimal(45))
                                            .currencyCode("GBP")
                                            .summaryDate("2023-04-24")
                                    .build()))
                            .currencyCode("USD")
                            .build())
                    .build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder()
                            .code("UK")
                            .build())
                    .build())
            .reservationId(RESERVATION_ID)
            .build();
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponse.builder()
            .reservationByIdList(reservationByIdList).build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseRateInfoExcluded (boolean includeUserDefinedFields) {
    List<ReservationById> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
        .pricePerNight(new BigDecimal(123.34))
        .startDate("2023-04-24").build();

    ratePerNightList.add(ratePerNight);
    List<ReservationGuest> reservationGuestList = new ArrayList<>();
    ReservationGuest reservationGuest = ReservationGuest.builder()
        .type("Primary")
        .givenName("givenName")
        .surname("surname")
        .build();
    reservationGuestList.add(reservationGuest);
    ReservationById reservationById = ReservationById.builder()
        .roomStay(RoomStay.builder()
            .arrivalDate(LocalDate.parse("2023-04-24"))
            .departureDate(LocalDate.parse("2023-04-24"))
            .adultCount(1)
            .childCount(0)
            .ratesPerNight(ratePerNightList)
            .roomType("FMQUAD")
            .ratePlanCode("FLEXRATE")
            .build())
        .reservationGuestList(reservationGuestList)
        .reservationBooker(ReservationBooker.builder()
            .address(ReservationBookerAddress.builder()
                .addressLine1("SAID BLOCK")
                .addressLine2("NEW ARENA Layout")
                .addressLine3("JADERAN")
                .cityName("LONDON")
                .postalCode("SWA 1AA")
                .countryCode("GB").build()).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder()
                .code("UK")
                .build())
            .build())
        .reservationId(RESERVATION_ID)
        .build();
    if(includeUserDefinedFields) {
      reservationById.setUserDefinedFields(UserDefinedFields.builder()
              .characterUDFs(List.of(CharacterUDFs.builder()
                      .name("UDFC16")
                      .value("12345678")
                      .build()))
              .build());
    }
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdList).build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefForAmendStayDates () {
    List<ReservationById> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
            .pricePerNight(new BigDecimal(87))
            .startDate("2023-04-24")
            .build();

    ratePerNightList.add(ratePerNight);
    ReservationById reservationById = ReservationById.builder()
            .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.parse("2023-04-24"))
                    .departureDate(LocalDate.parse("2023-04-25"))
                    .ratesPerNight(ratePerNightList)
                    .build())
            .reservationId(RESERVATION_ID)
            .build();
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdList)
        .currencyCode("USD")
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefActualDates() {
    List<ReservationById> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
        .pricePerNight(new BigDecimal(87))
        .startDate(LocalDate.now().plusDays(1).toString())
        .build();

    ratePerNightList.add(ratePerNight);
    ReservationById reservationById = ReservationById.builder()
        .roomStay(RoomStay.builder()
            .arrivalDate(LocalDate.parse(LocalDate.now().plusDays(1).toString()))
            .departureDate(LocalDate.parse(LocalDate.now().plusDays(4).toString()))
            .ratesPerNight(ratePerNightList)
            .build())
        .reservationId(RESERVATION_ID)
        .build();
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdList)
        .currencyCode("USD")
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestOnlyStayDatesRequest() {

    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("DE")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id(RESERVATION_ID)
        .build();

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay("2023-04-24",
        "2023-04-27", null, null);

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(resType)
        .roomStay(roomStay)
        .build();
    ReservationByBasketRefResponse tempReservations = ReservationByBasketRefResponse.builder()
            .reservationByIdList(List.of(ReservationById.builder()
                    .reservationId(RESERVATION_ID)
                            .roomStay(RoomStay.builder()
                                    .sourceCode(null)
                                    .ratesPerNight(List.of(RatePerNight.builder()
                                                    .startDate("2023-04-24")
                                                    .pricePerNight(BigDecimal.valueOf(87))
                                            .build()))
                                    .ratePlanCode("SEMIFLEX")
                                    .roomType("DOUBLE")
                                    .adultCount(0)
                                    .childCount(0)
                                    .build())
                            .rateInfo(RateInfo.builder().summary(RateInfoSummary.builder().currencyCode("USD")
                                    .build())
                                    .build())
                    .build()))
            .build();
    Map<String, String> linkBetweenReservations = new HashMap<>();
    linkBetweenReservations.put("454657", RESERVATION_ID);

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
        .tempReservations(tempReservations)
        .linkAmendReservations(linkBetweenReservations)
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestCurrentDates() {

    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("DE")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id(RESERVATION_ID)
        .build();

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in
        .RoomStay(LocalDate.now().plusDays(1).toString(),
        LocalDate.now().plusDays(3).toString(), null, null);

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(resType)
        .roomStay(roomStay)
        .build();
    ReservationByBasketRefResponse tempReservations = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationById.builder()
            .reservationId(RESERVATION_ID)
            .roomStay(RoomStay.builder()
                .sourceCode(null)
                .ratesPerNight(List.of(RatePerNight.builder()
                    .startDate(LocalDate.now().plusDays(1).toString())
                    .pricePerNight(BigDecimal.valueOf(87))
                    .build()))
                .ratePlanCode("SEMIFLEX")
                .roomType("DOUBLE")
                .adultCount(0)
                .childCount(0)
                .build())
            .rateInfo(RateInfo.builder().summary(RateInfoSummary.builder().currencyCode("USD")
                    .build())
                .build())
            .build()))
        .build();
    Map<String, String> linkBetweenReservations = new HashMap<>();
    linkBetweenReservations.put("454657", RESERVATION_ID);

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
        .tempReservations(tempReservations)
        .linkAmendReservations(linkBetweenReservations)
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestOnlyStayDatesRequestNoBookingChannel() {

    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id(RESERVATION_ID)
        .build();

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay("2023-04-24",
        "2023-04-27", null, null);

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(resType)
        .roomStay(roomStay)
        .build();
    ReservationByBasketRefResponse tempReservations = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationById.builder()
            .reservationId("1268956")
            .roomStay(RoomStay.builder()
                .sourceCode(null)
                .ratesPerNight(List.of(RatePerNight.builder()
                    .startDate("2023-04-24")
                    .pricePerNight(BigDecimal.valueOf(87))
                    .build()))
                .ratePlanCode("SEMIFLEX")
                .roomType("DOUBLE")
                .adultCount(0)
                .childCount(0)
                .build())
            .rateInfo(RateInfo.builder().summary(RateInfoSummary.builder().currencyCode("USD")
                    .build())
                .build())
            .build()))
        .build();
    Map<String, String> linkBetweenReservations = new HashMap<>();
    linkBetweenReservations.put("454657", RESERVATION_ID);

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
        .tempReservations(tempReservations)
        .linkAmendReservations(linkBetweenReservations)
        .build();
  }

  private List<RoomRate> createRoomRatesWithOverlapInFuture() {
    List<RoomRate> currentRoomRates = new ArrayList<>();
    currentRoomRates.add(RoomRate.builder()
            .rates(RateType.builder()
                    .rate(List.of(AmountType.builder()
                            .base(TotalType.builder()
                                    .amountBeforeTax(BigDecimal.valueOf(87))
                                    .currencyCode("USD")
                                    .build())
                            .start("2023-04-24")
                            .end("2023-04-24")
                            .build()))
                    .build())
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-24")
            .endDate("2023-04-24")
            .fixedRate(true)
            .build());

    currentRoomRates.add(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-25")
            .endDate("2023-04-25")
            .fixedRate(false)
            .build());

    currentRoomRates.add(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-26")
            .endDate("2023-04-26")
            .fixedRate(false)
            .build());

    return currentRoomRates;
  }

  private List<RoomRate> createRoomRatesWithOverlapInPast() {
    List<RoomRate> currentRoomRates = new ArrayList<>();
    currentRoomRates.add(RoomRate.builder()
            .rates(RateType.builder()
                    .rate(List.of(AmountType.builder()
                            .base(TotalType.builder()
                                    .amountBeforeTax(BigDecimal.valueOf(87))
                                    .currencyCode("USD")
                                    .build())
                            .start("2023-04-24")
                            .end("2023-04-24")
                            .build()))
                    .build())
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-24")
            .endDate("2023-04-24")
            .fixedRate(true)
            .build());

    currentRoomRates.add(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-21")
            .endDate("2023-04-21")
            .fixedRate(false)
            .build());

    currentRoomRates.add(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-22")
            .endDate("2023-04-22")
            .fixedRate(false)
            .build());

    currentRoomRates.add(RoomRate.builder()
            .roomOccupancy(RoomOccupancy.builder()
                    .adultCount(0)
                    .childCount(0)
                    .build())
            .roomType("DOUBLE")
            .ratePlanCode("SEMIFLEX")
            .startDate("2023-04-23")
            .endDate("2023-04-23")
            .fixedRate(false)
            .build());

    return currentRoomRates;
  }

  private BookerDetailsCnpRequest createbookerDetailsCnpRequest() {
    return BookerDetailsCnpRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetailsCnp.builder()
            .emailAddress("test@whitbread.com")
            .build()
        ).build();
  }

  private BillingAddressRequest updateBillingAddressRequest() {

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetails.builder()
            .firstName("Emma")
            .lastName("Watson")
            .address(address)
            .build()
        ).build();
  }

  private BillingAddressRequest updateBillingAddressCcuiRequest() {

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .paymentOption("ACCOUNT_COMPANY")
        .reservationIds(List.of("1234"))
        .booker(BookerDetails.builder()
            .firstName("Emma")
            .lastName("Watson")
            .address(address)
            .build()
        ).build();
  }

  private RoomRate createRoomRate (BigDecimal amount, String startDate, String endDate) {
    return RoomRate.builder()
        .roomOccupancy(RoomOccupancy.builder()
            .adultCount(0)
            .childCount(0)
            .build())
        .roomType("DOUBLE")
        .ratePlanCode("SEMIFLEX")
        .rates(RateType.builder()
            .rate(List.of(
                AmountType.builder()
                    .base(TotalType.builder()
                        .amountBeforeTax(amount)
                        .currencyCode("USD").build())
                    .start(startDate)
                    .end(endDate)
                    .build()
            ))
            .build())
        .startDate(startDate)
        .endDate(endDate)
        .fixedRate(true)
        .build();
  }

    @ParameterizedTest
    @CsvSource({
            ", 01, false",
            "WB_DIGITAL, 35, true",
            "WB_DIGITAL, 10, false",
    })
    void isThirdPartyBookingTest(String idContext, String sourceCode, boolean expected) {
        FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking())).thenReturn(true);
        boolean actual = hotelReservationInPort.is3rdPartyBooking(idContext, sourceCode);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "WB_DIGITAL, 01, true",
            "OTHER, 01, false",
            ", 01, false",
            "WB_DIGITAL, , false",
            "WB_DIGITAL, 35, false",
    })
    void isDigitalBookingTest(String channel, String siteId, boolean expected) {
        boolean actual = hotelReservationInPort.isDigitalBooking(channel, siteId);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "null, 01, false",
            "null, 31, false",
            "WB_DIGITAL, 01, false",
            "WB_DIGITAL, 31, false",
            "null, null, false",
            "null, 99, false",
    })
    void isDesktopBookingTest(String channel, String siteId, boolean expected) {
        boolean actual = hotelReservationInPort.isDesktopBooking(channel, siteId);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "WB_DIGITAL, 35, true",
            "WB_DIGITAL, 38, true",
            "WB_DIGITAL, 43, true",
            "OTHER, 35, false",
            "null, 35, false",
            "WB_DIGITAL, null, false",
            "WB_DIGITAL, 01, false"
    })
    void isDistributionBookingTest(String channel, String siteId, boolean expected) {
        boolean actual = hotelReservationInPort.isDistributionBooking(channel, siteId);
        assertEquals(expected, actual);
    }

    @Test
    void getReservationsByIdsTest_ThirdParty_Success() {
        final String hotelId = "TKINPT";
        final Set<String> reservationIds = Collections.singleton("12345");
        FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking())).thenReturn(true);
        DepositPolicies dpA = createDepositPolicies(new BigDecimal("100"), new BigDecimal("0"), "POLICY_A");
        DepositPolicies dpB = createDepositPolicies(new BigDecimal("200"), new BigDecimal("0"), "POLICY_B");

        ReservationById res1 = createReservations(Collections.singletonList(dpA));
        RoomStay rs1 = RoomStay.builder()
                .sourceCode("35")
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(1))
                .build();
        res1.setRoomStay(rs1);

        ReservationById res2 = createReservations(Collections.singletonList(dpB));
        RoomStay rs2 = RoomStay.builder()
                .sourceCode("35")
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(1))
                .build();
        res2.setRoomStay(rs2);

        ReservationByBasketRefResponse basket = ReservationByBasketRefResponse.builder()
                .idContext("WB_DIGITAL")
                .currencyCode("GBP")
                .reservationByIdList(List.of(res1, res2))
                .build();

        when(hotelReservationOutPort.getReservationsByIds(
                eq(hotelId), eq(reservationIds), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(basket);

        ReservationByBasketRefResponse result =
                hotelReservationInPort.getReservationsByIds(hotelId, reservationIds, false, false, false);

        assertNotNull(result);
        assertEquals("WB_DIGITAL", result.getIdContext());
        assertEquals("POLICY_A", result.getPolicyCode());
    }

    @Test
    void getReservationsByIdsTest_FlagDisabled_ShouldThrow() {
        final String hotelId = "TKINPT";
        final Set<String> reservationIds = Collections.singleton("12345");

        FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking())).thenReturn(false); // disabled

        DepositPolicies dpA = createDepositPolicies(new BigDecimal("100"), BigDecimal.ZERO, "POLICY_A");
        DepositPolicies dpB = createDepositPolicies(new BigDecimal("200"), BigDecimal.ZERO, "POLICY_B");

        ReservationById res1 = createReservations(Collections.singletonList(dpA));
        RoomStay rs1 = RoomStay.builder().sourceCode("35").build();
        res1.setRoomStay(rs1);

        ReservationById res2 = createReservations(Collections.singletonList(dpB));
        RoomStay rs2 = RoomStay.builder().sourceCode("35").build();
        res2.setRoomStay(rs2);

        ReservationByBasketRefResponse basket = ReservationByBasketRefResponse.builder()
                .idContext("WB_DIGITAL")
                .reservationByIdList(List.of(res1, res2))
                .build();

        when(hotelReservationOutPort.getReservationsByIds(
                eq(hotelId), eq(reservationIds), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(basket);

        assertThrows(PolicyCodeMismatchException.class, () ->
                hotelReservationInPort.getReservationsByIds(hotelId, reservationIds, false, false, false)
        );
    }

    @Test
    void saveReservationPreRegister_success() {
      //Arrange
      when(hotelReservationOutPort.saveReservationPreRegister(any())).thenReturn(
              getPreCheckInResponse("Success", "Pre-CheckIn status saved successfully"));
      //Act
      var response = hotelReservationInPort.saveReservationPreRegister(mockPreCheckInRequest());
      //Assert
      assertThat(response, notNullValue());
      verifyNoMoreInteractions(hotelReservationOutPort);
    }

  private UpdateReservationsRequest mockUpdateReservationsRequestForGuestNameUpdate() {
    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id(RESERVATION_ID)
        .build();

    var roomOccupancy = new RoomOccupancy(2, 2);
    var rateType = RateType.builder()
        .rate(List.of(uk.co.whitbread.ohip.domain.model.reservation.in.AmountType.builder()
            .base(uk.co.whitbread.ohip.domain.model.reservation.in.TotalType.builder()
                .amountBeforeTax(new BigDecimal(87))
                .currencyCode("GBP")
                .build())
            .start("2023-04-24")
            .end("2023-04-24")
            .build()))
        .build();

    var roomRate = new RoomRate(rateType, "FMQUAD", "FLEXRATE", roomOccupancy,
        "2023-04-24", "2023-04-24", false);

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay("2023-04-24",
        "2023-04-24", roomOccupancy, List.of(roomRate));

    List<ReservationGuests> reservationGuestsList = new ArrayList<>();
    ProfileType profileType = new ProfileType();
    PersonNameType personName = new PersonNameType();
    personName.setGivenName("UpdatedFirstName");
    personName.setSurname("UpdatedLastName");
    personName.setNameTitle("Mr.");
    personName.setNameType(PersonNameTypeType.PRIMARY);

    CustomerType customerType = new CustomerType();
    customerType.setPersonName(List.of(personName));
    profileType.setCustomer(customerType);

    AddressType addressType = new AddressType();
    addressType.setAddressLine(List.of("line 1", "line 2", "line 3", "line4"));
    AddressInfoType addressInfoType = new AddressInfoType();
    addressInfoType.setAddress(addressType);
    ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    profileType.setAddresses(profileTypeAddresses);

    var reservationGuests = ReservationGuests.builder()
        .profileInfo(ProfileInfo.builder()
            .profile(profileType)
            .build())
        .build();

    reservationGuestsList.add(reservationGuests);

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(resType)
        .roomStay(roomStay)
        .reservationGuests(reservationGuestsList)
        .build();

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
        .tempReservations(null)
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseForGuestNameUpdate() {
    List<ReservationById> reservationByIdList = new ArrayList<>();
    List<RatePerNight> ratePerNightList = new ArrayList<>();
    RatePerNight ratePerNight = RatePerNight.builder()
        .pricePerNight(new BigDecimal(87))
        .startDate("2023-04-24").build();

    ratePerNightList.add(ratePerNight);
    List<ReservationGuest> reservationGuestList = new ArrayList<>();
    ReservationGuest reservationGuest = ReservationGuest.builder()
        .type("Primary")
        .givenName("OriginalFirstName")
        .surname("OriginalLastName")
        .build();
    reservationGuestList.add(reservationGuest);
    ReservationById reservationById = ReservationById.builder()
        .roomStay(RoomStay.builder()
            .arrivalDate(LocalDate.parse("2023-04-24"))
            .departureDate(LocalDate.parse("2023-04-24"))
            .adultCount(2)
            .childCount(2)
            .ratesPerNight(ratePerNightList)
            .roomType("FMQUAD")
            .ratePlanCode("FLEXRATE")
            .build())
        .reservationGuestList(reservationGuestList)
        .reservationBooker(ReservationBooker.builder()
            .address(ReservationBookerAddress.builder()
                .addressLine1("SAID BLOCK")
                .addressLine2("NEW ARENA Layout")
                .addressLine3("JADERAN")
                .cityName("LONDON")
                .postalCode("SWA 1AA")
                .countryCode("GB").build()).build())
        .rateInfo(RateInfo.builder()
            .summary(RateInfoSummary.builder()
                .details(List.of(RateInfoDetails.builder()
                    .net(new BigDecimal(87))
                    .currencyCode("GBP")
                    .summaryDate("2023-04-24")
                    .build()))
                .currencyCode("GBP")
                .build())
            .build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder()
                .code("UK")
                .build())
            .build())
        .reservationId(RESERVATION_ID)
        .build();
    reservationByIdList.add(reservationById);
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdList).build();
  }
}
