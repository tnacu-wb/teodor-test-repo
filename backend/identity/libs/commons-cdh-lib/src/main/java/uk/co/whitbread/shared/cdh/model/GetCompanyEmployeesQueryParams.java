package uk.co.whitbread.shared.cdh.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.co.whitbread.shared.cdh.AccessLevel;

@Builder
@Getter
@Setter
public class GetCompanyEmployeesQueryParams {

  /**
   * May contain first name, last name or email filter for a "begins with" type of search.
   */
  private String searchCriteria;

  /**
   * It will return only the employees with the specified access level
   */
  private AccessLevel accessLevel;

  /**
   * If {@code true}, it will return only employees who are waiting for travel manager’s approval.
   */
  private Boolean awaitingApproval;

  /**
   * This is the pagination token to be used for requesting the next page of items for paged result
   * sets. If missing, it indicates that the request is for the first page of results.
   */
  private String pageToken;

  /**
   * The size of the page desired. If no value is provided, the default one will be used by the CDH
   * system.
   */
  private Integer pageSize;
}
