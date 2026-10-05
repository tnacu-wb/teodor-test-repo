package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.AddressCcuiDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CardCcuiDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardCcuiDto {

  private AddressCcuiDto cardHolderAddress;

  private String cardHolderFirstName;

  private String cardHolderLastName;

  public CardCcuiDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CardCcuiDto(AddressCcuiDto cardHolderAddress, String cardHolderFirstName, String cardHolderLastName) {
    this.cardHolderAddress = cardHolderAddress;
    this.cardHolderFirstName = cardHolderFirstName;
    this.cardHolderLastName = cardHolderLastName;
  }

  public CardCcuiDto cardHolderAddress(AddressCcuiDto cardHolderAddress) {
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
  public AddressCcuiDto getCardHolderAddress() {
    return cardHolderAddress;
  }

  public void setCardHolderAddress(AddressCcuiDto cardHolderAddress) {
    this.cardHolderAddress = cardHolderAddress;
  }

  public CardCcuiDto cardHolderFirstName(String cardHolderFirstName) {
    this.cardHolderFirstName = cardHolderFirstName;
    return this;
  }

  /**
   * Get cardHolderFirstName
   * @return cardHolderFirstName
   */
  @NotNull 
  @Schema(name = "cardHolderFirstName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolderFirstName")
  public String getCardHolderFirstName() {
    return cardHolderFirstName;
  }

  public void setCardHolderFirstName(String cardHolderFirstName) {
    this.cardHolderFirstName = cardHolderFirstName;
  }

  public CardCcuiDto cardHolderLastName(String cardHolderLastName) {
    this.cardHolderLastName = cardHolderLastName;
    return this;
  }

  /**
   * Get cardHolderLastName
   * @return cardHolderLastName
   */
  @NotNull 
  @Schema(name = "cardHolderLastName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cardHolderLastName")
  public String getCardHolderLastName() {
    return cardHolderLastName;
  }

  public void setCardHolderLastName(String cardHolderLastName) {
    this.cardHolderLastName = cardHolderLastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardCcuiDto cardCcuiDto = (CardCcuiDto) o;
    return Objects.equals(this.cardHolderAddress, cardCcuiDto.cardHolderAddress) &&
        Objects.equals(this.cardHolderFirstName, cardCcuiDto.cardHolderFirstName) &&
        Objects.equals(this.cardHolderLastName, cardCcuiDto.cardHolderLastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardHolderAddress, cardHolderFirstName, cardHolderLastName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardCcuiDto {\n");
    sb.append("    cardHolderAddress: ").append(toIndentedString(cardHolderAddress)).append("\n");
    sb.append("    cardHolderFirstName: ").append(toIndentedString(cardHolderFirstName)).append("\n");
    sb.append("    cardHolderLastName: ").append(toIndentedString(cardHolderLastName)).append("\n");
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

