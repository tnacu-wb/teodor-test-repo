/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.infrastructure.rest.client.config.OhipAdapterConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyQuestionAndAnswerDetailsRequest {

  @NotEmpty(message = OhipAdapterConstants.RESERVATION_ID_ERROR)
  private Set<String> reservationIds;

  @NotNull(message = OhipAdapterConstants.HOTEL_ID_ERROR)
  private String hotelId;

  @NotNull(message = OhipAdapterConstants.COMPANY_QNA_DETAILS_ERROR)
  private CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails;

}