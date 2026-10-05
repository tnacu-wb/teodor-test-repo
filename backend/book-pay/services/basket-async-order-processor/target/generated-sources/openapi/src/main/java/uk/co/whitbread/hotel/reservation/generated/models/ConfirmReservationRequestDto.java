package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.PaymentCardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmReservationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmReservationRequestDto {

  private String hotelId;

  private @Nullable PaymentCardDto paymentCard;

  private @Nullable String paymentId;

  private @Nullable String paymentMethod;

  /**
   * Gets or Sets paymentOption
   */
  public enum PaymentOptionEnum {
    PAY_NOW("PAY_NOW"),
    
    PAY_ON_ARRIVAL("PAY_ON_ARRIVAL"),
    
    RESERVE_WITHOUT_CARD("RESERVE_WITHOUT_CARD"),
    
    ACCOUNT_COMPANY("ACCOUNT_COMPANY");

    private String value;

    PaymentOptionEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PaymentOptionEnum fromValue(String value) {
      for (PaymentOptionEnum b : PaymentOptionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private PaymentOptionEnum paymentOption;

  private @Nullable String paymentType;

  private String reservationId;

  private @Nullable String ccAgentId;

  private @Nullable String threeDSIndicator;

  public ConfirmReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConfirmReservationRequestDto(String hotelId, PaymentOptionEnum paymentOption, String reservationId) {
    this.hotelId = hotelId;
    this.paymentOption = paymentOption;
    this.reservationId = reservationId;
  }

  public ConfirmReservationRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ConfirmReservationRequestDto paymentCard(PaymentCardDto paymentCard) {
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
  public PaymentCardDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  public ConfirmReservationRequestDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public ConfirmReservationRequestDto paymentMethod(String paymentMethod) {
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

  public ConfirmReservationRequestDto paymentOption(PaymentOptionEnum paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  @NotNull 
  @Schema(name = "paymentOption", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("paymentOption")
  public PaymentOptionEnum getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(PaymentOptionEnum paymentOption) {
    this.paymentOption = paymentOption;
  }

  public ConfirmReservationRequestDto paymentType(String paymentType) {
    this.paymentType = paymentType;
    return this;
  }

  /**
   * Get paymentType
   * @return paymentType
   */
  
  @Schema(name = "paymentType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentType")
  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public ConfirmReservationRequestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  @NotNull 
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public ConfirmReservationRequestDto ccAgentId(String ccAgentId) {
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

  public ConfirmReservationRequestDto threeDSIndicator(String threeDSIndicator) {
    this.threeDSIndicator = threeDSIndicator;
    return this;
  }

  /**
   * Get threeDSIndicator
   * @return threeDSIndicator
   */
  
  @Schema(name = "threeDSIndicator", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("threeDSIndicator")
  public String getThreeDSIndicator() {
    return threeDSIndicator;
  }

  public void setThreeDSIndicator(String threeDSIndicator) {
    this.threeDSIndicator = threeDSIndicator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmReservationRequestDto confirmReservationRequestDto = (ConfirmReservationRequestDto) o;
    return Objects.equals(this.hotelId, confirmReservationRequestDto.hotelId) &&
        Objects.equals(this.paymentCard, confirmReservationRequestDto.paymentCard) &&
        Objects.equals(this.paymentId, confirmReservationRequestDto.paymentId) &&
        Objects.equals(this.paymentMethod, confirmReservationRequestDto.paymentMethod) &&
        Objects.equals(this.paymentOption, confirmReservationRequestDto.paymentOption) &&
        Objects.equals(this.paymentType, confirmReservationRequestDto.paymentType) &&
        Objects.equals(this.reservationId, confirmReservationRequestDto.reservationId) &&
        Objects.equals(this.ccAgentId, confirmReservationRequestDto.ccAgentId) &&
        Objects.equals(this.threeDSIndicator, confirmReservationRequestDto.threeDSIndicator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, paymentCard, paymentId, paymentMethod, paymentOption, paymentType, reservationId, ccAgentId, threeDSIndicator);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmReservationRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    paymentType: ").append(toIndentedString(paymentType)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    ccAgentId: ").append(toIndentedString(ccAgentId)).append("\n");
    sb.append("    threeDSIndicator: ").append(toIndentedString(threeDSIndicator)).append("\n");
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

