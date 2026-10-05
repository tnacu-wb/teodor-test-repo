package uk.co.whitbread.promo.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.PromoServiceApplication;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = PromoServiceApplication.class)
@ActiveProfiles("test")
class BatchEligibilityRepositoryItTest {

    @Autowired
    private BatchEligibilityRepository batchEligibilityRepository;

    @MockitoBean
    private OhipAdapterClient ohipAdapterClient;

    @MockitoBean
    private S3Presigner s3Presigner;

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private Tracer tracer;

  private BatchEligibilityEntity createEntity(
          UUID batchId,
          Region region,
          Channel channel,
          Platform platform) {

      return BatchEligibilityEntity.builder()
              .batchId(batchId)
              .region(region)
              .channel(channel)
              .platform(platform)
              .build();
  }

  @Test
  void findByBatchIdIn_shouldReturnMatchingEligibilities() {

      UUID batch1 = UUID.randomUUID();
      UUID batch2 = UUID.randomUUID();

      BatchEligibilityEntity entity1 =
              createEntity(batch1, Region.GB, Channel.PI, Platform.WEB);

      BatchEligibilityEntity entity2 =
              createEntity(batch1, Region.GB, Channel.PI, Platform.MOBILE);

      BatchEligibilityEntity entity3 =
              createEntity(batch2, Region.DE, Channel.CCUI, Platform.WEB);

      batchEligibilityRepository.saveAll(
              List.of(entity1, entity2, entity3));

      List<BatchEligibilityEntity> result =
              batchEligibilityRepository.findByBatchIdIn(List.of(batch1));

      assertThat(result).isNotNull().hasSize(2);

      assertThat(result.get(0).getBatchId()).isEqualTo(batch1);
      assertThat(result.get(1).getBatchId()).isEqualTo(batch1);
    }

}
