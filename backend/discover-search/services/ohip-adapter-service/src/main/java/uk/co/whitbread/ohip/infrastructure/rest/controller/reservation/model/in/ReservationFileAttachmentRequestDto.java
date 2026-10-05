package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReservationFileAttachmentRequestDto {

  @NotEmpty(message = "fileName is required")
  @Schema(example = "REG_RES1234567_ID232323_P76767676.pdf", requiredMode = RequiredMode.REQUIRED)
  private String fileName;
  @NotEmpty(message = "reservationId is required")
  @Schema(example = "1234567", requiredMode = RequiredMode.REQUIRED)
  private String reservationId;
  @Schema(example = "false")
  private Boolean overwriteExistingFile;
  private String description;
  @NotEmpty(message = "hotelId is required")
  @Schema(example = "STUAIR", requiredMode = RequiredMode.REQUIRED)
  private String hotelId;
  @Schema(example = "false")
  private Boolean global;
  @NotEmpty(message = "fileAttachment is required with valid Base64 encoded string")
  @Schema(example = "Base64 encoded string", requiredMode = RequiredMode.REQUIRED)
  private String fileAttachment;
}
