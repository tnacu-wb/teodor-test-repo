package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReservationFileAttachmentRequestDto {

  @NotNull
  @Schema(example = "REG_RES1234567_ID232323_P76767676.pdf", requiredMode = RequiredMode.REQUIRED)
  private String fileName;
  @NotNull
  @Schema(example = "1234567", requiredMode = RequiredMode.REQUIRED)
  private String reservationId;
  @Schema(example = "false")
  private Boolean overwriteExistingFile;
  private String description;
  @NotNull
  @Schema(example = "STUAIR", requiredMode = RequiredMode.REQUIRED)
  private String hotelId;
  @Schema(example = "false")
  private Boolean global;
  @NotNull
  @Schema(example = "Base64 encoded string", requiredMode = RequiredMode.REQUIRED)
  private String fileAttachment;

}
