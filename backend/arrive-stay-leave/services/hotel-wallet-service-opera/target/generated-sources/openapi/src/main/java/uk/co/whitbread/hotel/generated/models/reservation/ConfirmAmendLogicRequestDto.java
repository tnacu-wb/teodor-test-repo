package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.BookingChannelDto;
import uk.co.whitbread.hotel.generated.models.reservation.CcuiExtraItemsDto;
import uk.co.whitbread.hotel.generated.models.reservation.PaymentCcuiRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConfirmAmendLogicRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmAmendLogicRequestDto {

  private BookingChannelDto bookingChannel;

  private @Nullable CcuiExtraItemsDto ccuiExtraItems;

  private @Nullable String emailAddress;

  private String environment;

  private String originalBookingRef;

  private @Nullable String paymentOption;

  /**
   * Gets or Sets paymentOptionSelected
   */
  public enum PaymentOptionSelectedEnum {
    PAY_NOW("PAY_NOW"),
    
    PAY_ON_ARRIVAL("PAY_ON_ARRIVAL");

    private String value;

    PaymentOptionSelectedEnum(String value) {
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
    public static PaymentOptionSelectedEnum fromValue(String value) {
      for (PaymentOptionSelectedEnum b : PaymentOptionSelectedEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PaymentOptionSelectedEnum paymentOptionSelected;

  private @Nullable PaymentCcuiRequestDto paymentRequest;

  @Valid
  private List<String> preCheckIn = new ArrayList<>();

  private @Nullable String subPaymentType;

  private String tempBookingRef;

  private String token;

  public ConfirmAmendLogicRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConfirmAmendLogicRequestDto(BookingChannelDto bookingChannel, String environment, String originalBookingRef, String tempBookingRef, String token) {
    this.bookingChannel = bookingChannel;
    this.environment = environment;
    this.originalBookingRef = originalBookingRef;
    this.tempBookingRef = tempBookingRef;
    this.token = token;
  }

  public ConfirmAmendLogicRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public ConfirmAmendLogicRequestDto ccuiExtraItems(CcuiExtraItemsDto ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
    return this;
  }

  /**
   * Get ccuiExtraItems
   * @return ccuiExtraItems
   */
  @Valid 
  @Schema(name = "ccuiExtraItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccuiExtraItems")
  public CcuiExtraItemsDto getCcuiExtraItems() {
    return ccuiExtraItems;
  }

  public void setCcuiExtraItems(CcuiExtraItemsDto ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
  }

  public ConfirmAmendLogicRequestDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public ConfirmAmendLogicRequestDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Get environment
   * @return environment
   */
  @NotNull 
  @Schema(name = "environment", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public ConfirmAmendLogicRequestDto originalBookingRef(String originalBookingRef) {
    this.originalBookingRef = originalBookingRef;
    return this;
  }

  /**
   * Get originalBookingRef
   * @return originalBookingRef
   */
  @NotNull 
  @Schema(name = "originalBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("originalBookingRef")
  public String getOriginalBookingRef() {
    return originalBookingRef;
  }

  public void setOriginalBookingRef(String originalBookingRef) {
    this.originalBookingRef = originalBookingRef;
  }

  public ConfirmAmendLogicRequestDto paymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  
  @Schema(name = "paymentOption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOption")
  public String getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
  }

  public ConfirmAmendLogicRequestDto paymentOptionSelected(PaymentOptionSelectedEnum paymentOptionSelected) {
    this.paymentOptionSelected = paymentOptionSelected;
    return this;
  }

  /**
   * Get paymentOptionSelected
   * @return paymentOptionSelected
   */
  
  @Schema(name = "paymentOptionSelected", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOptionSelected")
  public PaymentOptionSelectedEnum getPaymentOptionSelected() {
    return paymentOptionSelected;
  }

  public void setPaymentOptionSelected(PaymentOptionSelectedEnum paymentOptionSelected) {
    this.paymentOptionSelected = paymentOptionSelected;
  }

  public ConfirmAmendLogicRequestDto paymentRequest(PaymentCcuiRequestDto paymentRequest) {
    this.paymentRequest = paymentRequest;
    return this;
  }

  /**
   * Get paymentRequest
   * @return paymentRequest
   */
  @Valid 
  @Schema(name = "paymentRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentRequest")
  public PaymentCcuiRequestDto getPaymentRequest() {
    return paymentRequest;
  }

  public void setPaymentRequest(PaymentCcuiRequestDto paymentRequest) {
    this.paymentRequest = paymentRequest;
  }

  public ConfirmAmendLogicRequestDto preCheckIn(List<String> preCheckIn) {
    this.preCheckIn = preCheckIn;
    return this;
  }

  public ConfirmAmendLogicRequestDto addPreCheckInItem(String preCheckInItem) {
    if (this.preCheckIn == null) {
      this.preCheckIn = new ArrayList<>();
    }
    this.preCheckIn.add(preCheckInItem);
    return this;
  }

  /**
   * Get preCheckIn
   * @return preCheckIn
   */
  
  @Schema(name = "preCheckIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preCheckIn")
  public List<String> getPreCheckIn() {
    return preCheckIn;
  }

  public void setPreCheckIn(List<String> preCheckIn) {
    this.preCheckIn = preCheckIn;
  }

  public ConfirmAmendLogicRequestDto subPaymentType(String subPaymentType) {
    this.subPaymentType = subPaymentType;
    return this;
  }

  /**
   * Get subPaymentType
   * @return subPaymentType
   */
  
  @Schema(name = "subPaymentType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subPaymentType")
  public String getSubPaymentType() {
    return subPaymentType;
  }

  public void setSubPaymentType(String subPaymentType) {
    this.subPaymentType = subPaymentType;
  }

  public ConfirmAmendLogicRequestDto tempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
    return this;
  }

  /**
   * Get tempBookingRef
   * @return tempBookingRef
   */
  @NotNull 
  @Schema(name = "tempBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tempBookingRef")
  public String getTempBookingRef() {
    return tempBookingRef;
  }

  public void setTempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
  }

  public ConfirmAmendLogicRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  @NotNull 
  @Schema(name = "token", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ConfirmAmendLogicRequestDto confirmAmendLogicRequestDto = (ConfirmAmendLogicRequestDto) o;
    return Objects.equals(this.bookingChannel, confirmAmendLogicRequestDto.bookingChannel) &&
        Objects.equals(this.ccuiExtraItems, confirmAmendLogicRequestDto.ccuiExtraItems) &&
        Objects.equals(this.emailAddress, confirmAmendLogicRequestDto.emailAddress) &&
        Objects.equals(this.environment, confirmAmendLogicRequestDto.environment) &&
        Objects.equals(this.originalBookingRef, confirmAmendLogicRequestDto.originalBookingRef) &&
        Objects.equals(this.paymentOption, confirmAmendLogicRequestDto.paymentOption) &&
        Objects.equals(this.paymentOptionSelected, confirmAmendLogicRequestDto.paymentOptionSelected) &&
        Objects.equals(this.paymentRequest, confirmAmendLogicRequestDto.paymentRequest) &&
        Objects.equals(this.preCheckIn, confirmAmendLogicRequestDto.preCheckIn) &&
        Objects.equals(this.subPaymentType, confirmAmendLogicRequestDto.subPaymentType) &&
        Objects.equals(this.tempBookingRef, confirmAmendLogicRequestDto.tempBookingRef) &&
        Objects.equals(this.token, confirmAmendLogicRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, ccuiExtraItems, emailAddress, environment, originalBookingRef, paymentOption, paymentOptionSelected, paymentRequest, preCheckIn, subPaymentType, tempBookingRef, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmAmendLogicRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    ccuiExtraItems: ").append(toIndentedString(ccuiExtraItems)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    originalBookingRef: ").append(toIndentedString(originalBookingRef)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    paymentOptionSelected: ").append(toIndentedString(paymentOptionSelected)).append("\n");
    sb.append("    paymentRequest: ").append(toIndentedString(paymentRequest)).append("\n");
    sb.append("    preCheckIn: ").append(toIndentedString(preCheckIn)).append("\n");
    sb.append("    subPaymentType: ").append(toIndentedString(subPaymentType)).append("\n");
    sb.append("    tempBookingRef: ").append(toIndentedString(tempBookingRef)).append("\n");
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

