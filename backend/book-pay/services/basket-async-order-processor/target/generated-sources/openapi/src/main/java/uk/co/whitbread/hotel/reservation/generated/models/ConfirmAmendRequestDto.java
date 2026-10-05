package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.BookingChannelDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmAmendRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmAmendRequestDto {

  private BookingChannelDto bookingChannel;

  private String originalBookingRef;

  private String tempBookingRef;

  private String token;

  private @Nullable String paymentOptionSelected;

  private @Nullable String emailAddress;

  private @Nullable String ccAgentId;

  public ConfirmAmendRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConfirmAmendRequestDto(BookingChannelDto bookingChannel, String originalBookingRef, String tempBookingRef, String token) {
    this.bookingChannel = bookingChannel;
    this.originalBookingRef = originalBookingRef;
    this.tempBookingRef = tempBookingRef;
    this.token = token;
  }

  public ConfirmAmendRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public ConfirmAmendRequestDto originalBookingRef(String originalBookingRef) {
    this.originalBookingRef = originalBookingRef;
    return this;
  }

  /**
   * Get originalBookingRef
   * @return originalBookingRef
   */
  @NotNull 
  @Schema(name = "originalBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("originalBookingRef")
  public String getOriginalBookingRef() {
    return originalBookingRef;
  }

  public void setOriginalBookingRef(String originalBookingRef) {
    this.originalBookingRef = originalBookingRef;
  }

  public ConfirmAmendRequestDto tempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
    return this;
  }

  /**
   * Get tempBookingRef
   * @return tempBookingRef
   */
  @NotNull 
  @Schema(name = "tempBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tempBookingRef")
  public String getTempBookingRef() {
    return tempBookingRef;
  }

  public void setTempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
  }

  public ConfirmAmendRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  @NotNull 
  @Schema(name = "token", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public ConfirmAmendRequestDto paymentOptionSelected(String paymentOptionSelected) {
    this.paymentOptionSelected = paymentOptionSelected;
    return this;
  }

  /**
   * Get paymentOptionSelected
   * @return paymentOptionSelected
   */
  
  @Schema(name = "paymentOptionSelected", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOptionSelected")
  public String getPaymentOptionSelected() {
    return paymentOptionSelected;
  }

  public void setPaymentOptionSelected(String paymentOptionSelected) {
    this.paymentOptionSelected = paymentOptionSelected;
  }

  public ConfirmAmendRequestDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public ConfirmAmendRequestDto ccAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
    return this;
  }

  /**
   * Get ccAgentId
   * @return ccAgentId
   */
  
  @Schema(name = "ccAgentId", example = "jane.doe@wb.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccAgentId")
  public String getCcAgentId() {
    return ccAgentId;
  }

  public void setCcAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmAmendRequestDto confirmAmendRequestDto = (ConfirmAmendRequestDto) o;
    return Objects.equals(this.bookingChannel, confirmAmendRequestDto.bookingChannel) &&
        Objects.equals(this.originalBookingRef, confirmAmendRequestDto.originalBookingRef) &&
        Objects.equals(this.tempBookingRef, confirmAmendRequestDto.tempBookingRef) &&
        Objects.equals(this.token, confirmAmendRequestDto.token) &&
        Objects.equals(this.paymentOptionSelected, confirmAmendRequestDto.paymentOptionSelected) &&
        Objects.equals(this.emailAddress, confirmAmendRequestDto.emailAddress) &&
        Objects.equals(this.ccAgentId, confirmAmendRequestDto.ccAgentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, originalBookingRef, tempBookingRef, token, paymentOptionSelected, emailAddress, ccAgentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmAmendRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    originalBookingRef: ").append(toIndentedString(originalBookingRef)).append("\n");
    sb.append("    tempBookingRef: ").append(toIndentedString(tempBookingRef)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    paymentOptionSelected: ").append(toIndentedString(paymentOptionSelected)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    ccAgentId: ").append(toIndentedString(ccAgentId)).append("\n");
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

