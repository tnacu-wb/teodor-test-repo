package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessAllowanceDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelInformationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeLogResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmAmendOnReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmationCustomerDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmationRoomStayDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateMemoRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CustomerDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MarketingPreferencesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationCreationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreferencesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypeChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SpecialRequestsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIdTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateBookerEmailRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateCancellationPoliciesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateDiscountRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions.DiscountInvalidAmountException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions.HotelAvailabilityException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.AmendDistributionSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.SearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterTimeoutConfiguredClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.RoomRateDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.RoomTypeDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipAdapterClientTest {

  public static final String HOTEL_ID = "TKINPT";
  public static final String ARRIVAL_DATE = "2022-03-01";
  public static final String OPERA_END_DATE = "2022-03-02";
  public static final String DEPARTURE_DATE = "2022-03-03";
  public static final List<Integer> ADULTS = of(1, 2);
  public static final List<String> ROOM_TYPES = Collections.singletonList("DB");

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private OhipAdapterProperties ohipAdapterProperties;
  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;
  @InjectMocks
  private OhipAdapterTimeoutConfiguredClient ohipAdapterTimeoutConfiguredClient;

  @Test
  void testCreateReservation_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationResponseDto.class)).thenReturn(
        mockCreateReservationResponse());

    // Act
    var response = ohipAdapterClient.createReservation(new ReservationRequestDto());

    // Assert
    assertThat(response.getReservations().get(0).getReservationId(), is("123"));
  }

  @Test
  void sendGetReservationsByReservationId_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationIdDetailsDto.class)).thenReturn(
        mockSendGetReservationsByResId());

    // Act
    var response = ohipAdapterClient.sendGetReservationsByReservationId("122345", "MANOLD");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void createReservation_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ReservationResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ReservationResponseDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.createReservation(new ReservationRequestDto()));
  }

  @Test
  void testGetReservationsByBasketReference_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetailsResponseDto.class)).thenReturn(
        mockReservationsDetailsResponse());

    // Act
    var response = ohipAdapterClient.getReservationsByBasketReference("123", "456", 20, 0);

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationsByBasketReference_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ReservationsDetailsResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ReservationsDetailsResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getReservationsByBasketReference("123", "456", 20, 0));
  }

  @Test
  void testConfirmReservation_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getConfirmReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ConfirmReservationResponseDto.class)).thenReturn(
        mockConfirmReservationResponse());

    // Act
    var response = ohipAdapterClient.sendConfirmReservationRequest(
        new ConfirmReservationRequestDto());

    // Assert
    assertThat(response.getReservationIdList().get(1).getId(), is("264873"));
    assertThat(response.getReservationStatus(), is("Reserved"));
  }

  @Test
  void testCreateReservationGuest_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationGuestEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationGuestResponseDto.class)).thenReturn(
        mockCreateReservationGuestResponse());

    // Act
    var response = ohipAdapterClient.sendReservationGuestRequest(
        new ReservationGuestRequestDto());

    // Assert

    assertThat(response.getHotelId(), is("LONEUS"));
    assertThat(response.getReservationIds().get(0), is("1234"));
  }

  @Test
  void sendReservationGuestRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ReservationGuestResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ReservationGuestResponseDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationGuestEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendReservationGuestRequest(
            new ReservationGuestRequestDto()));
  }

  @Test
  void testCancelReservation_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getCancelReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelReservationResponseDto.class)).thenReturn(
        mockCancelReservationResponse());

    // Act
    var response = ohipAdapterClient.sendCancelReservationRequest(
        new CancelReservationRequestDto());

    // Assert
    assertThat(response.getCancellationIds().get(0), is("1234"));
  }

  @Test
  void sendCancelReservationRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<CancelReservationResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(CancelReservationResponseDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getCancelReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendCancelReservationRequest(new CancelReservationRequestDto()));
  }

  @Test
  void sendGetCancelInformationRequest__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelInformationResponseDto.class)).thenReturn(
        mockCancelInformationResponse());

    //Act
    CancelInformationResponseDto cancelInformationResponse =
        this.ohipAdapterClient.sendGetCancelInformationRequest("TestHotelId",
            Collections.singleton("12345"), "2022-11-29T10:59:11");

    //Assert
    assertThat(cancelInformationResponse, notNullValue());
  }

  @Test
  void sendGetCancelInformationRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<CancelInformationResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(CancelInformationResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetCancelInformationRequest("hotel", Set.of("1234"),
            "userDateTime"));
  }

  @Test
  void testUpdateCompanyQuestionAndAnswerDetailsRequests_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    var companyQuestionAndAnswerDetailsRequestDto = createCompanyQuestionAndAnswerDetailsRequestDto();
    ohipAdapterClient.sendUpdateCompanyQuestionAndAnswerDetailsRequests(
        companyQuestionAndAnswerDetailsRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateDiscount_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateDiscountEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    var updateDiscountRequestDto = createUpdateDiscountRequestDto();
    ohipAdapterClient.sendUpdateDiscountRequest(updateDiscountRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateBusinessItems_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateBusinessItemsEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    BusinessItemsRequestDto businessItemsRequestDto = createBusinessItemsRequestDto();
    ohipAdapterClient.sendUpdateBusinessItemsRequest(businessItemsRequestDto);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateSpecialRequests_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateSpecialRequestsEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    var specialRequestsDto = createSpecialRequestsDto();
    ohipAdapterClient.sendUpdateSpecialRequests(specialRequestsDto);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  private SpecialRequestsDto createSpecialRequestsDto() {
    var specialReq = new SpecialRequestsDto();
    specialReq.setHotelId("MANOLD");
    return specialReq;
  }

  @Test
  void testSearchBookings_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SearchBookingsResponseDto.class)).thenReturn(
        mockSearchBookingsResponse());
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Act
    var searchBookingsRequestDto = createSearchBookingsRequestDto();
    ohipAdapterClient.sendSearchBookings(searchBookingsRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateDiscount_failure_BadRequest() {
    // Arrange
    DiscountInvalidAmountException ex = mock(DiscountInvalidAmountException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateDiscountEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act
    var updateDiscountRequestDto = createUpdateDiscountRequestDto();
    Exception exception = assertThrows(DiscountInvalidAmountException.class,
        () -> ohipAdapterClient.sendUpdateDiscountRequest(updateDiscountRequestDto));

    // Assert
    assertThat(exception, notNullValue());
  }

  @Test
  void updateReasonForStayRequest_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateReasonForStayEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(UpdateReasonForStayResponseDto.class)).thenReturn(
        mockUpdateReasonForStayResponse());
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Act
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = createReasonForStayRequestDto();
    ohipAdapterClient.sendUpdateReasonForStayRequest(updateReasonForStayRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateReservationOverrideReasons_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationOverrideReasonsEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    var updateReservationOverrideReasonsRequestDto = createUpdateReservationOverrideReasonsRequestDto();
    ohipAdapterClient.sendUpdateReservationOverrideReasons(
        updateReservationOverrideReasonsRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendUpdateReservationOverrideReasons_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationOverrideReasonsEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationOverrideReasons(
            new UpdateReservationOverrideReasonsRequestDto()));
  }

  @Test
  void testUpdateReservationCcAgentId_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationCcAgentIdEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    var updateReservationCcAgentIdRequestDto = createUpdateReservationCcAgentIdRequestDto();
    ohipAdapterClient.sendUpdateReservationCcAgentId(
        updateReservationCcAgentIdRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendUpdateReservationCcAgentId_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationCcAgentIdEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationCcAgentId(
            new UpdateReservationCcAgentIdRequestDto()));
  }

  @Test
  void testGetDepositsByResId_success() {
    // Arrange
    var depositsOhip = new DepositsResponseDto();
    depositsOhip.addDepositsItem(new DepositsDto().paymentReference("3CPReference"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DepositsResponseDto.class)).thenReturn(Mono.just(depositsOhip));
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    // Act
    var response = ohipAdapterClient.getDepositsByReservationId("DAHHME", "12345678");

    // Assert
    assertThat(response.getDeposits().get(0).getPaymentReference(), is("3CPReference"));
  }

  @Test
  void sendUpdateRateCodeRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationRateCodeEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateRateCodeRequest(new RatePlanChangeRequestDto()));
  }

  @Test
  void updateRoomTypeRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    when(customResponseSpec.bodyToMono(RoomTypeChangeRequestDto.class))
        .thenReturn(Mono.error(ex));
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateRoomTypeEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateRoomTypeRequest(new RoomTypeChangeRequestDto()));
  }

  @Test
  void testGetCancellationPolicies_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancellationPoliciesResponseDto.class)).thenReturn(
        mockGetCancellationPoliciesResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    Set<String> reservationIds = Collections.singleton("147");
    var response = ohipAdapterClient.getCancellationPolicies(
        reservationIds, "HOTEL_ID", "FLEX", null);

    // Assert
    assertThat(response, notNullValue());
  }

  @Test
  void testGetMarketingPreferences_success() {
    // Arrange
    String hotelId = "DAHHME";
    String resNo = "12345678";

    var customerDto = new CustomerDto();
    customerDto.setTitle("Mr");
    customerDto.setFirstName("Sarah");
    customerDto.setLastName("Smith");
    customerDto.setCountry("GB");
    customerDto.setLanguage("en");
    var marketingPreferencesResponseDto = new MarketingPreferencesResponseDto();
    marketingPreferencesResponseDto.setContactValue("mail@mail.com");
    marketingPreferencesResponseDto.setOptIn(true);
    marketingPreferencesResponseDto.setCustomer(customerDto);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MarketingPreferencesResponseDto.class)).thenReturn(
        Mono.just(marketingPreferencesResponseDto));
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Act
    var response = ohipAdapterClient.getMarketingPreferences(hotelId, resNo);

    // Assert
    assertEquals("mail@mail.com", response.getContactValue());
    assertTrue(response.getOptIn());
    assertNotNull(response.getCustomer());
    assertEquals("Sarah", response.getCustomer().getFirstName());
  }

  @Test
  void testGetBookingAllowances_success() {
    // Arrange
    String hotelId = "HOTELID";
    String reservationId = "1234567";
    List<String> basketAllowances = List.of("dinner");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(BookingAllowancesResponse.class)).thenReturn(
        Mono.just(createBookingAllowancesResponse()));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = ohipAdapterClient.getBookingAllowances(hotelId, reservationId, basketAllowances);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getBookingAllowances().size());
    assertEquals("dinner", response.getBookingAllowances().get(0).getAllowance());
    assertEquals(BigDecimal.TEN, response.getBookingAllowances().get(0).getBudget());
  }

  @Test
  void sendUpdateRateCodeRequest_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateReservationRateCodeEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    ohipAdapterClient.sendUpdateRateCodeRequest(new RatePlanChangeRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateRoomTypeRequest_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateRoomTypeEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomTypeChangeRequestDto.class)).thenReturn(
        Mono.just(new RoomTypeChangeRequestDto()));

    // Act
    ohipAdapterClient.sendUpdateRoomTypeRequest(new RoomTypeChangeRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetHotelInformationRequest_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class)).thenReturn(mockGetHotelInfoResponse());

    // Act
    var response = ohipAdapterClient.sendGetHotelInformationRequest("HOTELTEST");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetReservationsByExternalReferenceId_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationDetailsEnhancedDto.class)).thenReturn(
        mockSendGetReservationsByExternalResId());

    // Act
    var response = ohipAdapterClient.sendGetReservationsByExternalReferenceId("122345");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendUpdateReservationAmend_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    ohipAdapterClient.sendUpdateReservationAmend(new UpdateReservationsRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getAmendSummaryDetails_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AmendSummaryAmountResponse.class)).thenReturn(
        mockGetAmendSummaryResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    AmendSummaryAmountRequest amendSummaryAmountRequest = AmendSummaryAmountRequest.builder()
        .hotelId("HOTELTEST")
        .reservationIds(List.of("12234"))
        .build();
    var response = ohipAdapterClient.getAmendSummaryDetails(amendSummaryAmountRequest);

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendConfirmAmend_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getConfirmAmend())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
        mockSendConfirmAmend());

    // Act
    var response = ohipAdapterClient.sendConfirmAmend(new ConfirmAmendOnReservationsRequestDto());

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateReservations_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipAdapterClient.updateReservations(new UpdateReservationsRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetRatePlansRequest_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RatePlansResponseDto.class)).thenReturn(
        mockSendGetRatePlansResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = ohipAdapterClient.sendGetRatePlansRequest(List.of("12345"), "hotelId");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendCharityPackagesDetailsRequest_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DonationPackagesResponseDto.class)).thenReturn(
        mockSendCharityPackagesResponse());

    // Act
    var response = ohipAdapterClient.sendCharityPackagesDetailsRequest("HOTELTEST",
        List.of("12345"));

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void movePaymentDetails_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    Set<String> reservationIds = Collections.singleton("147890");
    ohipAdapterClient.movePaymentDetails("HOTEL_ID", reservationIds);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteRoutingInstructions_Success() {
    //Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    Set<String> reservationIds = Collections.singleton("147");
    ohipAdapterClient.deleteRoutingInstructions("HOTEL_ID", reservationIds);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteRoutingInstructions__exception() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    Set<String> reservationIds = Collections.singleton("12345");
    assertThrows(HotelReservationOhipException.class, () ->
        ohipAdapterClient.deleteRoutingInstructions("FRAMTI", reservationIds));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetHotelInformationRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<HotelInfoDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(HotelInfoDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetHotelInformationRequest("hotel"));
  }

  @Test
  void sendUpdateReservationPackagesRequest_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(
        requestBodyUriSpec.uri(ohipAdapterProperties.getReservationsPackagesEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationPackagesRequest(
            new ReservationPackagesRequestDto()));
  }

  @Test
  void testUpdateBookerEmail_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getUpdateEmailReservationEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    var updateBookerEmailRequestDto = createUpdateBookerEmailRequestDto();
    ohipAdapterClient.sendUpdateBookerEmailRequest(updateBookerEmailRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetGeneratedDepositFolios_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DepositFoliosResponseDto.class)).thenReturn(
        mockGetGeneratedDepositFoliosResponse());

    // Act
    var response = ohipAdapterClient.getGeneratedDepositFolios("NEWDRO",
        Collections.singleton("11111"));

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetGeneratedDepositFolios_InternalServer_RulesException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<DepositFoliosResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(DepositFoliosResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getGeneratedDepositFolios("NEWDRO",
            Collections.singleton("11111")));
  }

  @Test
  void createMemo_Success() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getMemosEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MemosResponseDto.class)).thenReturn(
        Mono.just(new MemosResponseDto()));

    //Act
    ohipAdapterClient.createMemo(new CreateMemoRequestDto());

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getMemos_Success() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MemosResponseDto.class)).thenReturn(
        Mono.just(new MemosResponseDto()));

    //Act
    ohipAdapterClient.getMemos("hotelId", Set.of("reservationId1, reservationId2"));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getPackages_Success() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PackagesResponseDto.class)).thenReturn(
        Mono.just(new PackagesResponseDto()));

    //Act
    var packagesResponse = ohipAdapterClient.getPackages(
        new PackagesRequestDto("hotelId", "2023-10-05", "2023-10-06", 1, 1, 1));

    //Assert
    assertNotNull(packagesResponse);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getPackages_Throws40xException() {
    //Arrange
    PackagesException ex = mock(PackagesException.class);
    Mono<PackagesResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(PackagesResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    assertThrows(PackagesException.class, () -> ohipAdapterClient.getPackages(
        new PackagesRequestDto("hotelId", "2023-10-05", "2023-10-06", 1, 1, 1)));
  }

  @Test
  void getPackages_Throws50xException() {
    //Arrange
    PackagesException ex = mock(PackagesException.class);
    Mono<PackagesResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(PackagesResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    assertThrows(PackagesException.class, () -> ohipAdapterClient.getPackages(
        new PackagesRequestDto("hotelId", "2023-10-05", "2023-10-06", 1, 1, 1)));
  }

  @Test
  void createMemo_Exception() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<MemosResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(MemosResponseDto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    CreateMemoRequestDto createMemoRequestDto = new CreateMemoRequestDto();
    assertThrows(HotelReservationOhipException.class, () ->
        ohipAdapterClient.createMemo(createMemoRequestDto));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelAvailabilityByIdsV2_Exception() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<AvailabilityByIdsResponseV2Dto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(AvailabilityByIdsResponseV2Dto.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getAvailabilityByIdsEndpointV2())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    HotelAvailabilityByIdsRequestOhipV2Dto ohipRequest = new HotelAvailabilityByIdsRequestOhipV2Dto();
    assertThrows(HotelReservationOhipException.class, () ->
        ohipAdapterClient.getHotelAvailabilityByIdsV2(ohipRequest));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelAvailabilityByIdsV2_Success() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getAvailabilityByIdsEndpointV2())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AvailabilityByIdsResponseV2Dto.class)).thenReturn(
        Mono.just(new AvailabilityByIdsResponseV2Dto()));

    //Act
    ohipAdapterClient.getHotelAvailabilityByIdsV2(new HotelAvailabilityByIdsRequestOhipV2Dto());

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetRatePlans_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RatePlansResponseDto.class)).thenReturn(
        mockSendGetRatePlansResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = ohipAdapterClient.getRatePlans(List.of("12345"), "hotelId");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelAvailabilityByIds__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityByIdsDto.class)).thenReturn(mockAvailabilityByIdsResponse());

    // Act
    HotelAvailabilityByIdsDto response = ohipAdapterClient.getHotelAvailabilityByIds(
        getHotelAvailabilityByIdsRequestOhipDto());

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HOTEL_ID, response.getHotelAvailability().get(0).getHotelId());
    Assertions.assertEquals(ARRIVAL_DATE, response.getHotelAvailability().get(0).getStartDate());
    Assertions.assertEquals(OPERA_END_DATE, response.getHotelAvailability().get(0).getEndDate());
    Assertions.assertTrue(response.getHotelAvailability().get(0).isAvailable());
  }

  @Test
  void getHotelAvailabilityByIds_shouldReturnException(){
    String errorMessage = String.format("Error while trying to create HotelAvailabilityByIds for hotelAvailabilityByIdsRequestOhipDto=%s",
        getHotelAvailabilityByIdsRequestOhipDto());
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelAvailabilityByIdsDto.class))
        .thenReturn(Mono.error(new HotelAvailabilityException("message",
            errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(HotelAvailabilityException.class,
        () -> ohipAdapterClient.getHotelAvailabilityByIds(getHotelAvailabilityByIdsRequestOhipDto()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(errorMessage, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testCreateDepositFolios_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getGeneratedDepositFoliosEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    ohipAdapterClient.saveCharges(new DepositFoliosRequestDto());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetChangeLogShouldReturnOk() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChangeLogResponseDto.class)).thenReturn(Mono.just(new ChangeLogResponseDto()));

    //Act
    ohipAdapterClient.getChangeLog(hotelId, reservationId, 10, 1);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetChangeLogShouldReturnException_if500() {
    String hotelId = "HOTELTEST";
    String reservationId = "1234";
    ChangeLogException ex = mock(ChangeLogException.class);
    Mono<ChangeLogResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ChangeLogResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    //Act
   assertThrows(ChangeLogException.class, () ->
        ohipAdapterClient.getChangeLog(hotelId, reservationId, 10, 5));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetChangeLogShouldReturnException_if400() {
    String hotelId = "HOTELTEST";
    String reservationId = "1234";
    ChangeLogException ex = mock(ChangeLogException.class);
    Mono<ChangeLogResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ChangeLogResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    assertThrows(ChangeLogException.class, () ->
        ohipAdapterClient.getChangeLog(hotelId, reservationId, 10, 5));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetChangeLogShouldReturnException_if204() {
    String hotelId = "HOTELTEST";
    String reservationId = "1234";
    ChangeLogException ex = mock(ChangeLogException.class);
    Mono<ChangeLogResponseDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ChangeLogResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NO_CONTENT);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    //Act
    assertThrows(ChangeLogException.class, () ->
        ohipAdapterClient.getChangeLog(hotelId, reservationId, 10, 5));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendConfirmAmendSingleCall_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getConfirmAmendForSingleCall())).thenReturn(
            requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationByBasketRefResponseDto.class)).thenReturn(
            mockSendConfirmAmend());

    // Act
    var response = ohipAdapterClient.sendConfirmAmendForSingleCall(new AmendDistributionSingleCallRequestDto());

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testSendSaveReservationPreCheckInStatus_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getPreCheckInStatus())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PreCheckInResponse.class)).thenReturn(
        Mono.just(getPreCheckInResponse()));

    // Act
    PreCheckInResponse response = ohipAdapterClient.sendSaveReservationPreCheckInStatus(
        mockPreCheckInRequestDto());

    // Assert
    assertNotNull(response);
    assertThat(response.getStatus(), is("Success"));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testSendSaveReservationPreCheckInStatus_ThrowsException() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getPreCheckInStatus())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    assertThrows(NullPointerException.class,
        () -> ohipAdapterClient.sendSaveReservationPreCheckInStatus(mockPreCheckInRequestDto()));
  }

  @Test
  void sendDeleteRegCardAttachment_Success() {
    String hotelId = "FRAMTI";
    String reservationId = "12345";

    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Call the method
    ohipAdapterClient.sendDeleteRegCardAttachment(hotelId, reservationId);

    // Verify the interactions
    verify(webClient).delete();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(), any());
    verify(responseSpec).toBodilessEntity();
  }

  @Test
  void sendDeleteRegCardAttachment_Exception() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(ex));

    // Act & Assert
    assertThrows(HotelReservationOhipException.class, () ->
        ohipAdapterClient.sendDeleteRegCardAttachment("FRAMTI", "12345"));

    // Verify
    verify(webClient).delete();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).toBodilessEntity();
    verifyNoMoreInteractions(webClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);
  }

  @Test
  void sendDeleteReservationPreCheckInStatus_Success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipAdapterClient.sendDeleteReservationPreCheckInStatus("FRAMTI", "12345");

    // Assert
    verify(webClient).delete();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).toBodilessEntity();
    verifyNoMoreInteractions(webClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);
  }

  @Test
  void sendDeleteReservationPreCheckInStatus_Exception() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(ex));

    // Act & Assert
    assertThrows(HotelReservationOhipException.class, () ->
        ohipAdapterClient.sendDeleteReservationPreCheckInStatus("FRAMTI", "12345"));

    // Verify
    verify(webClient).delete();
    verify(requestHeadersUriSpec).uri(any(Function.class));
    verify(requestHeadersSpec).retrieve();
    verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).toBodilessEntity();
    verifyNoMoreInteractions(webClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);
  }

  @Test
  void testUpdateExternalReference_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    ohipAdapterClient.updateReservationExternalReference("HOTELID", List.of("1234"), "12345678");

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateExternalReference_Throws4xxException() {
    // Arrange
    HotelReservationNotFoundException ex = mock(HotelReservationNotFoundException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.updateReservationExternalReference("HOTELID", List.of("1234"), "12345678"));

  }

  @Test
  void testUpdateExternalReference_Throws5xxException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.updateReservationExternalReference("HOTELID", List.of("1234"), "12345678"));

  }

  @Test
  void sendLinkReservationToLeisureCustomer_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getLinkReservationToLeisureCustomerEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(
        Mono.just(ResponseEntity.ok().build()));

    // Act
    var linkReservationToLeisureCustomerRequestDto = new LinkReservationToLeisureCustomerRequestDto();
    linkReservationToLeisureCustomerRequestDto.setHotelId("HOTELID1");
    linkReservationToLeisureCustomerRequestDto.setReservationIds(Set.of("123456"));
    linkReservationToLeisureCustomerRequestDto.setCustomerAccountId("CUST-123456");
    ohipAdapterClient.sendLinkReservationToLeisureCustomer(
        linkReservationToLeisureCustomerRequestDto);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendLinkReservationToLeisureCustomer_ThrowsException() {
    // Arrange
    HotelReservationOhipException ex = mock(HotelReservationOhipException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        ohipAdapterProperties.getLinkReservationToLeisureCustomerEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendLinkReservationToLeisureCustomer(
            new LinkReservationToLeisureCustomerRequestDto()));
  }

  @Test
  void testUpdateReservationPreferences_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationsPreferencesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    ReservationPreferencesRequestDto reservationPreferencesRequestDto =
        createReservationPreferencesRequestDto();

    //Act
    ohipAdapterClient.updateReservationPreferences(reservationPreferencesRequestDto);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateReservationPreferences_Throws4xxException() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationsPreferencesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    HotelReservationNotFoundException ex = mock(HotelReservationNotFoundException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    ReservationPreferencesRequestDto reservationPreferencesRequestDto =
        createReservationPreferencesRequestDto();

    //Act and Assert
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.updateReservationPreferences(reservationPreferencesRequestDto));
  }

  @Test
  void testUpdateReservationUdf_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationsPreferencesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
    var updateUdf = new UpdateReservationAlertsRequest();

    //Act
    ohipAdapterClient.updateReservationAlerts(updateUdf);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateReservationUdf_Throws4xxException() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationsPreferencesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    HotelReservationNotFoundException ex = mock(HotelReservationNotFoundException.class);
    Mono<ResponseEntity<Void>> rsp = Mono.error(ex);
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    var updateUdf = new UpdateReservationAlertsRequest();

    //Act and Assert
    assertThrows(HotelReservationNotFoundException.class,
        () -> ohipAdapterClient.updateReservationAlerts(updateUdf));
  }

  @Test
  void testUpdateCancellationPolicies_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateCancellationPoliciesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    UpdateCancellationPoliciesRequestDto requestDto = new UpdateCancellationPoliciesRequestDto();

    //Act
    ohipAdapterClient.updateCancellationPolicies(requestDto);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateCancellationPolicies_ThrowsException() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateCancellationPoliciesEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    Mono<ResponseEntity<Void>> rsp = Mono.error(new RuntimeException());
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    UpdateCancellationPoliciesRequestDto requestDto = new UpdateCancellationPoliciesRequestDto();

    //Act and Assert
    assertThrows(RuntimeException.class,
        () -> ohipAdapterClient.updateCancellationPolicies(requestDto));
  }

  @Test
  void testUpdateUdfc20_Success() {
    //Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateUdfc20Endpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    UpdateReservationUdfsRequest updateReservationUdfsRequest = new UpdateReservationUdfsRequest();

    //Act
    ohipAdapterClient.updateUdfc20(updateReservationUdfsRequest);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testUpdateUdfc20_ThrowsException() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getUpdateUdfc20Endpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    Mono<ResponseEntity<Void>> rsp = Mono.error(new RuntimeException());
    when(customResponseSpec.toBodilessEntity()).thenReturn(rsp);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    UpdateReservationUdfsRequest updateReservationUdfsRequest = new UpdateReservationUdfsRequest();

    //Act and Assert
    assertThrows(RuntimeException.class, () -> ohipAdapterClient.updateUdfc20(updateReservationUdfsRequest));
  }

  private ReservationPreferencesRequestDto createReservationPreferencesRequestDto() {
    ReservationPreferencesRequestDto reservationPreferencesRequestDto = new ReservationPreferencesRequestDto();
    reservationPreferencesRequestDto.setHotelId("HOTELID");
    reservationPreferencesRequestDto.setReservationsIds(List.of("123456"));
    reservationPreferencesRequestDto.setPreferencesCollections(List.of());
    return reservationPreferencesRequestDto;
  }

  private PreCheckInRequestDto mockPreCheckInRequestDto() {
    PreCheckInRequestDto preCheckInRequestDto = new PreCheckInRequestDto();
    preCheckInRequestDto.setArrivalTime(LocalDate.of(2024, 7, 13));
    preCheckInRequestDto.setHotelId("STUAIR");
    preCheckInRequestDto.setReservationId("123456");
    return preCheckInRequestDto;
  }

  private PreCheckInResponse getPreCheckInResponse() {
    PreCheckInResponse expectedResponse = new PreCheckInResponse();
    expectedResponse.setStatus("Success");
    return expectedResponse;
  }

  private UpdateBookerEmailRequestDto createUpdateBookerEmailRequestDto() {
    var updateBookerEmailRequestDto = new UpdateBookerEmailRequestDto();

    updateBookerEmailRequestDto.setEmailAddress("secondEmail@domain.uk");
    return updateBookerEmailRequestDto;
  }

  private BookingAllowancesResponse createBookingAllowancesResponse() {
    return BookingAllowancesResponse.builder()
        .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .build()))
        .build();
  }

  private UpdateReasonForStayRequestDto createReasonForStayRequestDto() {
    var updateReasonForStayRequestDto = new UpdateReasonForStayRequestDto();
    updateReasonForStayRequestDto.setHotelId("LONEUS");
    updateReasonForStayRequestDto.setReservationIds(Collections.singletonList("123456"));

    return updateReasonForStayRequestDto;
  }

  private CompanyQuestionAndAnswerDetailsRequestDto createCompanyQuestionAndAnswerDetailsRequestDto() {
    var companyQuestionAndAnswerDetailsRequestDto = new CompanyQuestionAndAnswerDetailsRequestDto();
    companyQuestionAndAnswerDetailsRequestDto.setHotelId("HOTELID1");
    companyQuestionAndAnswerDetailsRequestDto.setReservationIds(Set.of("123456"));
    return companyQuestionAndAnswerDetailsRequestDto;
  }

  private UpdateDiscountRequestDto createUpdateDiscountRequestDto() {

    UpdateDiscountRequestDto updateDiscountRequestDto = new UpdateDiscountRequestDto();
    updateDiscountRequestDto.setHotelId("HOTELID1");
    updateDiscountRequestDto.setReservationIds(Set.of("123456"));
    updateDiscountRequestDto.setDiscountAmount(BigDecimal.TEN);
    return updateDiscountRequestDto;
  }

  private static BusinessItemsRequestDto createBusinessItemsRequestDto() {
    BusinessItemsRequestDto businessItemsRequestDto = new BusinessItemsRequestDto();
    businessItemsRequestDto.setHotelId("HOTELID1");
    businessItemsRequestDto.setReservationIds(Set.of("123456"));

    BusinessItemsDto businessItemsDto = new BusinessItemsDto();
    businessItemsDto.setPurchaseOrderNumber("10101010");
    businessItemsDto.setCustomReferenceNumber("101010010");

    BusinessAllowanceDto businessAllowanceDto = new BusinessAllowanceDto();
    businessAllowanceDto.setAllowance("alowance");
    businessAllowanceDto.setBudget(BigDecimal.ZERO);
    businessAllowanceDto.setIsAuthorised(Boolean.TRUE);

    businessItemsDto.addBusinessAllowancesItem(businessAllowanceDto);
    businessItemsRequestDto.setBusinessItems(businessItemsDto);
    return businessItemsRequestDto;
  }

  private SearchBookingsRequestDto createSearchBookingsRequestDto() {

    SearchBookingsRequestDto searchBookingsRequestDto = new SearchBookingsRequestDto();
    searchBookingsRequestDto.setBookingReference("TestBookingRef");
    searchBookingsRequestDto.setBookerLastName("TestBookerLastName");
    searchBookingsRequestDto.setArrivalDate("2023-01-19");
    return searchBookingsRequestDto;
  }

  private UpdateReservationOverrideReasonsRequestDto createUpdateReservationOverrideReasonsRequestDto() {
    var updateReservationOverrideReasonsRequestDto = new UpdateReservationOverrideReasonsRequestDto();
    updateReservationOverrideReasonsRequestDto.setHotelId("HOTELID1");
    updateReservationOverrideReasonsRequestDto.setReservationIds(Set.of("123456"));
    updateReservationOverrideReasonsRequestDto.setReasonName("Illness");
    updateReservationOverrideReasonsRequestDto.setCallerName("John Doe");
    updateReservationOverrideReasonsRequestDto.setManagerName("James Bond");
    return updateReservationOverrideReasonsRequestDto;
  }

  private UpdateReservationCcAgentIdRequestDto createUpdateReservationCcAgentIdRequestDto() {
    var updateReservationCcAgentIdRequestDto = new UpdateReservationCcAgentIdRequestDto();
    updateReservationCcAgentIdRequestDto.setHotelId("HOTELID1");
    updateReservationCcAgentIdRequestDto.setReservationIds(Set.of("123456"));
    updateReservationCcAgentIdRequestDto.ccAgentId("Jane.Doe@wb.com");
    return updateReservationCcAgentIdRequestDto;
  }

  private Mono<CancelInformationResponseDto> mockCancelInformationResponse() {
    return Mono.just(new CancelInformationResponseDto());
  }

  private Mono<ConfirmReservationResponseDto> mockConfirmReservationResponse() {
    var response = new ConfirmReservationResponseDto();
    response.setReservationIdList(
        List.of(mockIdType("34865", "Reservation"), mockIdType("264873", "Confirmation")));
    response.setReservationStatus("Reserved");
    ConfirmationCustomerDto customer = new ConfirmationCustomerDto();
    customer.setGivenName("Tester");
    customer.setSurName("Testerson");
    response.setReservationGuest(customer);
    ConfirmationRoomStayDto roomStay = new ConfirmationRoomStayDto();
    roomStay.arrivalDate(LocalDate.now());
    roomStay.departureDate(LocalDate.now());
    response.setRoomStay(roomStay);
    return Mono.just(response);
  }

  private UniqueIdTypeDto mockIdType(String id, String type) {
    UniqueIdTypeDto idType = new UniqueIdTypeDto();
    idType.setType(type);
    idType.setId(id);
    return idType;
  }

  private Mono<ReservationResponseDto> mockCreateReservationResponse() {
    var response = new ReservationResponseDto();
    var reservation = new ReservationCreationResponseDto();
    reservation.setReservationId("123");
    reservation.setCreateDateTime("2022");
    response.setReservations(asList(reservation));

    return Mono.just(response);
  }

  private Mono<ReservationsDetailsResponseDto> mockReservationsDetailsResponse() {
    return Mono.just(new ReservationsDetailsResponseDto());
  }

  private Mono<ReservationIdDetailsDto> mockSendGetReservationsByResId() {
    return Mono.just(new ReservationIdDetailsDto());
  }

  private Mono<ReservationGuestResponseDto> mockCreateReservationGuestResponse() {
    var response = new ReservationGuestResponseDto();
    response.setHotelId("LONEUS");
    response.setReservationIds(List.of("1234"));

    return Mono.just(response);
  }

  private Mono<CancelReservationResponseDto> mockCancelReservationResponse() {
    var response = new CancelReservationResponseDto();
    response.setCancellationIds(List.of("1234"));

    return Mono.just(response);
  }

  private Mono<SearchBookingsResponseDto> mockSearchBookingsResponse() {
    return Mono.just(new SearchBookingsResponseDto());
  }

  private Mono<UpdateReasonForStayResponseDto> mockUpdateReasonForStayResponse() {
    return Mono.just(new UpdateReasonForStayResponseDto());
  }

  private Mono<CancellationPoliciesResponseDto> mockGetCancellationPoliciesResponse() {
    return Mono.just(new CancellationPoliciesResponseDto());
  }

  private Mono<HotelInfoDto> mockGetHotelInfoResponse() {
    return Mono.just(new HotelInfoDto());
  }

  private Mono<ReservationDetailsEnhancedDto> mockSendGetReservationsByExternalResId() {
    return Mono.just(new ReservationDetailsEnhancedDto());
  }

  private Mono<AmendSummaryAmountResponse> mockGetAmendSummaryResponse() {
    return Mono.just(new AmendSummaryAmountResponse());
  }

  private Mono<ReservationByBasketRefResponseDto> mockSendConfirmAmend() {
    return Mono.just(new ReservationByBasketRefResponseDto());
  }

  private Mono<RatePlansResponseDto> mockSendGetRatePlansResponse() {
    return Mono.just(new RatePlansResponseDto());
  }

  private Mono<DonationPackagesResponseDto> mockSendCharityPackagesResponse() {
    return Mono.just(new DonationPackagesResponseDto());
  }

  private Mono<ReservationByBasketRefResponseDto> mockSendReservationByBasketReservationResponse() {
    return Mono.just(new ReservationByBasketRefResponseDto());
  }

  private Mono<DepositFoliosResponseDto> mockGetGeneratedDepositFoliosResponse() {
    return Mono.just(new DepositFoliosResponseDto());
  }

  private HotelAvailabilityByIdsRequestOhipDto getHotelAvailabilityByIdsRequestOhipDto() {
    return HotelAvailabilityByIdsRequestOhipDto.builder()
        .hotelIds(List.of(HOTEL_ID))
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .adultsNumber(ADULTS)
        .roomTypes(ROOM_TYPES)
        .childrenNumber(of())
        .cotsRequired(of())
        .build();
  }

  private Mono<HotelAvailabilityByIdsDto> mockAvailabilityByIdsResponse() {
    var response = new HotelAvailabilityByIdsDto();
    response.setHotelAvailability(List.of(mockAvailabilityResponse().block()));
    return Mono.just(response);
  }

  private Mono<HotelAvailabilityDto> mockAvailabilityResponse() {
    var roomType = new RoomTypeDto();
    roomType.setRoomType("DB");

    var roomRate = new RoomRateDto();
    roomRate.setRatePlanCode("FLEXRATE");
    roomRate.setRoomTypes(of(roomType));

    var response = new HotelAvailabilityDto();
    response.setHotelId("TKINPT");
    response.setStartDate("2022-03-01");
    response.setEndDate("2022-03-02");
    response.setAvailable(true);
    response.setRoomRates(of(roomRate));

    return Mono.just(response);
  }
}
