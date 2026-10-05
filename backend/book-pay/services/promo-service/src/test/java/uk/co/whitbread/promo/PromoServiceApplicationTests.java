package uk.co.whitbread.promo;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.promo.infrastructure.adapter.PromoCodeStagingManager;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoBatchS3Uploader;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PromoServiceApplicationTests {

  @Autowired
  ApplicationContext applicationContext;

  @MockitoBean
  private PromoCodeStagingManager promoCodeStagingManager;

  @MockitoBean
  private S3Presigner s3Presigner;

  @MockitoBean
  private S3Client s3Client;

  @MockitoBean
  private PromoBatchS3Uploader s3Uploader;

  @MockitoBean
  private Tracer tracer;

  @MockitoBean
  private UnleashWrapper unleashWrapper;

  @Test
  void contextLoads() {
  }
}