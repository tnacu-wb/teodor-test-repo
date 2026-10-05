package uk.co.whitbread.hotel.register.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class HotelRegisterRedisConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(HotelRegisterRedisConfig.class));


    @Test
    void shouldLoadRedisConfig_whenCacheTypeIsRedis() {
        contextRunner
            .withPropertyValues("spring.cache.type=redis")
            .run(context -> {
                assertThat(context).hasSingleBean(HotelRegisterRedisConfig.class);
            });
    }

    @Test
    void shouldNotLoadRedisConfig_whenCacheTypeIsNotRedis() {
        contextRunner
            .withPropertyValues("spring.cache.type=none")
            .run(context -> {
                assertThat(context).doesNotHaveBean(HotelRegisterRedisConfig.class);
            });
    }

    @Test
    void shouldNotLoadRedisConfig_whenCacheTypePropertyIsMissing() {
        contextRunner
            .run(context -> {
                assertThat(context).doesNotHaveBean(HotelRegisterRedisConfig.class);
            });
    }
}
