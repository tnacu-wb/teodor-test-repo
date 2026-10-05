package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.BusinessDto;
import uk.co.whitbread.basket.generated.models.content.LeisureDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ForgottenPasswordDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ForgottenPasswordDto {

  private @Nullable BusinessDto business;

  private @Nullable String cancel;

  private @Nullable LeisureDto leisure;

  public ForgottenPasswordDto business(BusinessDto business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  @Valid 
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public BusinessDto getBusiness() {
    return business;
  }

  public void setBusiness(BusinessDto business) {
    this.business = business;
  }

  public ForgottenPasswordDto cancel(String cancel) {
    this.cancel = cancel;
    return this;
  }

  /**
   * Get cancel
   * @return cancel
   */
  
  @Schema(name = "cancel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancel")
  public String getCancel() {
    return cancel;
  }

  public void setCancel(String cancel) {
    this.cancel = cancel;
  }

  public ForgottenPasswordDto leisure(LeisureDto leisure) {
    this.leisure = leisure;
    return this;
  }

  /**
   * Get leisure
   * @return leisure
   */
  @Valid 
  @Schema(name = "leisure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leisure")
  public LeisureDto getLeisure() {
    return leisure;
  }

  public void setLeisure(LeisureDto leisure) {
    this.leisure = leisure;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ForgottenPasswordDto forgottenPasswordDto = (ForgottenPasswordDto) o;
    return Objects.equals(this.business, forgottenPasswordDto.business) &&
        Objects.equals(this.cancel, forgottenPasswordDto.cancel) &&
        Objects.equals(this.leisure, forgottenPasswordDto.leisure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(business, cancel, leisure);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ForgottenPasswordDto {\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    cancel: ").append(toIndentedString(cancel)).append("\n");
    sb.append("    leisure: ").append(toIndentedString(leisure)).append("\n");
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

