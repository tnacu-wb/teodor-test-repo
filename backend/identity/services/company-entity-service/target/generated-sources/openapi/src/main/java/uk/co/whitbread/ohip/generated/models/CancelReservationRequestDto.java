package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.DepositFolioChargeDto;
import uk.co.whitbread.ohip.generated.models.ReservationOverrideReasonDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CancelReservationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelReservationRequestDto {

  @Valid
  private Map<String, List<@Valid DepositFolioChargeDto>> chargesByReservationIds = new HashMap<>();

  private String hotelId;

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

  private @Nullable PaymentOptionEnum paymentOption;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  private @Nullable ReservationOverrideReasonDto reservationOverrideReason;

  public CancelReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CancelReservationRequestDto(String hotelId, List<String> reservationIds) {
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public CancelReservationRequestDto chargesByReservationIds(Map<String, List<@Valid DepositFolioChargeDto>> chargesByReservationIds) {
    this.chargesByReservationIds = chargesByReservationIds;
    return this;
  }

  public CancelReservationRequestDto putChargesByReservationIdsItem(String key, List<@Valid DepositFolioChargeDto> chargesByReservationIdsItem) {
    if (this.chargesByReservationIds == null) {
      this.chargesByReservationIds = new HashMap<>();
    }
    this.chargesByReservationIds.put(key, chargesByReservationIdsItem);
    return this;
  }

  /**
   * Get chargesByReservationIds
   * @return chargesByReservationIds
   */
  @Valid 
  @Schema(name = "chargesByReservationIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chargesByReservationIds")
  public Map<String, List<@Valid DepositFolioChargeDto>> getChargesByReservationIds() {
    return chargesByReservationIds;
  }

  public void setChargesByReservationIds(Map<String, List<@Valid DepositFolioChargeDto>> chargesByReservationIds) {
    this.chargesByReservationIds = chargesByReservationIds;
  }

  public CancelReservationRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public CancelReservationRequestDto paymentOption(PaymentOptionEnum paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  
  @Schema(name = "paymentOption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOption")
  public PaymentOptionEnum getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(PaymentOptionEnum paymentOption) {
    this.paymentOption = paymentOption;
  }

  public CancelReservationRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public CancelReservationRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new ArrayList<>();
    }
    this.reservationIds.add(reservationIdsItem);
    return this;
  }

  /**
   * Get reservationIds
   * @return reservationIds
   */
  @NotNull 
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIds")
  public List<String> getReservationIds() {
    return reservationIds;
  }

  public void setReservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  public CancelReservationRequestDto reservationOverrideReason(ReservationOverrideReasonDto reservationOverrideReason) {
    this.reservationOverrideReason = reservationOverrideReason;
    return this;
  }

  /**
   * Get reservationOverrideReason
   * @return reservationOverrideReason
   */
  @Valid 
  @Schema(name = "reservationOverrideReason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationOverrideReason")
  public ReservationOverrideReasonDto getReservationOverrideReason() {
    return reservationOverrideReason;
  }

  public void setReservationOverrideReason(ReservationOverrideReasonDto reservationOverrideReason) {
    this.reservationOverrideReason = reservationOverrideReason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelReservationRequestDto cancelReservationRequestDto = (CancelReservationRequestDto) o;
    return Objects.equals(this.chargesByReservationIds, cancelReservationRequestDto.chargesByReservationIds) &&
        Objects.equals(this.hotelId, cancelReservationRequestDto.hotelId) &&
        Objects.equals(this.paymentOption, cancelReservationRequestDto.paymentOption) &&
        Objects.equals(this.reservationIds, cancelReservationRequestDto.reservationIds) &&
        Objects.equals(this.reservationOverrideReason, cancelReservationRequestDto.reservationOverrideReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chargesByReservationIds, hotelId, paymentOption, reservationIds, reservationOverrideReason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelReservationRequestDto {\n");
    sb.append("    chargesByReservationIds: ").append(toIndentedString(chargesByReservationIds)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
    sb.append("    reservationOverrideReason: ").append(toIndentedString(reservationOverrideReason)).append("\n");
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

