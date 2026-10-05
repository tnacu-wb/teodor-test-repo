package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Business
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Business {

  /**
   * Gets or Sets accessLevel
   */
  public enum AccessLevelEnum {
    STAYER("STAYER"),
    
    SELF("SELF"),
    
    BOOKER("BOOKER"),
    
    SUPER("SUPER");

    private String value;

    AccessLevelEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AccessLevelEnum fromValue(String value) {
      for (AccessLevelEnum b : AccessLevelEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable AccessLevelEnum accessLevel;

  private @Nullable Long awaitingApproval;

  private @Nullable String centralCard;

  private @Nullable String customerReferenceAnswer;

  private @Nullable Boolean dismissMPILink;

  private @Nullable String employeeId;

  private @Nullable Boolean miSetupRequired;

  private @Nullable String myPILink;

  private @Nullable String purchaseOrderAnswer;

  private @Nullable Boolean tethered;

  public Business accessLevel(AccessLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
    return this;
  }

  /**
   * Get accessLevel
   * @return accessLevel
   */
  
  @Schema(name = "accessLevel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessLevel")
  public AccessLevelEnum getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(AccessLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public Business awaitingApproval(Long awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
    return this;
  }

  /**
   * Get awaitingApproval
   * @return awaitingApproval
   */
  
  @Schema(name = "awaitingApproval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("awaitingApproval")
  public Long getAwaitingApproval() {
    return awaitingApproval;
  }

  public void setAwaitingApproval(Long awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
  }

  public Business centralCard(String centralCard) {
    this.centralCard = centralCard;
    return this;
  }

  /**
   * Get centralCard
   * @return centralCard
   */
  
  @Schema(name = "centralCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centralCard")
  public String getCentralCard() {
    return centralCard;
  }

  public void setCentralCard(String centralCard) {
    this.centralCard = centralCard;
  }

  public Business customerReferenceAnswer(String customerReferenceAnswer) {
    this.customerReferenceAnswer = customerReferenceAnswer;
    return this;
  }

  /**
   * Get customerReferenceAnswer
   * @return customerReferenceAnswer
   */
  
  @Schema(name = "customerReferenceAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReferenceAnswer")
  public String getCustomerReferenceAnswer() {
    return customerReferenceAnswer;
  }

  public void setCustomerReferenceAnswer(String customerReferenceAnswer) {
    this.customerReferenceAnswer = customerReferenceAnswer;
  }

  public Business dismissMPILink(Boolean dismissMPILink) {
    this.dismissMPILink = dismissMPILink;
    return this;
  }

  /**
   * Get dismissMPILink
   * @return dismissMPILink
   */
  
  @Schema(name = "dismissMPILink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dismissMPILink")
  public Boolean getDismissMPILink() {
    return dismissMPILink;
  }

  public void setDismissMPILink(Boolean dismissMPILink) {
    this.dismissMPILink = dismissMPILink;
  }

  public Business employeeId(String employeeId) {
    this.employeeId = employeeId;
    return this;
  }

  /**
   * Get employeeId
   * @return employeeId
   */
  
  @Schema(name = "employeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeId")
  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public Business miSetupRequired(Boolean miSetupRequired) {
    this.miSetupRequired = miSetupRequired;
    return this;
  }

  /**
   * Get miSetupRequired
   * @return miSetupRequired
   */
  
  @Schema(name = "miSetupRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("miSetupRequired")
  public Boolean getMiSetupRequired() {
    return miSetupRequired;
  }

  public void setMiSetupRequired(Boolean miSetupRequired) {
    this.miSetupRequired = miSetupRequired;
  }

  public Business myPILink(String myPILink) {
    this.myPILink = myPILink;
    return this;
  }

  /**
   * Get myPILink
   * @return myPILink
   */
  
  @Schema(name = "myPILink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("myPILink")
  public String getMyPILink() {
    return myPILink;
  }

  public void setMyPILink(String myPILink) {
    this.myPILink = myPILink;
  }

  public Business purchaseOrderAnswer(String purchaseOrderAnswer) {
    this.purchaseOrderAnswer = purchaseOrderAnswer;
    return this;
  }

  /**
   * Get purchaseOrderAnswer
   * @return purchaseOrderAnswer
   */
  
  @Schema(name = "purchaseOrderAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderAnswer")
  public String getPurchaseOrderAnswer() {
    return purchaseOrderAnswer;
  }

  public void setPurchaseOrderAnswer(String purchaseOrderAnswer) {
    this.purchaseOrderAnswer = purchaseOrderAnswer;
  }

  public Business tethered(Boolean tethered) {
    this.tethered = tethered;
    return this;
  }

  /**
   * Get tethered
   * @return tethered
   */
  
  @Schema(name = "tethered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tethered")
  public Boolean getTethered() {
    return tethered;
  }

  public void setTethered(Boolean tethered) {
    this.tethered = tethered;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Business business = (Business) o;
    return Objects.equals(this.accessLevel, business.accessLevel) &&
        Objects.equals(this.awaitingApproval, business.awaitingApproval) &&
        Objects.equals(this.centralCard, business.centralCard) &&
        Objects.equals(this.customerReferenceAnswer, business.customerReferenceAnswer) &&
        Objects.equals(this.dismissMPILink, business.dismissMPILink) &&
        Objects.equals(this.employeeId, business.employeeId) &&
        Objects.equals(this.miSetupRequired, business.miSetupRequired) &&
        Objects.equals(this.myPILink, business.myPILink) &&
        Objects.equals(this.purchaseOrderAnswer, business.purchaseOrderAnswer) &&
        Objects.equals(this.tethered, business.tethered);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessLevel, awaitingApproval, centralCard, customerReferenceAnswer, dismissMPILink, employeeId, miSetupRequired, myPILink, purchaseOrderAnswer, tethered);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Business {\n");
    sb.append("    accessLevel: ").append(toIndentedString(accessLevel)).append("\n");
    sb.append("    awaitingApproval: ").append(toIndentedString(awaitingApproval)).append("\n");
    sb.append("    centralCard: ").append(toIndentedString(centralCard)).append("\n");
    sb.append("    customerReferenceAnswer: ").append(toIndentedString(customerReferenceAnswer)).append("\n");
    sb.append("    dismissMPILink: ").append(toIndentedString(dismissMPILink)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    miSetupRequired: ").append(toIndentedString(miSetupRequired)).append("\n");
    sb.append("    myPILink: ").append(toIndentedString(myPILink)).append("\n");
    sb.append("    purchaseOrderAnswer: ").append(toIndentedString(purchaseOrderAnswer)).append("\n");
    sb.append("    tethered: ").append(toIndentedString(tethered)).append("\n");
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

