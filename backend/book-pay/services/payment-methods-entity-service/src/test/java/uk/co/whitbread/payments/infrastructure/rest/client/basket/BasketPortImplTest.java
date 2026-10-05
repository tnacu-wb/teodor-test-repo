package uk.co.whitbread.payments.infrastructure.rest.client.basket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.model.out.Basket;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions.BasketBadRequestException;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out.BasketDto;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out.BasketStatusEnum;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.service.BasketClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketPortImplTest {

  @Mock
  private BasketClient basketClient;

  @InjectMocks
  private BasketPortImpl underTest;

  @Test
  void getBasket_success() {
    // Arrange
    BasketDto basketDto = BasketDto.builder()
        .hotelId("hotel123")
        .channel("WEB")
        .idContext("ctx123")
        .status(BasketStatusEnum.COMPLETED)
        .build();

    when(basketClient.sendGetBasketByReference("ref123")).thenReturn(basketDto);

    //Act
    Basket basket = underTest.getBasket("ref123");

    //Assert
    assertEquals("hotel123", basket.getHotelId());
    assertEquals("WEB", basket.getChannel());
    assertEquals("ctx123", basket.getIdContext());
    assertEquals("COMPLETED", basket.getBasketStatus());
  }

  @Test
  void getBasket_nullBody() {
    // Arrange

    when(basketClient.sendGetBasketByReference("ref123")).thenReturn(null);

    //Act
    Basket basket = underTest.getBasket("ref123");

    //Assert
    assertNull(basket);
  }

  @Test
  void getBasket_notFound() {
    // Arrange
    String errorMessage = "Basket not found";
    when(basketClient.sendGetBasketByReference("notfound"))
        .thenThrow(new BasketBadRequestException("error", errorMessage, new Exception(), 1));

    //Act
    var exception = assertThrows(BasketBadRequestException.class,
        () -> underTest.getBasket("notfound"));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }
}
