/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyQuestionAndAnswerDetailsDto {

  private CompanyQuestionAndAnswerDto purchaseOrderQuestionAndAnswer;
  private CompanyQuestionAndAnswerDto customerReferenceQuestionAndAnswer;
  private List<CompanyQuestionAndAnswerDto> userDefinedQuestionAndAnswers;

}
