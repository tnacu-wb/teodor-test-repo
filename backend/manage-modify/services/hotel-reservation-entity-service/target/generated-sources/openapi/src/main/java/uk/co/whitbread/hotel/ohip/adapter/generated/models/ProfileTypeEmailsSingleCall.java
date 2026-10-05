package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailInfoTypeSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ProfileTypeEmailsSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProfileTypeEmailsSingleCall {

  @Valid
  private List<@Valid EmailInfoTypeSingleCall> emailInfo = new ArrayList<>();

  public ProfileTypeEmailsSingleCall emailInfo(List<@Valid EmailInfoTypeSingleCall> emailInfo) {
    this.emailInfo = emailInfo;
    return this;
  }

  public ProfileTypeEmailsSingleCall addEmailInfoItem(EmailInfoTypeSingleCall emailInfoItem) {
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
  public List<@Valid EmailInfoTypeSingleCall> getEmailInfo() {
    return emailInfo;
  }

  public void setEmailInfo(List<@Valid EmailInfoTypeSingleCall> emailInfo) {
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
    ProfileTypeEmailsSingleCall profileTypeEmailsSingleCall = (ProfileTypeEmailsSingleCall) o;
    return Objects.equals(this.emailInfo, profileTypeEmailsSingleCall.emailInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileTypeEmailsSingleCall {\n");
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

