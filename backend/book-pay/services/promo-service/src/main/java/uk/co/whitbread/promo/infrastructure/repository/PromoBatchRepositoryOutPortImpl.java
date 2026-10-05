package uk.co.whitbread.promo.infrastructure.repository;

import io.getunleash.UnleashContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import uk.co.whitbread.promo.domain.model.feature.FeatureFlag;
import uk.co.whitbread.promo.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;
import uk.co.whitbread.promo.domain.model.promobatch.in.BatchEligibility;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKind;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKindResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemPromoCodeResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemStatus;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchCodeGeneratorOutPort;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchRepositoryOutPort;
import uk.co.whitbread.promo.infrastructure.exception.ErrorCode;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchException;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchFileUploadException;
import uk.co.whitbread.promo.infrastructure.repository.mapper.BatchEligibilityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchEntityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchSummaryMapper;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;
import uk.co.whitbread.promo.infrastructure.rest.client.CodeGenerator;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.model.out.PromotionResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.client.properties.S3Properties;

@RequiredArgsConstructor
@Slf4j
public class PromoBatchRepositoryOutPortImpl implements PromoBatchRepositoryOutPort {

  private final PromoBatchRepository promoBatchRepository;
  private final PromoCodeRepository promoCodeRepository;
  private final PromoBatchEntityMapper promoBatchEntityMapper;
  private final PromoBatchSummaryMapper promoBatchSummaryMapper;
  private final PromoBatchStatusService promoBatchStatusService;
  private final PromoBatchCodeGeneratorOutPort codeGenerator;
  private final S3Presigner s3Presigner;
  private final S3Properties s3Properties;
  private final OhipAdapterClient ohipAdapterClient;
  private final PromoBatchS3UploaderService promoBatchS3UploaderService;
  private final Executor promoCopyExecutor;
  private final Executor s3Executor;
  private final BatchEligibilityRepository batchEligibilityRepository;
  private final BatchEligibilityMapper batchEligibilityMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  private static final String BATCH_NOT_FOUND_MSG = "Promo batch not found for id: %s";
  private static final String BATCH_RECOVERY_FAILED_MSG = "Failed to recover batch: %s";

  @Override
  public PromoBatchResponse createPromoBatch(PromoBatchRequest promoBatchRequest) {
    validateOperaPromoCode(promoBatchRequest.getOperaPromoCode(), promoBatchRequest.getHotelId());
    validateExpiryDate(promoBatchRequest.getOperaPromoCode(), promoBatchRequest.getHotelId(),
        promoBatchRequest.getExpiryDate());
    validateNoActiveBatch(promoBatchRequest);
    validateMaxRedemptionLimit(promoBatchRequest);
    PromoBatchEntity batch = PromoBatchEntity.builder()
        .campaignName(promoBatchRequest.getCampaignName())
        .batchCount(promoBatchRequest.getBatchCount())
        .codeLength(promoBatchRequest.getCodeLength())
        .status(PromoBatchStatus.PENDING)
        .expiryDate(promoBatchRequest.getExpiryDate())
        .operaPromoCode(promoBatchRequest.getOperaPromoCode())
        .notes(promoBatchRequest.getNotes())
        .requestedBy(promoBatchRequest.getRequestedBy())
        .isMultiple(promoBatchRequest.getIsMultiple())
        .maxRedemptionLimit(promoBatchRequest.getMaxRedemptionLimit())
        .build();

    UUID batchId = UUID.randomUUID();
    log.info("Promo batch created: {}", batchId);
    String normalizedPrefix =
            promoBatchRequest.getPrefix().trim().toUpperCase();
    batch.setBatchId(batchId);
    batch.setPrefix(normalizedPrefix);
    PromoBatchEntity savedBatch = promoBatchRepository.save(batch);
    if (promoBatchRequest.getBatchEligibilities() != null
            && !promoBatchRequest.getBatchEligibilities().isEmpty()) {
      saveBatchEligibilities(
              batchId,
              promoBatchRequest.getBatchEligibilities());
    }
    generateUniquePromoCodesAndS3Upload(promoBatchRequest, batchId, normalizedPrefix);

    return promoBatchEntityMapper.toModel(savedBatch);
  }

  @Override
  @Transactional(readOnly = true)
  public PromoBatchSummary getPromoBatchById(UUID batchId) {
    PromoBatchEntity batch = findPromoBatchById(batchId);
    PromoBatchSummary summary = promoBatchSummaryMapper.toModel(batch);

    List<BatchEligibilityEntity> eligibilities =
            batchEligibilityRepository.findByBatchIdIn(List.of(batchId));

    summary.setBatchEligibilities(
            batchEligibilityMapper.toSummaryModel(eligibilities));

    if (batch.getS3Key() != null) {
      String presignedUrl = getPresignedUrl(batchId);
      summary.setS3Key(presignedUrl);
    }
    applyMasking(summary, false);
    return summary;
  }

  private void validateOperaPromoCode(String operaPromoCode, String hotelId) {
    if (operaPromoCode == null || operaPromoCode.trim().isEmpty()) {
      throw new PromoBatchException(ErrorCode.INVALID_STATE_TRANSITION,
          "Opera promo code cannot be empty");
    }
    List<PromotionResponseDto> promotions =
        ohipAdapterClient.getPromotions(
            List.of(operaPromoCode),
            hotelId
        );
    if (ObjectUtils.isEmpty(promotions)) {
      throw new PromoBatchException(
          ErrorCode.INVALID_OPERA_PROMO_CODE,
          "This promotion is not available for the selected hotel " + hotelId
      );
    }

    boolean existsInResponse = promotions.stream()
        .map(PromotionResponseDto::getPromotionCode)
        .filter(Objects::nonNull)
        .anyMatch(p -> p.equals(operaPromoCode));

    if (!existsInResponse) {
      throw new PromoBatchException(
          ErrorCode.INVALID_OPERA_PROMO_CODE,
          "Enter valid promo code " + operaPromoCode
      );
    }
  }

  private void validateExpiryDate(String operaPromoCode, String hotelId, LocalDate expiryDate) {
    List<PromotionResponseDto> promotions =
        ohipAdapterClient.getPromotions(
            List.of(operaPromoCode),
            hotelId
        );

    boolean isInvalidExpiry = promotions.stream()
        .filter(p -> p.getStayStartDate() != null && p.getStayEndDate() != null)
        .noneMatch(p ->
            !expiryDate.isBefore(p.getStayStartDate()) && !expiryDate.isAfter(p.getStayEndDate())
        );

    if (isInvalidExpiry) {
      throw new PromoBatchException(
          ErrorCode.INVALID_EXPIRY_DATE,
          "Enter a valid Expiry date "
      );
    }
  }


  @Override
  public PromoBatchSummaryPage getPromoBatchSummary(Pageable pageable) {
    var summaryPage = promoBatchStatusService.getPromoBatchSummary(pageable);
    summaryPage.getPromoBatchSummary()
        .forEach(s -> applyMasking(s, true));
    return summaryPage;
  }

  @Override
  public void recoverInterruptedBatches() {
    log.info("Starting Batch Recovery");
    List<PromoBatchEntity> interruptedBatches = promoBatchRepository.findByStatusIn(
        List.of(PromoBatchStatus.RUNNING, PromoBatchStatus.PENDING));

    if (interruptedBatches.isEmpty()) {
      log.info("No interrupted batches found");
      return;
    }

    log.warn("Found {} interrupted batches, attempting recovery", interruptedBatches.size());
    for (PromoBatchEntity batch : interruptedBatches) {
      recoverPromoBatch(batch);
    }
    log.info("Batch Recovery Complete");
  }

  @Override
  @Transactional
  public void markAsDownloaded(UUID batchId) {
    int updated = promoBatchRepository.markAsDownloaded(batchId);
    if (updated == 0) {
      if (!promoBatchRepository.existsById(batchId)) {
        throw new PromoBatchException(ErrorCode.DIGITAL_PROMO_BATCH_NOT_FOUND,
            String.format(BATCH_NOT_FOUND_MSG, batchId));
      }
      throw new PromoBatchException(ErrorCode.DIGITAL_PROMO_BATCH_UPDATE_FAILED,
          String.format("Batch exists but update failed for id: %s", batchId));
    }
  }

  public String getPresignedUrl(UUID batchId) {
    PromoBatchEntity batch = promoBatchRepository.findById(batchId)
        .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

    GetObjectPresignRequest presignRequest =
            GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(r -> r
                            .bucket(s3Properties.getPromoBucketName())
                            .key(batch.getS3Key()))
                    .build();

    return s3Presigner
            .presignGetObject(presignRequest)
            .url()
            .toExternalForm();
  }

  @Override
  public PromoKindResponse validatePromoKind(PromoKindRequest request) {

    String promoCode = request.getPromoCode().trim().toUpperCase();
    validatePromoCode(promoCode);

    List<PromoBatchEntity> promoBatchList = findAllByOperaPromoCode(promoCode);
    if (!promoBatchList.isEmpty()) {
      return genericPromoResponse(promoCode);
    }

    PromoKindResponse response = validateIsMultiplePromo(promoCode, request);

    if (response != null) {
      return response;
    }
    Optional<PromoCodeEntity> promoCodeOpt = findUniquePromo(promoCode);
    if (promoCodeOpt.isPresent()) {
      return uniquePromoResponse(promoCodeOpt.get(), request);
    }

    return PromoKindResponse.builder()
            .promoKind(PromoKind.GENERIC)
            .operaPromoCode(promoCode)
            .build();
  }

  @Override
  @Transactional
  public RedeemPromoCodeResponse redeemPromoCode(RedeemPromoCodeRequest request) {


    PromoBatchEntity batch = findIsMultipleBatch(request.getPromoCode());
    if (batch != null) {
      redeemIsMultiplePromo(batch, request);
      return buildRedeemResponse(
              request.getPromoCode(),
              RedeemStatus.REDEEMED,
              true,
              "Promo code redeemed successfully");

    }

    if (isRedeemedSuccessfully(request)) {
      return buildRedeemResponse(
          request.getPromoCode(),
          RedeemStatus.REDEEMED,
          true,
          "Promo code redeemed successfully"
      );
    }

    return findUniquePromo(request.getPromoCode())
        .map(this::buildResponseForExistingPromo)
        .orElseGet(() -> invalidPromoResponse(request.getPromoCode()));
  }

  private boolean isRedeemedSuccessfully(RedeemPromoCodeRequest request) {
    return promoCodeRepository.redeemPromoCode(
        request.getPromoCode(),
        request.getBookingReference(),
        OffsetDateTime.now()
    ) == 1;
  }

  private RedeemPromoCodeResponse buildResponseForExistingPromo(PromoCodeEntity promoCodeEntity) {
    return switch (promoCodeEntity.getStatus()) {
      case REDEEMED -> buildRedeemResponse(
          promoCodeEntity.getCode(),
          RedeemStatus.ALREADY_REDEEMED,
          true,
          "Promo code already redeemed"
      );
      case EXPIRED -> buildRedeemResponse(
          promoCodeEntity.getCode(),
          RedeemStatus.EXPIRED,
          true,
          "Promo code has expired"
      );
      default -> buildRedeemResponse(
          promoCodeEntity.getCode(),
          RedeemStatus.INVALID_PROMO_CODE,
          false,
          "Promo code is invalid or already used"
      );
    };
  }

  private RedeemPromoCodeResponse invalidPromoResponse(String promoCode) {
    return buildRedeemResponse(promoCode, RedeemStatus.INVALID_PROMO_CODE,
        false, "Promo code is invalid"
    );
  }

  private RedeemPromoCodeResponse buildRedeemResponse(String promoCode,
      RedeemStatus status, boolean success, String message) {

    return RedeemPromoCodeResponse.builder()
        .promoCode(promoCode)
        .status(status)
        .success(success)
        .message(message)
        .build();
  }

  private void validatePromoCode(String promoCode) {
    if (promoCode == null || promoCode.isBlank()) {
      throw new PromoBatchException(
          ErrorCode.DIGITAL_INVALID_REQUEST,
          "Promo code cannot be empty"
      );
    }
  }

  private List<PromoBatchEntity> findAllByOperaPromoCode(String promoCode) {
    return promoBatchRepository.findAllByOperaPromoCode(promoCode);
  }

  private Optional<PromoCodeEntity> findUniquePromo(String promoCode) {
    return promoCodeRepository.findById(promoCode);
  }

  private PromoKindResponse genericPromoResponse(String promoCode) {
    return PromoKindResponse.builder()
        .promoKind(PromoKind.GENERIC)
        .operaPromoCode(promoCode)
        .build();
  }

  private PromoKindResponse uniquePromoResponse(PromoCodeEntity promoCodeEntity,
                                                PromoKindRequest request) {
    PromoBatchEntity promoBatchObj =
            findPromoBatchById(promoCodeEntity.getBatchId());

    if (unleashWrapper.isEnabled(
            unleashWrapper.featureFlag().getBatchEligibilityCheck(),
            UnleashContext.builder().build())
            && request.getCountry() != null
            && request.getChannel() != null
            && request.getSubChannel() != null
    ) {
      boolean eligible =
              batchEligibilityRepository
                      .existsByBatchIdAndRegionAndChannelAndPlatform(
                              promoBatchObj.getBatchId(),
                              Region.valueOf(request.getCountry().toUpperCase()),
                              Channel.valueOf(request.getChannel().toUpperCase()),
                              Platform.valueOf(request.getSubChannel().toUpperCase()));

      if (!eligible) {
        return PromoKindResponse.builder()
                .promoKind(PromoKind.UNIQUE)
                .operaPromoCode(promoBatchObj.getOperaPromoCode())
                .build();
      }
    }

    if (promoBatchObj.getStatus() == PromoBatchStatus.EXPIRED) {
      return PromoKindResponse.builder()
              .promoKind(PromoKind.UNIQUE)
              .operaPromoCode(promoBatchObj.getOperaPromoCode())
              .uniquePromoCodeStatus(PromoCodeStatus.EXPIRED)
              .build();
    }

    return PromoKindResponse.builder()
            .promoKind(PromoKind.UNIQUE)
            .operaPromoCode(promoBatchObj.getOperaPromoCode())
            .uniquePromoCodeStatus(
                    PromoCodeStatus.valueOf(promoCodeEntity.getStatus().name()))
            .build();
  }

  public void generateUniquePromoCodesAndS3Upload(PromoBatchRequest promoBatchRequest, UUID batchId, String prefix) {

    try {

      CompletableFuture.runAsync(() -> {

        boolean flipped = promoBatchStatusService.setStatusIfPending(batchId, PromoBatchStatus.RUNNING);

        if (!flipped) {
          log.warn("Batch was not in PENDING state when worker started, continuing: {}", batchId);
        }


        if (!Boolean.TRUE.equals(promoBatchRequest.getIsMultiple())) {
          log.info("Starting async promo code generation for batch: {}", batchId);

          codeGenerator.generateCodes(
              batchId,
                          promoBatchRequest.getBatchCount(),
                          prefix,
                          promoBatchRequest.getCodeLength());
        } else {
          log.info("Skipping unique promo code generation for generic promo batch: {}", batchId);
        }

      }, promoCopyExecutor)
              .thenRunAsync(() -> {
                log.info("Starting S3 upload for batch: {}", batchId);

                try {

                  String password = CodeGenerator.generateCode(null, 6);

                  PromoBatchEntity batch = promoBatchRepository.findById(batchId)
                                  .orElseThrow(() ->
                                          new IllegalStateException("Promo batch not found for batchId=" + batchId));

                  String s3Key;

                  if (Boolean.TRUE.equals(promoBatchRequest.getIsMultiple())) {
                    s3Key = promoBatchS3UploaderService.createAndUploadGenericPromoFile(
                            batchId,
                            password,
                            batch);
                  } else {
                    s3Key = promoBatchS3UploaderService.createAndUploadFile(
                            batchId,
                            password,
                            batch);
                  }

                  batch.setS3Key(s3Key);
                  batch.setPassword(password);

                  promoBatchRepository.save(batch);

                  safeMarkCompleted(batchId);

                  log.info("Promo batch {} completed successfully", batchId);

                } catch (Exception ex) {
                  log.error("S3 upload failed for batch {}", batchId, ex);
                  if (Boolean.FALSE.equals(promoBatchRequest.getIsMultiple())) {
                    promoCodeRepository.deleteByBatchId(batchId);
                  }
                  throw new PromoBatchFileUploadException(
                          ErrorCode.FILE_UPLOAD_FAILED,
                          "S3 upload failed"
                  );
                }

              }, s3Executor)

              .exceptionally(ex -> {
                safeMarkFailed(batchId);
                return null;
              });

    } catch (RejectedExecutionException exception) {
      safeMarkFailed(batchId);
      throw new PromoBatchException(
              ErrorCode.EXECUTOR_SATURATED,
              String.format("Executor saturated, rejecting batch: %s", batchId)
      );
    }
  }

  private PromoBatchEntity findPromoBatchById(UUID batchId) {
    return promoBatchRepository.findById(batchId).orElseThrow(
        () -> new PromoBatchException(ErrorCode.DIGITAL_PROMO_BATCH_NOT_FOUND,
            String.format(BATCH_NOT_FOUND_MSG, batchId)));
  }

  private void safeMarkCompleted(UUID batchId) {
    try {
      promoBatchStatusService.markCompleted(batchId);
    } catch (DataAccessException
             | org.springframework.transaction.CannotCreateTransactionException ex) {
      log.error("Failed to mark batch as COMPLETED due to DB pressure: {}", batchId, ex);
    }
  }

  private void safeMarkFailed(UUID batchId) {
    try {
      promoBatchStatusService.markFailed(batchId);
    } catch (DataAccessException
             | org.springframework.transaction.CannotCreateTransactionException ex) {
      log.error("Failed to mark batch as FAILED due to DB pressure: {}", batchId, ex);
    }
  }

  private void recoverPromoBatch(PromoBatchEntity batch) {
    UUID recoveryBatchId = batch.getBatchId();
    try {
      long alreadyInserted = promoCodeRepository.countByBatchId(recoveryBatchId);
      long remaining = batch.getBatchCount() - alreadyInserted;

      if (remaining <= 0) {
        log.info("Batch already completed, marking as such: {}", recoveryBatchId);
        safeMarkCompleted(recoveryBatchId);
        return;
      }

      log.info("Batch interrupted - already inserted: {}, remaining: {}", alreadyInserted,
          remaining);

      codeGenerator.generateCodes(recoveryBatchId, (int) remaining, batch.getPrefix(),
          batch.getCodeLength());

      log.info("Batch recovered successfully: {}", recoveryBatchId);
      safeMarkCompleted(recoveryBatchId);

    } catch (Exception e) {
      log.error(BATCH_RECOVERY_FAILED_MSG + "{}", recoveryBatchId, e);
      safeMarkFailed(recoveryBatchId);
      throw new PromoBatchException(ErrorCode.DIGITAL_PROMO_BATCH_RECOVERY_FAILED,
          String.format(BATCH_RECOVERY_FAILED_MSG, recoveryBatchId));
    }
  }

  public void applyMasking(PromoBatchSummary summary, boolean isSummaryView) {
    if (Boolean.TRUE.equals(summary.getDownloaded())) {
      summary.setPassword(null);
    }
    if (isSummaryView) {
      summary.setS3Key(null);
    }
  }

  @Override
  @Transactional
  public void updatePromoBatchStatusToExpiry() {
    LocalDate currentDate = LocalDate.now();
    int updatedCount = promoBatchRepository.expireOldBatches(currentDate);
    log.info("Expired {} promo batches", updatedCount);
  }

  @Override
  @Transactional
  public void deleteExpiredPromoCodesAfterRetention() {
    int deletedCount = promoCodeRepository.deleteExpiredPromoCodesAfterRetention();
    log.info("Deleted {} expired promo codes after retention", deletedCount);
  }

  private void saveBatchEligibilities(
          UUID batchId,
          List<BatchEligibility> batchEligibilities) {

    List<BatchEligibilityEntity> entities = new ArrayList<>();

    for (BatchEligibility eligibility : batchEligibilities) {

      for (Platform platform : eligibility.getPlatforms()) {

        entities.add(
                BatchEligibilityEntity.builder()
                                        .batchId(batchId)
                                        .region(eligibility.getRegion())
                                        .channel(eligibility.getChannel())
                                        .platform(platform)
                                        .build());
      }
    }

    batchEligibilityRepository.saveAll(entities);
  }

  private boolean isBatchEligible(
          UUID batchId,
          PromoKindRequest request) {

    return batchEligibilityRepository.existsByBatchIdAndRegionAndChannelAndPlatform(
            batchId,
            Region.valueOf(request.getCountry().toUpperCase()),
            Channel.valueOf(request.getChannel().toUpperCase()),
            Platform.valueOf(request.getSubChannel().toUpperCase()));
  }

  private PromoKindResponse validateIsMultiplePromo(
          String promoCode,
          PromoKindRequest request) {

    List<PromoBatchEntity> batches =
            batchEligibilityRepository.findByMatchingPrefix(promoCode);

    List<PromoBatchEntity> isMultipleBatches = batches.stream()
            .filter(batch -> Boolean.TRUE.equals(batch.getIsMultiple()))
            .toList();

    if (isMultipleBatches.isEmpty()) {
      return null;
    }

    // Prefer a COMPLETED batch
    PromoBatchEntity batch = isMultipleBatches.stream()
            .filter(b -> b.getStatus() == PromoBatchStatus.COMPLETED)
            .findFirst()
            .orElse(null);

    // If no COMPLETED batch exists, treat as expired
    if (batch == null) {
      PromoBatchEntity expiredBatch = isMultipleBatches.getFirst();

      return PromoKindResponse.builder()
              .promoKind(PromoKind.UNIQUE)
              .operaPromoCode(expiredBatch.getOperaPromoCode())
              .uniquePromoCodeStatus(PromoCodeStatus.EXPIRED)
              .build();
    }

    // Max redemption exhausted
    if (batch.getMaxRedemptionLimit() != null
            && batch.getMaxRedemptionLimit() == 0) {

      return PromoKindResponse.builder()
              .promoKind(PromoKind.UNIQUE)
              .operaPromoCode(batch.getOperaPromoCode())
              .build();
    }

    // Region / Channel / Platform eligibility
    if (request.getCountry() != null
            && request.getChannel() != null
            && request.getSubChannel() != null
            && !isBatchEligible(batch.getBatchId(), request)) {

      return PromoKindResponse.builder()
              .promoKind(PromoKind.UNIQUE)
              .operaPromoCode(batch.getOperaPromoCode())
              .build();
    }

    // Valid promo
    return PromoKindResponse.builder()
            .promoKind(PromoKind.UNIQUE)
            .operaPromoCode(batch.getOperaPromoCode())
            .uniquePromoCodeStatus(PromoCodeStatus.ISSUED)
            .build();
  }

  private String generateUniquePromoCode(String promoCode) {

    String generatedCode;

    do {
      generatedCode = promoCode + CodeGenerator.generateCode(null, 6);
    } while (promoCodeRepository.existsById(generatedCode));

    return generatedCode;
  }

  private void redeemIsMultiplePromo(
          PromoBatchEntity batch,
          RedeemPromoCodeRequest request) {

    String generatedCode = generateUniquePromoCode(request.getPromoCode());

    PromoCodeEntity entity = PromoCodeEntity.builder()
            .code(generatedCode)
            .batchId(batch.getBatchId())
            .status(uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus.REDEEMED)
            .bookingReference(request.getBookingReference())
            .redeemedAt(OffsetDateTime.now(ZoneOffset.UTC))
            .build();

    promoCodeRepository.save(entity);

    promoBatchRepository.decrementMaxRedemptionLimit(batch.getBatchId());
  }

  private PromoBatchEntity findIsMultipleBatch(String promoCode) {

    List<PromoBatchEntity> batches =
            batchEligibilityRepository.findByMatchingPrefix(promoCode);

    return batches.stream()
            .filter(batch -> Boolean.TRUE.equals(batch.getIsMultiple()))
            .filter(batch -> batch.getStatus() == PromoBatchStatus.COMPLETED)
            .findFirst()
            .orElse(null);
  }

  private void validateNoActiveBatch(PromoBatchRequest request) {

    if (!Boolean.TRUE.equals(request.getIsMultiple())) {
      return;
    }

    String normalizedPrefix = request.getPrefix()
            .trim()
            .toUpperCase();
    List<PromoBatchStatus> statuses = List.of(
            PromoBatchStatus.PENDING,
            PromoBatchStatus.RUNNING,
            PromoBatchStatus.COMPLETED);


    boolean exists = promoBatchRepository.existsActiveIsMultipleBatch(
            normalizedPrefix, statuses);

    log.info("existsActiveIsMultipleBatch = {}", exists);

    if (exists) {
      throw new PromoBatchException(
              ErrorCode.DIGITAL_INVALID_REQUEST,
              "An active isMultiple promo batch already exists for this promo code.");
    }
  }

  private void validateMaxRedemptionLimit(PromoBatchRequest request) {
    if (Boolean.TRUE.equals(request.getIsMultiple())
            && Integer.valueOf(0).equals(request.getMaxRedemptionLimit())) {

      throw new PromoBatchException(
              ErrorCode.DIGITAL_INVALID_REQUEST,
              "Max redemption limit must be greater than zero");
    }
  }
}
