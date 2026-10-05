package uk.co.whitbread.ohip.infrastructure.rest.controller.lov;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.mapper.CancellationReasonsDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.model.out.CancellationReasonDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.model.out.CancellationReasonsResponseDto;

@ExtendWith(MockitoExtension.class)
class ListOfValuesControllerTest {

  @InjectMocks
  ListOfValuesController listOfValuesControllerTest;

  @Mock
  private ListOfValuesInPort listOfValuesInPort;
  @Mock
  private CancellationReasonsDtoMapper cancellationReasonsDtoMapper;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    //Arrange
    Mockito.when(listOfValuesInPort.getListOfCancellationReasons(anyString()))
        .thenReturn(mockCancellationReasonsResponse());
    Mockito.when(cancellationReasonsDtoMapper.toDto(any(CancellationReasonsResponse.class)))
        .thenReturn(mockCancellationReasonsResponseDto());

    //Act
    var response = listOfValuesControllerTest.getCancellationReasons("MANOLD");

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
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

  private CancellationReasonsResponseDto mockCancellationReasonsResponseDto() {
    var cancellationReason = CancellationReasonDto.builder()
        .code("ILL")
        .name("Illness")
        .description("Illness")
        .active(true)
        .build();
    return CancellationReasonsResponseDto.builder().cancellationReasons(List.of(cancellationReason))
        .build();
  }

}
