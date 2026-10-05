package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.PaymentCardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentDetailsDto {

  private @Nullable Boolean allowIndividualCards;

  @Valid
  private List<@Valid PaymentCardDto> paymentCards = new ArrayList<>();

  private @Nullable Boolean profileLocked;

  public PaymentDetailsDto allowIndividualCards(Boolean allowIndividualCards) {
    this.allowIndividualCards = allowIndividualCards;
    return this;
  }

  /**
   * Get allowIndividualCards
   * @return allowIndividualCards
   */
  
  @Schema(name = "allowIndividualCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowIndividualCards")
  public Boolean getAllowIndividualCards() {
    return allowIndividualCards;
  }

  public void setAllowIndividualCards(Boolean allowIndividualCards) {
    this.allowIndividualCards = allowIndividualCards;
  }

  public PaymentDetailsDto paymentCards(List<@Valid PaymentCardDto> paymentCards) {
    this.paymentCards = paymentCards;
    return this;
  }

  public PaymentDetailsDto addPaymentCardsItem(PaymentCardDto paymentCardsItem) {
    if (this.paymentCards == null) {
      this.paymentCards = new ArrayList<>();
    }
    this.paymentCards.add(paymentCardsItem);
    return this;
  }

  /**
   * Get paymentCards
   * @return paymentCards
   */
  @Valid 
  @Schema(name = "paymentCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCards")
  public List<@Valid PaymentCardDto> getPaymentCards() {
    return paymentCards;
  }

  public void setPaymentCards(List<@Valid PaymentCardDto> paymentCards) {
    this.paymentCards = paymentCards;
  }

  public PaymentDetailsDto profileLocked(Boolean profileLocked) {
    this.profileLocked = profileLocked;
    return this;
  }

  /**
   * Get profileLocked
   * @return profileLocked
   */
  
  @Schema(name = "profileLocked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileLocked")
  public Boolean getProfileLocked() {
    return profileLocked;
  }

  public void setProfileLocked(Boolean profileLocked) {
    this.profileLocked = profileLocked;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentDetailsDto paymentDetailsDto = (PaymentDetailsDto) o;
    return Objects.equals(this.allowIndividualCards, paymentDetailsDto.allowIndividualCards) &&
        Objects.equals(this.paymentCards, paymentDetailsDto.paymentCards) &&
        Objects.equals(this.profileLocked, paymentDetailsDto.profileLocked);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowIndividualCards, paymentCards, profileLocked);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentDetailsDto {\n");
    sb.append("    allowIndividualCards: ").append(toIndentedString(allowIndividualCards)).append("\n");
    sb.append("    paymentCards: ").append(toIndentedString(paymentCards)).append("\n");
    sb.append("    profileLocked: ").append(toIndentedString(profileLocked)).append("\n");
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

