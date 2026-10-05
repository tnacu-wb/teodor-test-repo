package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.BookingChannelSingleCall;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseSingleCall;
import uk.co.whitbread.basket.generated.models.ohip.UpdateReservationRequestSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationsRequestSingleCall
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationsRequestSingleCall {

  private @Nullable BookingChannelSingleCall bookingChannel;

  private @Nullable String companyId;

  private @Nullable String distributionIATANumber;

  @Valid
  private Map<String, String> linkAmendReservations = new HashMap<>();

  @Valid
  private List<@Valid UpdateReservationRequestSingleCall> reservations = new ArrayList<>();

  private @Nullable ReservationByBasketRefResponseSingleCall tempReservations;

  private @Nullable String token;

  public UpdateReservationsRequestSingleCall bookingChannel(BookingChannelSingleCall bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelSingleCall getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelSingleCall bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public UpdateReservationsRequestSingleCall companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public UpdateReservationsRequestSingleCall distributionIATANumber(String distributionIATANumber) {
    this.distributionIATANumber = distributionIATANumber;
    return this;
  }

  /**
   * Get distributionIATANumber
   * @return distributionIATANumber
   */
  
  @Schema(name = "distributionIATANumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distributionIATANumber")
  public String getDistributionIATANumber() {
    return distributionIATANumber;
  }

  public void setDistributionIATANumber(String distributionIATANumber) {
    this.distributionIATANumber = distributionIATANumber;
  }

  public UpdateReservationsRequestSingleCall linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public UpdateReservationsRequestSingleCall putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
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
  
  @Schema(name = "linkAmendReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAmendReservations")
  public Map<String, String> getLinkAmendReservations() {
    return linkAmendReservations;
  }

  public void setLinkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
  }

  public UpdateReservationsRequestSingleCall reservations(List<@Valid UpdateReservationRequestSingleCall> reservations) {
    this.reservations = reservations;
    return this;
  }

  public UpdateReservationsRequestSingleCall addReservationsItem(UpdateReservationRequestSingleCall reservationsItem) {
    if (this.reservations == null) {
      this.reservations = new ArrayList<>();
    }
    this.reservations.add(reservationsItem);
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid UpdateReservationRequestSingleCall> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid UpdateReservationRequestSingleCall> reservations) {
    this.reservations = reservations;
  }

  public UpdateReservationsRequestSingleCall tempReservations(ReservationByBasketRefResponseSingleCall tempReservations) {
    this.tempReservations = tempReservations;
    return this;
  }

  /**
   * Get tempReservations
   * @return tempReservations
   */
  @Valid 
  @Schema(name = "tempReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tempReservations")
  public ReservationByBasketRefResponseSingleCall getTempReservations() {
    return tempReservations;
  }

  public void setTempReservations(ReservationByBasketRefResponseSingleCall tempReservations) {
    this.tempReservations = tempReservations;
  }

  public UpdateReservationsRequestSingleCall token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReservationsRequestSingleCall updateReservationsRequestSingleCall = (UpdateReservationsRequestSingleCall) o;
    return Objects.equals(this.bookingChannel, updateReservationsRequestSingleCall.bookingChannel) &&
        Objects.equals(this.companyId, updateReservationsRequestSingleCall.companyId) &&
        Objects.equals(this.distributionIATANumber, updateReservationsRequestSingleCall.distributionIATANumber) &&
        Objects.equals(this.linkAmendReservations, updateReservationsRequestSingleCall.linkAmendReservations) &&
        Objects.equals(this.reservations, updateReservationsRequestSingleCall.reservations) &&
        Objects.equals(this.tempReservations, updateReservationsRequestSingleCall.tempReservations) &&
        Objects.equals(this.token, updateReservationsRequestSingleCall.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, companyId, distributionIATANumber, linkAmendReservations, reservations, tempReservations, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationsRequestSingleCall {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    distributionIATANumber: ").append(toIndentedString(distributionIATANumber)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    reservations: ").append(toIndentedString(reservations)).append("\n");
    sb.append("    tempReservations: ").append(toIndentedString(tempReservations)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

