package uk.co.whitbread.hotel.card.client.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Payment3CPFallbackFactoryTest {

  private Payment3CPFallbackFactory payment3CPFallbackFactory;

  @BeforeEach
  void setUp() {
    payment3CPFallbackFactory = new Payment3CPFallbackFactory();
  }

  @Test
  void create_initiateSaveCard_throwsThreeCPClientException() {
    // Arrange
    var payment3CPFallback = payment3CPFallbackFactory.create(new Throwable());
    // Act & Assert
    assertThrows(ThreeCPClientException.class, () -> {
      payment3CPFallback
          .initiateSaveCard(null);
    });
  }

  @Test
  void create_createToken_throwsThreeCPClientException() {
    // Arrange
    var payment3CPFallback = payment3CPFallbackFactory.create(new Throwable());
    // Act & Assert
    assertThrows(ThreeCPClientException.class, () -> {
      payment3CPFallback
          .createToken(null);
    });
  }

  @Test
  void create_initiateAuthorizeSca_throwsThreeCPClientException() {
    // Arrange
    var payment3CPFallback = payment3CPFallbackFactory.create(new Throwable());
    // Act & Assert
    assertThrows(ThreeCPClientException.class, () -> payment3CPFallback
            .initiateAuthorizeSca(null));
  }

}