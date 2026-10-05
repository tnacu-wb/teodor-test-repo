package uk.co.whitbread.hotel.card.client.worldline;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.hotel.card.exceptions.WorldlineAuthenticationException;
import uk.co.whitbread.hotel.card.exceptions.WorldlineClientException;

class WorldlineFallbackFactoryTest {

  private WorldlineFallbackFactory worldlineFallbackFactory;

  @BeforeEach
  void setUp() {
    worldlineFallbackFactory = new WorldlineFallbackFactory();
  }

  // --- createCardHolderUser ---

  @Test
  void create_createCardHolderUser_withGenericThrowable_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(new Throwable());
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .createCardHolderUser(null, null, null, null, null, null));
  }

  @ParameterizedTest
  @ValueSource(ints = {401, 403})
  void create_createCardHolderUser_withFeignAuthenticationError_throwsWorldlineAuthenticationException(int status) {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(status));
    assertThrows(WorldlineAuthenticationException.class, () -> worldlineFallback
        .createCardHolderUser(null, null, null, null, null, null));
  }

  @Test
  void create_createCardHolderUser_withFeignNonAuthError_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(500));
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .createCardHolderUser(null, null, null, null, null, null));
  }

  // --- costCentreDetails ---

  @Test
  void create_costCentreDetails_withGenericThrowable_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(new Throwable());
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .costCentreDetails(null, null, null, null, null));
  }

  @ParameterizedTest
  @ValueSource(ints = {401, 403})
  void create_costCentreDetails_withFeignAuthenticationError_throwsWorldlineAuthenticationException(int status) {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(status));
    assertThrows(WorldlineAuthenticationException.class, () -> worldlineFallback
        .costCentreDetails(null, null, null, null, null));
  }

  @Test
  void create_costCentreDetails_withFeignNonAuthError_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(500));
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .costCentreDetails(null, null, null, null, null));
  }

  // --- replaceCard ---

  @Test
  void create_replaceCard_withGenericThrowable_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(new Throwable());
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .replaceCard(null, null, null, null, null, null));
  }

  @ParameterizedTest
  @ValueSource(ints = {401, 403})
  void create_replaceCard_withFeignAuthenticationError_throwsWorldlineAuthenticationException(int status) {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(status));
    assertThrows(WorldlineAuthenticationException.class, () -> worldlineFallback
        .replaceCard(null, null, null, null, null, null));
  }

  @Test
  void create_replaceCard_withFeignNonAuthError_throwsWorldlineClientException() {
    var worldlineFallback = worldlineFallbackFactory.create(feignExceptionWithStatus(500));
    assertThrows(WorldlineClientException.class, () -> worldlineFallback
        .replaceCard(null, null, null, null, null, null));
  }

  // --- helpers ---

  private FeignException feignExceptionWithStatus(int status) {
    var feignException = mock(FeignException.class);
    when(feignException.status()).thenReturn(status);
    return feignException;
  }

}
