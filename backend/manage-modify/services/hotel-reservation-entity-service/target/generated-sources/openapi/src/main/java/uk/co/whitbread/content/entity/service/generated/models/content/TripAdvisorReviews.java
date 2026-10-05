package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.Awards;
import uk.co.whitbread.content.entity.service.generated.models.content.Reviews;
import uk.co.whitbread.content.entity.service.generated.models.content.SubRatings;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TripAdvisorReviews
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TripAdvisorReviews {

  private String address;

  @Valid
  private List<@Valid Awards> awards = new ArrayList<>();

  private String hotelCode;

  private String locationId;

  private String name;

  private Integer numberOfReviews;

  private BigDecimal rating;

  @Valid
  private List<@Valid Reviews> reviews = new ArrayList<>();

  @Valid
  private List<@Valid SubRatings> subRatings = new ArrayList<>();

  private String webUrl;

  private String writeReview;

  public TripAdvisorReviews() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TripAdvisorReviews(String address, List<@Valid Awards> awards, String hotelCode, String locationId, String name, Integer numberOfReviews, BigDecimal rating, List<@Valid Reviews> reviews, List<@Valid SubRatings> subRatings, String webUrl, String writeReview) {
    this.address = address;
    this.awards = awards;
    this.hotelCode = hotelCode;
    this.locationId = locationId;
    this.name = name;
    this.numberOfReviews = numberOfReviews;
    this.rating = rating;
    this.reviews = reviews;
    this.subRatings = subRatings;
    this.webUrl = webUrl;
    this.writeReview = writeReview;
  }

  public TripAdvisorReviews address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @NotNull 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public TripAdvisorReviews awards(List<@Valid Awards> awards) {
    this.awards = awards;
    return this;
  }

  public TripAdvisorReviews addAwardsItem(Awards awardsItem) {
    if (this.awards == null) {
      this.awards = new ArrayList<>();
    }
    this.awards.add(awardsItem);
    return this;
  }

  /**
   * Get awards
   * @return awards
   */
  @NotNull @Valid 
  @Schema(name = "awards", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("awards")
  public List<@Valid Awards> getAwards() {
    return awards;
  }

  public void setAwards(List<@Valid Awards> awards) {
    this.awards = awards;
  }

  public TripAdvisorReviews hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  @NotNull 
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public TripAdvisorReviews locationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  /**
   * Get locationId
   * @return locationId
   */
  @NotNull 
  @Schema(name = "locationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("locationId")
  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public TripAdvisorReviews name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TripAdvisorReviews numberOfReviews(Integer numberOfReviews) {
    this.numberOfReviews = numberOfReviews;
    return this;
  }

  /**
   * Get numberOfReviews
   * @return numberOfReviews
   */
  @NotNull 
  @Schema(name = "numberOfReviews", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("numberOfReviews")
  public Integer getNumberOfReviews() {
    return numberOfReviews;
  }

  public void setNumberOfReviews(Integer numberOfReviews) {
    this.numberOfReviews = numberOfReviews;
  }

  public TripAdvisorReviews rating(BigDecimal rating) {
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

  public TripAdvisorReviews reviews(List<@Valid Reviews> reviews) {
    this.reviews = reviews;
    return this;
  }

  public TripAdvisorReviews addReviewsItem(Reviews reviewsItem) {
    if (this.reviews == null) {
      this.reviews = new ArrayList<>();
    }
    this.reviews.add(reviewsItem);
    return this;
  }

  /**
   * Get reviews
   * @return reviews
   */
  @NotNull @Valid 
  @Schema(name = "reviews", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reviews")
  public List<@Valid Reviews> getReviews() {
    return reviews;
  }

  public void setReviews(List<@Valid Reviews> reviews) {
    this.reviews = reviews;
  }

  public TripAdvisorReviews subRatings(List<@Valid SubRatings> subRatings) {
    this.subRatings = subRatings;
    return this;
  }

  public TripAdvisorReviews addSubRatingsItem(SubRatings subRatingsItem) {
    if (this.subRatings == null) {
      this.subRatings = new ArrayList<>();
    }
    this.subRatings.add(subRatingsItem);
    return this;
  }

  /**
   * Get subRatings
   * @return subRatings
   */
  @NotNull @Valid 
  @Schema(name = "subRatings", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subRatings")
  public List<@Valid SubRatings> getSubRatings() {
    return subRatings;
  }

  public void setSubRatings(List<@Valid SubRatings> subRatings) {
    this.subRatings = subRatings;
  }

  public TripAdvisorReviews webUrl(String webUrl) {
    this.webUrl = webUrl;
    return this;
  }

  /**
   * Get webUrl
   * @return webUrl
   */
  @NotNull 
  @Schema(name = "webUrl", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("webUrl")
  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public TripAdvisorReviews writeReview(String writeReview) {
    this.writeReview = writeReview;
    return this;
  }

  /**
   * Get writeReview
   * @return writeReview
   */
  @NotNull 
  @Schema(name = "writeReview", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("writeReview")
  public String getWriteReview() {
    return writeReview;
  }

  public void setWriteReview(String writeReview) {
    this.writeReview = writeReview;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripAdvisorReviews tripAdvisorReviews = (TripAdvisorReviews) o;
    return Objects.equals(this.address, tripAdvisorReviews.address) &&
        Objects.equals(this.awards, tripAdvisorReviews.awards) &&
        Objects.equals(this.hotelCode, tripAdvisorReviews.hotelCode) &&
        Objects.equals(this.locationId, tripAdvisorReviews.locationId) &&
        Objects.equals(this.name, tripAdvisorReviews.name) &&
        Objects.equals(this.numberOfReviews, tripAdvisorReviews.numberOfReviews) &&
        Objects.equals(this.rating, tripAdvisorReviews.rating) &&
        Objects.equals(this.reviews, tripAdvisorReviews.reviews) &&
        Objects.equals(this.subRatings, tripAdvisorReviews.subRatings) &&
        Objects.equals(this.webUrl, tripAdvisorReviews.webUrl) &&
        Objects.equals(this.writeReview, tripAdvisorReviews.writeReview);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, awards, hotelCode, locationId, name, numberOfReviews, rating, reviews, subRatings, webUrl, writeReview);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TripAdvisorReviews {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    awards: ").append(toIndentedString(awards)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    locationId: ").append(toIndentedString(locationId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    numberOfReviews: ").append(toIndentedString(numberOfReviews)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
    sb.append("    reviews: ").append(toIndentedString(reviews)).append("\n");
    sb.append("    subRatings: ").append(toIndentedString(subRatings)).append("\n");
    sb.append("    webUrl: ").append(toIndentedString(webUrl)).append("\n");
    sb.append("    writeReview: ").append(toIndentedString(writeReview)).append("\n");
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

