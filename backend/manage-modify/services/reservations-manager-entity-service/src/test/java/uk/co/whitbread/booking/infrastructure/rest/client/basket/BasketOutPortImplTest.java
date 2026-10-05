package uk.co.whitbread.booking.infrastructure.rest.client.basket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.model.out.BasketForBookingReferencesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;


@ExtendWith(MockitoExtension.class)
class BasketOutPortImplTest {

  @Mock
  private BasketClient basketClient;

  @InjectMocks
  private BasketOutPortImpl basketOutPort;

  @Test
  void getBasketStatusesForBookingRefs_success() {
    // Arrange
    var basketResponse = List.of(
        new BasketForBookingReferencesDto(BasketStatus.COMPLETED, "ref1",
            List.of(new BasketItemDto())),
        new BasketForBookingReferencesDto(BasketStatus.PRE_CHECKED_IN, "ref2",
            List.of(new BasketItemDto()))
    );
    when(basketClient.getBasketsForBookingReferences(any())).thenReturn(basketResponse);

    // Act
    var response = basketOutPort.getBasketStatusesForBookingRefs(Set.of("ref1", "ref2"));

    // Assert
    assertNotNull(response);
    assertEquals(BasketStatus.COMPLETED, response.get("ref1"));
    assertEquals(BasketStatus.PRE_CHECKED_IN, response.get("ref2"));
  }

  @Test
  void getBasketStatusesForBookingRefs_noEligibleBookings() {
    // Act
    var response = basketOutPort.getBasketStatusesForBookingRefs(Set.of());

    // Assert
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  @Test
  void getBasketSourceIdsForBookingRefs_success() {
    // Arrange
    var basketItem1 = new BasketItemDto();
    basketItem1.setSourceId("sourceA");
    var basketItem2 = new BasketItemDto();
    basketItem2.setSourceId("sourceB");
    var basketItemNoSource = new BasketItemDto();
    var basketResponse = List.of(
        new BasketForBookingReferencesDto(BasketStatus.COMPLETED, "ref1", List.of(basketItem1)),
        new BasketForBookingReferencesDto(BasketStatus.PRE_CHECKED_IN, "ref2", List.of(basketItem2)),
        new BasketForBookingReferencesDto(BasketStatus.COMPLETED, "ref3", List.of(basketItemNoSource))
    );
    when(basketClient.getBasketsForBookingReferences(any())).thenReturn(basketResponse);

    // Act
    var result = basketOutPort.getBasketSourceIdsForBookingRefs(Set.of("ref1", "ref2", "ref3"));

    // Assert
    assertNotNull(result);
    assertTrue(result.containsKey("ref1"));
    assertTrue(result.containsKey("ref2"));
    assertTrue(result.containsKey("ref3"));
    assertNotNull(result.get("ref1"));
    assertNotNull(result.get("ref2"));
    assertNotNull(result.get("ref3"));
    assertTrue(result.get("ref1").contains("sourceA"));
    assertTrue(result.get("ref2").contains("sourceB"));
    assertTrue(result.get("ref3").isEmpty());
  }

  @Test
  void getBasketSourceIdsForBookingRefs_emptyInput() {
    // Act
    var result = basketOutPort.getBasketSourceIdsForBookingRefs(Set.of());

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

}
