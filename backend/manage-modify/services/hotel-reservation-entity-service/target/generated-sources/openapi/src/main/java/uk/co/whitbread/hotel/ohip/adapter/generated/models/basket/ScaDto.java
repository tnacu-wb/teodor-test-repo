package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * ScaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ScaDto {

  private @Nullable Float authorisedAmount;

  private @Nullable String captureMethod;

  private @Nullable Integer cavv;

  private @Nullable String dsTransactionID;

  private @Nullable Integer eci;

  private @Nullable String protocolVersion;

  private @Nullable String transStatus;

  private @Nullable String xid;

  public ScaDto authorisedAmount(Float authorisedAmount) {
    this.authorisedAmount = authorisedAmount;
    return this;
  }

  /**
   * Get authorisedAmount
   * @return authorisedAmount
   */
  
  @Schema(name = "authorisedAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("authorisedAmount")
  public Float getAuthorisedAmount() {
    return authorisedAmount;
  }

  public void setAuthorisedAmount(Float authorisedAmount) {
    this.authorisedAmount = authorisedAmount;
  }

  public ScaDto captureMethod(String captureMethod) {
    this.captureMethod = captureMethod;
    return this;
  }

  /**
   * Get captureMethod
   * @return captureMethod
   */
  
  @Schema(name = "captureMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("captureMethod")
  public String getCaptureMethod() {
    return captureMethod;
  }

  public void setCaptureMethod(String captureMethod) {
    this.captureMethod = captureMethod;
  }

  public ScaDto cavv(Integer cavv) {
    this.cavv = cavv;
    return this;
  }

  /**
   * Get cavv
   * @return cavv
   */
  
  @Schema(name = "cavv", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cavv")
  public Integer getCavv() {
    return cavv;
  }

  public void setCavv(Integer cavv) {
    this.cavv = cavv;
  }

  public ScaDto dsTransactionID(String dsTransactionID) {
    this.dsTransactionID = dsTransactionID;
    return this;
  }

  /**
   * Get dsTransactionID
   * @return dsTransactionID
   */
  
  @Schema(name = "dsTransactionID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dsTransactionID")
  public String getDsTransactionID() {
    return dsTransactionID;
  }

  public void setDsTransactionID(String dsTransactionID) {
    this.dsTransactionID = dsTransactionID;
  }

  public ScaDto eci(Integer eci) {
    this.eci = eci;
    return this;
  }

  /**
   * Get eci
   * @return eci
   */
  
  @Schema(name = "eci", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eci")
  public Integer getEci() {
    return eci;
  }

  public void setEci(Integer eci) {
    this.eci = eci;
  }

  public ScaDto protocolVersion(String protocolVersion) {
    this.protocolVersion = protocolVersion;
    return this;
  }

  /**
   * Get protocolVersion
   * @return protocolVersion
   */
  
  @Schema(name = "protocolVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("protocolVersion")
  public String getProtocolVersion() {
    return protocolVersion;
  }

  public void setProtocolVersion(String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public ScaDto transStatus(String transStatus) {
    this.transStatus = transStatus;
    return this;
  }

  /**
   * Get transStatus
   * @return transStatus
   */
  
  @Schema(name = "transStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transStatus")
  public String getTransStatus() {
    return transStatus;
  }

  public void setTransStatus(String transStatus) {
    this.transStatus = transStatus;
  }

  public ScaDto xid(String xid) {
    this.xid = xid;
    return this;
  }

  /**
   * Get xid
   * @return xid
   */
  
  @Schema(name = "xid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("xid")
  public String getXid() {
    return xid;
  }

  public void setXid(String xid) {
    this.xid = xid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScaDto scaDto = (ScaDto) o;
    return Objects.equals(this.authorisedAmount, scaDto.authorisedAmount) &&
        Objects.equals(this.captureMethod, scaDto.captureMethod) &&
        Objects.equals(this.cavv, scaDto.cavv) &&
        Objects.equals(this.dsTransactionID, scaDto.dsTransactionID) &&
        Objects.equals(this.eci, scaDto.eci) &&
        Objects.equals(this.protocolVersion, scaDto.protocolVersion) &&
        Objects.equals(this.transStatus, scaDto.transStatus) &&
        Objects.equals(this.xid, scaDto.xid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authorisedAmount, captureMethod, cavv, dsTransactionID, eci, protocolVersion, transStatus, xid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ScaDto {\n");
    sb.append("    authorisedAmount: ").append(toIndentedString(authorisedAmount)).append("\n");
    sb.append("    captureMethod: ").append(toIndentedString(captureMethod)).append("\n");
    sb.append("    cavv: ").append(toIndentedString(cavv)).append("\n");
    sb.append("    dsTransactionID: ").append(toIndentedString(dsTransactionID)).append("\n");
    sb.append("    eci: ").append(toIndentedString(eci)).append("\n");
    sb.append("    protocolVersion: ").append(toIndentedString(protocolVersion)).append("\n");
    sb.append("    transStatus: ").append(toIndentedString(transStatus)).append("\n");
    sb.append("    xid: ").append(toIndentedString(xid)).append("\n");
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

