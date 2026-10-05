package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.SnowdropLookupException;

@Disabled("Not compatible with Kaniko executor")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"increase-batch-size"})
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
@Slf4j

class SnowdropFeignClientFallbackFactoryTest {

  @Autowired
  private SnowdropFeignClient.SnowdropFeignClientFallbackFactory snowdropFeignClientFallbackFactory;

  @MockitoBean
  private Throwable mockThrowable;

  @Test
  void shouldThrowSnowdropLookupException() {
    SnowdropFeignClient snowdropFeignClient = snowdropFeignClientFallbackFactory.create(mockThrowable);

    assertThatExceptionOfType(SnowdropLookupException.class)
        .isThrownBy(() -> snowdropFeignClient.getHotelsFromSnowdrop(null, null))
        .withMessageContaining("Failed to call snowdrop to get hotels by placeId=null and radius=null");
  }

}
