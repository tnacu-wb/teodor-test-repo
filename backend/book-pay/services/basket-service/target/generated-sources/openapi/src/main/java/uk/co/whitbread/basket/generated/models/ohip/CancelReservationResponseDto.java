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
import uk.co.whitbread.basket.generated.models.ohip.DepositsResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CancelReservationResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelReservationResponseDto {

  @Valid
  private List<String> cancellationIds = new ArrayList<>();

  @Valid
  private Map<String, DepositsResponseDto> refundedDeposits = new HashMap<>();

  public CancelReservationResponseDto cancellationIds(List<String> cancellationIds) {
    this.cancellationIds = cancellationIds;
    return this;
  }

  public CancelReservationResponseDto addCancellationIdsItem(String cancellationIdsItem) {
    if (this.cancellationIds == null) {
      this.cancellationIds = new ArrayList<>();
    }
    this.cancellationIds.add(cancellationIdsItem);
    return this;
  }

  /**
   * Get cancellationIds
   * @return cancellationIds
   */
  
  @Schema(name = "cancellationIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationIds")
  public List<String> getCancellationIds() {
    return cancellationIds;
  }

  public void setCancellationIds(List<String> cancellationIds) {
    this.cancellationIds = cancellationIds;
  }

  public CancelReservationResponseDto refundedDeposits(Map<String, DepositsResponseDto> refundedDeposits) {
    this.refundedDeposits = refundedDeposits;
    return this;
  }

  public CancelReservationResponseDto putRefundedDepositsItem(String key, DepositsResponseDto refundedDepositsItem) {
    if (this.refundedDeposits == null) {
      this.refundedDeposits = new HashMap<>();
    }
    this.refundedDeposits.put(key, refundedDepositsItem);
    return this;
  }

  /**
   * Get refundedDeposits
   * @return refundedDeposits
   */
  @Valid 
  @Schema(name = "refundedDeposits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refundedDeposits")
  public Map<String, DepositsResponseDto> getRefundedDeposits() {
    return refundedDeposits;
  }

  public void setRefundedDeposits(Map<String, DepositsResponseDto> refundedDeposits) {
    this.refundedDeposits = refundedDeposits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelReservationResponseDto cancelReservationResponseDto = (CancelReservationResponseDto) o;
    return Objects.equals(this.cancellationIds, cancelReservationResponseDto.cancellationIds) &&
        Objects.equals(this.refundedDeposits, cancelReservationResponseDto.refundedDeposits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancellationIds, refundedDeposits);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelReservationResponseDto {\n");
    sb.append("    cancellationIds: ").append(toIndentedString(cancellationIds)).append("\n");
    sb.append("    refundedDeposits: ").append(toIndentedString(refundedDeposits)).append("\n");
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

