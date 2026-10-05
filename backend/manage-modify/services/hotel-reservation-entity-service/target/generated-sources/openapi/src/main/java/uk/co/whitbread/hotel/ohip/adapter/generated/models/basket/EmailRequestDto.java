package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.DepositsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EmailRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmailRequestDto {

  private String bookingReference;

  @Valid
  private List<@Valid DepositsDto> deposits = new ArrayList<>();

  private @Nullable String email;

  private @Nullable String emailRequestType;

  private @Nullable Boolean failedRefund;

  public EmailRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EmailRequestDto(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public EmailRequestDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public EmailRequestDto deposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
    return this;
  }

  public EmailRequestDto addDepositsItem(DepositsDto depositsItem) {
    if (this.deposits == null) {
      this.deposits = new ArrayList<>();
    }
    this.deposits.add(depositsItem);
    return this;
  }

  /**
   * Get deposits
   * @return deposits
   */
  @Valid 
  @Schema(name = "deposits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposits")
  public List<@Valid DepositsDto> getDeposits() {
    return deposits;
  }

  public void setDeposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
  }

  public EmailRequestDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public EmailRequestDto emailRequestType(String emailRequestType) {
    this.emailRequestType = emailRequestType;
    return this;
  }

  /**
   * Get emailRequestType
   * @return emailRequestType
   */
  
  @Schema(name = "emailRequestType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailRequestType")
  public String getEmailRequestType() {
    return emailRequestType;
  }

  public void setEmailRequestType(String emailRequestType) {
    this.emailRequestType = emailRequestType;
  }

  public EmailRequestDto failedRefund(Boolean failedRefund) {
    this.failedRefund = failedRefund;
    return this;
  }

  /**
   * Get failedRefund
   * @return failedRefund
   */
  
  @Schema(name = "failedRefund", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("failedRefund")
  public Boolean getFailedRefund() {
    return failedRefund;
  }

  public void setFailedRefund(Boolean failedRefund) {
    this.failedRefund = failedRefund;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailRequestDto emailRequestDto = (EmailRequestDto) o;
    return Objects.equals(this.bookingReference, emailRequestDto.bookingReference) &&
        Objects.equals(this.deposits, emailRequestDto.deposits) &&
        Objects.equals(this.email, emailRequestDto.email) &&
        Objects.equals(this.emailRequestType, emailRequestDto.emailRequestType) &&
        Objects.equals(this.failedRefund, emailRequestDto.failedRefund);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, deposits, email, emailRequestType, failedRefund);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailRequestDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    deposits: ").append(toIndentedString(deposits)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    emailRequestType: ").append(toIndentedString(emailRequestType)).append("\n");
    sb.append("    failedRefund: ").append(toIndentedString(failedRefund)).append("\n");
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

