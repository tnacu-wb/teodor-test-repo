package uk.co.whitbread.basket.domain.model.basket.out;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CleanUpTime {
  private long openValue;
  private long processingValue;
  private long amendingValue;
  private long amendedValue;
  private long payPendingValue;
  private long completedValue;
  private long cancelledValue;
  private long amendFailedValue;
  private long failedValue;
  private long preCheckedInValue;
  private long preCheckedOutValue;
  private long ciolRcFailedValue;

  public Long getCleanUpTime(Basket basket) {
    return getCleanUpTime(basket.getStatus(), Instant.now());
  }

  public Long getCleanUpTime(BasketStatus basketStatus, Instant baseTime) {
    return switch (basketStatus) {
      case OPEN -> baseTime.plusSeconds(openValue).getEpochSecond();
      case PROCESSING -> baseTime.plusSeconds(processingValue).getEpochSecond();
      case AMENDING -> baseTime.plusSeconds(amendingValue).getEpochSecond();
      case AMENDED -> baseTime.plusSeconds(amendedValue).getEpochSecond();
      case PAY_PENDING -> baseTime.plusSeconds(payPendingValue).getEpochSecond();
      case COMPLETED -> baseTime.plusSeconds(completedValue).getEpochSecond();
      case CANCELLED -> baseTime.plusSeconds(cancelledValue).getEpochSecond();
      case AMEND_FAILED -> baseTime.plusSeconds(amendFailedValue).getEpochSecond();
      case FAILED, CIOL_FAILED, SECURE_FAILED -> baseTime.plusSeconds(failedValue).getEpochSecond();
      case PRE_CHECKED_IN -> baseTime.plusSeconds(preCheckedInValue).getEpochSecond();
      case PRE_CHECKED_OUT -> baseTime.plusSeconds(preCheckedOutValue).getEpochSecond();
      case CIOL_RC_FAILED -> baseTime.plusSeconds(ciolRcFailedValue).getEpochSecond();
    };
  }

}
