package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateTokenRequestDto
 */

@JsonTypeName("UpdateTokenRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateTokenRequestDto {

  private AddressDto cardHolderAddress;

  private String cardHolderFirstName;

  private String cardHolderLastName;

  private String requestId;

  private String token;

  public UpdateTokenRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateTokenRequestDto(AddressDto cardHolderAddress, String cardHolderFirstName, String cardHolderLastName, String requestId, String token) {
    this.cardHolderAddress = cardHolderAddress;
    this.cardHolderFirstName = cardHolderFirstName;
    this.cardHolderLastName = cardHolderLastName;
    this.requestId = requestId;
    this.token = token;
  }

  public UpdateTokenRequestDto cardHolderAddress(AddressDto cardHolderAddress) {
    this.cardHolderAddress = cardHolderAddress;
    return this;
  }

  /**
   * Get cardHolderAddress
   * @return cardHolderAddress
   */
  @NotNull @Valid 
  @Schema(name = "cardHolderAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolderAddress")
  public AddressDto getCardHolderAddress() {
    return cardHolderAddress;
  }

  public void setCardHolderAddress(AddressDto cardHolderAddress) {
    this.cardHolderAddress = cardHolderAddress;
  }

  public UpdateTokenRequestDto cardHolderFirstName(String cardHolderFirstName) {
    this.cardHolderFirstName = cardHolderFirstName;
    return this;
  }

  /**
   * Card holder first name
   * @return cardHolderFirstName
   */
  @NotNull 
  @Schema(name = "cardHolderFirstName", description = "Card holder first name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolderFirstName")
  public String getCardHolderFirstName() {
    return cardHolderFirstName;
  }

  public void setCardHolderFirstName(String cardHolderFirstName) {
    this.cardHolderFirstName = cardHolderFirstName;
  }

  public UpdateTokenRequestDto cardHolderLastName(String cardHolderLastName) {
    this.cardHolderLastName = cardHolderLastName;
    return this;
  }

  /**
   * Card holder last name
   * @return cardHolderLastName
   */
  @NotNull 
  @Schema(name = "cardHolderLastName", description = "Card holder last name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolderLastName")
  public String getCardHolderLastName() {
    return cardHolderLastName;
  }

  public void setCardHolderLastName(String cardHolderLastName) {
    this.cardHolderLastName = cardHolderLastName;
  }

  public UpdateTokenRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Unique reference for transaction provided by consumer.
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", example = "a0a9f782-98ee-468c-9839-30c487c832a3", description = "Unique reference for transaction provided by consumer.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public UpdateTokenRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Token number
   * @return token
   */
  @NotNull 
  @Schema(name = "token", description = "Token number", requiredMode = Schema.RequiredMode.REQUIRED)
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
    UpdateTokenRequestDto updateTokenRequest = (UpdateTokenRequestDto) o;
    return Objects.equals(this.cardHolderAddress, updateTokenRequest.cardHolderAddress) &&
        Objects.equals(this.cardHolderFirstName, updateTokenRequest.cardHolderFirstName) &&
        Objects.equals(this.cardHolderLastName, updateTokenRequest.cardHolderLastName) &&
        Objects.equals(this.requestId, updateTokenRequest.requestId) &&
        Objects.equals(this.token, updateTokenRequest.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderAddress, cardHolderFirstName, cardHolderLastName, requestId, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateTokenRequestDto {\n");
    sb.append("    cardHolderAddress: ").append(toIndentedString(cardHolderAddress)).append("\n");
    sb.append("    cardHolderFirstName: ").append(toIndentedString(cardHolderFirstName)).append("\n");
    sb.append("    cardHolderLastName: ").append(toIndentedString(cardHolderLastName)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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

