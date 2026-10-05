package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.RoomRequirementsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingPreferenceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class BookingPreferenceDto {

  private @Nullable Boolean continentalBreakfast;

  private @Nullable Boolean electronicInvoiceRequired;

  private @Nullable Boolean mealDeal;

  private @Nullable Boolean premierBreakfast;

  private @Nullable Boolean preselectWifi;

  private @Nullable String reason;

  private @Nullable RoomRequirementsDto roomRequirements;

  public BookingPreferenceDto continentalBreakfast(@Nullable Boolean continentalBreakfast) {
    this.continentalBreakfast = continentalBreakfast;
    return this;
  }

  /**
   * Get continentalBreakfast
   * @return continentalBreakfast
   */
  
  @Schema(name = "continentalBreakfast", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continentalBreakfast")
  public @Nullable Boolean getContinentalBreakfast() {
    return continentalBreakfast;
  }

  public void setContinentalBreakfast(@Nullable Boolean continentalBreakfast) {
    this.continentalBreakfast = continentalBreakfast;
  }

  public BookingPreferenceDto electronicInvoiceRequired(@Nullable Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
    return this;
  }

  /**
   * Get electronicInvoiceRequired
   * @return electronicInvoiceRequired
   */
  
  @Schema(name = "electronicInvoiceRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("electronicInvoiceRequired")
  public @Nullable Boolean getElectronicInvoiceRequired() {
    return electronicInvoiceRequired;
  }

  public void setElectronicInvoiceRequired(@Nullable Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
  }

  public BookingPreferenceDto mealDeal(@Nullable Boolean mealDeal) {
    this.mealDeal = mealDeal;
    return this;
  }

  /**
   * Get mealDeal
   * @return mealDeal
   */
  
  @Schema(name = "mealDeal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealDeal")
  public @Nullable Boolean getMealDeal() {
    return mealDeal;
  }

  public void setMealDeal(@Nullable Boolean mealDeal) {
    this.mealDeal = mealDeal;
  }

  public BookingPreferenceDto premierBreakfast(@Nullable Boolean premierBreakfast) {
    this.premierBreakfast = premierBreakfast;
    return this;
  }

  /**
   * Get premierBreakfast
   * @return premierBreakfast
   */
  
  @Schema(name = "premierBreakfast", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("premierBreakfast")
  public @Nullable Boolean getPremierBreakfast() {
    return premierBreakfast;
  }

  public void setPremierBreakfast(@Nullable Boolean premierBreakfast) {
    this.premierBreakfast = premierBreakfast;
  }

  public BookingPreferenceDto preselectWifi(@Nullable Boolean preselectWifi) {
    this.preselectWifi = preselectWifi;
    return this;
  }

  /**
   * Get preselectWifi
   * @return preselectWifi
   */
  
  @Schema(name = "preselectWifi", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preselectWifi")
  public @Nullable Boolean getPreselectWifi() {
    return preselectWifi;
  }

  public void setPreselectWifi(@Nullable Boolean preselectWifi) {
    this.preselectWifi = preselectWifi;
  }

  public BookingPreferenceDto reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   */
  
  @Schema(name = "reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  public BookingPreferenceDto roomRequirements(@Nullable RoomRequirementsDto roomRequirements) {
    this.roomRequirements = roomRequirements;
    return this;
  }

  /**
   * Get roomRequirements
   * @return roomRequirements
   */
  @Valid 
  @Schema(name = "roomRequirements", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRequirements")
  public @Nullable RoomRequirementsDto getRoomRequirements() {
    return roomRequirements;
  }

  public void setRoomRequirements(@Nullable RoomRequirementsDto roomRequirements) {
    this.roomRequirements = roomRequirements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingPreferenceDto bookingPreferenceDto = (BookingPreferenceDto) o;
    return Objects.equals(this.continentalBreakfast, bookingPreferenceDto.continentalBreakfast) &&
        Objects.equals(this.electronicInvoiceRequired, bookingPreferenceDto.electronicInvoiceRequired) &&
        Objects.equals(this.mealDeal, bookingPreferenceDto.mealDeal) &&
        Objects.equals(this.premierBreakfast, bookingPreferenceDto.premierBreakfast) &&
        Objects.equals(this.preselectWifi, bookingPreferenceDto.preselectWifi) &&
        Objects.equals(this.reason, bookingPreferenceDto.reason) &&
        Objects.equals(this.roomRequirements, bookingPreferenceDto.roomRequirements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(continentalBreakfast, electronicInvoiceRequired, mealDeal, premierBreakfast, preselectWifi, reason, roomRequirements);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingPreferenceDto {\n");
    sb.append("    continentalBreakfast: ").append(toIndentedString(continentalBreakfast)).append("\n");
    sb.append("    electronicInvoiceRequired: ").append(toIndentedString(electronicInvoiceRequired)).append("\n");
    sb.append("    mealDeal: ").append(toIndentedString(mealDeal)).append("\n");
    sb.append("    premierBreakfast: ").append(toIndentedString(premierBreakfast)).append("\n");
    sb.append("    preselectWifi: ").append(toIndentedString(preselectWifi)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    roomRequirements: ").append(toIndentedString(roomRequirements)).append("\n");
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

