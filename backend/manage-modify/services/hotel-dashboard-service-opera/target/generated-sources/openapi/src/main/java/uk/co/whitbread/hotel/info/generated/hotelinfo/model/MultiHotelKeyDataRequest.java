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
 * MultiHotelKeyDataRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:34.398121+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiHotelKeyDataRequest {

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

  public MultiHotelKeyDataRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MultiHotelKeyDataRequest(List<String> hotelCodes) {
    this.hotelCodes = hotelCodes;
  }

  public MultiHotelKeyDataRequest hotelCodes(List<String> hotelCodes) {
    this.hotelCodes = hotelCodes;
    return this;
  }

  public MultiHotelKeyDataRequest addHotelCodesItem(String hotelCodesItem) {
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
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "hotelCodes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelCodes")
  public List<String> getHotelCodes() {
    return hotelCodes;
  }

  public void setHotelCodes(List<String> hotelCodes) {
    this.hotelCodes = hotelCodes;
  }

  public MultiHotelKeyDataRequest language(LanguageEnum language) {
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
    MultiHotelKeyDataRequest multiHotelKeyDataRequest = (MultiHotelKeyDataRequest) o;
    return Objects.equals(this.hotelCodes, multiHotelKeyDataRequest.hotelCodes) &&
        Objects.equals(this.language, multiHotelKeyDataRequest.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelCodes, language);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiHotelKeyDataRequest {\n");
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

