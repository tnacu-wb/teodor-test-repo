package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChannelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ReservationBasketInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UserInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateBasketRequestReservationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CreateBasketRequestReservationDto {

  private @Nullable BasketItemInfoDto basketItemInfoDto;

  /**
   * Gets or Sets basketStatus
   */
  public enum BasketStatusEnum {
    OPEN("OPEN"),
    
    PROCESSING("PROCESSING"),
    
    AMENDING("AMENDING"),
    
    COMPLETED("COMPLETED"),
    
    AMENDED("AMENDED"),
    
    PRE_CHECKED_IN("PRE_CHECKED_IN"),
    
    CANCELLED("CANCELLED"),
    
    PAY_PENDING("PAY_PENDING"),
    
    AMEND_FAILED("AMEND_FAILED"),
    
    FAILED("FAILED"),
    
    CIOL_FAILED("CIOL_FAILED"),
    
    PRE_CHECKED_OUT("PRE_CHECKED_OUT"),
    
    CIOL_RC_FAILED("CIOL_RC_FAILED"),
    
    SECURE_FAILED("SECURE_FAILED");

    private String value;

    BasketStatusEnum(String value) {
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
    public static BasketStatusEnum fromValue(String value) {
      for (BasketStatusEnum b : BasketStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable BasketStatusEnum basketStatus;

  private @Nullable ChannelInfoDto channelInfoDto;

  private @Nullable String hotelId;

  private @Nullable PaymentInfoDto paymentInfoDto;

  private @Nullable ReservationBasketInfoDto reservationBasketInfoDto;

  private @Nullable UserInfoDto userInfoDto;

  public CreateBasketRequestReservationDto basketItemInfoDto(BasketItemInfoDto basketItemInfoDto) {
    this.basketItemInfoDto = basketItemInfoDto;
    return this;
  }

  /**
   * Get basketItemInfoDto
   * @return basketItemInfoDto
   */
  @Valid 
  @Schema(name = "basketItemInfoDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketItemInfoDto")
  public BasketItemInfoDto getBasketItemInfoDto() {
    return basketItemInfoDto;
  }

  public void setBasketItemInfoDto(BasketItemInfoDto basketItemInfoDto) {
    this.basketItemInfoDto = basketItemInfoDto;
  }

  public CreateBasketRequestReservationDto basketStatus(BasketStatusEnum basketStatus) {
    this.basketStatus = basketStatus;
    return this;
  }

  /**
   * Get basketStatus
   * @return basketStatus
   */
  
  @Schema(name = "basketStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketStatus")
  public BasketStatusEnum getBasketStatus() {
    return basketStatus;
  }

  public void setBasketStatus(BasketStatusEnum basketStatus) {
    this.basketStatus = basketStatus;
  }

  public CreateBasketRequestReservationDto channelInfoDto(ChannelInfoDto channelInfoDto) {
    this.channelInfoDto = channelInfoDto;
    return this;
  }

  /**
   * Get channelInfoDto
   * @return channelInfoDto
   */
  @Valid 
  @Schema(name = "channelInfoDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channelInfoDto")
  public ChannelInfoDto getChannelInfoDto() {
    return channelInfoDto;
  }

  public void setChannelInfoDto(ChannelInfoDto channelInfoDto) {
    this.channelInfoDto = channelInfoDto;
  }

  public CreateBasketRequestReservationDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public CreateBasketRequestReservationDto paymentInfoDto(PaymentInfoDto paymentInfoDto) {
    this.paymentInfoDto = paymentInfoDto;
    return this;
  }

  /**
   * Get paymentInfoDto
   * @return paymentInfoDto
   */
  @Valid 
  @Schema(name = "paymentInfoDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentInfoDto")
  public PaymentInfoDto getPaymentInfoDto() {
    return paymentInfoDto;
  }

  public void setPaymentInfoDto(PaymentInfoDto paymentInfoDto) {
    this.paymentInfoDto = paymentInfoDto;
  }

  public CreateBasketRequestReservationDto reservationBasketInfoDto(ReservationBasketInfoDto reservationBasketInfoDto) {
    this.reservationBasketInfoDto = reservationBasketInfoDto;
    return this;
  }

  /**
   * Get reservationBasketInfoDto
   * @return reservationBasketInfoDto
   */
  @Valid 
  @Schema(name = "reservationBasketInfoDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationBasketInfoDto")
  public ReservationBasketInfoDto getReservationBasketInfoDto() {
    return reservationBasketInfoDto;
  }

  public void setReservationBasketInfoDto(ReservationBasketInfoDto reservationBasketInfoDto) {
    this.reservationBasketInfoDto = reservationBasketInfoDto;
  }

  public CreateBasketRequestReservationDto userInfoDto(UserInfoDto userInfoDto) {
    this.userInfoDto = userInfoDto;
    return this;
  }

  /**
   * Get userInfoDto
   * @return userInfoDto
   */
  @Valid 
  @Schema(name = "userInfoDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userInfoDto")
  public UserInfoDto getUserInfoDto() {
    return userInfoDto;
  }

  public void setUserInfoDto(UserInfoDto userInfoDto) {
    this.userInfoDto = userInfoDto;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBasketRequestReservationDto createBasketRequestReservationDto = (CreateBasketRequestReservationDto) o;
    return Objects.equals(this.basketItemInfoDto, createBasketRequestReservationDto.basketItemInfoDto) &&
        Objects.equals(this.basketStatus, createBasketRequestReservationDto.basketStatus) &&
        Objects.equals(this.channelInfoDto, createBasketRequestReservationDto.channelInfoDto) &&
        Objects.equals(this.hotelId, createBasketRequestReservationDto.hotelId) &&
        Objects.equals(this.paymentInfoDto, createBasketRequestReservationDto.paymentInfoDto) &&
        Objects.equals(this.reservationBasketInfoDto, createBasketRequestReservationDto.reservationBasketInfoDto) &&
        Objects.equals(this.userInfoDto, createBasketRequestReservationDto.userInfoDto);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketItemInfoDto, basketStatus, channelInfoDto, hotelId, paymentInfoDto, reservationBasketInfoDto, userInfoDto);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateBasketRequestReservationDto {\n");
    sb.append("    basketItemInfoDto: ").append(toIndentedString(basketItemInfoDto)).append("\n");
    sb.append("    basketStatus: ").append(toIndentedString(basketStatus)).append("\n");
    sb.append("    channelInfoDto: ").append(toIndentedString(channelInfoDto)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    paymentInfoDto: ").append(toIndentedString(paymentInfoDto)).append("\n");
    sb.append("    reservationBasketInfoDto: ").append(toIndentedString(reservationBasketInfoDto)).append("\n");
    sb.append("    userInfoDto: ").append(toIndentedString(userInfoDto)).append("\n");
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

