package uk.co.whitbread.infrastructure.rest.client.basket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.infrastructure.rest.client.BasketServiceClient;
import uk.co.whitbread.infrastructure.rest.client.basket.mapper.BasketMapper;
import uk.co.whitbread.basket.generated.models.BasketDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceOutPortImplTest {

  private static final String TEST_BASKET = "TEST_BASKET";
  @Mock
  private BasketServiceClient basketServiceClient;

  @Mock
  private BasketMapper basketMapper;

  @InjectMocks
  private BasketServiceOutPortImpl basketServiceOutPort;

  @Test
  void getBasket_shouldReturnBasket() {
    // Arrange
    var basketDto = new BasketDto();
    var expectedBasket = new Basket();

    when(basketServiceClient.getBasket(TEST_BASKET)).thenReturn(basketDto);
    when(basketMapper.toModel(basketDto)).thenReturn(expectedBasket);

    // Act
    var actualBasket = basketServiceOutPort.getBasket(TEST_BASKET);

    // Assert
    assertNotNull(actualBasket);
    assertEquals(expectedBasket, actualBasket);
    verify(basketServiceClient).getBasket(TEST_BASKET);
    verify(basketMapper).toModel(basketDto);
  }

  @Test
  void getBasket_shouldThrowExceptionWhenClientFails() {
    // Arrange
    when(basketServiceClient.getBasket(TEST_BASKET))
        .thenThrow(new RuntimeException("Service failure"));

    // Act & Assert
    var exception = assertThrows(RuntimeException.class, () -> {
      basketServiceOutPort.getBasket(TEST_BASKET);
    });

    assertEquals("Service failure", exception.getMessage());
    verify(basketServiceClient).getBasket(TEST_BASKET);
    verifyNoInteractions(basketMapper);
  }
}