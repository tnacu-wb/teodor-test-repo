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
 * SaveCardRequestDto
 */

@JsonTypeName("SaveCardRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SaveCardRequestDto {

  private @Nullable String accountId;

  private AddressDto billingAddress;

  private @Nullable Boolean business;

  private @Nullable String cardId;

  private @Nullable String cardLabel;

  private String cardType;

  private @Nullable Boolean cnpRequired;

  private @Nullable String companyAccountId;

  private String country = "gb";

  private @Nullable String email;

  private @Nullable String employeeAccountId;

  private String environment;

  private @Nullable String language;

  private @Nullable String memorableWord;

  private @Nullable Boolean personalCard;

  private String requestId;

  public SaveCardRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SaveCardRequestDto(AddressDto billingAddress, String cardType, String environment, String requestId) {
    this.billingAddress = billingAddress;
    this.cardType = cardType;
    this.environment = environment;
    this.requestId = requestId;
  }

  public SaveCardRequestDto accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Authenticated account id
   * @return accountId
   */
  
  @Schema(name = "accountId", description = "Authenticated account id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountId")
  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public SaveCardRequestDto billingAddress(AddressDto billingAddress) {
    this.billingAddress = billingAddress;
    return this;
  }

  /**
   * Get billingAddress
   * @return billingAddress
   */
  @NotNull @Valid 
  @Schema(name = "billingAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("billingAddress")
  public AddressDto getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(AddressDto billingAddress) {
    this.billingAddress = billingAddress;
  }

  public SaveCardRequestDto business(Boolean business) {
    this.business = business;
    return this;
  }

  /**
   * Is the card for business use - true for BB flow, false for MyPI flow; Defaults to false
   * @return business
   */
  
  @Schema(name = "business", description = "Is the card for business use - true for BB flow, false for MyPI flow; Defaults to false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public Boolean getBusiness() {
    return business;
  }

  public void setBusiness(Boolean business) {
    this.business = business;
  }

  public SaveCardRequestDto cardId(String cardId) {
    this.cardId = cardId;
    return this;
  }

  /**
   * Card identifier for centrally stored cards
   * @return cardId
   */
  
  @Schema(name = "cardId", description = "Card identifier for centrally stored cards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardId")
  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public SaveCardRequestDto cardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
    return this;
  }

  /**
   * A user-defined label for easy recognition of centrally stored cards
   * @return cardLabel
   */
  
  @Schema(name = "cardLabel", description = "A user-defined label for easy recognition of centrally stored cards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardLabel")
  public String getCardLabel() {
    return cardLabel;
  }

  public void setCardLabel(String cardLabel) {
    this.cardLabel = cardLabel;
  }

  public SaveCardRequestDto cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Get cardType
   * @return cardType
   */
  @NotNull 
  @Schema(name = "cardType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public SaveCardRequestDto cnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
    return this;
  }

  /**
   * Indicates whether it's a CNP context; Defaults to false
   * @return cnpRequired
   */
  
  @Schema(name = "cnpRequired", description = "Indicates whether it's a CNP context; Defaults to false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cnpRequired")
  public Boolean getCnpRequired() {
    return cnpRequired;
  }

  public void setCnpRequired(Boolean cnpRequired) {
    this.cnpRequired = cnpRequired;
  }

  public SaveCardRequestDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Authenticated company account id
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", description = "Authenticated company account id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public SaveCardRequestDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Website location of the user
   * @return country
   */
  
  @Schema(name = "country", description = "Website location of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public SaveCardRequestDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Authenticated user email
   * @return email
   */
  
  @Schema(name = "email", description = "Authenticated user email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public SaveCardRequestDto employeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Authenticated employee account id
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", description = "Authenticated employee account id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public SaveCardRequestDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Environment hostname
   * @return environment
   */
  @NotNull 
  @Schema(name = "environment", description = "Environment hostname", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public SaveCardRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Language specified by the user
   * @return language
   */
  
  @Schema(name = "language", description = "Language specified by the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public SaveCardRequestDto memorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
    return this;
  }

  /**
   * Card Not Present (CNP) memorable word for PIBA
   * @return memorableWord
   */
  
  @Schema(name = "memorableWord", description = "Card Not Present (CNP) memorable word for PIBA", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("memorableWord")
  public String getMemorableWord() {
    return memorableWord;
  }

  public void setMemorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
  }

  public SaveCardRequestDto personalCard(Boolean personalCard) {
    this.personalCard = personalCard;
    return this;
  }

  /**
   * True for personal cards (MyPI and BB), false for centrally stored cards (BB); Defaults to false
   * @return personalCard
   */
  
  @Schema(name = "personalCard", description = "True for personal cards (MyPI and BB), false for centrally stored cards (BB); Defaults to false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("personalCard")
  public Boolean getPersonalCard() {
    return personalCard;
  }

  public void setPersonalCard(Boolean personalCard) {
    this.personalCard = personalCard;
  }

  public SaveCardRequestDto requestId(String requestId) {
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
    SaveCardRequestDto saveCardRequest = (SaveCardRequestDto) o;
    return Objects.equals(this.accountId, saveCardRequest.accountId) &&
        Objects.equals(this.billingAddress, saveCardRequest.billingAddress) &&
        Objects.equals(this.business, saveCardRequest.business) &&
        Objects.equals(this.cardId, saveCardRequest.cardId) &&
        Objects.equals(this.cardLabel, saveCardRequest.cardLabel) &&
        Objects.equals(this.cardType, saveCardRequest.cardType) &&
        Objects.equals(this.cnpRequired, saveCardRequest.cnpRequired) &&
        Objects.equals(this.companyAccountId, saveCardRequest.companyAccountId) &&
        Objects.equals(this.country, saveCardRequest.country) &&
        Objects.equals(this.email, saveCardRequest.email) &&
        Objects.equals(this.employeeAccountId, saveCardRequest.employeeAccountId) &&
        Objects.equals(this.environment, saveCardRequest.environment) &&
        Objects.equals(this.language, saveCardRequest.language) &&
        Objects.equals(this.memorableWord, saveCardRequest.memorableWord) &&
        Objects.equals(this.personalCard, saveCardRequest.personalCard) &&
        Objects.equals(this.requestId, saveCardRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, billingAddress, business, cardId, cardLabel, cardType, cnpRequired, companyAccountId, country, email, employeeAccountId, environment, language, memorableWord, personalCard, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SaveCardRequestDto {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    billingAddress: ").append(toIndentedString(billingAddress)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    cardId: ").append(toIndentedString(cardId)).append("\n");
    sb.append("    cardLabel: ").append(toIndentedString(cardLabel)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    cnpRequired: ").append(toIndentedString(cnpRequired)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    memorableWord: ").append(toIndentedString(memorableWord)).append("\n");
    sb.append("    personalCard: ").append(toIndentedString(personalCard)).append("\n");
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

