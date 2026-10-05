package uk.co.whitbread.promo.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.PromoServiceApplication;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;

import java.util.UUID;

@SpringBootTest(classes = PromoServiceApplication.class)
@ActiveProfiles("test")
class PromoCodeRepositoryItTest {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @MockitoBean
    private OhipAdapterClient ohipAdapterClient;

    @MockitoBean
    private S3Presigner s3Presigner;

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private Tracer tracer;

    private PromoCodeEntity createPromoCodeEntity(UUID batchId, String code) {
        PromoCodeEntity entity = new PromoCodeEntity();
        entity.setCode(code);
        entity.setBatchId(batchId);
        return entity;
    }

    @Test
    void countByBatchId__shouldReturnCorrectCount() {
        UUID batchId = UUID.randomUUID();

        PromoCodeEntity code1 = createPromoCodeEntity(batchId, "CODE1");
        PromoCodeEntity code2 = createPromoCodeEntity(batchId, "CODE2");

        promoCodeRepository.save(code1);
        promoCodeRepository.save(code2);

        long count = promoCodeRepository.countByBatchId(batchId);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByBatchId__shouldReturnZero_whenNoCodesExist() {
        UUID batchId = UUID.randomUUID();

        long count = promoCodeRepository.countByBatchId(batchId);

        assertThat(count).isZero();
    }
}
