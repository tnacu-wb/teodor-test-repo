/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */

package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyQuestionAndAnswerDto {
  private String question;
  private String questionHeader;
  private String answer;
}
