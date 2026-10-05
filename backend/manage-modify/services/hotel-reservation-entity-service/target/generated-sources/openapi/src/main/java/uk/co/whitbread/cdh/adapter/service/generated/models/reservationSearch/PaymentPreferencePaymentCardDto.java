package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BillingAddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentPreferencePaymentCardDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentPreferencePaymentCardDto {

  private @Nullable BillingAddressDto billingAddress;

  private @Nullable String cardHolderName;

  private @Nullable String cardNumber;

  private @Nullable String cardType;

  private @Nullable String cnpBusinessAccountPassword;

  private @Nullable String cnpBusinessAccountUsername;

  private @Nullable Boolean cnpRequired;

  private @Nullable String expiryDate;

  private @Nullable String token;

  public PaymentPreferencePaymentCardDto billingAddress(BillingAddressDto billingAddress) {
    this.billingAddress = billingAddress;
    return this;
  }

  /**
   * Get billingAddress
   * @return billingAddress
   */
  @Valid 
  @Schema(name = "billingAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billingAddress")
  public BillingAddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(BillingAddressDto billingAddress) {
    this.billingAddress = billingAddress;
  }

  public PaymentPreferencePaymentCardDto cardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
    return this;
  }

  /**
   * Get cardHolderName
   * @return cardHolderName
   */
  
  @Schema(name = "cardHolderName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardHolderName")
  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public PaymentPreferencePaymentCardDto cardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * Get cardNumber
   * @return cardNumber
   */
  
  @Schema(name = "cardNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumber")
  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public PaymentPreferencePaymentCardDto cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Get cardType
   * @return cardType
   */
  
  @Schema(name = "cardType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public PaymentPreferencePaymentCardDto cnpBusinessAccountPassword(String cnpBusinessAccountPassword) {
    this.cnpBusinessAccountPassword = cnpBusinessAccountPassword;
    return this;
  }

  /**
   * Get cnpBusinessAccountPassword
   * @return cnpBusinessAccountPassword
   */
  
  @Schema(name = "cnpBusinessAccountPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpBusinessAccountPassword")
  public String getCnpBusinessAccountPassword() {
    return cnpBusinessAccountPassword;
  }

  public void setCnpBusinessAccountPassword(String cnpBusinessAccountPassword) {
    this.cnpBusinessAccountPassword = cnpBusinessAccountPassword;
  }

  public PaymentPreferencePaymentCardDto cnpBusinessAccountUsername(String cnpBusinessAccountUsername) {
    this.cnpBusinessAccountUsername = cnpBusinessAccountUsername;
    return this;
  }

  /**
   * Get cnpBusinessAccountUsername
   * @return cnpBusinessAccountUsername
   */
  
  @Schema(name = "cnpBusinessAccountUsername", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpBusinessAccountUsername")
  public String getCnpBusinessAccountUsername() {
    return cnpBusinessAccountUsername;
  }

  public void setCnpBusinessAccountUsername(String cnpBusinessAccountUsername) {
    this.cnpBusinessAccountUsername = cnpBusinessAccountUsername;
  }

  public PaymentPreferencePaymentCardDto cnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
    return this;
  }

  /**
   * Get cnpRequired
   * @return cnpRequired
   */
  
  @Schema(name = "cnpRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpRequired")
  public Boolean getCnpRequired() {
    return cnpRequired;
  }

  public void setCnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
  }

  public PaymentPreferencePaymentCardDto expiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * Get expiryDate
   * @return expiryDate
   */
  
  @Schema(name = "expiryDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryDate")
  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public PaymentPreferencePaymentCardDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentPreferencePaymentCardDto paymentPreferencePaymentCardDto = (PaymentPreferencePaymentCardDto) o;
    return Objects.equals(this.billingAddress, paymentPreferencePaymentCardDto.billingAddress) &&
        Objects.equals(this.cardHolderName, paymentPreferencePaymentCardDto.cardHolderName) &&
        Objects.equals(this.cardNumber, paymentPreferencePaymentCardDto.cardNumber) &&
        Objects.equals(this.cardType, paymentPreferencePaymentCardDto.cardType) &&
        Objects.equals(this.cnpBusinessAccountPassword, paymentPreferencePaymentCardDto.cnpBusinessAccountPassword) &&
        Objects.equals(this.cnpBusinessAccountUsername, paymentPreferencePaymentCardDto.cnpBusinessAccountUsername) &&
        Objects.equals(this.cnpRequired, paymentPreferencePaymentCardDto.cnpRequired) &&
        Objects.equals(this.expiryDate, paymentPreferencePaymentCardDto.expiryDate) &&
        Objects.equals(this.token, paymentPreferencePaymentCardDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingAddress, cardHolderName, cardNumber, cardType, cnpBusinessAccountPassword, cnpBusinessAccountUsername, cnpRequired, expiryDate, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentPreferencePaymentCardDto {\n");
    sb.append("    billingAddress: ").append(toIndentedString(billingAddress)).append("\n");
    sb.append("    cardHolderName: ").append(toIndentedString(cardHolderName)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    cnpBusinessAccountPassword: ").append(toIndentedString(cnpBusinessAccountPassword)).append("\n");
    sb.append("    cnpBusinessAccountUsername: ").append(toIndentedString(cnpBusinessAccountUsername)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

