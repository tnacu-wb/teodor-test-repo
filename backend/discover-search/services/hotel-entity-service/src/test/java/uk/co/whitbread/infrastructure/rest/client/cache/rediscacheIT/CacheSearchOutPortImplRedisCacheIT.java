package uk.co.whitbread.infrastructure.rest.client.cache.rediscacheIT;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.infrastructure.config.cache.RedisProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.cache.CacheSearchOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.cache.mapper.CacheResponseMapper;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@ContextConfiguration(classes = {RedisCacheTestConfig.class})
class CacheSearchOutPortImplRedisCacheIT {
  @Mock
  private CacheManager cacheManager;
  @Mock
  private CacheResponseMapper responseMapper;
  @Mock
  private OhipClient ohipClient;

  @Autowired
  private RedisTemplate<String, HotelStatusDto> hotelStatusesRedisTemplate;

  @Autowired
  private CacheKeyPrefix cacheKeyPrefix;

  private CacheSearchOutPortImpl cacheSearchOutPort;

  @BeforeEach
  void before() {
    RedisProperties redisProperties = new RedisProperties();
    redisProperties.setAvailabilityCacheTTLms(10000);
    redisProperties.setOnSaleFlagCacheTTLHours(1); //set to 10 seconds for testing

    cacheSearchOutPort = new CacheSearchOutPortImpl(cacheManager,
        responseMapper,
        Optional.empty(),
        Optional.empty(),
        Optional.of(hotelStatusesRedisTemplate),
        redisProperties,
        Optional.of(cacheKeyPrefix),
        ohipClient);
  }

  @Test
  void getOnsaleFlagFromCache_ReturnsFromCacheAndFetchesMissing_ThenCachesWithTTL()
      throws InterruptedException {
    List<HotelStatusDto> fromOpera = new ArrayList<>() {{
      add(new HotelStatusDto().hotelId("FRAMTI").onSale(false).pmsSource("OPERA"));
      add(new HotelStatusDto().hotelId("LONKIN").onSale(true).pmsSource("OPERA"));
    }};

    List<String> idsList = fromOpera.stream().map(HotelStatusDto::getHotelId).toList();

    when(ohipClient.getOnSaleFlagFromOpera(argThat(list ->
        new HashSet<>(list).equals(new HashSet<>(idsList))))
    )
        .thenReturn(fromOpera);

    cacheSearchOutPort.getOnsaleFlagFromCache(List.of("LONKIN", "FRAMTI"));

    String lonkinKey = cacheKeyPrefix.compute("LONKIN");
    String framtiKey = cacheKeyPrefix.compute("FRAMTI");
    hotelStatusesRedisTemplate.expire(lonkinKey, 2, java.util.concurrent.TimeUnit.SECONDS);
    hotelStatusesRedisTemplate.expire(framtiKey, 2, java.util.concurrent.TimeUnit.SECONDS);

    Thread.sleep(1000);
    cacheSearchOutPort.getOnsaleFlagFromCache(List.of("LONKIN", "FRAMTI"));

    Thread.sleep(1000);
    cacheSearchOutPort.getOnsaleFlagFromCache(List.of("LONKIN", "FRAMTI"));

    verify(ohipClient, times(2))
        .getOnSaleFlagFromOpera(argThat(list ->
            new HashSet<>(list).equals(new HashSet<>(idsList)))
        );
  }
}
