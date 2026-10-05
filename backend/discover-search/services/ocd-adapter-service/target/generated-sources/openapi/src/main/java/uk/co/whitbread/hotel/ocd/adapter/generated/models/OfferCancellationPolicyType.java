package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.CancelPolicyAmountPercentType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.CancelPolicyDeadlineType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The cancellation rule associated to the cancellation policy.
 */

@Schema(name = "OfferCancellationPolicyType", description = "The cancellation rule associated to the cancellation policy.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferCancellationPolicyType {

  private String policyCode;

  private CancelPolicyDeadlineType deadline;

  private CancelPolicyAmountPercentType amountPercent;

  private String penaltyDescription;

  public OfferCancellationPolicyType() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OfferCancellationPolicyType(String policyCode, CancelPolicyDeadlineType deadline, CancelPolicyAmountPercentType amountPercent, String penaltyDescription) {
    this.policyCode = policyCode;
    this.deadline = deadline;
    this.amountPercent = amountPercent;
    this.penaltyDescription = penaltyDescription;
  }

  public OfferCancellationPolicyType policyCode(String policyCode) {
    this.policyCode = policyCode;
    return this;
  }

  /**
   * The code for the cancellation rule.
   * @return policyCode
   */
  @NotNull @Size(max = 20) 
  @Schema(name = "policyCode", example = "POLICY001", description = "The code for the cancellation rule.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("policyCode")
  public String getPolicyCode() {
    return policyCode;
  }

  public void setPolicyCode(String policyCode) {
    this.policyCode = policyCode;
  }

  public OfferCancellationPolicyType deadline(CancelPolicyDeadlineType deadline) {
    this.deadline = deadline;
    return this;
  }

  /**
   * Get deadline
   * @return deadline
   */
  @NotNull @Valid 
  @Schema(name = "deadline", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("deadline")
  public CancelPolicyDeadlineType getDeadline() {
    return deadline;
  }

  public void setDeadline(CancelPolicyDeadlineType deadline) {
    this.deadline = deadline;
  }

  public OfferCancellationPolicyType amountPercent(CancelPolicyAmountPercentType amountPercent) {
    this.amountPercent = amountPercent;
    return this;
  }

  /**
   * Get amountPercent
   * @return amountPercent
   */
  @NotNull @Valid 
  @Schema(name = "amountPercent", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amountPercent")
  public CancelPolicyAmountPercentType getAmountPercent() {
    return amountPercent;
  }

  public void setAmountPercent(CancelPolicyAmountPercentType amountPercent) {
    this.amountPercent = amountPercent;
  }

  public OfferCancellationPolicyType penaltyDescription(String penaltyDescription) {
    this.penaltyDescription = penaltyDescription;
    return this;
  }

  /**
   * Description of the cancellation rule.
   * @return penaltyDescription
   */
  @NotNull 
  @Schema(name = "penaltyDescription", example = "Cancellation policy description in English", description = "Description of the cancellation rule.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("penaltyDescription")
  public String getPenaltyDescription() {
    return penaltyDescription;
  }

  public void setPenaltyDescription(String penaltyDescription) {
    this.penaltyDescription = penaltyDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferCancellationPolicyType offerCancellationPolicyType = (OfferCancellationPolicyType) o;
    return Objects.equals(this.policyCode, offerCancellationPolicyType.policyCode) &&
        Objects.equals(this.deadline, offerCancellationPolicyType.deadline) &&
        Objects.equals(this.amountPercent, offerCancellationPolicyType.amountPercent) &&
        Objects.equals(this.penaltyDescription, offerCancellationPolicyType.penaltyDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(policyCode, deadline, amountPercent, penaltyDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferCancellationPolicyType {\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    deadline: ").append(toIndentedString(deadline)).append("\n");
    sb.append("    amountPercent: ").append(toIndentedString(amountPercent)).append("\n");
    sb.append("    penaltyDescription: ").append(toIndentedString(penaltyDescription)).append("\n");
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

