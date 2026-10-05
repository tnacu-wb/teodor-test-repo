package uk.co.whitbread.hotel.generated.models.reservation;

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
 * NavigationOptionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NavigationOptionsDto {

  private @Nullable Boolean amendPaymentPage;

  public NavigationOptionsDto amendPaymentPage(Boolean amendPaymentPage) {
    this.amendPaymentPage = amendPaymentPage;
    return this;
  }

  /**
   * Get amendPaymentPage
   * @return amendPaymentPage
   */
  
  @Schema(name = "amendPaymentPage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amendPaymentPage")
  public Boolean getAmendPaymentPage() {
    return amendPaymentPage;
  }

  public void setAmendPaymentPage(Boolean amendPaymentPage) {
    this.amendPaymentPage = amendPaymentPage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationOptionsDto navigationOptionsDto = (NavigationOptionsDto) o;
    return Objects.equals(this.amendPaymentPage, navigationOptionsDto.amendPaymentPage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amendPaymentPage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationOptionsDto {\n");
    sb.append("    amendPaymentPage: ").append(toIndentedString(amendPaymentPage)).append("\n");
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

