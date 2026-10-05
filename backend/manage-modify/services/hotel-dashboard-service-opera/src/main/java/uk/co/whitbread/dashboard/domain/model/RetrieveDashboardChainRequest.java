package uk.co.whitbread.dashboard.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveDashboardChainRequest {

  public RetrieveDashboardChainRequest(final RetrieveDashboardRequest retrieveDashboardRequest,
      final String token, final String sessionId, final boolean hasRecentSearches,
      final String origin, final String customerId) {
    this.confirmationNumber = retrieveDashboardRequest.getConfirmationNumber();
    this.surname = retrieveDashboardRequest.getSurname();
    this.arrivalDate = retrieveDashboardRequest.getArrivalDate();
    this.business = retrieveDashboardRequest.isBusiness();
    this.token = token;
    this.recentSearches = hasRecentSearches;
    this.companyId = retrieveDashboardRequest.getCompanyId();
    this.employeeId = retrieveDashboardRequest.getEmployeeId();
    this.sessionId = sessionId;
    this.language = retrieveDashboardRequest.getLanguage();
    this.origin = origin;
    this.customerId = customerId;
  }

  private String customerId;

  private String confirmationNumber;

  private String surname;

  @Schema(type = "string", format = "date", example = "2022-01-01")
  private LocalDate arrivalDate;

  private boolean business;

  private String token;

  private boolean recentSearches;

  private String companyId;

  private String employeeId;

  private String sessionId;

  private String language;

  private String origin;
}
