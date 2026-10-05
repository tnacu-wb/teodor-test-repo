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
 * ProcessAmendRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProcessAmendRequestDto {

  private @Nullable String ccAgentId;

  private String channel;

  private @Nullable String emailAddress;

  private String language;

  private @Nullable String paymentOptionSelected;

  private @Nullable String token;

  public ProcessAmendRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProcessAmendRequestDto(String channel, String language) {
    this.channel = channel;
    this.language = language;
  }

  public ProcessAmendRequestDto ccAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
    return this;
  }

  /**
   * Get ccAgentId
   * @return ccAgentId
   */
  
  @Schema(name = "ccAgentId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccAgentId")
  public String getCcAgentId() {
    return ccAgentId;
  }

  public void setCcAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
  }

  public ProcessAmendRequestDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public ProcessAmendRequestDto emailAddress(String emailAddress) {
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

  public ProcessAmendRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  @NotNull 
  @Schema(name = "language", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public ProcessAmendRequestDto paymentOptionSelected(String paymentOptionSelected) {
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

  public ProcessAmendRequestDto token(String token) {
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
    ProcessAmendRequestDto processAmendRequestDto = (ProcessAmendRequestDto) o;
    return Objects.equals(this.ccAgentId, processAmendRequestDto.ccAgentId) &&
        Objects.equals(this.channel, processAmendRequestDto.channel) &&
        Objects.equals(this.emailAddress, processAmendRequestDto.emailAddress) &&
        Objects.equals(this.language, processAmendRequestDto.language) &&
        Objects.equals(this.paymentOptionSelected, processAmendRequestDto.paymentOptionSelected) &&
        Objects.equals(this.token, processAmendRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ccAgentId, channel, emailAddress, language, paymentOptionSelected, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessAmendRequestDto {\n");
    sb.append("    ccAgentId: ").append(toIndentedString(ccAgentId)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    paymentOptionSelected: ").append(toIndentedString(paymentOptionSelected)).append("\n");
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

