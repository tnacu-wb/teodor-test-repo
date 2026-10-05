package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class LockRequest implements SelfValidation<LockRequest> {

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String roomType;

  @NotNull
  private OperationType operationType;

  @NotEmpty
  private String arrivalDate;

  @NotEmpty
  private String departureDate;

  public LockRequest(String hotelId, String roomType,
                     OperationType operationType,
                     String arrivalDate,
                     String departureDate) {
    this.hotelId = hotelId;
    this.roomType = roomType;
    this.operationType = operationType;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.validateSelf();
  }
}
