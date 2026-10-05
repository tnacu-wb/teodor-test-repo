package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.EmailDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmailFolioInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmailFolioInfoDto {

  private @Nullable EmailDto email;

  private @Nullable Boolean emailFolio;

  private @Nullable String id;

  private @Nullable String type;

  public EmailFolioInfoDto email(EmailDto email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @Valid 
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public EmailDto getEmail() {
    return email;
  }

  public void setEmail(EmailDto email) {
    this.email = email;
  }

  public EmailFolioInfoDto emailFolio(Boolean emailFolio) {
    this.emailFolio = emailFolio;
    return this;
  }

  /**
   * Get emailFolio
   * @return emailFolio
   */
  
  @Schema(name = "emailFolio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailFolio")
  public Boolean getEmailFolio() {
    return emailFolio;
  }

  public void setEmailFolio(Boolean emailFolio) {
    this.emailFolio = emailFolio;
  }

  public EmailFolioInfoDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public EmailFolioInfoDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailFolioInfoDto emailFolioInfoDto = (EmailFolioInfoDto) o;
    return Objects.equals(this.email, emailFolioInfoDto.email) &&
        Objects.equals(this.emailFolio, emailFolioInfoDto.emailFolio) &&
        Objects.equals(this.id, emailFolioInfoDto.id) &&
        Objects.equals(this.type, emailFolioInfoDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, emailFolio, id, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailFolioInfoDto {\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    emailFolio: ").append(toIndentedString(emailFolio)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

