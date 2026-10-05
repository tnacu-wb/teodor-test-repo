package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelShortInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelShortInformationDto {

  private @Nullable String brand;

  private @Nullable String code;

  private @Nullable String hotelPagePath;

  private @Nullable String title;

  public HotelShortInformationDto brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public HotelShortInformationDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public HotelShortInformationDto hotelPagePath(String hotelPagePath) {
    this.hotelPagePath = hotelPagePath;
    return this;
  }

  /**
   * Get hotelPagePath
   * @return hotelPagePath
   */
  
  @Schema(name = "hotelPagePath", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelPagePath")
  public String getHotelPagePath() {
    return hotelPagePath;
  }

  public void setHotelPagePath(String hotelPagePath) {
    this.hotelPagePath = hotelPagePath;
  }

  public HotelShortInformationDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelShortInformationDto hotelShortInformationDto = (HotelShortInformationDto) o;
    return Objects.equals(this.brand, hotelShortInformationDto.brand) &&
        Objects.equals(this.code, hotelShortInformationDto.code) &&
        Objects.equals(this.hotelPagePath, hotelShortInformationDto.hotelPagePath) &&
        Objects.equals(this.title, hotelShortInformationDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brand, code, hotelPagePath, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelShortInformationDto {\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    hotelPagePath: ").append(toIndentedString(hotelPagePath)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

