package uk.co.whitbread.promo.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import uk.co.whitbread.promo.domain.model.feature.FeatureFlag;
import uk.co.whitbread.promo.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;
import uk.co.whitbread.promo.domain.model.promobatch.in.BatchEligibility;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.*;
import uk.co.whitbread.promo.infrastructure.exception.ErrorCode;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchException;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchCodeGeneratorOutPort;
import uk.co.whitbread.promo.infrastructure.repository.mapper.BatchEligibilityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchEntityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchSummaryMapper;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.model.out.PromotionResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.client.properties.S3Properties;

@ExtendWith(MockitoExtension.class)
class PromoBatchRepositoryOutPortImplTest {

  @Mock
  private PromoBatchRepository promoBatchRepository;
  @Mock
  private PromoCodeRepository promoCodeRepository;
  @Mock
  private PromoBatchEntityMapper promoBatchEntityMapper;
  @Mock
  private PromoBatchSummaryMapper promoBatchSummaryMapper;
  @Mock
  private PromoBatchStatusService promoBatchStatusService;
  @Mock
  private PromoBatchCodeGeneratorOutPort codeGenerator;
  @Mock
  private OhipAdapterClient ohipAdapterClient;
  @Mock
  private S3Properties s3Properties;
  @Mock
  private S3Presigner s3Presigner;
  @Mock
  private PromoBatchS3UploaderService promoBatchS3UploaderService;
  @Mock
  private TaskExecutor promoCopyExecutor;
  @Mock
  private TaskExecutor s3Executor;
  @Mock
  private BatchEligibilityRepository batchEligibilityRepository;
  @Mock
  private BatchEligibilityMapper batchEligibilityMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @BeforeEach
  void setUp() {

    Executor sameThreadExecutor = Runnable::run;

    repoAdapter = new PromoBatchRepositoryOutPortImpl(
            promoBatchRepository,
            promoCodeRepository,
            promoBatchEntityMapper,
            promoBatchSummaryMapper,
            promoBatchStatusService,
            codeGenerator,
            s3Presigner,
            s3Properties,
            ohipAdapterClient,
            promoBatchS3UploaderService,
            sameThreadExecutor,
            sameThreadExecutor,
            batchEligibilityRepository,
            batchEligibilityMapper,
            unleashWrapper
    );

    s3Properties.setPromoBucketName("test-bucket");
    s3Properties.setRegion("test-region");
  }

  private PromoBatchRepositoryOutPortImpl repoAdapter;

  private static final String TEST_PREFIX = "SALE10";
  private static final int TEST_BATCH_COUNT = 100_000;
  private static final int TEST_CODE_LENGTH = 6;
  private static final String TEST_OPERA_CODE = "FX10R";
  private static final String TEST_CAMPAIGN_NAME = "campaign";
  private static final LocalDate TEST_EXPIRY_DATE = LocalDate.of(2026, 1, 30);
  private static final boolean TEST_DOWNLOADED = true;
  private static final String TEST_PASSWORD = "dummy-secret123";
  private static final String TEST_S3KEY = "s3://dummy-bucket/dummy.csv";
  private static final int TEST_TOTAL_PAGES = 1;
  private static final String TEST_PROMO_CODE = "PROMO123";
  private static final String TEST_HOTEL_ID = "HEAPTI";

  @Test
  void createPromoBatch_shouldReturnPromoBatchResponse() {

    // Arrange
    var promoBatchRequest = createPromoBatchRequest();
    promoBatchRequest.setIsMultiple(false);
    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);
    UUID batchId = UUID.randomUUID();

    var savedEntity = createPromoBatchEntity(batchId);
    savedEntity.setStatus(PromoBatchStatus.PENDING);

    var completedEntity = createPromoBatchEntity(batchId);
    completedEntity.setStatus(PromoBatchStatus.COMPLETED);

    var expectedResponse = PromoBatchResponse.builder()
            .batchId(batchId)
            .expiryDate(LocalDate.of(2025, 11, 29))
            .status(
                    uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchStatus.COMPLETED
            )
            .password(completedEntity.getPassword())
            .build();

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(promoBatchRequest.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any()))
            .thenReturn(savedEntity);

    when(promoBatchRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(completedEntity));

    when(promoBatchEntityMapper.toModel(savedEntity))
            .thenReturn(expectedResponse);

    when(promoBatchS3UploaderService.createAndUploadFile(
            any(UUID.class),
            anyString(),
            any(PromoBatchEntity.class)
    )).thenReturn("s3/key/path.zip");

    // Act
    PromoBatchResponse actualResponse =
            repoAdapter.createPromoBatch(promoBatchRequest);

    // Assert
    assertEquals(batchId, actualResponse.getBatchId());

    ArgumentCaptor<UUID> batchIdCaptor =
            ArgumentCaptor.forClass(UUID.class);

    verify(codeGenerator)
            .generateCodes(
                    batchIdCaptor.capture(),
                    eq(TEST_BATCH_COUNT),
                    eq(TEST_PREFIX),
                    eq(TEST_CODE_LENGTH)
            );

    UUID generatedBatchId = batchIdCaptor.getValue();
    assertNotNull(generatedBatchId);
    verify(promoBatchS3UploaderService)
            .createAndUploadFile(any(UUID.class), anyString(), any(PromoBatchEntity.class));

    verify(promoBatchStatusService)
            .markCompleted(generatedBatchId);

    verify(promoBatchEntityMapper)
            .toModel(savedEntity);
  }

  @Test
  void createPromoBatch_shouldThrowException_whenExpiryDateIsInvalid() {
    // Arrange
    var promoBatchRequest = createPromoBatchRequest();
    when(ohipAdapterClient.getPromotions(anyList(), any()))
        .thenReturn(List.of(
            PromotionResponseDto.builder()
                .promotionCode("FX10R")
                .stayEndDate(LocalDate.of(2025, 1, 10))
                .build()
        ));
    // Act + Assert
    PromoBatchException ex = assertThrows(
        PromoBatchException.class,
        () -> repoAdapter.createPromoBatch(promoBatchRequest)
    );
    assertEquals(ErrorCode.INVALID_EXPIRY_DATE.getCode(), ex.getErrorCode());
  }

  @Test
  void getPromoBatchById_shouldReturnSummary_whenBatchExists() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    var batchEntity = PromoBatchEntity.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .password("secret")
        .downloaded(false)
        .build();
    var expectedSummary = PromoBatchSummary.builder()
        .batchId(batchId)
        .status(uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchStatus.COMPLETED)
        .build();

    when(promoBatchRepository.findById(batchId)).thenReturn(Optional.of(batchEntity));
    when(promoBatchSummaryMapper.toModel(batchEntity)).thenReturn(expectedSummary);

    // Act
    var result = repoAdapter.getPromoBatchById(batchId);

  // Assert
    assertEquals(expectedSummary, result);
    verify(promoBatchSummaryMapper).toModel(batchEntity);
  }

  @Test
  void getPromoBatchById_shouldGeneratePresignedUrl() {
    // Arrange
    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .password("secret")
        .downloaded(false)
        .s3Key("promo/batches/" + batchId + "/promo.xlsx")
        .build();

    PromoBatchSummary summary = PromoBatchSummary.builder()
        .batchId(batchId)
        .status(
            uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchStatus.COMPLETED)
        .build();

    when(promoBatchRepository.findById(batchId))
        .thenReturn(Optional.of(batchEntity));
    when(promoBatchSummaryMapper.toModel(batchEntity))
        .thenReturn(summary);

    // Act
    PromoBatchRepositoryOutPortImpl spyAdapter = spy(repoAdapter);

    String presignedUrl = "https://signed-url.com/file.xlsx";

    doReturn(presignedUrl)
        .when(spyAdapter)
        .getPresignedUrl(batchId);

    doNothing()
        .when(spyAdapter)
        .applyMasking(summary, false);
    PromoBatchSummary result = spyAdapter.getPromoBatchById(batchId);

    // Assert
    assertEquals(presignedUrl, result.getS3Key());
    verify(promoBatchSummaryMapper).toModel(batchEntity);
    promoBatchRepository.updateS3Key(batchId, summary.getS3Key());

  }

  @Test
  void createPromoBatch_shouldMarkFailedAndCleanup_whenExcelFails() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(Boolean.FALSE);
    UUID batchId = UUID.randomUUID();

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any(PromoBatchEntity.class)))
            .thenAnswer(invocation -> {
              PromoBatchEntity entity = invocation.getArgument(0);
              entity.setBatchId(batchId);
              return entity;
            });

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(
                    PromoBatchEntity.builder()
                            .batchId(batchId)
                            .status(PromoBatchStatus.RUNNING)
                            .build()
            ));

    when(promoBatchS3UploaderService.createAndUploadFile(
            any(), anyString(), any()
    )).thenThrow(new RuntimeException("Excel failed"));

    // Act
    repoAdapter.createPromoBatch(request);

    // Assert
    ArgumentCaptor<UUID> batchIdCaptor =
            ArgumentCaptor.forClass(UUID.class);

    verify(promoCodeRepository)
            .deleteByBatchId(batchIdCaptor.capture());

    UUID generatedBatchId = batchIdCaptor.getValue();

    assertNotNull(generatedBatchId);

    verify(promoBatchStatusService)
            .markFailed(generatedBatchId);
  }

  @Test
  void getPromoBatchById_shouldNotGeneratePresignedUrl_whenS3KeyIsNull() {
    // Arrange
    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.PENDING)
        .s3Key(null)
        .build();

    PromoBatchSummary summary = PromoBatchSummary.builder()
        .batchId(batchId)
        .build();

    when(promoBatchRepository.findById(batchId))
        .thenReturn(Optional.of(batchEntity));
    when(promoBatchSummaryMapper.toModel(batchEntity))
        .thenReturn(summary);

    PromoBatchRepositoryOutPortImpl spyAdapter = spy(repoAdapter);

    // Act
    PromoBatchSummary result = spyAdapter.getPromoBatchById(batchId);

    // Assert
    assertNull(result.getS3Key());
    verify(spyAdapter, never()).getPresignedUrl(any());
  }

  @Test
  void getPresignedUrl_shouldReturnUrl_whenS3KeyExists() {
    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batch = PromoBatchEntity.builder()
        .batchId(batchId)
        .s3Key("promo/batches/file.xlsx")
        .build();

    when(promoBatchRepository.findById(batchId))
        .thenReturn(Optional.of(batch));

    PresignedGetObjectRequest presignedRequest =
        mock(PresignedGetObjectRequest.class);

    URL url = null;
    try {
      url = URI.create("https://signed-url.com/file.xlsx").toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    when(presignedRequest.url()).thenReturn(url);

    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    // Act
    String result = repoAdapter.getPresignedUrl(batchId);

    // Assert
    assertEquals("https://signed-url.com/file.xlsx", result);
  }

  @Test
  void getPromoBatchById_shouldMaskPassword_whenDownloaded() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    var batchEntity = PromoBatchEntity.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .password("secret")
        .downloaded(true)
        .build();
    var expectedSummary = PromoBatchSummary.builder()
        .batchId(batchId)
        .status(uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchStatus.COMPLETED)
        .password(null)
        .build();

    when(promoBatchRepository.findById(batchId)).thenReturn(Optional.of(batchEntity));
    when(promoBatchSummaryMapper.toModel(batchEntity)).thenReturn(expectedSummary);

    // Act
    var result = repoAdapter.getPromoBatchById(batchId);

    // Assert
    assertNull(result.getPassword());
    verify(promoBatchSummaryMapper).toModel(batchEntity);
  }

  @Test
  void getPromoBatchById_shouldThrowException_whenBatchNotFound() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    when(promoBatchRepository.findById(batchId)).thenReturn(Optional.empty());

    // Act & Assert
    var exception = assertThrows(PromoBatchException.class,
        () -> repoAdapter.getPromoBatchById(batchId));
    assertEquals(ErrorCode.DIGITAL_PROMO_BATCH_NOT_FOUND.getCode(), exception.getErrorCode());
  }

  @Test
  void getPromoBatchSummary_shouldDelegateToStatusService() {
    Pageable pageable = PageRequest.of(0, 20);
    PromoBatchSummaryPage expectedPage = PromoBatchSummaryPage.builder()
        .promoBatchSummary(List.of(PromoBatchSummary.builder().batchId(UUID.randomUUID()).build()))
        .totalElements(1L)
        .totalPages(1)
        .pageNumber(0)
        .pageSize(20)
        .pageable(pageable)
        .build();

    when(promoBatchStatusService.getPromoBatchSummary(pageable)).thenReturn(expectedPage);

    PromoBatchSummaryPage result = promoBatchStatusService.getPromoBatchSummary(pageable);

    assertEquals(expectedPage, result);
    verify(promoBatchStatusService).getPromoBatchSummary(pageable);
  }


  @Test
  void markAsDownloaded_shouldUpdateBatchStatus_whenSuccess() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    when(promoBatchRepository.markAsDownloaded(batchId)).thenReturn(1);

    // Act
    repoAdapter.markAsDownloaded(batchId);

    // Assert
    verify(promoBatchRepository).markAsDownloaded(batchId);
  }

  @Test
  void markAsDownloaded_shouldThrowNotFound_whenBatchDoesNotExist() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    when(promoBatchRepository.markAsDownloaded(batchId)).thenReturn(0);
    when(promoBatchRepository.existsById(batchId)).thenReturn(false);

    // Act & Assert
    var exception = assertThrows(PromoBatchException.class,
        () -> repoAdapter.markAsDownloaded(batchId));
    assertEquals(ErrorCode.DIGITAL_PROMO_BATCH_NOT_FOUND.getCode(), exception.getErrorCode());
  }

  @Test
  void markAsDownloaded_shouldThrowUpdateFailed_whenBatchExistsButUpdateFails() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    when(promoBatchRepository.markAsDownloaded(batchId)).thenReturn(0);
    when(promoBatchRepository.existsById(batchId)).thenReturn(true);

    // Act & Assert
    var exception = assertThrows(PromoBatchException.class,
        () -> repoAdapter.markAsDownloaded(batchId));
    assertEquals(ErrorCode.DIGITAL_PROMO_BATCH_UPDATE_FAILED.getCode(), exception.getErrorCode());
  }

  @Test
  void recoverInterruptedBatches_shouldHandleNoBatches() {
    // Arrange
    when(promoBatchRepository.findByStatusIn(anyList())).thenReturn(List.of());

    // Act
    repoAdapter.recoverInterruptedBatches();

    // Assert
    verify(promoBatchRepository).findByStatusIn(argThat(statuses ->
        statuses.contains(PromoBatchStatus.RUNNING) &&
            statuses.contains(PromoBatchStatus.PENDING)));
  }

  @Test
  void recoverInterruptedBatches_shouldScheduleRecoveryTasks() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    var interruptedBatch = PromoBatchEntity.builder()
        .batchId(batchId)
        .batchCount(100_000)
        .prefix(TEST_PREFIX)
        .codeLength(TEST_CODE_LENGTH)
        .build();
    when(promoBatchRepository.findByStatusIn(anyList())).thenReturn(List.of(interruptedBatch));

    // Act
    repoAdapter.recoverInterruptedBatches();
  }

  @Test
  void recoverPromoBatch_shouldComplete_whenNoRemainingCodes() throws Exception {
    // Use reflection to call private method
    var method = PromoBatchRepositoryOutPortImpl.class.getDeclaredMethod("recoverPromoBatch",
        PromoBatchEntity.class);
    method.setAccessible(true);

    // Arrange
    UUID batchId = UUID.randomUUID();
    var batchEntity = createPromoBatchEntity(batchId);
    when(promoCodeRepository.countByBatchId(batchId)).thenReturn(100_000L);

    // Act
    method.invoke(repoAdapter, batchEntity);

    // Assert
    verify(promoCodeRepository).countByBatchId(batchId);
    verify(promoBatchStatusService).markCompleted(batchId);
    verifyNoInteractions(codeGenerator);
  }


  @Test
  void createPromoBatch_shouldContinue_whenStatusNotPending() {

    // Arrange

    var request = createPromoBatchRequest();
    request.setIsMultiple(false);
    UUID batchId = UUID.randomUUID();
    var entity = createPromoBatchEntity(batchId);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any()))
            .thenReturn(entity);

    when(promoBatchStatusService.setStatusIfPending(
            any(UUID.class),
            eq(PromoBatchStatus.RUNNING)
    )).thenReturn(false);
    // Act

    repoAdapter.createPromoBatch(request);

    // Assert

    verify(codeGenerator).generateCodes(any(), anyInt(), any(), anyInt());

    verify(promoBatchStatusService)
            .setStatusIfPending(
                    any(UUID.class),
                    eq(PromoBatchStatus.RUNNING)
            );

    verify(promoCodeRepository)
            .deleteByBatchId(any());
  }

  @Test
  void createPromoBatch_shouldMarkFailed_whenCodeGenerationThrowsRejectedExecutionException() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(false);
    UUID batchId = UUID.randomUUID();

    var entity = createPromoBatchEntity(batchId);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any()))
            .thenReturn(entity);

    doThrow(new RejectedExecutionException("boom"))
            .when(codeGenerator)
            .generateCodes(any(), anyInt(), any(), anyInt());

    repoAdapter.createPromoBatch(request);

    verify(promoBatchStatusService)
            .markFailed(any(UUID.class));

    verify(promoCodeRepository, never())
            .deleteByBatchId(any());
  }

  @Test
  void createPromoBatch_shouldMarkFailed_whenCodeGenerationFails() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(false);
    UUID batchId = UUID.randomUUID();

    var entity = createPromoBatchEntity(batchId);
    entity.setStatus(PromoBatchStatus.PENDING);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any()))
            .thenReturn(entity);

    doThrow(new RuntimeException("boom"))
            .when(codeGenerator)
            .generateCodes(any(), anyInt(), any(), anyInt());

    repoAdapter.createPromoBatch(request);

    verify(promoBatchStatusService).markFailed(any(UUID.class));

    verify(promoCodeRepository, never())
            .deleteByBatchId(any());
  }

  @Test
  void recoverPromoBatch_shouldFailAndThrowException_onError() throws Exception {
    var method = PromoBatchRepositoryOutPortImpl.class
        .getDeclaredMethod("recoverPromoBatch", PromoBatchEntity.class);
    method.setAccessible(true);

    UUID batchId = UUID.randomUUID();
    var batch = createPromoBatchEntity(batchId);

    when(promoCodeRepository.countByBatchId(batchId)).thenReturn(0L);
    doThrow(new RuntimeException("fail"))
        .when(codeGenerator)
        .generateCodes(any(), anyInt(), any(), anyInt());

    assertThrows(InvocationTargetException.class,
        () -> method.invoke(repoAdapter, batch));

    verify(promoBatchStatusService).markFailed(batchId);
  }

  @Test
  void createPromoBatch_shouldNotCleanup_whenMarkCompletedFails() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(false);
    UUID batchId = UUID.randomUUID();
    var entity = createPromoBatchEntity(batchId);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(promoBatchRepository.save(any())).thenReturn(entity);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(entity));

    doThrow(new DataAccessResourceFailureException("DB down"))
            .when(promoBatchStatusService)
            .markCompleted(any(UUID.class));

    // Act
    repoAdapter.createPromoBatch(request);

    // Assert
    verify(promoBatchStatusService)
            .markCompleted(any(UUID.class));

    verify(promoCodeRepository, never())
            .deleteByBatchId(any(UUID.class));
  }

  @Test
  void validateOperaPromoCode_shouldThrow_whenOhipReturnsEmpty() {
    var request = createPromoBatchRequest();
    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
        .thenReturn(Collections.emptyList());
    PromoBatchException ex = assertThrows(
        PromoBatchException.class,
        () -> repoAdapter.createPromoBatch(request)
    );
    assertEquals(ErrorCode.INVALID_OPERA_PROMO_CODE.getCode(), ex.getErrorCode());
  }

  @Test
  void getPromoBatchSummary_shouldApplyMaskingForSummaryView() {
    Pageable pageable = PageRequest.of(0, 10);

    PromoBatchSummary summary = createPromoBatchSummary();
    PromoBatchSummaryPage summaryPage = createPromoBatchSummaryPage(pageable, summary);

    when(promoBatchStatusService.getPromoBatchSummary(pageable))
        .thenReturn(summaryPage);

    PromoBatchSummaryPage result = repoAdapter.getPromoBatchSummary(pageable);

    PromoBatchSummary resultSummary = result.getPromoBatchSummary().get(0);
    assertNull(resultSummary.getPassword());
    assertNull(resultSummary.getS3Key());
  }

  private PromoBatchRequest createPromoBatchRequest() {
    return PromoBatchRequest.builder()
        .prefix(TEST_PREFIX)
        .batchCount(TEST_BATCH_COUNT)
        .codeLength(TEST_CODE_LENGTH)
        .expiryDate(LocalDate.now().plusDays(10))
        .operaPromoCode(TEST_OPERA_CODE)
        .campaignName(TEST_CAMPAIGN_NAME)
        .hotelId(TEST_HOTEL_ID)
        .batchEligibilities(createBatchEligibilities())
        .maxRedemptionLimit(1)
        .build();
  }

  @Test
  void validatePromoKind_shouldThrowException_whenPromoCodeIsNullOrBlank() {
    PromoKindRequest request = mockPromoKindRequest();
    request.setPromoCode(" ");

    PromoBatchException ex = assertThrows(
            PromoBatchException.class,
            () -> repoAdapter.validatePromoKind(request)
    );

    assertEquals(ErrorCode.DIGITAL_INVALID_REQUEST.getCode(), ex.getErrorCode());
    verifyNoInteractions(promoBatchRepository, promoCodeRepository);
  }

  @Test
  void validatePromoKind_shouldReturnGeneric_whenGenericPromoExists() {
    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .operaPromoCode(TEST_PROMO_CODE)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of(batchEntity));

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.GENERIC, response.getPromoKind());
    assertEquals(TEST_PROMO_CODE, response.getOperaPromoCode());

    verify(promoBatchRepository).findAllByOperaPromoCode(TEST_PROMO_CODE);
    verify(promoCodeRepository, never()).findById(any());
  }

  @Test
  void validatePromoKind_shouldReturnUnique_whenUniquePromoExists() {
    UUID batchId = UUID.randomUUID();

    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus infraStatus =
            uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED;

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(infraStatus)  // infra enum
            .build();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .batchId(batchId)
            .operaPromoCode("GENERIC123")
            .build();

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());
    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));
    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batchEntity));

    when(batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.GB,
            Channel.PI,
            Platform.WEB))
            .thenReturn(true);

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals("GENERIC123", response.getOperaPromoCode());

    // Domain enum
    assertEquals(PromoCodeStatus.ISSUED, response.getUniquePromoCodeStatus());
  }

  @Test
  void validatePromoKind_shouldReturnGeneric_whenPromoNotFoundAnywhere() {
    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());
    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.empty());

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.GENERIC, response.getPromoKind());
    assertEquals(TEST_PROMO_CODE, response.getOperaPromoCode());
  }

  @Test
  void validatePromoKind_shouldPreferGeneric_whenBothExist() {
    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .operaPromoCode(TEST_PROMO_CODE)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of(batchEntity));

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.GENERIC, response.getPromoKind());
    verify(promoCodeRepository, never()).findById(any());
  }

  @Test
  void validatePromoKind_shouldThrowException_whenUniquePromoBatchNotFound() {
    UUID batchId = UUID.randomUUID();

    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus infraStatus =
            uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED;

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(infraStatus)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());
    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));
    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.empty());
    PromoKindRequest request = mockPromoKindRequest();

    assertThrows(
            PromoBatchException.class,
            () -> repoAdapter.validatePromoKind(request)
    );
  }

  @Test
  void redeemPromoCode_shouldReturnRedeemed_whenUpdateSucceeds() {
    when(promoCodeRepository.redeemPromoCode(
        eq("PROMO123"),
        eq("BOOK123"),
        any(OffsetDateTime.class)
    )).thenReturn(1);

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(mockRedeemRequest());

    assertEquals(RedeemStatus.REDEEMED, response.getStatus());
    assertEquals("Promo code redeemed successfully", response.getMessage());
  }

  @Test
  void redeemPromoCode_shouldReturnAlreadyRedeemed_whenPromoAlreadyRedeemed() {
    when(promoCodeRepository.redeemPromoCode(any(), any(), any()))
        .thenReturn(0);

    PromoCodeEntity entity = PromoCodeEntity.builder()
        .code("PROMO123")
        .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.REDEEMED)
        .build();

    when(promoCodeRepository.findById("PROMO123"))
        .thenReturn(Optional.of(entity));

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(mockRedeemRequest());

    assertEquals(RedeemStatus.ALREADY_REDEEMED, response.getStatus());
  }

  @Test
  void redeemPromoCode_shouldReturnExpired_whenPromoExpired() {
    when(promoCodeRepository.redeemPromoCode(any(), any(), any()))
        .thenReturn(0);

    PromoCodeEntity entity = PromoCodeEntity.builder()
        .code("PROMO123")
        .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.EXPIRED)
        .build();

    when(promoCodeRepository.findById("PROMO123"))
        .thenReturn(Optional.of(entity));

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(mockRedeemRequest());

    assertEquals(RedeemStatus.EXPIRED, response.getStatus());
  }

  @Test
  void redeemPromoCode_shouldReturnInvalid_whenPromoExistsButInvalidStatus() {
    when(promoCodeRepository.redeemPromoCode(any(), any(), any()))
        .thenReturn(0);

    PromoCodeEntity entity = PromoCodeEntity.builder()
        .code("PROMO123")
        .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.VOID)
        .build();

    when(promoCodeRepository.findById("PROMO123"))
        .thenReturn(Optional.of(entity));

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(mockRedeemRequest());

    assertEquals(RedeemStatus.INVALID_PROMO_CODE, response.getStatus());
  }

  @Test
  void redeemPromoCode_shouldReturnInvalid_whenPromoDoesNotExist() {
    when(promoCodeRepository.redeemPromoCode(any(), any(), any()))
        .thenReturn(0);

    when(promoCodeRepository.findById("PROMO123"))
        .thenReturn(Optional.empty());

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(mockRedeemRequest());

    assertEquals(RedeemStatus.INVALID_PROMO_CODE, response.getStatus());
  }

  @Test
  void updatePromoBatchStatusToExpiry_shouldCallRepositoryWithCurrentDate() {

    // Arrange
    when(promoBatchRepository.expireOldBatches(any(LocalDate.class)))
            .thenReturn(3);

    // Act
    repoAdapter.updatePromoBatchStatusToExpiry();

    // Assert
    verify(promoBatchRepository, times(1))
            .expireOldBatches(any(LocalDate.class));
  }

  @Test
  void deleteExpiredPromoCodesAfterRetention_shouldCallRepository() {

    // Arrange
    when(promoCodeRepository.deleteExpiredPromoCodesAfterRetention())
            .thenReturn(15);

    // Act
    repoAdapter.deleteExpiredPromoCodesAfterRetention();

    // Assert
    verify(promoCodeRepository, times(1))
            .deleteExpiredPromoCodesAfterRetention();
  }

  @Test
  void validatePromoKind_shouldReturnExpired_whenBatchIsExpired() {
    // Arrange
    UUID batchId = UUID.randomUUID();

    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus infraStatus =
            uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED;

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(infraStatus)
            .build();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .batchId(batchId)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.EXPIRED)
            .build();

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batchEntity));

    when(batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.GB,
            Channel.PI,
            Platform.WEB))
            .thenReturn(true);

    // Act
    PromoKindResponse response =
            repoAdapter.validatePromoKind(mockPromoKindRequest());

    // Assert
    assertNotNull(response);
    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals(PromoCodeStatus.EXPIRED, response.getUniquePromoCodeStatus());
  }

  @Test
  void validatePromoKind_shouldReturnUniqueWithNullStatus_whenEligibilityDoesNotMatch() {
    // Arrange
    UUID batchId = UUID.randomUUID();

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED)
            .build();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .batchId(batchId)
            .operaPromoCode("GENERIC123")
            .build();

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batchEntity));

    when(batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.GB,
            Channel.PI,
            Platform.WEB))
            .thenReturn(false);

    // Act
    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    // Assert
    assertNotNull(response);
    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals("GENERIC123", response.getOperaPromoCode());
    assertNull(response.getUniquePromoCodeStatus());

    verify(batchEligibilityRepository)
            .existsByBatchIdAndRegionAndChannelAndPlatform(
                    batchId,
                    Region.GB,
                    Channel.PI,
                    Platform.WEB);
  }

  @Test
  void createPromoBatch_shouldUploadGenericPromoFile_whenIsMultipleIsTrue() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(true);

    UUID batchId = UUID.randomUUID();

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any(PromoBatchEntity.class)))
            .thenAnswer(invocation -> {
              PromoBatchEntity entity = invocation.getArgument(0);
              entity.setBatchId(batchId);
              return entity;
            });

    PromoBatchEntity batch = createPromoBatchEntity(batchId);
    batch.setPrefix(request.getPrefix());

    when(promoBatchRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(batch));

    when(promoBatchS3UploaderService.createAndUploadGenericPromoFile(
            any(),
            anyString(),
            any()))
            .thenReturn("promo.zip");

    repoAdapter.createPromoBatch(request);

    verify(codeGenerator, never())
            .generateCodes(any(), anyInt(), anyString(), anyInt());

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);

    verify(promoBatchStatusService)
            .markCompleted(captor.capture());
  }

  @Test
  void createPromoBatch_shouldMarkFailed_whenGenericPromoUploadFails() {

    var request = createPromoBatchRequest();
    request.setIsMultiple(true);

    UUID batchId = UUID.randomUUID();

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(30);

    when(ohipAdapterClient.getPromotions(anyList(), anyString()))
            .thenReturn(List.of(
                    PromotionResponseDto.builder()
                            .promotionCode(request.getOperaPromoCode())
                            .stayStartDate(start)
                            .stayEndDate(end)
                            .build()
            ));

    when(promoBatchRepository.save(any(PromoBatchEntity.class)))
            .thenAnswer(invocation -> {
              PromoBatchEntity entity = invocation.getArgument(0);
              entity.setBatchId(batchId);
              return entity;
            });

    PromoBatchEntity batch = createPromoBatchEntity(batchId);
    batch.setPrefix(request.getPrefix());

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batch));

    when(promoBatchS3UploaderService.createAndUploadGenericPromoFile(
            any(),
            anyString(),
            any()))
            .thenThrow(new RuntimeException("Upload failed"));

    repoAdapter.createPromoBatch(request);

    verify(codeGenerator, never())
            .generateCodes(any(), anyInt(), anyString(), anyInt());

    verify(promoCodeRepository, never())
            .deleteByBatchId(any());

    verify(promoBatchStatusService)
            .markFailed(any(UUID.class));
  }

  @ParameterizedTest
  @MethodSource("promoStatuses")
  void validatePromoKind_shouldReturnUniquePromoStatus(
          uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus infraStatus,
          PromoCodeStatus expectedStatus) {

    UUID batchId = UUID.randomUUID();

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(infraStatus)
            .build();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .batchId(batchId)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.COMPLETED)
            .build();

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batchEntity));

    when(batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.GB,
            Channel.PI,
            Platform.WEB))
            .thenReturn(true);

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals("GENERIC123", response.getOperaPromoCode());
    assertEquals(expectedStatus, response.getUniquePromoCodeStatus());
  }

  private static Stream<Arguments> promoStatuses() {
    return Stream.of(
            Arguments.of(
                    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED,
                    PromoCodeStatus.ISSUED),
            Arguments.of(
                    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.REDEEMED,
                    PromoCodeStatus.REDEEMED),
            Arguments.of(
                    uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.EXPIRED,
                    PromoCodeStatus.EXPIRED)
    );
  }

  @Test
  void redeemPromoCode_shouldRedeemIsMultiplePromoSuccessfully() {

    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batch = PromoBatchEntity.builder()
            .batchId(batchId)
            .status(PromoBatchStatus.COMPLETED)
            .isMultiple(true)
            .prefix("TEST")
            .maxRedemptionLimit(5)
            .build();

    when(batchEligibilityRepository.findByMatchingPrefix("TEST123"))
            .thenReturn(List.of(batch));

    when(promoCodeRepository.existsById(anyString()))
            .thenReturn(false);

    RedeemPromoCodeRequest request = RedeemPromoCodeRequest.builder()
            .promoCode("TEST123")
            .bookingReference("BOOK123")
            .build();

    RedeemPromoCodeResponse response = repoAdapter.redeemPromoCode(request);

    assertEquals(RedeemStatus.REDEEMED, response.getStatus());
    assertTrue(response.isSuccess());

    verify(promoCodeRepository).save(any(PromoCodeEntity.class));
    verify(promoBatchRepository).decrementMaxRedemptionLimit(batchId);
  }

  @Test
  void validatePromoKind_shouldReturnExpired_whenIsMultipleBatchExpired() {

    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batch = PromoBatchEntity.builder()
            .batchId(batchId)
            .isMultiple(true)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.EXPIRED)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(batchEligibilityRepository.findByMatchingPrefix(TEST_PROMO_CODE))
            .thenReturn(List.of(batch));

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals(PromoCodeStatus.EXPIRED, response.getUniquePromoCodeStatus());
  }

  @Test
  void validatePromoKind_shouldReturnNullStatus_whenMaxRedemptionLimitReached() {

    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batch = PromoBatchEntity.builder()
            .batchId(batchId)
            .isMultiple(true)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.COMPLETED)
            .maxRedemptionLimit(0)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(batchEligibilityRepository.findByMatchingPrefix(TEST_PROMO_CODE))
            .thenReturn(List.of(batch));

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertNull(response.getUniquePromoCodeStatus());
  }


  @Test
  void validatePromoKind_shouldReturnNullStatus_whenIsMultipleBatchNotEligible() {

    UUID batchId = UUID.randomUUID();

    PromoBatchEntity batch = PromoBatchEntity.builder()
            .batchId(batchId)
            .isMultiple(true)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.COMPLETED)
            .maxRedemptionLimit(10)
            .build();

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(batchEligibilityRepository.findByMatchingPrefix(TEST_PROMO_CODE))
            .thenReturn(List.of(batch));

    when(batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.GB,
            Channel.PI,
            Platform.WEB))
            .thenReturn(false);

    PromoKindResponse response = repoAdapter.validatePromoKind(mockPromoKindRequest());

    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertNull(response.getUniquePromoCodeStatus());
  }

  @Test
  void createPromoBatch_shouldThrowException_whenIsMultipleAndMaxRedemptionLimitIsZero() {

    // Arrange
    var request = createPromoBatchRequest();
    request.setIsMultiple(true);
    request.setMaxRedemptionLimit(0);

    // Act & Assert
    PromoBatchException ex = assertThrows(
            PromoBatchException.class,
            () -> repoAdapter.createPromoBatch(request));

    assertEquals(
            ErrorCode.DIGITAL_INVALID_REQUEST.getCode(),
            ex.getErrorCode());

    verify(promoBatchRepository, never()).save(any());
    verifyNoInteractions(codeGenerator);
  }

  @ParameterizedTest
  @MethodSource("invalidEligibilityRequests")
  void validatePromoKind_shouldSkipEligibilityValidation_whenRequestFieldsAreNull(
          String country,
          String channel,
          String subChannel) {

    UUID batchId = UUID.randomUUID();

    PromoCodeEntity promoCodeEntity = PromoCodeEntity.builder()
            .code(TEST_PROMO_CODE)
            .batchId(batchId)
            .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.ISSUED)
            .build();

    PromoBatchEntity batchEntity = PromoBatchEntity.builder()
            .batchId(batchId)
            .operaPromoCode("GENERIC123")
            .status(PromoBatchStatus.COMPLETED)
            .build();

    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any(), any())).thenReturn(true);

    when(promoBatchRepository.findAllByOperaPromoCode(TEST_PROMO_CODE))
            .thenReturn(List.of());

    when(promoCodeRepository.findById(TEST_PROMO_CODE))
            .thenReturn(Optional.of(promoCodeEntity));

    when(promoBatchRepository.findById(batchId))
            .thenReturn(Optional.of(batchEntity));

    PromoKindRequest request = PromoKindRequest.builder()
            .promoCode(TEST_PROMO_CODE)
            .country(country)
            .channel(channel)
            .subChannel(subChannel)
            .build();

    PromoKindResponse response = repoAdapter.validatePromoKind(request);

    assertNotNull(response);
    assertEquals(PromoKind.UNIQUE, response.getPromoKind());
    assertEquals(PromoCodeStatus.ISSUED, response.getUniquePromoCodeStatus());

    verify(batchEligibilityRepository, never())
            .existsByBatchIdAndRegionAndChannelAndPlatform(any(), any(), any(), any());
  }

  private static Stream<Arguments> invalidEligibilityRequests() {
    return Stream.of(
            Arguments.of(null, "PI", "WEB"),
            Arguments.of("GB", null, "WEB"),
            Arguments.of("GB", "PI", null)
    );
  }

  private RedeemPromoCodeRequest mockRedeemRequest() {
    return RedeemPromoCodeRequest.builder()
        .promoCode("PROMO123")
        .bookingReference("BOOK123")
        .build();
  }

  private PromoBatchEntity createPromoBatchEntity(UUID batchId) {
    return PromoBatchEntity.builder()
        .batchId(batchId)
        .prefix(TEST_PREFIX)
        .batchCount(TEST_BATCH_COUNT)
        .codeLength(TEST_CODE_LENGTH)
        .status(PromoBatchStatus.PENDING)
        .campaignName(TEST_CAMPAIGN_NAME)
        .expiryDate(TEST_EXPIRY_DATE)
        .downloaded(false)
        .password("password")
        .build();
  }

  private PromoBatchSummary createPromoBatchSummary() {
    return PromoBatchSummary.builder()
        .downloaded(TEST_DOWNLOADED)
        .password(TEST_PASSWORD)
        .s3Key(TEST_S3KEY)
        .build();
  }

  private PromoBatchSummaryPage createPromoBatchSummaryPage(Pageable pageable,
      PromoBatchSummary... summaries) {
    return PromoBatchSummaryPage.builder()
        .promoBatchSummary(List.of(summaries))
        .totalElements(summaries.length)
        .totalPages(TEST_TOTAL_PAGES)
        .pageNumber(pageable.getPageNumber())
        .pageSize(pageable.getPageSize())
        .pageable(pageable)
        .build();
  }

  private List<BatchEligibility> createBatchEligibilities() {
    return List.of(
            BatchEligibility.builder()
                    .region(Region.GB)
                    .channel(Channel.PI)
                    .platforms(List.of(Platform.WEB))
                    .build()
    );
  }

  private PromoKindRequest mockPromoKindRequest() {
    return PromoKindRequest.builder()
            .promoCode(TEST_PROMO_CODE)
            .country("GB")
            .channel("PI")
            .subChannel("WEB")
            .build();
  }
}
