package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.ApiDto;
import uk.co.whitbread.basket.generated.models.content.AuthenticationDto;
import uk.co.whitbread.basket.generated.models.content.BookingSearchDto;
import uk.co.whitbread.basket.generated.models.content.RoomCodesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfigDto {

  private @Nullable ApiDto api;

  private @Nullable AuthenticationDto authentication;

  private @Nullable BookingSearchDto bookingSearch;

  private @Nullable RoomCodesDto roomCodes;

  public ConfigDto api(ApiDto api) {
    this.api = api;
    return this;
  }

  /**
   * Get api
   * @return api
   */
  @Valid 
  @Schema(name = "api", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("api")
  public ApiDto getApi() {
    return api;
  }

  public void setApi(ApiDto api) {
    this.api = api;
  }

  public ConfigDto authentication(AuthenticationDto authentication) {
    this.authentication = authentication;
    return this;
  }

  /**
   * Get authentication
   * @return authentication
   */
  @Valid 
  @Schema(name = "authentication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("authentication")
  public AuthenticationDto getAuthentication() {
    return authentication;
  }

  public void setAuthentication(AuthenticationDto authentication) {
    this.authentication = authentication;
  }

  public ConfigDto bookingSearch(BookingSearchDto bookingSearch) {
    this.bookingSearch = bookingSearch;
    return this;
  }

  /**
   * Get bookingSearch
   * @return bookingSearch
   */
  @Valid 
  @Schema(name = "bookingSearch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingSearch")
  public BookingSearchDto getBookingSearch() {
    return bookingSearch;
  }

  public void setBookingSearch(BookingSearchDto bookingSearch) {
    this.bookingSearch = bookingSearch;
  }

  public ConfigDto roomCodes(RoomCodesDto roomCodes) {
    this.roomCodes = roomCodes;
    return this;
  }

  /**
   * Get roomCodes
   * @return roomCodes
   */
  @Valid 
  @Schema(name = "roomCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCodes")
  public RoomCodesDto getRoomCodes() {
    return roomCodes;
  }

  public void setRoomCodes(RoomCodesDto roomCodes) {
    this.roomCodes = roomCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfigDto configDto = (ConfigDto) o;
    return Objects.equals(this.api, configDto.api) &&
        Objects.equals(this.authentication, configDto.authentication) &&
        Objects.equals(this.bookingSearch, configDto.bookingSearch) &&
        Objects.equals(this.roomCodes, configDto.roomCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(api, authentication, bookingSearch, roomCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigDto {\n");
    sb.append("    api: ").append(toIndentedString(api)).append("\n");
    sb.append("    authentication: ").append(toIndentedString(authentication)).append("\n");
    sb.append("    bookingSearch: ").append(toIndentedString(bookingSearch)).append("\n");
    sb.append("    roomCodes: ").append(toIndentedString(roomCodes)).append("\n");
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

