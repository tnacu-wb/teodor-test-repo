package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.ContactDto;
import uk.co.whitbread.content.entity.service.generated.models.content.FaqDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TourDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HelpDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HelpDto {

  private @Nullable ContactDto contact;

  private @Nullable FaqDto faq;

  private @Nullable String needHelp;

  private @Nullable TourDto tour;

  public HelpDto contact(ContactDto contact) {
    this.contact = contact;
    return this;
  }

  /**
   * Get contact
   * @return contact
   */
  @Valid 
  @Schema(name = "contact", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contact")
  public ContactDto getContact() {
    return contact;
  }

  public void setContact(ContactDto contact) {
    this.contact = contact;
  }

  public HelpDto faq(FaqDto faq) {
    this.faq = faq;
    return this;
  }

  /**
   * Get faq
   * @return faq
   */
  @Valid 
  @Schema(name = "faq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faq")
  public FaqDto getFaq() {
    return faq;
  }

  public void setFaq(FaqDto faq) {
    this.faq = faq;
  }

  public HelpDto needHelp(String needHelp) {
    this.needHelp = needHelp;
    return this;
  }

  /**
   * Get needHelp
   * @return needHelp
   */
  
  @Schema(name = "needHelp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("needHelp")
  public String getNeedHelp() {
    return needHelp;
  }

  public void setNeedHelp(String needHelp) {
    this.needHelp = needHelp;
  }

  public HelpDto tour(TourDto tour) {
    this.tour = tour;
    return this;
  }

  /**
   * Get tour
   * @return tour
   */
  @Valid 
  @Schema(name = "tour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tour")
  public TourDto getTour() {
    return tour;
  }

  public void setTour(TourDto tour) {
    this.tour = tour;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HelpDto helpDto = (HelpDto) o;
    return Objects.equals(this.contact, helpDto.contact) &&
        Objects.equals(this.faq, helpDto.faq) &&
        Objects.equals(this.needHelp, helpDto.needHelp) &&
        Objects.equals(this.tour, helpDto.tour);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contact, faq, needHelp, tour);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HelpDto {\n");
    sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
    sb.append("    faq: ").append(toIndentedString(faq)).append("\n");
    sb.append("    needHelp: ").append(toIndentedString(needHelp)).append("\n");
    sb.append("    tour: ").append(toIndentedString(tour)).append("\n");
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

