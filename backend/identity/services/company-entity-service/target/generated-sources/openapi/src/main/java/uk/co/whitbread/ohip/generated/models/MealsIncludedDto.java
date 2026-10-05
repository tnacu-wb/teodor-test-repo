package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MealsIncludedDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MealsIncludedDto {

  private @Nullable Boolean breakfast;

  private @Nullable Boolean dinner;

  private @Nullable String mealName;

  public MealsIncludedDto breakfast(Boolean breakfast) {
    this.breakfast = breakfast;
    return this;
  }

  /**
   * Get breakfast
   * @return breakfast
   */
  
  @Schema(name = "breakfast", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("breakfast")
  public Boolean getBreakfast() {
    return breakfast;
  }

  public void setBreakfast(Boolean breakfast) {
    this.breakfast = breakfast;
  }

  public MealsIncludedDto dinner(Boolean dinner) {
    this.dinner = dinner;
    return this;
  }

  /**
   * Get dinner
   * @return dinner
   */
  
  @Schema(name = "dinner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dinner")
  public Boolean getDinner() {
    return dinner;
  }

  public void setDinner(Boolean dinner) {
    this.dinner = dinner;
  }

  public MealsIncludedDto mealName(String mealName) {
    this.mealName = mealName;
    return this;
  }

  /**
   * Get mealName
   * @return mealName
   */
  
  @Schema(name = "mealName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealName")
  public String getMealName() {
    return mealName;
  }

  public void setMealName(String mealName) {
    this.mealName = mealName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MealsIncludedDto mealsIncludedDto = (MealsIncludedDto) o;
    return Objects.equals(this.breakfast, mealsIncludedDto.breakfast) &&
        Objects.equals(this.dinner, mealsIncludedDto.dinner) &&
        Objects.equals(this.mealName, mealsIncludedDto.mealName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(breakfast, dinner, mealName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MealsIncludedDto {\n");
    sb.append("    breakfast: ").append(toIndentedString(breakfast)).append("\n");
    sb.append("    dinner: ").append(toIndentedString(dinner)).append("\n");
    sb.append("    mealName: ").append(toIndentedString(mealName)).append("\n");
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

