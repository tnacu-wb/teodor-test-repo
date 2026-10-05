package uk.co.whitbread.hotel.account.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.account.service.generated.models.AccountValueResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AccountInfoResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AccountInfoResponseDto {

  private @Nullable String billingFrequency;

  private @Nullable Integer daysToPay;

  private @Nullable AccountValueResponseDto outStandingBalance;

  private @Nullable AccountValueResponseDto statementValue;

  private @Nullable String status;

  public AccountInfoResponseDto billingFrequency(String billingFrequency) {
    this.billingFrequency = billingFrequency;
    return this;
  }

  /**
   * Get billingFrequency
   * @return billingFrequency
   */
  
  @Schema(name = "billingFrequency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billingFrequency")
  public String getBillingFrequency() {
    return billingFrequency;
  }

  public void setBillingFrequency(String billingFrequency) {
    this.billingFrequency = billingFrequency;
  }

  public AccountInfoResponseDto daysToPay(Integer daysToPay) {
    this.daysToPay = daysToPay;
    return this;
  }

  /**
   * Get daysToPay
   * @return daysToPay
   */
  
  @Schema(name = "daysToPay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("daysToPay")
  public Integer getDaysToPay() {
    return daysToPay;
  }

  public void setDaysToPay(Integer daysToPay) {
    this.daysToPay = daysToPay;
  }

  public AccountInfoResponseDto outStandingBalance(AccountValueResponseDto outStandingBalance) {
    this.outStandingBalance = outStandingBalance;
    return this;
  }

  /**
   * Get outStandingBalance
   * @return outStandingBalance
   */
  @Valid 
  @Schema(name = "outStandingBalance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("outStandingBalance")
  public AccountValueResponseDto getOutStandingBalance() {
    return outStandingBalance;
  }

  public void setOutStandingBalance(AccountValueResponseDto outStandingBalance) {
    this.outStandingBalance = outStandingBalance;
  }

  public AccountInfoResponseDto statementValue(AccountValueResponseDto statementValue) {
    this.statementValue = statementValue;
    return this;
  }

  /**
   * Get statementValue
   * @return statementValue
   */
  @Valid 
  @Schema(name = "statementValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("statementValue")
  public AccountValueResponseDto getStatementValue() {
    return statementValue;
  }

  public void setStatementValue(AccountValueResponseDto statementValue) {
    this.statementValue = statementValue;
  }

  public AccountInfoResponseDto status(String status) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountInfoResponseDto accountInfoResponseDto = (AccountInfoResponseDto) o;
    return Objects.equals(this.billingFrequency, accountInfoResponseDto.billingFrequency) &&
        Objects.equals(this.daysToPay, accountInfoResponseDto.daysToPay) &&
        Objects.equals(this.outStandingBalance, accountInfoResponseDto.outStandingBalance) &&
        Objects.equals(this.statementValue, accountInfoResponseDto.statementValue) &&
        Objects.equals(this.status, accountInfoResponseDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingFrequency, daysToPay, outStandingBalance, statementValue, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountInfoResponseDto {\n");
    sb.append("    billingFrequency: ").append(toIndentedString(billingFrequency)).append("\n");
    sb.append("    daysToPay: ").append(toIndentedString(daysToPay)).append("\n");
    sb.append("    outStandingBalance: ").append(toIndentedString(outStandingBalance)).append("\n");
    sb.append("    statementValue: ").append(toIndentedString(statementValue)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

