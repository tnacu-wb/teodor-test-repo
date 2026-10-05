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
 * FiltersDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class FiltersDto {

  private @Nullable String cancelledCards;

  private @Nullable String myCards;

  private @Nullable String show;

  public FiltersDto cancelledCards(String cancelledCards) {
    this.cancelledCards = cancelledCards;
    return this;
  }

  /**
   * Get cancelledCards
   * @return cancelledCards
   */
  
  @Schema(name = "cancelledCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelledCards")
  public String getCancelledCards() {
    return cancelledCards;
  }

  public void setCancelledCards(String cancelledCards) {
    this.cancelledCards = cancelledCards;
  }

  public FiltersDto myCards(String myCards) {
    this.myCards = myCards;
    return this;
  }

  /**
   * Get myCards
   * @return myCards
   */
  
  @Schema(name = "myCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("myCards")
  public String getMyCards() {
    return myCards;
  }

  public void setMyCards(String myCards) {
    this.myCards = myCards;
  }

  public FiltersDto show(String show) {
    this.show = show;
    return this;
  }

  /**
   * Get show
   * @return show
   */
  
  @Schema(name = "show", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("show")
  public String getShow() {
    return show;
  }

  public void setShow(String show) {
    this.show = show;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltersDto filtersDto = (FiltersDto) o;
    return Objects.equals(this.cancelledCards, filtersDto.cancelledCards) &&
        Objects.equals(this.myCards, filtersDto.myCards) &&
        Objects.equals(this.show, filtersDto.show);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancelledCards, myCards, show);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltersDto {\n");
    sb.append("    cancelledCards: ").append(toIndentedString(cancelledCards)).append("\n");
    sb.append("    myCards: ").append(toIndentedString(myCards)).append("\n");
    sb.append("    show: ").append(toIndentedString(show)).append("\n");
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

