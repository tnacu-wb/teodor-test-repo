package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.payments.CardHolderDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateTokenRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("CreateTokenRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CreateTokenRequestDto {

  private CardHolderDto cardHolder;

  private String cardNumber;

  private @Nullable String countryCode;

  private String expiryMonth;

  private String expiryYear;

  private String requestId;

  public CreateTokenRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateTokenRequestDto(CardHolderDto cardHolder, String cardNumber, String expiryMonth, String expiryYear, String requestId) {
    this.cardHolder = cardHolder;
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.requestId = requestId;
  }

  public CreateTokenRequestDto cardHolder(CardHolderDto cardHolder) {
    this.cardHolder = cardHolder;
    return this;
  }

  /**
   * Get cardHolder
   * @return cardHolder
   */
  @NotNull @Valid 
  @Schema(name = "cardHolder", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolder")
  public CardHolderDto getCardHolder() {
    return cardHolder;
  }

  public void setCardHolder(CardHolderDto cardHolder) {
    this.cardHolder = cardHolder;
  }

  public CreateTokenRequestDto cardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * Information on the type of Payment to process.
   * @return cardNumber
   */
  @NotNull 
  @Schema(name = "cardNumber", description = "Information on the type of Payment to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardNumber")
  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public CreateTokenRequestDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public CreateTokenRequestDto expiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
    return this;
  }

  /**
   * Information on the type of Payment to process.
   * @return expiryMonth
   */
  @NotNull 
  @Schema(name = "expiryMonth", description = "Information on the type of Payment to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expiryMonth")
  public String getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(String expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public CreateTokenRequestDto expiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
    return this;
  }

  /**
   * Information on the type of Payment to process.
   * @return expiryYear
   */
  @NotNull 
  @Schema(name = "expiryYear", description = "Information on the type of Payment to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expiryYear")
  public String getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(String expiryYear) {
    this.expiryYear = expiryYear;
  }

  public CreateTokenRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Unique reference for transaction provided by consumer.
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", example = "a0a9f782-98ee-468c-9839-30c487c832a3", description = "Unique reference for transaction provided by consumer.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateTokenRequestDto createTokenRequest = (CreateTokenRequestDto) o;
    return Objects.equals(this.cardHolder, createTokenRequest.cardHolder) &&
        Objects.equals(this.cardNumber, createTokenRequest.cardNumber) &&
        Objects.equals(this.countryCode, createTokenRequest.countryCode) &&
        Objects.equals(this.expiryMonth, createTokenRequest.expiryMonth) &&
        Objects.equals(this.expiryYear, createTokenRequest.expiryYear) &&
        Objects.equals(this.requestId, createTokenRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolder, cardNumber, countryCode, expiryMonth, expiryYear, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateTokenRequestDto {\n");
    sb.append("    cardHolder: ").append(toIndentedString(cardHolder)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    expiryMonth: ").append(toIndentedString(expiryMonth)).append("\n");
    sb.append("    expiryYear: ").append(toIndentedString(expiryYear)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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

