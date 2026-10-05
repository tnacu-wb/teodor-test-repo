package uk.co.whitbread.contentservice.roomtypes.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import uk.co.whitbread.contentservice.roomtypes.config.properties.CacheConfigProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.CacheConfigProperty;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CacheConfigTest {

    @Mock
    private RedisTemplate redisTemplate;

    private ObjectMapper objectMapper;

    private CacheConfig target;

    @Mock
    private CacheConfigProperties cacheProperties;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        target = new CacheConfig(redisTemplate, objectMapper, cacheProperties);
    }


    @Test
    public void errorHandler() {
        final CacheErrorHandler cacheErrorHandler = target.errorHandler();
        assertNotNull(cacheErrorHandler);
    }

    @Test
    public void redisCacheManager() {
        Map<String, CacheConfigProperty> cachePropertiesCache = new HashMap<>();
        CacheConfigProperty cacheConfigProperty = new CacheConfigProperty();
        cacheConfigProperty.setTtl(300);
        cacheConfigProperty.setName("allRoomTypes");
        cachePropertiesCache.put("allRoomTypes", cacheConfigProperty);

        when(cacheProperties.getCache()).thenReturn(cachePropertiesCache);

        final RedisCacheManager cacheManager = (RedisCacheManager) target.redisCacheManager(redisConnectionFactory);
        cacheManager.afterPropertiesSet();

        assertEquals(cacheManager.getCacheNames().stream().findFirst().orElse(null), "allRoomTypes");
        assertEquals(cacheManager.getCacheNames().stream().findFirst().orElse(null), "allRoomTypes");
    }

}

