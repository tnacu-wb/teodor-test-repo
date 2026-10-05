package uk.co.whitbread.company.employee.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeesResponse {

    private boolean success;
    private List<EmployeeSummary> employees;
    private String pageToken;
}
