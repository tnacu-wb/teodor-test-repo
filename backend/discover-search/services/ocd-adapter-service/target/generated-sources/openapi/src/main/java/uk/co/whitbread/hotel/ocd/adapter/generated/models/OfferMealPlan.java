package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRatePlanMealPlanType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRatePlanMealType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Details on meal plans included with the rate plan.
 */

@Schema(name = "OfferMealPlan", description = "Details on meal plans included with the rate plan.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferMealPlan {

  private @Nullable OfferRatePlanMealPlanType mealPlanIncluded;

  private @Nullable String mealPlanCode;

  private @Nullable String mealPlanDescription;

  @Valid
  private List<OfferRatePlanMealType> mealsIncluded = new ArrayList<>();

  public OfferMealPlan mealPlanIncluded(OfferRatePlanMealPlanType mealPlanIncluded) {
    this.mealPlanIncluded = mealPlanIncluded;
    return this;
  }

  /**
   * Get mealPlanIncluded
   * @return mealPlanIncluded
   */
  @Valid 
  @Schema(name = "mealPlanIncluded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealPlanIncluded")
  public OfferRatePlanMealPlanType getMealPlanIncluded() {
    return mealPlanIncluded;
  }

  public void setMealPlanIncluded(OfferRatePlanMealPlanType mealPlanIncluded) {
    this.mealPlanIncluded = mealPlanIncluded;
  }

  public OfferMealPlan mealPlanCode(String mealPlanCode) {
    this.mealPlanCode = mealPlanCode;
    return this;
  }

  /**
   * The code of the meal plan included to the rate plan.
   * @return mealPlanCode
   */
  
  @Schema(name = "mealPlanCode", example = "1", description = "The code of the meal plan included to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealPlanCode")
  public String getMealPlanCode() {
    return mealPlanCode;
  }

  public void setMealPlanCode(String mealPlanCode) {
    this.mealPlanCode = mealPlanCode;
  }

  public OfferMealPlan mealPlanDescription(String mealPlanDescription) {
    this.mealPlanDescription = mealPlanDescription;
    return this;
  }

  /**
   * Description of the meal plan included to the rate plan.
   * @return mealPlanDescription
   */
  
  @Schema(name = "mealPlanDescription", example = "All inclusive meal plan", description = "Description of the meal plan included to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealPlanDescription")
  public String getMealPlanDescription() {
    return mealPlanDescription;
  }

  public void setMealPlanDescription(String mealPlanDescription) {
    this.mealPlanDescription = mealPlanDescription;
  }

  public OfferMealPlan mealsIncluded(List<OfferRatePlanMealType> mealsIncluded) {
    this.mealsIncluded = mealsIncluded;
    return this;
  }

  public OfferMealPlan addMealsIncludedItem(OfferRatePlanMealType mealsIncludedItem) {
    if (this.mealsIncluded == null) {
      this.mealsIncluded = new ArrayList<>();
    }
    this.mealsIncluded.add(mealsIncludedItem);
    return this;
  }

  /**
   * List of meal plan types included to the rate plan.
   * @return mealsIncluded
   */
  @Valid 
  @Schema(name = "mealsIncluded", description = "List of meal plan types included to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealsIncluded")
  public List<OfferRatePlanMealType> getMealsIncluded() {
    return mealsIncluded;
  }

  public void setMealsIncluded(List<OfferRatePlanMealType> mealsIncluded) {
    this.mealsIncluded = mealsIncluded;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferMealPlan offerMealPlan = (OfferMealPlan) o;
    return Objects.equals(this.mealPlanIncluded, offerMealPlan.mealPlanIncluded) &&
        Objects.equals(this.mealPlanCode, offerMealPlan.mealPlanCode) &&
        Objects.equals(this.mealPlanDescription, offerMealPlan.mealPlanDescription) &&
        Objects.equals(this.mealsIncluded, offerMealPlan.mealsIncluded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mealPlanIncluded, mealPlanCode, mealPlanDescription, mealsIncluded);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferMealPlan {\n");
    sb.append("    mealPlanIncluded: ").append(toIndentedString(mealPlanIncluded)).append("\n");
    sb.append("    mealPlanCode: ").append(toIndentedString(mealPlanCode)).append("\n");
    sb.append("    mealPlanDescription: ").append(toIndentedString(mealPlanDescription)).append("\n");
    sb.append("    mealsIncluded: ").append(toIndentedString(mealsIncluded)).append("\n");
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

