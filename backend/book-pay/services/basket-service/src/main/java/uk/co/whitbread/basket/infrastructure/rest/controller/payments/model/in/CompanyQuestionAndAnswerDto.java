/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyQuestionAndAnswerDto {
  private String question;
  private String questionHeader;
  private String answer;
}
