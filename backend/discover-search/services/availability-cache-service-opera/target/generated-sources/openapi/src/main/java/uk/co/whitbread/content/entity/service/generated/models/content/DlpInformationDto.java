package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BreadcrumbDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CoordinatesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.DlpDto;
import uk.co.whitbread.content.entity.service.generated.models.content.FaqDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PromoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SeoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ThingsToDoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.WhyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DlpInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DlpInformationDto {

  @Valid
  private List<@Valid BreadcrumbDto> breadcrumbs = new ArrayList<>();

  private @Nullable CoordinatesDto coordinates;

  private @Nullable String description;

  private @Nullable DlpDto dlps;

  @Valid
  private List<@Valid FaqDto> faq = new ArrayList<>();

  @Valid
  private List<@Valid HotelDto> hotels = new ArrayList<>();

  private @Nullable String picture;

  @Valid
  private List<@Valid PromoDto> promos = new ArrayList<>();

  private @Nullable SeoDto seo;

  private @Nullable ThingsToDoDto thingsToDo;

  private @Nullable String title;

  private @Nullable WhyDto why;

  public DlpInformationDto breadcrumbs(List<@Valid BreadcrumbDto> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
    return this;
  }

  public DlpInformationDto addBreadcrumbsItem(BreadcrumbDto breadcrumbsItem) {
    if (this.breadcrumbs == null) {
      this.breadcrumbs = new ArrayList<>();
    }
    this.breadcrumbs.add(breadcrumbsItem);
    return this;
  }

  /**
   * Get breadcrumbs
   * @return breadcrumbs
   */
  @Valid 
  @Schema(name = "breadcrumbs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("breadcrumbs")
  public List<@Valid BreadcrumbDto> getBreadcrumbs() {
    return breadcrumbs;
  }

  public void setBreadcrumbs(List<@Valid BreadcrumbDto> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
  }

  public DlpInformationDto coordinates(CoordinatesDto coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  /**
   * Get coordinates
   * @return coordinates
   */
  @Valid 
  @Schema(name = "coordinates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coordinates")
  public CoordinatesDto getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(CoordinatesDto coordinates) {
    this.coordinates = coordinates;
  }

  public DlpInformationDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DlpInformationDto dlps(DlpDto dlps) {
    this.dlps = dlps;
    return this;
  }

  /**
   * Get dlps
   * @return dlps
   */
  @Valid 
  @Schema(name = "dlps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dlps")
  public DlpDto getDlps() {
    return dlps;
  }

  public void setDlps(DlpDto dlps) {
    this.dlps = dlps;
  }

  public DlpInformationDto faq(List<@Valid FaqDto> faq) {
    this.faq = faq;
    return this;
  }

  public DlpInformationDto addFaqItem(FaqDto faqItem) {
    if (this.faq == null) {
      this.faq = new ArrayList<>();
    }
    this.faq.add(faqItem);
    return this;
  }

  /**
   * Get faq
   * @return faq
   */
  @Valid 
  @Schema(name = "faq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faq")
  public List<@Valid FaqDto> getFaq() {
    return faq;
  }

  public void setFaq(List<@Valid FaqDto> faq) {
    this.faq = faq;
  }

  public DlpInformationDto hotels(List<@Valid HotelDto> hotels) {
    this.hotels = hotels;
    return this;
  }

  public DlpInformationDto addHotelsItem(HotelDto hotelsItem) {
    if (this.hotels == null) {
      this.hotels = new ArrayList<>();
    }
    this.hotels.add(hotelsItem);
    return this;
  }

  /**
   * Get hotels
   * @return hotels
   */
  @Valid 
  @Schema(name = "hotels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotels")
  public List<@Valid HotelDto> getHotels() {
    return hotels;
  }

  public void setHotels(List<@Valid HotelDto> hotels) {
    this.hotels = hotels;
  }

  public DlpInformationDto picture(String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Get picture
   * @return picture
   */
  
  @Schema(name = "picture", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("picture")
  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public DlpInformationDto promos(List<@Valid PromoDto> promos) {
    this.promos = promos;
    return this;
  }

  public DlpInformationDto addPromosItem(PromoDto promosItem) {
    if (this.promos == null) {
      this.promos = new ArrayList<>();
    }
    this.promos.add(promosItem);
    return this;
  }

  /**
   * Get promos
   * @return promos
   */
  @Valid 
  @Schema(name = "promos", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promos")
  public List<@Valid PromoDto> getPromos() {
    return promos;
  }

  public void setPromos(List<@Valid PromoDto> promos) {
    this.promos = promos;
  }

  public DlpInformationDto seo(SeoDto seo) {
    this.seo = seo;
    return this;
  }

  /**
   * Get seo
   * @return seo
   */
  @Valid 
  @Schema(name = "seo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seo")
  public SeoDto getSeo() {
    return seo;
  }

  public void setSeo(SeoDto seo) {
    this.seo = seo;
  }

  public DlpInformationDto thingsToDo(ThingsToDoDto thingsToDo) {
    this.thingsToDo = thingsToDo;
    return this;
  }

  /**
   * Get thingsToDo
   * @return thingsToDo
   */
  @Valid 
  @Schema(name = "thingsToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thingsToDo")
  public ThingsToDoDto getThingsToDo() {
    return thingsToDo;
  }

  public void setThingsToDo(ThingsToDoDto thingsToDo) {
    this.thingsToDo = thingsToDo;
  }

  public DlpInformationDto title(String title) {
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

  public DlpInformationDto why(WhyDto why) {
    this.why = why;
    return this;
  }

  /**
   * Get why
   * @return why
   */
  @Valid 
  @Schema(name = "why", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("why")
  public WhyDto getWhy() {
    return why;
  }

  public void setWhy(WhyDto why) {
    this.why = why;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DlpInformationDto dlpInformationDto = (DlpInformationDto) o;
    return Objects.equals(this.breadcrumbs, dlpInformationDto.breadcrumbs) &&
        Objects.equals(this.coordinates, dlpInformationDto.coordinates) &&
        Objects.equals(this.description, dlpInformationDto.description) &&
        Objects.equals(this.dlps, dlpInformationDto.dlps) &&
        Objects.equals(this.faq, dlpInformationDto.faq) &&
        Objects.equals(this.hotels, dlpInformationDto.hotels) &&
        Objects.equals(this.picture, dlpInformationDto.picture) &&
        Objects.equals(this.promos, dlpInformationDto.promos) &&
        Objects.equals(this.seo, dlpInformationDto.seo) &&
        Objects.equals(this.thingsToDo, dlpInformationDto.thingsToDo) &&
        Objects.equals(this.title, dlpInformationDto.title) &&
        Objects.equals(this.why, dlpInformationDto.why);
  }

  @Override
  public int hashCode() {
    return Objects.hash(breadcrumbs, coordinates, description, dlps, faq, hotels, picture, promos, seo, thingsToDo, title, why);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DlpInformationDto {\n");
    sb.append("    breadcrumbs: ").append(toIndentedString(breadcrumbs)).append("\n");
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    dlps: ").append(toIndentedString(dlps)).append("\n");
    sb.append("    faq: ").append(toIndentedString(faq)).append("\n");
    sb.append("    hotels: ").append(toIndentedString(hotels)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    promos: ").append(toIndentedString(promos)).append("\n");
    sb.append("    seo: ").append(toIndentedString(seo)).append("\n");
    sb.append("    thingsToDo: ").append(toIndentedString(thingsToDo)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    why: ").append(toIndentedString(why)).append("\n");
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

