package uk.co.whitbread.reservation.domain.logic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.ContentException;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockChannelRuleResponse;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockIndexHeader;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL;

@ExtendWith(MockitoExtension.class)
public class ManageBookingLogicTest {


  private static final String HOTEL_ID = "TESTHOTEL";
  public static final String OPERA_CONFIRMATION = "12345678";


  @InjectMocks
  ManageBookingLogic manageBookingLogic;

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private RulesOutPort rulesOutPort;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  ContentOutPort contentOutPort;
  @Mock
  ThirdpartyBookingProperties otaBookingProperties;

  @CsvSource(value = {"12345678,false", "ABCDEF12345678,false", "abcdef12345678,false",
      "12345678,true"})
  void createBasket_OperaConfirmation_shouldSaveCharges(String bookingReference, Boolean isOta) {
    //Arrange
    var channel = mockChannelRuleResponse("CCUI");
    var basket = ManageReservationUtils.mockBasketResponseWithPN();
    basket.setBookingReference(bookingReference);

    var rsvDetails =
        ManageReservationUtils.mockReservationByBasketRefResponse_PrePaid_OperaConfirmation();
    when(rulesOutPort.getChannelBasedOnSourceId(any())).thenReturn(channel);
    when(basketOutPort.createBasket(any(), any(), any(), any(), any(), any(), any(),
        any())).thenReturn(basket);
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), eq(null))).thenReturn(basket);

    //Act
    var response = manageBookingLogic.createBasketForOperaUiCreatedReservations(HOTEL_ID,
        OPERA_CONFIRMATION,
        rsvDetails, isOta);

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort).saveCharges(any());
    if (isOta) {
      verify(basketOutPort).changeIdContext(bookingReference, "3rd Party");
    }
  }

  @ParameterizedTest
  @CsvSource(value = {"12345678,false", "ABCDEF12345678,false", "abcdef12345678,false",
      "12345678,true"})
  void createBasket_operaConfirmation_shouldSaveAllowances(String bookingReference, Boolean isOta) {
    //Arrange
    var channel = mockChannelRuleResponse("CCUI");
    var basket = ManageReservationUtils.mockCreateBasketResponse(bookingReference);

    var rsvDetails =
        ManageReservationUtils.mockReservationByBasketRefResponse_OperaConfirmation("CO");

    when(rulesOutPort.getChannelBasedOnSourceId(any())).thenReturn(channel);
    when(basketOutPort.createBasket(any(), any(), any(), any(), any(), any(), any(),
        any())).thenReturn(basket);
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), eq(null))).thenReturn(basket);

    //Act
    var response = manageBookingLogic.createBasketForOperaUiCreatedReservations(HOTEL_ID,
        OPERA_CONFIRMATION,
        rsvDetails, isOta);

    //Assert
    assertThat(response, notNullValue());
    verify(basketOutPort, never()).saveCharges(any());
    verify(basketOutPort).updateAllowances(any(), any(), any());
    if (isOta) {
      verify(basketOutPort).changeIdContext(bookingReference, "3rd Party");
    }
  }

  @ParameterizedTest
  @CsvSource(value = {"12345678, 12345678", "ABCDEF12345678, 12345678", "abcdef12345678, 12345678"})
  void createBasket_operaConfirmation_replaceHotelId(String bookingReferenceIn,
      String bookingReferenceOut) {
    //Act
    var response = manageBookingLogic.replaceHotelId(bookingReferenceIn);

    //Assert
    assertEquals(response, bookingReferenceOut);
  }

  @ParameterizedTest
  @CsvSource(value = {"12345678, true", "ABCDEF12345678, true", "abcdef12345678, true",
      "TST1234567, false"})
  void createBasket_operaConfirmation_operaUi(String bookingReferenceIn, boolean operaUi) {
    //Act
    var response = manageBookingLogic.operaUiRsv(bookingReferenceIn);

    //Assert
    assertEquals(response, operaUi);
  }

  @ParameterizedTest
  @CsvSource(value = {"false,false", "true,true"})
  void createBasket_operaConfirmation_isSearchFlowEnabled(
      Boolean isConfNumFF,
      Boolean expected) {
    //Act
    var featureFlag = mock(FeatureFlag.class);
    var mockedFeatureIsConfNum = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getPiSearchByOperaConfirmation()).thenReturn(mockedFeatureIsConfNum);
    when(unleashWrapper.isEnabled(featureFlag.getPiSearchByOperaConfirmation()))
        .thenReturn(isConfNumFF);
    var response = manageBookingLogic.isSearchFlowEnabled();
    //Assert
    assertEquals(expected, response);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "aaa,false", "1234,false", "AB12345,false", "ABC1234567,true",
      ",false", "ABC1234567,true"})
  void isDigitalReference_test(String reference, Boolean expected) {
    //Act
    var response = manageBookingLogic.isDigitalReference(reference);
    //Assert
    assertEquals(expected, response);
  }

  @ParameterizedTest
  @CsvSource(value = {",false", "aaa,false", "1234,true"})
  void isOperaConfirmationNumber_test(String confNum, Boolean expected) {
    //Act
    var response = manageBookingLogic.isOperaConfirmationNumber(confNum);
    //Assert
    assertEquals(expected, response);
  }

  @ParameterizedTest
  @CsvSource(value = {
      ",01,false", ",31,false",
      "EXPEDIA,35,true", "EXPEDIA,38,true", "EXPEDIA,43,true",
      "WB_DIGITAL,01,false", "WB_DIGITAL,31,false",
      "WB_DIGITAL,35,true", "WB_DIGITAL,38,true", "WB_DIGITAL,43,true",
      "WB_DIGITAL,XX,false",
      "OTHER_CONTEXT,01,true", "OTHER_CONTEXT,31,true",
      "OTHER_CONTEXT,35,true", "OTHER_CONTEXT,38,true", "OTHER_CONTEXT,43,true",
      "OTHER_CONTEXT,XX,true"})
  void is3rdPartyBooking_test(String idContext, String idSource, Boolean expected) {
    //Act
    var response = manageBookingLogic.is3rdPartyBooking(idContext, idSource);
    //Assert
    assertEquals(expected, response);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "WB_DIGITAL,44,false,true,WB_DIGITAL,PI", // digital but not ota
      "WB_DIGITAL,35,true,true,3rd Party,PI",//distr and ota
      "WB_DIGITAL,38,true,true,3rd Party,PI",//distr and ota
      "WB_DIGITAL,43,true,true,3rd Party,PI",//distr and ota
      "AGENCY,43,false,true,AGENCY,DISTR",//distr channel not ota,
      "xx,35,true,true,3rd Party,PI",// ota
      "xx,38,true,true,3rd Party,PI",// ota
      "xx,43,true,true,3rd Party,PI",// ota
      "xx,01,true,true,3rd Party,PI",// ota
      "xx,31,true,true,3rd Party,PI",// ota
      "xx,36,true,true,3rd Party,PI",// ota
      ",01,false,true,,PI", //desktop
      ",31,false,true,,PI",//desktop
      "WB_DIGITAL,44,false,false,WB_DIGITAL,PI"})
  void buildFindBookingResponse_test(String idContext, String sourceCode, boolean isOtaBooking,
      boolean isOtaFf, String responseIdContext, String channel) {
    //Arrange
    FindBookingRequest findBookingRequest = mockFindBookingRequest("BREF1234");
    BookingChannel bookingChannel = mockBookingChannel(channel, "MOBILE");
    BasketResponse basket = mockBasket("BREF1234", "BASKET1234", idContext);

    when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
        .thenReturn(mockIndexHeader());
    var featureFlag = mock(FeatureFlag.class);
    var mockedFeature = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedFeature);
    when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking()))
        .thenReturn(true);
    if (isOtaBooking && isOtaFf) {
      when(otaBookingProperties.getProvidersExcluded()).thenReturn(Set.of("TEST"));
      when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));
      when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));
    }

    //Act
    var response = manageBookingLogic.getFindBookingResponse(findBookingRequest, bookingChannel,
        idContext, sourceCode, basket);

    //Assert
    assertNotNull(response.get());
    assertEquals(responseIdContext, response.get().getIdContext());

    if (Objects.nonNull(responseIdContext) && "3rd Party".equals(responseIdContext)) {
      verify(basketOutPort).changeIdContext("BREF1234", responseIdContext);
    }
  }

  @ParameterizedTest
  @CsvSource(value = {
      "35,x,true,PI,WEB",
      "38,x,true,PI,WEB",
      "36,x,false,PI,MOBILE",
      "31,x,false,PI,MOBILE",
      "01,x,false,PI,MOBILE"})
  void buildFindBookingResponse_Exception_test(String sourceCode, String idContext, Boolean ffOta,
      String channel, String subchannel) {
    //Arrange
    FindBookingRequest findBookingRequest = mockFindBookingRequest("BREF1234");
    BookingChannel bookingChannel = mockBookingChannel(channel, subchannel);
    BasketResponse basket = mockBasket("BREF1234", "BASKET1234", idContext);

    var featureFlag = mock(FeatureFlag.class);
    var mockedFeature = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedFeature);
    when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking()))
        .thenReturn(ffOta);
    when(otaBookingProperties.getProvidersExcluded()).thenReturn(Set.of("TEST"));
    when(otaBookingProperties.getSubchannel()).thenReturn(Set.of("PI.MOBILE"));
    //Act
    var ex = assertThrows(GenericBadRequestException.class,
        () -> manageBookingLogic.getFindBookingResponse(findBookingRequest, bookingChannel, idContext,
            sourceCode, basket));

    //Assert
    assertNotNull(ex);
    assertEquals(ErrorCode.DIGITAL_INVALID_OTA_EXCEPTION.getCode(), ex.getErrorCode());
    verifyNoInteractions(basketOutPort);
  }

  @ParameterizedTest
  @MethodSource("findBookingProvider")
  void buildFindBookingResponse_test(FindBookingRequest findBookingRequest, BasketResponse basket,
      FindBookingResponse expected) {
    //Arrange
    when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
        .thenReturn(mockIndexHeader());
    //Act
    var response = manageBookingLogic.buildFindBookingResponse(findBookingRequest, basket);
    var token = response.get().getToken();
    //Assert
    assertNotNull(response);
    assertNotNull(token);
    expected.setToken(token);
    assertEquals(Optional.of(expected), response);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideShouldBypassMatchesOperaTestCases")
  void shouldBypassMatchesOpera_variousScenarios(
      String testName,
      String channel,
      String subchannel,
      boolean expectedResult) {

    // Arrange
    BookingChannel bookingChannel = BookingChannel.builder()
        .channel(channel)
        .subchannel(subchannel)
        .build();

    // Act
    boolean result = manageBookingLogic.shouldBypassMatchesOpera(bookingChannel);

    // Assert
    assertEquals(expectedResult, result, testName);
  }

  private static Stream<Arguments> provideShouldBypassMatchesOperaTestCases() {
    return Stream.of(
        // Happy path - all conditions met
        Arguments.of(
            "Should bypass: KIOSK.WEB",
            "KIOSK", "WEB", true
        ),

        // Wrong channel/subchannel scenarios
        Arguments.of(
            "Should NOT bypass: PI channel instead of KIOSK",
            "PI", "WEB", false
        ),
        Arguments.of(
            "Should NOT bypass: MOBILE subchannel instead of WEB",
            "KIOSK", "MOBILE", false
        )
    );
  }

  @ParameterizedTest
  @MethodSource("provide3rdPartyBookingFlowTestCases")
  void testIs3rdPartyBookingFlow(
      String idContext,
      String sourceCode,
      BookingChannel bookingChannel,
      boolean isOtaFlag,
      Set<String> providers,
      Set<String> defSubchannel,
      boolean expectedResult) {
    //Assert
    var featureFlag = mock(FeatureFlag.class);
    var mockedFeature = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getMobileAcceptsOtaBooking()).thenReturn(mockedFeature);
    when(unleashWrapper.isEnabled(featureFlag.getMobileAcceptsOtaBooking()))
        .thenReturn(true);
    if (isOtaFlag && expectedResult) {
      when(otaBookingProperties.getProvidersExcluded()).thenReturn(providers);
      when(otaBookingProperties.getSubchannel()).thenReturn(providers);
      when(otaBookingProperties.getSubchannel()).thenReturn(defSubchannel);
    }
    // Act
    boolean result = manageBookingLogic.is3rdPartyBookingFlow(idContext, sourceCode,
        bookingChannel);

    // Assert
    assertEquals(expectedResult, result);
  }

  @ParameterizedTest
  @MethodSource("buildFindBookingResponseHeaderTestCases")
  void buildFindBookingResponse_withHeaderHandling_test(
      FindBookingRequest findBookingRequest,
      BasketResponse basket,
      IndexHeaderData indexHeaderData,
      boolean shouldThrowException,
      boolean expectCookieFields,
      boolean expectRedirectBase) {
    //Arrange
    if (shouldThrowException) {
      when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
          .thenThrow(new ContentException(
              "Not found", "Header data not found", null, 404));
    } else {
      when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
          .thenReturn(indexHeaderData);
    }

    //Act
    var response = manageBookingLogic.buildFindBookingResponse(findBookingRequest, basket);

    //Assert
    assertNotNull(response);
    assertTrue(response.isPresent());
    assertEquals("Opera", response.get().getSourcePms());
    assertEquals(basket.getBookingReference(), response.get().getRef());
    assertEquals(basket.getReference(), response.get().getBasketReference());

    if (expectCookieFields) {
      assertNotNull(response.get().getCookieName());
      assertNotNull(response.get().getMinutesTillExpiry());
    } else {
      assertNull(response.get().getCookieName());
      assertNull(response.get().getMinutesTillExpiry());
    }

    if (expectRedirectBase) {
      assertNotNull(response.get().getRedirectBase());
    } else {
      assertNull(response.get().getRedirectBase());
    }
  }

  @ParameterizedTest
  @MethodSource("buildFindBookingResponseResNoTestCases")
  void buildFindBookingResponse_withOperaConfNumber_test(
      String resNo,
      String expectedOperaConfNumber) {
    //Arrange
    FindBookingRequest findBookingRequest = FindBookingRequest.builder()
        .resNo(resNo)
        .country("gb")
        .language("en")
        .build();
    BasketResponse basket = mockBasket("BREF1234", "BASKET1234", null);

    when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
        .thenReturn(mockIndexHeader());

    //Act
    var response = manageBookingLogic.buildFindBookingResponse(findBookingRequest, basket);

    //Assert
    assertNotNull(response);
    assertTrue(response.isPresent());
    assertEquals(expectedOperaConfNumber, response.get().getOperaConfNumber());
  }

  @ParameterizedTest
  @MethodSource("findBookingProviderforReservation")
  void buildReservationFindBookingResponse_test(FindBookingRequest findBookingRequest, BasketDto basketDto,
                                     FindBookingResponse expected) {
    //Arrange
    when(contentOutPort.getIndexHeaderData(anyString(), anyString()))
        .thenReturn(mockIndexHeader());
    //Act
    var response = manageBookingLogic.buildReservationFindBookingResponse(findBookingRequest, basketDto);
    var token = response.get().getToken();
    //Assert
    assertNotNull(response);
    assertNotNull(token);
    expected.setToken(token);
    assertEquals(Optional.of(expected), response);
  }

  private static Stream<Arguments> provide3rdPartyBookingFlowTestCases() {
    return Stream.of(
        Arguments.of("BOOKING.COM", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of(), Set.of("KIOSK.WEB"), true),
        Arguments.of("BOOKING.COM", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of(), Set.of("PI.MOBILE"), false),
        Arguments.of("WB_DIGITAL", "01",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of(), Set.of("KIOSK.WEB"), false),
        Arguments.of("WB_DIGITAL", "38",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of(), Set.of("KIOSK.WEB"), true),
        Arguments.of("WB_DIGITAL", "44",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of(), Set.of("KIOSK.WEB"), false),
        Arguments.of("EXPEDIA", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of("EXPEDIA"), Set.of("KIOSK.WEB"), false),
        Arguments.of("BOOKING.COM", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("MOBILE").build(),
            true,
            Set.of(), Set.of("KIOSK.WEB"), false),
        Arguments.of("BOOKING.COM", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            false,
            Set.of(), Set.of("KIOSK.WEB"), false),
        Arguments.of("BOOKING.COM", "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of("BOOKING.COM"), Set.of("KIOSK.WEB"), false),
        Arguments.of(null, "35",
            BookingChannel.builder().channel("KIOSK").subchannel("WEB").build(),
            true,
            Set.of("BOOKING.COM"), Set.of("KIOSK.WEB"), false)
    );
  }

  private static Stream<Arguments> buildFindBookingResponseHeaderTestCases() {
    return Stream.of(
        // Case 1: Full header with complete nested objects and cookie
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockIndexHeader(),
            false,
            true,
            true
        ),
        // Case 2: Exception thrown - NoHeaderDataException
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            null,
            true,
            false,
            false
        ),
        // Case 3: IndexHeader with null config
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockIndexHeaderWithNullConfig(),
            false,
            false,
            false
        ),
        // Case 4: IndexHeader with null bookingSearch
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockIndexHeaderWithNullBookingSearch(),
            false,
            false,
            false
        ),
        // Case 5: IndexHeader with null dashboardRedirect
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockIndexHeaderWithNullDashboardRedirect(),
            false,
            false,
            false
        ),
        // Case 6: DashboardRedirect with null cookie
        Arguments.of(
            mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockIndexHeaderWithNullCookie(),
            false,
            false,
            true
        )
    );
  }

  private static Stream<Arguments> buildFindBookingResponseResNoTestCases() {
    return Stream.of(
        // Case 1: Numeric resNo (Opera confirmation format)
        Arguments.of("12345678", "12345678"),
        // Case 2: Prefixed resNo (ABCDEF + digits)
        Arguments.of("ABCDEF12345678", "12345678"),
        // Case 3: Lowercase prefixed resNo
        Arguments.of("abcdef12345678", "abcdef12345678"),
        // Case 4: Non-Opera format (digital reference)
        Arguments.of("TST1234567", null),
        // Case 5: Non-matching format
        Arguments.of("INVALID", null),
        // Case 6: Empty string
        Arguments.of("", null)
    );
  }


  static Stream<Arguments> findBookingProvider() {
    return Stream.of(
        Arguments.of(mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", null),
            mockFindBookingResponse("BREF1234", "BASKET1234", null, null)),
        Arguments.of(mockFindBookingRequest("BREF1234"),
            mockBasket("BREF1234", "BASKET1234", "35"),
            mockFindBookingResponse("BREF1234", "BASKET1234", "35", null)),
        Arguments.of(mockFindBookingRequest("1234"),
            mockBasket("1234", "BASKET1234", "35"),
            mockFindBookingResponse("1234", "BASKET1234", "35", "1234")),
        Arguments.of(mockFindBookingRequest("abcdef123"),
            mockBasket("abcdef123", "BASKET1234", "35"),
            mockFindBookingResponse("abcdef123", "BASKET1234", "35", "abcdef123"))
    );
  }

  static Stream<Arguments> findBookingProviderforReservation() {
    return Stream.of(
        Arguments.of(mockFindBookingRequest("BREF1234"),
            mockBasketDto("BREF1234", "BASKET1234", null),
            mockFindBookingResponse("BREF1234", "BASKET1234", null, null)),
        Arguments.of(mockFindBookingRequest("BREF1234"),
            mockBasketDto("BREF1234", "BASKET1234", "35"),
            mockFindBookingResponse("BREF1234", "BASKET1234", "35", null)),
        Arguments.of(mockFindBookingRequest("1234"),
            mockBasketDto("1234", "BASKET1234", "35"),
            mockFindBookingResponse("1234", "BASKET1234", "35", "1234")),
        Arguments.of(mockFindBookingRequest("abcdef123"),
            mockBasketDto("abcdef123", "BASKET1234", "35"),
            mockFindBookingResponse("abcdef123", "BASKET1234", "35", "abcdef123"))
    );
  }

  private static FindBookingRequest mockFindBookingRequest(String resNo) {
    return FindBookingRequest.builder()
        .resNo(resNo)
        .lastName("John")
        .arrivalDate("2022-05-05")
        .country("gb")
        .language("en")
        .build();
  }

  private static BasketResponse mockBasket(String bookingRef, String basketRef, String idContext) {
    return BasketResponse.builder()
        .bookingReference(bookingRef)
        .hotelId(HOTEL_ID)
        .reference(basketRef)
        .items(Collections.singletonList(
            BasketItemResponse.builder()
                .sourceId(bookingRef)
                .build()))
        .paymentOption(PAY_ON_ARRIVAL)
        .idContext(idContext)
        .build();
  }

  private static BasketDto mockBasketDto(String bookingRef, String basketRef, String idContext) {
    BasketDto basketDto = new BasketDto();
    basketDto.setBookingReference(bookingRef);
    basketDto.setHotelId(HOTEL_ID);
    basketDto.setReference(basketRef);
    BasketItemDto basketItemDto = new BasketItemDto();
    basketItemDto.setSourceId(bookingRef);
    basketDto.setItems(List.of(basketItemDto));
    basketDto.setPaymentOption(String.valueOf(PaymentOption.PAY_ON_ARRIVAL));
    basketDto.setIdContext(idContext);
    return basketDto;
  }

  private static FindBookingResponse mockFindBookingResponse(String bookingRef, String basketRef,
      String idContext, String operaConfNum) {
    return FindBookingResponse.builder()
        .ref(bookingRef)
        .sourcePms("Opera")
        .basketReference(basketRef)
        .idContext(idContext)
        .cookieName("Test")
        .minutesTillExpiry("30")
        .hotelId(HOTEL_ID)
        .idContext(idContext)
        .redirectBase("testUrl")
        .operaConfNumber(operaConfNum)
        .build();
  }

  private static BookingChannel mockBookingChannel(String channel, String subchannel) {
    return BookingChannel.builder().channel(channel).subchannel(subchannel).build();
  }

  private static uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData mockIndexHeaderWithNullConfig() {
    return uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData.builder()
        .config(null)
        .build();
  }

  private static uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData mockIndexHeaderWithNullBookingSearch() {
    return uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData.builder()
        .config(uk.co.whitbread.reservation.domain.model.index.header.data.out.Config.builder()
            .bookingSearch(null)
            .build())
        .build();
  }

  private static uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData mockIndexHeaderWithNullDashboardRedirect() {
    return uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData.builder()
        .config(uk.co.whitbread.reservation.domain.model.index.header.data.out.Config.builder()
            .bookingSearch(uk.co.whitbread.reservation.domain.model.index.header.data.out.BookingSearch.builder()
                .dashboardRedirect(null)
                .build())
            .build())
        .build();
  }

  private static uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData mockIndexHeaderWithNullCookie() {
    return uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData.builder()
        .config(uk.co.whitbread.reservation.domain.model.index.header.data.out.Config.builder()
            .bookingSearch(uk.co.whitbread.reservation.domain.model.index.header.data.out.BookingSearch.builder()
                .dashboardRedirect(uk.co.whitbread.reservation.domain.model.index.header.data.out.DashboardRedirect.builder()
                    .cookie(null)
                    .operaUrl("testUrl")
                    .build())
                .build())
            .build())
        .build();
  }
}
