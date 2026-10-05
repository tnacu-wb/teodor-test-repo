package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.CompanyQuestionAndAnswerDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanyQuestionAndAnswerDetailsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyQuestionAndAnswerDetailsRequestDto {

  private CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails;

  private String hotelId;

  @Valid
  private Set<String> reservationIds = new LinkedHashSet<>();

  public CompanyQuestionAndAnswerDetailsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CompanyQuestionAndAnswerDetailsRequestDto(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails, String hotelId, Set<String> reservationIds) {
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails) {
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    return this;
  }

  /**
   * Get companyQuestionAndAnswerDetails
   * @return companyQuestionAndAnswerDetails
   */
  @NotNull @Valid 
  @Schema(name = "companyQuestionAndAnswerDetails", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("companyQuestionAndAnswerDetails")
  public CompanyQuestionAndAnswerDetailsDto getCompanyQuestionAndAnswerDetails() {
    return companyQuestionAndAnswerDetails;
  }

  public void setCompanyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails) {
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
  }

  public CompanyQuestionAndAnswerDetailsRequestDto hotelId(String hotelId) {
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

  public CompanyQuestionAndAnswerDetailsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public CompanyQuestionAndAnswerDetailsRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new LinkedHashSet<>();
    }
    this.reservationIds.add(reservationIdsItem);
    return this;
  }

  /**
   * Get reservationIds
   * @return reservationIds
   */
  @NotNull 
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIds")
  public Set<String> getReservationIds() {
    return reservationIds;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setReservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto = (CompanyQuestionAndAnswerDetailsRequestDto) o;
    return Objects.equals(this.companyQuestionAndAnswerDetails, companyQuestionAndAnswerDetailsRequestDto.companyQuestionAndAnswerDetails) &&
        Objects.equals(this.hotelId, companyQuestionAndAnswerDetailsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, companyQuestionAndAnswerDetailsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyQuestionAndAnswerDetails, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyQuestionAndAnswerDetailsRequestDto {\n");
    sb.append("    companyQuestionAndAnswerDetails: ").append(toIndentedString(companyQuestionAndAnswerDetails)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
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

