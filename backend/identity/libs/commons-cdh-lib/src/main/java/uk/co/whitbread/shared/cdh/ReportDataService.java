package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_LEVEL_DETAILS;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANY_LEVEL_DETAILS;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.IB_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.TRANSACTIONS_DETAILS;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.queryParamsToMap;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.shared.cdh.model.GetAccountLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCompanyLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsRequest;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;


  /**
   * Get a list of company spending based on company account ID and query params.
   *
   * @param companyAccountId ID of the company in CDH
   * @param queryParams      wrapper over query parameters
   * @param accessedBy       information about who is making the request
   * @param accessContext    information about who is making the request
   * @return list of company spending for each month matching the query criteria
   */
  public List<CompanySpendingResponse> getCompanyLevelDetails(String companyAccountId,
      GetCompanyLevelDetailsQueryParams queryParams, String accessedBy, String accessContext) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + IB_SERVICES_ENDPOINT + COMPANY_LEVEL_DETAILS);
    builder.queryParams(queryParamsToMap(queryParams));

    return cdhClient.getListCDH(
        builder.buildAndExpand(companyAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
        CompanySpendingResponse.class);
  }


  /**
   * Get a list of account spending based on account ID and query params.

   * @param pibaAccountId ID of the account
   * @param queryParams   wrapper over query parameters
   * @param accessedBy    information about who is making the request
   * @param accessContext information about who is making the request
   * @return list of account spending for each month matching the query criteria
   */
  public List<AccountSpendingResponse> getAccountLevelDetails(String pibaAccountId,
      GetAccountLevelDetailsQueryParams queryParams, String accessedBy, String accessContext) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + IB_SERVICES_ENDPOINT + ACCOUNT_LEVEL_DETAILS);
    builder.queryParams(queryParamsToMap(queryParams));

    return cdhClient.getListCDH(
        builder.buildAndExpand(pibaAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
        AccountSpendingResponse.class
    );
  }

  /**
   * Get a paginated list of transactions for a user in a time frame.
   *
   * @param requestBody request body
   * @param accessedBy information about who is making the request
   * @param accessContext information about who is making the request
   * @return a list of paginated transactions
   */
  public TransactionDetailsResponse getTransactionDetails(TransactionDetailsRequest requestBody,
                                                          String accessedBy, String accessContext) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + IB_SERVICES_ENDPOINT + TRANSACTIONS_DETAILS);

    return cdhClient.postCDH(
        builder.toUriString(),
        requestBody,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
        TransactionDetailsRequest.class,
        TransactionDetailsResponse.class
    );
  }
}
