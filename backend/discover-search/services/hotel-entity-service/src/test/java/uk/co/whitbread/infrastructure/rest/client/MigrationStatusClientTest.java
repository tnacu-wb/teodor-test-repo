package uk.co.whitbread.infrastructure.rest.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.exception.MigrationStatusException;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.in.MigrationStatusRequest;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class MigrationStatusClientTest {
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @InjectMocks
  private MigrationStatusClient migrationStatusClient;

  @Test
  void getMigrationStatusResponse__ShouldThrowBadRequestException() {
    //Arrange
    MigrationStatusRequest migrationStatusRequest = new MigrationStatusRequest(List.of("1","2"));
    String error = "Migration status returned \"Invalid Hotel Id\" error message!";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var exception = Assertions.assertThrows(MigrationStatusException.class,
            () -> migrationStatusClient.getMigrationStatusResponse(migrationStatusRequest));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }
}
