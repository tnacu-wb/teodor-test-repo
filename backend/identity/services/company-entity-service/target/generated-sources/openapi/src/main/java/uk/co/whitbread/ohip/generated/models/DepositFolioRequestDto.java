package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.DepositFolioChargeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositFolioRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositFolioRequestDto {

  @Valid
  private List<@Valid DepositFolioChargeDto> charges = new ArrayList<>();

  private String hotelId;

  private String paymentId;

  private String reservationId;

  public DepositFolioRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DepositFolioRequestDto(List<@Valid DepositFolioChargeDto> charges, String hotelId, String paymentId, String reservationId) {
    this.charges = charges;
    this.hotelId = hotelId;
    this.paymentId = paymentId;
    this.reservationId = reservationId;
  }

  public DepositFolioRequestDto charges(List<@Valid DepositFolioChargeDto> charges) {
    this.charges = charges;
    return this;
  }

  public DepositFolioRequestDto addChargesItem(DepositFolioChargeDto chargesItem) {
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
  @NotNull @Valid @Size(min = 1, max = 2147483647) 
  @Schema(name = "charges", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("charges")
  public List<@Valid DepositFolioChargeDto> getCharges() {
    return charges;
  }

  public void setCharges(List<@Valid DepositFolioChargeDto> charges) {
    this.charges = charges;
  }

  public DepositFolioRequestDto hotelId(String hotelId) {
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

  public DepositFolioRequestDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  @NotNull 
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public DepositFolioRequestDto reservationId(String reservationId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DepositFolioRequestDto depositFolioRequestDto = (DepositFolioRequestDto) o;
    return Objects.equals(this.charges, depositFolioRequestDto.charges) &&
        Objects.equals(this.hotelId, depositFolioRequestDto.hotelId) &&
        Objects.equals(this.paymentId, depositFolioRequestDto.paymentId) &&
        Objects.equals(this.reservationId, depositFolioRequestDto.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(charges, hotelId, paymentId, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositFolioRequestDto {\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
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

