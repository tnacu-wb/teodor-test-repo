package uk.co.whitbread.hotel.account.service.generated.models;

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
 * ResetMemorableWordRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ResetMemorableWordRequest {

  private String companyId;

  private String employeeId;

  private String ipAddress;

  private String memorableWord;

  private String secret;

  private String worldlineSession;

  public ResetMemorableWordRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ResetMemorableWordRequest(String companyId, String employeeId, String ipAddress, String memorableWord, String secret, String worldlineSession) {
    this.companyId = companyId;
    this.employeeId = employeeId;
    this.ipAddress = ipAddress;
    this.memorableWord = memorableWord;
    this.secret = secret;
    this.worldlineSession = worldlineSession;
  }

  public ResetMemorableWordRequest companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  @NotNull 
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public ResetMemorableWordRequest employeeId(String employeeId) {
    this.employeeId = employeeId;
    return this;
  }

  /**
   * Get employeeId
   * @return employeeId
   */
  @NotNull 
  @Schema(name = "employeeId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("employeeId")
  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public ResetMemorableWordRequest ipAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  /**
   * Get ipAddress
   * @return ipAddress
   */
  @NotNull 
  @Schema(name = "ipAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ipAddress")
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public ResetMemorableWordRequest memorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
    return this;
  }

  /**
   * Get memorableWord
   * @return memorableWord
   */
  @NotNull 
  @Schema(name = "memorableWord", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("memorableWord")
  public String getMemorableWord() {
    return memorableWord;
  }

  public void setMemorableWord(String memorableWord) {
    this.memorableWord = memorableWord;
  }

  public ResetMemorableWordRequest secret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * Get secret
   * @return secret
   */
  @NotNull 
  @Schema(name = "secret", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("secret")
  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public ResetMemorableWordRequest worldlineSession(String worldlineSession) {
    this.worldlineSession = worldlineSession;
    return this;
  }

  /**
   * Get worldlineSession
   * @return worldlineSession
   */
  @NotNull 
  @Schema(name = "worldlineSession", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("worldlineSession")
  public String getWorldlineSession() {
    return worldlineSession;
  }

  public void setWorldlineSession(String worldlineSession) {
    this.worldlineSession = worldlineSession;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResetMemorableWordRequest resetMemorableWordRequest = (ResetMemorableWordRequest) o;
    return Objects.equals(this.companyId, resetMemorableWordRequest.companyId) &&
        Objects.equals(this.employeeId, resetMemorableWordRequest.employeeId) &&
        Objects.equals(this.ipAddress, resetMemorableWordRequest.ipAddress) &&
        Objects.equals(this.memorableWord, resetMemorableWordRequest.memorableWord) &&
        Objects.equals(this.secret, resetMemorableWordRequest.secret) &&
        Objects.equals(this.worldlineSession, resetMemorableWordRequest.worldlineSession);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyId, employeeId, ipAddress, memorableWord, secret, worldlineSession);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResetMemorableWordRequest {\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    ipAddress: ").append(toIndentedString(ipAddress)).append("\n");
    sb.append("    memorableWord: ").append(toIndentedString(memorableWord)).append("\n");
    sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
    sb.append("    worldlineSession: ").append(toIndentedString(worldlineSession)).append("\n");
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

