package uk.co.whitbread.digitalkey.infrastructure.rest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.digitalkey.domain.model.roomallocation.in.AllocateRequest;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.KioskAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.properties.KioskClientProperties;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KioskAdapterClientTest {

  @Mock
  private WebClient kioskWebClient;

  @Mock
  private KioskClientProperties kioskClientProperties;

  @InjectMocks
  private KioskAdapterClient kioskAdapterClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAllocateRoom_success() {
    // Use deep stubs to allow chained calls
    WebClient kioskWebClientObj = Mockito.mock(WebClient.class, Answers.RETURNS_DEEP_STUBS);
    KioskClientProperties kioskClientPropertiesConfig = Mockito.mock(KioskClientProperties.class);

    KioskAdapterClient localKioskAdapterClient = new KioskAdapterClient(kioskWebClientObj, kioskClientPropertiesConfig);

    RoomAllocationRequestDto requestDto = new RoomAllocationRequestDto();
    AllocationResponseDto expectedResponse = new AllocationResponseDto();

    WebClient.RequestBodyUriSpec uriSpec = kioskWebClientObj.post();
    WebClient.RequestHeadersSpec<?> headersSpec = uriSpec
        .uri("/v1/kiosk/allocate")
        .body(any(Mono.class), eq(AllocateRequest.class));
    WebClient.ResponseSpec localResponseSpec = headersSpec.retrieve();
    when(localResponseSpec.onStatus(any(), any())).thenReturn(localResponseSpec);
    when(localResponseSpec.bodyToMono(AllocationResponseDto.class)).thenReturn(Mono.just(expectedResponse));

    AllocationResponseDto actualResponse = localKioskAdapterClient.allocateRoom(requestDto);

    assertNull(actualResponse);
  }

  @Test
  void testAllocateRoom_failure() {
    WebClient kioskWebClientObj = Mockito.mock(WebClient.class);
    KioskClientProperties kioskClientPropertiesLocal = Mockito.mock(KioskClientProperties.class);
    KioskAdapterClient localKioskAdapterClient = new KioskAdapterClient(kioskWebClientObj, kioskClientPropertiesLocal);

    RoomAllocationRequestDto requestDto = new RoomAllocationRequestDto();

    // Stubbing the WebClient chain
    when(kioskClientPropertiesLocal.getKioskAllocate()).thenReturn("/v1/kiosk/allocate");

    // Assertion
    assertThrows(RuntimeException.class, () -> localKioskAdapterClient.allocateRoom(requestDto));
  }
}
