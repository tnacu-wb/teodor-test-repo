package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.TransactionCodeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * VatRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.460652+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class VatRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  @Valid
  private List<@Valid TransactionCodeDto> tranCodes = new ArrayList<>();

  private @Nullable String vatRegion;

  public VatRuleResponseDto generatedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
    return this;
  }

  /**
   * Get generatedAt
   * @return generatedAt
   */
  @Valid 
  @Schema(name = "generatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generatedAt")
  public Date getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
  }

  public VatRuleResponseDto tranCodes(List<@Valid TransactionCodeDto> tranCodes) {
    this.tranCodes = tranCodes;
    return this;
  }

  public VatRuleResponseDto addTranCodesItem(TransactionCodeDto tranCodesItem) {
    if (this.tranCodes == null) {
      this.tranCodes = new ArrayList<>();
    }
    this.tranCodes.add(tranCodesItem);
    return this;
  }

  /**
   * Get tranCodes
   * @return tranCodes
   */
  @Valid 
  @Schema(name = "tranCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tranCodes")
  public List<@Valid TransactionCodeDto> getTranCodes() {
    return tranCodes;
  }

  public void setTranCodes(List<@Valid TransactionCodeDto> tranCodes) {
    this.tranCodes = tranCodes;
  }

  public VatRuleResponseDto vatRegion(String vatRegion) {
    this.vatRegion = vatRegion;
    return this;
  }

  /**
   * Get vatRegion
   * @return vatRegion
   */
  
  @Schema(name = "vatRegion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatRegion")
  public String getVatRegion() {
    return vatRegion;
  }

  public void setVatRegion(String vatRegion) {
    this.vatRegion = vatRegion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VatRuleResponseDto vatRuleResponseDto = (VatRuleResponseDto) o;
    return Objects.equals(this.generatedAt, vatRuleResponseDto.generatedAt) &&
        Objects.equals(this.tranCodes, vatRuleResponseDto.tranCodes) &&
        Objects.equals(this.vatRegion, vatRuleResponseDto.vatRegion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, tranCodes, vatRegion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VatRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    tranCodes: ").append(toIndentedString(tranCodes)).append("\n");
    sb.append("    vatRegion: ").append(toIndentedString(vatRegion)).append("\n");
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

