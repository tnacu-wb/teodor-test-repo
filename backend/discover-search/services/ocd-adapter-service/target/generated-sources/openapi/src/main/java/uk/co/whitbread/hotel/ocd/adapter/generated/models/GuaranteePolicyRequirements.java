package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Additional requirement for the payment policy.
 */

@Schema(name = "GuaranteePolicyRequirements", description = "Additional requirement for the payment policy.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GuaranteePolicyRequirements {

  private @Nullable Boolean travelAgent;

  private @Nullable Boolean company;

  private @Nullable Boolean creditCard;

  private @Nullable Boolean deposit;

  public GuaranteePolicyRequirements travelAgent(Boolean travelAgent) {
    this.travelAgent = travelAgent;
    return this;
  }

  /**
   * When true a valid travel agent ID is required for the guarantee type.
   * @return travelAgent
   */
  
  @Schema(name = "travelAgent", example = "false", description = "When true a valid travel agent ID is required for the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("travelAgent")
  public Boolean getTravelAgent() {
    return travelAgent;
  }

  public void setTravelAgent(Boolean travelAgent) {
    this.travelAgent = travelAgent;
  }

  public GuaranteePolicyRequirements company(Boolean company) {
    this.company = company;
    return this;
  }

  /**
   * When true a valid company ID is required for the guarantee type.
   * @return company
   */
  
  @Schema(name = "company", example = "false", description = "When true a valid company ID is required for the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("company")
  public Boolean getCompany() {
    return company;
  }

  public void setCompany(Boolean company) {
    this.company = company;
  }

  public GuaranteePolicyRequirements creditCard(Boolean creditCard) {
    this.creditCard = creditCard;
    return this;
  }

  /**
   * When true a valid credit card is required for the guarantee type.
   * @return creditCard
   */
  
  @Schema(name = "creditCard", example = "true", description = "When true a valid credit card is required for the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditCard")
  public Boolean getCreditCard() {
    return creditCard;
  }

  public void setCreditCard(Boolean creditCard) {
    this.creditCard = creditCard;
  }

  public GuaranteePolicyRequirements deposit(Boolean deposit) {
    this.deposit = deposit;
    return this;
  }

  /**
   * When true a deposit is required for the guarantee type.
   * @return deposit
   */
  
  @Schema(name = "deposit", example = "true", description = "When true a deposit is required for the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposit")
  public Boolean getDeposit() {
    return deposit;
  }

  public void setDeposit(Boolean deposit) {
    this.deposit = deposit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GuaranteePolicyRequirements guaranteePolicyRequirements = (GuaranteePolicyRequirements) o;
    return Objects.equals(this.travelAgent, guaranteePolicyRequirements.travelAgent) &&
        Objects.equals(this.company, guaranteePolicyRequirements.company) &&
        Objects.equals(this.creditCard, guaranteePolicyRequirements.creditCard) &&
        Objects.equals(this.deposit, guaranteePolicyRequirements.deposit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(travelAgent, company, creditCard, deposit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GuaranteePolicyRequirements {\n");
    sb.append("    travelAgent: ").append(toIndentedString(travelAgent)).append("\n");
    sb.append("    company: ").append(toIndentedString(company)).append("\n");
    sb.append("    creditCard: ").append(toIndentedString(creditCard)).append("\n");
    sb.append("    deposit: ").append(toIndentedString(deposit)).append("\n");
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

