package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ThreeCResponseDto
 */

@JsonTypeName("ThreeCResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ThreeCResponseDto {

  private @Nullable String sessionId;

  private @Nullable String template;

  private @Nullable String providerUrl;

  private @Nullable String providerStatus;

  private @Nullable String providerStatusText;

  private @Nullable String providerReason;

  private @Nullable String providerResult;

  private @Nullable String authCode;

  private @Nullable String last4Digits;

  private @Nullable String cardSchemeId;

  private @Nullable String cardSchemeName;

  private @Nullable String token;

  private @Nullable String binRange;

  private @Nullable String expiry;

  private @Nullable String status;

  private @Nullable String avsResult;

  private @Nullable String fraudCheckDecision;

  private @Nullable String fraudCheckResult;

  private @Nullable String fraudCheckResultReason;

  private @Nullable String fraudCheckRequestId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate date;

  private @Nullable String scaReference;

  private @Nullable String threeDSIndicator;

  private @Nullable String iPageHtml;

  public ThreeCResponseDto sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   */
  
  @Schema(name = "sessionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionId")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public ThreeCResponseDto template(String template) {
    this.template = template;
    return this;
  }

  /**
   * Get template
   * @return template
   */
  
  @Schema(name = "template", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("template")
  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public ThreeCResponseDto providerUrl(String providerUrl) {
    this.providerUrl = providerUrl;
    return this;
  }

  /**
   * Get providerUrl
   * @return providerUrl
   */
  
  @Schema(name = "providerUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerUrl")
  public String getProviderUrl() {
    return providerUrl;
  }

  public void setProviderUrl(String providerUrl) {
    this.providerUrl = providerUrl;
  }

  public ThreeCResponseDto providerStatus(String providerStatus) {
    this.providerStatus = providerStatus;
    return this;
  }

  /**
   * Get providerStatus
   * @return providerStatus
   */
  
  @Schema(name = "providerStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerStatus")
  public String getProviderStatus() {
    return providerStatus;
  }

  public void setProviderStatus(String providerStatus) {
    this.providerStatus = providerStatus;
  }

  public ThreeCResponseDto providerStatusText(String providerStatusText) {
    this.providerStatusText = providerStatusText;
    return this;
  }

  /**
   * Get providerStatusText
   * @return providerStatusText
   */
  
  @Schema(name = "providerStatusText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerStatusText")
  public String getProviderStatusText() {
    return providerStatusText;
  }

  public void setProviderStatusText(String providerStatusText) {
    this.providerStatusText = providerStatusText;
  }

  public ThreeCResponseDto providerReason(String providerReason) {
    this.providerReason = providerReason;
    return this;
  }

  /**
   * Get providerReason
   * @return providerReason
   */
  
  @Schema(name = "providerReason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerReason")
  public String getProviderReason() {
    return providerReason;
  }

  public void setProviderReason(String providerReason) {
    this.providerReason = providerReason;
  }

  public ThreeCResponseDto providerResult(String providerResult) {
    this.providerResult = providerResult;
    return this;
  }

  /**
   * Get providerResult
   * @return providerResult
   */
  
  @Schema(name = "providerResult", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerResult")
  public String getProviderResult() {
    return providerResult;
  }

  public void setProviderResult(String providerResult) {
    this.providerResult = providerResult;
  }

  public ThreeCResponseDto authCode(String authCode) {
    this.authCode = authCode;
    return this;
  }

  /**
   * Get authCode
   * @return authCode
   */
  
  @Schema(name = "authCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("authCode")
  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public ThreeCResponseDto last4Digits(String last4Digits) {
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

  public ThreeCResponseDto cardSchemeId(String cardSchemeId) {
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

  public ThreeCResponseDto cardSchemeName(String cardSchemeName) {
    this.cardSchemeName = cardSchemeName;
    return this;
  }

  /**
   * Get cardSchemeName
   * @return cardSchemeName
   */
  
  @Schema(name = "cardSchemeName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardSchemeName")
  public String getCardSchemeName() {
    return cardSchemeName;
  }

  public void setCardSchemeName(String cardSchemeName) {
    this.cardSchemeName = cardSchemeName;
  }

  public ThreeCResponseDto token(String token) {
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

  public ThreeCResponseDto binRange(String binRange) {
    this.binRange = binRange;
    return this;
  }

  /**
   * Get binRange
   * @return binRange
   */
  
  @Schema(name = "binRange", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("binRange")
  public String getBinRange() {
    return binRange;
  }

  public void setBinRange(String binRange) {
    this.binRange = binRange;
  }

  public ThreeCResponseDto expiry(String expiry) {
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

  public ThreeCResponseDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ThreeCResponseDto avsResult(String avsResult) {
    this.avsResult = avsResult;
    return this;
  }

  /**
   * Get avsResult
   * @return avsResult
   */
  
  @Schema(name = "avsResult", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avsResult")
  public String getAvsResult() {
    return avsResult;
  }

  public void setAvsResult(String avsResult) {
    this.avsResult = avsResult;
  }

  public ThreeCResponseDto fraudCheckDecision(String fraudCheckDecision) {
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

  public ThreeCResponseDto fraudCheckResult(String fraudCheckResult) {
    this.fraudCheckResult = fraudCheckResult;
    return this;
  }

  /**
   * Get fraudCheckResult
   * @return fraudCheckResult
   */
  
  @Schema(name = "fraudCheckResult", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fraudCheckResult")
  public String getFraudCheckResult() {
    return fraudCheckResult;
  }

  public void setFraudCheckResult(String fraudCheckResult) {
    this.fraudCheckResult = fraudCheckResult;
  }

  public ThreeCResponseDto fraudCheckResultReason(String fraudCheckResultReason) {
    this.fraudCheckResultReason = fraudCheckResultReason;
    return this;
  }

  /**
   * Get fraudCheckResultReason
   * @return fraudCheckResultReason
   */
  
  @Schema(name = "fraudCheckResultReason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fraudCheckResultReason")
  public String getFraudCheckResultReason() {
    return fraudCheckResultReason;
  }

  public void setFraudCheckResultReason(String fraudCheckResultReason) {
    this.fraudCheckResultReason = fraudCheckResultReason;
  }

  public ThreeCResponseDto fraudCheckRequestId(String fraudCheckRequestId) {
    this.fraudCheckRequestId = fraudCheckRequestId;
    return this;
  }

  /**
   * Get fraudCheckRequestId
   * @return fraudCheckRequestId
   */
  
  @Schema(name = "fraudCheckRequestId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fraudCheckRequestId")
  public String getFraudCheckRequestId() {
    return fraudCheckRequestId;
  }

  public void setFraudCheckRequestId(String fraudCheckRequestId) {
    this.fraudCheckRequestId = fraudCheckRequestId;
  }

  public ThreeCResponseDto date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
   */
  @Valid 
  @Schema(name = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public ThreeCResponseDto scaReference(String scaReference) {
    this.scaReference = scaReference;
    return this;
  }

  /**
   * Get scaReference
   * @return scaReference
   */
  
  @Schema(name = "scaReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scaReference")
  public String getScaReference() {
    return scaReference;
  }

  public void setScaReference(String scaReference) {
    this.scaReference = scaReference;
  }

  public ThreeCResponseDto threeDSIndicator(String threeDSIndicator) {
    this.threeDSIndicator = threeDSIndicator;
    return this;
  }

  /**
   * Get threeDSIndicator
   * @return threeDSIndicator
   */
  
  @Schema(name = "threeDSIndicator", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("threeDSIndicator")
  public String getThreeDSIndicator() {
    return threeDSIndicator;
  }

  public void setThreeDSIndicator(String threeDSIndicator) {
    this.threeDSIndicator = threeDSIndicator;
  }

  public ThreeCResponseDto iPageHtml(String iPageHtml) {
    this.iPageHtml = iPageHtml;
    return this;
  }

  /**
   * Get iPageHtml
   * @return iPageHtml
   */
  
  @Schema(name = "iPageHtml", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iPageHtml")
  public String getiPageHtml() {
    return iPageHtml;
  }

  public void setiPageHtml(String iPageHtml) {
    this.iPageHtml = iPageHtml;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ThreeCResponseDto threeCResponse = (ThreeCResponseDto) o;
    return Objects.equals(this.sessionId, threeCResponse.sessionId) &&
        Objects.equals(this.template, threeCResponse.template) &&
        Objects.equals(this.providerUrl, threeCResponse.providerUrl) &&
        Objects.equals(this.providerStatus, threeCResponse.providerStatus) &&
        Objects.equals(this.providerStatusText, threeCResponse.providerStatusText) &&
        Objects.equals(this.providerReason, threeCResponse.providerReason) &&
        Objects.equals(this.providerResult, threeCResponse.providerResult) &&
        Objects.equals(this.authCode, threeCResponse.authCode) &&
        Objects.equals(this.last4Digits, threeCResponse.last4Digits) &&
        Objects.equals(this.cardSchemeId, threeCResponse.cardSchemeId) &&
        Objects.equals(this.cardSchemeName, threeCResponse.cardSchemeName) &&
        Objects.equals(this.token, threeCResponse.token) &&
        Objects.equals(this.binRange, threeCResponse.binRange) &&
        Objects.equals(this.expiry, threeCResponse.expiry) &&
        Objects.equals(this.status, threeCResponse.status) &&
        Objects.equals(this.avsResult, threeCResponse.avsResult) &&
        Objects.equals(this.fraudCheckDecision, threeCResponse.fraudCheckDecision) &&
        Objects.equals(this.fraudCheckResult, threeCResponse.fraudCheckResult) &&
        Objects.equals(this.fraudCheckResultReason, threeCResponse.fraudCheckResultReason) &&
        Objects.equals(this.fraudCheckRequestId, threeCResponse.fraudCheckRequestId) &&
        Objects.equals(this.date, threeCResponse.date) &&
        Objects.equals(this.scaReference, threeCResponse.scaReference) &&
        Objects.equals(this.threeDSIndicator, threeCResponse.threeDSIndicator) &&
        Objects.equals(this.iPageHtml, threeCResponse.iPageHtml);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionId, template, providerUrl, providerStatus, providerStatusText, providerReason, providerResult, authCode, last4Digits, cardSchemeId, cardSchemeName, token, binRange, expiry, status, avsResult, fraudCheckDecision, fraudCheckResult, fraudCheckResultReason, fraudCheckRequestId, date, scaReference, threeDSIndicator, iPageHtml);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ThreeCResponseDto {\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    template: ").append(toIndentedString(template)).append("\n");
    sb.append("    providerUrl: ").append(toIndentedString(providerUrl)).append("\n");
    sb.append("    providerStatus: ").append(toIndentedString(providerStatus)).append("\n");
    sb.append("    providerStatusText: ").append(toIndentedString(providerStatusText)).append("\n");
    sb.append("    providerReason: ").append(toIndentedString(providerReason)).append("\n");
    sb.append("    providerResult: ").append(toIndentedString(providerResult)).append("\n");
    sb.append("    authCode: ").append(toIndentedString(authCode)).append("\n");
    sb.append("    last4Digits: ").append(toIndentedString(last4Digits)).append("\n");
    sb.append("    cardSchemeId: ").append(toIndentedString(cardSchemeId)).append("\n");
    sb.append("    cardSchemeName: ").append(toIndentedString(cardSchemeName)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    binRange: ").append(toIndentedString(binRange)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    avsResult: ").append(toIndentedString(avsResult)).append("\n");
    sb.append("    fraudCheckDecision: ").append(toIndentedString(fraudCheckDecision)).append("\n");
    sb.append("    fraudCheckResult: ").append(toIndentedString(fraudCheckResult)).append("\n");
    sb.append("    fraudCheckResultReason: ").append(toIndentedString(fraudCheckResultReason)).append("\n");
    sb.append("    fraudCheckRequestId: ").append(toIndentedString(fraudCheckRequestId)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    scaReference: ").append(toIndentedString(scaReference)).append("\n");
    sb.append("    threeDSIndicator: ").append(toIndentedString(threeDSIndicator)).append("\n");
    sb.append("    iPageHtml: ").append(toIndentedString(iPageHtml)).append("\n");
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

