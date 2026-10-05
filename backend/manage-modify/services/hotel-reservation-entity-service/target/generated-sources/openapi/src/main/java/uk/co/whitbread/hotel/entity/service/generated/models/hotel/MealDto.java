package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MealDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MealDto {

  private @Nullable String allergyInfoSrc;

  private @Nullable String currency;

  private @Nullable String id;

  private @Nullable String idDesc;

  private @Nullable String idImg;

  private @Nullable String name;

  private @Nullable BigDecimal price;

  public MealDto allergyInfoSrc(String allergyInfoSrc) {
    this.allergyInfoSrc = allergyInfoSrc;
    return this;
  }

  /**
   * Get allergyInfoSrc
   * @return allergyInfoSrc
   */
  
  @Schema(name = "allergyInfoSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allergyInfoSrc")
  public String getAllergyInfoSrc() {
    return allergyInfoSrc;
  }

  public void setAllergyInfoSrc(String allergyInfoSrc) {
    this.allergyInfoSrc = allergyInfoSrc;
  }

  public MealDto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public MealDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MealDto idDesc(String idDesc) {
    this.idDesc = idDesc;
    return this;
  }

  /**
   * Get idDesc
   * @return idDesc
   */
  
  @Schema(name = "idDesc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idDesc")
  public String getIdDesc() {
    return idDesc;
  }

  public void setIdDesc(String idDesc) {
    this.idDesc = idDesc;
  }

  public MealDto idImg(String idImg) {
    this.idImg = idImg;
    return this;
  }

  /**
   * Get idImg
   * @return idImg
   */
  
  @Schema(name = "idImg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idImg")
  public String getIdImg() {
    return idImg;
  }

  public void setIdImg(String idImg) {
    this.idImg = idImg;
  }

  public MealDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MealDto price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  @Valid 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MealDto mealDto = (MealDto) o;
    return Objects.equals(this.allergyInfoSrc, mealDto.allergyInfoSrc) &&
        Objects.equals(this.currency, mealDto.currency) &&
        Objects.equals(this.id, mealDto.id) &&
        Objects.equals(this.idDesc, mealDto.idDesc) &&
        Objects.equals(this.idImg, mealDto.idImg) &&
        Objects.equals(this.name, mealDto.name) &&
        Objects.equals(this.price, mealDto.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allergyInfoSrc, currency, id, idDesc, idImg, name, price);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MealDto {\n");
    sb.append("    allergyInfoSrc: ").append(toIndentedString(allergyInfoSrc)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idDesc: ").append(toIndentedString(idDesc)).append("\n");
    sb.append("    idImg: ").append(toIndentedString(idImg)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
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

