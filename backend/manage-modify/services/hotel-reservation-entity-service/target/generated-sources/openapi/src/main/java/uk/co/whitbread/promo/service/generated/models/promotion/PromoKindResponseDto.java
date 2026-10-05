package uk.co.whitbread.promo.service.generated.models.promotion;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoKind;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PromoKindResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.959010+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromoKindResponseDto {

  private @Nullable String operaPromoCode;

  private @Nullable PromoCodeStatus uniquePromoCodeStatus;

  private @Nullable PromoKind promoKind;

  public PromoKindResponseDto operaPromoCode(String operaPromoCode) {
    this.operaPromoCode = operaPromoCode;
    return this;
  }

  /**
   * Get operaPromoCode
   * @return operaPromoCode
   */
  
  @Schema(name = "operaPromoCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaPromoCode")
  public String getOperaPromoCode() {
    return operaPromoCode;
  }

  public void setOperaPromoCode(String operaPromoCode) {
    this.operaPromoCode = operaPromoCode;
  }

  public PromoKindResponseDto uniquePromoCodeStatus(PromoCodeStatus uniquePromoCodeStatus) {
    this.uniquePromoCodeStatus = uniquePromoCodeStatus;
    return this;
  }

  /**
   * Get uniquePromoCodeStatus
   * @return uniquePromoCodeStatus
   */
  @Valid 
  @Schema(name = "uniquePromoCodeStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uniquePromoCodeStatus")
  public PromoCodeStatus getUniquePromoCodeStatus() {
    return uniquePromoCodeStatus;
  }

  public void setUniquePromoCodeStatus(PromoCodeStatus uniquePromoCodeStatus) {
    this.uniquePromoCodeStatus = uniquePromoCodeStatus;
  }

  public PromoKindResponseDto promoKind(PromoKind promoKind) {
    this.promoKind = promoKind;
    return this;
  }

  /**
   * Get promoKind
   * @return promoKind
   */
  @Valid 
  @Schema(name = "promoKind", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoKind")
  public PromoKind getPromoKind() {
    return promoKind;
  }

  public void setPromoKind(PromoKind promoKind) {
    this.promoKind = promoKind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromoKindResponseDto promoKindResponseDto = (PromoKindResponseDto) o;
    return Objects.equals(this.operaPromoCode, promoKindResponseDto.operaPromoCode) &&
        Objects.equals(this.uniquePromoCodeStatus, promoKindResponseDto.uniquePromoCodeStatus) &&
        Objects.equals(this.promoKind, promoKindResponseDto.promoKind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operaPromoCode, uniquePromoCodeStatus, promoKind);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromoKindResponseDto {\n");
    sb.append("    operaPromoCode: ").append(toIndentedString(operaPromoCode)).append("\n");
    sb.append("    uniquePromoCodeStatus: ").append(toIndentedString(uniquePromoCodeStatus)).append("\n");
    sb.append("    promoKind: ").append(toIndentedString(promoKind)).append("\n");
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

