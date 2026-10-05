package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookingChannelDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmAmendOnReservationsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmAmendOnReservationsRequestDto {

  private BookingChannelDto bookingChannel;

  private @Nullable Boolean clearCcAgentIdUdf;

  private String hotelId;

  @Valid
  private @Nullable Map<String, String> linkAmendReservations;

  private @Nullable Boolean markAsPayOnArrival;

  @Valid
  private List<String> originalReservations;

  private @Nullable Boolean sendEmailConfirmation;

  private @Nullable Boolean sendEmailInvoice;

  @Valid
  private List<String> tempReservations;

  public ConfirmAmendOnReservationsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConfirmAmendOnReservationsRequestDto(BookingChannelDto bookingChannel, String hotelId, List<String> originalReservations, List<String> tempReservations) {
    this.bookingChannel = bookingChannel;
    this.hotelId = hotelId;
    this.originalReservations = originalReservations;
    this.tempReservations = tempReservations;
  }

  public ConfirmAmendOnReservationsRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public ConfirmAmendOnReservationsRequestDto clearCcAgentIdUdf(Boolean clearCcAgentIdUdf) {
    this.clearCcAgentIdUdf = clearCcAgentIdUdf;
    return this;
  }

  /**
   * Get clearCcAgentIdUdf
   * @return clearCcAgentIdUdf
   */
  
  @Schema(name = "clearCcAgentIdUdf", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("clearCcAgentIdUdf")
  public Boolean getClearCcAgentIdUdf() {
    return clearCcAgentIdUdf;
  }

  public void setClearCcAgentIdUdf(Boolean clearCcAgentIdUdf) {
    this.clearCcAgentIdUdf = clearCcAgentIdUdf;
  }

  public ConfirmAmendOnReservationsRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ConfirmAmendOnReservationsRequestDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public ConfirmAmendOnReservationsRequestDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
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

  public ConfirmAmendOnReservationsRequestDto markAsPayOnArrival(Boolean markAsPayOnArrival) {
    this.markAsPayOnArrival = markAsPayOnArrival;
    return this;
  }

  /**
   * Get markAsPayOnArrival
   * @return markAsPayOnArrival
   */
  
  @Schema(name = "markAsPayOnArrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("markAsPayOnArrival")
  public Boolean getMarkAsPayOnArrival() {
    return markAsPayOnArrival;
  }

  public void setMarkAsPayOnArrival(Boolean markAsPayOnArrival) {
    this.markAsPayOnArrival = markAsPayOnArrival;
  }

  public ConfirmAmendOnReservationsRequestDto originalReservations(List<String> originalReservations) {
    this.originalReservations = originalReservations;
    return this;
  }

  public ConfirmAmendOnReservationsRequestDto addOriginalReservationsItem(String originalReservationsItem) {
    if (this.originalReservations == null) {
      this.originalReservations = new ArrayList<>();
    }
    this.originalReservations.add(originalReservationsItem);
    return this;
  }

  /**
   * Get originalReservations
   * @return originalReservations
   */
  @NotNull 
  @Schema(name = "originalReservations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("originalReservations")
  public List<String> getOriginalReservations() {
    return originalReservations;
  }

  public void setOriginalReservations(List<String> originalReservations) {
    this.originalReservations = originalReservations;
  }

  public ConfirmAmendOnReservationsRequestDto sendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
    return this;
  }

  /**
   * Get sendEmailConfirmation
   * @return sendEmailConfirmation
   */
  
  @Schema(name = "sendEmailConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailConfirmation")
  public Boolean getSendEmailConfirmation() {
    return sendEmailConfirmation;
  }

  public void setSendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
  }

  public ConfirmAmendOnReservationsRequestDto sendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
    return this;
  }

  /**
   * Get sendEmailInvoice
   * @return sendEmailInvoice
   */
  
  @Schema(name = "sendEmailInvoice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailInvoice")
  public Boolean getSendEmailInvoice() {
    return sendEmailInvoice;
  }

  public void setSendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
  }

  public ConfirmAmendOnReservationsRequestDto tempReservations(List<String> tempReservations) {
    this.tempReservations = tempReservations;
    return this;
  }

  public ConfirmAmendOnReservationsRequestDto addTempReservationsItem(String tempReservationsItem) {
    if (this.tempReservations == null) {
      this.tempReservations = new ArrayList<>();
    }
    this.tempReservations.add(tempReservationsItem);
    return this;
  }

  /**
   * Get tempReservations
   * @return tempReservations
   */
  @NotNull 
  @Schema(name = "tempReservations", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tempReservations")
  public List<String> getTempReservations() {
    return tempReservations;
  }

  public void setTempReservations(List<String> tempReservations) {
    this.tempReservations = tempReservations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmAmendOnReservationsRequestDto confirmAmendOnReservationsRequestDto = (ConfirmAmendOnReservationsRequestDto) o;
    return Objects.equals(this.bookingChannel, confirmAmendOnReservationsRequestDto.bookingChannel) &&
        Objects.equals(this.clearCcAgentIdUdf, confirmAmendOnReservationsRequestDto.clearCcAgentIdUdf) &&
        Objects.equals(this.hotelId, confirmAmendOnReservationsRequestDto.hotelId) &&
        Objects.equals(this.linkAmendReservations, confirmAmendOnReservationsRequestDto.linkAmendReservations) &&
        Objects.equals(this.markAsPayOnArrival, confirmAmendOnReservationsRequestDto.markAsPayOnArrival) &&
        Objects.equals(this.originalReservations, confirmAmendOnReservationsRequestDto.originalReservations) &&
        Objects.equals(this.sendEmailConfirmation, confirmAmendOnReservationsRequestDto.sendEmailConfirmation) &&
        Objects.equals(this.sendEmailInvoice, confirmAmendOnReservationsRequestDto.sendEmailInvoice) &&
        Objects.equals(this.tempReservations, confirmAmendOnReservationsRequestDto.tempReservations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, clearCcAgentIdUdf, hotelId, linkAmendReservations, markAsPayOnArrival, originalReservations, sendEmailConfirmation, sendEmailInvoice, tempReservations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmAmendOnReservationsRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    clearCcAgentIdUdf: ").append(toIndentedString(clearCcAgentIdUdf)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    markAsPayOnArrival: ").append(toIndentedString(markAsPayOnArrival)).append("\n");
    sb.append("    originalReservations: ").append(toIndentedString(originalReservations)).append("\n");
    sb.append("    sendEmailConfirmation: ").append(toIndentedString(sendEmailConfirmation)).append("\n");
    sb.append("    sendEmailInvoice: ").append(toIndentedString(sendEmailInvoice)).append("\n");
    sb.append("    tempReservations: ").append(toIndentedString(tempReservations)).append("\n");
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

