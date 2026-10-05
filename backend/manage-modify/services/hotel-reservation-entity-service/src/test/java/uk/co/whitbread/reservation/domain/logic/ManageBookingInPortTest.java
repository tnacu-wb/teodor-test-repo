package uk.co.whitbread.reservation.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.*;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockCdhSearchBookingsResponse;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockFindBookingRequestByOperaConfirmation;
import static uk.co.whitbread.reservation.domain.logic.utils.UserDefinedFieldsConstants.USER_ACCOUNT_ID_UDFC_35;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient.BASKET_EXCEPTION_MSG;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.util.Pair;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentInfoDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils;
import uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils;
import uk.co.whitbread.reservation.domain.logic.utils.TokenUtils;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.Booker;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.Classifications;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.ExternalReferenceType;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePlan;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.RulesAmendmentResponse;
import uk.co.whitbread.reservation.domain.model.out.SearchBooking;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.UserDefinedFields;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.CdhSearchBookingOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.BusinessBookerConfigProperties;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalNotFoundException;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@ExtendWith(MockitoExtension.class)
class ManageBookingInPortTest {

  private static final String BASKET_REFERENCE = "TST-16a014c5-d8a4-4418-8d15-2537660f08e9";
  private static final String BOOKING_REFERENCE = "TST1234567";
  private static final String OTA_REFERENCE = "1111234567";
  private static final String HOTEL_ID = "TESTHOTEL";
  private static final String POLICY_CODE = "code";
  public static final String BOOKING_REFERENCE_2 = "TEST123456";
  public static final String OPERA_CONFIRMATION = "12345678";

  public static final String HOTEL_ID_OPERA_CONFIRMATION = "ABCDEF12345678";
  private static final String PAYMENT_REFERENCE = "3CPReference";
  private static final String BASKET_REF = "basket";
  private static final String TEST_HOTEL_ID = "TestHotelId";
  private static final String RESERVATION_ID_12345 = "12345";
  private static final String USER_DATE_TIME = "2022-09-15T07:47:19 00:00";
  private static final String TIME_ZONE_LONDON = "Europe/London";
  private static final String COUNTRY_CODE_GB = "GB";
  private static final String TOKEN = "token";
  private static final String EMPLOYEE_RATE_PLAN = "EMPLOYEE";
  private static final String TRAVEL_INDUSTRY_RATE_PLAN = "FCDNLR30";
  private static final String FLEX = "FLEX";

  @InjectMocks
  private ManageBookingInPortImpl manageBookingInPort;

  @Mock
  private HotelReservationOhipOutPort reservationOutPort;

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private RulesOutPort rulesOutPort;
  @Mock
  private BusinessBookerConfigProperties businessBookerConfigProperties;
  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private ContentOutPort contentOutPort;
  @Mock
  private AmendLogicInPort amendLogicInPort;
  @Mock
  private CdhSearchBookingOutPort cdhSearchBookingOutPort;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private ThirdpartyBookingProperties otaBookingProperties;

  @Mock
  private ManageBookingLogic manageBookingLogic;

  @Mock
  private CheckInOnlineLogic checkInOnlineLogic;
  @Mock
  private CheckOutOnlineLogic checkOutOnlineLogic;
  @Mock
  private DigitalKeyFeature digtialKeyLogic;

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__ShouldReturnOk(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_ROOM_STAY_RATES_INVALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsCancellable(), is(true));
      assertThat(cancelInformation.isCheckInOnlineAvailable(), is(false));
      assertThat(cancelInformation.isCheckOutOnlineAvailable(), is(false));
      assertEquals(DIGITAL_CIOL_ROOM_STAY_RATES_INVALID,cancelInformation.getCiolErrorLabelKey());
    }
  }


  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__NegotiatedRates__ShouldReturnAmendableFalse(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(mockNegotiatedRatePlans());
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
              Set.of("PBF", "BMD", "BFL"));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsCancellable(), is(true));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__CheckinReservation__ShouldReturnAmendableAndCancelableFalse(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      ReservationByBasketRefResponse reservationByBasketRefResponse = ManageReservationUtils.mockReservationByBasketRefResponse();
      reservationByBasketRefResponse.getReservationByIdList().get(0).setReservationStatus("InHouse");
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservationByBasketRefResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);


      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "Cancelled, true, true",
      "Cancelled, true, false",
      "Cancelled, false, true",
      "Cancelled, false, false",
      "CheckedOut, true, true",
      "CheckedOut, true, false",
      "CheckedOut, false, true",
      "CheckedOut, false, false",
      "NoShow, true, true",
      "NoShow, true, false",
      "NoShow, false, true",
      "NoShow, false, false"
  })
  void getCancelPolicies__CheckinReservation__UnwantedStatuses_Ccui(String unwantedStatus, Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      ReservationByBasketRefResponse reservationByBasketRefResponse = ManageReservationUtils.mockReservationByBasketRefResponseCancelledStatus();
      reservationByBasketRefResponse.getReservationByIdList().get(0).setReservationStatus(unwantedStatus);
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservationByBasketRefResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "Cancelled, true, true",
      "Cancelled, true, false",
      "Cancelled, false, true",
      "Cancelled, false, false",
      "CheckedOut, true, true",
      "CheckedOut, true, false",
      "CheckedOut, false, true",
      "CheckedOut, false, false",
      "NoShow, true, true",
      "NoShow, true, false",
      "NoShow, false, true",
      "NoShow, false, false"
  })
  void getCancelPolicies__CheckinReservation__UnwantedStatuses_Bb(String unwantedStatus, Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      ReservationByBasketRefResponse reservationByBasketRefResponse = ManageReservationUtils.mockReservationByBasketRefResponseCancelledStatus();
      reservationByBasketRefResponse.getReservationByIdList().get(0).setReservationStatus(unwantedStatus);
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservationByBasketRefResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN, mockBbBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__Busiflex__ShouldReturnAmendableTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(mockBusiflexRatePlans());
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
              Set.of("PBF", "BMD", "BFL"));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(true));
      assertThat(cancelInformation.getIsCancellable(), is(true));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_AccountCompanyPayment_ShouldReturnOk(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(ACCOUNT_COMPANY);
      var reservationByBasketRef = ReservationByBasketRefResponse.builder()
              .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
              .build();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(1));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(1));
      }

      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(false));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(),
              true, reservationByBasketRef);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsAmendable(), is(false));
    }
  }

  @MockitoSettings(strictness = Strictness.LENIENT)
  @ParameterizedTest
  @CsvSource({
      "false, false, false, false",
      "false, false, false, true",
      "true, true, false, false",
      "true, true, false, true",
      "false, false, true, false",
      "false, false, true, true",
      "true, true, true, false",
      "true, true, true, true"
  })
  void getCancelPolicies_AccountCompanyPayment_isCancellable_FeatureFlag(boolean isBookingCancellable,
      boolean isCancellableExpectedValue, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    var basket = mockBasketForCancelResponse();
    basket.setPaymentOption(ACCOUNT_COMPANY);
    basket.setChannel("CCUI");
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(
            List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();

    when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
    when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
        ManageReservationUtils.mockRatePlans());
    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
        new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(new RulesAmendmentResponse(true));
    when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));
    

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(1)
                .maxRoomsAmend(1)
                .maxRooms(1)
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(1)
                .maxRooms(1)
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(1));
      when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(1));
    }

    when(reservationOutPort
        .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
            USER_DATE_TIME))
        .thenReturn(new CancelInformationResponse(isBookingCancellable));

    //Act
    var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
        BASKET_REF, USER_DATE_TIME, TOKEN,
        new BookingChannel("CCUI", "WEB", "DE"),
        false, reservationByBasketRef);

    //Assert
    assertNotNull(cancelInformation);
    assertThat(cancelInformation.getIsCancellable(), is(isCancellableExpectedValue));
  }

  @MockitoSettings(strictness = Strictness.LENIENT)
  @ParameterizedTest
  @CsvSource({
      "false,false,PI,true,true",
      "false,false,PI,true,false",
      "false,false,PI,false,true",
      "false,false,PI,false,false",
      "false,false,BB,true,true",
      "false,false,BB,true,false",
      "false,false,BB,false,true",
      "false,false,BB,false,false"
  })
  void getCancelPolicies_AccountCompanyPayment_isNotCancellable_and_isNotAmendable_PI_or_BB(boolean isBookingCancellable,
               boolean isCancellableExpectedValue, String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    var basket = mockBasketForCancelResponse();
    basket.setPaymentOption(ACCOUNT_COMPANY);
    basket.setChannel(channel);
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
            .build();

    when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
    when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
            ManageReservationUtils.mockRatePlans());
    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
            new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(new RulesAmendmentResponse(true));
    when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(1)
                .maxRoomsAmend(1)
                .maxRooms(1)
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(1)
                .maxRooms(1)
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(1));
      when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(1));
    }

    when(reservationOutPort
            .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                    USER_DATE_TIME))
            .thenReturn(new CancelInformationResponse(isBookingCancellable));

    //Act
    var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
            BASKET_REF, USER_DATE_TIME, TOKEN,
            new BookingChannel(channel, "WEB", "DE"),
            false, reservationByBasketRef);

    //Assert
    assertNotNull(cancelInformation);
    assertThat(cancelInformation.getIsCancellable(), is(isCancellableExpectedValue));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_AccountCompanyPayment_ShouldReturnAmendableFalse(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(ACCOUNT_COMPANY);
      basket.setChannel("CCUI");
      var reservationByBasketRef = ReservationByBasketRefResponse.builder()
          .reservationByIdList(
              List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
          .build();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(1)
                  .maxRoomsAmend(1)
                  .maxRooms(1)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(1)
                  .maxRooms(1)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(1));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(1));
      }

      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(false));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
          ManageReservationUtils.mockBookingChannel(),
          true, reservationByBasketRef);

      //Assert
      assertThat(cancelInformation.getIsAmendable(), is(false));
    }
  }
  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable1_shoudReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: PAY_ON_ARRIVAL | paidAmount: 1+
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable2_shoudReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: PAY_ON_ARRIVAL | paidAmount: 0
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
      reservations.setAmountPaid(BigDecimal.ZERO);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable3_shoudReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: PAY_ON_ARRIVAL | paidAmount: NULL
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
      reservations.setAmountPaid(null);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable4_shoudReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: NULL | paidAmount: 0
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(null);

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
      reservations.setAmountPaid(BigDecimal.ZERO);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable4_ForEmployeeBooking_shouldReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: NULL | paidAmount: 0
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(null);
      var reservations = mockReservationByBasketRefResponseForEmployeeBooking();
      reservations.setAmountPaid(BigDecimal.ZERO);
      reservations.getReservationByIdList().get(0).getRoomStay().setRatePlanCode("EMPLOYEE");
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(2));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable1_shoudReturnFalse(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockBbBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable2_shouldReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var payNowBasket = mockBasketForCancelResponse();
      payNowBasket.setPaymentOption(PaymentOption.PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(payNowBasket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(basketOutPort.getCharges(anyString())).thenReturn(
              DepositFoliosResponse.builder().build());
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable3_shouldReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(null);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));


      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable4_shoudReturnFalse(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var reserveWithoutCardBasket = mockBasketForCancelResponse();
      reserveWithoutCardBasket.setPaymentOption(RESERVE_WITHOUT_CARD);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(reserveWithoutCardBasket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable5_shouldReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var reserveWithoutCardBasket = mockBasketForCancelResponse();
      reserveWithoutCardBasket.setPaymentOption(RESERVE_WITHOUT_CARD);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(reserveWithoutCardBasket);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID,
              Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(),
              true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true, false",
      "true, false, false",
      "false, true, false",
      "false, false, false",
      "true, true, true",
      "true, false, true",
      "false, true, true",
      "false, false, true",
  })
  void getCancelPolicies_isAmendable6_shouldReturnTrue(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf, Boolean isOtaFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);
      var payNowBasket = mockBasketForCancelResponse();
      payNowBasket.setPaymentOption(PAY_NOW);
      var bookingChannel = mockCcuiBookingChannel();
      if (isOtaFf) {
        var mockedOtaFf = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedOtaFf);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileAcceptsOtaBooking())).thenReturn(
            true);
        payNowBasket.setIdContext("3rd Party");
        when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));

        bookingChannel.setChannel("PI");
        bookingChannel.setSubchannel("MOBILE");
      }else{
        when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      }
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(payNowBasket);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));


      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(
            maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      if (isOtaFf) {
        assertFalse(cancelInformation.getIsAmendable());
      } else {
        assertTrue(cancelInformation.getIsAmendable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_isAmendable8_shouldReturnTrue(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    // paymentOption: PAY_NOW | paidAmount: 0
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = mockBasketForCancelResponse();
      basket.setPaymentOption(PAY_NOW);

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
      reservations.setAmountPaid(BigDecimal.ZERO);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));


      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(basketOutPort.getCharges(anyString())).thenReturn(
              DepositFoliosResponse.builder().build());
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.isCheckInOnlineAvailable());
      assertFalse(cancelInformation.isCheckOutOnlineAvailable());
      assertEquals(DIGITAL_CIOL_DATA_NOT_VALID,cancelInformation.getCiolErrorLabelKey());

    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__EmployeeRatePlan(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());

      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(mockReservationByBasketRefResponseEmployeeRate());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));


      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(3)
                  .maxRoomsAmend(1)
                  .maxRooms(1)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(3)
                  .maxRooms(1)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(1));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(3));
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(Boolean.FALSE));
      when(rulesOutPort
              .isBookingAmendable(eq(HotelReservationConstants.EMPLOYEE), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.FALSE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(false));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_ShouldReturnIsAmendableForRWCC(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForAmendRWCCResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));


      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(true));
    }
  }

  @Test
  void getCancelPolicies_ShouldReturnExceptionForInvalidToken() {
    // Arrange

    // Act and assert
    Assertions.assertThrows(InvalidTokenException.class, () -> manageBookingInPort
            .getManageBookingInformation(TEST_HOTEL_ID, BASKET_REF, USER_DATE_TIME,
                    "invalid token", ManageReservationUtils.mockBookingChannel(), true, null));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_tokenNotValidatedIfUserAuthenticated(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                  USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(), true, null);

      //Assert
      tokenUtilsMock.verify(() -> TokenUtils.isValid(anyString(), anyString()), times(0));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelCcuiAndReservationWasOverridden(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertThat(cancelInformation.getIsCancellable(), is(true));
      assertThat(cancelInformation.getIsAmendable(), is(true));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelCcuiAndReservationWasOverriddenAndCancelResponseFalse(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
          .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(
          new CancelInformationResponse(false));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
          when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
          mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
          anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertTrue(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelCcuiAndReservationWasOverriddenPayNow(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById("basket")).thenReturn(basketResponse);
      var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
      when(reservationOutPort
          .getReservationsByIds("TestHotelId", Collections.singletonList("12345"), false))
          .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse("Europe/London", "GB"));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(reservationOutPort.getCancelInformation("TestHotelId", Collections.singleton("12345"),
          "2022-09-15T07:47:19 00:00")).thenReturn(
          new CancelInformationResponse(true));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation("TestHotelId",
          "basket", "2022-09-15T07:47:19 00:00", "token",
          mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
          anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertTrue(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "GB, true, true",
      "GB, true, false",
      "GB, false, true",
      "GB, false, false",
      "DE, true, true",
      "DE, true, false",
      "DE, false, true",
      "DE, false, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndNonFlex(String hotelCountryCode, Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseNonFlex();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, hotelCountryCode));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "GB, true, true",
      "GB, true, false",
      "GB, false, true",
      "GB, false, false",
      "DE, true, true",
      "DE, true, false",
      "DE, false, true",
      "DE, false, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndFlex(String hotelCountryCode, Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseFlex();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
                      RESERVATION_ID_12345),
              USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, hotelCountryCode));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndRandomRatePlanCode(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseRandomRatePlanCode();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
                      RESERVATION_ID_12345),
              USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "GB, true, true",
      "GB, true, false",
      "GB, false, true",
      "GB, false, false",
      "DE, true, true",
      "DE, true, false",
      "DE, false, true",
      "DE, false, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndSemiFlex(String hotelCountryCode, Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseSemiFlex();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
                      RESERVATION_ID_12345),
              USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, hotelCountryCode));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      var mockedCcuiAmendPiba = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getCcuiAmendPiba()).thenReturn(mockedCcuiAmendPiba);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "GB, true, true",
      "GB, true, false",
      "GB, false, true",
      "GB, false, false",
      "DE, true, true",
      "DE, true, false",
      "DE, false, true",
      "DE, false, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndAdvance(String hotelCountryCode, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseAdvance();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
                      RESERVATION_ID_12345),
              USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, hotelCountryCode));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      var mockedFeatureCcuiAmendPiba = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getCcuiAmendPiba()).thenReturn(mockedFeatureCcuiAmendPiba);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba())).thenReturn(true);
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "GB, true",
      "GB, false",
      "DE, true",
      "DE, false"
  })
  void getCancelPolicies_whenCcuiReservationPayNowAndStandard(String hotelCountryCode, Boolean aemSearchRulesFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba())).thenReturn(true);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      basketResponse.setPaymentOption(PAY_NOW);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponseStandard();
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));


      if (aemSearchRulesFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(364)
                .maxRoomsAmend(9)
                .maxRooms(9)
                .build());
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
                      RESERVATION_ID_12345),
              USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, hotelCountryCode));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertTrue(cancelInformation.getIsCancellable());
      assertFalse(cancelInformation.getIsAmendable());
      assertTrue(cancelInformation.getIsRuleCompliant());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelCcuiAndReservationWasNotOverridden(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
      when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID,
              Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservation);
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(
              RESERVATION_ID_12345),
          USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(false));
      when(reservationOutPort.getRatePlans(anyList(), eq(TEST_HOTEL_ID))).thenReturn(
              ManageReservationUtils.mockRatePlans());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
              Collections.singleton("PBF"));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(),
              anyString())).thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      //Act
      reservation.getReservationByIdList().get(0).setReservationOverridden(false);
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
      verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
              anyString());

      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(true));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenCcuiReservationWithPibaAndFeatureToggleEnabled_thenIsAmendableIsTrue(
      Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      when(basketOutPort.getBasketById(BASKET_REF))
          .thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponsePIBA();
      when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(
          RESERVATION_ID_12345), false))
          .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),
          any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any()))
          .thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
          USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString()))
          .thenReturn(new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      var mockedFeatureCcuiAmendPiba = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getCcuiAmendPiba()).thenReturn(mockedFeatureCcuiAmendPiba);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertTrue(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenCcuiReservationWithPibaAndFeatureToggleDisabled_thenIsAmendableIsFalse(
      Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      BasketResponse basketResponse = mockBasketForCancelResponse();
      when(basketOutPort.getBasketById(BASKET_REF))
          .thenReturn(basketResponse);
      var reservation = mockReservationByBasketRefResponsePIBA();
      when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
          .thenReturn(reservation);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRoomsAmend(9)
                  .maxRooms(9)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(364)
                  .maxRooms(9)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(9));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(364));
      }

      when(reservationOutPort.getRatePlans(any(), any()))
          .thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
          USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getHotelInformation(anyString()))
          .thenReturn(new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

      var mockedFeatureCcuiAmendPiba = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getCcuiAmendPiba()).thenReturn(mockedFeatureCcuiAmendPiba);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba())).thenReturn(false);
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(), true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelBBAndUserIsNotSameAsBooker(Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(ManageReservationUtils.mockCurrentUserAccount(null));
    when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
    when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(
        RESERVATION_ID_12345), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
    when(reservationOutPort
            .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                USER_DATE_TIME))
            .thenReturn(new CancelInformationResponse(false));
    when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRoomsAmend(4)
                .maxRooms(4)
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRooms(4)
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(14));
    }

    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
            new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));

    //Act
    var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
        BASKET_REF, USER_DATE_TIME, "",
            mockBbBookingChannel(), true, null);

    //Assert
    assertNotNull(cancelInformation);
    verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
    verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
            anyString());

    assertThat(cancelInformation.getIsCancellable(), is(false));
    assertThat(cancelInformation.getIsAmendable(), is(false));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelBBAndUserIsSameAsBookerAndRateIsNotNegociated(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    var authenticatedUser = ManageReservationUtils.mockCurrentUserAccount("test@wb.com");
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(authenticatedUser);
    when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
    var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservation.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getUserDefinedFields)
        .map(UserDefinedFields::getCharacterUDFs)
        .flatMap(Collection::stream)
        .filter(udf -> udf.getName().equals(USER_ACCOUNT_ID_UDFC_35))
        .forEach(udf -> udf.setValue(authenticatedUser.get().getEmployeeId()));
    when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(
        RESERVATION_ID_12345), false)).thenReturn(reservation);
    when(reservationOutPort
            .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                USER_DATE_TIME))
            .thenReturn(new CancelInformationResponse(false));
    when(reservationOutPort.getRatePlans(anyList(), eq(TEST_HOTEL_ID))).thenReturn(ManageReservationUtils.mockRatePlans());
    when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
            Collections.singleton("PBF"));
    when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRoomsAmend(4)
                .maxRooms(4)
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRooms(4)
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(14));
    }

    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
            new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(new RulesAmendmentResponse(false));

    //Act
    var manageBookingResponse = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
        BASKET_REF, USER_DATE_TIME, "",
            mockBbBookingChannel(), true, null);

    //Assert
    assertNotNull(manageBookingResponse);
    verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
    verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
            anyString());

    assertThat(manageBookingResponse.getIsCancellable(), is(false));
    assertThat(manageBookingResponse.getIsAmendable(), is(false));
    assertThat(manageBookingResponse.getIsRuleCompliant(), is(true));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenChannelBBAndUserIsSameAsBookerAndRateIsNegociated(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    //Arrange
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    var authenticatedUser = ManageReservationUtils.mockCurrentUserAccount("test@wb.com");
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(authenticatedUser);
    when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
    var reservation = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservation.getReservationByIdList().stream()
            .map(ReservationByIdResponse::getUserDefinedFields)
            .map(UserDefinedFields::getCharacterUDFs)
            .flatMap(Collection::stream)
            .filter(udf -> udf.getName().equals(USER_ACCOUNT_ID_UDFC_35))
            .forEach(udf -> udf.setValue(authenticatedUser.get().getEmployeeId()));
    when(reservationOutPort.getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(
        RESERVATION_ID_12345), false)).thenReturn(reservation);
    when(reservationOutPort
            .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                USER_DATE_TIME))
            .thenReturn(new CancelInformationResponse(false));
    when(reservationOutPort.getRatePlans(anyList(), eq(TEST_HOTEL_ID))).thenReturn(ManageReservationUtils.mockRatePlans());
    when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
            Collections.singleton("NEG"));
    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
            new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort
            .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
    ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
    when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRoomsAmend(4)
                .maxRooms(4)
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any()))
            .thenReturn(SearchRules.builder()
                .maxNights(14)
                .maxRooms(4)
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(14));
    }

    when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
            new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
    when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(new RulesAmendmentResponse(true));

    //Act
    var manageBookingResponse = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
        BASKET_REF, USER_DATE_TIME, "",
            mockBbBookingChannel(), true, null);

    //Assert
    assertNotNull(manageBookingResponse);
    verify(reservationOutPort, times(1)).getCancelInformation(anyString(), anySet(), anyString());
    verify(rulesOutPort, times(1)).isBookingAmendable(anyString(), anyString(), anyString(),
            anyString());

    assertThat(manageBookingResponse.getIsCancellable(), is(false));
    assertThat(manageBookingResponse.getIsAmendable(), is(true));
    assertThat(manageBookingResponse.getIsRuleCompliant(), is(true));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies_whenErroredBooking_shouldReturnFalse(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = ManageReservationUtils.mockBasketResponse();
      basket.setErroredBooking(true);
      var reservationByBasketRef = ReservationByBasketRefResponse.builder()
              .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
              .build();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
              .thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID, Set.of("res1", "res3", "res2"),
          USER_DATE_TIME)).thenReturn(
              new CancelInformationResponse(true));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN,
              ManageReservationUtils.mockBookingChannel(),
              true, reservationByBasketRef);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsCancellable(), is(false));
      assertThat(cancelInformation.getIsAmendable(), is(false));
      assertThat(cancelInformation.getIsRuleCompliant(), is(false));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_payNow(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);
      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_NOW);
      var bookingChannel = BookingChannel.builder().channel(channel).build();

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(reservations);
      when(rulesOutPort
          .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(2));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }

      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        assertTrue(cancelInformation.isCheckOutOnlineAvailable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, WEB, true, true,,true,NEG1",
      "PI, WEB, true, false,,true,NEG1",
      "PI, WEB, false, true,,true,NEG1",
      "PI, WEB, false, false,,true,NEG1",
      "CCUI, WEB, true, true,,true,NEG1",
      "CCUI, WEB, true, false,,true,NEG1",
      "CCUI, WEB, false, true,,true,NEG1",
      "CCUI, WEB, false, false,,true,NEG1",
      "PI, WEB, true, true,,true,NEG",
      "PI, WEB, true, false,,true,NEG",
      "PI, WEB, false, true,,true,NEG",
      "PI, WEB, false, false,,true,NEG",
      "CCUI, WEB, true, true,,true,NEG",
      "CCUI, WEB, true, false,,true,NEG",
      "CCUI, WEB, false, true,,true,NEG",
      "CCUI, WEB, false, false,,true,NEG",
      "PI, MOBILE, true, true, 3rd Party,true,NEG",
      "PI, MOBILE, true, false, 3rd Party,true,NEG",
      "PI, MOBILE, false, true, 3rd Party,true,NEG",
      "PI, MOBILE, false, false, 3rd Party,true,NEG",
      "CCUI, MOBILE, true, true, 3rd Party,true,NEG",
      "CCUI, MOBILE, true, false, 3rd Party,true,NEG",
      "CCUI, MOBILE, false, true, 3rd Party, true,NEG",
      "CCUI, MOBILE, false, false, 3rd Party,true,NEG",
      "PI, MOBILE, true, true, 3rd Party,true,NEG1",
      "PI, MOBILE, true, false, 3rd Party,true,NEG1",
      "PI, MOBILE, false, true, 3rd Party,true,NEG1",
      "PI, MOBILE, false, false, 3rd Party,true,NEG1",
      "CCUI, MOBILE, true, true, 3rd Party,true,NEG1",
      "CCUI, MOBILE, true, false, 3rd Party,true,NEG1",
      "CCUI, MOBILE, false, true, 3rd Party, true,NEG1",
      "CCUI, MOBILE, false, false, 3rd Party,true,NEG1"
  })
  void getManageBooking_operaUiRsv_multiRoomReservation(String channel, String subchannel,
      Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf, String idContext, Boolean isOtaBookingFf, String negRatePlan) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);
      if (idContext != null) {
        var mockedOTAFeature = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedOTAFeature);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileAcceptsOtaBooking())).thenReturn(
            isOtaBookingFf);
        when(otaBookingProperties.getSubchannel()).thenReturn(Set.of(channel + "." + subchannel));
      }
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
          Set.of(negRatePlan));

      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      basket.setIdContext(idContext);
      var bookingChannel = BookingChannel.builder().channel(channel).subchannel(subchannel).build();

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse3rdParty("rate1",
          "rate2", "rate3", "44");

      reservations.getReservationByIdList().get(0).setOperaLinkedReservation(Boolean.TRUE);
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans("rate1", "rate2", "rate3"));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(reservations);
      if (idContext == null) {
        when(rulesOutPort
            .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
        ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      }
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingLogic.operaUiRsv(basket.getBookingReference())).thenReturn(true);

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(2));
      }

      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        if ("WEB".equalsIgnoreCase(subchannel) && "NEG".equalsIgnoreCase(negRatePlan) && idContext == null) {
          assertFalse(cancelInformation.isCheckOutOnlineAvailable());
        }else{
          assertTrue(cancelInformation.isCheckOutOnlineAvailable());
        }
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_oneGuest(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {

    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      var bookingChannel = BookingChannel.builder().channel(channel).build();

      var reservationByIdResponse = ManageReservationUtils.mockReservationByIdFlex("11111111");

      reservationByIdResponse.getRoomStay().setAdultsNumber(1);
      var reservations = ReservationByBasketRefResponse.builder()
          .amountPaid(BigDecimal.valueOf(30))
          .totalCost(BigDecimal.TEN)
          .newTotal(BigDecimal.TEN)
          .previousTotal(BigDecimal.TEN)
          .balanceOutstanding(BigDecimal.TEN)
          .hotelId(HOTEL_ID)
          .policyCode(POLICY_CODE)
          .currencyCode("GBP")
          .reservationByIdList(
              List.of(reservationByIdResponse))
          .build();
      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);

        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(10)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(10)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(10));
      }

      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
          .thenReturn(reservations);
      when(rulesOutPort
          .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingLogic.operaUiRsv(basket.getBookingReference())).thenReturn(true);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      if("CCUI".equalsIgnoreCase(channel)) {
        assertTrue(cancelInformation.getIsAmendable());
      } else {
        assertTrue(cancelInformation.getIsCancellable());
      }
      assertTrue(cancelInformation.getIsCancellable());
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        assertTrue(cancelInformation.isCheckOutOnlineAvailable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_maxRooms(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {

    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      var bookingChannel = BookingChannel.builder().channel(channel).build();

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponseMoreRooms();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                      USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        assertTrue(cancelInformation.isCheckOutOnlineAvailable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "BB, true, true",
      "BB, true, false",
      "BB, false, true",
      "BB, false, false"
  })
  void getManageBooking_operaUiRsv_checkRules(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);
      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      var bookingChannel = BookingChannel.builder().channel(channel).build();

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponseMoreRooms();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
              .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
                      USER_DATE_TIME))
              .thenReturn(new CancelInformationResponse(true));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(4)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(4)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(4));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
              new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
              .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
              .thenReturn(reservations);
      when(rulesOutPort
              .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
              .thenReturn(Boolean.TRUE);

      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      assertEquals(DASHBOARD_BOOKINGS_ERROR_MAXROOMS,cancelInformation.getAemLabelKey());
      assertTrue(cancelInformation.isCheckInOnlineAvailable());
      assertTrue(cancelInformation.isCheckOutOnlineAvailable());
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_breakfastAllowances(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);
      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      basket.setBookingAllowances(
          List.of(BookingAllowance.builder().allowance("premierInnBreakfast")
              .budget(BigDecimal.valueOf(30))
              .build()));

      var bookingChannel = BookingChannel.builder().channel(channel).build();
      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
      var businessAllowanceRuleResponse = ManageReservationUtils.mockBusinessAllowanceRuleResponse();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(businessAllowanceRuleResponse);
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(reservations);
      when(rulesOutPort
          .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(manageBookingLogic.operaUiRsv(basket.getBookingReference())).thenReturn(true);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(2));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
              BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        assertTrue(cancelInformation.isCheckOutOnlineAvailable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_negRates(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);
      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);

      var bookingChannel = BookingChannel.builder().channel(channel).build();
      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(mockNegotiatedRatePlans());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
          Collections.singleton("BMD"));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(reservations);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingLogic.operaUiRsv(basket.getBookingReference())).thenReturn(true);

      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.FALSE));

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(2));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      assertTrue(cancelInformation.isCheckInOnlineAvailable());
    }
  }


  @ParameterizedTest
  @CsvSource({
      "PI, MOBILE, 3rd Party, 36, true, false",
      "PI, MOBILE, 3rd Party, 36, false, true",
      "PI, WEB, 3rd Party, 36, true, true",
      "PI, WEB, 3rd Party, 01, true, true",
  })
  void getManageBooking_operaUiRsv_OTA_negRates(String channel, String subchannel,
      String idContext, String sourceCode, String isOtaFFEnabled, String isNegRateFLow) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          true);
      var mockedFeatureOta = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedFeatureOta);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileAcceptsOtaBooking())).thenReturn(
          Boolean.parseBoolean(isOtaFFEnabled));
      when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));
      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      basket.setIdContext(idContext);
      var bookingChannel = BookingChannel.builder().channel(channel).subchannel(subchannel).build();
      var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();

      for (ReservationByIdResponse res : reservations.getReservationByIdList()) {
        res.getRoomStay().setSourceCode(sourceCode);
        res.setUserDefinedFields(null);
      }
     if(Boolean.parseBoolean(isNegRateFLow)) {
       when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
           .thenReturn(new RulesAmendmentResponse(Boolean.FALSE));
     }
      when(digtialKeyLogic.isDigitalKeyAvailable(reservations)).thenReturn(false);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(mockNegotiatedRatePlans());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
          Collections.singleton("BMD"));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(reservations);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingLogic.operaUiRsv(basket.getBookingReference())).thenReturn(true);
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));

      when(contentOutPort.getSearchRules(anyString(), any()))
          .thenReturn(SearchRules.builder()
              .maxNights(9)
              .maxRoomsAmend(2)
              .maxRooms(2)
              .build());

      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
      assertFalse(cancelInformation.getIsCancellable());
      assertTrue(cancelInformation.isCheckInOnlineAvailable());
      assertFalse(cancelInformation.isDigitalKey());

    }
  }

  @ParameterizedTest
  @CsvSource({
      "PI, true, true",
      "PI, true, false",
      "PI, false, true",
      "PI, false, false",
      "CCUI, true, true",
      "CCUI, true, false",
      "CCUI, false, true",
      "CCUI, false, false"
  })
  void getManageBooking_operaUiRsv_maxRoomsAmendRule_fail(String channel, Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {

    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(aemSearchRulesFf);

      var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
      var bookingChannel = BookingChannel.builder().channel(channel).build();

      var reservations = ManageReservationUtils.mockReservationByBasketRefResponseMoreRooms();

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
          when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
          when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(maxRoomsAmendFf);
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRoomsAmend(4)
                  .maxRooms(5)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(4)
                  .maxRooms(5)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(5));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(4));
      }

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345), false))
          .thenReturn(reservations);
      when(rulesOutPort
          .isBookingAmendable(anyString(), anyString(), anyString(), anyString())
      ).thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));
      when(checkOutOnlineLogic.isCoolAvailable(any(),any(),any())).thenReturn(true);

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, bookingChannel, true, null);

      //Assert
      assertNotNull(cancelInformation);
      if (maxRoomsAmendFf && aemSearchRulesFf) {
        assertFalse(cancelInformation.getIsAmendable());
        assertTrue(cancelInformation.getIsCancellable());
      } else {
        assertTrue(cancelInformation.getIsAmendable());
        assertTrue(cancelInformation.getIsCancellable());
      }
      if (!channel.equals("CCUI")) {
        assertTrue(cancelInformation.isCheckInOnlineAvailable());
        assertTrue(cancelInformation.isCheckOutOnlineAvailable());
      }
    }
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void getCancelPolicies__TravelIndustryRate__ShouldReturnOk(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      //Arrange
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
          aemSearchRulesFf);

      when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(mockBasketForCancelResponse());
      when(reservationOutPort
          .getCancelInformation(TEST_HOTEL_ID, Collections.singleton(RESERVATION_ID_12345),
              USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));
      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(mockNegotiatedRatePlans());
      when(reservationOutPort
          .getReservationsByIds(TEST_HOTEL_ID, Collections.singletonList(RESERVATION_ID_12345),
              false))
          .thenReturn(
              ManageReservationUtils.mockReservationByBasketRefTravelIndustryRateResponse());
      when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
          Set.of("PBF", "BMD", "BFL"));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      if (aemSearchRulesFf) {
        if (maxRoomsAmendFf) {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRoomsAmend(2)
                  .maxRooms(2)
                  .build());
        } else {
          when(contentOutPort.getSearchRules(anyString(), any()))
              .thenReturn(SearchRules.builder()
                  .maxNights(9)
                  .maxRooms(2)
                  .build());
        }
      } else {
        when(rulesOutPort.getMaxRoomsRule(anyString())).thenReturn(new MaxRoomsRuleResponse(2));
        when(rulesOutPort.getMaxNightsRule(anyString())).thenReturn(new MaxNightsRuleResponse(9));
      }

      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(new RulesAmendmentResponse(Boolean.TRUE));
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(true, ""));

      //Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(TEST_HOTEL_ID,
          BASKET_REF, USER_DATE_TIME, TOKEN, mockCcuiBookingChannel(),
          true, null);

      //Assert
      assertNotNull(cancelInformation);
      assertThat(cancelInformation.getIsAmendable(), is(true));
      assertThat(cancelInformation.isCheckInOnlineAvailable(), is(true));
    }
  }

  @Test
  void checkRateBooking_ShouldReturnEmployeeRate() {
    // Arrange
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(
            ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
                EMPLOYEE_RATE_PLAN)))
        .build();

    // Act
    String ratePlanCode = ManageBookingUtils.checkRateBooking(reservationResponse,
        BASKET_REFERENCE);

    // Assert
    assertEquals(HotelReservationConstants.EMPLOYEE, ratePlanCode);
  }

  @Test
  void checkRateBooking_ShouldReturnTravelIndustryRate() {
    // Arrange
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(
            ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
                TRAVEL_INDUSTRY_RATE_PLAN)))
        .build();

    // Act
    String ratePlanCode = ManageBookingUtils.checkRateBooking(reservationResponse,
        BASKET_REFERENCE);

    // Assert
    assertEquals(TRAVEL_INDUSTRY_RATE_PLAN, ratePlanCode);
  }

  @Test
  void checkRateBooking_ShouldReturnEmptyStringWhenNoMatchingRate() {
    // Arrange
    var reservationResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(
            ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
                "TEST_RATE_PLAN_CODE")))
        .build();

    // Act
    String ratePlanCode = ManageBookingUtils.checkRateBooking(reservationResponse,
        BASKET_REFERENCE);

    // Assert
    assertEquals("", ratePlanCode);
  }

  @Test
  void findBooking_basket_success() {
    //Arrange
    var basket = BasketResponse.builder()
        .bookingReference(BOOKING_REFERENCE)
        .hotelId(HOTEL_ID)
        .reference(BASKET_REFERENCE)
        .items(Collections.singletonList(
            BasketItemResponse.builder()
                .sourceId(BOOKING_REFERENCE)
                .build()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(basket));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.getFindBookingResponse(mockFindBookingRequest(),
        ManageReservationUtils.mockBookingChannel(), null,
        "44", basket)).thenReturn(Optional.of(FindBookingResponse.builder()
        .ref(BOOKING_REFERENCE)
        .sourcePms("Opera")
        .basketReference(BASKET_REFERENCE)
        .build()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSourcePms(), is("Opera"));
    assertThat(response.getRef(), is(BOOKING_REFERENCE));
    verify(basketOutPort, never()).saveCharges(any());
  }


  @ParameterizedTest
  @MethodSource("otaBookingProvider")
  void testGetManageBookingInformation_IsAmendableFlag_When_OtaBooking(
      ReservationByIdResponse operaRes, Set<String> channels, String channel, String subchannel,
      boolean featureFlag, boolean isOta) {
    var searchRules = SearchRules.builder()
        .maxNights(4)
        .maxRoomsAmend(4)
        .maxRooms(4)
        .build();
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      List<ReservationByIdResponse> list =
          operaRes != null ? List.of(operaRes) : Collections.emptyList();
      var reservationByBasketRef = ReservationByBasketRefResponse.builder()
          .reservationByIdList(list)
          .build();
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
      var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(true);
      when(checkInOnlineLogic.isCiolAvailable(any(),any(),anyBoolean(),any())).thenReturn(Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID));
      if (channel == null) {
        when(contentOutPort.getSearchRules(null, Optional.empty())).thenReturn(searchRules);
      } else if (channel == null || subchannel == null) {
        when(contentOutPort.getSearchRules("PI", Optional.empty())).thenReturn(searchRules);
      } else {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(searchRules);
      }

      when(reservationOutPort.getCancelInformation(TEST_HOTEL_ID,
          Collections.singleton(RESERVATION_ID_12345),
          USER_DATE_TIME))
          .thenReturn(new CancelInformationResponse(true));

      when(reservationOutPort.getRatePlans(any(), any())).thenReturn(
          ManageReservationUtils.mockRatePlans());
      when(reservationOutPort.getHotelInformation(anyString())).thenReturn(
          new HotelInformationResponse(TIME_ZONE_LONDON, COUNTRY_CODE_GB));
      var basket = mockBasketForCancelResponse();
      basket.setChannel("PI");
      if (!isOta) {
        when(rulesOutPort.isBookingAmendable(anyString(), anyString(), anyString(),
            anyString())).thenReturn(
            RulesAmendmentResponse.builder().isBookingAmendable(false).build());
        when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);
      } else {
        var mockedFeatureMobileOta = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedFeatureMobileOta);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMobileAcceptsOtaBooking())).thenReturn(
            featureFlag);
        when(otaBookingProperties.getSubchannel()).thenReturn(channels);
        basket.setIdContext("3rd Party");
        when(basketOutPort.getBasketById(BASKET_REF)).thenReturn(basket);

      }
      // Act
      var cancelInformation = manageBookingInPort.getManageBookingInformation(
          TEST_HOTEL_ID,
          BASKET_REF,
          USER_DATE_TIME,
          TOKEN,
          new BookingChannel(channel, subchannel, "EN"),
          true,
          reservationByBasketRef
      );

      // Assert
      assertNotNull(cancelInformation);
      assertFalse(cancelInformation.getIsAmendable());
    }
  }

  static Stream<Arguments> otaBookingProvider() {
    var operaRes = ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");

    var otaRes = ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    otaRes.getRoomStay().setSourceCode("36");
    var otaResIdContext = ManageReservationUtils.mockReservationByIdAndRatePlanCode(
        RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");


    var distrRes = ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    distrRes.getRoomStay().setSourceCode("36");
    distrRes.setUserDefinedFields(UserDefinedFields.builder().characterUDFs(List.of(
        CharacterUDFs.builder().name("UDFC09").value("DISTR").build())).build());

    var mixtRes = ManageReservationUtils.mockReservationByIdAndRatePlanCode(RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    mixtRes.getRoomStay().setSourceCode("90");
    mixtRes.setUserDefinedFields(UserDefinedFields.builder().characterUDFs(List.of(
        CharacterUDFs.builder().name("UDFC09").value("DISTR").build())).build());

    var operaResNullUF = ManageReservationUtils.mockReservationByIdAndRatePlanCode(
        RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    operaResNullUF.setUserDefinedFields(null);

    var operaResNoUF = ManageReservationUtils.mockReservationByIdAndRatePlanCode(
        RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    operaResNoUF.setUserDefinedFields(UserDefinedFields.builder().build());

    var operaResNullSC = ManageReservationUtils.mockReservationByIdAndRatePlanCode(
        RESERVATION_ID_12345,
        "TEST_RATE_PLAN_CODE");
    operaResNullSC.getRoomStay().setSourceCode(null);

    return Stream.of(Arguments.of(operaRes, Set.of("PI.MOBILE"), "PI", "MOBILE", true, false),
        Arguments.of(otaResIdContext, Set.of("PI.MOBILE"), "PI", "MOBILE", true, true),
        Arguments.of(otaRes, Set.of("PI.MOBILE"), "PI", "MOBILE", false, false),
        Arguments.of(distrRes, Set.of("PI.MOBILE"), "PI", "MOBILE", true, false),
        Arguments.of(mixtRes, Set.of("PI.MOBILE"), "PI", "MOBILE", true, false),
        Arguments.of(otaRes, Set.of("PI.WEB"), "PI", "MOBILE", true, false),
        Arguments.of(otaRes, null, "PI", "MOBILE", true, false),
        Arguments.of(otaRes, Set.of("PI.MOBILE"), "PI", null, true, false),
        Arguments.of(otaRes, Set.of("PI.MOBILE"), null, "MOBILE", true, false),
        Arguments.of(operaResNoUF, Set.of("PI.MOBILE"), "PI", "MOBILE", true, false),
        Arguments.of(operaResNullUF, Set.of("PI.MOBILE"), "PI", "MOBILE", true, false),
        Arguments.of(operaResNullSC, Set.of("PI.MOBILE"), "PI", "MOBILE", true, true),
        Arguments.of(operaResNullSC, Set.of("PI.MOBILE"), "PI", "MOBILE", false, false)
    );
  }

  @ParameterizedTest
  @MethodSource("bookingChannelProvider")
  void findOtaBooking_test(BookingChannel channel, Set<String> subchannels,
      ReservationsDetailsEnhancedResponse operaRes, boolean otaFf, boolean isOtaAllowed,
      Set<String> provider) {
    //Arrange
    var req = mockFindOtaBookingRequest();
    var basket = ManageReservationUtils.mockCreateBasketReservationIdContextResponse(OTA_REFERENCE,
        "BOOKING.COM");
    if (operaRes != null) {
      when(otaBookingProperties.getSubchannel()).thenReturn(subchannels);
      when(otaBookingProperties.getProvidersExcluded()).thenReturn(provider);
      when(manageBookingLogic.isOtaFlowEnabled()).thenReturn(otaFf);
      when(reservationOutPort.getReservationsByExternalId(req.getResNo())).thenReturn(operaRes);
      when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
      when(manageBookingLogic.replaceHotelId(OTA_REFERENCE)).thenReturn(OTA_REFERENCE);
    }

    if (isOtaAllowed && operaRes != null) {
      DepositsResponse depositsResponse = new DepositsResponse();
      when(reservationOutPort.getReservationsByExternalId(req.getResNo())).thenReturn(operaRes);
      when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
          .thenReturn(depositsResponse);
      when(basketOutPort.createBasketReservation(any())).thenReturn(basket);
      when(manageBookingLogic.buildReservationFindBookingResponse(req, basket)).thenReturn(
          Optional.of(FindBookingResponse.builder()
              .ref(OTA_REFERENCE)
              .sourcePms("Opera")
              .basketReference(BASKET_REFERENCE)
              .idContext("BOOKING.COM")
              .build()));
      when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));
      //Act
      var response = manageBookingInPort.findBooking(req, channel);
      //Assert
      if (operaRes == null ) {
        assertNull(response);
      } else {
        assertNotNull(response);
        assertEquals("Opera", response.getSourcePms());
        assertEquals(BASKET_REFERENCE, response.getBasketReference());
        assertEquals("BOOKING.COM", response.getIdContext());
        assertEquals(OTA_REFERENCE, response.getRef());
        verify(basketOutPort, never()).saveCharges(any());
      }
    }

    if (!isOtaAllowed) {
      assertThrows(GenericBadRequestException.class, () ->
          manageBookingInPort.findBooking(req, channel));
    }
  }

  static Stream<Arguments> bookingChannelProvider() {
    var otaRes= ManageReservationUtils.mockReservationEnhancedResponse("35");
    var resInfo = otaRes.getReservations().getReservationInfo().get(0);
    resInfo.setExternalReferences(List.of(
        ExternalReferenceType.builder().id(OTA_REFERENCE).idContext("BOOKING.COM").build()));
    var operaResNull = ManageReservationUtils.mockReservationEnhancedResponse("35");
    operaResNull.getReservations().setReservationInfo(Collections.emptyList());
    return Stream.of(Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "WEB"),
            Set.of("PI.MOBILE"), otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("KIOSK", null),
            Set.of("KIOSK"), otaRes, true, true, Set.of("EXPEDIA") ),
        Arguments.of(ManageReservationUtils.mockBookingChannel("KIOSK", null),
            Set.of("KIOSK"), otaRes, true, true, Set.of("TEST", "EXPEDIA") ),
        Arguments.of(ManageReservationUtils.mockBookingChannel("KIOSK", null),
            Set.of("KIOSK"), otaRes, false, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("KIOSK", null),
            Set.of("KIOSK"), otaRes, false, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("PI.MOBILE", "PI.WEB"), otaRes, true, true, Set.of() ),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("PI.MOBILE", "PI.WEB"), otaRes, true, true, null ),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("PI.MOBILE", "PI.WEB"), otaRes, false, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", null),
            Set.of("PI.MOBILE", "PI.WEB"), otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("TEST", null),
            Set.of("PI.MOBILE", "BB.MOBILE"), otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            null, otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel(null, "MOBILE"),
            null, otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel(null, null),
            null, otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", null),
            null, otaRes, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            null, operaResNull, true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("BB.MOBILE", "PI.MOBILE"), null, true, true, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("BB.MOBILE", "PI.MOBILE"), ReservationsDetailsEnhancedResponse.builder().build(),
            true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("BB.MOBILE", "PI.MOBILE"),
            ReservationsDetailsEnhancedResponse.builder().reservations(
                Reservations.builder().build()).build(),
            true, false, null),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"),
            Set.of("BB.MOBILE", "PI.MOBILE"),
            ReservationsDetailsEnhancedResponse.builder().reservations(
                Reservations.builder().reservationInfo(Collections.emptyList()).build()).build(),
            true, false, null)
    );
  }

  @ParameterizedTest
  @CsvSource({"WB_DIGITAL,35,WB_DIGITAL",
      "WB_DIGITAL,44,WB_DIGITAL",
      "WB_DIGITAL,43,WB_DIGITAL",
      "Booking.com,35,3rd Party"})
  void findExternalBooking_MachingDetails_byBasket_test(String idContext, String sourceCode, String newIdContext) {
    var req = mockFindOtaBookingRequest();
    var basket = BasketResponse.builder()
        .bookingReference(BOOKING_REFERENCE)
        .hotelId(HOTEL_ID)
        .reference(BASKET_REFERENCE)
        .items(Collections.singletonList(
            BasketItemResponse.builder()
                .sourceId(BOOKING_REFERENCE)
                .build()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();
    var operaRes = ManageReservationUtils.mockReservationByBasketRefResponse3rdParty(
        FLEX, FLEX, FLEX, sourceCode);
    operaRes.setIdContext(idContext);
    when(manageBookingLogic.replaceHotelId(OTA_REFERENCE)).thenReturn(OTA_REFERENCE);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(basketOutPort.getBasketByReference(OTA_REFERENCE)).thenReturn(Optional.of(basket));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
        .thenReturn(operaRes);
    when(manageBookingLogic.getFindBookingResponse(req, ManageReservationUtils.mockBookingChannel(), idContext,
        sourceCode, basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BASKET_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .idContext(newIdContext)
            .build()));

    //Act
    var response = manageBookingInPort.findBooking(req,
        ManageReservationUtils.mockBookingChannel());

    assertNotNull(response);
    assertEquals(newIdContext, response.getIdContext());
  }

  @ParameterizedTest
  @CsvSource({"WB_DIGITAL,35,WB_DIGITAL,DISTR,AGENCY,false",
      "WB_DIGITAL,44,WB_DIGITAL,PI,MOBILE,false",
      "WB_DIGITAL,44,WB_DIGITAL,PI,WEB,false",
      "Booking.com,35,3rd Party,PI,MOBILE,true"})
  void findExternalBooking_byBasket_nonMatchesOpera_test(String idContext, String sourceCode, String newIdContext,
      String channel, String subchannel, boolean isOta) {
    var req = mockFindOtaBookingRequest();
    var basket = BasketResponse.builder()
        .bookingReference(BOOKING_REFERENCE)
        .hotelId(HOTEL_ID)
        .reference(BASKET_REFERENCE)
        .items(Collections.singletonList(
            BasketItemResponse.builder()
                .sourceId(BOOKING_REFERENCE)
                .build()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();
    var operaRes = ManageReservationUtils.mockReservationByBasketRefResponse3rdParty(
        FLEX, FLEX, FLEX, sourceCode);
    operaRes.setIdContext(idContext);
    operaRes.getReservationByIdList().forEach(elem -> elem.getBilling().setLastName("x"));
    operaRes.getReservationByIdList()
        .forEach(elem -> elem.getReservationGuestList().forEach(guest -> guest.setSurName("y")));
    when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));
    when(manageBookingLogic.isOtaFlowEnabled()).thenReturn(true);
    if (isOta) {
      when(manageBookingLogic.is3rdPartyBooking(idContext, sourceCode)).thenReturn(true);
    }
    when(manageBookingLogic.replaceHotelId(OTA_REFERENCE)).thenReturn(OTA_REFERENCE);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(basketOutPort.getBasketByReference(OTA_REFERENCE)).thenReturn(Optional.of(basket));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
        .thenReturn(operaRes);
    when(manageBookingLogic.getFindBookingResponse(req,
        ManageReservationUtils.mockBookingChannel(channel, subchannel), idContext,
        sourceCode, basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BASKET_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .idContext(newIdContext)
            .build()));

    //Act
    if (!isOta) {
      var response = manageBookingInPort.findBooking(req,
          ManageReservationUtils.mockBookingChannel(channel, subchannel));
      assertNull(response);
    } else {
      var bookingChannel = ManageReservationUtils.mockBookingChannel(channel, subchannel);
      var ex = assertThrows(GenericBadRequestException.class,
          () -> manageBookingInPort.findBooking(req, bookingChannel));
      assertEquals(ErrorCode.DIGITAL_NOT_MATCH_OTA_EXCEPTION.getCode(), ex.getErrorCode());
    }
  }

  @Test
  void findBooking_basket_shouldSaveChargesForExistingWithPNAndNoChargesOrAmountLeft() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(mockBasketResponseWithPN()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder().build());
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.getFindBookingResponse(mockFindBookingRequest(),
        ManageReservationUtils.mockBookingChannel(), null,
        "44", mockBasketResponseWithPN())).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BASKET_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .idContext("3rd party")
            .build()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort).saveCharges(any());
  }

  @Test
  void findBooking_basket_shouldNotSaveChargesForExistingWithAmountLeft() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(mockBasketResponseWithPN()));

    var resByBasket = ManageReservationUtils.mockReservationByBasketRefResponse();
    resByBasket.getReservationByIdList().get(0)
            .setRateInfo(ManageReservationUtils.mockRateInfoWithPayOnArrivalAmountLeft());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false)).thenReturn(
            resByBasket);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.getFindBookingResponse(mockFindBookingRequest(), ManageReservationUtils.mockBookingChannel(),
        null, "44", mockBasketResponseWithPN())).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .build()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void findBooking_basket_shouldNotSaveChargesForExistingWithCharges() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(mockBasketResponseWithPN()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any())).thenReturn(ManageReservationUtils.mockDepositFoliosResponse());
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.getFindBookingResponse(mockFindBookingRequest(),
        ManageReservationUtils.mockBookingChannel(), null,
        "44", mockBasketResponseWithPN())).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BASKET_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .idContext("3rd party")
            .build()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void findBooking_shouldReturnNullForCompletedEmptyBasket() {
    //Arrange
    when(basketOutPort.getBasketByReference("TEST123456"))
            .thenReturn(Optional.of(BasketResponse.builder()
                    .reference("TEST123456")
                    .status("COMPLETED")
                    .lastModifiedAt("123")
                    .items(Collections.emptyList())
                    .build()));
    when(manageBookingLogic.replaceHotelId("TEST123456")).thenReturn("TEST123456");
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, nullValue());
  }

  @Test
  void findBookingBasket_differentResStatus_callCancelBasket() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(BasketResponse.builder()
                    .bookingReference("123456")
                    .hotelId(HOTEL_ID)
                    .reference(BASKET_REFERENCE)
                    .status(CreateBasketRequestDto.BasketStatusEnum.COMPLETED.name())
                    .items(Collections.singletonList(
                            BasketItemResponse.builder()
                                    .sourceId(BOOKING_REFERENCE_2)
                                    .build()))
                    .build()));

    ReservationByBasketRefResponse reservation =
            mockReservationByBasketRefResponseCancelledStatus();

    when(reservationOutPort.getReservationsByIds(eq(HOTEL_ID), anyList(), anyBoolean()))
            .thenReturn(reservation);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    //Act
    manageBookingInPort.findBooking(mockFindBookingRequest(), ManageReservationUtils.mockBookingChannel());

    //Assert
    verify(basketOutPort).cancelBasket(BASKET_REFERENCE, false, false, null);
  }

  @Test
  void findBookingBasket_sameResStatus_cancelBasketIsNeverCalled() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(BasketResponse.builder()
                    .bookingReference("123456")
                    .hotelId(HOTEL_ID)
                    .reference(BOOKING_REFERENCE_2)
                    .status("CANCELLED")
                    .items(Collections.singletonList(
                            BasketItemResponse.builder()
                                    .sourceId(BOOKING_REFERENCE_2)
                                    .build()))
                    .build()));

    ReservationByBasketRefResponse reservation =
            mockReservationByBasketRefResponseCancelledStatus();

    when(reservationOutPort.getReservationsByIds(eq(HOTEL_ID), anyList(), anyBoolean()))
            .thenReturn(reservation);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    //Act
    manageBookingInPort.findBooking(mockFindBookingRequest(), ManageReservationUtils.mockBookingChannel());

    //Assert
    verify(basketOutPort, times(0))
            .cancelBasket(BOOKING_REFERENCE_2, false, false, null);
  }

  @ParameterizedTest
  @ValueSource(strings = {"FAILED", "OPEN"})
  void findBooking_shouldReturnNullForFailedAndOpenBasket(String status) {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenReturn(Optional.of(BasketResponse.builder()
                    .reference(BOOKING_REFERENCE_2)
                    .status(status)
                    .items(Collections.singletonList(
                            BasketItemResponse.builder()
                                    .sourceId(BOOKING_REFERENCE_2)
                                    .build()))
                    .build()));
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, nullValue());
  }

  @Test
  void findBooking_opera_success() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
            .thenReturn(depositsResponse);
    when(basketOutPort.createBasketReservation(any()))
            .thenReturn(ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE));
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(),
        ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE))).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .token("token123")
            .build()));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSourcePms(), is("Opera"));
    assertThat(response.getRef(), is(BOOKING_REFERENCE));
    assertNotNull(response.getToken());
    assertNull(response.getIdContext());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void findBooking_setsHotelId_whenBasketHotelIdIsNull() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
        .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
        .thenReturn(depositsResponse);

    when(basketOutPort.createBasketReservation(any()))
        .thenReturn(ManageReservationUtils.mockCreateBasketDtoResponseWithoutHotelId(BOOKING_REFERENCE));
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(),
        ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE))).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .token("token123")
            .build()));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
        ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSourcePms(), is("Opera"));
    assertThat(response.getRef(), is(BOOKING_REFERENCE));
    assertNotNull(response.getToken());
    assertNull(response.getIdContext());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void findBooking_opera_shouldNotSaveChargesForMigratedWithAmountLeft() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));

    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
            .thenReturn(depositsResponse);
    var basket = ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE);
    basket.setPaymentOption(PaymentOption.PAY_NOW.name());
    when(basketOutPort.createBasketReservation(any())).thenReturn(basket);
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(), basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .build()));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));
    when(amendLogicInPort.getAmountFromReservationRateInfo(any()))
        .thenReturn(
            AmendSummaryAmountResponse.builder()
                .guestPay(Map.of("1", BigDecimal.TEN)) // non-zero to ensure NO saveCharges
                .build()
        );

    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void findBooking_opera_shouldSaveChargesForMigratedWithPNAndNoAmountLeft() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
            .thenReturn(depositsResponse);
    var basket = ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE);
    basket.setPaymentOption(String.valueOf(PAY_NOW));
    when(basketOutPort.createBasketReservation(any())).thenReturn(basket);
    when(amendLogicInPort.getAmountFromReservationRateInfo(any()))
            .thenReturn(
                    AmendSummaryAmountResponse.builder().guestPay(Map.of("1", BigDecimal.ZERO)).build());
    when(manageBookingLogic.replaceHotelId(anyString())).thenAnswer(inv -> inv.getArgument(0));
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(), basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .build()));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort).saveCharges(any());
  }

  @Test
  void findBooking_opera_shouldNotSaveChargesForMigratedWithPNAndCharges() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));

    var basket = ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE);
    basket.setPaymentOption(String.valueOf(PaymentOption.PAY_NOW));
    basket.setIdContext("BART_OHIP");

    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(basketOutPort.createBasketReservation(any())).thenReturn(basket);
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenThrow(new BasketDigitalNotFoundException(ErrorCode.DIGITAL_RESERVATION_ID_EXCEPTION,
                    BASKET_EXCEPTION_MSG));
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
            .thenReturn(depositsResponse);
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())).thenReturn(Pair.of(List.of(), List.of()));
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(), basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .idContext("BART_OHIP")
            .build()));
    when(amendLogicInPort.getAmountFromReservationRateInfo(any()))
        .thenReturn(
            AmendSummaryAmountResponse.builder()
                .guestPay(Map.of("1", BigDecimal.TEN)) // non-zero to ensure NO saveCharges
                .build()
        );

    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertEquals("BART_OHIP", response.getIdContext());
    verify(basketOutPort, never()).saveCharges(any());
  }

  @Test
  void whenOperaResStatCancelled_findBooking_opera_success() {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    ReservationsDetailsEnhancedResponse reservationDetails = ManageReservationUtils.mockReservationEnhancedResponse("44");
    reservationDetails.getReservations().getReservationInfo().get(0)
            .setReservationStatus("CANCELLED");
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(reservationDetails);
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
            .thenReturn(depositsResponse);
    ArgumentCaptor<CreateBasketRequestReservationDto> req = ArgumentCaptor.forClass(CreateBasketRequestReservationDto.class);
    when(basketOutPort.createBasketReservation(req.capture()))
            .thenReturn(ManageReservationUtils.mockCreateBasketDtoResponse(BOOKING_REFERENCE));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));

    //Act
    manageBookingInPort.findBooking(mockFindBookingRequest(), ManageReservationUtils.mockBookingChannel());

    //Assert
    verify(basketOutPort).createBasketReservation(any());
    assertEquals(CreateBasketRequestReservationDto.BasketStatusEnum.CANCELLED, req.getValue().getBasketStatus());
    assertEquals(PaymentInfoDto.PaymentOptionEnum.PAY_NOW,req.getValue().getPaymentInfoDto().getPaymentOption());
  }

  @Test
  void findBooking_opera_success_no_payment_id() {
    //Arrange
    var basket = ManageReservationUtils.mockCreateBasketReservationIdContextResponse(BOOKING_REFERENCE, "BART_OHIP");
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
            .thenThrow(new BasketDigitalNotFoundException(ErrorCode.DIGITAL_RESERVATION_ID_EXCEPTION,
                    "Could not find reservation in basket service"));
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
            .thenReturn(ManageReservationUtils.mockReservationEnhancedResponse("44"));
    when(reservationOutPort.getDepositsForReservationId(anyString(), anyString())).thenReturn(
            new DepositsResponse());
    when(basketOutPort.createBasketReservation(any())).thenReturn(basket);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.buildReservationFindBookingResponse(mockFindBookingRequest(), basket)).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(BOOKING_REFERENCE)
            .sourcePms("Opera")
            .basketReference(BOOKING_REFERENCE)
            .token("token123")
            .idContext("BART_OHIP")
            .build()));
    when(basketOutPort.addReservationsBasketItemsAndTypes(any())) .thenReturn(Pair.of(List.of(), List.of()));

    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSourcePms(), is("Opera"));
    assertThat(response.getRef(), is(BOOKING_REFERENCE));
    assertNotNull(response.getToken());
    assertEquals("BART_OHIP" , response.getIdContext());
  }

  @Test
  void findBooking_byOperaConfirmation_success_FF_true() {
    //Arrange
    when(basketOutPort.getBasketByReference(OPERA_CONFIRMATION)).thenReturn(Optional.empty());
    when(cdhSearchBookingOutPort.searchBookingsFromCdh(any())).thenReturn(mockCdhSearchBookingsResponse());
    when(reservationOutPort.getReservationsByIds(any(), anyList(), any(), any()))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse_OperaConfirmation("NON"));
    when(manageBookingLogic.createBasketForOperaUiCreatedReservations(any(),
        any(), any(), eq(false))).thenReturn(
        ManageReservationUtils.mockCreateBasketResponse(OPERA_CONFIRMATION));

    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(OPERA_CONFIRMATION)).thenReturn(OPERA_CONFIRMATION);
    when(manageBookingLogic.isOperaConfirmationNumber(any())).thenReturn(true);
    when(manageBookingLogic.buildFindBookingResponse(mockFindBookingRequestByOperaConfirmation(),
        (ManageReservationUtils.mockCreateBasketResponse(OPERA_CONFIRMATION)))).thenReturn(
        Optional.of(FindBookingResponse.builder()
            .ref(OPERA_CONFIRMATION)
            .sourcePms("Opera")
            .basketReference(OPERA_CONFIRMATION)
            .token("token123")
            .idContext("BART_OHIP")
            .build()));
    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequestByOperaConfirmation(),
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSourcePms(), is("Opera"));
    assertThat(response.getRef(), is(OPERA_CONFIRMATION));
    assertNotNull(response.getToken());
    verify(basketOutPort, never()).saveCharges(any());

  }

  @ParameterizedTest
  @CsvSource(value = {
      "true, false, true",
      "true, false, false",
      "true, true, true",
      "true, true, false",
      "false, false, true",
      "false, false, false",
      "false, true, true",
      "false, true, true"})
  void findBooking_byOperaConfirmation_hotelId_success_FF_true(Boolean isOtaFlowEnabled,
      Boolean byPass, Boolean isDetailsMatch) {
    //Arrange
    var findBookingRequest = mockFindBookingRequestByOperaConfirmation();
    findBookingRequest.setResNo(HOTEL_ID_OPERA_CONFIRMATION);
    if(!isDetailsMatch){
      findBookingRequest.setLastName("aaa");
    }
    when(manageBookingLogic.shouldBypassMatchesOpera(any())).thenReturn(byPass);
    when(basketOutPort.getBasketByReference(OPERA_CONFIRMATION)).thenReturn(Optional.empty());
    when(cdhSearchBookingOutPort.searchBookingsFromCdh(any())).thenReturn(mockCdhSearchBookingsResponse());
    when(reservationOutPort.getReservationsByIds(any(), anyList(), any(), any()))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse_OperaConfirmation("NON"));
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(HOTEL_ID_OPERA_CONFIRMATION)).thenReturn(OPERA_CONFIRMATION);
    when(manageBookingLogic.isOperaConfirmationNumber(any())).thenReturn(true);
    if (isDetailsMatch || byPass) {
      when(manageBookingLogic.is3rdPartyBookingFlow(any(), any(), any())).thenReturn(
          isOtaFlowEnabled);
      when(manageBookingLogic.createBasketForOperaUiCreatedReservations(any(),
          any(), any(), eq(isOtaFlowEnabled))).thenReturn(
          ManageReservationUtils.mockCreateBasketResponse(OPERA_CONFIRMATION));
      if (isOtaFlowEnabled) {
        when(manageBookingLogic.buildFindBookingResponse(findBookingRequest,
            ManageReservationUtils.mockCreateBasketResponse(OPERA_CONFIRMATION),
            "3rd Party")).thenReturn(
            Optional.of(FindBookingResponse.builder()
                .ref(OPERA_CONFIRMATION)
                .sourcePms("Opera")
                .basketReference(OPERA_CONFIRMATION)
                .token("token123")
                .idContext("3rd Party")
                .build()));
      } else {
        when(manageBookingLogic.buildFindBookingResponse(findBookingRequest,
            ManageReservationUtils.mockCreateBasketResponse(OPERA_CONFIRMATION))).thenReturn(
            Optional.of(FindBookingResponse.builder()
                .ref(OPERA_CONFIRMATION)
                .sourcePms("Opera")
                .basketReference(OPERA_CONFIRMATION)
                .token("token123")
                .idContext("BART_OHIP")
                .build()));
      }
    }
    //Act
    var response = manageBookingInPort.findBooking(findBookingRequest,
            ManageReservationUtils.mockBookingChannel());

    //Assert
    if (!isDetailsMatch && !(byPass)) {
      assertThat(response, nullValue());
    } else {
      assertThat(response, notNullValue());
      assertThat(response.getSourcePms(), is("Opera"));
      assertThat(response.getRef(), is(OPERA_CONFIRMATION));
      assertNotNull(response.getToken());
    }
    verify(basketOutPort, never()).saveCharges(any());
    verify(manageBookingLogic).shouldBypassMatchesOpera(any());
  }

  @Test
  void findBooking_byOperaConfirmation_different_surname_FF_true() {
    //Arrange
    when(manageBookingLogic.replaceHotelId(OPERA_CONFIRMATION)).thenReturn(OPERA_CONFIRMATION);

    //Act
    var response = manageBookingInPort.findBooking(FindBookingRequest.builder()
        .resNo(OPERA_CONFIRMATION)
        .lastName("Carl")
        .arrivalDate("2022-05-05")
        .country("gb")
        .language("en")
        .build(),
        ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, nullValue());

  }

  @Test
  void findBooking_byOperaConfirmation_different_arrivalDate_FF_true() {
    //Arrange
    when(basketOutPort.getBasketByReference(OPERA_CONFIRMATION)).thenReturn(Optional.empty());
    when(manageBookingLogic.replaceHotelId(OPERA_CONFIRMATION)).thenReturn(OPERA_CONFIRMATION);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    //Act
    var response = manageBookingInPort.findBooking(FindBookingRequest.builder()
            .resNo(OPERA_CONFIRMATION)
            .lastName("John")
            .arrivalDate("2022-05-01")
            .country("gb")
            .language("en")
            .build(),
        ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, nullValue());

  }

  @Test
  void findBooking_byOperaConfirmation_success_FF_false() {
    //Arrange
    var findBookingRequest = mockFindBookingRequestByOperaConfirmation();
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(findBookingRequest.getResNo())).thenReturn(findBookingRequest.getResNo());

    //Act
    var response = manageBookingInPort.findBooking(findBookingRequest,
            ManageReservationUtils.mockBookingChannel());

    //Assert
    assertThat(response, nullValue());

  }

  @Test
  void findBooking_WhenGetBasketReturnsEmptyChannel_ThenProvidedChannelIsUsed() {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
        .thenReturn(Optional.of(BasketResponse.builder()
            .bookingReference(BOOKING_REFERENCE)
            .hotelId(HOTEL_ID)
            .reference(BASKET_REFERENCE)
            .items(Collections.singletonList(
                BasketItemResponse.builder()
                    .sourceId(BOOKING_REFERENCE)
                    .build()))
            .paymentOption(PAY_ON_ARRIVAL)
            .build()));
    ReservationByBasketRefResponse reservationByBasketRefResponse = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservationByBasketRefResponse.getReservationByIdList().forEach(p -> p.getRoomStay().setSourceCode("42")); // BB reservations only
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
        .thenReturn(reservationByBasketRefResponse);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
        new BookingChannel("PI", "SUB", ""));

    //Assert
    assertThat(response, nullValue());
  }

  @ParameterizedTest()
  @MethodSource("getReservationsByIds")
  void findBooking_WhenGetBasketThrowsException(Exception exception,
      Exception exceptionByExternalId) {
    //Arrange

    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2))
        .thenReturn(Optional.of(BasketResponse.builder()
            .bookingReference(BOOKING_REFERENCE)
            .hotelId(HOTEL_ID)
            .reference(BASKET_REFERENCE)
            .items(Collections.singletonList(
                BasketItemResponse.builder()
                    .sourceId(BOOKING_REFERENCE)
                    .build()))
            .paymentOption(PAY_ON_ARRIVAL)
            .build()));
    if (!(exceptionByExternalId instanceof GenericBadRequestException)) {
      when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2))
          .thenThrow(exception);
    }
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
        .thenThrow(exceptionByExternalId);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);

    if (exception instanceof HotelReservationOhipException) {
      var bookingChannel = new BookingChannel("PI", "SUB", "");
      var request = mockFindBookingRequest();
      assertThrows(HotelReservationOhipException.class, () ->
          manageBookingInPort.findBooking(request, bookingChannel));
    }else {
      var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
          new BookingChannel("PI", "SUB", ""));
      assertThat(response, nullValue());
    }
  }

  private static Stream<Arguments> getReservationsByIds() {
    return Stream.of(Arguments.of(new RuntimeException(), new RuntimeException()),
        Arguments.of(new HotelReservationOhipException("mess ohip", "debug mess", null, 1),
            new RuntimeException()),
        Arguments.of(new HotelReservationNotFoundException("mess", "debug mess", null, 1),
            new RuntimeException()),
        Arguments.of(new RuntimeException(),
            new GenericBadRequestException(ErrorCode.DIGITAL_RESERVATION_CHARGES_EXCEPTION,
                "mess")));
  }
  @Test
  void findBooking_WhenGetBasketReturnsNonEmptyChannel_ThenProvidedChannelIsOverwritten() {
    //Arrange
    var basket = BasketResponse.builder()
        .bookingReference(BOOKING_REFERENCE)
        .hotelId(HOTEL_ID)
        .reference(BASKET_REFERENCE)
        .items(Collections.singletonList(
            BasketItemResponse.builder()
                .sourceId(BOOKING_REFERENCE)
                .build()))
        .paymentOption(PAY_ON_ARRIVAL)
        .channel("BB") // overwrites provided channel
        .build();
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2)).thenReturn(Optional.of(basket));
    ReservationByBasketRefResponse reservationByBasketRefResponse = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservationByBasketRefResponse.getReservationByIdList().forEach(p -> p.getRoomStay().setSourceCode("42")); // BB reservations only
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false))
        .thenReturn(reservationByBasketRefResponse);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_2)).thenReturn(BOOKING_REFERENCE_2);
    when(manageBookingLogic.isSearchFlowEnabled()).thenReturn(true);
    when(manageBookingLogic.getFindBookingResponse(mockFindBookingRequest(),
        new BookingChannel("PI", "SUB", ""), null,
        "42", basket)).thenReturn(Optional.of(FindBookingResponse.builder()
        .ref(BASKET_REFERENCE)
        .sourcePms("Opera")
        .basketReference(BOOKING_REFERENCE_2)
        .idContext("3rd party")
        .build()));

    //Act
    var response = manageBookingInPort.findBooking(mockFindBookingRequest(),
        new BookingChannel("PI", "SUB", ""));

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void searchBookings_Opera_success() {
    //Arrange
    when(reservationOutPort.searchBookings(any(SearchBookingsRequest.class))).thenReturn(
            mockSearchBookingsResponse(
                    "Opera"));

    //Act
    var response = manageBookingInPort.searchBookings(mockSearchBookingsRequest());

    //Assert
    assertThat(response, notNullValue());
    assertEquals(1, response.getBookings().size());
    assertEquals("Opera", response.getBookings().get(0).getSourcePms());
    assertEquals(BOOKING_REFERENCE, response.getBookings().get(0).getBookingReference());
    assertEquals("John", response.getBookings().get(0).getBooker().getLastName());
  }

  @Test
  void searchBookings_noResults() {
    //Arrange
    when(reservationOutPort.searchBookings(any(SearchBookingsRequest.class))).thenReturn(
        mockEmptySearchBookingsResponse());

    //Act
    var response = manageBookingInPort.searchBookings(mockSearchBookingsRequest());

    //Assert
    assertThat(response, notNullValue());
    assertEquals(0, response.getBookings().size());
    assertEquals(0, response.getTotalResults());
  }

  @Test
  void buildDisableCiolResponse_shouldSetFieldsCorrectly() throws Exception {
    Method method = ManageBookingInPortImpl.class.getDeclaredMethod(
        "buildDisableCiolResponse", boolean.class, boolean.class, boolean.class);
    method.setAccessible(true);
    Object response = method.invoke(null, true, false, true);
    assertNotNull(response);
    ManageBookingResponse mbResponse = (ManageBookingResponse) response;
    assertFalse(mbResponse.getIsCancellable());
    assertFalse(mbResponse.getIsAmendable());
    assertEquals(false, mbResponse.getIsRuleCompliant());
    assertNull(mbResponse.getAemLabelKey());
    assertEquals(true, mbResponse.isCheckOutOnlineAvailable());
    assertEquals(true, mbResponse.isDigitalKey());
  }

  @Test
  void updateUdfc20_shouldDelegateToOutPort() {
    // Arrange
    UpdateReservationUdfsRequest request = UpdateReservationUdfsRequest.builder()
                .reservationIds(Set.of("RES1"))
                .hotelId("TEST_HOTEL")
                .udfs(null)
                .build();
    // Act
    manageBookingInPort.updateUdfc20(request);
    // Assert
    Mockito.verify(reservationOutPort).updateUdfc20(request);
  }

  private BasketResponse mockBasketForCancelResponse() {
    return BasketResponse.builder()
            .hotelId(TEST_HOTEL_ID)
            .bookingReference(BOOKING_REFERENCE)
            .reference(BASKET_REFERENCE)
            .createdAt(new Date().toString())
            .status("OPEN")
            .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
            .itemTypes(Collections.emptySet())
            .items(ManageReservationUtils.mockBasketItemsListForCancel())
            .build();
  }

  private RatePlansResponse mockNegotiatedRatePlans(){
    return RatePlansResponse.builder()
            .ratePlans(Collections.singletonList(
                    RatePlan.builder()
                            .ratePlanCode("FIXEDM07")
                            .classifications(Classifications.builder().displaySet("BMD").build())
                            .build()))
            .build();
  }

  private BookingChannel mockCcuiBookingChannel() {
    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel("CCUI");
    bookingChannel.setSubchannel("WEB");
    bookingChannel.setLanguage("EN");

    return bookingChannel;
  }

  private RatePlansResponse mockBusiflexRatePlans(){
    return RatePlansResponse.builder()
            .ratePlans(Collections.singletonList(
                    RatePlan.builder()
                            .ratePlanCode("BUSIFLEX")
                            .classifications(Classifications.builder().displaySet("BFL").build())
                            .build()))
            .build();
  }

  private BasketResponse mockBasketForAmendRWCCResponse() {
    return BasketResponse.builder()
            .hotelId(TEST_HOTEL_ID)
            .bookingReference(BOOKING_REFERENCE)
            .reference(BASKET_REFERENCE)
            .createdAt(new Date().toString())
            .status("OPEN")
            .paymentOption(RESERVE_WITHOUT_CARD)
            .itemTypes(Collections.emptySet())
            .items(ManageReservationUtils.mockBasketItemsListForCancel())
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseForEmployeeBooking() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdFlex("11111111"),
                        ManageReservationUtils.mockReservationByIdFlex("22222222")))
            .build();
  }


  private ReservationByBasketRefResponse mockReservationByBasketRefResponseEmployeeRate() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.TEN)
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdEmployeeRate(),
                            ManageReservationUtils.mockReservationByIdEmployeeRate()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefCancellationResponse() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.TEN)
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId("HOTELTEST")
            .policyCode("DOA")
            .currencyCode("GBP")
            .reservationByIdList(Collections.singletonList(ManageReservationUtils.mockReservationByIdFlex("11111111")))
            .build();
  }

  private SearchBookingsRequest mockSearchBookingsRequest() {
    return SearchBookingsRequest.builder().hotelId(TEST_HOTEL_ID).bookingReference("TestBookingRef")
            .arrivalDate("2023-01-18").bookerLastName("TestBookerLastName").offset(0).limit(20).build();
  }

  private SearchBookingsResponse mockSearchBookingsResponse(String sourcePms) {
    return SearchBookingsResponse.builder()
            .bookings(List.of(SearchBooking.builder()
                    .bookingReference(BOOKING_REFERENCE)
                    .booker(Booker.builder().lastName("John").firstName("Julius").build())
                    .arrivalDate(LocalDate.of(2022, 5, 5))
                    .sourcePms(sourcePms)
                    .build()))
            .totalResults(1)
            .hasMore(false)
            .offset(20)
            .limit(20)
            .totalPages(1)
            .build();
  }

  private SearchBookingsResponse mockEmptySearchBookingsResponse() {
    return SearchBookingsResponse.builder()
            .bookings(new ArrayList<>())
            .totalResults(0)
            .hasMore(false)
            .offset(0)
            .limit(20)
            .totalPages(0)
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseCancelledStatus() {
    ReservationByIdResponse reservationByIdResponse = ReservationByIdResponse.builder()
            .reservationStatus("Cancelled")
            .build();
    return ReservationByBasketRefResponse.builder()
            .hotelId(HOTEL_ID)
            .reservationByIdList(List.of(reservationByIdResponse))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseNonFlex() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdNonFlex()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseFlex() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdFlex()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseRandomRatePlanCode() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdRandomRatePlanCode()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseSemiFlex() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdSemiFlex()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseStandard() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdStandardFlex()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseAdvance() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(ManageReservationUtils.mockReservationByIdAdvanceFlex()))
            .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponsePIBA() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(ManageReservationUtils.mockReservationByIdPIBA()))
        .build();
  }

  private BookingChannel mockBbBookingChannel() {
    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel("BB");
    bookingChannel.setSubchannel("WEB");
    bookingChannel.setLanguage("EN");

    return bookingChannel;
  }

  private static FindBookingRequest mockFindBookingRequest() {
    return FindBookingRequest.builder()
            .resNo(BOOKING_REFERENCE_2)
            .lastName("John")
            .arrivalDate("2022-05-05")
            .country("gb")
            .language("en")
            .build();
  }

  private static FindBookingRequest mockFindOtaBookingRequest() {
    return FindBookingRequest.builder()
        .resNo(OTA_REFERENCE)
        .lastName("John")
        .arrivalDate("2022-05-05")
        .country("gb")
        .language("en")
        .build();
  }


  private static BasketResponse mockBasketResponseWithPN() {
    return BasketResponse.builder()
            .bookingReference(BOOKING_REFERENCE)
            .hotelId(HOTEL_ID)
            .reference(BASKET_REFERENCE)
            .items(Collections.singletonList(
                    BasketItemResponse.builder()
                            .sourceId(BOOKING_REFERENCE)
                            .build()))
            .paymentOption(PAY_NOW)
            .eTag("eTag")
            .build();
  }

}
