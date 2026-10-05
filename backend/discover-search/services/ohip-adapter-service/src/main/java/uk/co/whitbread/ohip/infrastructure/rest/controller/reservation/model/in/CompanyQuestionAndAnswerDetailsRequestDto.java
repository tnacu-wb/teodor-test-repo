/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyQuestionAndAnswerDetailsRequestDto {

  @Schema(required = true)
  @NotEmpty(message = OhipConstants.RESERVATION_ID_ERROR)
  private Set<String> reservationIds;

  @Schema(required = true)
  @NotNull(message = OhipConstants.HOTEL_ID_ERROR)
  private String hotelId;

  @Schema(required = true)
  @NotNull(message = OhipConstants.COMPANY_QNA_DETAILS_ERROR)
  private CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails;

}
