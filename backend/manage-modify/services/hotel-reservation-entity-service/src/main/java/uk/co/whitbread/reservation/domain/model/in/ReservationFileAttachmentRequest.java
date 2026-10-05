package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationFileAttachmentRequest {

  @NotEmpty
  private String fileName;
  @NotEmpty
  private String reservationId;
  private Boolean overwriteExistingFile;
  private String description;
  @NotEmpty
  private String hotelId;
  private Boolean global;
  @NotEmpty
  private String fileAttachment;
}
