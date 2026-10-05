package uk.co.whitbread.promo.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  DIGITAL_PROMO_BATCH_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 1000),
  DIGITAL_PROMO_BATCH_NOT_FOUND(Constants.PROMO_BATCH_NOT_FOUND, 1001),
  DIGITAL_PROMO_BATCH_UPDATE_FAILED(Constants.PROMO_BATCH_UPDATE_FAILED, 1002),
  DIGITAL_PROMO_BATCH_RECOVERY_FAILED(Constants.PROMO_BATCH_RECOVERY_FAILED, 1003),
  DIGITAL_INVALID_REQUEST(Constants.INVALID_REQUEST, 1011),
  DIGITAL_PROMO_CODE_ALREADY_REDEEMED(Constants.PROMO_CODE_ALREADY_REDEEMED, 1012),

  BATCH_SIZE_EXCEEDED(Constants.BATCH_SIZE_EXCEEDED, 1004),
  CODE_GENERATION_FAILED(Constants.CODE_GENERATION_FAILED, 1005),
  DATABASE_ERROR(Constants.DATABASE_ERROR, 1006),
  LOCK_ACQUISITION_FAILED(Constants.LOCK_ACQUISITION_FAILED, 1007),
  EXECUTOR_SATURATED(Constants.EXECUTOR_SATURATED, 1008),
  INVALID_STATE_TRANSITION(Constants.INVALID_STATE_TRANSITION, 1009),
  INVALID_OPERA_PROMO_CODE(Constants.INVALID_OPERA_PROMO_CODE, 1011),
  INVALID_EXPIRY_DATE(Constants.INVALID_EXPIRY_DATE, 1012),
  FILE_UPLOAD_FAILED(Constants.FILE_UPLOAD_FAILED, 1013);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    public static final String PROMO_BATCH_NOT_FOUND = "promo.batch.not.found";
    public static final String PROMO_BATCH_UPDATE_FAILED = "promo.batch.update.failed";
    public static final String PROMO_BATCH_RECOVERY_FAILED = "promo.batch.recovery.failed";
    public static final String BATCH_SIZE_EXCEEDED = "promo.batch.size.exceeded";
    public static final String CODE_GENERATION_FAILED = "promo.code.generation.failed";
    public static final String DATABASE_ERROR = "database.error";
    public static final String LOCK_ACQUISITION_FAILED = "lock.acquisition.failed";
    public static final String EXECUTOR_SATURATED = "executor.saturated";
    public static final String INVALID_STATE_TRANSITION = "invalid.state.transition";
    public static final String INVALID_REQUEST = "invalid.request";
    public static final String PROMO_CODE_ALREADY_REDEEMED = "promo.code.already.redeemed";
    public static final String FILE_UPLOAD_FAILED = "batchFailed";
    public static final String INVALID_OPERA_PROMO_CODE = "invalidCode";
    public static final String INVALID_EXPIRY_DATE = "endDate.invalid";

    private Constants() {
    }
  }
}
