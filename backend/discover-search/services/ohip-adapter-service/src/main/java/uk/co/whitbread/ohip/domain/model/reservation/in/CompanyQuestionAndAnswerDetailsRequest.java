/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CompanyQuestionAndAnswerDetailsRequest implements SelfValidation<CompanyQuestionAndAnswerDetailsRequest> {

  @NotEmpty(message = OhipConstants.RESERVATION_ID_ERROR)
  private Set<String> reservationIds;

  @NotNull(message = OhipConstants.HOTEL_ID_ERROR)
  private String hotelId;

  @NotNull(message = OhipConstants.COMPANY_QNA_DETAILS_ERROR)
  private CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails;

  @Builder(toBuilder = true)
  public CompanyQuestionAndAnswerDetailsRequest(final Set<String> reservationIds, final String hotelId,
      final CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    this.validateSelf();
  }

}