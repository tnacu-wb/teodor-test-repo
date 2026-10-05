package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.RateClassificationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateInformationDto {

  @Valid
  private List<@Valid RateClassificationDto> rateClassifications = new ArrayList<>();

  public RateInformationDto rateClassifications(List<@Valid RateClassificationDto> rateClassifications) {
    this.rateClassifications = rateClassifications;
    return this;
  }

  public RateInformationDto addRateClassificationsItem(RateClassificationDto rateClassificationsItem) {
    if (this.rateClassifications == null) {
      this.rateClassifications = new ArrayList<>();
    }
    this.rateClassifications.add(rateClassificationsItem);
    return this;
  }

  /**
   * Get rateClassifications
   * @return rateClassifications
   */
  @Valid 
  @Schema(name = "rateClassifications", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateClassifications")
  public List<@Valid RateClassificationDto> getRateClassifications() {
    return rateClassifications;
  }

  public void setRateClassifications(List<@Valid RateClassificationDto> rateClassifications) {
    this.rateClassifications = rateClassifications;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateInformationDto rateInformationDto = (RateInformationDto) o;
    return Objects.equals(this.rateClassifications, rateInformationDto.rateClassifications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rateClassifications);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateInformationDto {\n");
    sb.append("    rateClassifications: ").append(toIndentedString(rateClassifications)).append("\n");
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

