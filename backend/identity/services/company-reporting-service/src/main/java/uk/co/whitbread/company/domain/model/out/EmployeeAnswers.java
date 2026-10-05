package uk.co.whitbread.company.domain.model.out;

import java.util.List;

public record EmployeeAnswers(
    String customerReferenceAnswer,
    String purchaseOrderAnswer,
    List<CompanyAnswers> companyAnswers
) {

}
