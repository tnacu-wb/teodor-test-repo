package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.FactItem;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Facts
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Facts {

  @Valid
  private List<@Valid FactItem> factItems = new ArrayList<>();

  public Facts() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Facts(List<@Valid FactItem> factItems) {
    this.factItems = factItems;
  }

  public Facts factItems(List<@Valid FactItem> factItems) {
    this.factItems = factItems;
    return this;
  }

  public Facts addFactItemsItem(FactItem factItemsItem) {
    if (this.factItems == null) {
      this.factItems = new ArrayList<>();
    }
    this.factItems.add(factItemsItem);
    return this;
  }

  /**
   * Get factItems
   * @return factItems
   */
  @NotNull @Valid 
  @Schema(name = "factItems", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("factItems")
  public List<@Valid FactItem> getFactItems() {
    return factItems;
  }

  public void setFactItems(List<@Valid FactItem> factItems) {
    this.factItems = factItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Facts facts = (Facts) o;
    return Objects.equals(this.factItems, facts.factItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(factItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Facts {\n");
    sb.append("    factItems: ").append(toIndentedString(factItems)).append("\n");
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

