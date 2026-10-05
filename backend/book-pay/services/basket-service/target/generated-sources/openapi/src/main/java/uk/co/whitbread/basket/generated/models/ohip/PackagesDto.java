package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.MealDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackagesDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesDto {

  @Valid
  private List<@Valid MealDto> meals = new ArrayList<>();

  public PackagesDto meals(List<@Valid MealDto> meals) {
    this.meals = meals;
    return this;
  }

  public PackagesDto addMealsItem(MealDto mealsItem) {
    if (this.meals == null) {
      this.meals = new ArrayList<>();
    }
    this.meals.add(mealsItem);
    return this;
  }

  /**
   * Get meals
   * @return meals
   */
  @Valid 
  @Schema(name = "meals", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("meals")
  public List<@Valid MealDto> getMeals() {
    return meals;
  }

  public void setMeals(List<@Valid MealDto> meals) {
    this.meals = meals;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesDto packagesDto = (PackagesDto) o;
    return Objects.equals(this.meals, packagesDto.meals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(meals);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesDto {\n");
    sb.append("    meals: ").append(toIndentedString(meals)).append("\n");
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

