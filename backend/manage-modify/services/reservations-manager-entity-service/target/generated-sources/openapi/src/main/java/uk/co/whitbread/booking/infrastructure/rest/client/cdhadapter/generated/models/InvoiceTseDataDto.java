package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

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
 * InvoiceTseDataDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceTseDataDto {

  private @Nullable String code;

  private @Nullable String endDateTime;

  private @Nullable String fn;

  private @Nullable String seqNo;

  private @Nullable String serial;

  private @Nullable String sign;

  private @Nullable String signCnt;

  private @Nullable String sq;

  private @Nullable String startDateTime;

  private @Nullable String tn;

  public InvoiceTseDataDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public InvoiceTseDataDto endDateTime(String endDateTime) {
    this.endDateTime = endDateTime;
    return this;
  }

  /**
   * Get endDateTime
   * @return endDateTime
   */
  
  @Schema(name = "endDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDateTime")
  public String getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(String endDateTime) {
    this.endDateTime = endDateTime;
  }

  public InvoiceTseDataDto fn(String fn) {
    this.fn = fn;
    return this;
  }

  /**
   * Get fn
   * @return fn
   */
  
  @Schema(name = "fn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fn")
  public String getFn() {
    return fn;
  }

  public void setFn(String fn) {
    this.fn = fn;
  }

  public InvoiceTseDataDto seqNo(String seqNo) {
    this.seqNo = seqNo;
    return this;
  }

  /**
   * Get seqNo
   * @return seqNo
   */
  
  @Schema(name = "seqNo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seqNo")
  public String getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public InvoiceTseDataDto serial(String serial) {
    this.serial = serial;
    return this;
  }

  /**
   * Get serial
   * @return serial
   */
  
  @Schema(name = "serial", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("serial")
  public String getSerial() {
    return serial;
  }

  public void setSerial(String serial) {
    this.serial = serial;
  }

  public InvoiceTseDataDto sign(String sign) {
    this.sign = sign;
    return this;
  }

  /**
   * Get sign
   * @return sign
   */
  
  @Schema(name = "sign", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sign")
  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public InvoiceTseDataDto signCnt(String signCnt) {
    this.signCnt = signCnt;
    return this;
  }

  /**
   * Get signCnt
   * @return signCnt
   */
  
  @Schema(name = "signCnt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signCnt")
  public String getSignCnt() {
    return signCnt;
  }

  public void setSignCnt(String signCnt) {
    this.signCnt = signCnt;
  }

  public InvoiceTseDataDto sq(String sq) {
    this.sq = sq;
    return this;
  }

  /**
   * Get sq
   * @return sq
   */
  
  @Schema(name = "sq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sq")
  public String getSq() {
    return sq;
  }

  public void setSq(String sq) {
    this.sq = sq;
  }

  public InvoiceTseDataDto startDateTime(String startDateTime) {
    this.startDateTime = startDateTime;
    return this;
  }

  /**
   * Get startDateTime
   * @return startDateTime
   */
  
  @Schema(name = "startDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDateTime")
  public String getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(String startDateTime) {
    this.startDateTime = startDateTime;
  }

  public InvoiceTseDataDto tn(String tn) {
    this.tn = tn;
    return this;
  }

  /**
   * Get tn
   * @return tn
   */
  
  @Schema(name = "tn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tn")
  public String getTn() {
    return tn;
  }

  public void setTn(String tn) {
    this.tn = tn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceTseDataDto invoiceTseDataDto = (InvoiceTseDataDto) o;
    return Objects.equals(this.code, invoiceTseDataDto.code) &&
        Objects.equals(this.endDateTime, invoiceTseDataDto.endDateTime) &&
        Objects.equals(this.fn, invoiceTseDataDto.fn) &&
        Objects.equals(this.seqNo, invoiceTseDataDto.seqNo) &&
        Objects.equals(this.serial, invoiceTseDataDto.serial) &&
        Objects.equals(this.sign, invoiceTseDataDto.sign) &&
        Objects.equals(this.signCnt, invoiceTseDataDto.signCnt) &&
        Objects.equals(this.sq, invoiceTseDataDto.sq) &&
        Objects.equals(this.startDateTime, invoiceTseDataDto.startDateTime) &&
        Objects.equals(this.tn, invoiceTseDataDto.tn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, endDateTime, fn, seqNo, serial, sign, signCnt, sq, startDateTime, tn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceTseDataDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    endDateTime: ").append(toIndentedString(endDateTime)).append("\n");
    sb.append("    fn: ").append(toIndentedString(fn)).append("\n");
    sb.append("    seqNo: ").append(toIndentedString(seqNo)).append("\n");
    sb.append("    serial: ").append(toIndentedString(serial)).append("\n");
    sb.append("    sign: ").append(toIndentedString(sign)).append("\n");
    sb.append("    signCnt: ").append(toIndentedString(signCnt)).append("\n");
    sb.append("    sq: ").append(toIndentedString(sq)).append("\n");
    sb.append("    startDateTime: ").append(toIndentedString(startDateTime)).append("\n");
    sb.append("    tn: ").append(toIndentedString(tn)).append("\n");
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

