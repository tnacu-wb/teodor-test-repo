package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.HotelRoomsDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * VacantRoomResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class VacantRoomResponseDto {

  private @Nullable HotelRoomsDetailsDto hotelRoomsDetails;

  public VacantRoomResponseDto hotelRoomsDetails(HotelRoomsDetailsDto hotelRoomsDetails) {
    this.hotelRoomsDetails = hotelRoomsDetails;
    return this;
  }

  /**
   * Get hotelRoomsDetails
   * @return hotelRoomsDetails
   */
  @Valid 
  @Schema(name = "hotelRoomsDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelRoomsDetails")
  public HotelRoomsDetailsDto getHotelRoomsDetails() {
    return hotelRoomsDetails;
  }

  public void setHotelRoomsDetails(HotelRoomsDetailsDto hotelRoomsDetails) {
    this.hotelRoomsDetails = hotelRoomsDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VacantRoomResponseDto vacantRoomResponseDto = (VacantRoomResponseDto) o;
    return Objects.equals(this.hotelRoomsDetails, vacantRoomResponseDto.hotelRoomsDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelRoomsDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VacantRoomResponseDto {\n");
    sb.append("    hotelRoomsDetails: ").append(toIndentedString(hotelRoomsDetails)).append("\n");
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

