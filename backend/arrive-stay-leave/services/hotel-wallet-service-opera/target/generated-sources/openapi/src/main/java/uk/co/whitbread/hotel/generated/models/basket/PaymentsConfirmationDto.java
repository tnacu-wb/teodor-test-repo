package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.PaymentErrorDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentsConfirmationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentsConfirmationDto {

  private String bookingReference;

  private @Nullable String cardSchemeId;

  private String channel;

  private String countryCode;

  private @Nullable String expiry;

  private @Nullable String firstName;

  private @Nullable String fraudCheckDecision;

  private String language;

  private @Nullable String last4Digits;

  private @Nullable String lastName;

  private @Nullable PaymentErrorDto paymentError;

  private String paymentId;

  private String paymentStatus;

  private String reference;

  private @Nullable String token;

  public PaymentsConfirmationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentsConfirmationDto(String bookingReference, String channel, String countryCode, String language, String paymentId, String paymentStatus, String reference) {
    this.bookingReference = bookingReference;
    this.channel = channel;
    this.countryCode = countryCode;
    this.language = language;
    this.paymentId = paymentId;
    this.paymentStatus = paymentStatus;
    this.reference = reference;
  }

  public PaymentsConfirmationDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public PaymentsConfirmationDto cardSchemeId(String cardSchemeId) {
    this.cardSchemeId = cardSchemeId;
    return this;
  }

  /**
   * Get cardSchemeId
   * @return cardSchemeId
   */
  
  @Schema(name = "cardSchemeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardSchemeId")
  public String getCardSchemeId() {
    return cardSchemeId;
  }

  public void setCardSchemeId(String cardSchemeId) {
    this.cardSchemeId = cardSchemeId;
  }

  public PaymentsConfirmationDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public PaymentsConfirmationDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  @NotNull 
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public PaymentsConfirmationDto expiry(String expiry) {
    this.expiry = expiry;
    return this;
  }

  /**
   * Get expiry
   * @return expiry
   */
  
  @Schema(name = "expiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiry")
  public String getExpiry() {
    return expiry;
  }

  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  public PaymentsConfirmationDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public PaymentsConfirmationDto fraudCheckDecision(String fraudCheckDecision) {
    this.fraudCheckDecision = fraudCheckDecision;
    return this;
  }

  /**
   * Get fraudCheckDecision
   * @return fraudCheckDecision
   */
  
  @Schema(name = "fraudCheckDecision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fraudCheckDecision")
  public String getFraudCheckDecision() {
    return fraudCheckDecision;
  }

  public void setFraudCheckDecision(String fraudCheckDecision) {
    this.fraudCheckDecision = fraudCheckDecision;
  }

  public PaymentsConfirmationDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  @NotNull 
  @Schema(name = "language", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public PaymentsConfirmationDto last4Digits(String last4Digits) {
    this.last4Digits = last4Digits;
    return this;
  }

  /**
   * Get last4Digits
   * @return last4Digits
   */
  
  @Schema(name = "last4Digits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last4Digits")
  public String getLast4Digits() {
    return last4Digits;
  }

  public void setLast4Digits(String last4Digits) {
    this.last4Digits = last4Digits;
  }

  public PaymentsConfirmationDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public PaymentsConfirmationDto paymentError(PaymentErrorDto paymentError) {
    this.paymentError = paymentError;
    return this;
  }

  /**
   * Get paymentError
   * @return paymentError
   */
  @Valid 
  @Schema(name = "paymentError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentError")
  public PaymentErrorDto getPaymentError() {
    return paymentError;
  }

  public void setPaymentError(PaymentErrorDto paymentError) {
    this.paymentError = paymentError;
  }

  public PaymentsConfirmationDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  @NotNull 
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public PaymentsConfirmationDto paymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
    return this;
  }

  /**
   * Get paymentStatus
   * @return paymentStatus
   */
  @NotNull 
  @Schema(name = "paymentStatus", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("paymentStatus")
  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public PaymentsConfirmationDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public PaymentsConfirmationDto token(String token) {
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
    PaymentsConfirmationDto paymentsConfirmationDto = (PaymentsConfirmationDto) o;
    return Objects.equals(this.bookingReference, paymentsConfirmationDto.bookingReference) &&
        Objects.equals(this.cardSchemeId, paymentsConfirmationDto.cardSchemeId) &&
        Objects.equals(this.channel, paymentsConfirmationDto.channel) &&
        Objects.equals(this.countryCode, paymentsConfirmationDto.countryCode) &&
        Objects.equals(this.expiry, paymentsConfirmationDto.expiry) &&
        Objects.equals(this.firstName, paymentsConfirmationDto.firstName) &&
        Objects.equals(this.fraudCheckDecision, paymentsConfirmationDto.fraudCheckDecision) &&
        Objects.equals(this.language, paymentsConfirmationDto.language) &&
        Objects.equals(this.last4Digits, paymentsConfirmationDto.last4Digits) &&
        Objects.equals(this.lastName, paymentsConfirmationDto.lastName) &&
        Objects.equals(this.paymentError, paymentsConfirmationDto.paymentError) &&
        Objects.equals(this.paymentId, paymentsConfirmationDto.paymentId) &&
        Objects.equals(this.paymentStatus, paymentsConfirmationDto.paymentStatus) &&
        Objects.equals(this.reference, paymentsConfirmationDto.reference) &&
        Objects.equals(this.token, paymentsConfirmationDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, cardSchemeId, channel, countryCode, expiry, firstName, fraudCheckDecision, language, last4Digits, lastName, paymentError, paymentId, paymentStatus, reference, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentsConfirmationDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    cardSchemeId: ").append(toIndentedString(cardSchemeId)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    fraudCheckDecision: ").append(toIndentedString(fraudCheckDecision)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    last4Digits: ").append(toIndentedString(last4Digits)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    paymentError: ").append(toIndentedString(paymentError)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
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

