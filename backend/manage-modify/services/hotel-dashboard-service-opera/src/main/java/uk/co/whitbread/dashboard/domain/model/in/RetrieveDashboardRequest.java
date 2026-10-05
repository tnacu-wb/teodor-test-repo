package uk.co.whitbread.dashboard.domain.model.in;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveDashboardRequest {

  private String confirmationNumber;
  private String surname;
  private LocalDate arrivalDate;
  private boolean business;
  private String companyId;
  private String employeeId;
  private String language;
}
