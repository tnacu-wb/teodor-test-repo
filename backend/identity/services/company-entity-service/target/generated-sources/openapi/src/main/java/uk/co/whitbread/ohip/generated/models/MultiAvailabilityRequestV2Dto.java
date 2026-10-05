package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.BookingChannelDto;
import uk.co.whitbread.ohip.generated.models.RoomV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiAvailabilityRequestV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiAvailabilityRequestV2Dto {

  private @Nullable String accountId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  private BookingChannelDto bookingChannel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departureDate;

  @Valid
  private List<String> hotelIds = new ArrayList<>();

  private @Nullable Boolean includePublicRates;

  private @Nullable Integer limit;

  private @Nullable BigDecimal minRate;

  private @Nullable Integer offset;

  @Valid
  private List<@Valid RoomV2Dto> rooms = new ArrayList<>();

  private @Nullable String sortBy;

  public MultiAvailabilityRequestV2Dto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MultiAvailabilityRequestV2Dto(LocalDate arrivalDate, BookingChannelDto bookingChannel, LocalDate departureDate, List<String> hotelIds, List<@Valid RoomV2Dto> rooms) {
    this.arrivalDate = arrivalDate;
    this.bookingChannel = bookingChannel;
    this.departureDate = departureDate;
    this.hotelIds = hotelIds;
    this.rooms = rooms;
  }

  public MultiAvailabilityRequestV2Dto accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
   */
  
  @Schema(name = "accountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountId")
  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public MultiAvailabilityRequestV2Dto arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull @Valid 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public MultiAvailabilityRequestV2Dto bookingChannel(BookingChannelDto bookingChannel) {
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

  public MultiAvailabilityRequestV2Dto departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull @Valid 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public MultiAvailabilityRequestV2Dto hotelIds(List<String> hotelIds) {
    this.hotelIds = hotelIds;
    return this;
  }

  public MultiAvailabilityRequestV2Dto addHotelIdsItem(String hotelIdsItem) {
    if (this.hotelIds == null) {
      this.hotelIds = new ArrayList<>();
    }
    this.hotelIds.add(hotelIdsItem);
    return this;
  }

  /**
   * Get hotelIds
   * @return hotelIds
   */
  @NotNull 
  @Schema(name = "hotelIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelIds")
  public List<String> getHotelIds() {
    return hotelIds;
  }

  public void setHotelIds(List<String> hotelIds) {
    this.hotelIds = hotelIds;
  }

  public MultiAvailabilityRequestV2Dto includePublicRates(Boolean includePublicRates) {
    this.includePublicRates = includePublicRates;
    return this;
  }

  /**
   * Get includePublicRates
   * @return includePublicRates
   */
  
  @Schema(name = "includePublicRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includePublicRates")
  public Boolean getIncludePublicRates() {
    return includePublicRates;
  }

  public void setIncludePublicRates(Boolean includePublicRates) {
    this.includePublicRates = includePublicRates;
  }

  public MultiAvailabilityRequestV2Dto limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get limit
   * @return limit
   */
  
  @Schema(name = "limit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limit")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public MultiAvailabilityRequestV2Dto minRate(BigDecimal minRate) {
    this.minRate = minRate;
    return this;
  }

  /**
   * Get minRate
   * @return minRate
   */
  @Valid 
  @Schema(name = "minRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minRate")
  public BigDecimal getMinRate() {
    return minRate;
  }

  public void setMinRate(BigDecimal minRate) {
    this.minRate = minRate;
  }

  public MultiAvailabilityRequestV2Dto offset(Integer offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Get offset
   * @return offset
   */
  
  @Schema(name = "offset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offset")
  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public MultiAvailabilityRequestV2Dto rooms(List<@Valid RoomV2Dto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public MultiAvailabilityRequestV2Dto addRoomsItem(RoomV2Dto roomsItem) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.add(roomsItem);
    return this;
  }

  /**
   * Get rooms
   * @return rooms
   */
  @NotNull @Valid 
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rooms")
  public List<@Valid RoomV2Dto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomV2Dto> rooms) {
    this.rooms = rooms;
  }

  public MultiAvailabilityRequestV2Dto sortBy(String sortBy) {
    this.sortBy = sortBy;
    return this;
  }

  /**
   * Get sortBy
   * @return sortBy
   */
  
  @Schema(name = "sortBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortBy")
  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiAvailabilityRequestV2Dto multiAvailabilityRequestV2Dto = (MultiAvailabilityRequestV2Dto) o;
    return Objects.equals(this.accountId, multiAvailabilityRequestV2Dto.accountId) &&
        Objects.equals(this.arrivalDate, multiAvailabilityRequestV2Dto.arrivalDate) &&
        Objects.equals(this.bookingChannel, multiAvailabilityRequestV2Dto.bookingChannel) &&
        Objects.equals(this.departureDate, multiAvailabilityRequestV2Dto.departureDate) &&
        Objects.equals(this.hotelIds, multiAvailabilityRequestV2Dto.hotelIds) &&
        Objects.equals(this.includePublicRates, multiAvailabilityRequestV2Dto.includePublicRates) &&
        Objects.equals(this.limit, multiAvailabilityRequestV2Dto.limit) &&
        Objects.equals(this.minRate, multiAvailabilityRequestV2Dto.minRate) &&
        Objects.equals(this.offset, multiAvailabilityRequestV2Dto.offset) &&
        Objects.equals(this.rooms, multiAvailabilityRequestV2Dto.rooms) &&
        Objects.equals(this.sortBy, multiAvailabilityRequestV2Dto.sortBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, arrivalDate, bookingChannel, departureDate, hotelIds, includePublicRates, limit, minRate, offset, rooms, sortBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiAvailabilityRequestV2Dto {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelIds: ").append(toIndentedString(hotelIds)).append("\n");
    sb.append("    includePublicRates: ").append(toIndentedString(includePublicRates)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    minRate: ").append(toIndentedString(minRate)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    sortBy: ").append(toIndentedString(sortBy)).append("\n");
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

