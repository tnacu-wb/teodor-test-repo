package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.PriceCapLocationsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingAllowancesDto
 */

@JsonTypeName("BookingAllowances")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingAllowancesDto {

  private @Nullable Boolean allowAdditionalCosts;

  private @Nullable Boolean allowAlcohol;

  private @Nullable Boolean allowCarParking;

  private @Nullable Boolean allowIndividualCards;

  private @Nullable Boolean allowPremierSaverRates;

  @Valid
  private List<String> extrasCodes = new ArrayList<>();

  private @Nullable PriceCapLocationsDto maxDinnerBudgets;

  private @Nullable Long maxNumberOfNights;

  @Valid
  private List<String> upsellItemsAllowed = new ArrayList<>();

  public BookingAllowancesDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingAllowancesDto(List<String> extrasCodes) {
    this.extrasCodes = extrasCodes;
  }

  public BookingAllowancesDto allowAdditionalCosts(Boolean allowAdditionalCosts) {
    this.allowAdditionalCosts = allowAdditionalCosts;
    return this;
  }

  /**
   * Get allowAdditionalCosts
   * @return allowAdditionalCosts
   */
  
  @Schema(name = "allowAdditionalCosts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowAdditionalCosts")
  public Boolean getAllowAdditionalCosts() {
    return allowAdditionalCosts;
  }

  public void setAllowAdditionalCosts(Boolean allowAdditionalCosts) {
    this.allowAdditionalCosts = allowAdditionalCosts;
  }

  public BookingAllowancesDto allowAlcohol(Boolean allowAlcohol) {
    this.allowAlcohol = allowAlcohol;
    return this;
  }

  /**
   * Get allowAlcohol
   * @return allowAlcohol
   */
  
  @Schema(name = "allowAlcohol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowAlcohol")
  public Boolean getAllowAlcohol() {
    return allowAlcohol;
  }

  public void setAllowAlcohol(Boolean allowAlcohol) {
    this.allowAlcohol = allowAlcohol;
  }

  public BookingAllowancesDto allowCarParking(Boolean allowCarParking) {
    this.allowCarParking = allowCarParking;
    return this;
  }

  /**
   * Get allowCarParking
   * @return allowCarParking
   */
  
  @Schema(name = "allowCarParking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowCarParking")
  public Boolean getAllowCarParking() {
    return allowCarParking;
  }

  public void setAllowCarParking(Boolean allowCarParking) {
    this.allowCarParking = allowCarParking;
  }

  public BookingAllowancesDto allowIndividualCards(Boolean allowIndividualCards) {
    this.allowIndividualCards = allowIndividualCards;
    return this;
  }

  /**
   * Get allowIndividualCards
   * @return allowIndividualCards
   */
  
  @Schema(name = "allowIndividualCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowIndividualCards")
  public Boolean getAllowIndividualCards() {
    return allowIndividualCards;
  }

  public void setAllowIndividualCards(Boolean allowIndividualCards) {
    this.allowIndividualCards = allowIndividualCards;
  }

  public BookingAllowancesDto allowPremierSaverRates(Boolean allowPremierSaverRates) {
    this.allowPremierSaverRates = allowPremierSaverRates;
    return this;
  }

  /**
   * Get allowPremierSaverRates
   * @return allowPremierSaverRates
   */
  
  @Schema(name = "allowPremierSaverRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowPremierSaverRates")
  public Boolean getAllowPremierSaverRates() {
    return allowPremierSaverRates;
  }

  public void setAllowPremierSaverRates(Boolean allowPremierSaverRates) {
    this.allowPremierSaverRates = allowPremierSaverRates;
  }

  public BookingAllowancesDto extrasCodes(List<String> extrasCodes) {
    this.extrasCodes = extrasCodes;
    return this;
  }

  public BookingAllowancesDto addExtrasCodesItem(String extrasCodesItem) {
    if (this.extrasCodes == null) {
      this.extrasCodes = new ArrayList<>();
    }
    this.extrasCodes.add(extrasCodesItem);
    return this;
  }

  /**
   * Get extrasCodes
   * @return extrasCodes
   */
  @NotNull 
  @Schema(name = "extrasCodes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("extrasCodes")
  public List<String> getExtrasCodes() {
    return extrasCodes;
  }

  public void setExtrasCodes(List<String> extrasCodes) {
    this.extrasCodes = extrasCodes;
  }

  public BookingAllowancesDto maxDinnerBudgets(PriceCapLocationsDto maxDinnerBudgets) {
    this.maxDinnerBudgets = maxDinnerBudgets;
    return this;
  }

  /**
   * Get maxDinnerBudgets
   * @return maxDinnerBudgets
   */
  @Valid 
  @Schema(name = "maxDinnerBudgets", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxDinnerBudgets")
  public PriceCapLocationsDto getMaxDinnerBudgets() {
    return maxDinnerBudgets;
  }

  public void setMaxDinnerBudgets(PriceCapLocationsDto maxDinnerBudgets) {
    this.maxDinnerBudgets = maxDinnerBudgets;
  }

  public BookingAllowancesDto maxNumberOfNights(Long maxNumberOfNights) {
    this.maxNumberOfNights = maxNumberOfNights;
    return this;
  }

  /**
   * Get maxNumberOfNights
   * @return maxNumberOfNights
   */
  
  @Schema(name = "maxNumberOfNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxNumberOfNights")
  public Long getMaxNumberOfNights() {
    return maxNumberOfNights;
  }

  public void setMaxNumberOfNights(Long maxNumberOfNights) {
    this.maxNumberOfNights = maxNumberOfNights;
  }

  public BookingAllowancesDto upsellItemsAllowed(List<String> upsellItemsAllowed) {
    this.upsellItemsAllowed = upsellItemsAllowed;
    return this;
  }

  public BookingAllowancesDto addUpsellItemsAllowedItem(String upsellItemsAllowedItem) {
    if (this.upsellItemsAllowed == null) {
      this.upsellItemsAllowed = new ArrayList<>();
    }
    this.upsellItemsAllowed.add(upsellItemsAllowedItem);
    return this;
  }

  /**
   * Get upsellItemsAllowed
   * @return upsellItemsAllowed
   */
  
  @Schema(name = "upsellItemsAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellItemsAllowed")
  public List<String> getUpsellItemsAllowed() {
    return upsellItemsAllowed;
  }

  public void setUpsellItemsAllowed(List<String> upsellItemsAllowed) {
    this.upsellItemsAllowed = upsellItemsAllowed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingAllowancesDto bookingAllowances = (BookingAllowancesDto) o;
    return Objects.equals(this.allowAdditionalCosts, bookingAllowances.allowAdditionalCosts) &&
        Objects.equals(this.allowAlcohol, bookingAllowances.allowAlcohol) &&
        Objects.equals(this.allowCarParking, bookingAllowances.allowCarParking) &&
        Objects.equals(this.allowIndividualCards, bookingAllowances.allowIndividualCards) &&
        Objects.equals(this.allowPremierSaverRates, bookingAllowances.allowPremierSaverRates) &&
        Objects.equals(this.extrasCodes, bookingAllowances.extrasCodes) &&
        Objects.equals(this.maxDinnerBudgets, bookingAllowances.maxDinnerBudgets) &&
        Objects.equals(this.maxNumberOfNights, bookingAllowances.maxNumberOfNights) &&
        Objects.equals(this.upsellItemsAllowed, bookingAllowances.upsellItemsAllowed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowAdditionalCosts, allowAlcohol, allowCarParking, allowIndividualCards, allowPremierSaverRates, extrasCodes, maxDinnerBudgets, maxNumberOfNights, upsellItemsAllowed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingAllowancesDto {\n");
    sb.append("    allowAdditionalCosts: ").append(toIndentedString(allowAdditionalCosts)).append("\n");
    sb.append("    allowAlcohol: ").append(toIndentedString(allowAlcohol)).append("\n");
    sb.append("    allowCarParking: ").append(toIndentedString(allowCarParking)).append("\n");
    sb.append("    allowIndividualCards: ").append(toIndentedString(allowIndividualCards)).append("\n");
    sb.append("    allowPremierSaverRates: ").append(toIndentedString(allowPremierSaverRates)).append("\n");
    sb.append("    extrasCodes: ").append(toIndentedString(extrasCodes)).append("\n");
    sb.append("    maxDinnerBudgets: ").append(toIndentedString(maxDinnerBudgets)).append("\n");
    sb.append("    maxNumberOfNights: ").append(toIndentedString(maxNumberOfNights)).append("\n");
    sb.append("    upsellItemsAllowed: ").append(toIndentedString(upsellItemsAllowed)).append("\n");
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

