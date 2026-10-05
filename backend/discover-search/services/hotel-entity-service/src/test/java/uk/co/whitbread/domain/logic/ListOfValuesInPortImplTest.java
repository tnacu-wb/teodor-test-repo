package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.domain.ports.secondary.ListOfValuesOutPort;

@ExtendWith(MockitoExtension.class)
class ListOfValuesInPortImplTest {

  @Mock
  private ListOfValuesOutPort listOfValuesOutPort;

  @InjectMocks
  private ListOfValuesInPortImpl listOfValuesInPort;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    when(listOfValuesOutPort.getListOfCancellationReasons(anyString())).thenReturn(
        mockCancellationReasonsResponse());

    var response = listOfValuesInPort.getListOfCancellationReasons("MANOLD");

    //Assert
    assertNotNull(response);
    assertNotNull(response.getCancellationReasons());
    assertEquals("ILL", response.getCancellationReasons().get(0).getCode());
    assertEquals("Illness", response.getCancellationReasons().get(0).getDescription());
    assertEquals("Illness", response.getCancellationReasons().get(0).getName());
    assertTrue(response.getCancellationReasons().get(0).isActive());
    assertTrue(response.getCancellationReasons().get(0).isManagerApprovalNeeded());
    verifyNoMoreInteractions(listOfValuesOutPort);
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
