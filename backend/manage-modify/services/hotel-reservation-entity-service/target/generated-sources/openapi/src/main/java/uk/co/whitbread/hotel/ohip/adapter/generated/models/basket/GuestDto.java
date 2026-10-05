package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * GuestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
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
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
   * Get previousBookings
   * @return previousBookings
   */
  
  @Schema(name = "previousBookings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
   * Get registered
   * @return registered
   */
  
  @Schema(name = "registered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
   * Get registeredSince
   * @return registeredSince
   */
  @Valid 
  @Schema(name = "registeredSince", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    GuestDto guestDto = (GuestDto) o;
    return Objects.equals(this.name, guestDto.name) &&
        Objects.equals(this.previousBookings, guestDto.previousBookings) &&
        Objects.equals(this.registered, guestDto.registered) &&
        Objects.equals(this.registeredSince, guestDto.registeredSince);
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

