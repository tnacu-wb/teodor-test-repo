/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.basket.domain.model.payments.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyQuestionAndAnswerDetails {

  private CompanyQuestionAndAnswer purchaseOrderQuestionAndAnswer;
  private CompanyQuestionAndAnswer customerReferenceQuestionAndAnswer;
  private List<CompanyQuestionAndAnswer> userDefinedQuestionAndAnswers;

}
