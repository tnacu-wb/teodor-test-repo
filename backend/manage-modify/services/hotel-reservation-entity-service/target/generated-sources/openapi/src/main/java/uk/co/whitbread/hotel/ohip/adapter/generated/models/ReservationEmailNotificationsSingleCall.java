package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * ReservationEmailNotificationsSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationEmailNotificationsSingleCall {

  private @Nullable Boolean sendEmailConfirmation;

  private @Nullable Boolean sendEmailInvoice;

  public ReservationEmailNotificationsSingleCall sendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
    return this;
  }

  /**
   * Get sendEmailConfirmation
   * @return sendEmailConfirmation
   */
  
  @Schema(name = "sendEmailConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailConfirmation")
  public Boolean getSendEmailConfirmation() {
    return sendEmailConfirmation;
  }

  public void setSendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
  }

  public ReservationEmailNotificationsSingleCall sendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
    return this;
  }

  /**
   * Get sendEmailInvoice
   * @return sendEmailInvoice
   */
  
  @Schema(name = "sendEmailInvoice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailInvoice")
  public Boolean getSendEmailInvoice() {
    return sendEmailInvoice;
  }

  public void setSendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationEmailNotificationsSingleCall reservationEmailNotificationsSingleCall = (ReservationEmailNotificationsSingleCall) o;
    return Objects.equals(this.sendEmailConfirmation, reservationEmailNotificationsSingleCall.sendEmailConfirmation) &&
        Objects.equals(this.sendEmailInvoice, reservationEmailNotificationsSingleCall.sendEmailInvoice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sendEmailConfirmation, sendEmailInvoice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationEmailNotificationsSingleCall {\n");
    sb.append("    sendEmailConfirmation: ").append(toIndentedString(sendEmailConfirmation)).append("\n");
    sb.append("    sendEmailInvoice: ").append(toIndentedString(sendEmailInvoice)).append("\n");
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

