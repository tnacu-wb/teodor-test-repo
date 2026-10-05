package uk.co.whitbread.booking.infrastructure.rest.client.ohip.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.exceptions.HotelReservationOhipException;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.in.PackageGroupRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.HotelInfoDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageCodesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupOhipResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationInfoPaymentTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipAdapterClientTest {

  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private WebClient webClient;

  @Mock
  private OhipAdapterProperties properties;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void fetchMultipleHotelsInfo_Success() {
    // Arrange
    Set<String> hotelIds = Set.of("HOTEL1");
    HotelInfoDto hotelInfo = new HotelInfoDto("utc","gb","HOTEL1");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class)).thenReturn(Mono.just(hotelInfo));

    // Act
    var result = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void fetchMultipleHotelsInfo_MultipleHotels_Success() {
    // Arrange
    Set<String> hotelIds = Set.of("HOTEL1", "HOTEL2");
    HotelInfoDto hotelInfo1 = new HotelInfoDto("utc","gb","HOTEL1");
    HotelInfoDto hotelInfo2 = new HotelInfoDto("utc","gb","HOTEL2");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.just(hotelInfo1))
        .thenReturn(Mono.just(hotelInfo2));

    // Act
    var result = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void fetchMultipleHotelsInfo_EmptySet() {
    // Arrange
    Set<String> hotelIds = Set.of();

    // Act
    var result = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(webClient, never()).get();
  }

  @Test
  void fetchMultipleHotelsInfo_WithError() {
    // Arrange
    Set<String> hotelIds = Set.of("HOTEL1", "HOTEL2");
    HotelInfoDto hotelInfo2 = new HotelInfoDto("utc","gb","HOTEL2");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelReservationOhipException("Error message", "Debug message", new Exception(), 500)))
        .thenReturn(Mono.just(hotelInfo2));

    // Act
    var result = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void fetchMultipleHotelsInfo_AllErrors() {
    // Arrange
    Set<String> hotelIds = Set.of("HOTEL1", "HOTEL2");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInfoDto.class))
        .thenReturn(Mono.error(new HotelReservationOhipException("Error message", "Debug message", new Exception(), 500)))
        .thenReturn(Mono.error(new HotelReservationOhipException("Error message", "Debug message", new Exception(), 500)));

    // Act
    var result = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }


  @Test
  void getLightweightReservationsByIds_Success() {
    // Arrange
    String hotelId = "HOTEL1";
    Set<String> reservationIds = Set.of("RES1", "RES2");
    ReservationLightweightResponseDto responseDto = new ReservationLightweightResponseDto();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class)).thenReturn(Mono.just(responseDto));

    // Act
    var result = ohipAdapterClient.getLightweightReservationsByIds(hotelId, reservationIds);

    // Assert
    assertNotNull(result);
    assertEquals(responseDto, result);
  }

  @Test
  void getLightweightReservationsByIds_Error() {
    // Arrange
    String hotelId = "HOTEL1";
    Set<String> reservationIds = Set.of("RES1", "RES2");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class))
        .thenReturn(Mono.error(new RuntimeException("Error occurred")));

    // Act
    var result = ohipAdapterClient.getLightweightReservationsByIds(hotelId, reservationIds);

    // Assert
    assertNull(result);
  }

  @Test
  void getLightweightReservationsByIds_EmptyResponse() {
    // Arrange
    String hotelId = "HOTEL1";
    Set<String> reservationIds = Set.of("RES1", "RES2");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class)).thenReturn(Mono.empty());

    // Act
    var result = ohipAdapterClient.getLightweightReservationsByIds(hotelId, reservationIds);

    // Assert
    assertNull(result);
  }

  @Test
  void getLightweightReservationsByIds_UriBuilder() {
    // Arrange
    String hotelId = "HOTEL1";
    Set<String> reservationIds = Set.of("RES1", "RES2");
    String expectedPath = "/lightweight-reservations";

    when(properties.getLightweightReservationsByIdsEndpoint()).thenReturn(expectedPath);

    ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(uriCaptor.capture())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationLightweightResponseDto.class)).thenReturn(Mono.empty());

    // Act
    ohipAdapterClient.getLightweightReservationsByIds(hotelId, reservationIds);

    // Assert
    Function<UriBuilder, URI> capturedUriFunction = uriCaptor.getValue();
    DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
    UriBuilder uriBuilder = uriBuilderFactory.builder().scheme("http").host("example.com");
    URI constructedUri = capturedUriFunction.apply(uriBuilder);

    assertTrue(constructedUri.toString().startsWith("http://example.com/lightweight-reservations?hotelId=HOTEL1"));
    assertTrue(constructedUri.toString().contains("reservationIds=RES1,RES2") ||
          constructedUri.toString().contains("reservationIds=RES2,RES1"));
  }

  @Test
  void getReservationsPaymentTypeByReservationIds_UriBuilder() {
    // Arrange
    String hotelId = "HOTEL1";
    Set<String> reservationIds = Set.of("RES1", "RES2");
    String expectedPath = "/reservations/paymentType";
    ReservationInfoPaymentTypeDto dto = mockReservationInfoPaymentTypeDto();
    when(properties.getReservationsPaymentTypeByReservationIds()).thenReturn(expectedPath);

    ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(uriCaptor.capture())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    doReturn(Mono.just(List.of(dto)))
      .when(responseSpec)
      .bodyToMono(any(ParameterizedTypeReference.class));

    // Act
    ohipAdapterClient.getReservationsPaymentTypeByReservationIds(hotelId, reservationIds);

    // Assert
    Function<UriBuilder, URI> capturedUriFunction = uriCaptor.getValue();
    DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
    UriBuilder uriBuilder = uriBuilderFactory.builder().scheme("http").host("example.com");
    URI constructedUri = capturedUriFunction.apply(uriBuilder);

    assertEquals(expectedPath, constructedUri.getPath());
    assertTrue(constructedUri.getQuery().contains("hotelId=HOTEL1"));
    assertTrue(constructedUri.getQuery().contains("&reservationIds=RES1"));
    assertTrue(constructedUri.getQuery().contains("&reservationIds=RES2"));
  }

  @Test
  void getReservationsPaymentTypeByReservationIds_Success() {
    // Arrange
    Set<String> ids = Set.of("ref");
    ReservationInfoPaymentTypeDto dto = mockReservationInfoPaymentTypeDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    doReturn(Mono.just(List.of(dto)))
      .when(responseSpec)
      .bodyToMono(any(ParameterizedTypeReference.class));

    // Act
    var result = ohipAdapterClient.getReservationsPaymentTypeByReservationIds("hotel", ids);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(dto, result.get(0));
  }

  @Test
  void getReservationsPaymentTypeByReservationIds_Exception() {
    // Arrange
    Set<String> ids = Set.of("ref");
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    doReturn(Mono.error(new HotelReservationOhipException("exp", "message", new Exception(), 100)))
      .when(responseSpecMock)
      .bodyToMono(any(ParameterizedTypeReference.class));

    // Act
    var exception = assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getReservationsPaymentTypeByReservationIds("hotel", ids));

    // Assert
    assertEquals("message", exception.getMessage());
  }

  @Test
  void getPackageGroups_Success() {
    // Arrange
    PackageGroupRequestDto packageGroupRequest = new PackageGroupRequestDto();
    packageGroupRequest.setHotelId("HEAPTI");
    packageGroupRequest.setPackageGroupList(Set.of("MDP"));

    PackageGroupsDto packageGroup = new PackageGroupsDto();
    packageGroup.setPackageGroup("MDP");
    packageGroup.setPackageGroupDescription("Meal Deal Package");
    packageGroup.setPackageCodes(List.of(
        PackageCodesDto.builder().packageCode("MDBEVA").packageDescription("Meal Deal Dinner")
            .build(),
        PackageCodesDto.builder().packageCode("MD2DIN")
            .packageDescription("Meal Deal Dinner Beverage").build(),
        PackageCodesDto.builder().packageCode("MDBFST")
            .packageDescription("Meal Deal Breakfast Food").build()
    ));

    PackageGroupOhipResponseDto responseDto = new PackageGroupOhipResponseDto();
    responseDto.setPackagesGroup(Collections.singletonList(packageGroup));

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getPackageGroupsEndPoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PackageGroupOhipResponseDto.class)).thenReturn(
        Mono.just(responseDto));

    // Act
    PackageGroupOhipResponseDto response = ohipAdapterClient.getPackageGroups(packageGroupRequest);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getPackagesGroup());
    assertThat(response.getPackagesGroup().get(0).getPackageGroup(), is("MDP"));
    assertThat(response.getPackagesGroup().get(0).getPackageGroupDescription(),
        is("Meal Deal Package"));
  }

  @Test
  void getPackageGroups_Exception() {
    // Arrange
    PackageGroupRequestDto packageGroupRequest = new PackageGroupRequestDto();
    packageGroupRequest.setHotelId("HEAPTI");
    packageGroupRequest.setPackageGroupList(Set.of("MDP"));

    HotelReservationOhipException thrownException =
        new HotelReservationOhipException("exp", "message", new Exception(), 100);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(properties.getPackageGroupsEndPoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(
        responseSpecMock);
    when(responseSpecMock.bodyToMono(PackageGroupOhipResponseDto.class))
        .thenReturn(Mono.error(thrownException));

    // Act & Assert
    HotelReservationOhipException exception = assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getPackageGroups(packageGroupRequest));

    assertEquals("message", exception.getMessage());
  }

  private ReservationInfoPaymentTypeDto mockReservationInfoPaymentTypeDto() {
    return mock(ReservationInfoPaymentTypeDto.class);
  }
}