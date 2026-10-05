package uk.co.whitbread.hotel.account.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.account.service.generated.models.RoomCriteria;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingPreference
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingPreference {

  private @Nullable Long foodPreference;

  private @Nullable Boolean preselectWifi;

  /**
   * Gets or Sets reason
   */
  public enum ReasonEnum {
    LEISURE("LEISURE"),
    
    BUSINESS("BUSINESS");

    private String value;

    ReasonEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ReasonEnum fromValue(String value) {
      for (ReasonEnum b : ReasonEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable ReasonEnum reason;

  private @Nullable RoomCriteria roomRequirements;

  private @Nullable Boolean wantSmsConfirmations;

  public BookingPreference foodPreference(Long foodPreference) {
    this.foodPreference = foodPreference;
    return this;
  }

  /**
   * Get foodPreference
   * @return foodPreference
   */
  
  @Schema(name = "foodPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("foodPreference")
  public Long getFoodPreference() {
    return foodPreference;
  }

  public void setFoodPreference(Long foodPreference) {
    this.foodPreference = foodPreference;
  }

  public BookingPreference preselectWifi(Boolean preselectWifi) {
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

  public BookingPreference reason(ReasonEnum reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   */
  
  @Schema(name = "reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public ReasonEnum getReason() {
    return reason;
  }

  public void setReason(ReasonEnum reason) {
    this.reason = reason;
  }

  public BookingPreference roomRequirements(RoomCriteria roomRequirements) {
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
  public RoomCriteria getRoomRequirements() {
    return roomRequirements;
  }

  public void setRoomRequirements(RoomCriteria roomRequirements) {
    this.roomRequirements = roomRequirements;
  }

  public BookingPreference wantSmsConfirmations(Boolean wantSmsConfirmations) {
    this.wantSmsConfirmations = wantSmsConfirmations;
    return this;
  }

  /**
   * Get wantSmsConfirmations
   * @return wantSmsConfirmations
   */
  
  @Schema(name = "wantSmsConfirmations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wantSmsConfirmations")
  public Boolean getWantSmsConfirmations() {
    return wantSmsConfirmations;
  }

  public void setWantSmsConfirmations(Boolean wantSmsConfirmations) {
    this.wantSmsConfirmations = wantSmsConfirmations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingPreference bookingPreference = (BookingPreference) o;
    return Objects.equals(this.foodPreference, bookingPreference.foodPreference) &&
        Objects.equals(this.preselectWifi, bookingPreference.preselectWifi) &&
        Objects.equals(this.reason, bookingPreference.reason) &&
        Objects.equals(this.roomRequirements, bookingPreference.roomRequirements) &&
        Objects.equals(this.wantSmsConfirmations, bookingPreference.wantSmsConfirmations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(foodPreference, preselectWifi, reason, roomRequirements, wantSmsConfirmations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingPreference {\n");
    sb.append("    foodPreference: ").append(toIndentedString(foodPreference)).append("\n");
    sb.append("    preselectWifi: ").append(toIndentedString(preselectWifi)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    roomRequirements: ").append(toIndentedString(roomRequirements)).append("\n");
    sb.append("    wantSmsConfirmations: ").append(toIndentedString(wantSmsConfirmations)).append("\n");
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

