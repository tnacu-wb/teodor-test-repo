package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelAvailabilitiesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilitiesResponseDto {

  @Valid
  private List<@Valid HotelAvailabilityResponseDto> hotelAvailabilities = new ArrayList<>();

  private @Nullable Integer page;

  private @Nullable Integer pageSize;

  private @Nullable Integer total;

  public HotelAvailabilitiesResponseDto hotelAvailabilities(List<@Valid HotelAvailabilityResponseDto> hotelAvailabilities) {
    this.hotelAvailabilities = hotelAvailabilities;
    return this;
  }

  public HotelAvailabilitiesResponseDto addHotelAvailabilitiesItem(HotelAvailabilityResponseDto hotelAvailabilitiesItem) {
    if (this.hotelAvailabilities == null) {
      this.hotelAvailabilities = new ArrayList<>();
    }
    this.hotelAvailabilities.add(hotelAvailabilitiesItem);
    return this;
  }

  /**
   * Get hotelAvailabilities
   * @return hotelAvailabilities
   */
  @Valid 
  @Schema(name = "hotelAvailabilities", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelAvailabilities")
  public List<@Valid HotelAvailabilityResponseDto> getHotelAvailabilities() {
    return hotelAvailabilities;
  }

  public void setHotelAvailabilities(List<@Valid HotelAvailabilityResponseDto> hotelAvailabilities) {
    this.hotelAvailabilities = hotelAvailabilities;
  }

  public HotelAvailabilitiesResponseDto page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  
  @Schema(name = "page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public HotelAvailabilitiesResponseDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public HotelAvailabilitiesResponseDto total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  
  @Schema(name = "total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilitiesResponseDto hotelAvailabilitiesResponseDto = (HotelAvailabilitiesResponseDto) o;
    return Objects.equals(this.hotelAvailabilities, hotelAvailabilitiesResponseDto.hotelAvailabilities) &&
        Objects.equals(this.page, hotelAvailabilitiesResponseDto.page) &&
        Objects.equals(this.pageSize, hotelAvailabilitiesResponseDto.pageSize) &&
        Objects.equals(this.total, hotelAvailabilitiesResponseDto.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelAvailabilities, page, pageSize, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilitiesResponseDto {\n");
    sb.append("    hotelAvailabilities: ").append(toIndentedString(hotelAvailabilities)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

