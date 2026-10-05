package uk.co.whitbread.shared.cdh.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetEmployeesQueryParams {

  /**
   * Email address of the employee.
   */
  private final String emailAddress;

  /**
   * Activation key of the employee.
   */
  private final String activationKey;

  /**
   * This is the pagination token to be used for requesting the next page of items for paged result sets.
   * If missing, it indicates that the request is for the first page of results.
   */
  private final String pageToken;

  /**
   * The size of the page desired. If no value is provided, the default one will be used by the CDH system.
   */
  private final Integer pageSize;
}
