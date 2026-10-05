package uk.co.whitbread.company.employee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EmployeeAnswers {

    private String customerReferenceAnswer;
    private String purchaseOrderAnswer;
    private List<UserDefinedAnswer> userDefinedAnswers;
}
