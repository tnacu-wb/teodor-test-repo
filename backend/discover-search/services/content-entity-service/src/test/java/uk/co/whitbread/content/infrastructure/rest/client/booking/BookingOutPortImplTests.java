package uk.co.whitbread.content.infrastructure.rest.client.booking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_RATE_INFORMATION_EXCEPTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingDonation;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.Info;
import uk.co.whitbread.content.domain.model.booking.out.Item;
import uk.co.whitbread.content.domain.model.booking.out.PrivacyPolicy;
import uk.co.whitbread.content.domain.model.booking.out.RateClassification;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingDonationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationMapperImpl;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.ItemMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.PrivacyPolicyMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.PromotionPanelMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemBookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemRateInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.InfoDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.InfoMessageDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.ItemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.MessageDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.PaymentInfoDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.PrivacyPolicyDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.RateClassificationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.RatesConfigCommonDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.TermsAndConditionsDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.AemRateOverridesDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.BookingInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateOverrideDetailsDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.OhipException;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.ClassificationsDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.DescriptionDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.PrimaryDetailsDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.RatePlanDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.RatePlansResponseDto;
import uk.co.whitbread.content.domain.model.feature.FeatureFlag;

@ExtendWith(MockitoExtension.class)
class BookingOutPortImplTests {

  private static final String PROMOTIONAL_RATES = "PROBRKST";
  private static final String PROMOTIONAL_PACKAGE = "ADBFPR";

  @InjectMocks
  private BookingOutPortImpl bookingOutPort;

  @Mock
  private BookingAemClient aemClient;

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private BookingInformationMapper bookingInformationMapper;

  @Mock
  private RateInformationMapper rateInformationMapper;

  @Mock
  private BookingInformationRequestMapper bookingInformationRequestMapper;

  @Mock
  private RateInformationRequestMapper rateInformationRequestMapper;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  Exception exception = new Exception();

  @Test
  void getBookingInformation_badInput_ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get booking information.";
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(aemClient.getBookingInformation(any())).thenThrow(
        new AemResponseException(AEM_BOOKING_INFORMATION_EXCEPTION, expectedMessage, exception));
    var bookingInformationRequest = new BookingInformationRequest("null", "null", "null", "null",
        null, null);

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        bookingOutPort.getBookingInformation(bookingInformationRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void geBookingInformation__ShouldReturnOK() {
    //Arrange
    when(aemClient.getBookingInformation((any(BookingInformationRequestAemDto.class))))
        .thenReturn(mockAemBookingInformationDtoResponse());
    when(aemClient.getRateInformationForHotel(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(bookingInformationMapper.toDomainModel(any())).thenReturn(mockBookingInformation());
    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(false);

    //Act
    var bookingInformation = bookingOutPort
        .getBookingInformation(createBookingInformationRequest());

    //AssertOF
    assertThat(bookingInformation, notNullValue());
    assertEquals("Support the Disasters Emergency Committee and the humanitarian effort in Ukraine",
        bookingInformation.getBookingDonation().getName());
    assertEquals("/content/dam/pi/websites/desktop/booking/ukraine/dec-appeal-500x320.png",
        bookingInformation.getBookingDonation().getImageSrc());
    assertThat(bookingInformation.getBookingDonation().getInformationBox(), notNullValue());
    assertThat(bookingInformation.getBookingDonation().getCharityCodes(), notNullValue());

    assertThat(bookingInformation.getPrivacyPolicy(), notNullValue());
    assertEquals(
        "We need to collect and keep some mandatory information in order to process your booking.",
        bookingInformation.getPrivacyPolicy().getDescription());
    assertEquals("We keep your personal data safe and secure.",
        bookingInformation.getPrivacyPolicy().getName());
    assertEquals("View our Privacy Notice", bookingInformation.getPrivacyPolicy().getLinkLabel());
    assertEquals("Find Out More", bookingInformation.getPrivacyPolicy().getMoreInfoLabel());
    assertEquals("/content/dam/global/booking/verisign.png",
        bookingInformation.getPrivacyPolicy().getMoreInfo().get(0).getImage());
    assertEquals("3", bookingInformation.getUpsellItems().get(0).getOrder());
    assertEquals("BARTID", bookingInformation.getUpsellItems().get(0).getBartId());
    assertEquals("CODE", bookingInformation.getUpsellItems().get(0).getCode());
    assertFalse(bookingInformation.getUpsellItems().stream()
        .anyMatch(item -> item.getCode().equals(PROMOTIONAL_PACKAGE)));

    verify(bookingInformationMapper).toDomainModel(any(AemBookingInformationDto.class));

  }

  @Test
  void geBookingInformationWithRatePlanCodes__ShouldReturnOK() {
    //Arrange
    when(aemClient.getBookingInformation((any(BookingInformationRequestAemDto.class))))
        .thenReturn(mockAemBookingInformationDtoResponse());
    when(aemClient.getRateInformationForHotel(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any())).thenReturn(
        mockRatePlansDto("FLEXRATE"));
    when(bookingInformationMapper.toDomainModel(any())).thenReturn(mockBookingInformation());
    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(false);

    //Act
    var bookingInformation = bookingOutPort
        .getBookingInformation(createBookingInformationRequestWithRatePlanCodes());

    //AssertOF
    assertThat(bookingInformation, notNullValue());
    assertEquals("Support the Disasters Emergency Committee and the humanitarian effort in Ukraine",
        bookingInformation.getBookingDonation().getName());
    assertEquals("/content/dam/pi/websites/desktop/booking/ukraine/dec-appeal-500x320.png",
        bookingInformation.getBookingDonation().getImageSrc());
    assertThat(bookingInformation.getBookingDonation().getInformationBox(), notNullValue());
    assertThat(bookingInformation.getBookingDonation().getCharityCodes(), notNullValue());

    assertThat(bookingInformation.getPrivacyPolicy(), notNullValue());
    assertEquals(
        "We need to collect and keep some mandatory information in order to process your booking.",
        bookingInformation.getPrivacyPolicy().getDescription());
    assertEquals("We keep your personal data safe and secure.",
        bookingInformation.getPrivacyPolicy().getName());
    assertEquals("View our Privacy Notice", bookingInformation.getPrivacyPolicy().getLinkLabel());
    assertEquals("Find Out More", bookingInformation.getPrivacyPolicy().getMoreInfoLabel());
    assertEquals("/content/dam/global/booking/verisign.png",
        bookingInformation.getPrivacyPolicy().getMoreInfo().get(0).getImage());
    assertEquals("3", bookingInformation.getUpsellItems().get(0).getOrder());
    assertEquals("BARTID", bookingInformation.getUpsellItems().get(0).getBartId());
    assertEquals("CODE", bookingInformation.getUpsellItems().get(0).getCode());
    assertFalse(bookingInformation.getUpsellItems().stream()
        .anyMatch(item -> item.getCode().equals(PROMOTIONAL_PACKAGE)));

    verify(bookingInformationMapper).toDomainModel(any(AemBookingInformationDto.class));

  }

  @Captor ArgumentCaptor<List<String>> captor;
  @Test
  void getBookingInformationWithRatePlanCodes__ShouldSortRatePlanCodesBeforeClientCall() {
    //Arrange
    when(aemClient.getBookingInformation((any(BookingInformationRequestAemDto.class))))
            .thenReturn(mockAemBookingInformationDtoResponse());
    when(aemClient.getRateInformationForHotel(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
            new BookingInformationRequestAemDto());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any())).thenReturn(
            mockRatePlansDto("FLEXRATE"));
    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(false);
    var request = createBookingInformationRequestWithRatePlanCodes();
    List<String> ratePlanCodesToSort = new ArrayList<>(request.getRatePlanCodes());
    Collections.sort(ratePlanCodesToSort);

    //Act
    bookingOutPort
            .getBookingInformation(request);

    //assert
    verify(ohipAdapterClient).sendGetRatePlansRequest(captor.capture(),any());
    assertEquals(ratePlanCodesToSort, captor.getValue());
}

  @Test
  void getBookingInformation_ShouldReturnResourceNotFoundException() {
    //Arrange
    var debugMessage = "Unable to get booking information 1234";
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(aemClient.getBookingInformation(any())).thenThrow(
        new AemResponseException(AEM_BOOKING_INFORMATION_EXCEPTION, debugMessage, exception));
    var bookingInformationRequest = new BookingInformationRequest("gb", "n/a", "booking-a1",
        "DUBSOU", null, null);

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        bookingOutPort.getBookingInformation(bookingInformationRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(debugMessage));
  }

  @Test
  void getBookingInformationWithRatePlanCodes__ShouldReturnException() {
    //Arrange
    var bookingInformationRequest = new BookingInformationRequest("gb", "n/a", "booking-a1",
        "DUBSOU", new ArrayList<>(List.of("FLEXRATE")), null);
    var ohipException = new OhipException(
        "message",
        "debug message",
        new Exception(),
        900);
    when(aemClient.getBookingInformation((any(BookingInformationRequestAemDto.class))))
        .thenReturn(mockAemBookingInformationDtoResponse());
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any())).thenThrow(ohipException);

    //Act
    var actual = assertThrows(OhipException.class, () ->
        bookingOutPort.getBookingInformation(bookingInformationRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is("message"));
    assertThat(actual.getDebugMessage(), is("debug message"));
    assertThat(actual.getErrorCode(), is(900));
  }

  @Test
  void geBookingInformationWithPromoRatePlanCode__filterRegularPackages__ShouldReturnOK() {
    //Arrange
    var bookingDonationMapper = Mappers.getMapper(BookingDonationMapper.class);
    var privacyPolicyMapper = Mappers.getMapper(PrivacyPolicyMapper.class);
    var promotionPanelMapper = Mappers.getMapper(PromotionPanelMapper.class);
    var itemMapper = Mappers.getMapper(ItemMapper.class);
    var bookingInfoMapper = new BookingInformationMapperImpl(bookingDonationMapper, privacyPolicyMapper,
        promotionPanelMapper, itemMapper);
    var bookingInformationRequest = createBookingInformationRequestWithRatePlanCodes();
    when(aemClient.getBookingInformation((any(BookingInformationRequestAemDto.class))))
        .thenReturn(mockAemBookingInformationDtoResponse());
    when(bookingInformationRequestMapper.toDtoModel(any())).thenReturn(
        new BookingInformationRequestAemDto());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any())).thenReturn(
        mockRatePlansDto("FLEXRATE"));
    when(aemClient.getRateInformationForHotel(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex",
            "DPROMO1,DPROMO2," + PROMOTIONAL_RATES));
    when(bookingInformationMapper.toDomainModel(any())).thenAnswer(invocation -> {
      AemBookingInformationDto aemBookingInformationDto = invocation.getArgument(0);
      return bookingInfoMapper.toDomainModel(aemBookingInformationDto);
    });
    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(false);

    //Act
    var bookingInformation = bookingOutPort.getBookingInformation(bookingInformationRequest);

    //AssertOF
    assertThat(bookingInformation, notNullValue());
    assertEquals(1, bookingInformation.getUpsellItems().size());
    assertEquals("4", bookingInformation.getUpsellItems().get(0).getOrder());
    assertEquals("11", bookingInformation.getUpsellItems().get(0).getBartId());
    assertEquals(PROMOTIONAL_PACKAGE, bookingInformation.getUpsellItems().get(0).getCode());
  }

  @Test
  void getRateInformation_aemRateNameNotBlank_ShouldReturnAemRateName() {
    //Arrange
    when(aemClient.getRateInformationForBrand(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU")
            .ratePlans(Collections.singletonList("FLEXRATE")).build());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockRatePlansDto("FLEXRATE"));
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemRatesOverrideDetails());

    //Act
    var rateInformation = bookingOutPort
        .getRateInformation(createRateInformationRequest());

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("FLEXRATE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInformation.getRateClassifications().get(0).getRateDescription());

  }

  @Captor ArgumentCaptor<List<String>> ratePlansCaptor;
  @Test
  void getRateInformation__ShouldSortRatePlanCodesBeforeClientCall() {
    //Arrange
    when(aemClient.getRateInformationForBrand(any(RateInformationRequestAemDto.class)))
            .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    List<String> ratePlanCodes = new ArrayList<>(List.of("SEMIFLEX","FLEXRATE"));
    List<String> ratePlanCodesToSort = new ArrayList<>(ratePlanCodes);
    Collections.sort(ratePlanCodesToSort);
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
            RateInformationRequestAemDto.builder()
                    .country("gb")
                    .language("en")
                    .hotelId("DUBSOU")
                    .ratePlans(ratePlanCodes).build());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
            .thenReturn(mockRatePlansDto("FLEXRATE"));
    when(aemClient.getRatesOverrideDetails(any()))
            .thenReturn(mockAemRatesOverrideDetails());

    //Act
    bookingOutPort
            .getRateInformation(createRateInformationRequest());
    //assert
    verify(ohipAdapterClient).sendGetRatePlansRequest(ratePlansCaptor.capture(),any());
    assertEquals(ratePlanCodesToSort, ratePlansCaptor.getValue());
  }

  @Test
  void getRateInformationWithHotel__ShouldReturnOkAndCallAEMHotelEndpoint() {
    //Arrange
    RateInformationRequest request = RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .hotelId("LONHOL")
        .build();
    when(aemClient.getRateInformationForHotel(any()))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemRatesOverrideDetails());

    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU")
            .ratePlans(Collections.singletonList("FLEXRATE")).build());

    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockRatePlansDto("FLEXRATE"));

    //Act
    var rateInformation = bookingOutPort.getRateInformation(request);

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("FLEXRATE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInformation.getRateClassifications().get(0).getRateDescription());
    verify(aemClient).getRateInformationForHotel(any());

  }

  @Test
  void getRateInformationForEmployeeRate__ShouldReturnOkAndCallAEMHotelEndpoint() {
    //Arrange
    RateInformationRequest request = RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .hotelId("LONHOL")
        .build();
    when(aemClient.getRateInformationForHotel(any()))
        .thenReturn(mockAemEmployeeRateInformationDtoResponse());
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemEmployeeRatesOverrideDetails());

    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("LONHOL").build());

    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockEmployeeRatePlansDto("EMPLOYEE"));

    //Act
    var rateInformation = bookingOutPort.getRateInformation(request);

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("EMPLOYEE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Whitbread Employee Discount",
        rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("2", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("Employee override long description",
        rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals(
        "Employee override description",
        rateInformation.getRateClassifications().get(0).getRateDescription());
    assertEquals("Employee override notes",
        rateInformation.getRateClassifications().get(0).getRateNotes());
    assertEquals("Free breakfast",
        rateInformation.getRateClassifications().get(0).getRateTags().get(0));

    verify(aemClient).getRateInformationForHotel(any());
  }

  @Test
  void getRateInformationCheck__ShouldReturnOk_WithNullRateTag_AndCallAEMHotelEndpoint() {
    //Arrange
    RateInformationRequest request = RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .hotelId("LONHOL")
        .build();
    when(aemClient.getRateInformationForHotel(any()))
        .thenReturn(mockAemEmployeeRateInformationDtoResponse());
    var mockAemEmployeeRatesOverrideDetails = mockAemEmployeeRatesOverrideDetails();
    mockAemEmployeeRatesOverrideDetails.getRateOverrides().get(0).setRateTags(null);
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemEmployeeRatesOverrideDetails());

    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("LONHOL").build());

    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockEmployeeRatePlansDto(anyString()));

    //Act
    var rateInformation = bookingOutPort.getRateInformation(request);

    //Assert
    assertThat(rateInformation, notNullValue());
    assertEquals(0, rateInformation.getRateClassifications().get(0).getRateTags().size());
    verify(aemClient).getRateInformationForHotel(any());
  }

  @Test
  void getRateInformationWithOverride__ShouldReturnOK() {
    //Arrange
    when(aemClient.getRateInformationForBrand(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU")
            .ratePlans(Collections.singletonList("FLEXRATE")).build());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockRatePlansDto("BUSIFLEX"));
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemRatesOverrideDetails());

    //Act
    var rateInformation = bookingOutPort
        .getRateInformation(createRateInformationRequest());

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("BUSIFLEX",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Override Name", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("2", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("Override long description",
        rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Some description",
        rateInformation.getRateClassifications().get(0).getAdditionalDescription());
    assertEquals("Override description",
        rateInformation.getRateClassifications().get(0).getRateDescription());

  }

  @Test
  void getHotelRateInformationWithHotel__ShouldReturnOk() {
    //Arrange
    RateInformationRequest request = RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .hotelId("LONARC")
        .build();
    when(aemClient.getRateInformationForHotel(any()))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));

    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU")
            .ratePlans(Collections.singletonList("FLEXRATE")).build());
    when(rateInformationMapper.toDomainModel(any())).thenReturn(mockRateInformation());

    //Act
    var rateInformation = bookingOutPort.getHotelRateInformation(request);

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("FLEXRATE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInformation.getRateClassifications().get(0).getRateDescription());
    verify(aemClient).getRateInformationForHotel(any());

  }

  @Test
  void getHotelRateInformation_ShouldReturnResourceNotFoundException() {
    var request = RateInformationRequest
        .builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
    var expectedMessage = String.format("Unable to get booking information for hotel %s",
        request.getHotelId());
    //Arrange
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        new RateInformationRequestAemDto());
    when(aemClient.getRateInformationForHotel(any())).thenThrow(
        new AemResponseException(AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION, expectedMessage,
            exception));

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        bookingOutPort.getHotelRateInformation(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void getHotelRateInformation_badInput_ShouldReturnException() {
    //Arrange
    var request = RateInformationRequest
        .builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
    var expectedMessage = String.format("Unable to get booking information for hotel %s",
        request.getHotelId());
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        new RateInformationRequestAemDto());
    when(aemClient.getRateInformationForHotel(any())).thenThrow(new AemResponseException(
        AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION,
        String.format("Unable to get booking information for hotel %s", request.getHotelId()),
        exception));

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        bookingOutPort.getHotelRateInformation(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getMessage(), is(expectedMessage));
  }

  @Test
  void getRateInformation_aemRateNameBlank_ShouldReturnOperaRateName() {
    //Arrange
    when(aemClient.getRateInformationForBrand(any(RateInformationRequestAemDto.class)))
        .thenReturn(mockAemRateInformationDtoResponse(" "));
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU")
            .ratePlans(Collections.singletonList("FLEXRATE")).build());
    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
        .thenReturn(mockRatePlansDto("FLEXRATE"));
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemRatesOverrideDetails());

    //Act
    var rateInformation = bookingOutPort
        .getRateInformation(createRateInformationRequest());

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("FLEXRATE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex Rate", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInformation.getRateClassifications().get(0).getRateDescription());

  }

  @Test
  void getHotelRateInformation_promotionRatesIncluded__ShouldReturnOk() {
    //Arrange
    RateInformationRequest request = RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .hotelId("LONHOL")
        .build();
    when(aemClient.getRateInformationForHotel(any()))
        .thenReturn(mockAemRateInformationDtoResponse("Flex"));
    when(aemClient.getRatesOverrideDetails(any()))
        .thenReturn(mockAemRatesOverrideDetails());

    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        RateInformationRequestAemDto.builder()
            .country("gb")
            .language("en")
            .hotelId("DUBSOU").build());
    var ratePlans = Arrays.asList("FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD", "NONFLEX",
        "EMPLOYEE", "BUSIFLEX", "PROMO1", "PROMO2", "DPROMO1", "DPROMO2");

    when(ohipAdapterClient.sendGetRatePlansRequest(eq(ratePlans), any()))
        .thenReturn(mockRatePlansDto("FLEXRATE"));

    //Act
    var rateInformation = bookingOutPort.getRateInformation(request);

    //AssertOF
    assertThat(rateInformation, notNullValue());
    assertEquals("FLEXRATE",
        rateInformation.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInformation.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInformation.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInformation.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInformation.getRateClassifications().get(0).getRateDescription());
    verify(aemClient).getRateInformationForHotel(any());
  }

  private AemRateOverridesDto mockAemRatesOverrideDetails() {
    return AemRateOverridesDto
        .builder()
        .rateOverrides(Collections.singletonList(RateOverrideDetailsDto
            .builder()
            .ratePlanCode("BUSIFLEX")
            .additionalDescription("Some description")
            .rateName("Override Name")
            .rateOrder("2")
            .rateDescription("Override description")
            .rateLongDescription("Override long description")
            .rateNotes("Override notes")
            .build()))
        .build();
  }

  private AemRateOverridesDto mockAemEmployeeRatesOverrideDetails() {
    return AemRateOverridesDto
        .builder()
        .rateOverrides(Collections.singletonList(RateOverrideDetailsDto
            .builder()
            .ratePlanCode("EMPLOYEE")
            .additionalDescription("Some description")
            .rateName("Whitbread Employee Discount")
            .rateOrder("2")
            .rateDescription("Employee override description")
            .rateLongDescription("Employee override long description")
            .rateNotes("Employee override notes")
            .rateTags(List.of("Free breakfast"))
            .build()))
        .build();
  }

  private RatePlansResponseDto mockRatePlansDto(String ratePlanCode) {
    return RatePlansResponseDto
        .builder()
        .ratePlans(Collections.singletonList(mockRatePlanDto(ratePlanCode)))
        .build();
  }

  private RatePlansResponseDto mockEmployeeRatePlansDto(String ratePlanCode) {
    return RatePlansResponseDto
        .builder()
        .ratePlans(Collections.singletonList(RatePlanDto.builder()
            .primaryDetails(mockPrimaryDetailsDto())
            .ratePlanCode(ratePlanCode)
            .classifications(
                ClassificationsDto.builder().displaySet("DIS").rateCategory("B").marketCode("OTH")
                    .build())
            .build()))
        .build();
  }


  private RatePlanDto mockRatePlanDto(String ratePlanCode) {
    return RatePlanDto.builder()
        .primaryDetails(mockPrimaryDetailsDto())
        .ratePlanCode(ratePlanCode)
        .classifications(
            ClassificationsDto.builder().displaySet("PBN").rateCategory("U").marketCode("OTH")
                .build())
        .build();
  }


  private PrimaryDetailsDto mockPrimaryDetailsDto() {
    return PrimaryDetailsDto
        .builder()
        .description(DescriptionDto.builder().defaultText("Flex Rate").build())
        .build();

  }

  @Test
  void getRateInformation_badInput_ShouldReturnException() {
    //Arrange
    var request = RateInformationRequest
        .builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
    var expectedMessage = String.format("Unable to get rate information for brand: %s",
        request.getBrand());
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        new RateInformationRequestAemDto());
    when(aemClient.getRateInformationForBrand(any())).thenThrow(new AemResponseException(
        AEM_RATE_INFORMATION_EXCEPTION, expectedMessage, exception));

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> bookingOutPort.getRateInformation(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void getRateInformation_ShouldReturnResourceNotFoundException() {
    //Arrange
    var request = RateInformationRequest
        .builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
    String expectedMessage = String.format("Unable to get rate information for brand.: %s",
        request.getBrand());
    when(rateInformationRequestMapper.toDto(any())).thenReturn(
        new RateInformationRequestAemDto());
    when(aemClient.getRateInformationForBrand(any())).thenThrow(
        new AemResponseException(AEM_RATE_INFORMATION_EXCEPTION, expectedMessage, exception));

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        bookingOutPort.getRateInformation(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void getBookingInformation_featureFlagEnabled_ShouldSkipFilterPackages() {
    // Arrange
    var bookingDonationMapper = Mappers.getMapper(BookingDonationMapper.class);
    var privacyPolicyMapper = Mappers.getMapper(PrivacyPolicyMapper.class);
    var promotionPanelMapper = Mappers.getMapper(PromotionPanelMapper.class);
    var itemMapper = Mappers.getMapper(ItemMapper.class);

    var bookingInfoMapper = new BookingInformationMapperImpl(
            bookingDonationMapper,
            privacyPolicyMapper,
            promotionPanelMapper,
            itemMapper
    );

    var bookingInformationRequest = createBookingInformationRequestWithRatePlanCodes();

    when(aemClient.getBookingInformation(any()))
            .thenReturn(mockAemBookingInformationDtoResponse());

    when(bookingInformationRequestMapper.toDtoModel(any()))
            .thenReturn(new BookingInformationRequestAemDto());

    when(ohipAdapterClient.sendGetRatePlansRequest(anyList(), any()))
            .thenReturn(mockRatePlansDto("FLEXRATE"));

    when(aemClient.getRateInformationForHotel(any()))
            .thenReturn(mockAemRateInformationDtoResponse(
                    "Flex",
                    "DPROMO1,DPROMO2," + PROMOTIONAL_RATES
            ));

    when(bookingInformationMapper.toDomainModel(any())).thenAnswer(invocation -> {
      AemBookingInformationDto dto = invocation.getArgument(0);
      return bookingInfoMapper.toDomainModel(dto);
    });

    when(unleashWrapper.featureFlag()).thenReturn(mockFeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    // Act
    var bookingInformation = bookingOutPort.getBookingInformation(bookingInformationRequest);

    // Assert
    assertThat(bookingInformation, notNullValue());


    assertEquals(4, bookingInformation.getUpsellItems().size());

    assertThat(
            bookingInformation.getUpsellItems().stream()
                    .anyMatch(item -> item.getCode().equals(PROMOTIONAL_PACKAGE)),
            is(true)
    );
  }

  private AemBookingInformationDto mockAemBookingInformationDtoResponse() {
    AemBookingInformationDto bookingInformationDto = new AemBookingInformationDto();
    bookingInformationDto.setRestaurantClosedTitle("Restaurant unavailable");
    bookingInformationDto.setRestaurantClosedMessage(
        "We're sorry, the restaurant at this hotel is closed on your selected dates.");
    bookingInformationDto.setMealsNotAvailableTitle("Important restaurant information");
    bookingInformationDto.setMealsNotAvailableMessage(
        "We're sorry, the restaurant at this hotel isn't serving breakfasts or Meal Deals on the dates you've selected.");
    bookingInformationDto.setInfoMessage(new ArrayList<>());
    bookingInformationDto.getInfoMessage().add(mockInfoMessageDto());
    bookingInformationDto.setPrivacyPolicy(mockPrivacyPolicyDto());
    bookingInformationDto.setTermsAndConditions(mockTermsAndConditionsDto());
    bookingInformationDto.setPaymentInfoMessages(mockPaymentInfoMessagesDto());
    bookingInformationDto.setUpsellitemsConfiguration(mockItemsDto());

    return bookingInformationDto;
  }

  private List<PaymentInfoDto> mockPaymentInfoMessagesDto() {
    return new ArrayList<>(List.of(PaymentInfoDto.builder()
        .rateCategory("U")
        .rateDisplaySet("PBN")
        .build()));
  }

  private BookingInformation mockBookingInformation() {
    BookingInformation bookingInformation = new BookingInformation();
    bookingInformation.setBookingDonation(BookingDonation.builder()
        .imageSrc("/content/dam/pi/websites/desktop/booking/ukraine/dec-appeal-500x320.png")
        .name("Support the Disasters Emergency Committee and the humanitarian effort in Ukraine")
        .informationBox(
            "<p>We’ve been proudly raising money for Great Ormond Street Hospital Children’s Charity</p>")
        .description("Description").charityCodes(List.of("CHRTY3", "CHRTY4", "CHRTY5")).build());
    bookingInformation.setPrivacyPolicy(PrivacyPolicy.builder()
        .description(
            "We need to collect and keep some mandatory information in order to process your booking.")
        .name("We keep your personal data safe and secure.")
        .linkLabel("View our Privacy Notice")
        .linkSrc("/content/pi/websites/desktop/gb/en/secured/terms/privacy-policy.html")
        .moreInfoLabel("Find Out More")
        .moreInfo(Collections.singletonList(Info.builder()
            .image("/content/dam/global/booking/verisign.png")
            .description("test description")
            .build()))
        .build());
    bookingInformation.setUpsellItems(mockItem());
    return bookingInformation;
  }

  private PrivacyPolicyDto mockPrivacyPolicyDto() {
    PrivacyPolicyDto privacyPolicy = new PrivacyPolicyDto();
    privacyPolicy.setDescription(
        "We need to collect and keep some mandatory information in order to process your booking.");
    privacyPolicy.setTitle("We keep your personal data safe and secure.");
    privacyPolicy
        .setLinkPath("/content/pi/websites/desktop/gb/en/secured/terms/privacy-policy.html");
    privacyPolicy.setLinkLabel("View our Privacy Notice");
    privacyPolicy.setMoreInfoLabel("View our Privacy Notice");
    privacyPolicy.setMoreInfo(Collections.singletonList(InfoDto.builder()
        .image("/content/dam/global/booking/verisign.png")
        .description("test description")
        .build()));
    privacyPolicy.setMoreInfoLabel("Find Out More");

    return privacyPolicy;
  }

  private InfoMessageDto mockInfoMessageDto() {
    InfoMessageDto infoMessage = new InfoMessageDto();
    infoMessage.setRate("FLEXRATE");
    infoMessage.setMessages(List.of(MessageDto.builder().
        message(
            "You can amend or cancel your booking any time up to 1pm on the day you’re due to arrive.")
        .build()));
    return infoMessage;
  }

  private List<TermsAndConditionsDto> mockTermsAndConditionsDto() {
    return new ArrayList<>(
        List.of(
            TermsAndConditionsDto.builder().rate("A").rateCategory("U").rateDisplaySet("PBN").text(
                    "<p>I have read, understand and accept the&nbsp;<a href=\\\"/content/pi/websites/desktop/gb/en/secured/terms/terms-uk.html\\\">Terms and Conditions</a>. Cancellations must be made before 1pm on your arrival day.</p>")
                .build(),
            TermsAndConditionsDto.builder().rate("S").rateCategory("U").rateDisplaySet("PBN").text(
                    "<p>I have read, understand and accept the&nbsp;<a href=\\\"/content/pi/websites/desktop/gb/en/secured/terms/terms-uk.html\\\">Terms and Conditions</a>.&nbsp;Cancellations must be made 28 days prior to the arrival date.</p>")
                .build()));
  }

  private BookingInformationRequest createBookingInformationRequest() {
    return BookingInformationRequest.builder()
        .country("gb")
        .language("en")
        .bookingFlowId("booking-a1")
        .hotelId("DUBSOU")
        .build();
  }

  private BookingInformationRequest createBookingInformationRequestWithRatePlanCodes() {
    return BookingInformationRequest.builder()
        .country("gb")
        .language("en")
        .bookingFlowId("booking-a1")
        .hotelId("DUBSOU")
        .ratePlanCodes(new ArrayList<>(List.of("SEMIFLEX","FLEXRATE")))
        .reservationRatePlanCode(PROMOTIONAL_RATES)
        .build();
  }

  private RateInformationRequest createRateInformationRequest() {
    return RateInformationRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .ratePlans(Collections.singletonList("FLEXRATE"))
        .build();
  }

  private RateInformation mockRateInformation() {
    RateInformation rateInformation = new RateInformation();
    RateClassification rateClassification = RateClassification.builder()
        .rateClassification("FLEXRATE")
        .rateOrder("1")
        .rateName("Flex")
        .rateDescription("Pay now or on arrival, fully refundable with free cancellation up to 1pm")
        .rateLongDescription("")
        .build();
    rateInformation.setRateClassifications(Collections.singletonList(rateClassification));
    return rateInformation;
  }

  private AemRateInformationDto mockAemRateInformationDtoResponse(String rateName) {
    return mockAemRateInformationDtoResponse(rateName, "PROMO1,PROMO2");
  }

  private AemRateInformationDto mockAemRateInformationDtoResponse(String rateName,
      String promotionalRates) {
    AemRateInformationDto aemRateInformationDto = new AemRateInformationDto();
    RatesConfigCommonDto ratesConfigCommonDto = RatesConfigCommonDto.builder()
        .defaultRates("FLEXRATE,SEMIFLEX,ADVANCE,STANDARD,NONFLEX,EMPLOYEE,BUSIFLEX")
        .promotionalRates(promotionalRates)
        .promotionalDiscountRates("DPROMO1,DPROMO2")
        .promotionalPackage(PROMOTIONAL_PACKAGE)
        .build();
    aemRateInformationDto.setRatesConfigCommon(ratesConfigCommonDto);
    RateClassificationDto rateClassification = RateClassificationDto.builder()
        .rateClassification("FLEXRATE")
        .rateCategory("U")
        .rateDisplaySet("PBN")
        .rateOrder("1")
        .rateName(rateName)
        .rateDescription("Pay now or on arrival, fully refundable with free cancellation up to 1pm")
        .rateLongDescription("")
        .build();
    aemRateInformationDto.setRateClassifications(Collections.singletonList(rateClassification));

    return aemRateInformationDto;
  }

  private AemRateInformationDto mockAemEmployeeRateInformationDtoResponse() {
    AemRateInformationDto aemRateInformationDto = new AemRateInformationDto();
    RateClassificationDto rateClassification = RateClassificationDto.builder()
        .rateClassification("EMPLOYEE")
        .rateCategory("B")
        .rateDisplaySet("DIS")
        .rateOrder("1")
        .rateName("Flex (Employee Discount Applied)")
        .rateDescription(
            "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival")
        .rateLongDescription("")
        .build();
    aemRateInformationDto.setRateClassifications(Collections.singletonList(rateClassification));

    return aemRateInformationDto;
  }

  private List<Item> mockItem() {
    return List.of(Item.builder()
        .code("CODE")
        .bartId("BARTID")
        .order("3")
        .build());
  }

  private List<ItemDto> mockItemsDto() {
    var premierInnBreakfast = ItemDto.builder()
        .code("BFADBF")
        .bartCode("11")
        .order("1")
        .build();
    var continentalBreakfast = ItemDto.builder()
        .code("BFADCT")
        .bartCode("12")
        .order("2")
        .build();
    var mealDeal = ItemDto.builder()
        .code("MDP")
        .bartCode("17")
        .order("3")
        .build();
    var promoBreakfast = ItemDto.builder()
        .code(PROMOTIONAL_PACKAGE)
        .bartCode("11")
        .order("4")
        .build();
    return new ArrayList<>(List.of(premierInnBreakfast, continentalBreakfast, mealDeal, promoBreakfast));
  }

  private FeatureFlag mockFeatureFlag() {
    FeatureFlag featureFlag = new FeatureFlag();

    FeatureFlag.Feature feature = new FeatureFlag.Feature();
    feature.setKey("release_pi_free_fnb_and_extras");
    feature.setFallback(false);

    featureFlag.setFreeFnbExtras(feature);

    return featureFlag;
  }
}
