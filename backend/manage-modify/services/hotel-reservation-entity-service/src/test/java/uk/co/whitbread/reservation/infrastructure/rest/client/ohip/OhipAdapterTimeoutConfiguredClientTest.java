package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterTimeoutConfiguredClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
public class OhipAdapterTimeoutConfiguredClientTest {

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private OhipAdapterProperties ohipAdapterProperties;

  @InjectMocks
  private OhipAdapterTimeoutConfiguredClient client;

  @Test
  public void testSendAddFileAttachmentToReservationRequest_Success() {
    //Arrange
    ReservationFileAttachmentRequestDto requestDto = mockReservationFileAttachmentRequestDto();
    PreCheckInResponse expectedResponse = new PreCheckInResponse();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getReservationGuestEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class),
        eq(ReservationFileAttachmentRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PreCheckInResponse.class)).thenReturn(Mono.just(expectedResponse));

    PreCheckInResponse actualResponse = client.sendAddFileAttachmentToReservationRequest(
        requestDto);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testSendAddFileAttachmentToReservationRequest_HotelReservationOhipException() {
    //Arrange
    ReservationFileAttachmentRequestDto requestDto = mockReservationFileAttachmentRequestDto();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getFileAttachmentEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class),
        eq(ReservationFileAttachmentRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PreCheckInResponse.class)).thenReturn(
        Mono.error(mock(HotelReservationOhipException.class)));

    assertThrows(HotelReservationOhipException.class, () -> {
      client.sendAddFileAttachmentToReservationRequest(requestDto);
    });
  }

  @Test
  public void testSendAddFileAttachmentToReservationRequest_ResponseTimeout() {
    // Arrange
    ReservationFileAttachmentRequestDto requestDto = mockReservationFileAttachmentRequestDto();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getFileAttachmentEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class),
        eq(ReservationFileAttachmentRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Simulate a timeout
    when(responseSpec.bodyToMono(PreCheckInResponse.class))
        .thenReturn(Mono.error(new TimeoutException("Simulated timeout")));

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () -> {
      client.sendAddFileAttachmentToReservationRequest(requestDto);
    });

    assertInstanceOf(TimeoutException.class, exception.getCause());
    assertTrue(exception.getCause().getMessage().contains("Simulated timeout"));
  }

  @Test
  public void testSendAddFileAttachmentToReservationRequest_ResponseTimeout2() {
    // Arrange
    ReservationFileAttachmentRequestDto requestDto = mockReservationFileAttachmentRequestDto();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterProperties.getFileAttachmentEndpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class),
        eq(ReservationFileAttachmentRequestDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Simulate a delay longer than the timeout
    when(responseSpec.bodyToMono(PreCheckInResponse.class))
        .thenReturn(Mono.just(new PreCheckInResponse()).delayElement(Duration.ofSeconds(7)));

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class,
        () -> client.sendAddFileAttachmentToReservationRequest(requestDto));

    assertTrue(exception.getCause() instanceof TimeoutException);
  }

  private ReservationFileAttachmentRequestDto mockReservationFileAttachmentRequestDto() {
    ReservationFileAttachmentRequestDto attachmentRequestDto = new ReservationFileAttachmentRequestDto();
    attachmentRequestDto.setFileAttachment("Base64 string");
    attachmentRequestDto.setDescription("Test attachment");
    attachmentRequestDto.setFileName("REG_RES1234567_ID232323_P76767676.pdf");
    attachmentRequestDto.setGlobal(false);
    attachmentRequestDto.setReservationId("1613333");
    attachmentRequestDto.setOverwriteExistingFile(true);
    attachmentRequestDto.setHotelId("STUAIR");
    return attachmentRequestDto;
  }

}
