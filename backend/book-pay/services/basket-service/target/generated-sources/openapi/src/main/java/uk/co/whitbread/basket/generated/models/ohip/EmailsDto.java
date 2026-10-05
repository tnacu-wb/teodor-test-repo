package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.EmailInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmailsDto {

  @Valid
  private List<@Valid EmailInfoDto> emailInfo = new ArrayList<>();

  public EmailsDto emailInfo(List<@Valid EmailInfoDto> emailInfo) {
    this.emailInfo = emailInfo;
    return this;
  }

  public EmailsDto addEmailInfoItem(EmailInfoDto emailInfoItem) {
    if (this.emailInfo == null) {
      this.emailInfo = new ArrayList<>();
    }
    this.emailInfo.add(emailInfoItem);
    return this;
  }

  /**
   * Get emailInfo
   * @return emailInfo
   */
  @Valid 
  @Schema(name = "emailInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailInfo")
  public List<@Valid EmailInfoDto> getEmailInfo() {
    return emailInfo;
  }

  public void setEmailInfo(List<@Valid EmailInfoDto> emailInfo) {
    this.emailInfo = emailInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailsDto emailsDto = (EmailsDto) o;
    return Objects.equals(this.emailInfo, emailsDto.emailInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailsDto {\n");
    sb.append("    emailInfo: ").append(toIndentedString(emailInfo)).append("\n");
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

