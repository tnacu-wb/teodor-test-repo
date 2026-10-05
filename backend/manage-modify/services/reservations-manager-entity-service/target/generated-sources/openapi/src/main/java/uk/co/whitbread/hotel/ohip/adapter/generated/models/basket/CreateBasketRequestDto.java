package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateBasketRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CreateBasketRequestDto {

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
    
    CIOL_RC_FAILED("CIOL_RC_FAILED");

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

  private @Nullable String channel;

  private @Nullable String subChannel;

  private String hotelId;

  @Valid
  private Map<String, String> linkAmendReservations = new HashMap<>();

  private @Nullable String migratedResNo;

  private @Nullable String originalBasketId;

  private @Nullable String paymentId;

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

  private @Nullable String userId;

  public CreateBasketRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateBasketRequestDto(String hotelId) {
    this.hotelId = hotelId;
  }

  public CreateBasketRequestDto basketStatus(BasketStatusEnum basketStatus) {
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

  public CreateBasketRequestDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public CreateBasketRequestDto subChannel(String subChannel) {
    this.subChannel = subChannel;
    return this;
  }

  /**
   * Get subChannel
   * @return subChannel
   */
  
  @Schema(name = "subChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subChannel")
  public String getSubChannel() {
    return subChannel;
  }

  public void setSubChannel(String subChannel) {
    this.subChannel = subChannel;
  }

  public CreateBasketRequestDto hotelId(String hotelId) {
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

  public CreateBasketRequestDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public CreateBasketRequestDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
    if (this.linkAmendReservations == null) {
      this.linkAmendReservations = new HashMap<>();
    }
    this.linkAmendReservations.put(key, linkAmendReservationsItem);
    return this;
  }

  /**
   * Get linkAmendReservations
   * @return linkAmendReservations
   */
  
  @Schema(name = "linkAmendReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAmendReservations")
  public Map<String, String> getLinkAmendReservations() {
    return linkAmendReservations;
  }

  public void setLinkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
  }

  public CreateBasketRequestDto migratedResNo(String migratedResNo) {
    this.migratedResNo = migratedResNo;
    return this;
  }

  /**
   * Get migratedResNo
   * @return migratedResNo
   */
  
  @Schema(name = "migratedResNo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("migratedResNo")
  public String getMigratedResNo() {
    return migratedResNo;
  }

  public void setMigratedResNo(String migratedResNo) {
    this.migratedResNo = migratedResNo;
  }

  public CreateBasketRequestDto originalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
    return this;
  }

  /**
   * Get originalBasketId
   * @return originalBasketId
   */
  
  @Schema(name = "originalBasketId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalBasketId")
  public String getOriginalBasketId() {
    return originalBasketId;
  }

  public void setOriginalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
  }

  public CreateBasketRequestDto paymentId(String paymentId) {
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

  public CreateBasketRequestDto paymentOption(PaymentOptionEnum paymentOption) {
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

  public CreateBasketRequestDto userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBasketRequestDto createBasketRequestDto = (CreateBasketRequestDto) o;
    return Objects.equals(this.basketStatus, createBasketRequestDto.basketStatus) &&
        Objects.equals(this.channel, createBasketRequestDto.channel) &&
        Objects.equals(this.subChannel, createBasketRequestDto.subChannel) &&
        Objects.equals(this.hotelId, createBasketRequestDto.hotelId) &&
        Objects.equals(this.linkAmendReservations, createBasketRequestDto.linkAmendReservations) &&
        Objects.equals(this.migratedResNo, createBasketRequestDto.migratedResNo) &&
        Objects.equals(this.originalBasketId, createBasketRequestDto.originalBasketId) &&
        Objects.equals(this.paymentId, createBasketRequestDto.paymentId) &&
        Objects.equals(this.paymentOption, createBasketRequestDto.paymentOption) &&
        Objects.equals(this.userId, createBasketRequestDto.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketStatus, channel, subChannel, hotelId, linkAmendReservations, migratedResNo, originalBasketId, paymentId, paymentOption, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateBasketRequestDto {\n");
    sb.append("    basketStatus: ").append(toIndentedString(basketStatus)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    subChannel: ").append(toIndentedString(subChannel)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    migratedResNo: ").append(toIndentedString(migratedResNo)).append("\n");
    sb.append("    originalBasketId: ").append(toIndentedString(originalBasketId)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

