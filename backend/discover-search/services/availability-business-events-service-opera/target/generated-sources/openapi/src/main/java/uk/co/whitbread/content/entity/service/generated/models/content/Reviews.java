package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.User;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Reviews
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Reviews {

  private String publishedDate;

  private BigDecimal rating;

  private String text;

  private String title;

  private String tripType;

  private User user;

  public Reviews() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Reviews(String publishedDate, BigDecimal rating, String text, String title, String tripType, User user) {
    this.publishedDate = publishedDate;
    this.rating = rating;
    this.text = text;
    this.title = title;
    this.tripType = tripType;
    this.user = user;
  }

  public Reviews publishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
    return this;
  }

  /**
   * Get publishedDate
   * @return publishedDate
   */
  @NotNull 
  @Schema(name = "publishedDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("publishedDate")
  public String getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
  }

  public Reviews rating(BigDecimal rating) {
    this.rating = rating;
    return this;
  }

  /**
   * Get rating
   * @return rating
   */
  @NotNull @Valid 
  @Schema(name = "rating", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rating")
  public BigDecimal getRating() {
    return rating;
  }

  public void setRating(BigDecimal rating) {
    this.rating = rating;
  }

  public Reviews text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  @NotNull 
  @Schema(name = "text", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Reviews title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Reviews tripType(String tripType) {
    this.tripType = tripType;
    return this;
  }

  /**
   * Get tripType
   * @return tripType
   */
  @NotNull 
  @Schema(name = "tripType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tripType")
  public String getTripType() {
    return tripType;
  }

  public void setTripType(String tripType) {
    this.tripType = tripType;
  }

  public Reviews user(User user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
   */
  @NotNull @Valid 
  @Schema(name = "user", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("user")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Reviews reviews = (Reviews) o;
    return Objects.equals(this.publishedDate, reviews.publishedDate) &&
        Objects.equals(this.rating, reviews.rating) &&
        Objects.equals(this.text, reviews.text) &&
        Objects.equals(this.title, reviews.title) &&
        Objects.equals(this.tripType, reviews.tripType) &&
        Objects.equals(this.user, reviews.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publishedDate, rating, text, title, tripType, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Reviews {\n");
    sb.append("    publishedDate: ").append(toIndentedString(publishedDate)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    tripType: ").append(toIndentedString(tripType)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
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

