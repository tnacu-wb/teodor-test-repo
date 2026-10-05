/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyQuestionAndAnswerDetailsDto implements SelfValidation<CompanyQuestionAndAnswerDetailsDto> {

  private CompanyQuestionAndAnswerDto purchaseOrderQuestionAndAnswer;
  private CompanyQuestionAndAnswerDto customerReferenceQuestionAndAnswer;
  private List<CompanyQuestionAndAnswerDto> userDefinedQuestionAndAnswers;

}
