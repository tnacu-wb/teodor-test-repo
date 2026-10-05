package uk.co.whitbread.hotel.register.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
@Import(DataRedisAutoConfiguration.class)
public class HotelRegisterRedisConfig {}
