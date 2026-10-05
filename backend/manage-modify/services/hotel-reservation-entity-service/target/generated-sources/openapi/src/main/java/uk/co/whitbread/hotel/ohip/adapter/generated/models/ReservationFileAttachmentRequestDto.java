package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationFileAttachmentRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationFileAttachmentRequestDto {

  private @Nullable String description;

  private String fileAttachment;

  private String fileName;

  private @Nullable Boolean global;

  private String hotelId;

  private @Nullable Boolean overwriteExistingFile;

  private String reservationId;

  public ReservationFileAttachmentRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationFileAttachmentRequestDto(String fileAttachment, String fileName, String hotelId, String reservationId) {
    this.fileAttachment = fileAttachment;
    this.fileName = fileName;
    this.hotelId = hotelId;
    this.reservationId = reservationId;
  }

  public ReservationFileAttachmentRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ReservationFileAttachmentRequestDto fileAttachment(String fileAttachment) {
    this.fileAttachment = fileAttachment;
    return this;
  }

  /**
   * Get fileAttachment
   * @return fileAttachment
   */
  @NotNull 
  @Schema(name = "fileAttachment", example = "Base64 encoded string", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fileAttachment")
  public String getFileAttachment() {
    return fileAttachment;
  }

  public void setFileAttachment(String fileAttachment) {
    this.fileAttachment = fileAttachment;
  }

  public ReservationFileAttachmentRequestDto fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
   */
  @NotNull 
  @Schema(name = "fileName", example = "REG_RES1234567_ID232323_P76767676.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public ReservationFileAttachmentRequestDto global(Boolean global) {
    this.global = global;
    return this;
  }

  /**
   * Get global
   * @return global
   */
  
  @Schema(name = "global", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("global")
  public Boolean getGlobal() {
    return global;
  }

  public void setGlobal(Boolean global) {
    this.global = global;
  }

  public ReservationFileAttachmentRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "STUAIR", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationFileAttachmentRequestDto overwriteExistingFile(Boolean overwriteExistingFile) {
    this.overwriteExistingFile = overwriteExistingFile;
    return this;
  }

  /**
   * Get overwriteExistingFile
   * @return overwriteExistingFile
   */
  
  @Schema(name = "overwriteExistingFile", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("overwriteExistingFile")
  public Boolean getOverwriteExistingFile() {
    return overwriteExistingFile;
  }

  public void setOverwriteExistingFile(Boolean overwriteExistingFile) {
    this.overwriteExistingFile = overwriteExistingFile;
  }

  public ReservationFileAttachmentRequestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  @NotNull 
  @Schema(name = "reservationId", example = "1234567", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto = (ReservationFileAttachmentRequestDto) o;
    return Objects.equals(this.description, reservationFileAttachmentRequestDto.description) &&
        Objects.equals(this.fileAttachment, reservationFileAttachmentRequestDto.fileAttachment) &&
        Objects.equals(this.fileName, reservationFileAttachmentRequestDto.fileName) &&
        Objects.equals(this.global, reservationFileAttachmentRequestDto.global) &&
        Objects.equals(this.hotelId, reservationFileAttachmentRequestDto.hotelId) &&
        Objects.equals(this.overwriteExistingFile, reservationFileAttachmentRequestDto.overwriteExistingFile) &&
        Objects.equals(this.reservationId, reservationFileAttachmentRequestDto.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, fileAttachment, fileName, global, hotelId, overwriteExistingFile, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationFileAttachmentRequestDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    fileAttachment: ").append(toIndentedString(fileAttachment)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    global: ").append(toIndentedString(global)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    overwriteExistingFile: ").append(toIndentedString(overwriteExistingFile)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

