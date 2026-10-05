package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CharacterUdfDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UdfsRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UdfsRequestDto {

  private String hotelId;

  @Valid
  private Set<String> reservationIds = new LinkedHashSet<>();

  @Valid
  private List<@Valid CharacterUdfDto> udfs = new ArrayList<>();

  public UdfsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UdfsRequestDto(String hotelId, Set<String> reservationIds, List<@Valid CharacterUdfDto> udfs) {
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
    this.udfs = udfs;
  }

  public UdfsRequestDto hotelId(String hotelId) {
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

  public UdfsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UdfsRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new LinkedHashSet<>();
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
  public Set<String> getReservationIds() {
    return reservationIds;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setReservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  public UdfsRequestDto udfs(List<@Valid CharacterUdfDto> udfs) {
    this.udfs = udfs;
    return this;
  }

  public UdfsRequestDto addUdfsItem(CharacterUdfDto udfsItem) {
    if (this.udfs == null) {
      this.udfs = new ArrayList<>();
    }
    this.udfs.add(udfsItem);
    return this;
  }

  /**
   * Get udfs
   * @return udfs
   */
  @NotNull @Valid 
  @Schema(name = "udfs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("udfs")
  public List<@Valid CharacterUdfDto> getUdfs() {
    return udfs;
  }

  public void setUdfs(List<@Valid CharacterUdfDto> udfs) {
    this.udfs = udfs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UdfsRequestDto udfsRequestDto = (UdfsRequestDto) o;
    return Objects.equals(this.hotelId, udfsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, udfsRequestDto.reservationIds) &&
        Objects.equals(this.udfs, udfsRequestDto.udfs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reservationIds, udfs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UdfsRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
    sb.append("    udfs: ").append(toIndentedString(udfs)).append("\n");
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

