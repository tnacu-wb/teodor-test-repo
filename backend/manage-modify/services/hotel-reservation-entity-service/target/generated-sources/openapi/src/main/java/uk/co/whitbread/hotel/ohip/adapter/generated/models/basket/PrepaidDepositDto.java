package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChargeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PrepaidDepositDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PrepaidDepositDto {

  @Valid
  private List<@Valid ChargeDto> charges = new ArrayList<>();

  private @Nullable Long paymentNo;

  private @Nullable String reservationId;

  public PrepaidDepositDto charges(List<@Valid ChargeDto> charges) {
    this.charges = charges;
    return this;
  }

  public PrepaidDepositDto addChargesItem(ChargeDto chargesItem) {
    if (this.charges == null) {
      this.charges = new ArrayList<>();
    }
    this.charges.add(chargesItem);
    return this;
  }

  /**
   * Get charges
   * @return charges
   */
  @Valid 
  @Schema(name = "charges", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("charges")
  public List<@Valid ChargeDto> getCharges() {
    return charges;
  }

  public void setCharges(List<@Valid ChargeDto> charges) {
    this.charges = charges;
  }

  public PrepaidDepositDto paymentNo(Long paymentNo) {
    this.paymentNo = paymentNo;
    return this;
  }

  /**
   * Get paymentNo
   * @return paymentNo
   */
  
  @Schema(name = "paymentNo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentNo")
  public Long getPaymentNo() {
    return paymentNo;
  }

  public void setPaymentNo(Long paymentNo) {
    this.paymentNo = paymentNo;
  }

  public PrepaidDepositDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrepaidDepositDto prepaidDepositDto = (PrepaidDepositDto) o;
    return Objects.equals(this.charges, prepaidDepositDto.charges) &&
        Objects.equals(this.paymentNo, prepaidDepositDto.paymentNo) &&
        Objects.equals(this.reservationId, prepaidDepositDto.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(charges, paymentNo, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrepaidDepositDto {\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    paymentNo: ").append(toIndentedString(paymentNo)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
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

