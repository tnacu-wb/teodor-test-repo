package uk.co.whitbread.hotel.info.generated.hotelinfo.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiHotelInfoRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:34.398121+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiHotelInfoRequest {

  /**
   * Gets or Sets country
   */
  public enum CountryEnum {
    GB("GB"),
    
    DE("DE");

    private String value;

    CountryEnum(String value) {
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
    public static CountryEnum fromValue(String value) {
      for (CountryEnum b : CountryEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable CountryEnum country;

  /**
   * Gets or Sets format
   */
  public enum FormatEnum {
    SHORT("SHORT"),
    
    LONG("LONG");

    private String value;

    FormatEnum(String value) {
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
    public static FormatEnum fromValue(String value) {
      for (FormatEnum b : FormatEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable FormatEnum format;

  @Valid
  private List<String> hotelCodes = new ArrayList<>();

  /**
   * Gets or Sets language
   */
  public enum LanguageEnum {
    EN("EN"),
    
    DE("DE");

    private String value;

    LanguageEnum(String value) {
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
    public static LanguageEnum fromValue(String value) {
      for (LanguageEnum b : LanguageEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable LanguageEnum language;

  public MultiHotelInfoRequest country(CountryEnum country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  
  @Schema(name = "country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public CountryEnum getCountry() {
    return country;
  }

  public void setCountry(CountryEnum country) {
    this.country = country;
  }

  public MultiHotelInfoRequest format(FormatEnum format) {
    this.format = format;
    return this;
  }

  /**
   * Get format
   * @return format
   */
  
  @Schema(name = "format", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("format")
  public FormatEnum getFormat() {
    return format;
  }

  public void setFormat(FormatEnum format) {
    this.format = format;
  }

  public MultiHotelInfoRequest hotelCodes(List<String> hotelCodes) {
    this.hotelCodes = hotelCodes;
    return this;
  }

  public MultiHotelInfoRequest addHotelCodesItem(String hotelCodesItem) {
    if (this.hotelCodes == null) {
      this.hotelCodes = new ArrayList<>();
    }
    this.hotelCodes.add(hotelCodesItem);
    return this;
  }

  /**
   * Get hotelCodes
   * @return hotelCodes
   */
  @Size(min = 0, max = 100) 
  @Schema(name = "hotelCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCodes")
  public List<String> getHotelCodes() {
    return hotelCodes;
  }

  public void setHotelCodes(List<String> hotelCodes) {
    this.hotelCodes = hotelCodes;
  }

  public MultiHotelInfoRequest language(LanguageEnum language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public LanguageEnum getLanguage() {
    return language;
  }

  public void setLanguage(LanguageEnum language) {
    this.language = language;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiHotelInfoRequest multiHotelInfoRequest = (MultiHotelInfoRequest) o;
    return Objects.equals(this.country, multiHotelInfoRequest.country) &&
        Objects.equals(this.format, multiHotelInfoRequest.format) &&
        Objects.equals(this.hotelCodes, multiHotelInfoRequest.hotelCodes) &&
        Objects.equals(this.language, multiHotelInfoRequest.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(country, format, hotelCodes, language);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiHotelInfoRequest {\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    hotelCodes: ").append(toIndentedString(hotelCodes)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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

