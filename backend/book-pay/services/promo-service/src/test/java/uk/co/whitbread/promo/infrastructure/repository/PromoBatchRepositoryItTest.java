
package uk.co.whitbread.promo.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.PromoServiceApplication;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = PromoServiceApplication.class)

@ActiveProfiles("test")
class PromoBatchRepositoryItTest {

    @Autowired
    private PromoBatchRepository promoBatchRepository;

    @MockitoBean
    private OhipAdapterClient ohipAdapterClient;

    @MockitoBean
    private S3Presigner s3Presigner;

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private Tracer tracer;

    private static final int TEST_BATCH_COUNT = 100_000;
    private static final int TEST_CODE_LENGTH = 12;
    private static final String TEST_OPERA_CODE = "ABCD";
    private static final String TEST_CAMPAIGN = "NEW_YEAR";
    private static final String TEST_REQUESTED_BY = "test-user";


    private PromoBatchEntity createPromoBatchEntity(String prefix){
        return PromoBatchEntity.builder()
                .campaignName(TEST_CAMPAIGN)
                .prefix(prefix)
                .batchCount(TEST_BATCH_COUNT)
                .codeLength(TEST_CODE_LENGTH)
                .operaPromoCode(TEST_OPERA_CODE)
                .requestedBy(TEST_REQUESTED_BY)
                .build();
    }

    @Test
    void save__shouldSaveInDb(){
        PromoBatchEntity batch=createPromoBatchEntity("NYS");
        var savedBatch = promoBatchRepository.save(batch);

        assertThat(savedBatch.getPrefix()).isEqualTo(batch.getPrefix());
        assertThat(savedBatch.getCampaignName()).isEqualTo(batch.getCampaignName());
        assertThat(savedBatch.getBatchId()).isNotNull();
    }

    @Test
    void findByStatusIn__shouldReturnBatchesMatchingStatus() {
        PromoBatchEntity batch1 = createPromoBatchEntity("ABCD");
        batch1.setStatus(PromoBatchStatus.COMPLETED);
        PromoBatchEntity batch2 = createPromoBatchEntity("XYZ");
        batch2.setStatus(PromoBatchStatus.RUNNING);
        promoBatchRepository.saveAll(List.of(batch1, batch2));

        List<PromoBatchEntity> createdBatches = promoBatchRepository.findByStatusIn(List.of(PromoBatchStatus.COMPLETED));

        assertThat(createdBatches).isNotNull();

        PromoBatchEntity result = createdBatches.get(0);
        assertThat(result.getStatus()).isEqualTo(PromoBatchStatus.COMPLETED);
        assertThat(result.getPrefix()).isEqualTo("ABCD");
    }

    @Test
    @Transactional
    void markAsDownloaded__shouldUpdateDownloadedFlag() {
        UUID batchId = UUID.randomUUID();
        PromoBatchEntity batch = createPromoBatchEntity("DL");
        batch.setBatchId(batchId);
        batch.setDownloaded(false);
        promoBatchRepository.saveAndFlush(batch);

        promoBatchRepository.markAsDownloaded(batch.getBatchId());

        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();

        PromoBatchEntity updatedBatch =
                promoBatchRepository.findById(batch.getBatchId()).orElseThrow();

        assertThat(updatedBatch.getDownloaded()).isTrue();
    }

    @Test
    void findAllProjectedBy__shouldReturnNonEmptyPage() {

        PromoBatchEntity batch = createPromoBatchEntity("PRJ");
        promoBatchRepository.save(batch);

        Pageable pageable = PageRequest.of(0, 10);

        var page = promoBatchRepository.findAllProjectedBy(pageable);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull();
        assertThat(page.getContent().size()).isNotZero();
    }
}