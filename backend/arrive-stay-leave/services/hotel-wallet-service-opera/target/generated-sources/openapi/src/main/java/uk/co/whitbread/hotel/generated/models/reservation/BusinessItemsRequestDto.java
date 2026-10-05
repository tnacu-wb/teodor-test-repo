package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.BusinessItemsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessItemsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessItemsRequestDto {

  private @Nullable BusinessItemsDto businessItems;

  private @Nullable String channel;

  private @Nullable String companyId;

  private @Nullable String hotelId;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  public BusinessItemsRequestDto businessItems(BusinessItemsDto businessItems) {
    this.businessItems = businessItems;
    return this;
  }

  /**
   * Get businessItems
   * @return businessItems
   */
  @Valid 
  @Schema(name = "businessItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessItems")
  public BusinessItemsDto getBusinessItems() {
    return businessItems;
  }

  public void setBusinessItems(BusinessItemsDto businessItems) {
    this.businessItems = businessItems;
  }

  public BusinessItemsRequestDto channel(String channel) {
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

  public BusinessItemsRequestDto companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public BusinessItemsRequestDto hotelId(String hotelId) {
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

  public BusinessItemsRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public BusinessItemsRequestDto addReservationIdsItem(String reservationIdsItem) {
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
  
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationIds")
  public List<String> getReservationIds() {
    return reservationIds;
  }

  public void setReservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessItemsRequestDto businessItemsRequestDto = (BusinessItemsRequestDto) o;
    return Objects.equals(this.businessItems, businessItemsRequestDto.businessItems) &&
        Objects.equals(this.channel, businessItemsRequestDto.channel) &&
        Objects.equals(this.companyId, businessItemsRequestDto.companyId) &&
        Objects.equals(this.hotelId, businessItemsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, businessItemsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessItems, channel, companyId, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessItemsRequestDto {\n");
    sb.append("    businessItems: ").append(toIndentedString(businessItems)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
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

