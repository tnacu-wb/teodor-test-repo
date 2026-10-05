package uk.co.whitbread.hotel.generated.models.basket;

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
 * SendEmailDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SendEmailDto {

  private Boolean sendEmail;

  public SendEmailDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SendEmailDto(Boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  public SendEmailDto sendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
    return this;
  }

  /**
   * Get sendEmail
   * @return sendEmail
   */
  @NotNull 
  @Schema(name = "sendEmail", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sendEmail")
  public Boolean getSendEmail() {
    return sendEmail;
  }

  public void setSendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SendEmailDto sendEmailDto = (SendEmailDto) o;
    return Objects.equals(this.sendEmail, sendEmailDto.sendEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sendEmail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SendEmailDto {\n");
    sb.append("    sendEmail: ").append(toIndentedString(sendEmail)).append("\n");
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

