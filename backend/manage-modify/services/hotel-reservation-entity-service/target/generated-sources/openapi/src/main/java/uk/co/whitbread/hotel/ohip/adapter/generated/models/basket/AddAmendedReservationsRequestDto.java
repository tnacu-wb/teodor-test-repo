package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AddAmendedReservationsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddAmendedReservationsRequestDto {

  private String channel;

  @Valid
  private Map<String, String> linkAmendReservations = new HashMap<>();

  public AddAmendedReservationsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddAmendedReservationsRequestDto(String channel, Map<String, String> linkAmendReservations) {
    this.channel = channel;
    this.linkAmendReservations = linkAmendReservations;
  }

  public AddAmendedReservationsRequestDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public AddAmendedReservationsRequestDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public AddAmendedReservationsRequestDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
    if (this.linkAmendReservations == null) {
      this.linkAmendReservations = new HashMap<>();
    }
    this.linkAmendReservations.put(key, linkAmendReservationsItem);
    return this;
  }

  /**
   * Get linkAmendReservations
   * @return linkAmendReservations
   */
  @NotNull 
  @Schema(name = "linkAmendReservations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("linkAmendReservations")
  public Map<String, String> getLinkAmendReservations() {
    return linkAmendReservations;
  }

  public void setLinkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddAmendedReservationsRequestDto addAmendedReservationsRequestDto = (AddAmendedReservationsRequestDto) o;
    return Objects.equals(this.channel, addAmendedReservationsRequestDto.channel) &&
        Objects.equals(this.linkAmendReservations, addAmendedReservationsRequestDto.linkAmendReservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, linkAmendReservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddAmendedReservationsRequestDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
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

