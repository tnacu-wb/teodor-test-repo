package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information regarding the lead guest of the booking.
 */

@Schema(name = "Guest", description = "Information regarding the lead guest of the booking.")
@JsonTypeName("Guest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GuestDto {

  private @Nullable String name;

  private @Nullable Integer previousBookings;

  private @Nullable Boolean registered;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate registeredSince;

  public GuestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Full name of guest.
   * @return name
   */
  
  @Schema(name = "name", example = "Mr James Bond", description = "Full name of guest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GuestDto previousBookings(Integer previousBookings) {
    this.previousBookings = previousBookings;
    return this;
  }

  /**
   * How many previous bookings the customer has made.
   * @return previousBookings
   */
  
  @Schema(name = "previousBookings", example = "10", description = "How many previous bookings the customer has made.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("previousBookings")
  public Integer getPreviousBookings() {
    return previousBookings;
  }

  public void setPreviousBookings(Integer previousBookings) {
    this.previousBookings = previousBookings;
  }

  public GuestDto registered(Boolean registered) {
    this.registered = registered;
    return this;
  }

  /**
   * Whether the guest is registered with a Premier Inn account.
   * @return registered
   */
  
  @Schema(name = "registered", example = "true", description = "Whether the guest is registered with a Premier Inn account.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("registered")
  public Boolean getRegistered() {
    return registered;
  }

  public void setRegistered(Boolean registered) {
    this.registered = registered;
  }

  public GuestDto registeredSince(LocalDate registeredSince) {
    this.registeredSince = registeredSince;
    return this;
  }

  /**
   * How long the guest has been registered with a Premier Inn Account.
   * @return registeredSince
   */
  @Valid 
  @Schema(name = "registeredSince", example = "2021-11-26", description = "How long the guest has been registered with a Premier Inn Account.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("registeredSince")
  public LocalDate getRegisteredSince() {
    return registeredSince;
  }

  public void setRegisteredSince(LocalDate registeredSince) {
    this.registeredSince = registeredSince;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GuestDto guest = (GuestDto) o;
    return Objects.equals(this.name, guest.name) &&
        Objects.equals(this.previousBookings, guest.previousBookings) &&
        Objects.equals(this.registered, guest.registered) &&
        Objects.equals(this.registeredSince, guest.registeredSince);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, previousBookings, registered, registeredSince);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GuestDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    previousBookings: ").append(toIndentedString(previousBookings)).append("\n");
    sb.append("    registered: ").append(toIndentedString(registered)).append("\n");
    sb.append("    registeredSince: ").append(toIndentedString(registeredSince)).append("\n");
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

