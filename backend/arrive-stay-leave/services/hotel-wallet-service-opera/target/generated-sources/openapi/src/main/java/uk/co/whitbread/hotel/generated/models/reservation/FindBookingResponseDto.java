package uk.co.whitbread.hotel.generated.models.reservation;

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
 * FindBookingResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class FindBookingResponseDto {

  private @Nullable String basketReference;

  private @Nullable String cookieName;

  private @Nullable String hotelId;

  private @Nullable String idContext;

  private @Nullable String minutesTillExpiry;

  private @Nullable String operaConfNumber;

  private @Nullable String redirectBase;

  private @Nullable String ref;

  private @Nullable String sourcePms;

  private @Nullable String token;

  public FindBookingResponseDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public FindBookingResponseDto cookieName(String cookieName) {
    this.cookieName = cookieName;
    return this;
  }

  /**
   * Get cookieName
   * @return cookieName
   */
  
  @Schema(name = "cookieName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookieName")
  public String getCookieName() {
    return cookieName;
  }

  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }

  public FindBookingResponseDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public FindBookingResponseDto idContext(String idContext) {
    this.idContext = idContext;
    return this;
  }

  /**
   * Get idContext
   * @return idContext
   */
  
  @Schema(name = "idContext", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idContext")
  public String getIdContext() {
    return idContext;
  }

  public void setIdContext(String idContext) {
    this.idContext = idContext;
  }

  public FindBookingResponseDto minutesTillExpiry(String minutesTillExpiry) {
    this.minutesTillExpiry = minutesTillExpiry;
    return this;
  }

  /**
   * Get minutesTillExpiry
   * @return minutesTillExpiry
   */
  
  @Schema(name = "minutesTillExpiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minutesTillExpiry")
  public String getMinutesTillExpiry() {
    return minutesTillExpiry;
  }

  public void setMinutesTillExpiry(String minutesTillExpiry) {
    this.minutesTillExpiry = minutesTillExpiry;
  }

  public FindBookingResponseDto operaConfNumber(String operaConfNumber) {
    this.operaConfNumber = operaConfNumber;
    return this;
  }

  /**
   * Get operaConfNumber
   * @return operaConfNumber
   */
  
  @Schema(name = "operaConfNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaConfNumber")
  public String getOperaConfNumber() {
    return operaConfNumber;
  }

  public void setOperaConfNumber(String operaConfNumber) {
    this.operaConfNumber = operaConfNumber;
  }

  public FindBookingResponseDto redirectBase(String redirectBase) {
    this.redirectBase = redirectBase;
    return this;
  }

  /**
   * Get redirectBase
   * @return redirectBase
   */
  
  @Schema(name = "redirectBase", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("redirectBase")
  public String getRedirectBase() {
    return redirectBase;
  }

  public void setRedirectBase(String redirectBase) {
    this.redirectBase = redirectBase;
  }

  public FindBookingResponseDto ref(String ref) {
    this.ref = ref;
    return this;
  }

  /**
   * Get ref
   * @return ref
   */
  
  @Schema(name = "ref", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ref")
  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public FindBookingResponseDto sourcePms(String sourcePms) {
    this.sourcePms = sourcePms;
    return this;
  }

  /**
   * Get sourcePms
   * @return sourcePms
   */
  
  @Schema(name = "sourcePms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourcePms")
  public String getSourcePms() {
    return sourcePms;
  }

  public void setSourcePms(String sourcePms) {
    this.sourcePms = sourcePms;
  }

  public FindBookingResponseDto token(String token) {
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
    FindBookingResponseDto findBookingResponseDto = (FindBookingResponseDto) o;
    return Objects.equals(this.basketReference, findBookingResponseDto.basketReference) &&
        Objects.equals(this.cookieName, findBookingResponseDto.cookieName) &&
        Objects.equals(this.hotelId, findBookingResponseDto.hotelId) &&
        Objects.equals(this.idContext, findBookingResponseDto.idContext) &&
        Objects.equals(this.minutesTillExpiry, findBookingResponseDto.minutesTillExpiry) &&
        Objects.equals(this.operaConfNumber, findBookingResponseDto.operaConfNumber) &&
        Objects.equals(this.redirectBase, findBookingResponseDto.redirectBase) &&
        Objects.equals(this.ref, findBookingResponseDto.ref) &&
        Objects.equals(this.sourcePms, findBookingResponseDto.sourcePms) &&
        Objects.equals(this.token, findBookingResponseDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketReference, cookieName, hotelId, idContext, minutesTillExpiry, operaConfNumber, redirectBase, ref, sourcePms, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FindBookingResponseDto {\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    cookieName: ").append(toIndentedString(cookieName)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    idContext: ").append(toIndentedString(idContext)).append("\n");
    sb.append("    minutesTillExpiry: ").append(toIndentedString(minutesTillExpiry)).append("\n");
    sb.append("    operaConfNumber: ").append(toIndentedString(operaConfNumber)).append("\n");
    sb.append("    redirectBase: ").append(toIndentedString(redirectBase)).append("\n");
    sb.append("    ref: ").append(toIndentedString(ref)).append("\n");
    sb.append("    sourcePms: ").append(toIndentedString(sourcePms)).append("\n");
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

