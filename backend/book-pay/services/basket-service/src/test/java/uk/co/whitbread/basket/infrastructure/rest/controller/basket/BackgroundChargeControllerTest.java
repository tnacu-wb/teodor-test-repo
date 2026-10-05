package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.basket.domain.ports.primary.BackgroundChargeInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BackgroundChargeRequestDto;

@ExtendWith(MockitoExtension.class)
class BackgroundChargeControllerTest {

  @InjectMocks
  private BackgroundChargeController backgroundChargeController;

  @Mock
  private BackgroundChargeInPort backgroundChargeInPort;

  @Test
  void backgroundCharge_returnsNoContentAndDelegatesToInPort() {
    var request = new BackgroundChargeRequestDto("BASKET-1", "token-1");

    var response = backgroundChargeController.backgroundCharge(request);

    verify(backgroundChargeInPort, times(1))
        .processBackgroundCharge("BASKET-1", "token-1");
    assertThat(response, notNullValue());
    assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
  }

  @Test
  void backgroundCharge_propagatesExceptionWhenInPortFails() {
    var request = new BackgroundChargeRequestDto("BASKET-1", "token-1");
    doThrow(new RuntimeException("processing failed")).when(backgroundChargeInPort)
        .processBackgroundCharge("BASKET-1", "token-1");

    assertThrows(RuntimeException.class, () -> backgroundChargeController.backgroundCharge(request));

    verify(backgroundChargeInPort, times(1))
        .processBackgroundCharge("BASKET-1", "token-1");
  }
}

