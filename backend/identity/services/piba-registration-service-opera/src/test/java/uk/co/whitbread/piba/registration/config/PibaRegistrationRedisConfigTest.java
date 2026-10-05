package uk.co.whitbread.piba.registration.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import uk.co.whitbread.shared.cdh.config.RedisConfig;

class PibaRegistrationRedisConfigTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withUserConfiguration(PibaRegistrationRedisConfig.class, RedisConfig.class)
      .withPropertyValues(
          "spring.cache.type=redis",
          "spring.data.redis.cluster.nodes=localhost:6379");

  @Test
  void shouldCreateCdhCacheManagersWhenRedisCachingEnabled() {
    contextRunner.run(context -> {
      assertThat(context).hasSingleBean(RedisConnectionFactory.class);
      assertThat(context).hasBean("cacheManager");
      assertThat(context).hasBean("cacheManager1HourCdh");
    });
  }
}
