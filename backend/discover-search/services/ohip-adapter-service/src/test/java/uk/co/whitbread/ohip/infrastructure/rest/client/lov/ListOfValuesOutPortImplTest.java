package uk.co.whitbread.ohip.infrastructure.rest.client.lov;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_CANCELLATION_REASONS_EXCEPTION;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.OhipListOfValuesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.exceptions.ListOfValuesException;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper.CancellationReasonsOhipMapper;

@ExtendWith(MockitoExtension.class)
class ListOfValuesOutPortImplTest {

  @Mock
  private OhipListOfValuesClient ohipListOfValuesClient;

  @Mock
  private CancellationReasonsOhipMapper cancellationReasonsOhipMapper;

  @InjectMocks
  private ListOfValuesOutPortImpl listOfValuesOutPort;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    var listOfValues = mock(ListOfValues.class);

    when(ohipListOfValuesClient.getListOfCancellationReasons(anyString())).thenReturn(listOfValues);
    when(cancellationReasonsOhipMapper.toCancellationReasonsResponseModel(
        any(ListOfValues.class))).thenReturn(mockCancellationReasonsResponse());

    var response = listOfValuesOutPort.getListOfCancellationReasons("MANOLD");

    assertNotNull(response);
    assertThat(response.getCancellationReasons(), hasSize(1));
  }

  @Test
  void getListOfCancellationReasons__shouldThrowException() {
    // Arrange
    String error = "An error was returned by OHIP: Could not fetch list of cancellation reasons.";
    Mockito.when(ohipListOfValuesClient.getListOfCancellationReasons(anyString()))
        .thenThrow(new ListOfValuesException(OHIP_GET_CANCELLATION_REASONS_EXCEPTION,
            error));
    // Act
    ListOfValuesException exception = Assertions
        .assertThrows(ListOfValuesException.class, () ->
          listOfValuesOutPort.getListOfCancellationReasons("HOTEL_ID")
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  private CancellationReasonsResponse mockCancellationReasonsResponse() {
    var cancellationReason = CancellationReason.builder()
        .code("ILL")
        .name("Illness")
        .description("Illness")
        .active(true)
        .managerApprovalNeeded(true)
        .build();
    return CancellationReasonsResponse.builder().cancellationReasons(List.of(cancellationReason))
        .build();
  }

}
