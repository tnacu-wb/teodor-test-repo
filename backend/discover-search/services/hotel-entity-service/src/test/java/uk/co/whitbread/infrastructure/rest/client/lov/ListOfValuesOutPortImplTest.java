package uk.co.whitbread.infrastructure.rest.client.lov;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonsResponseDto;
import uk.co.whitbread.infrastructure.rest.client.lov.mapper.CancellationReasonsMapper;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;

@ExtendWith(MockitoExtension.class)
class ListOfValuesOutPortImplTest {

  @Mock
  private OhipClient ohipClient;

  @Mock
  private CancellationReasonsMapper cancellationReasonsMapper;

  @InjectMocks
  private ListOfValuesOutPortImpl listOfValuesOutPort;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    var cancellationReasonsResponseDto = mock(CancellationReasonsResponseDto.class);

    when(ohipClient.getListOfCancellationReasons(anyString())).thenReturn(
        cancellationReasonsResponseDto);
    when(cancellationReasonsMapper.toDomainModel(
        any(CancellationReasonsResponseDto.class))).thenReturn(
        mockCancellationReasonsResponse());

    var response = listOfValuesOutPort.getListOfCancellationReasons("MANOLD");

    assertNotNull(response);
    assertThat(response.getCancellationReasons(), hasSize(1));
  }

  private CancellationReasonsResponse mockCancellationReasonsResponse() {
    var cancellationReason = CancellationReason.builder()
        .code("ILL")
        .name("Illness")
        .description("Illness")
        .active(true)
        .build();
    return CancellationReasonsResponse.builder().cancellationReasons(List.of(cancellationReason))
        .build();
  }

}
