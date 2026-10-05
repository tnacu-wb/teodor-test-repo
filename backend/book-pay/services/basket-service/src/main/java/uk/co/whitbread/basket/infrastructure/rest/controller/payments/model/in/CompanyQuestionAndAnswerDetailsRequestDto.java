/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyQuestionAndAnswerDetailsRequestDto {

  private Set<String> reservationIds;
  private String hotelId;
  private CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails;

}
