package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAnswersDto {
  private String customerReferenceAnswer;
  private String purchaseOrderAnswer;
  private List<CompanyAnswerDto> companyAnswers;
}
