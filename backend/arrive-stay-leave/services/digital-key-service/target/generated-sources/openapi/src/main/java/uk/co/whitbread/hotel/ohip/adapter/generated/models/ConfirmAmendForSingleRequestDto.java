package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsCnpRequest;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsRequest;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SpecialRequests;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmAmendForSingleRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmAmendForSingleRequestDto {

  private @Nullable BookerDetailsCnpRequest bookerDetailsCnpRequest;

  private @Nullable BusinessItemsRequest bookingAllowancesRequest;

  @Valid
  private @Nullable List<@Valid UpdateReservationsRequestSingleCall> editRoomRequest;

  @Valid
  private @Nullable List<@Valid SpecialRequests> specialRequests;

  private @Nullable UpdateReservationsRequestSingleCall stayDateUpdateRequest;

  private @Nullable UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest;

  public ConfirmAmendForSingleRequestDto bookerDetailsCnpRequest(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    this.bookerDetailsCnpRequest = bookerDetailsCnpRequest;
    return this;
  }

  /**
   * Get bookerDetailsCnpRequest
   * @return bookerDetailsCnpRequest
   */
  @Valid 
  @Schema(name = "bookerDetailsCnpRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookerDetailsCnpRequest")
  public BookerDetailsCnpRequest getBookerDetailsCnpRequest() {
    return bookerDetailsCnpRequest;
  }

  public void setBookerDetailsCnpRequest(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    this.bookerDetailsCnpRequest = bookerDetailsCnpRequest;
  }

  public ConfirmAmendForSingleRequestDto bookingAllowancesRequest(BusinessItemsRequest bookingAllowancesRequest) {
    this.bookingAllowancesRequest = bookingAllowancesRequest;
    return this;
  }

  /**
   * Get bookingAllowancesRequest
   * @return bookingAllowancesRequest
   */
  @Valid 
  @Schema(name = "bookingAllowancesRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowancesRequest")
  public BusinessItemsRequest getBookingAllowancesRequest() {
    return bookingAllowancesRequest;
  }

  public void setBookingAllowancesRequest(BusinessItemsRequest bookingAllowancesRequest) {
    this.bookingAllowancesRequest = bookingAllowancesRequest;
  }

  public ConfirmAmendForSingleRequestDto editRoomRequest(List<@Valid UpdateReservationsRequestSingleCall> editRoomRequest) {
    this.editRoomRequest = editRoomRequest;
    return this;
  }

  public ConfirmAmendForSingleRequestDto addEditRoomRequestItem(UpdateReservationsRequestSingleCall editRoomRequestItem) {
    if (this.editRoomRequest == null) {
      this.editRoomRequest = new ArrayList<>();
    }
    this.editRoomRequest.add(editRoomRequestItem);
    return this;
  }

  /**
   * Get editRoomRequest
   * @return editRoomRequest
   */
  @Valid 
  @Schema(name = "editRoomRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("editRoomRequest")
  public List<@Valid UpdateReservationsRequestSingleCall> getEditRoomRequest() {
    return editRoomRequest;
  }

  public void setEditRoomRequest(List<@Valid UpdateReservationsRequestSingleCall> editRoomRequest) {
    this.editRoomRequest = editRoomRequest;
  }

  public ConfirmAmendForSingleRequestDto specialRequests(List<@Valid SpecialRequests> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public ConfirmAmendForSingleRequestDto addSpecialRequestsItem(SpecialRequests specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  @Valid 
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<@Valid SpecialRequests> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<@Valid SpecialRequests> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public ConfirmAmendForSingleRequestDto stayDateUpdateRequest(UpdateReservationsRequestSingleCall stayDateUpdateRequest) {
    this.stayDateUpdateRequest = stayDateUpdateRequest;
    return this;
  }

  /**
   * Get stayDateUpdateRequest
   * @return stayDateUpdateRequest
   */
  @Valid 
  @Schema(name = "stayDateUpdateRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayDateUpdateRequest")
  public UpdateReservationsRequestSingleCall getStayDateUpdateRequest() {
    return stayDateUpdateRequest;
  }

  public void setStayDateUpdateRequest(UpdateReservationsRequestSingleCall stayDateUpdateRequest) {
    this.stayDateUpdateRequest = stayDateUpdateRequest;
  }

  public ConfirmAmendForSingleRequestDto updateReservationPackagesByIdRequest(UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest) {
    this.updateReservationPackagesByIdRequest = updateReservationPackagesByIdRequest;
    return this;
  }

  /**
   * Get updateReservationPackagesByIdRequest
   * @return updateReservationPackagesByIdRequest
   */
  @Valid 
  @Schema(name = "updateReservationPackagesByIdRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updateReservationPackagesByIdRequest")
  public UpdateReservationPackagesByIdRequest getUpdateReservationPackagesByIdRequest() {
    return updateReservationPackagesByIdRequest;
  }

  public void setUpdateReservationPackagesByIdRequest(UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest) {
    this.updateReservationPackagesByIdRequest = updateReservationPackagesByIdRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmAmendForSingleRequestDto confirmAmendForSingleRequestDto = (ConfirmAmendForSingleRequestDto) o;
    return Objects.equals(this.bookerDetailsCnpRequest, confirmAmendForSingleRequestDto.bookerDetailsCnpRequest) &&
        Objects.equals(this.bookingAllowancesRequest, confirmAmendForSingleRequestDto.bookingAllowancesRequest) &&
        Objects.equals(this.editRoomRequest, confirmAmendForSingleRequestDto.editRoomRequest) &&
        Objects.equals(this.specialRequests, confirmAmendForSingleRequestDto.specialRequests) &&
        Objects.equals(this.stayDateUpdateRequest, confirmAmendForSingleRequestDto.stayDateUpdateRequest) &&
        Objects.equals(this.updateReservationPackagesByIdRequest, confirmAmendForSingleRequestDto.updateReservationPackagesByIdRequest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookerDetailsCnpRequest, bookingAllowancesRequest, editRoomRequest, specialRequests, stayDateUpdateRequest, updateReservationPackagesByIdRequest);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmAmendForSingleRequestDto {\n");
    sb.append("    bookerDetailsCnpRequest: ").append(toIndentedString(bookerDetailsCnpRequest)).append("\n");
    sb.append("    bookingAllowancesRequest: ").append(toIndentedString(bookingAllowancesRequest)).append("\n");
    sb.append("    editRoomRequest: ").append(toIndentedString(editRoomRequest)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    stayDateUpdateRequest: ").append(toIndentedString(stayDateUpdateRequest)).append("\n");
    sb.append("    updateReservationPackagesByIdRequest: ").append(toIndentedString(updateReservationPackagesByIdRequest)).append("\n");
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

