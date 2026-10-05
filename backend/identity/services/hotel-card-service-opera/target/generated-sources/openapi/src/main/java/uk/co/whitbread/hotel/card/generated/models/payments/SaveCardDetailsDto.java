package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SaveCardDetailsDto
 */

@JsonTypeName("SaveCardDetails")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SaveCardDetailsDto {

  private @Nullable String accountId;

  private @Nullable AddressDto billingAddress;

  private @Nullable Boolean business;

  private @Nullable String cardId;

  private @Nullable String cardLabel;

  private @Nullable Boolean cnpRequired;

  private @Nullable String companyAccountId;

  private @Nullable String email;

  private @Nullable String employeeAccountId;

  private @Nullable String environment;

  private @Nullable String language;

  private @Nullable String memorableWord;

  private @Nullable Boolean personalCard;

  public SaveCardDetailsDto accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
   */
  
  @Schema(name = "accountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountId")
  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public SaveCardDetailsDto billingAddress(AddressDto billingAddress) {
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
  public AddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(AddressDto billingAddress) {
    this.billingAddress = billingAddress;
  }

  public SaveCardDetailsDto business(Boolean business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public Boolean getBusiness() {
    return business;
  }

  public void setBusiness(Boolean business) {
    this.business = business;
  }

  public SaveCardDetailsDto cardId(String cardId) {
    this.cardId = cardId;
    return this;
  }

  /**
   * Get cardId
   * @return cardId
   */
  
  @Schema(name = "cardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardId")
  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public SaveCardDetailsDto cardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
    return this;
  }

  /**
   * Get cardLabel
   * @return cardLabel
   */
  
  @Schema(name = "cardLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardLabel")
  public String getCardLabel() {
    return cardLabel;
  }

  public void setCardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
  }

  public SaveCardDetailsDto cnpRequired(Boolean cnpRequired) {
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

  public SaveCardDetailsDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public SaveCardDetailsDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public SaveCardDetailsDto employeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public SaveCardDetailsDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Get environment
   * @return environment
   */
  
  @Schema(name = "environment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public SaveCardDetailsDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public SaveCardDetailsDto memorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
    return this;
  }

  /**
   * Get memorableWord
   * @return memorableWord
   */
  
  @Schema(name = "memorableWord", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("memorableWord")
  public String getMemorableWord() {
    return memorableWord;
  }

  public void setMemorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
  }

  public SaveCardDetailsDto personalCard(Boolean personalCard) {
    this.personalCard = personalCard;
    return this;
  }

  /**
   * Get personalCard
   * @return personalCard
   */
  
  @Schema(name = "personalCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("personalCard")
  public Boolean getPersonalCard() {
    return personalCard;
  }

  public void setPersonalCard(Boolean personalCard) {
    this.personalCard = personalCard;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaveCardDetailsDto saveCardDetails = (SaveCardDetailsDto) o;
    return Objects.equals(this.accountId, saveCardDetails.accountId) &&
        Objects.equals(this.billingAddress, saveCardDetails.billingAddress) &&
        Objects.equals(this.business, saveCardDetails.business) &&
        Objects.equals(this.cardId, saveCardDetails.cardId) &&
        Objects.equals(this.cardLabel, saveCardDetails.cardLabel) &&
        Objects.equals(this.cnpRequired, saveCardDetails.cnpRequired) &&
        Objects.equals(this.companyAccountId, saveCardDetails.companyAccountId) &&
        Objects.equals(this.email, saveCardDetails.email) &&
        Objects.equals(this.employeeAccountId, saveCardDetails.employeeAccountId) &&
        Objects.equals(this.environment, saveCardDetails.environment) &&
        Objects.equals(this.language, saveCardDetails.language) &&
        Objects.equals(this.memorableWord, saveCardDetails.memorableWord) &&
        Objects.equals(this.personalCard, saveCardDetails.personalCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, billingAddress, business, cardId, cardLabel, cnpRequired, companyAccountId, email, employeeAccountId, environment, language, memorableWord, personalCard);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SaveCardDetailsDto {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    billingAddress: ").append(toIndentedString(billingAddress)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    cardId: ").append(toIndentedString(cardId)).append("\n");
    sb.append("    cardLabel: ").append(toIndentedString(cardLabel)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    memorableWord: ").append(toIndentedString(memorableWord)).append("\n");
    sb.append("    personalCard: ").append(toIndentedString(personalCard)).append("\n");
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

