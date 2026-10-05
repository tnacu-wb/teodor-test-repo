package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransactionCodeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TransactionCodeDto {

  private @Nullable String pkgCode;

  private @Nullable String tranCode;

  private @Nullable Boolean vatBearing;

  public TransactionCodeDto pkgCode(String pkgCode) {
    this.pkgCode = pkgCode;
    return this;
  }

  /**
   * Get pkgCode
   * @return pkgCode
   */
  
  @Schema(name = "pkgCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pkgCode")
  public String getPkgCode() {
    return pkgCode;
  }

  public void setPkgCode(String pkgCode) {
    this.pkgCode = pkgCode;
  }

  public TransactionCodeDto tranCode(String tranCode) {
    this.tranCode = tranCode;
    return this;
  }

  /**
   * Get tranCode
   * @return tranCode
   */
  
  @Schema(name = "tranCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tranCode")
  public String getTranCode() {
    return tranCode;
  }

  public void setTranCode(String tranCode) {
    this.tranCode = tranCode;
  }

  public TransactionCodeDto vatBearing(Boolean vatBearing) {
    this.vatBearing = vatBearing;
    return this;
  }

  /**
   * Get vatBearing
   * @return vatBearing
   */
  
  @Schema(name = "vatBearing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatBearing")
  public Boolean getVatBearing() {
    return vatBearing;
  }

  public void setVatBearing(Boolean vatBearing) {
    this.vatBearing = vatBearing;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionCodeDto transactionCodeDto = (TransactionCodeDto) o;
    return Objects.equals(this.pkgCode, transactionCodeDto.pkgCode) &&
        Objects.equals(this.tranCode, transactionCodeDto.tranCode) &&
        Objects.equals(this.vatBearing, transactionCodeDto.vatBearing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pkgCode, tranCode, vatBearing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionCodeDto {\n");
    sb.append("    pkgCode: ").append(toIndentedString(pkgCode)).append("\n");
    sb.append("    tranCode: ").append(toIndentedString(tranCode)).append("\n");
    sb.append("    vatBearing: ").append(toIndentedString(vatBearing)).append("\n");
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

