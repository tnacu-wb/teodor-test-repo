package uk.co.whitbread.basket.generated.models.marketing;

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
 * LoyaltyAccount
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LoyaltyAccount {

  private @Nullable String loyaltyBrand;

  private @Nullable String loyaltySystemId;

  public LoyaltyAccount loyaltyBrand(String loyaltyBrand) {
    this.loyaltyBrand = loyaltyBrand;
    return this;
  }

  /**
   * Get loyaltyBrand
   * @return loyaltyBrand
   */
  
  @Schema(name = "loyaltyBrand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("loyaltyBrand")
  public String getLoyaltyBrand() {
    return loyaltyBrand;
  }

  public void setLoyaltyBrand(String loyaltyBrand) {
    this.loyaltyBrand = loyaltyBrand;
  }

  public LoyaltyAccount loyaltySystemId(String loyaltySystemId) {
    this.loyaltySystemId = loyaltySystemId;
    return this;
  }

  /**
   * Get loyaltySystemId
   * @return loyaltySystemId
   */
  
  @Schema(name = "loyaltySystemId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("loyaltySystemId")
  public String getLoyaltySystemId() {
    return loyaltySystemId;
  }

  public void setLoyaltySystemId(String loyaltySystemId) {
    this.loyaltySystemId = loyaltySystemId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoyaltyAccount loyaltyAccount = (LoyaltyAccount) o;
    return Objects.equals(this.loyaltyBrand, loyaltyAccount.loyaltyBrand) &&
        Objects.equals(this.loyaltySystemId, loyaltyAccount.loyaltySystemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loyaltyBrand, loyaltySystemId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoyaltyAccount {\n");
    sb.append("    loyaltyBrand: ").append(toIndentedString(loyaltyBrand)).append("\n");
    sb.append("    loyaltySystemId: ").append(toIndentedString(loyaltySystemId)).append("\n");
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

