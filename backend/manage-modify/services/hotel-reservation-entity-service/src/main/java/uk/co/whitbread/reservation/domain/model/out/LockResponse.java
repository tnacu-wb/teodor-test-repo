package uk.co.whitbread.reservation.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.reservation.domain.model.in.OperationType;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class LockResponse implements SelfValidation<LockResponse> {

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String roomType;

  @NotNull
  private OperationType operationType;

  @NotNull
  private Boolean isLocked;

  @NotNull
  private String lockExpiration;

  public LockResponse(String hotelId,
                      String roomType, OperationType operationType,
                      Boolean isLocked, String lockExpiration) {
    this.hotelId = hotelId;
    this.roomType = roomType;
    this.operationType = operationType;
    this.isLocked = isLocked;
    this.lockExpiration = lockExpiration;
    this.validateSelf();
  }
}
