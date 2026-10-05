package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInTelephoneInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CheckInTelephonesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInTelephonesDto {

  @Valid
  private List<@Valid CheckInTelephoneInfoDto> telephoneInfo = new ArrayList<>();

  public CheckInTelephonesDto telephoneInfo(List<@Valid CheckInTelephoneInfoDto> telephoneInfo) {
    this.telephoneInfo = telephoneInfo;
    return this;
  }

  public CheckInTelephonesDto addTelephoneInfoItem(CheckInTelephoneInfoDto telephoneInfoItem) {
    if (this.telephoneInfo == null) {
      this.telephoneInfo = new ArrayList<>();
    }
    this.telephoneInfo.add(telephoneInfoItem);
    return this;
  }

  /**
   * Get telephoneInfo
   * @return telephoneInfo
   */
  @Valid 
  @Schema(name = "telephoneInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephoneInfo")
  public List<@Valid CheckInTelephoneInfoDto> getTelephoneInfo() {
    return telephoneInfo;
  }

  public void setTelephoneInfo(List<@Valid CheckInTelephoneInfoDto> telephoneInfo) {
    this.telephoneInfo = telephoneInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInTelephonesDto checkInTelephonesDto = (CheckInTelephonesDto) o;
    return Objects.equals(this.telephoneInfo, checkInTelephonesDto.telephoneInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(telephoneInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInTelephonesDto {\n");
    sb.append("    telephoneInfo: ").append(toIndentedString(telephoneInfo)).append("\n");
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

