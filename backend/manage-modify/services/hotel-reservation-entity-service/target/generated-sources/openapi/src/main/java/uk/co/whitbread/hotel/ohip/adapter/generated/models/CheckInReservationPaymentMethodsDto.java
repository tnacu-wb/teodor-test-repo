package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailFolioInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskPaymentCardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CheckInReservationPaymentMethodsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInReservationPaymentMethodsDto {

  private @Nullable String description;

  private @Nullable EmailFolioInfoDto emailFolioInfo;

  private @Nullable Integer folioView;

  private @Nullable KioskPaymentCardDto paymentCard;

  private @Nullable String paymentMethod;

  public CheckInReservationPaymentMethodsDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CheckInReservationPaymentMethodsDto emailFolioInfo(EmailFolioInfoDto emailFolioInfo) {
    this.emailFolioInfo = emailFolioInfo;
    return this;
  }

  /**
   * Get emailFolioInfo
   * @return emailFolioInfo
   */
  @Valid 
  @Schema(name = "emailFolioInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailFolioInfo")
  public EmailFolioInfoDto getEmailFolioInfo() {
    return emailFolioInfo;
  }

  public void setEmailFolioInfo(EmailFolioInfoDto emailFolioInfo) {
    this.emailFolioInfo = emailFolioInfo;
  }

  public CheckInReservationPaymentMethodsDto folioView(Integer folioView) {
    this.folioView = folioView;
    return this;
  }

  /**
   * Get folioView
   * @return folioView
   */
  
  @Schema(name = "folioView", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("folioView")
  public Integer getFolioView() {
    return folioView;
  }

  public void setFolioView(Integer folioView) {
    this.folioView = folioView;
  }

  public CheckInReservationPaymentMethodsDto paymentCard(KioskPaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
    return this;
  }

  /**
   * Get paymentCard
   * @return paymentCard
   */
  @Valid 
  @Schema(name = "paymentCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCard")
  public KioskPaymentCardDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(KioskPaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  public CheckInReservationPaymentMethodsDto paymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  /**
   * Get paymentMethod
   * @return paymentMethod
   */
  
  @Schema(name = "paymentMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethod")
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInReservationPaymentMethodsDto checkInReservationPaymentMethodsDto = (CheckInReservationPaymentMethodsDto) o;
    return Objects.equals(this.description, checkInReservationPaymentMethodsDto.description) &&
        Objects.equals(this.emailFolioInfo, checkInReservationPaymentMethodsDto.emailFolioInfo) &&
        Objects.equals(this.folioView, checkInReservationPaymentMethodsDto.folioView) &&
        Objects.equals(this.paymentCard, checkInReservationPaymentMethodsDto.paymentCard) &&
        Objects.equals(this.paymentMethod, checkInReservationPaymentMethodsDto.paymentMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, emailFolioInfo, folioView, paymentCard, paymentMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInReservationPaymentMethodsDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    emailFolioInfo: ").append(toIndentedString(emailFolioInfo)).append("\n");
    sb.append("    folioView: ").append(toIndentedString(folioView)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
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

