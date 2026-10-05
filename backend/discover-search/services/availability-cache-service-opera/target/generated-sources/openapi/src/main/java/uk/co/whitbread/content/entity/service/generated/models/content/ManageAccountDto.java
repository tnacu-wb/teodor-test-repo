package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.CardsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.EmployeesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ProfileDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ManageAccountDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManageAccountDto {

  private @Nullable CardsDto cards;

  private @Nullable EmployeesDto employees;

  private @Nullable ProfileDto profile;

  private @Nullable String title;

  public ManageAccountDto cards(CardsDto cards) {
    this.cards = cards;
    return this;
  }

  /**
   * Get cards
   * @return cards
   */
  @Valid 
  @Schema(name = "cards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cards")
  public CardsDto getCards() {
    return cards;
  }

  public void setCards(CardsDto cards) {
    this.cards = cards;
  }

  public ManageAccountDto employees(EmployeesDto employees) {
    this.employees = employees;
    return this;
  }

  /**
   * Get employees
   * @return employees
   */
  @Valid 
  @Schema(name = "employees", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employees")
  public EmployeesDto getEmployees() {
    return employees;
  }

  public void setEmployees(EmployeesDto employees) {
    this.employees = employees;
  }

  public ManageAccountDto profile(ProfileDto profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
   */
  @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profile")
  public ProfileDto getProfile() {
    return profile;
  }

  public void setProfile(ProfileDto profile) {
    this.profile = profile;
  }

  public ManageAccountDto title(String title) {
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
    ManageAccountDto manageAccountDto = (ManageAccountDto) o;
    return Objects.equals(this.cards, manageAccountDto.cards) &&
        Objects.equals(this.employees, manageAccountDto.employees) &&
        Objects.equals(this.profile, manageAccountDto.profile) &&
        Objects.equals(this.title, manageAccountDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cards, employees, profile, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManageAccountDto {\n");
    sb.append("    cards: ").append(toIndentedString(cards)).append("\n");
    sb.append("    employees: ").append(toIndentedString(employees)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
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

