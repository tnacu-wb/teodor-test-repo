package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferAgeQualifyingCodeEnum;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OfferAdditionalGuestAmountType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferAdditionalGuestAmountType {

  private @Nullable BigDecimal amountBeforeTax;

  private @Nullable BigDecimal amountAfterTax;

  private @Nullable String currencyCode;

  private @Nullable OfferAgeQualifyingCodeEnum ageQualifyingCode;

  public OfferAdditionalGuestAmountType amountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
    return this;
  }

  /**
   * The available room rate not including any associated taxes (e.g., sales tax, VAT, GST or any associated taxes).
   * @return amountBeforeTax
   */
  @Valid 
  @Schema(name = "amountBeforeTax", example = "100.5", description = "The available room rate not including any associated taxes (e.g., sales tax, VAT, GST or any associated taxes).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountBeforeTax")
  public BigDecimal getAmountBeforeTax() {
    return amountBeforeTax;
  }

  public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
    this.amountBeforeTax = amountBeforeTax;
  }

  public OfferAdditionalGuestAmountType amountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
    return this;
  }

  /**
   * The available room rate including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).
   * @return amountAfterTax
   */
  @Valid 
  @Schema(name = "amountAfterTax", example = "123.2", description = "The available room rate including all associated taxes (e.g., sales tax, VAT, GST or any associated tax).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountAfterTax")
  public BigDecimal getAmountAfterTax() {
    return amountAfterTax;
  }

  public void setAmountAfterTax(BigDecimal amountAfterTax) {
    this.amountAfterTax = amountAfterTax;
  }

  public OfferAdditionalGuestAmountType currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * The code used for the local currency of the property. ISO 4217 currency code
   * @return currencyCode
   */
  @Pattern(regexp = "[A-Z]{3}") @Size(min = 3, max = 3) 
  @Schema(name = "currencyCode", example = "USD", description = "The code used for the local currency of the property. ISO 4217 currency code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public OfferAdditionalGuestAmountType ageQualifyingCode(OfferAgeQualifyingCodeEnum ageQualifyingCode) {
    this.ageQualifyingCode = ageQualifyingCode;
    return this;
  }

  /**
   * Get ageQualifyingCode
   * @return ageQualifyingCode
   */
  @Valid 
  @Schema(name = "ageQualifyingCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ageQualifyingCode")
  public OfferAgeQualifyingCodeEnum getAgeQualifyingCode() {
    return ageQualifyingCode;
  }

  public void setAgeQualifyingCode(OfferAgeQualifyingCodeEnum ageQualifyingCode) {
    this.ageQualifyingCode = ageQualifyingCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferAdditionalGuestAmountType offerAdditionalGuestAmountType = (OfferAdditionalGuestAmountType) o;
    return Objects.equals(this.amountBeforeTax, offerAdditionalGuestAmountType.amountBeforeTax) &&
        Objects.equals(this.amountAfterTax, offerAdditionalGuestAmountType.amountAfterTax) &&
        Objects.equals(this.currencyCode, offerAdditionalGuestAmountType.currencyCode) &&
        Objects.equals(this.ageQualifyingCode, offerAdditionalGuestAmountType.ageQualifyingCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountBeforeTax, amountAfterTax, currencyCode, ageQualifyingCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferAdditionalGuestAmountType {\n");
    sb.append("    amountBeforeTax: ").append(toIndentedString(amountBeforeTax)).append("\n");
    sb.append("    amountAfterTax: ").append(toIndentedString(amountAfterTax)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    ageQualifyingCode: ").append(toIndentedString(ageQualifyingCode)).append("\n");
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

