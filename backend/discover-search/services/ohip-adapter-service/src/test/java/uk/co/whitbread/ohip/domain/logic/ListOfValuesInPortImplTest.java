package uk.co.whitbread.ohip.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.ListOfValuesOutPort;

@ExtendWith(MockitoExtension.class)
class ListOfValuesInPortImplTest {

  @Mock
  private ListOfValuesOutPort listOfValuesOutPort;

  @InjectMocks
  private ListOfValuesInPortImpl listOfValuesInPort;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    //Arrange
    when(listOfValuesOutPort.getListOfCancellationReasons(anyString()))
        .thenReturn(mockCancellationReasonsResponse());

    //Act
    var response = listOfValuesInPort.getListOfCancellationReasons(anyString());

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getCancellationReasons());
    assertEquals("ILL", response.getCancellationReasons().get(0).getCode());
    assertEquals("Illness", response.getCancellationReasons().get(0).getDescription());
    assertEquals("Illness", response.getCancellationReasons().get(0).getName());
    assertTrue(response.getCancellationReasons().get(0).isActive());
    verifyNoMoreInteractions(listOfValuesOutPort);
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
