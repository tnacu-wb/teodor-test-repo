package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.FaqItem;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Faq
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Faq {

  @Valid
  private List<@Valid FaqItem> faqItems = new ArrayList<>();

  private @Nullable String title;

  public Faq faqItems(List<@Valid FaqItem> faqItems) {
    this.faqItems = faqItems;
    return this;
  }

  public Faq addFaqItemsItem(FaqItem faqItemsItem) {
    if (this.faqItems == null) {
      this.faqItems = new ArrayList<>();
    }
    this.faqItems.add(faqItemsItem);
    return this;
  }

  /**
   * Get faqItems
   * @return faqItems
   */
  @Valid 
  @Schema(name = "faqItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faqItems")
  public List<@Valid FaqItem> getFaqItems() {
    return faqItems;
  }

  public void setFaqItems(List<@Valid FaqItem> faqItems) {
    this.faqItems = faqItems;
  }

  public Faq title(String title) {
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
    Faq faq = (Faq) o;
    return Objects.equals(this.faqItems, faq.faqItems) &&
        Objects.equals(this.title, faq.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(faqItems, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Faq {\n");
    sb.append("    faqItems: ").append(toIndentedString(faqItems)).append("\n");
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

