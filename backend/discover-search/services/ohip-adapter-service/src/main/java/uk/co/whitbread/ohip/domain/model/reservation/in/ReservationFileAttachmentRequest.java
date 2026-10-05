package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationFileAttachmentRequest {

  @NotNull
  private String fileName;
  @NotNull
  private String reservationId;
  private Boolean overwriteExistingFile;
  private String description;
  @NotNull
  private String hotelId;
  private Boolean global;
  @NotNull
  private String fileAttachment;
}
