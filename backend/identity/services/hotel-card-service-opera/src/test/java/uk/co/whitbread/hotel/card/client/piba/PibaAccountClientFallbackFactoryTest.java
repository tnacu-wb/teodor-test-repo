package uk.co.whitbread.hotel.card.client.piba;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.card.exceptions.PibaAccountClientException;

class PibaAccountClientFallbackFactoryTest {

  private PibaAccountClientFallbackFactory pibaAccountClientFallbackFactory;

  @BeforeEach
  void setUp() {
    pibaAccountClientFallbackFactory = new PibaAccountClientFallbackFactory();
  }

  @Test
  void create_createCardHolderUser_throwsThreeCPClientException() {
    // Arrange
    var pibaAccountFallback = pibaAccountClientFallbackFactory.create(new Throwable());
    // Act & Assert
    assertThrows(PibaAccountClientException.class, () -> {
      pibaAccountFallback.registerTetheredUser(null, null);
    });
  }


}