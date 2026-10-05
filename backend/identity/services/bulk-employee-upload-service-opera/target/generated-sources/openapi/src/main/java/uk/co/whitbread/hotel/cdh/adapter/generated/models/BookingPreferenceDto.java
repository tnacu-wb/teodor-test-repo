package uk.co.whitbread.hotel.cdh.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.RoomRequirementsDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingPreferenceDto {

  private @Nullable Boolean continentalBreakfast;

  private @Nullable Boolean electronicInvoiceRequired;

  private @Nullable Boolean mealDeal;

  private @Nullable Boolean premierBreakfast;

  private @Nullable Boolean preselectWifi;

  private @Nullable String reason;

  private @Nullable RoomRequirementsDto roomRequirements;

  public BookingPreferenceDto continentalBreakfast(Boolean continentalBreakfast) {
    this.continentalBreakfast = continentalBreakfast;
    return this;
  }

  /**
   * Get continentalBreakfast
   * @return continentalBreakfast
   */
  
  @Schema(name = "continentalBreakfast", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continentalBreakfast")
  public Boolean getContinentalBreakfast() {
    return continentalBreakfast;
  }

  public void setContinentalBreakfast(Boolean continentalBreakfast) {
    this.continentalBreakfast = continentalBreakfast;
  }

  public BookingPreferenceDto electronicInvoiceRequired(Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
    return this;
  }

  /**
   * Get electronicInvoiceRequired
   * @return electronicInvoiceRequired
   */
  
  @Schema(name = "electronicInvoiceRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("electronicInvoiceRequired")
  public Boolean getElectronicInvoiceRequired() {
    return electronicInvoiceRequired;
  }

  public void setElectronicInvoiceRequired(Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
  }

  public BookingPreferenceDto mealDeal(Boolean mealDeal) {
    this.mealDeal = mealDeal;
    return this;
  }

  /**
   * Get mealDeal
   * @return mealDeal
   */
  
  @Schema(name = "mealDeal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealDeal")
  public Boolean getMealDeal() {
    return mealDeal;
  }

  public void setMealDeal(Boolean mealDeal) {
    this.mealDeal = mealDeal;
  }

  public BookingPreferenceDto premierBreakfast(Boolean premierBreakfast) {
    this.premierBreakfast = premierBreakfast;
    return this;
  }

  /**
   * Get premierBreakfast
   * @return premierBreakfast
   */
  
  @Schema(name = "premierBreakfast", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("premierBreakfast")
  public Boolean getPremierBreakfast() {
    return premierBreakfast;
  }

  public void setPremierBreakfast(Boolean premierBreakfast) {
    this.premierBreakfast = premierBreakfast;
  }

  public BookingPreferenceDto preselectWifi(Boolean preselectWifi) {
    this.preselectWifi = preselectWifi;
    return this;
  }

  /**
   * Get preselectWifi
   * @return preselectWifi
   */
  
  @Schema(name = "preselectWifi", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preselectWifi")
  public Boolean getPreselectWifi() {
    return preselectWifi;
  }

  public void setPreselectWifi(Boolean preselectWifi) {
    this.preselectWifi = preselectWifi;
  }

  public BookingPreferenceDto reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   */
  
  @Schema(name = "reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public BookingPreferenceDto roomRequirements(RoomRequirementsDto roomRequirements) {
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
  public RoomRequirementsDto getRoomRequirements() {
    return roomRequirements;
  }

  public void setRoomRequirements(RoomRequirementsDto roomRequirements) {
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

