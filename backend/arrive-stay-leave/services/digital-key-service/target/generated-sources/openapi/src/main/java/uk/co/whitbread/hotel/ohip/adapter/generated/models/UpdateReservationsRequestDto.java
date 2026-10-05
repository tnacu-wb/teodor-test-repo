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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationsRequestDto {

  private @Nullable BookingChannelDto bookingChannel;

  private @Nullable String companyId;

  private @Nullable String distributionIATANumber;

  @Valid
  private @Nullable Map<String, String> linkAmendReservations;

  @Valid
  private @Nullable List<@Valid ReservationDto> newRatesReservation;

  private @Nullable ReservationByBasketRefResponseDto tempReservations;

  @Valid
  private @Nullable List<@Valid UpdateReservationRequestDto> updateReservationsRequest;

  public UpdateReservationsRequestDto bookingChannel(BookingChannelDto bookingChannel) {
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
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public UpdateReservationsRequestDto companyId(String companyId) {
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

  public UpdateReservationsRequestDto distributionIATANumber(String distributionIATANumber) {
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

  public UpdateReservationsRequestDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public UpdateReservationsRequestDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
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

  public UpdateReservationsRequestDto newRatesReservation(List<@Valid ReservationDto> newRatesReservation) {
    this.newRatesReservation = newRatesReservation;
    return this;
  }

  public UpdateReservationsRequestDto addNewRatesReservationItem(ReservationDto newRatesReservationItem) {
    if (this.newRatesReservation == null) {
      this.newRatesReservation = new ArrayList<>();
    }
    this.newRatesReservation.add(newRatesReservationItem);
    return this;
  }

  /**
   * Get newRatesReservation
   * @return newRatesReservation
   */
  @Valid 
  @Schema(name = "newRatesReservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newRatesReservation")
  public List<@Valid ReservationDto> getNewRatesReservation() {
    return newRatesReservation;
  }

  public void setNewRatesReservation(List<@Valid ReservationDto> newRatesReservation) {
    this.newRatesReservation = newRatesReservation;
  }

  public UpdateReservationsRequestDto tempReservations(ReservationByBasketRefResponseDto tempReservations) {
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
  public ReservationByBasketRefResponseDto getTempReservations() {
    return tempReservations;
  }

  public void setTempReservations(ReservationByBasketRefResponseDto tempReservations) {
    this.tempReservations = tempReservations;
  }

  public UpdateReservationsRequestDto updateReservationsRequest(List<@Valid UpdateReservationRequestDto> updateReservationsRequest) {
    this.updateReservationsRequest = updateReservationsRequest;
    return this;
  }

  public UpdateReservationsRequestDto addUpdateReservationsRequestItem(UpdateReservationRequestDto updateReservationsRequestItem) {
    if (this.updateReservationsRequest == null) {
      this.updateReservationsRequest = new ArrayList<>();
    }
    this.updateReservationsRequest.add(updateReservationsRequestItem);
    return this;
  }

  /**
   * Get updateReservationsRequest
   * @return updateReservationsRequest
   */
  @Valid 
  @Schema(name = "updateReservationsRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateReservationsRequest")
  public List<@Valid UpdateReservationRequestDto> getUpdateReservationsRequest() {
    return updateReservationsRequest;
  }

  public void setUpdateReservationsRequest(List<@Valid UpdateReservationRequestDto> updateReservationsRequest) {
    this.updateReservationsRequest = updateReservationsRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReservationsRequestDto updateReservationsRequestDto = (UpdateReservationsRequestDto) o;
    return Objects.equals(this.bookingChannel, updateReservationsRequestDto.bookingChannel) &&
        Objects.equals(this.companyId, updateReservationsRequestDto.companyId) &&
        Objects.equals(this.distributionIATANumber, updateReservationsRequestDto.distributionIATANumber) &&
        Objects.equals(this.linkAmendReservations, updateReservationsRequestDto.linkAmendReservations) &&
        Objects.equals(this.newRatesReservation, updateReservationsRequestDto.newRatesReservation) &&
        Objects.equals(this.tempReservations, updateReservationsRequestDto.tempReservations) &&
        Objects.equals(this.updateReservationsRequest, updateReservationsRequestDto.updateReservationsRequest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, companyId, distributionIATANumber, linkAmendReservations, newRatesReservation, tempReservations, updateReservationsRequest);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationsRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    distributionIATANumber: ").append(toIndentedString(distributionIATANumber)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    newRatesReservation: ").append(toIndentedString(newRatesReservation)).append("\n");
    sb.append("    tempReservations: ").append(toIndentedString(tempReservations)).append("\n");
    sb.append("    updateReservationsRequest: ").append(toIndentedString(updateReservationsRequest)).append("\n");
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

