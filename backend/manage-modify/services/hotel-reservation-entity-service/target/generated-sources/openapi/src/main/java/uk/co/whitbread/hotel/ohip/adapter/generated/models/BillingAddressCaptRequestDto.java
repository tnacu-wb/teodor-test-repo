package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BillingAddressCaptRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BillingAddressCaptRequestDto {

  private BookerDetailsDto booker;

  private @Nullable String channel;

  private String hotelId;

  private @Nullable String paymentOption;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  private @Nullable Boolean updateCompanyProfile;

  private @Nullable Boolean updateContactProfile;

  private @Nullable Boolean updateGuestProfile;

  public BillingAddressCaptRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BillingAddressCaptRequestDto(BookerDetailsDto booker, String hotelId, List<String> reservationIds) {
    this.booker = booker;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public BillingAddressCaptRequestDto booker(BookerDetailsDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @NotNull @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("booker")
  public BookerDetailsDto getBooker() {
    return booker;
  }

  public void setBooker(BookerDetailsDto booker) {
    this.booker = booker;
  }

  public BillingAddressCaptRequestDto channel(String channel) {
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

  public BillingAddressCaptRequestDto hotelId(String hotelId) {
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

  public BillingAddressCaptRequestDto paymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  
  @Schema(name = "paymentOption", example = "PAY_NOW", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOption")
  public String getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
  }

  public BillingAddressCaptRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public BillingAddressCaptRequestDto addReservationIdsItem(String reservationIdsItem) {
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

  public BillingAddressCaptRequestDto updateCompanyProfile(Boolean updateCompanyProfile) {
    this.updateCompanyProfile = updateCompanyProfile;
    return this;
  }

  /**
   * Get updateCompanyProfile
   * @return updateCompanyProfile
   */
  
  @Schema(name = "updateCompanyProfile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateCompanyProfile")
  public Boolean getUpdateCompanyProfile() {
    return updateCompanyProfile;
  }

  public void setUpdateCompanyProfile(Boolean updateCompanyProfile) {
    this.updateCompanyProfile = updateCompanyProfile;
  }

  public BillingAddressCaptRequestDto updateContactProfile(Boolean updateContactProfile) {
    this.updateContactProfile = updateContactProfile;
    return this;
  }

  /**
   * Get updateContactProfile
   * @return updateContactProfile
   */
  
  @Schema(name = "updateContactProfile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateContactProfile")
  public Boolean getUpdateContactProfile() {
    return updateContactProfile;
  }

  public void setUpdateContactProfile(Boolean updateContactProfile) {
    this.updateContactProfile = updateContactProfile;
  }

  public BillingAddressCaptRequestDto updateGuestProfile(Boolean updateGuestProfile) {
    this.updateGuestProfile = updateGuestProfile;
    return this;
  }

  /**
   * Get updateGuestProfile
   * @return updateGuestProfile
   */
  
  @Schema(name = "updateGuestProfile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateGuestProfile")
  public Boolean getUpdateGuestProfile() {
    return updateGuestProfile;
  }

  public void setUpdateGuestProfile(Boolean updateGuestProfile) {
    this.updateGuestProfile = updateGuestProfile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillingAddressCaptRequestDto billingAddressCaptRequestDto = (BillingAddressCaptRequestDto) o;
    return Objects.equals(this.booker, billingAddressCaptRequestDto.booker) &&
        Objects.equals(this.channel, billingAddressCaptRequestDto.channel) &&
        Objects.equals(this.hotelId, billingAddressCaptRequestDto.hotelId) &&
        Objects.equals(this.paymentOption, billingAddressCaptRequestDto.paymentOption) &&
        Objects.equals(this.reservationIds, billingAddressCaptRequestDto.reservationIds) &&
        Objects.equals(this.updateCompanyProfile, billingAddressCaptRequestDto.updateCompanyProfile) &&
        Objects.equals(this.updateContactProfile, billingAddressCaptRequestDto.updateContactProfile) &&
        Objects.equals(this.updateGuestProfile, billingAddressCaptRequestDto.updateGuestProfile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booker, channel, hotelId, paymentOption, reservationIds, updateCompanyProfile, updateContactProfile, updateGuestProfile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingAddressCaptRequestDto {\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
    sb.append("    updateCompanyProfile: ").append(toIndentedString(updateCompanyProfile)).append("\n");
    sb.append("    updateContactProfile: ").append(toIndentedString(updateContactProfile)).append("\n");
    sb.append("    updateGuestProfile: ").append(toIndentedString(updateGuestProfile)).append("\n");
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

